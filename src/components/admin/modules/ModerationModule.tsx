import React, { useState, useEffect } from 'react';
import {
  AlertTriangle,
  Search,
  CheckCircle2,
  XCircle,
  Shield,
  Ban,
  VolumeX,
  Eye,
  Download,
  Filter,
} from 'lucide-react';
import { ReportTicket } from '../../../types/admin';
import { adminBackend } from '../../../services/adminBackendService';
import { useAura } from '../../../context/AuraContext';

interface ModerationModuleProps {
  isDarkMode: boolean;
}

export const ModerationModule: React.FC<ModerationModuleProps> = ({ isDarkMode }) => {
  const { banUser } = useAura();
  const [reports, setReports] = useState<ReportTicket[]>(adminBackend.getReports());
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [notice, setNotice] = useState<string | null>(null);

  useEffect(() => {
    const sync = () => setReports(adminBackend.getReports());
    return adminBackend.subscribe(sync);
  }, []);

  const filteredReports = reports.filter((r) => {
    const matchesSearch =
      r.targetName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.reporterName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.category.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || r.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const handleTakeAction = (report: ReportTicket, action: 'BAN' | 'MUTE' | 'DISMISS' | 'RESOLVE') => {
    if (action === 'BAN') {
      banUser(report.targetId, true);
      adminBackend.resolveReport(report.id, 'RESOLVED', 'PERMANENT_BAN', 'Account banned by moderator enforcement', 'Moderator');
      setNotice(`User ${report.targetName} banned and ticket resolved.`);
    } else if (action === 'MUTE') {
      adminBackend.resolveReport(report.id, 'RESOLVED', 'MUTED_24H', 'Microphone muted for 24 hours', 'Moderator');
      setNotice(`User ${report.targetName} muted for 24h.`);
    } else if (action === 'DISMISS') {
      adminBackend.resolveReport(report.id, 'DISMISSED', 'NONE', 'Dismissed: Insufficient violation evidence', 'Moderator');
      setNotice(`Report ticket dismissed.`);
    } else {
      adminBackend.resolveReport(report.id, 'RESOLVED', 'WARNING_SENT', 'Resolved: Warning issued to offender', 'Moderator');
      setNotice(`Report marked resolved.`);
    }
    setTimeout(() => setNotice(null), 3500);
  };

  const handleExportCsv = () => {
    const data = filteredReports.map((r) => ({
      TicketID: r.id,
      Reporter: r.reporterName,
      Offender: r.targetName,
      Category: r.category,
      Details: r.description,
      Status: r.status,
      ActionTaken: r.actionTaken || 'None',
      Timestamp: new Date(r.createdAt).toISOString(),
    }));
    adminBackend.exportCsv('aura_moderation_tickets', data);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <AlertTriangle className="w-6 h-6 text-rose-500" />
            <span>Safety, Abuse Reports & Content Moderation</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Triage harassment complaints, hate speech flags, scam alerts, and enforce community guidelines.
          </p>
        </div>

        <button
          onClick={handleExportCsv}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-bold transition-all shadow-xs ${
            isDarkMode
              ? 'border-rose-500/40 bg-rose-500/10 text-rose-300 hover:bg-rose-500/20'
              : 'border-rose-300 bg-rose-50 text-rose-700 hover:bg-rose-100'
          }`}
        >
          <Download className="w-3.5 h-3.5" />
          <span>Export Reports CSV</span>
        </button>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* FILTER BAR */}
      <div className={`p-4 rounded-2xl border flex flex-col sm:flex-row gap-3 items-center justify-between ${cardBg}`}>
        <div
          className={`w-full sm:w-80 flex items-center gap-2 px-3 py-2 rounded-xl border text-xs ${
            isDarkMode ? 'bg-[#180e30] border-[#361f5c]' : 'bg-slate-100 border-slate-200'
          }`}
        >
          <Search className="w-4 h-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search report by target, reason..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-transparent border-none outline-none text-xs"
          />
        </div>

        <div className="flex items-center gap-2 text-xs">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className={`px-3 py-2 rounded-xl border font-semibold outline-none ${
              isDarkMode ? 'bg-[#180e30] border-[#361f5c] text-slate-200' : 'bg-slate-100 border-slate-200 text-slate-800'
            }`}
          >
            <option value="ALL">All Tickets</option>
            <option value="PENDING">Pending Review</option>
            <option value="RESOLVED">Resolved</option>
            <option value="DISMISSED">Dismissed</option>
          </select>
        </div>
      </div>

      {/* REPORTS LIST */}
      <div className="space-y-3">
        {filteredReports.length === 0 ? (
          <div className={`p-8 rounded-2xl border text-center text-slate-400 ${cardBg}`}>
            No moderation tickets matching criteria.
          </div>
        ) : (
          filteredReports.map((report) => (
            <div
              key={report.id}
              className={`p-4 rounded-2xl border flex flex-col md:flex-row md:items-center justify-between gap-4 ${cardBg}`}
            >
              <div className="space-y-1 min-w-0">
                <div className="flex items-center gap-2">
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-rose-500/20 text-rose-300 border border-rose-500/30">
                    {report.category}
                  </span>
                  <span className="text-xs font-extrabold text-white">
                    Offender: <span className="text-rose-400">{report.targetName}</span>
                  </span>
                  <span className="text-[11px] text-slate-400">
                    (Reported by: <span className="text-cyan-300">{report.reporterName}</span>)
                  </span>
                </div>

                <p className="text-xs text-slate-300 italic">
                  &quot;{report.description}&quot;
                </p>

                <div className="text-[10px] text-slate-500 flex items-center gap-3 pt-1">
                  <span>Ticket: {report.id}</span>
                  <span>•</span>
                  <span>{new Date(report.createdAt).toLocaleString()}</span>
                  {report.actionTaken && (
                    <>
                      <span>•</span>
                      <span className="text-emerald-400 font-semibold">Action: {report.actionTaken}</span>
                    </>
                  )}
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex items-center gap-2 self-end md:self-center flex-shrink-0">
                {report.status === 'PENDING' ? (
                  <>
                    <button
                      onClick={() => handleTakeAction(report, 'BAN')}
                      className="px-3 py-1.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-bold text-xs flex items-center gap-1 shadow-xs"
                    >
                      <Ban className="w-3.5 h-3.5" />
                      <span>Ban User</span>
                    </button>

                    <button
                      onClick={() => handleTakeAction(report, 'MUTE')}
                      className="px-3 py-1.5 rounded-xl border border-amber-500/40 text-amber-300 hover:bg-amber-500/20 font-bold text-xs flex items-center gap-1"
                    >
                      <VolumeX className="w-3.5 h-3.5" />
                      <span>Mute 24h</span>
                    </button>

                    <button
                      onClick={() => handleTakeAction(report, 'RESOLVE')}
                      className="px-3 py-1.5 rounded-xl border border-emerald-500/40 text-emerald-300 hover:bg-emerald-500/20 font-bold text-xs"
                    >
                      Warn & Resolve
                    </button>

                    <button
                      onClick={() => handleTakeAction(report, 'DISMISS')}
                      className="px-3 py-1.5 rounded-xl border border-slate-500/40 text-slate-400 hover:bg-slate-500/20 font-bold text-xs"
                    >
                      Dismiss
                    </button>
                  </>
                ) : (
                  <span className="px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                    TICKET {report.status}
                  </span>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
