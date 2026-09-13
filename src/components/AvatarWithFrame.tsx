import React from 'react';
import { OFFICIAL_FRAMES } from '../data/seedData';
import { AdminRoleType } from '../types';

interface AvatarWithFrameProps {
  avatarUrl?: string;
  size?: number; // in pixels, default 48
  equippedFrameId?: string;
  vipLevel?: number;
  role?: AdminRoleType;
  isSpeaking?: boolean;
  isMuted?: boolean;
  className?: string;
  onClick?: () => void;
}

export const AvatarWithFrame: React.FC<AvatarWithFrameProps> = ({
  avatarUrl = 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150',
  size = 48,
  equippedFrameId,
  vipLevel = 0,
  role,
  isSpeaking = false,
  isMuted = false,
  className = '',
  onClick,
}) => {
  const officialFrame = OFFICIAL_FRAMES.find((f) => f.id === equippedFrameId);

  // Compute frame border styles based on official frame or standard store frame
  let frameBorder = 'border-transparent';
  let frameGlow = '';
  let badgeEmoji = officialFrame?.iconEmoji || '';

  if (officialFrame) {
    frameBorder = `border-[2.5px]`;
    frameGlow = `shadow-[0_0_12px_${officialFrame.primaryColor}88]`;
  } else if (equippedFrameId === 'frame_golden_dragon') {
    frameBorder = 'border-[2.5px] border-amber-400';
    frameGlow = 'shadow-[0_0_10px_rgba(251,191,36,0.6)]';
    badgeEmoji = '🐲';
  } else if (equippedFrameId === 'frame_neon_circle') {
    frameBorder = 'border-[2px] border-cyan-400';
    frameGlow = 'shadow-[0_0_8px_rgba(6,182,212,0.5)]';
  } else if (equippedFrameId === 'frame_cosmic_galaxy') {
    frameBorder = 'border-[2.5px] border-purple-500';
    frameGlow = 'shadow-[0_0_10px_rgba(168,85,247,0.6)]';
    badgeEmoji = '🌌';
  }

  return (
    <div
      id={`avatar-${Math.random().toString(36).substring(2, 7)}`}
      onClick={onClick}
      className={`relative inline-flex items-center justify-center flex-shrink-0 select-none ${
        onClick ? 'cursor-pointer' : ''
      } ${className}`}
      style={{ width: size + 8, height: size + 8 }}
    >
      {/* Speaking Pulse Ring */}
      {isSpeaking && (
        <div
          className="absolute inset-0 rounded-full border-2 border-emerald-400 speaking-pulse z-0 pointer-events-none"
          style={{ width: size + 8, height: size + 8 }}
        />
      )}

      {/* Frame Container */}
      <div
        className={`relative rounded-full p-[2px] z-10 transition-transform duration-200 ${frameBorder} ${frameGlow}`}
        style={{
          borderColor: officialFrame ? officialFrame.primaryColor : undefined,
          background: officialFrame
            ? `linear-gradient(135deg, ${officialFrame.primaryColor}22, ${officialFrame.secondaryColor}22)`
            : undefined,
        }}
      >
        <img
          src={avatarUrl}
          alt="Avatar"
          className="rounded-full object-cover shadow-inner"
          style={{ width: size, height: size }}
          referrerPolicy="no-referrer"
          loading="lazy"
        />

        {/* Official Frame Crown / Crest Badge */}
        {badgeEmoji && (
          <span
            className="absolute -top-2.5 -right-1 text-xs drop-shadow-md select-none pointer-events-none"
            title={officialFrame?.name || 'Equipped Frame'}
          >
            {badgeEmoji}
          </span>
        )}

        {/* VIP Level Tag */}
        {vipLevel > 0 && (
          <span
            className="absolute -bottom-1 -left-1 px-1 py-[0.5px] rounded-full text-[9px] font-extrabold text-white tracking-tight shadow-sm pointer-events-none"
            style={{
              background:
                vipLevel >= 6
                  ? 'linear-gradient(45deg, #f59e0b, #ef4444)'
                  : vipLevel >= 4
                  ? 'linear-gradient(45deg, #8b5cf6, #ec4899)'
                  : 'linear-gradient(45deg, #3b82f6, #06b6d4)',
            }}
          >
            V{vipLevel}
          </span>
        )}

        {/* Microphone status icon in voice room */}
        {isMuted && (
          <span className="absolute -bottom-1 -right-1 w-4 h-4 rounded-full bg-rose-600 text-white flex items-center justify-center text-[9px] shadow border border-[#0b0816]">
            🔇
          </span>
        )}
      </div>
    </div>
  );
};
