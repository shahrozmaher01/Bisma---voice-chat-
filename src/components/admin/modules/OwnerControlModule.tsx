import React, { useState } from 'react';
import {
  Crown,
  AlertTriangle,
  Lock,
  Unlock,
  Shield,
  Zap,
  RefreshCw,
  CheckCircle2,
  Database,
  Coins,
  Gem,
  DollarSign,
} from 'lucide-react';
import { useAura } from '../../../context/AuraContext';
import { adminBackend } from '../../../services/adminBackendService';

interface OwnerControlModuleProps {
  isDarkMode: boolean;
}

export const OwnerControlModule: React.FC<OwnerControlModuleProps> = ({ isDarkMode }) => {
  const { allUsers, assignUserRole } = useAura();
  const [rootTargetUserId, setRootTargetUserId] = useState('');
  const [payoutsLocked, setPayoutsLocked] = useState(false);
  const [voiceLocked, setVoiceLocked] = useState(false);
  const [registrationLocked, setRegistrationLocked] = useState(false);
  const [notice, setNotice] = useState<string | null>(null);

  const handleGrantSuperAdmin = (e: React.FormEvent) => {
    e.preventDefault();
    if (!rootTargetUserId.trim()) return;
    const target = allUsers.find(
      (u) => u.id === rootTargetUserId.trim() || u.username.toLowerCase() === rootTargetUserId.trim().toLowerCase()
    );
    if (!target) {
      setNotice(`User "${rootTargetUserId}" not found.`);
      setTimeout(() => setNotice(null), 3000);
      return;
    }

    assignUserRole(target.id, 'Super Admin');
    setNotice(`Super Admin privileges successfully conferred to ${target.username} (${target.id})!`);
    setRootTargetUserId('');
    setTimeout(() => setNotice(null), 3500);
  };

  const handleEmergencyReset = () => {
    if (window.confirm('CRITICAL OWNER CONFIRMATION: Are you sure you want to re-seed and reset application database states?')) {
      localStorage.removeItem('aura_live_app_state_v1');
      localStorage.removeItem('aura_custom_gifts_v1');
      setNotice('Database successfully reset. Reloading environment...');
      setTimeout(() => {
        window.location.reload();
      }, 1500);
    }
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div>
        <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
          <Crown className="w-6 h-6 text-amber-400" />
          <span>Owner Master Switchboard & Root Console</span>
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Supreme cryptographic authority. High-level liquidity management, emergency killswitches, and root delegation.
        </p>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* EMERGENCY MASTER SWITCHES */}
      <div className={`p-6 rounded-2xl border border-rose-500/40 bg-rose-950/20 shadow-lg`}>
        <div className="flex items-center gap-2 text-rose-400 font-black text-sm mb-4">
          <AlertTriangle className="w-5 h-5" />
          <span>Emergency Master Killswitches (Instant Global Halt)</span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
          {/* Killswitch 1 */}
          <div className="p-4 rounded-xl border border-rose-500/30 bg-rose-900/20 flex flex-col justify-between">
            <div>
              <div className="font-extrabold text-white text-sm">Financial Cashout Freeze</div>
              <p className="text-[11px] text-slate-300 mt-1">
                Instantly pauses all automated and manual withdrawal transactions across all gateways.
              </p>
            </div>
            <button
              onClick={() => {
                setPayoutsLocked(!payoutsLocked);
                setNotice(`Payout Freeze is now ${!payoutsLocked ? 'ACTIVE' : 'DEACTIVATED'}.`);
                setTimeout(() => setNotice(null), 3000);
              }}
              className={`mt-4 w-full py-2 rounded-xl font-black text-xs transition-colors ${
                payoutsLocked
                  ? 'bg-rose-600 text-white'
                  : 'bg-slate-800 text-slate-300 hover:text-white'
              }`}
            >
              {payoutsLocked ? '🔒 PAYOUTS FROZEN' : 'ARM FREEZE SWITCH'}
            </button>
          </div>

          {/* Killswitch 2 */}
          <div className="p-4 rounded-xl border border-amber-500/30 bg-amber-900/20 flex flex-col justify-between">
            <div>
              <div className="font-extrabold text-white text-sm">Global Stage Lockdown</div>
              <p className="text-[11px] text-slate-300 mt-1">
                Forces all active audio streams into muted broadcast-only mode for immediate compliance audit.
              </p>
            </div>
            <button
              onClick={() => {
                setVoiceLocked(!voiceLocked);
                setNotice(`Stage Lockdown is now ${!voiceLocked ? 'ACTIVE' : 'DEACTIVATED'}.`);
                setTimeout(() => setNotice(null), 3000);
              }}
              className={`mt-4 w-full py-2 rounded-xl font-black text-xs transition-colors ${
                voiceLocked
                  ? 'bg-amber-600 text-black font-black'
                  : 'bg-slate-800 text-slate-300 hover:text-white'
              }`}
            >
              {voiceLocked ? '🔒 STAGES LOCKED' : 'ARM STAGE LOCKDOWN'}
            </button>
          </div>

          {/* Killswitch 3 */}
          <div className="p-4 rounded-xl border border-purple-500/30 bg-purple-900/20 flex flex-col justify-between">
            <div>
              <div className="font-extrabold text-white text-sm">New Registration Gate</div>
              <p className="text-[11px] text-slate-300 mt-1">
                Halts incoming user signups to mitigate bot swarms and DDoS account flood attacks.
              </p>
            </div>
            <button
              onClick={() => {
                setRegistrationLocked(!registrationLocked);
                setNotice(`Registration Gate is now ${!registrationLocked ? 'CLOSED' : 'OPEN'}.`);
                setTimeout(() => setNotice(null), 3000);
              }}
              className={`mt-4 w-full py-2 rounded-xl font-black text-xs transition-colors ${
                registrationLocked
                  ? 'bg-purple-600 text-white'
                  : 'bg-slate-800 text-slate-300 hover:text-white'
              }`}
            >
              {registrationLocked ? '🔒 SIGNUPS BLOCKED' : 'ALLOW NEW USERS'}
            </button>
          </div>
        </div>
      </div>

      {/* DIRECT ROOT ROLE DELEGATION */}
      <div className={`p-6 rounded-2xl border ${cardBg}`}>
        <h3 className="font-extrabold text-sm text-white mb-2 flex items-center gap-2">
          <Shield className="w-4 h-4 text-purple-400" />
          <span>Direct Root Role Delegation</span>
        </h3>
        <p className="text-xs text-slate-400 mb-4">
          Confer Super Admin root authority to any user profile by username or account ID.
        </p>

        <form onSubmit={handleGrantSuperAdmin} className="flex gap-2 max-w-lg">
          <input
            type="text"
            required
            placeholder="Enter Username or ID (e.g. u1 or Sherry)..."
            value={rootTargetUserId}
            onChange={(e) => setRootTargetUserId(e.target.value)}
            className={`flex-1 px-3 py-2 rounded-xl border text-xs font-bold outline-none ${
              isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
            }`}
          />
          <button
            type="submit"
            className="px-4 py-2 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 text-white font-extrabold text-xs shadow-md"
          >
            Grant Super Admin
          </button>
        </form>
      </div>

      {/* DATABASE RE-SEED UTILITY */}
      <div className={`p-6 rounded-2xl border border-rose-500/20 ${cardBg} flex items-center justify-between`}>
        <div>
          <h4 className="font-bold text-sm text-white flex items-center gap-2">
            <Database className="w-4 h-4 text-cyan-400" />
            <span>Reset / Re-Seed Database State</span>
          </h4>
          <p className="text-xs text-slate-400 mt-0.5">
            Restores initial voice rooms, users, gifts, and wallet packages to factory fresh condition.
          </p>
        </div>

        <button
          onClick={handleEmergencyReset}
          className="px-4 py-2 rounded-xl border border-rose-500/40 text-rose-300 hover:bg-rose-500/20 font-bold text-xs flex items-center gap-1.5"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          <span>Factory Reset Seed</span>
        </button>
      </div>
    </div>
  );
};
