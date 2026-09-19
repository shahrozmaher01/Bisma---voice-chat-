import React, { useState } from 'react';
import {
  Settings,
  Activity,
  ShieldCheck,
  AlertTriangle,
  CheckCircle2,
  Server,
  Zap,
  Save,
  Lock,
  Radio,
  Cpu,
} from 'lucide-react';
import { adminBackend } from '../../../services/adminBackendService';

interface SettingsHealthModuleProps {
  isDarkMode: boolean;
}

export const SettingsHealthModule: React.FC<SettingsHealthModuleProps> = ({ isDarkMode }) => {
  const currentSettings = adminBackend.getSettings();
  const [maintenanceMode, setMaintenanceMode] = useState(currentSettings.maintenanceMode);
  const [minWithdrawal, setMinWithdrawal] = useState(currentSettings.minWithdrawalUsd.toString());
  const [maxWithdrawal, setMaxWithdrawal] = useState(currentSettings.maxWithdrawalUsd.toString());
  const [dailyLimit, setDailyLimit] = useState(currentSettings.dailyWithdrawalLimitUsd.toString());
  const [guestAccessAllowed, setGuestAccessAllowed] = useState(currentSettings.guestAccessAllowed);
  const [enableLuckyBags, setEnableLuckyBags] = useState(currentSettings.featureFlags.enableLuckyBags);
  const [enableLiveRooms, setEnableLiveRooms] = useState(currentSettings.featureFlags.enableLiveRoomCreations);

  const [notice, setNotice] = useState<string | null>(null);

  const handleSaveSettings = (e: React.FormEvent) => {
    e.preventDefault();
    adminBackend.updateSettings({
      maintenanceMode,
      minWithdrawalUsd: parseFloat(minWithdrawal) || 10,
      maxWithdrawalUsd: parseFloat(maxWithdrawal) || 5000,
      dailyWithdrawalLimitUsd: parseFloat(dailyLimit) || 2000,
      guestAccessAllowed,
      featureFlags: {
        ...currentSettings.featureFlags,
        enableLuckyBags,
        enableLiveRoomCreations: enableLiveRooms,
      },
    });
    setNotice(`System parameters and security settings updated successfully.`);
    setTimeout(() => setNotice(null), 3500);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <Settings className="w-6 h-6 text-purple-400" />
            <span>System Telemetry, Cluster Health & Global Parameters</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Real-time server infrastructure status, withdrawal thresholds, and emergency maintenance toggles.
          </p>
        </div>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* SYSTEM TELEMETRY CARDS */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3.5">
        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>Audio Cluster</span>
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
          </div>
          <div className="text-2xl font-black text-emerald-400 mt-1">99.98%</div>
          <span className="text-[11px] text-slate-400">WebRTC SFU active</span>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>Core Latency</span>
            <Activity className="w-3.5 h-3.5 text-cyan-400" />
          </div>
          <div className="text-2xl font-black text-cyan-400 mt-1">24ms</div>
          <span className="text-[11px] text-cyan-300">Fast edge distribution</span>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>Database Node</span>
            <Server className="w-3.5 h-3.5 text-purple-400" />
          </div>
          <div className="text-2xl font-black text-purple-300 mt-1">Healthy</div>
          <span className="text-[11px] text-purple-400">Replication 0 lag</span>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>Redis Cache</span>
            <Cpu className="w-3.5 h-3.5 text-amber-400" />
          </div>
          <div className="text-2xl font-black text-amber-300 mt-1">34% Mem</div>
          <span className="text-[11px] text-slate-400">148 connected clients</span>
        </div>
      </div>

      {/* GLOBAL SETTINGS FORM */}
      <form onSubmit={handleSaveSettings} className={`p-6 rounded-2xl border ${cardBg}`}>
        <div className="flex items-center justify-between pb-3 border-b border-inherit mb-4">
          <h3 className="font-extrabold text-sm text-white flex items-center gap-2">
            <Lock className="w-4 h-4 text-purple-400" />
            <span>Withdrawal Safety & Feature Gates</span>
          </h3>
          <button
            type="submit"
            className="flex items-center gap-1.5 px-4 py-1.5 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 text-white font-extrabold text-xs shadow-md hover:opacity-95"
          >
            <Save className="w-3.5 h-3.5" />
            <span>Save Settings</span>
          </button>
        </div>

        {/* Maintenance Switch */}
        <div
          className={`p-4 rounded-xl border mb-5 flex items-center justify-between ${
            maintenanceMode
              ? 'bg-rose-500/10 border-rose-500/40 text-rose-300'
              : isDarkMode
              ? 'bg-[#180e30] border-[#371f5c]'
              : 'bg-slate-50 border-slate-200'
          }`}
        >
          <div>
            <div className="font-extrabold text-xs flex items-center gap-2">
              <AlertTriangle className="w-4 h-4 text-amber-400" />
              <span>System Emergency Maintenance Mode</span>
            </div>
            <p className="text-[11px] text-slate-400 mt-0.5">
              When active, only Owner and Admins can log in. Users see maintenance notice.
            </p>
          </div>
          <input
            type="checkbox"
            checked={maintenanceMode}
            onChange={(e) => setMaintenanceMode(e.target.checked)}
            className="w-5 h-5 accent-rose-600 cursor-pointer"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs mb-5">
          <div>
            <label className="block text-slate-400 font-bold mb-1">Minimum Cashout Threshold (USD)</label>
            <input
              type="number"
              value={minWithdrawal}
              onChange={(e) => setMinWithdrawal(e.target.value)}
              className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
              }`}
            />
            <p className="text-[10px] text-slate-500 mt-1">Creator cannot withdraw less</p>
          </div>

          <div>
            <label className="block text-slate-400 font-bold mb-1">Single Request Cap (USD)</label>
            <input
              type="number"
              value={maxWithdrawal}
              onChange={(e) => setMaxWithdrawal(e.target.value)}
              className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
              }`}
            />
            <p className="text-[10px] text-slate-500 mt-1">Max per single transaction</p>
          </div>

          <div>
            <label className="block text-slate-400 font-bold mb-1">Daily Payout Limit (USD)</label>
            <input
              type="number"
              value={dailyLimit}
              onChange={(e) => setDailyLimit(e.target.value)}
              className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
              }`}
            />
            <p className="text-[10px] text-slate-500 mt-1">Total allowed per user per 24 hours</p>
          </div>
        </div>

        {/* Feature Switches */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs pt-4 border-t border-inherit">
          <div className="flex items-center justify-between p-3 rounded-xl border border-inherit">
            <div>
              <div className="font-bold text-slate-200">Lucky Coin Bags</div>
              <div className="text-[10px] text-slate-400">In-room lucky coin drops</div>
            </div>
            <input
              type="checkbox"
              checked={enableLuckyBags}
              onChange={(e) => setEnableLuckyBags(e.target.checked)}
              className="w-4 h-4 accent-purple-600"
            />
          </div>

          <div className="flex items-center justify-between p-3 rounded-xl border border-inherit">
            <div>
              <div className="font-bold text-slate-200">Guest Access Allowed</div>
              <div className="text-[10px] text-slate-400">Allows guest browsing</div>
            </div>
            <input
              type="checkbox"
              checked={guestAccessAllowed}
              onChange={(e) => setGuestAccessAllowed(e.target.checked)}
              className="w-4 h-4 accent-purple-600"
            />
          </div>

          <div className="flex items-center justify-between p-3 rounded-xl border border-inherit">
            <div>
              <div className="font-bold text-slate-200">Live Room Creation</div>
              <div className="text-[10px] text-slate-400">Permission to host new rooms</div>
            </div>
            <input
              type="checkbox"
              checked={enableLiveRooms}
              onChange={(e) => setEnableLiveRooms(e.target.checked)}
              className="w-4 h-4 accent-purple-600"
            />
          </div>
        </div>
      </form>
    </div>
  );
};
