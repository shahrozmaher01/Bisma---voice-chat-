import React, { useState, useRef, useEffect } from 'react';
import {
  X,
  Mic,
  MicOff,
  Send,
  Gift,
  Volume2,
  Lock,
  Unlock,
  Users,
  Shield,
  Trophy,
  Sparkles,
  Share2,
  Coins,
  Settings,
  MoreVertical,
  LogOut,
} from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { AvatarWithFrame } from './AvatarWithFrame';
import { GiftBottomSheet } from './GiftBottomSheet';
import { LuckyBagModal } from './LuckyBagModal';
import { SoundboardModal } from './SoundboardModal';
import { HostToolsModal } from './HostToolsModal';
import { RoomSeat, User } from '../types';

interface VoiceRoomModalProps {
  onOpenUserProfile: (userId: string) => void;
}

export const VoiceRoomModal: React.FC<VoiceRoomModalProps> = ({ onOpenUserProfile }) => {
  const {
    activeRoom,
    activeRoomSeats,
    roomMessages,
    leaveRoom,
    hostCloseRoom,
    takeSeat,
    leaveSeat,
    toggleMic,
    toggleSeatLock,
    hostTakeDownUser,
    sendRoomMessage,
    currentUser,
    floatingGiftAlert,
    activeLuckyBag,
  } = useAura();

  const [messageInput, setMessageInput] = useState('');
  const [showGiftSheet, setShowGiftSheet] = useState(false);
  const [showLuckyBagModal, setShowLuckyBagModal] = useState(false);
  const [showSoundboard, setShowSoundboard] = useState(false);
  const [showHostTools, setShowHostTools] = useState(false);
  const [selectedSeat, setSelectedSeat] = useState<RoomSeat | null>(null);

  const chatEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [roomMessages]);

  if (!activeRoom) return null;

  const isHostOrAdmin =
    currentUser?.id === activeRoom.ownerId ||
    currentUser?.role === 'Super Admin' ||
    currentUser?.role === 'Admin' ||
    activeRoom.adminUserIds.includes(currentUser?.id || '');

  const userCurrentSeat = activeRoomSeats.find((s) => s.userId === currentUser?.id);
  const isUserOnMic = !!userCurrentSeat;

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!messageInput.trim()) return;
    sendRoomMessage(messageInput);
    setMessageInput('');
  };

  const handleSeatClick = (seat: RoomSeat) => {
    if (seat.isLocked) {
      if (isHostOrAdmin) {
        toggleSeatLock(seat.seatIndex);
      } else {
        alert('This seat is locked by host.');
      }
      return;
    }

    if (seat.userId) {
      // Occupied seat: open action dialog or user profile
      setSelectedSeat(seat);
      return;
    }

    // Empty seat: take it
    takeSeat(seat.seatIndex);
  };

  return (
    <div
      id="voice-room-fullscreen-container"
      className="fixed inset-0 z-50 bg-[#0c071a] flex flex-col justify-between overflow-hidden animate-in fade-in duration-200"
    >
      {/* Background Wallpaper with gradient overlay */}
      <div
        className="absolute inset-0 bg-cover bg-center pointer-events-none opacity-25"
        style={{
          backgroundImage: `url(${activeRoom.wallpaperUrl || activeRoom.coverUrl})`,
        }}
      />
      <div className="absolute inset-0 bg-gradient-to-b from-[#0c071a]/85 via-[#0c071a]/70 to-[#0c071a] pointer-events-none" />

      {/* Floating Global Gift Announcement Banner */}
      {floatingGiftAlert && (
        <div className="absolute top-16 left-4 right-4 z-40 flex justify-center pointer-events-none">
          <div className="gift-banner-animate flex items-center gap-3 px-4 py-2 rounded-full bg-gradient-to-r from-[#ff2a85] via-[#7c4dff] to-[#00e5ff] text-white shadow-[0_0_25px_rgba(255,42,133,0.7)] border border-white/40">
            <span className="text-2xl animate-bounce">{floatingGiftAlert.gift.iconEmoji}</span>
            <div className="text-xs font-extrabold tracking-wide">
              <span className="text-amber-300 font-bold">{floatingGiftAlert.sender}</span>
              <span> sent </span>
              <span className="text-white underline decoration-amber-300">{floatingGiftAlert.gift.name}</span>
              <span> to </span>
              <span className="text-cyan-200 font-bold">{floatingGiftAlert.target}</span>!
            </div>
          </div>
        </div>
      )}

      {/* ROOM TOP HEADER */}
      <div className="relative z-10 px-4 pt-3 pb-2 border-b border-[#251545]/60 flex items-center justify-between gap-2">
        {/* Left: Room Cover, Title, Owner info */}
        <div className="flex items-center gap-2.5 min-w-0">
          <img
            src={activeRoom.coverUrl}
            alt={activeRoom.title}
            className="w-10 h-10 rounded-2xl object-cover border border-[#442875] shadow-md flex-shrink-0"
            referrerPolicy="no-referrer"
          />
          <div className="min-w-0">
            <div className="flex items-center gap-1.5">
              <h2 className="text-sm font-extrabold text-white truncate max-w-[140px] sm:max-w-[200px]">
                {activeRoom.title}
              </h2>
              {activeRoom.isFeatured && (
                <span className="px-1.5 py-[1px] rounded bg-gradient-to-r from-amber-500 to-rose-500 text-black text-[9px] font-extrabold">
                  TOP
                </span>
              )}
            </div>
            <div className="flex items-center gap-2 text-[11px] text-slate-400">
              <span className="flex items-center gap-1 text-emerald-400 font-bold">
                <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
                LIVE
              </span>
              <span>•</span>
              <span className="font-mono text-cyan-300">ID: {activeRoom.id.replace('room_', '')}</span>
              <span>•</span>
              <span className="flex items-center gap-1 text-emerald-400 font-semibold">
                <Users className="w-3 h-3" />
                {activeRoom.onlineCount}
              </span>
            </div>
          </div>
        </div>

        {/* Right Header Actions */}
        <div className="flex items-center gap-2">
          {/* Active Lucky Bag Button Pill */}
          {activeLuckyBag && (
            <button
              id="header-lucky-bag-pill"
              onClick={() => setShowLuckyBagModal(true)}
              className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-rose-600/90 hover:bg-rose-500 text-white text-xs font-bold shadow-[0_0_12px_rgba(244,63,94,0.6)] animate-pulse"
            >
              <span>🧧</span>
              <span>Claim</span>
            </button>
          )}

          {/* Host Tools Button if authorized */}
          {isHostOrAdmin && (
            <button
              id="header-host-tools-btn"
              onClick={() => setShowHostTools(true)}
              className="p-2 rounded-full bg-[#20123f] border border-[#3e246b] text-amber-400 hover:border-amber-400 transition-colors"
              title="Host Tools"
            >
              <Settings className="w-4 h-4" />
            </button>
          )}

          {/* Host End / Close Room button */}
          {isHostOrAdmin && (
            <button
              id="header-end-room-btn"
              onClick={() => {
                if (window.confirm('End and close this room session for everyone? The room will no longer appear as active.')) {
                  hostCloseRoom(activeRoom.id);
                }
              }}
              className="p-2 rounded-full bg-rose-500/20 border border-rose-500/40 text-rose-300 hover:bg-rose-500 hover:text-white transition-colors"
              title="Close Room Session (End for all)"
            >
              <LogOut className="w-4 h-4" />
            </button>
          )}

          {/* Minimize / Leave Room */}
          <button
            id="header-leave-room-btn"
            onClick={leaveRoom}
            className="p-2 rounded-full bg-[#20123f] border border-[#3e246b] text-slate-300 hover:text-rose-400 hover:border-rose-500 transition-colors"
            title="Leave / Minimize Room"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* ROOM MAIN BODY */}
      <div className="relative z-10 flex-1 flex flex-col justify-between overflow-hidden px-3 sm:px-6 py-2">
        {/* Room Announcement ticker */}
        <div className="w-full bg-[#180e33]/80 border border-[#321d5c] rounded-xl px-3 py-1 text-[11px] text-slate-300 flex items-center justify-between gap-2 mb-2">
          <span className="text-[#00e5ff] font-bold flex-shrink-0">📢 Notice:</span>
          <span className="truncate flex-1">{activeRoom.announcement}</span>
          <span className="text-[10px] text-slate-500 flex-shrink-0">{activeRoom.country}</span>
        </div>

        {/* 8-SEAT INTERACTIVE AUDIO STAGE */}
        <div id="voice-seats-stage" className="w-full max-w-xl mx-auto my-auto py-2">
          {/* Top Row: Seat 0 (Host), Seat 1, Seat 2, Seat 3 */}
          <div className="grid grid-cols-4 gap-2 sm:gap-4 mb-3">
            {activeRoomSeats.slice(0, 4).map((seat) => renderSeatItem(seat))}
          </div>

          {/* Bottom Row: Seat 4, Seat 5, Seat 6, Seat 7 (VIP Golden Seat 8) */}
          <div className="grid grid-cols-4 gap-2 sm:gap-4">
            {activeRoomSeats.slice(4, 8).map((seat) => renderSeatItem(seat))}
          </div>
        </div>

        {/* ROOM CHAT STREAM */}
        <div className="w-full max-w-xl mx-auto h-40 sm:h-52 flex flex-col justify-end overflow-hidden mt-2">
          <div
            id="room-chat-scroll"
            className="overflow-y-auto space-y-1.5 pr-2 select-text text-xs"
          >
            {roomMessages.map((msg) => (
              <div
                key={msg.id}
                className="flex items-start gap-1.5 leading-relaxed break-words"
              >
                {msg.senderVip > 0 && (
                  <span className="px-1 py-[0.5px] rounded text-[9px] font-extrabold bg-gradient-to-r from-amber-500 to-rose-500 text-black flex-shrink-0 mt-0.5">
                    V{msg.senderVip}
                  </span>
                )}
                {msg.senderRole && msg.senderRole !== 'User' && (
                  <span className="px-1 py-[0.5px] rounded text-[9px] font-bold bg-[#7c4dff]/40 text-[#00e5ff] border border-[#7c4dff]/50 flex-shrink-0 mt-0.5">
                    {msg.senderRole}
                  </span>
                )}
                <span
                  onClick={() => msg.senderId !== 'system' && onOpenUserProfile(msg.senderId)}
                  className="font-bold text-slate-300 hover:text-white cursor-pointer flex-shrink-0"
                >
                  {msg.senderName}:
                </span>
                <span
                  className={`${
                    msg.senderId === 'system'
                      ? 'text-amber-300 font-semibold'
                      : msg.giftName
                      ? 'text-[#ff2a85] font-bold'
                      : 'text-slate-100'
                  }`}
                >
                  {msg.content}
                  {msg.giftIcon && <span className="ml-1 text-sm">{msg.giftIcon}</span>}
                </span>
              </div>
            ))}
            <div ref={chatEndRef} />
          </div>
        </div>
      </div>

      {/* ROOM BOTTOM ACTION TOOLBAR */}
      <div className="relative z-20 px-3 sm:px-6 py-2.5 bg-[#120a26]/95 border-t border-[#29174b] backdrop-blur-md">
        <div className="max-w-xl mx-auto flex items-center justify-between gap-2">
          {/* Mic Toggle Button */}
          {isUserOnMic ? (
            <button
              id="room-mic-toggle-btn"
              onClick={toggleMic}
              className={`p-3 rounded-full font-bold shadow-lg transition-all ${
                userCurrentSeat?.isMuted
                  ? 'bg-rose-600 hover:bg-rose-500 text-white shadow-rose-900/50'
                  : 'bg-emerald-500 hover:bg-emerald-400 text-black shadow-emerald-900/50 animate-pulse'
              }`}
              title={userCurrentSeat?.isMuted ? 'Unmute Microphone' : 'Mute Microphone'}
            >
              {userCurrentSeat?.isMuted ? (
                <MicOff className="w-5 h-5" />
              ) : (
                <Mic className="w-5 h-5" />
              )}
            </button>
          ) : (
            /* Button to quickly take any open seat */
            <button
              id="room-join-mic-btn"
              onClick={() => {
                const firstOpen = activeRoomSeats.find((s) => !s.userId && !s.isLocked);
                if (firstOpen) {
                  takeSeat(firstOpen.seatIndex);
                } else {
                  alert('All seats are currently occupied or locked.');
                }
              }}
              className="p-3 rounded-full bg-[#20123f] border border-[#442875] text-slate-300 hover:text-white hover:border-[#7c4dff] transition-all"
              title="Join Speaker Seat"
            >
              <Mic className="w-5 h-5" />
            </button>
          )}

          {/* Chat Message Input Form */}
          <form
            onSubmit={handleSendMessage}
            className="flex-1 flex items-center bg-[#1b0f36] border border-[#392163] focus-within:border-[#7c4dff] rounded-full px-3 py-1.5 transition-colors"
          >
            <input
              id="room-chat-input"
              type="text"
              value={messageInput}
              onChange={(e) => setMessageInput(e.target.value)}
              placeholder="Send a chat message..."
              className="w-full bg-transparent text-xs text-white placeholder-slate-500 focus:outline-hidden"
            />
            <button
              type="submit"
              disabled={!messageInput.trim()}
              className="p-1 text-[#00e5ff] hover:text-white disabled:text-slate-600 disabled:cursor-not-allowed transition-colors"
            >
              <Send className="w-4 h-4" />
            </button>
          </form>

          {/* Soundboard Button */}
          <button
            id="room-soundboard-btn"
            onClick={() => setShowSoundboard(true)}
            className="p-2.5 rounded-full bg-[#1e113b] border border-[#3b2368] text-[#00e5ff] hover:bg-[#28164f] transition-all"
            title="Acoustic Soundboard"
          >
            <Volume2 className="w-5 h-5" />
          </button>

          {/* Lucky Bag Drop Button */}
          <button
            id="room-lucky-bag-btn"
            onClick={() => setShowLuckyBagModal(true)}
            className="p-2.5 rounded-full bg-[#1e113b] border border-[#3b2368] text-rose-400 hover:bg-[#28164f] transition-all"
            title="Drop Lucky Coin Bag"
          >
            <span className="text-lg">🧧</span>
          </button>

          {/* Send Virtual Gifts Sheet Button */}
          <button
            id="room-gifts-btn"
            onClick={() => setShowGiftSheet(true)}
            className="p-2.5 rounded-full bg-gradient-to-r from-[#ff2a85] to-[#7c4dff] text-white shadow-[0_0_15px_rgba(255,42,133,0.5)] hover:scale-105 active:scale-95 transition-transform"
            title="Send Virtual Gifts"
          >
            <Gift className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Seat Details Modal when a seat is clicked */}
      {selectedSeat && (
        <div
          id="seat-actions-modal-backdrop"
          className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4"
          onClick={() => setSelectedSeat(null)}
        >
          <div
            id="seat-actions-modal"
            className="w-full max-w-xs bg-[#160c2b] border border-[#442875] rounded-3xl p-5 shadow-2xl flex flex-col items-center text-center gap-3 relative"
            onClick={(e) => e.stopPropagation()}
          >
            <button
              onClick={() => setSelectedSeat(null)}
              className="absolute top-4 right-4 p-1 rounded-full text-slate-400 hover:text-white"
            >
              <X className="w-4 h-4" />
            </button>

            <AvatarWithFrame
              avatarUrl={selectedSeat.avatarUrl}
              size={64}
              equippedFrameId={selectedSeat.equippedFrameId}
              vipLevel={selectedSeat.vipLevel}
              role={selectedSeat.role}
            />

            <div>
              <h3 className="text-sm font-bold text-white">{selectedSeat.username}</h3>
              <p className="text-xs text-[#00e5ff] font-semibold">
                {selectedSeat.seatIndex === 0 ? 'Room Host 👑' : `Seat #${selectedSeat.seatIndex + 1}`}
              </p>
              {selectedSeat.role && (
                <span className="inline-block mt-1 px-2 py-0.5 rounded-full bg-[#7c4dff]/30 text-white text-[10px] font-bold">
                  {selectedSeat.role}
                </span>
              )}
            </div>

            <div className="w-full flex flex-col gap-2 mt-2">
              {/* If clicked own seat */}
              {selectedSeat.userId === currentUser?.id ? (
                <button
                  id="leave-mic-seat-btn"
                  onClick={() => {
                    leaveSeat();
                    setSelectedSeat(null);
                  }}
                  className="w-full py-2 rounded-xl bg-rose-500/20 hover:bg-rose-500/30 text-rose-300 font-bold text-xs border border-rose-500/40 transition-colors"
                >
                  Step Down from Mic
                </button>
              ) : (
                <>
                  <button
                    id="view-seat-profile-btn"
                    onClick={() => {
                      if (selectedSeat.userId) {
                        onOpenUserProfile(selectedSeat.userId);
                      }
                      setSelectedSeat(null);
                    }}
                    className="w-full py-2 rounded-xl bg-[#231444] hover:bg-[#301a5e] text-white font-bold text-xs border border-[#442875] transition-colors"
                  >
                    View User Profile
                  </button>

                  <button
                    id="send-gift-to-seat-btn"
                    onClick={() => {
                      setShowGiftSheet(true);
                      setSelectedSeat(null);
                    }}
                    className="w-full py-2 rounded-xl bg-gradient-to-r from-[#ff2a85] to-[#7c4dff] text-white font-bold text-xs shadow transition-transform active:scale-95"
                  >
                    Send Gift to {selectedSeat.username}
                  </button>

                  {/* Host Moderation: Take down speaker */}
                  {isHostOrAdmin && selectedSeat.seatIndex !== 0 && (
                    <button
                      id="host-takedown-speaker-btn"
                      onClick={() => {
                        hostTakeDownUser(selectedSeat.seatIndex);
                        setSelectedSeat(null);
                      }}
                      className="w-full py-1.5 rounded-xl bg-rose-500/20 hover:bg-rose-500/30 text-rose-300 font-bold text-xs border border-rose-500/30 transition-colors"
                    >
                      Move to Audience (Kick from Mic)
                    </button>
                  )}
                </>
              )}

              {/* Host lock seat toggle */}
              {isHostOrAdmin && (
                <button
                  id="toggle-seat-lock-btn"
                  onClick={() => {
                    toggleSeatLock(selectedSeat.seatIndex);
                    setSelectedSeat(null);
                  }}
                  className="w-full py-1.5 rounded-xl bg-[#1b0f36] hover:bg-[#251549] text-slate-300 font-bold text-[11px] border border-[#3b2368] transition-colors"
                >
                  {selectedSeat.isLocked ? 'Unlock Seat' : 'Lock Seat'}
                </button>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Sub-modals */}
      <GiftBottomSheet
        isOpen={showGiftSheet}
        onClose={() => setShowGiftSheet(false)}
        onSelectUser={onOpenUserProfile}
      />
      <LuckyBagModal
        isOpen={showLuckyBagModal}
        onClose={() => setShowLuckyBagModal(false)}
      />
      <SoundboardModal
        isOpen={showSoundboard}
        onClose={() => setShowSoundboard(false)}
      />
      <HostToolsModal
        isOpen={showHostTools}
        onClose={() => setShowHostTools(false)}
        roomId={activeRoom.id}
      />
    </div>
  );

  // Helper function to render an individual seat
  function renderSeatItem(seat: RoomSeat) {
    const isHostSeat = seat.seatIndex === 0;
    const isVipSeat8 = seat.seatIndex === 7;
    const isOccupied = !!seat.userId;

    return (
      <div
        key={seat.seatIndex}
        id={`room-seat-${seat.seatIndex}`}
        onClick={() => handleSeatClick(seat)}
        className="flex flex-col items-center cursor-pointer group"
      >
        <div className="relative">
          {isOccupied ? (
            <AvatarWithFrame
              avatarUrl={seat.avatarUrl}
              size={52}
              equippedFrameId={seat.equippedFrameId}
              vipLevel={seat.vipLevel}
              role={seat.role}
              isSpeaking={seat.isSpeaking}
              isMuted={seat.isMuted}
            />
          ) : (
            /* Empty or Locked Seat placeholder */
            <div
              className={`w-14 h-14 rounded-full flex flex-col items-center justify-center border transition-all ${
                seat.isLocked
                  ? 'bg-rose-950/30 border-rose-500/40 text-rose-400'
                  : isVipSeat8
                  ? 'bg-amber-950/30 border-amber-400/60 text-amber-300 shadow-[0_0_12px_rgba(251,191,36,0.3)]'
                  : 'bg-[#180e33]/70 border-[#382163] text-slate-400 hover:border-[#7c4dff] hover:text-white'
              }`}
            >
              {seat.isLocked ? (
                <Lock className="w-4 h-4" />
              ) : isVipSeat8 ? (
                <div className="flex flex-col items-center">
                  <span className="text-sm">👑</span>
                  <span className="text-[9px] font-extrabold text-amber-300">VIP 8</span>
                </div>
              ) : (
                <div className="flex flex-col items-center">
                  <Mic className="w-4 h-4 text-slate-400 group-hover:text-emerald-400 transition-colors" />
                  <span className="text-[9px] font-bold text-slate-500">
                    #{seat.seatIndex + 1}
                  </span>
                </div>
              )}
            </div>
          )}

          {/* Equalizer audio wave animation when speaking */}
          {isOccupied && seat.isSpeaking && !seat.isMuted && (
            <div className="absolute -bottom-1.5 left-1/2 -translate-x-1/2 flex items-end gap-[1.5px] px-1 py-0.5 rounded bg-black/60 z-20">
              <span className="w-1 bg-emerald-400 rounded-xs audio-bar-1" />
              <span className="w-1 bg-emerald-400 rounded-xs audio-bar-2" />
              <span className="w-1 bg-emerald-400 rounded-xs audio-bar-3" />
              <span className="w-1 bg-emerald-400 rounded-xs audio-bar-4" />
            </div>
          )}
        </div>

        {/* Seat Label: Username or Role */}
        <span className="mt-1 text-[11px] font-semibold text-slate-200 text-center truncate max-w-[72px] drop-shadow-sm">
          {isOccupied ? (
            seat.username
          ) : isHostSeat ? (
            <span className="text-amber-400 font-bold">Host</span>
          ) : isVipSeat8 ? (
            <span className="text-amber-300 font-bold">Boss 8</span>
          ) : (
            `Seat ${seat.seatIndex + 1}`
          )}
        </span>
      </div>
    );
  }
};
