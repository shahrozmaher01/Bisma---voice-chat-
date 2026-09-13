import React from 'react';
import {
  X,
  UserCheck,
  UserPlus,
  Gift,
  Coins,
  Gem,
  Award,
  Shield,
  MapPin,
  Calendar,
  MessageSquare,
} from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { AvatarWithFrame } from './AvatarWithFrame';
import { OFFICIAL_FRAMES } from '../data/seedData';

interface UserProfileModalProps {
  userId: string | null;
  onClose: () => void;
  onDirectMessage?: (user: { id: string; name: string }) => void;
}

export const UserProfileModal: React.FC<UserProfileModalProps> = ({
  userId,
  onClose,
  onDirectMessage,
}) => {
  const { allUsers, currentUser, followUser } = useAura();

  if (!userId) return null;

  const targetUser = allUsers.find((u) => u.id === userId);
  if (!targetUser) return null;

  const isSelf = currentUser?.id === targetUser.id;
  const officialFrame = OFFICIAL_FRAMES.find((f) => f.id === targetUser.equippedFrameId);

  return (
    <div
      id="user-profile-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-3 sm:p-5 animate-in fade-in duration-150"
      onClick={onClose}
    >
      <div
        id="user-profile-modal"
        className="w-full max-w-sm bg-[#130a26] border border-[#442875] rounded-3xl p-5 shadow-2xl flex flex-col items-center text-center gap-3 relative"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          id="close-user-profile-btn"
          onClick={onClose}
          className="absolute top-4 right-4 p-1.5 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Avatar with Frame */}
        <AvatarWithFrame
          avatarUrl={targetUser.avatarUrl}
          size={72}
          equippedFrameId={targetUser.equippedFrameId}
          vipLevel={targetUser.vipLevel}
          role={targetUser.role}
        />

        {/* User identification */}
        <div>
          <h3 className="text-base font-extrabold text-white flex items-center justify-center gap-1.5">
            <span>{targetUser.username}</span>
            {targetUser.gender === 'Female' ? '♀️' : targetUser.gender === 'Male' ? '♂️' : ''}
          </h3>
          <p className="text-xs text-slate-400 font-mono mt-0.5">ID: {targetUser.id}</p>
          <div className="flex items-center justify-center gap-2 mt-1.5">
            <span className="px-2 py-0.5 rounded-full bg-[#7c4dff]/30 text-[#00e5ff] text-[10px] font-extrabold border border-[#7c4dff]/50">
              {targetUser.role}
            </span>
            {officialFrame && (
              <span
                className="px-2 py-0.5 rounded-full text-[10px] font-black text-black"
                style={{ backgroundColor: officialFrame.primaryColor }}
              >
                {officialFrame.badgeLabel}
              </span>
            )}
          </div>
        </div>

        {/* Bio */}
        <p className="text-xs text-slate-300 italic px-2 max-w-xs">{targetUser.bio}</p>

        {/* Details chips */}
        <div className="flex items-center gap-3 text-[11px] text-slate-400">
          <span className="flex items-center gap-1">
            <MapPin className="w-3.5 h-3.5 text-rose-400" />
            {targetUser.country}
          </span>
          <span>•</span>
          <span className="flex items-center gap-1">
            <Calendar className="w-3.5 h-3.5 text-amber-400" />
            Joined 2025
          </span>
        </div>

        {/* Stats Grid */}
        <div className="w-full grid grid-cols-4 gap-1.5 p-2 bg-[#1a0f33] rounded-2xl border border-[#331c5b] text-center">
          <div>
            <div className="text-xs font-black text-white">{targetUser.followersCount}</div>
            <div className="text-[10px] text-slate-400">Followers</div>
          </div>
          <div>
            <div className="text-xs font-black text-white">{targetUser.followingCount}</div>
            <div className="text-[10px] text-slate-400">Following</div>
          </div>
          <div>
            <div className="text-xs font-black text-amber-400">Lv.{targetUser.richLevel}</div>
            <div className="text-[10px] text-slate-400">Rich</div>
          </div>
          <div>
            <div className="text-xs font-black text-rose-400">Lv.{targetUser.charmLevel}</div>
            <div className="text-[10px] text-slate-400">Charm</div>
          </div>
        </div>

        {/* Agency Info if any */}
        {targetUser.agencyName && (
          <div className="w-full p-2 rounded-xl bg-[#20113f] border border-[#3e236f] text-xs flex items-center justify-between text-left">
            <div className="flex items-center gap-2">
              <span className="text-lg">💎</span>
              <div>
                <span className="font-bold text-white block">{targetUser.agencyName}</span>
                <span className="text-[10px] text-slate-400">Official Certified Agency</span>
              </div>
            </div>
            <span className="text-[10px] font-bold text-cyan-300">ACTIVE</span>
          </div>
        )}

        {/* Action buttons */}
        {!isSelf && (
          <div className="w-full flex gap-2 mt-2">
            <button
              id="follow-user-btn"
              onClick={() => followUser(targetUser.id)}
              className="flex-1 py-2 rounded-xl bg-gradient-to-r from-[#ff2a85] to-[#7c4dff] hover:opacity-90 text-white font-bold text-xs shadow flex items-center justify-center gap-1.5 active:scale-95 transition-all"
            >
              <UserPlus className="w-4 h-4" />
              <span>Follow</span>
            </button>
            <button
              id="message-user-btn"
              onClick={() => {
                if (onDirectMessage) {
                  onDirectMessage({ id: targetUser.id, name: targetUser.username });
                }
                onClose();
              }}
              className="px-4 py-2 rounded-xl bg-[#20123f] hover:bg-[#2e1a5a] text-[#00e5ff] border border-[#442875] font-bold text-xs flex items-center justify-center gap-1.5 transition-colors"
            >
              <MessageSquare className="w-4 h-4" />
              <span>Chat</span>
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
