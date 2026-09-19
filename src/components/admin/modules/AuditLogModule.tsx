import React, { useState } from 'react';
import {
  FileText,
  Search,
  Download,
  Shield,
  Clock,
  UserCheck,
  Filter,
} from 'lucide-react';
import { useAura } from '../../../context/AuraContext';
import { AuditLogEntity } from '../../../types';
import { adminBackend } from '../../../services/adminBackendService';

interface AuditLogModuleProps {
  isDarkMode: boolean;
}

export const AuditLogModule: React.FC<AuditLogModuleProps> = ({ isDarkMode }) => {
  const { auditLogs } = useAura();
  const [searchQuery, setSearchQuery] = useState('');
  const [moduleFilter, setModuleFilter] = useState('ALL');

  const filteredLogs = auditLogs.filter((l) => {
    const matchesSearch =
      l.action.toLowerCase().includes(searchQuery.toLowerCase()) ||
      l.adminName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      l.targetName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      l.targetId.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesModule = moduleFilter === 'ALL' || l.targetType === moduleFilter;
    return matchesSearch && matchesModule;
  });

  const handleExportCsv = () => {
    const data = filteredLogs.map((l) => ({
      LogID: l.id,
      Timestamp: new Date(l.timestamp).toISOString(),
      Admin: l.adminName,
      Role: l.adminRole,
      TargetType: l.targetType,
      Action: l.action,
      TargetID: l.targetId,
      TargetName: l.targetName,
      PrevValue: l.previousValue || '',
      NewValue: l.newValue || '',
      Success: l.isSuccess ? 'YES' : 'NO',
    }));
    adminBackend.exportCsv('aura_audit_trail_immutable', data);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <FileText className="w-6 h-6 text-purple-400" />
            <span>Immutable Governance Audit Trail</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Cryptographically sealed activity log of every staff intervention, role assignment, and financial debit.
          </p>
        </div>

        <button
          onClick={handleExportCsv}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-bold transition-all shadow-xs ${
            isDarkMode
              ? 'border-purple-500/40 bg-purple-500/10 text-purple-300 hover:bg-purple-500/20'
              : 'border-purple-300 bg-purple-50 text-purple-700 hover:bg-purple-100'
          }`}
        >
          <Download className="w-3.5 h-3.5" />
          <span>Export Audit Log (CSV)</span>
        </button>
      </div>

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
            placeholder="Search audit trail by admin, action, target..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-transparent border-none outline-none text-xs"
          />
        </div>

        <div className="flex items-center gap-2 text-xs">
          <select
            value={moduleFilter}
            onChange={(e) => setModuleFilter(e.target.value)}
            className={`px-3 py-2 rounded-xl border font-semibold outline-none ${
              isDarkMode ? 'bg-[#180e30] border-[#361f5c] text-slate-200' : 'bg-slate-100 border-slate-200 text-slate-800'
            }`}
          >
            <option value="ALL">All Target Entities</option>
            <option value="User">User Accounts</option>
            <option value="Role">Role Privileges</option>
            <option value="Host">Host Operations</option>
            <option value="Agency">Agencies</option>
            <option value="Room">Voice Stages</option>
            <option value="Frame">Official Frames</option>
          </select>
        </div>
      </div>

      {/* LOGS TABLE */}
      <div className={`rounded-2xl border overflow-hidden ${cardBg}`}>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead
              className={`border-b text-[11px] font-extrabold uppercase tracking-wider ${
                isDarkMode ? 'bg-[#180d33] border-[#2f1954] text-slate-400' : 'bg-slate-100 border-slate-200 text-slate-600'
              }`}
            >
              <tr>
                <th className="py-3 px-4">Timestamp</th>
                <th className="py-3 px-4">Staff Administrator</th>
                <th className="py-3 px-4">Scope & Action</th>
                <th className="py-3 px-4">Target Entity</th>
                <th className="py-3 px-4">Delta (Prev → New)</th>
                <th className="py-3 px-4 text-right">Result</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-inherit">
              {filteredLogs.map((log) => (
                <tr
                  key={log.id}
                  className={`transition-colors ${
                    isDarkMode ? 'hover:bg-[#1a0f38]' : 'hover:bg-slate-50'
                  }`}
                >
                  <td className="py-3 px-4 font-mono">
                    <div className="text-white font-bold">
                      {new Date(log.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })}
                    </div>
                    <div className="text-[10px] text-slate-500">
                      {new Date(log.timestamp).toLocaleDateString()}
                    </div>
                  </td>

                  <td className="py-3 px-4">
                    <div className="font-extrabold text-purple-300">{log.adminName}</div>
                    <span className="text-[10px] text-slate-400 font-semibold">{log.adminRole}</span>
                  </td>

                  <td className="py-3 px-4">
                    <span className="px-2 py-0.5 rounded-full text-[10px] font-mono font-bold bg-purple-500/20 text-purple-300 border border-purple-500/30">
                      {log.targetType.toUpperCase()}
                    </span>
                    <div className="font-bold text-slate-200 mt-0.5">{log.action}</div>
                  </td>

                  <td className="py-3 px-4 font-mono text-[11px] text-cyan-300">
                    <div>{log.targetName}</div>
                    <span className="text-[10px] text-slate-500">{log.targetId}</span>
                  </td>

                  <td className="py-3 px-4 text-slate-300 max-w-xs truncate">
                    {log.previousValue || log.newValue ? (
                      <span>
                        <span className="text-rose-400 line-through mr-1">{log.previousValue || 'none'}</span>
                        <span className="text-emerald-400">→ {log.newValue}</span>
                      </span>
                    ) : (
                      <span className="text-slate-500 italic">No value change recorded</span>
                    )}
                  </td>

                  <td className="py-3 px-4 text-right">
                    <span
                      className={`px-2 py-0.5 rounded-full text-[10px] font-black border ${
                        log.isSuccess
                          ? 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30'
                          : 'bg-rose-500/20 text-rose-400 border-rose-500/30'
                      }`}
                    >
                      {log.isSuccess ? 'SUCCESS' : 'FAILED'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
