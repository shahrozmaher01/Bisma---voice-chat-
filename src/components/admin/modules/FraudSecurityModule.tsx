import React, { useState } from 'react';
import {
  Lock,
  AlertOctagon,
  ShieldAlert,
  Search,
  CheckCircle2,
  Ban,
  Eye,
  Smartphone,
  Globe,
  Fingerprint,
} from 'lucide-react';

interface FraudSecurityModuleProps {
  isDarkMode: boolean;
}

export const FraudSecurityModule: React.FC<FraudSecurityModuleProps> = ({ isDarkMode }) => {
  const [flags, setFlags] = useState([
    {
      id: 'SEC-101',
      username: 'ShadowBot_99',
      riskScore: 92,
      riskLevel: 'HIGH',
      reason: 'Shared device UUID with 6 newly registered accounts in 10 mins',
      ip: '182.185.12.45',
      device: 'Samsung SM-A525F',
      action: 'Account Frozen',
    },
    {
      id: 'SEC-102',
      username: 'FastTrader_PK',
      riskScore: 78,
      riskLevel: 'MEDIUM',
      reason: 'Abnormal coin transfer velocity: 500,000 coins bounced across 3 accounts',
      ip: '39.40.112.9',
      device: 'Infinix X688B',
      action: 'Pending Review',
    },
    {
      id: 'SEC-103',
      username: 'LuckySpender7',
      riskScore: 64,
      riskLevel: 'MEDIUM',
      reason: 'Multiple failed Google Play card billing authorizations',
      ip: '119.160.67.20',
      device: 'Xiaomi Redmi Note 11',
      action: 'Card Verification Required',
    },
  ]);

  const [notice, setNotice] = useState<string | null>(null);

  const handleFreeze = (id: string) => {
    setFlags(flags.map((f) => (f.id === id ? { ...f, action: 'Permanent Device Ban Executed' } : f)));
    setNotice(`Hardware device fingerprint permanently banned from server.`);
    setTimeout(() => setNotice(null), 3000);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div>
        <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
          <ShieldAlert className="w-6 h-6 text-rose-500" />
          <span>Fraud Detection, Device Fingerprints & Anti-Sybil</span>
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Detect multi-account bot farms, unusual gifting ring velocities, and stolen payment card attempts.
        </p>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* FRAUD TILES */}
      <div className="space-y-3">
        {flags.map((item) => (
          <div
            key={item.id}
            className={`p-5 rounded-2xl border flex flex-col md:flex-row md:items-center justify-between gap-4 ${cardBg}`}
          >
            <div className="space-y-1 min-w-0">
              <div className="flex items-center gap-2">
                <span
                  className={`px-2 py-0.5 rounded-full text-[10px] font-black border ${
                    item.riskLevel === 'HIGH'
                      ? 'bg-rose-500/20 text-rose-400 border-rose-500/30'
                      : 'bg-amber-500/20 text-amber-400 border-amber-500/30'
                  }`}
                >
                  RISK: {item.riskScore}/100 ({item.riskLevel})
                </span>
                <span className="font-extrabold text-sm text-white">{item.username}</span>
                <span className="text-[10px] font-mono text-slate-500">{item.id}</span>
              </div>

              <p className="text-xs text-rose-300 font-semibold">{item.reason}</p>

              <div className="text-[11px] text-slate-400 flex flex-wrap items-center gap-3 pt-1">
                <span className="flex items-center gap-1 font-mono">
                  <Globe className="w-3 h-3 text-slate-500" />
                  <span>{item.ip}</span>
                </span>
                <span className="flex items-center gap-1">
                  <Smartphone className="w-3 h-3 text-slate-500" />
                  <span>{item.device}</span>
                </span>
                <span className="text-amber-400 font-bold">Status: {item.action}</span>
              </div>
            </div>

            <div className="flex items-center gap-2 self-end md:self-center">
              <button
                onClick={() => handleFreeze(item.id)}
                className="px-3 py-1.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-bold text-xs flex items-center gap-1 shadow-xs"
              >
                <Ban className="w-3.5 h-3.5" />
                <span>Device Kill Ban</span>
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
