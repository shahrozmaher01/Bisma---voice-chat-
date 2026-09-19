import React, { useState, useEffect } from 'react';
import {
  Mic,
  Search,
  CheckCircle2,
  XCircle,
  AlertTriangle,
  Clock,
  Coins,
  Gem,
  Percent,
  Download,
  Edit,
  ShieldAlert,
  UserCheck,
  X,
  Radio,
} from 'lucide-react';
import { HostProfile } from '../../../types/admin';
import { adminBackend } from '../../../services/adminBackendService';

interface HostManagementModuleProps {
  isDarkMode: boolean;
}

export const HostManagementModule: React.FC<HostManagementModuleProps> = ({ isDarkMode }) => {
  const [hosts, setHosts] = useState<HostProfile[]>(adminBackend.getHosts());
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [selectedHost, setSelectedHost] = useState<HostProfile | null>(null);
  const [showEditModal, setShowEditModal] = useState(false);
  const [commissionRate, setCommissionRate] = useState('65');
  const [monthlyHours, setMonthlyHours] = useState('60');
  const [notes, setNotes] = useState('');
  const [notice, setNotice] = useState<string | null>(null);

  useEffect(() => {
    const sync = () => setHosts(adminBackend.getHosts());
    return adminBackend.subscribe(sync);
  }, []);

  const filteredHosts = hosts.filter((h) => {
    const matchesSearch =
      h.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
      h.userId.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (h.agencyName && h.agencyName.toLowerCase().includes(searchQuery.toLowerCase()));
    const matchesStatus = statusFilter === 'ALL' || h.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const handleApprove = (hostId: string) => {
    adminBackend.approveHost(hostId, 'Approved by staff review');
    setNotice(`Host application approved!`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleReject = (hostId: string) => {
    adminBackend.rejectHost(hostId, 'Did not meet voice audition standards');
    setNotice(`Host application rejected.`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleIssueWarning = (host: HostProfile) => {
    adminBackend.updateHost(host.id, { warningCount: host.warningCount + 1 });
    setNotice(`Issued official performance warning to ${host.username} (Count: ${host.warningCount + 1})`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleToggleSuspend = (host: HostProfile) => {
    const nextStatus = host.status === 'SUSPENDED' ? 'APPROVED' : 'SUSPENDED';
    adminBackend.updateHost(host.id, { status: nextStatus });
    setNotice(`Host ${host.username} is now ${nextStatus}.`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleSaveHost = (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedHost) return;
    const comm = (parseFloat(commissionRate) || 65) / 100;
    const hours = parseFloat(monthlyHours) || 60;
    adminBackend.updateHost(selectedHost.id, {
      commissionRate: comm,
      monthlyTargetHours: hours,
      notes: notes.trim() || selectedHost.notes,
    });
    setShowEditModal(false);
    setNotice(`Updated host parameters for ${selectedHost.username}`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleExportCsv = () => {
    const data = filteredHosts.map((h) => ({
      ID: h.id,
      UserId: h.userId,
      Username: h.username,
      Agency: h.agencyName || 'Direct',
      Status: h.status,
      RoomHours: h.totalRoomHours,
      EarnedDiamonds: h.totalEarnedDiamonds,
      WithdrawableDiamonds: h.withdrawableDiamonds,
      CommissionPercent: `${(h.commissionRate * 100).toFixed(0)}%`,
      Warnings: h.warningCount,
      AppliedDate: h.appliedDate,
    }));
    adminBackend.exportCsv('aura_hosts_roster', data);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <Mic className="w-6 h-6 text-cyan-400" />
            <span>Host Management & Creator Contracts</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Audit talent applications, manage streaming targets, set diamond commissions, and supervise active hosts.
          </p>
        </div>

        <button
          onClick={handleExportCsv}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-bold transition-all shadow-xs ${
            isDarkMode
              ? 'border-cyan-500/40 bg-cyan-500/10 text-cyan-300 hover:bg-cyan-500/20'
              : 'border-cyan-300 bg-cyan-50 text-cyan-700 hover:bg-cyan-100'
          }`}
        >
          <Download className="w-3.5 h-3.5" />
          <span>Export Hosts CSV</span>
        </button>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* FILTER BAR */}
      <div className={`p-4 rounded-2xl border flex flex-col md:flex-row gap-3 items-center justify-between ${cardBg}`}>
        <div
          className={`w-full md:w-80 flex items-center gap-2 px-3 py-2 rounded-xl border text-xs ${
            isDarkMode ? 'bg-[#180e30] border-[#361f5c]' : 'bg-slate-100 border-slate-200'
          }`}
        >
          <Search className="w-4 h-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search host by name, ID, agency..."
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
            <option value="ALL">All Statuses</option>
            <option value="APPROVED">Approved Hosts</option>
            <option value="PENDING">Pending Auditions</option>
            <option value="SUSPENDED">Suspended</option>
            <option value="REJECTED">Rejected</option>
          </select>
        </div>
      </div>

      {/* HOSTS TABLE */}
      <div className={`rounded-2xl border overflow-hidden ${cardBg}`}>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead
              className={`border-b text-[11px] font-extrabold uppercase tracking-wider ${
                isDarkMode ? 'bg-[#180d33] border-[#2f1954] text-slate-400' : 'bg-slate-100 border-slate-200 text-slate-600'
              }`}
            >
              <tr>
                <th className="py-3 px-4">Host Talent</th>
                <th className="py-3 px-4">Agency / Affiliation</th>
                <th className="py-3 px-4">Room Hours</th>
                <th className="py-3 px-4">Diamond Earnings</th>
                <th className="py-3 px-4">Commission</th>
                <th className="py-3 px-4">Status & Warnings</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-inherit">
              {filteredHosts.length === 0 ? (
                <tr>
                  <td colSpan={7} className="py-8 text-center text-slate-400">
                    No host accounts matching criteria.
                  </td>
                </tr>
              ) : (
                filteredHosts.map((h) => (
                  <tr
                    key={h.id}
                    className={`transition-colors ${
                      isDarkMode ? 'hover:bg-[#1a0f38]' : 'hover:bg-slate-50'
                    }`}
                  >
                    <td className="py-3 px-4">
                      <div className="flex items-center gap-3">
                        <div className="relative">
                          <img
                            src={h.avatarUrl}
                            alt={h.username}
                            className="w-9 h-9 rounded-full object-cover border border-cyan-500/40"
                          />
                          {h.isLive && (
                            <span className="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-rose-500 rounded-full border-2 border-[#120826] animate-pulse" />
                          )}
                        </div>
                        <div>
                          <div className="font-bold flex items-center gap-1.5">
                            <span>{h.username}</span>
                            {h.isLive && (
                              <span className="text-[9px] font-black text-rose-400 bg-rose-500/20 px-1 rounded">
                                LIVE
                              </span>
                            )}
                          </div>
                          <div className="text-[10px] text-slate-400 font-mono">ID: {h.userId}</div>
                        </div>
                      </div>
                    </td>

                    <td className="py-3 px-4 font-semibold text-slate-300">
                      {h.agencyName ? (
                        <div className="text-cyan-300">🏢 {h.agencyName}</div>
                      ) : (
                        <span className="text-slate-400">Direct Contract</span>
                      )}
                    </td>

                    <td className="py-3 px-4">
                      <div className="font-bold flex items-center gap-1 text-slate-200">
                        <Clock className="w-3 h-3 text-cyan-400" />
                        <span>{h.totalRoomHours.toFixed(1)}h</span>
                      </div>
                      <div className="text-[10px] text-slate-400">
                        Target: {h.monthlyTargetHours}h/mo
                      </div>
                    </td>

                    <td className="py-3 px-4">
                      <div className="font-bold text-cyan-300 flex items-center gap-1">
                        <Gem className="w-3 h-3" />
                        <span>{h.totalEarnedDiamonds.toLocaleString()}</span>
                      </div>
                      <div className="text-[10px] text-emerald-400">
                        Withdrawable: {h.withdrawableDiamonds.toLocaleString()}
                      </div>
                    </td>

                    <td className="py-3 px-4 font-bold text-purple-300">
                      {(h.commissionRate * 100).toFixed(0)}%
                    </td>

                    <td className="py-3 px-4">
                      <div className="flex items-center gap-2">
                        {h.status === 'APPROVED' && (
                          <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                            APPROVED
                          </span>
                        )}
                        {h.status === 'PENDING' && (
                          <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-amber-500/20 text-amber-400 border border-amber-500/30">
                            AUDITION
                          </span>
                        )}
                        {h.status === 'SUSPENDED' && (
                          <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-rose-500/20 text-rose-400 border border-rose-500/30">
                            SUSPENDED
                          </span>
                        )}
                        {h.status === 'REJECTED' && (
                          <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-slate-500/20 text-slate-400 border border-slate-500/30">
                            REJECTED
                          </span>
                        )}

                        {h.warningCount > 0 && (
                          <span className="text-[10px] font-bold text-amber-400 bg-amber-500/10 px-1 rounded">
                            ⚠️ {h.warningCount}
                          </span>
                        )}
                      </div>
                    </td>

                    <td className="py-3 px-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        {h.status === 'PENDING' ? (
                          <>
                            <button
                              onClick={() => handleApprove(h.id)}
                              className="px-2.5 py-1 rounded-lg bg-emerald-500/20 text-emerald-300 hover:bg-emerald-500/30 font-bold text-[11px]"
                            >
                              Approve
                            </button>
                            <button
                              onClick={() => handleReject(h.id)}
                              className="px-2.5 py-1 rounded-lg bg-rose-500/20 text-rose-300 hover:bg-rose-500/30 font-bold text-[11px]"
                            >
                              Reject
                            </button>
                          </>
                        ) : (
                          <>
                            <button
                              onClick={() => handleIssueWarning(h)}
                              className="p-1.5 rounded-lg border border-amber-500/40 text-amber-300 hover:bg-amber-500/20"
                              title="Issue Warning"
                            >
                              <AlertTriangle className="w-3.5 h-3.5" />
                            </button>

                            <button
                              onClick={() => handleToggleSuspend(h)}
                              className={`p-1.5 rounded-lg border ${
                                h.status === 'SUSPENDED'
                                  ? 'border-emerald-500/40 text-emerald-300 hover:bg-emerald-500/20'
                                  : 'border-rose-500/40 text-rose-300 hover:bg-rose-500/20'
                              }`}
                              title={h.status === 'SUSPENDED' ? 'Reactivate Host' : 'Suspend Host'}
                            >
                              <ShieldAlert className="w-3.5 h-3.5" />
                            </button>

                            <button
                              onClick={() => {
                                setSelectedHost(h);
                                setCommissionRate((h.commissionRate * 100).toString());
                                setMonthlyHours(h.monthlyTargetHours.toString());
                                setNotes(h.notes || '');
                                setShowEditModal(true);
                              }}
                              className="p-1.5 rounded-lg border border-cyan-500/40 text-cyan-300 hover:bg-cyan-500/20"
                              title="Edit Host Details"
                            >
                              <Edit className="w-3.5 h-3.5" />
                            </button>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* EDIT HOST MODAL */}
      {showEditModal && selectedHost && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4">
          <div
            className={`w-full max-w-md rounded-2xl border p-5 shadow-2xl ${
              isDarkMode ? 'bg-[#150a2b] border-[#391e63]' : 'bg-white border-slate-200'
            }`}
          >
            <div className="flex items-center justify-between pb-3 border-b border-inherit">
              <h3 className="text-sm font-bold flex items-center gap-2">
                <Mic className="w-4 h-4 text-cyan-400" />
                <span>Configure Host Contract: {selectedHost.username}</span>
              </h3>
              <button onClick={() => setShowEditModal(false)} className="text-slate-400 hover:text-white">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSaveHost} className="space-y-4 pt-4 text-xs">
              <div>
                <label className="block text-slate-400 font-bold mb-1">Commission Rate (%)</label>
                <input
                  type="number"
                  min={10}
                  max={95}
                  value={commissionRate}
                  onChange={(e) => setCommissionRate(e.target.value)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                    isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                  }`}
                />
              </div>

              <div>
                <label className="block text-slate-400 font-bold mb-1">Monthly Streaming Target (Hours)</label>
                <input
                  type="number"
                  value={monthlyHours}
                  onChange={(e) => setMonthlyHours(e.target.value)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                    isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                  }`}
                />
              </div>

              <div>
                <label className="block text-slate-400 font-bold mb-1">Internal Contract Notes</label>
                <textarea
                  rows={2}
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none ${
                    isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                  }`}
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowEditModal(false)}
                  className="px-4 py-2 rounded-xl border border-slate-500/40 text-slate-300 hover:bg-slate-500/20 font-bold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 text-white font-extrabold shadow-md"
                >
                  Update Host Contract
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
