import React, { useState } from 'react';
import {
  Crown,
  Sparkles,
  Users,
  CheckCircle2,
  Edit,
  Coins,
  Shield,
  Zap,
  Star,
  Award,
} from 'lucide-react';
import { useAura } from '../../../context/AuraContext';

interface VipManagementModuleProps {
  isDarkMode: boolean;
}

export const VipManagementModule: React.FC<VipManagementModuleProps> = ({ isDarkMode }) => {
  const { allUsers } = useAura();

  const [vipTiers, setVipTiers] = useState([
    { level: 1, name: 'VIP Baron', monthlyCoins: 100000, color: 'text-amber-400', badge: '🎖️', perks: ['Bronze Entrance Banner', 'VIP Nameplate', '1.1x XP Boost'] },
    { level: 2, name: 'VIP Count', monthlyCoins: 250000, color: 'text-cyan-400', badge: '🏅', perks: ['Silver Entrance Car', 'Exclusive Bubble Chat', 'Anti-Mute Immunity'] },
    { level: 3, name: 'VIP Duke', monthlyCoins: 500000, color: 'text-purple-400', badge: '👑', perks: ['Gold Supercar Entrance', 'Animated Avatar Frame', '1.25x XP Boost'] },
    { level: 4, name: 'VIP Prince', monthlyCoins: 1000000, color: 'text-rose-400', badge: '💎', perks: ['Diamond Jet Entrance', 'Invisible Room Stealth', 'VIP Crown Mic Ring'] },
    { level: 5, name: 'VIP King', monthlyCoins: 2500000, color: 'text-yellow-300', badge: '🪐', perks: ['Galaxy Entrance FX', 'Global Room Broadcast', 'Anti-Kick Immunity'] },
    { level: 6, name: 'VIP Emperor', monthlyCoins: 5000000, color: 'text-emerald-400', badge: '⚡', perks: ['Imperial Dragon Entrance', 'Custom Room ID Option', 'Dedicated Support'] },
    { level: 7, name: 'Supreme Sovereign', monthlyCoins: 10000000, color: 'text-indigo-300', badge: '🌌', perks: ['Supreme Universe FX', 'Official Creator Frame', 'Infinite Mic Hold'] },
  ]);

  const vipUsers = allUsers.filter((u) => u.vipLevel > 0);

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div>
        <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
          <Crown className="w-6 h-6 text-amber-400" />
          <span>VIP Hierarchy & Privilege Matrix</span>
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Define prestige tiers, configure entrance animations, audio rings, and view active VIP subscribers.
        </p>
      </div>

      {/* STATS */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3.5">
        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">Total VIP Subscribers</span>
          <div className="text-2xl font-black text-amber-400 mt-1">{vipUsers.length}</div>
          <span className="text-[11px] text-emerald-400">High retention rate</span>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">Highest Active Tier</span>
          <div className="text-2xl font-black text-purple-400 mt-1">
            VIP {Math.max(...allUsers.map((u) => u.vipLevel), 1)}
          </div>
          <span className="text-[11px] text-purple-300">Top spender prestige</span>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">Monthly VIP MRR</span>
          <div className="text-2xl font-black text-emerald-400 mt-1">$4,850</div>
          <span className="text-[11px] text-slate-400">Recurring coin burn</span>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">Available Tiers</span>
          <div className="text-2xl font-black text-cyan-400 mt-1">7 Tiers</div>
          <span className="text-[11px] text-cyan-300">Tier 1 through Tier 7</span>
        </div>
      </div>

      {/* VIP TIERS CARDS */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {vipTiers.map((tier) => (
          <div key={tier.level} className={`p-5 rounded-2xl border flex flex-col justify-between ${cardBg}`}>
            <div>
              <div className="flex items-center justify-between pb-3 border-b border-inherit">
                <div className="flex items-center gap-2">
                  <span className="text-2xl">{tier.badge}</span>
                  <div>
                    <h3 className={`font-black text-sm ${tier.color}`}>{tier.name}</h3>
                    <span className="text-[10px] font-mono text-slate-400">Level {tier.level}</span>
                  </div>
                </div>

                <div className="text-right">
                  <div className="text-xs font-black text-amber-300 flex items-center gap-1 justify-end">
                    <Coins className="w-3 h-3" />
                    <span>{tier.monthlyCoins.toLocaleString()}</span>
                  </div>
                  <div className="text-[10px] text-slate-500">/ 30 days</div>
                </div>
              </div>

              {/* Perks list */}
              <div className="py-3 space-y-1.5 text-xs">
                {tier.perks.map((perk, pIdx) => (
                  <div key={pIdx} className="flex items-center gap-2 text-slate-300">
                    <Sparkles className="w-3 h-3 text-amber-400 flex-shrink-0" />
                    <span>{perk}</span>
                  </div>
                ))}
              </div>
            </div>

            <div className="pt-3 border-t border-inherit flex items-center justify-between text-[11px] text-slate-400">
              <span>{allUsers.filter((u) => u.vipLevel === tier.level).length} Active Members</span>
              <span className="text-purple-400 font-bold">Privilege Active</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
