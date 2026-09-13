import React, { useState } from 'react';
import { X, Trophy, Flame, Crown, Gem, Coins, Heart } from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { AvatarWithFrame } from './AvatarWithFrame';

interface RankingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  onOpenUserProfile: (userId: string) => void;
}

export const RankingsModal: React.FC<RankingsModalProps> = ({
  isOpen,
  onClose,
  onOpenUserProfile,
}) => {
  const { allUsers, rooms } = useAura();
  const [rankingTab, setRankingTab] = useState<'wealth' | 'charm' | 'room'>('wealth');

  if (!isOpen) return null;

  // Compute Wealth ranking based on coins + richLevel
  const wealthRankings = [...allUsers].sort((a, b) => b.coins - a.coins);

  // Compute Charm ranking based on diamonds + charmLevel
  const charmRankings = [...allUsers].sort((a, b) => b.diamonds - a.diamonds);

  // Compute Room ranking based on onlineCount
  const roomRankings = [...rooms].sort((a, b) => b.onlineCount - a.onlineCount);

  return (
    <div
      id="rankings-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-3 sm:p-5 animate-in fade-in duration-150"
      onClick={onClose}
    >
      <div
        id="rankings-modal"
        className="w-full max-w-md bg-[#130a26] border border-[#442875] rounded-3xl p-4 sm:p-5 shadow-2xl flex flex-col max-h-[88vh] overflow-hidden relative"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between border-b border-[#2b174a] pb-3">
          <div className="flex items-center gap-2">
            <Trophy className="w-5 h-5 text-amber-400" />
            <h2 className="text-base font-extrabold text-white">Global Leaderboards</h2>
          </div>
          <button
            id="close-rankings-btn"
            onClick={onClose}
            className="p-1.5 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab Switcher */}
        <div className="grid grid-cols-3 gap-1.5 p-1 bg-[#1a0f33] rounded-2xl my-3">
          <button
            id="tab-rank-wealth"
            onClick={() => setRankingTab('wealth')}
            className={`py-1.5 text-xs font-bold rounded-xl transition-all ${
              rankingTab === 'wealth'
                ? 'bg-amber-500 text-black shadow'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Wealth 🪙
          </button>
          <button
            id="tab-rank-charm"
            onClick={() => setRankingTab('charm')}
            className={`py-1.5 text-xs font-bold rounded-xl transition-all ${
              rankingTab === 'charm'
                ? 'bg-[#ff2a85] text-white shadow'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Charm 💎
          </button>
          <button
            id="tab-rank-room"
            onClick={() => setRankingTab('room')}
            className={`py-1.5 text-xs font-bold rounded-xl transition-all ${
              rankingTab === 'room'
                ? 'bg-[#00e5ff] text-black shadow'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Top Rooms 🎙️
          </button>
        </div>

        {/* List Content */}
        <div className="flex-1 overflow-y-auto space-y-2 pr-1">
          {rankingTab === 'wealth' &&
            wealthRankings.map((u, idx) => (
              <div
                key={u.id}
                onClick={() => onOpenUserProfile(u.id)}
                className={`flex items-center gap-3 p-2.5 rounded-2xl border cursor-pointer transition-transform hover:scale-[1.01] ${
                  idx === 0
                    ? 'bg-gradient-to-r from-amber-500/20 to-[#221342] border-amber-400/50'
                    : idx === 1
                    ? 'bg-gradient-to-r from-slate-400/20 to-[#221342] border-slate-400/40'
                    : idx === 2
                    ? 'bg-gradient-to-r from-amber-700/20 to-[#221342] border-amber-700/40'
                    : 'bg-[#1a0f33] border-[#311b58]'
                }`}
              >
                <div className="w-6 text-center font-black text-sm">
                  {idx === 0 ? '🥇' : idx === 1 ? '🥈' : idx === 2 ? '🥉' : `${idx + 1}`}
                </div>
                <AvatarWithFrame
                  avatarUrl={u.avatarUrl}
                  size={38}
                  equippedFrameId={u.equippedFrameId}
                  vipLevel={u.vipLevel}
                />
                <div className="flex-1 min-w-0">
                  <div className="font-bold text-xs text-white truncate">{u.username}</div>
                  <div className="text-[10px] text-slate-400">ID: {u.id} • {u.country}</div>
                </div>
                <div className="text-right">
                  <div className="flex items-center gap-1 text-xs font-extrabold text-amber-300">
                    <Coins className="w-3.5 h-3.5 text-amber-400" />
                    <span>{u.coins.toLocaleString()}</span>
                  </div>
                  <div className="text-[10px] text-slate-400">Rich Lv.{u.richLevel}</div>
                </div>
              </div>
            ))}

          {rankingTab === 'charm' &&
            charmRankings.map((u, idx) => (
              <div
                key={u.id}
                onClick={() => onOpenUserProfile(u.id)}
                className={`flex items-center gap-3 p-2.5 rounded-2xl border cursor-pointer transition-transform hover:scale-[1.01] ${
                  idx === 0
                    ? 'bg-gradient-to-r from-rose-500/20 to-[#221342] border-rose-400/50'
                    : idx === 1
                    ? 'bg-gradient-to-r from-purple-500/20 to-[#221342] border-purple-400/40'
                    : idx === 2
                    ? 'bg-gradient-to-r from-pink-700/20 to-[#221342] border-pink-700/40'
                    : 'bg-[#1a0f33] border-[#311b58]'
                }`}
              >
                <div className="w-6 text-center font-black text-sm">
                  {idx === 0 ? '🥇' : idx === 1 ? '🥈' : idx === 2 ? '🥉' : `${idx + 1}`}
                </div>
                <AvatarWithFrame
                  avatarUrl={u.avatarUrl}
                  size={38}
                  equippedFrameId={u.equippedFrameId}
                  vipLevel={u.vipLevel}
                />
                <div className="flex-1 min-w-0">
                  <div className="font-bold text-xs text-white truncate">{u.username}</div>
                  <div className="text-[10px] text-slate-400">ID: {u.id} • {u.country}</div>
                </div>
                <div className="text-right">
                  <div className="flex items-center gap-1 text-xs font-extrabold text-rose-400">
                    <Gem className="w-3.5 h-3.5 text-cyan-400" />
                    <span>{u.diamonds.toLocaleString()}</span>
                  </div>
                  <div className="text-[10px] text-slate-400">Charm Lv.{u.charmLevel}</div>
                </div>
              </div>
            ))}

          {rankingTab === 'room' &&
            roomRankings.map((r, idx) => (
              <div
                key={r.id}
                className={`flex items-center gap-3 p-2.5 rounded-2xl border ${
                  idx === 0
                    ? 'bg-gradient-to-r from-cyan-500/20 to-[#221342] border-cyan-400/50'
                    : 'bg-[#1a0f33] border-[#311b58]'
                }`}
              >
                <div className="w-6 text-center font-black text-sm">
                  {idx === 0 ? '🥇' : idx === 1 ? '🥈' : idx === 2 ? '🥉' : `${idx + 1}`}
                </div>
                <img
                  src={r.coverUrl}
                  alt={r.title}
                  className="w-10 h-10 rounded-xl object-cover border border-[#442875]"
                />
                <div className="flex-1 min-w-0">
                  <div className="font-bold text-xs text-white truncate">{r.title}</div>
                  <div className="text-[10px] text-slate-400">Host: {r.ownerName}</div>
                </div>
                <div className="text-right">
                  <div className="text-xs font-extrabold text-emerald-400">
                    {r.onlineCount} Online
                  </div>
                  <div className="text-[10px] text-slate-400">{r.category}</div>
                </div>
              </div>
            ))}
        </div>
      </div>
    </div>
  );
};
