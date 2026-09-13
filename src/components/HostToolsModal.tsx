import React from 'react';
import { X, ShieldAlert, Lock, Unlock, MicOff, Trash2, Power } from 'lucide-react';
import { useAura } from '../context/AuraContext';

interface HostToolsModalProps {
  isOpen: boolean;
  onClose: () => void;
  roomId: string;
}

export const HostToolsModal: React.FC<HostToolsModalProps> = ({ isOpen, onClose, roomId }) => {
  const {
    hostLockEmptySeats,
    hostUnlockAllSeats,
    hostMuteAll,
    hostClearChat,
    hostCloseRoom,
    currentUser,
  } = useAura();

  if (!isOpen) return null;

  return (
    <div
      id="host-tools-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4 animate-in fade-in duration-150"
      onClick={onClose}
    >
      <div
        id="host-tools-modal"
        className="w-full max-w-sm bg-[#160c2b] border border-[#442875] rounded-3xl p-5 shadow-2xl relative flex flex-col gap-3"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          id="close-host-tools-btn"
          onClick={onClose}
          className="absolute top-4 right-4 p-1 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="flex items-center gap-2">
          <ShieldAlert className="w-5 h-5 text-amber-400" />
          <div>
            <h3 className="text-sm font-bold text-white">Host Moderation Tools</h3>
            <p className="text-[11px] text-slate-400">Manage audio seats & room security</p>
          </div>
        </div>

        <div className="space-y-2 mt-2">
          {/* Mute All */}
          <button
            id="tool-mute-all-btn"
            onClick={() => {
              hostMuteAll();
              onClose();
            }}
            className="w-full flex items-center gap-3 p-3 rounded-2xl bg-[#20123f] hover:bg-[#2c1955] border border-[#3c246b] text-left transition-all text-xs font-semibold text-slate-200"
          >
            <div className="w-8 h-8 rounded-xl bg-rose-500/20 text-rose-400 flex items-center justify-center">
              <MicOff className="w-4 h-4" />
            </div>
            <div className="flex-1">
              <div className="text-white font-bold">Mute All Speakers</div>
              <div className="text-[10px] text-slate-400">Silence all participant microphones</div>
            </div>
          </button>

          {/* Lock Empty Seats */}
          <button
            id="tool-lock-empty-btn"
            onClick={() => {
              hostLockEmptySeats();
              onClose();
            }}
            className="w-full flex items-center gap-3 p-3 rounded-2xl bg-[#20123f] hover:bg-[#2c1955] border border-[#3c246b] text-left transition-all text-xs font-semibold text-slate-200"
          >
            <div className="w-8 h-8 rounded-xl bg-amber-500/20 text-amber-400 flex items-center justify-center">
              <Lock className="w-4 h-4" />
            </div>
            <div className="flex-1">
              <div className="text-white font-bold">Lock Empty Seats</div>
              <div className="text-[10px] text-slate-400">Prevent new users from joining mic</div>
            </div>
          </button>

          {/* Unlock All Seats */}
          <button
            id="tool-unlock-all-btn"
            onClick={() => {
              hostUnlockAllSeats();
              onClose();
            }}
            className="w-full flex items-center gap-3 p-3 rounded-2xl bg-[#20123f] hover:bg-[#2c1955] border border-[#3c246b] text-left transition-all text-xs font-semibold text-slate-200"
          >
            <div className="w-8 h-8 rounded-xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center">
              <Unlock className="w-4 h-4" />
            </div>
            <div className="flex-1">
              <div className="text-white font-bold">Unlock All Seats</div>
              <div className="text-[10px] text-slate-400">Allow members to freely take seats</div>
            </div>
          </button>

          {/* Clear Chat */}
          <button
            id="tool-clear-chat-btn"
            onClick={() => {
              hostClearChat();
              onClose();
            }}
            className="w-full flex items-center gap-3 p-3 rounded-2xl bg-[#20123f] hover:bg-[#2c1955] border border-[#3c246b] text-left transition-all text-xs font-semibold text-slate-200"
          >
            <div className="w-8 h-8 rounded-xl bg-cyan-500/20 text-cyan-400 flex items-center justify-center">
              <Trash2 className="w-4 h-4" />
            </div>
            <div className="flex-1">
              <div className="text-white font-bold">Clear Chat Stream</div>
              <div className="text-[10px] text-slate-400">Wipe previous message history</div>
            </div>
          </button>

          {/* Close Room */}
          <button
            id="tool-close-room-btn"
            onClick={() => {
              if (confirm('Are you sure you want to end this voice room session?')) {
                hostCloseRoom(roomId);
                onClose();
              }
            }}
            className="w-full flex items-center gap-3 p-3 rounded-2xl bg-rose-500/10 hover:bg-rose-500/20 border border-rose-500/30 text-left transition-all text-xs font-semibold text-rose-300 mt-2"
          >
            <div className="w-8 h-8 rounded-xl bg-rose-500 text-white flex items-center justify-center">
              <Power className="w-4 h-4" />
            </div>
            <div className="flex-1">
              <div className="text-rose-200 font-bold">End Voice Room</div>
              <div className="text-[10px] text-rose-300/70">Close audio stage and disconnect all members</div>
            </div>
          </button>
        </div>
      </div>
    </div>
  );
};
