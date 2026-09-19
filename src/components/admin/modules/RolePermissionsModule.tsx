import React, { useState } from 'react';
import {
  Shield,
  CheckCircle2,
  XCircle,
  Lock,
  Unlock,
  Users,
  Eye,
  Edit,
  DollarSign,
  Mic,
  Briefcase,
  AlertTriangle,
  Radio,
  Sliders,
  Sparkles,
} from 'lucide-react';
import { AdminRole, RolePermissions } from '../../../types/admin';
import { DEFAULT_ROLE_PERMISSIONS } from '../../../services/adminBackendService';

interface RolePermissionsModuleProps {
  activeRole: AdminRole;
  onChangeRole: (role: AdminRole) => void;
  isDarkMode: boolean;
}

export const RolePermissionsModule: React.FC<RolePermissionsModuleProps> = ({
  activeRole,
  onChangeRole,
  isDarkMode,
}) => {
  const [selectedRoleToInspect, setSelectedRoleToInspect] = useState<AdminRole>('Super Admin');

  const ALL_ROLES: { role: AdminRole; desc: string; color: string }[] = [
    { role: 'Owner', desc: 'Supreme master authority. Unlimited privileges and system switchboard control.', color: 'text-amber-400 bg-amber-500/20 border-amber-500/40' },
    { role: 'Super Admin', desc: 'Enterprise administration, role delegation, security overrides, and global audits.', color: 'text-rose-400 bg-rose-500/20 border-rose-500/40' },
    { role: 'Admin', desc: 'Day-to-day platform oversight, user moderation, room controls, and content management.', color: 'text-purple-400 bg-purple-500/20 border-purple-500/40' },
    { role: 'Moderator', desc: 'Real-time room monitoring, audio chat safety enforcement, and report resolution.', color: 'text-blue-400 bg-blue-500/20 border-blue-500/40' },
    { role: 'Finance Manager', desc: 'Coin packages, withdrawal approval, payment transaction verification, and refunds.', color: 'text-emerald-400 bg-emerald-500/20 border-emerald-500/40' },
    { role: 'Host Manager', desc: 'Talent recruitment, voice audition review, target hours, and host warnings.', color: 'text-cyan-400 bg-cyan-500/20 border-cyan-500/40' },
    { role: 'Agent Manager', desc: 'Agency registration, commission contracts, host allocations, and agency payouts.', color: 'text-indigo-400 bg-indigo-500/20 border-indigo-500/40' },
    { role: 'Support Staff', desc: 'Customer support tickets, user profile verification, and basic inquiries.', color: 'text-slate-300 bg-slate-500/20 border-slate-500/40' },
  ];

  const permissions = DEFAULT_ROLE_PERMISSIONS[selectedRoleToInspect];

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div>
        <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
          <Shield className="w-6 h-6 text-purple-400" />
          <span>Role-Based Access Control (RBAC) & Authority Matrix</span>
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Granular permission definitions across 8 system roles. Verify operational limits and test roles in real-time.
        </p>
      </div>

      {/* ACTIVE ROLE SIMULATION BANNER */}
      <div
        className={`p-4 rounded-2xl border flex flex-col sm:flex-row items-center justify-between gap-3 ${
          isDarkMode ? 'bg-purple-950/30 border-purple-500/40' : 'bg-purple-50 border-purple-200'
        }`}
      >
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-purple-600 text-white flex items-center justify-center font-black shadow-md">
            <Lock className="w-5 h-5" />
          </div>
          <div>
            <div className="text-xs font-bold text-slate-400">Currently Active Simulation Role</div>
            <div className="text-base font-extrabold text-white flex items-center gap-2">
              <span>{activeRole}</span>
              <span className="text-[10px] font-mono px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                ACTIVE IN DASHBOARD
              </span>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-2 text-xs">
          <span className="text-slate-400 font-semibold">Test as:</span>
          <select
            value={activeRole}
            onChange={(e) => onChangeRole(e.target.value as AdminRole)}
            className={`px-3 py-1.5 rounded-xl border font-bold outline-none ${
              isDarkMode ? 'bg-[#1a0e36] border-[#442578] text-purple-300' : 'bg-white border-purple-300 text-purple-900'
            }`}
          >
            {ALL_ROLES.map((r) => (
              <option key={r.role} value={r.role}>
                {r.role}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* ROLES SELECTOR TABS */}
      <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-8 gap-2">
        {ALL_ROLES.map((r) => (
          <button
            key={r.role}
            onClick={() => setSelectedRoleToInspect(r.role)}
            className={`p-2.5 rounded-xl border text-left transition-all ${
              selectedRoleToInspect === r.role
                ? isDarkMode
                  ? 'bg-gradient-to-br from-[#7c4dff] to-[#451db0] text-white border-purple-400 shadow-md scale-102'
                  : 'bg-purple-600 text-white border-purple-700 shadow-md'
                : isDarkMode
                ? 'bg-[#150a2b] border-[#2b174e] text-slate-400 hover:text-white'
                : 'bg-white border-slate-200 text-slate-700 hover:bg-slate-50'
            }`}
          >
            <div className="font-extrabold text-xs truncate">{r.role}</div>
            <div className="text-[9px] opacity-80 truncate mt-0.5">8 Modules</div>
          </button>
        ))}
      </div>

      {/* INSPECTED ROLE DETAILS & PERMISSION BLOCKS */}
      <div className={`p-6 rounded-2xl border ${cardBg}`}>
        <div className="flex items-center justify-between pb-4 border-b border-inherit">
          <div>
            <h3 className="text-base font-extrabold flex items-center gap-2">
              <span>Authority Matrix for: {selectedRoleToInspect}</span>
            </h3>
            <p className="text-xs text-slate-400 mt-0.5">
              {ALL_ROLES.find((r) => r.role === selectedRoleToInspect)?.desc}
            </p>
          </div>

          <button
            onClick={() => onChangeRole(selectedRoleToInspect)}
            className="px-3 py-1.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs shadow-md"
          >
            Activate This Role
          </button>
        </div>

        {/* PERMISSIONS GRID */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 pt-4">
          {/* Users */}
          <div className={`p-4 rounded-xl border ${isDarkMode ? 'bg-[#180e30] border-[#311c57]' : 'bg-slate-50 border-slate-200'}`}>
            <h4 className="text-xs font-black uppercase text-purple-400 flex items-center gap-1.5 mb-3">
              <Users className="w-3.5 h-3.5" />
              <span>User Management</span>
            </h4>
            <div className="space-y-1.5 text-xs">
              {Object.entries(permissions.users).map(([perm, val]) => (
                <div key={perm} className="flex items-center justify-between">
                  <span className="capitalize text-slate-300">{perm.replace(/([A-Z])/g, ' $1')}</span>
                  {val ? (
                    <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                  ) : (
                    <XCircle className="w-4 h-4 text-rose-500/60" />
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Voice Rooms */}
          <div className={`p-4 rounded-xl border ${isDarkMode ? 'bg-[#180e30] border-[#311c57]' : 'bg-slate-50 border-slate-200'}`}>
            <h4 className="text-xs font-black uppercase text-rose-400 flex items-center gap-1.5 mb-3">
              <Radio className="w-3.5 h-3.5" />
              <span>Voice Rooms</span>
            </h4>
            <div className="space-y-1.5 text-xs">
              {Object.entries(permissions.rooms).map(([perm, val]) => (
                <div key={perm} className="flex items-center justify-between">
                  <span className="capitalize text-slate-300">{perm.replace(/([A-Z])/g, ' $1')}</span>
                  {val ? (
                    <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                  ) : (
                    <XCircle className="w-4 h-4 text-rose-500/60" />
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Finance */}
          <div className={`p-4 rounded-xl border ${isDarkMode ? 'bg-[#180e30] border-[#311c57]' : 'bg-slate-50 border-slate-200'}`}>
            <h4 className="text-xs font-black uppercase text-emerald-400 flex items-center gap-1.5 mb-3">
              <DollarSign className="w-3.5 h-3.5" />
              <span>Finance & Withdrawals</span>
            </h4>
            <div className="space-y-1.5 text-xs">
              {Object.entries(permissions.finance).map(([perm, val]) => (
                <div key={perm} className="flex items-center justify-between">
                  <span className="capitalize text-slate-300">{perm.replace(/([A-Z])/g, ' $1')}</span>
                  {val ? (
                    <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                  ) : (
                    <XCircle className="w-4 h-4 text-rose-500/60" />
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Hosts */}
          <div className={`p-4 rounded-xl border ${isDarkMode ? 'bg-[#180e30] border-[#311c57]' : 'bg-slate-50 border-slate-200'}`}>
            <h4 className="text-xs font-black uppercase text-cyan-400 flex items-center gap-1.5 mb-3">
              <Mic className="w-3.5 h-3.5" />
              <span>Hosts & Auditions</span>
            </h4>
            <div className="space-y-1.5 text-xs">
              {Object.entries(permissions.hosts).map(([perm, val]) => (
                <div key={perm} className="flex items-center justify-between">
                  <span className="capitalize text-slate-300">{perm.replace(/([A-Z])/g, ' $1')}</span>
                  {val ? (
                    <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                  ) : (
                    <XCircle className="w-4 h-4 text-rose-500/60" />
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Agents */}
          <div className={`p-4 rounded-xl border ${isDarkMode ? 'bg-[#180e30] border-[#311c57]' : 'bg-slate-50 border-slate-200'}`}>
            <h4 className="text-xs font-black uppercase text-amber-400 flex items-center gap-1.5 mb-3">
              <Briefcase className="w-3.5 h-3.5" />
              <span>Agency Network</span>
            </h4>
            <div className="space-y-1.5 text-xs">
              {Object.entries(permissions.agents).map(([perm, val]) => (
                <div key={perm} className="flex items-center justify-between">
                  <span className="capitalize text-slate-300">{perm.replace(/([A-Z])/g, ' $1')}</span>
                  {val ? (
                    <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                  ) : (
                    <XCircle className="w-4 h-4 text-rose-500/60" />
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* System Settings & Owner Controls */}
          <div className={`p-4 rounded-xl border ${isDarkMode ? 'bg-[#180e30] border-[#311c57]' : 'bg-slate-50 border-slate-200'}`}>
            <h4 className="text-xs font-black uppercase text-yellow-400 flex items-center gap-1.5 mb-3">
              <Lock className="w-3.5 h-3.5" />
              <span>System & Owner Controls</span>
            </h4>
            <div className="space-y-1.5 text-xs">
              {Object.entries(permissions.settings).map(([perm, val]) => (
                <div key={perm} className="flex items-center justify-between">
                  <span className="capitalize text-slate-300">{perm.replace(/([A-Z])/g, ' $1')}</span>
                  {val ? (
                    <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                  ) : (
                    <XCircle className="w-4 h-4 text-rose-500/60" />
                  )}
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
