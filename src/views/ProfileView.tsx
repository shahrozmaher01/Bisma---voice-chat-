import React, { useState } from 'react';
import {
  User as UserIcon,
  Shield,
  Coins,
  Gem,
  Award,
  Sparkles,
  Wallet,
  Settings,
  Gift,
  CheckCircle2,
  ChevronRight,
  Sliders,
  ExternalLink,
  Crown,
  Users,
} from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { AvatarWithFrame } from '../components/AvatarWithFrame';
import { OFFICIAL_FRAMES, STORE_ITEMS } from '../data/seedData';

interface ProfileViewProps {
  onOpenWallet: () => void;
  onOpenOfficialFrames: () => void;
  onOpenAdminPanel: () => void;
  onOpenRankings: () => void;
}

export const ProfileView: React.FC<ProfileViewProps> = ({
  onOpenWallet,
  onOpenOfficialFrames,
  onOpenAdminPanel,
  onOpenRankings,
}) => {
  const { currentUser, allUsers, switchUser, claimDailyTaskReward } = useAura();

  const [claimNotice, setClaimNotice] = useState<string | null>(null);

  if (!currentUser) return null;

  const officialFrame = OFFICIAL_FRAMES.find((f) => f.id === currentUser.equippedFrameId);
  const equippedBubble = STORE_ITEMS.find((i) => i.id === currentUser.equippedBubbleId);
  const equippedRide = STORE_ITEMS.find((i) => i.id === currentUser.equippedRideId);

  const isSuperAdminOrAdmin =
    currentUser.role === 'Super Admin' ||
    currentUser.role === 'Admin' ||
    currentUser.role === 'Manager';

  const handleClaim = (taskId: string, reward: number) => {
    claimDailyTaskReward(taskId);
    setClaimNotice(`Claimed +${reward} Coins reward! 🪙`);
    setTimeout(() => setClaimNotice(null), 3000);
  };

  return (
    <div id="profile-view-container" className="max-w-4xl mx-auto px-4 py-4 space-y-4 pb-24">
      {/* Profile Header Card */}
      <div className="bg-[#170e30] border border-[#341e5e] rounded-3xl p-5 sm:p-6 shadow-xl relative overflow-hidden">
        {/* Background ambient accent */}
        <div className="absolute top-0 right-0 w-64 h-64 bg-gradient-to-br from-[#7c4dff]/20 to-transparent rounded-full blur-3xl pointer-events-none" />

        <div className="relative z-10 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <AvatarWithFrame
              avatarUrl={currentUser.avatarUrl}
              size={72}
              equippedFrameId={currentUser.equippedFrameId}
              vipLevel={currentUser.vipLevel}
              role={currentUser.role}
            />

            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-lg sm:text-xl font-black text-white">{currentUser.username}</h1>
                <span className="px-2 py-0.5 rounded-full bg-gradient-to-r from-amber-500 to-rose-500 text-black text-[10px] font-black">
                  VIP {currentUser.vipLevel}
                </span>
              </div>

              <p className="text-xs text-slate-400 font-mono mt-0.5">
                ID: {currentUser.id} • {currentUser.country}
              </p>

              <div className="flex items-center gap-2 mt-2">
                <span className="px-2.5 py-0.5 rounded-full bg-[#7c4dff]/30 text-cyan-300 text-xs font-bold border border-[#7c4dff]/50">
                  {currentUser.role}
                </span>
                {officialFrame && (
                  <span
                    className="px-2.5 py-0.5 rounded-full text-[10px] font-black text-black"
                    style={{ backgroundColor: officialFrame.primaryColor }}
                  >
                    {officialFrame.badgeLabel}
                  </span>
                )}
              </div>
            </div>
          </div>

          {/* Persona Switcher for easy testing / evaluation */}
          <div className="flex flex-col sm:items-end gap-1.5 pt-2 sm:pt-0 border-t sm:border-t-0 border-[#2b174a]">
            <span className="text-[11px] font-bold text-slate-400">Switch Account Persona:</span>
            <select
              id="switch-account-persona-select"
              value={currentUser.id}
              onChange={(e) => switchUser(e.target.value)}
              className="bg-[#120a24] border border-[#3d226a] text-cyan-300 font-semibold rounded-xl px-3 py-1.5 text-xs focus:outline-hidden"
            >
              {allUsers.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.username} ({u.role})
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Bio */}
        <p className="text-xs text-slate-300 italic mt-3 pt-3 border-t border-[#29174d]">
          &quot;{currentUser.bio}&quot;
        </p>
      </div>

      {/* Wallet Balances Card */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        <div
          onClick={onOpenWallet}
          className="p-3.5 rounded-2xl bg-gradient-to-br from-amber-500/20 to-[#190e33] border border-amber-500/30 cursor-pointer hover:scale-[1.02] transition-transform"
        >
          <div className="flex items-center justify-between text-amber-300 text-xs font-bold">
            <span className="flex items-center gap-1">
              <Coins className="w-3.5 h-3.5" />
              <span>Coins</span>
            </span>
            <span className="text-[10px] underline">Top Up</span>
          </div>
          <div className="text-xl font-black text-white mt-1">
            {currentUser.coins.toLocaleString()}
          </div>
          <span className="text-[10px] text-amber-400">Virtual Currency</span>
        </div>

        <div
          onClick={onOpenWallet}
          className="p-3.5 rounded-2xl bg-gradient-to-br from-cyan-500/20 to-[#190e33] border border-cyan-500/30 cursor-pointer hover:scale-[1.02] transition-transform"
        >
          <div className="flex items-center justify-between text-cyan-300 text-xs font-bold">
            <span className="flex items-center gap-1">
              <Gem className="w-3.5 h-3.5" />
              <span>Diamonds</span>
            </span>
            <span className="text-[10px] underline">Convert</span>
          </div>
          <div className="text-xl font-black text-white mt-1">
            {currentUser.diamonds.toLocaleString()}
          </div>
          <span className="text-[10px] text-cyan-400">Creator Earnings</span>
        </div>

        <div
          onClick={onOpenRankings}
          className="p-3.5 rounded-2xl bg-[#190e33] border border-[#321d59] cursor-pointer hover:scale-[1.02] transition-transform"
        >
          <div className="flex items-center justify-between text-rose-300 text-xs font-bold">
            <span>Charm Level</span>
            <Award className="w-3.5 h-3.5 text-rose-400" />
          </div>
          <div className="text-xl font-black text-rose-300 mt-1">
            Lv.{currentUser.charmLevel}
          </div>
          <span className="text-[10px] text-slate-400">Received Gifts</span>
        </div>

        <div
          onClick={onOpenRankings}
          className="p-3.5 rounded-2xl bg-[#190e33] border border-[#321d59] cursor-pointer hover:scale-[1.02] transition-transform"
        >
          <div className="flex items-center justify-between text-amber-300 text-xs font-bold">
            <span>Rich Level</span>
            <Crown className="w-3.5 h-3.5 text-amber-400" />
          </div>
          <div className="text-xl font-black text-amber-300 mt-1">
            Lv.{currentUser.richLevel}
          </div>
          <span className="text-[10px] text-slate-400">Spending Prestige</span>
        </div>
      </div>

      {/* Claim Notification */}
      {claimNotice && (
        <div className="p-3 rounded-2xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{claimNotice}</span>
        </div>
      )}

      {/* Management & Authority Shortcuts (Official 1 Frames & Admin Panel) */}
      <div className="space-y-2">
        <h3 className="text-xs font-extrabold text-white uppercase tracking-wider">
          Authority & System Portals
        </h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          {/* Official 1 Frame Management */}
          <div
            id="open-official-frames-shortcut"
            onClick={onOpenOfficialFrames}
            className="p-4 rounded-3xl bg-gradient-to-br from-[#1d1039] to-[#251347] border border-[#442875] hover:border-[#00e5ff] cursor-pointer transition-all hover:scale-[1.01] flex items-center justify-between group shadow-lg"
          >
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-gradient-to-tr from-[#00e5ff] to-[#7c4dff] flex items-center justify-center text-white shadow">
                <Sliders className="w-5 h-5" />
              </div>
              <div>
                <h4 className="text-xs font-extrabold text-white group-hover:text-[#00e5ff] transition-colors flex items-center gap-1.5">
                  <span>Official 1 Frame Management</span>
                </h4>
                <p className="text-[11px] text-slate-400">
                  Grant & revoke 14 official frames with conflict safety
                </p>
              </div>
            </div>
            <ChevronRight className="w-5 h-5 text-slate-400 group-hover:text-white group-hover:translate-x-0.5 transition-all" />
          </div>

          {/* Executive Admin Panel */}
          <div
            id="open-admin-panel-shortcut"
            onClick={onOpenAdminPanel}
            className="p-4 rounded-3xl bg-gradient-to-br from-[#1d1039] to-[#251347] border border-[#442875] hover:border-rose-400 cursor-pointer transition-all hover:scale-[1.01] flex items-center justify-between group shadow-lg"
          >
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-gradient-to-tr from-rose-500 to-[#7c4dff] flex items-center justify-center text-white shadow">
                <Shield className="w-5 h-5" />
              </div>
              <div>
                <h4 className="text-xs font-extrabold text-white group-hover:text-rose-300 transition-colors flex items-center gap-1.5">
                  <span>Executive Admin & Security</span>
                </h4>
                <p className="text-[11px] text-slate-400">
                  Manage roles, audit logs, and employee link users
                </p>
              </div>
            </div>
            <ChevronRight className="w-5 h-5 text-slate-400 group-hover:text-white group-hover:translate-x-0.5 transition-all" />
          </div>
        </div>
      </div>

      {/* Daily Tasks & Free Coin Rewards */}
      <div className="bg-[#170e30] border border-[#341e5e] rounded-3xl p-4 sm:p-5 space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Gift className="w-4 h-4 text-amber-400" />
            <h3 className="text-xs font-bold text-white uppercase tracking-wider">
              Daily Quests & Task Rewards
            </h3>
          </div>
          <span className="text-[10px] text-slate-400">Refreshes every 24h</span>
        </div>

        <div className="space-y-2">
          {currentUser.dailyTasks.map((task) => (
            <div
              key={task.id}
              className="p-3 rounded-2xl bg-[#120a24] border border-[#2c184c] flex items-center justify-between gap-3"
            >
              <div>
                <h4 className="text-xs font-bold text-white">{task.title}</h4>
                <span className="text-[11px] text-amber-300 font-semibold">
                  +{task.rewardCoins} Coins 🪙
                </span>
              </div>

              {task.isClaimed ? (
                <span className="px-3 py-1.5 rounded-xl bg-slate-800 text-slate-400 text-xs font-bold">
                  Claimed ✓
                </span>
              ) : task.isCompleted ? (
                <button
                  id={`claim-task-${task.id}`}
                  onClick={() => handleClaim(task.id, task.rewardCoins)}
                  className="px-3.5 py-1.5 rounded-xl bg-gradient-to-r from-amber-500 to-rose-500 text-black text-xs font-extrabold shadow animate-pulse hover:opacity-95"
                >
                  Claim Reward
                </button>
              ) : (
                <span className="px-3 py-1.5 rounded-xl bg-[#20113f] text-slate-400 text-xs font-semibold">
                  In Progress
                </span>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Equipped Assets Display */}
      <div className="bg-[#170e30] border border-[#341e5e] rounded-3xl p-4 sm:p-5 space-y-3">
        <h3 className="text-xs font-bold text-white uppercase tracking-wider">
          Currently Equipped Assets
        </h3>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
          <div className="p-3 rounded-2xl bg-[#120a24] border border-[#2b174a]">
            <span className="text-slate-400 block text-[10px] font-semibold">Frame:</span>
            <span className="font-extrabold text-cyan-300 mt-1 block">
              {officialFrame?.name || 'Standard Frame'}
            </span>
          </div>
          <div className="p-3 rounded-2xl bg-[#120a24] border border-[#2b174a]">
            <span className="text-slate-400 block text-[10px] font-semibold">Chat Bubble:</span>
            <span className="font-extrabold text-purple-300 mt-1 block">
              {equippedBubble?.name || 'Classic Purple'}
            </span>
          </div>
          <div className="p-3 rounded-2xl bg-[#120a24] border border-[#2b174a]">
            <span className="text-slate-400 block text-[10px] font-semibold">Entrance Ride:</span>
            <span className="font-extrabold text-rose-300 mt-1 block">
              {equippedRide?.name || 'Walk On Foot'}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
