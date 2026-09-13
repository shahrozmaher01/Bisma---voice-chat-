import React, { useState } from 'react';
import { X, Mic, Sparkles, Tag, Globe } from 'lucide-react';
import { useAura } from '../context/AuraContext';

interface CreateRoomModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const CATEGORIES = [
  'Singing & Chill 🎵',
  'Chat & Friends 💬',
  'Late Night Talks 🌙',
  'Poetry & Art 📖',
  'Gaming Lounge 🎮',
  'Global & English 🌐',
];

export const CreateRoomModal: React.FC<CreateRoomModalProps> = ({ isOpen, onClose }) => {
  const { createRoom, currentUser } = useAura();
  const [title, setTitle] = useState(`${currentUser?.username || 'Star'}'s Party Room 🎙️`);
  const [category, setCategory] = useState(CATEGORIES[0]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;
    createRoom(title.trim(), category);
    onClose();
  };

  return (
    <div
      id="create-room-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-3 sm:p-5 animate-in fade-in duration-150"
      onClick={onClose}
    >
      <div
        id="create-room-modal"
        className="w-full max-w-sm bg-[#130a26] border border-[#442875] rounded-3xl p-5 shadow-2xl flex flex-col gap-3 relative"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          id="close-create-room-btn"
          onClick={onClose}
          className="absolute top-4 right-4 p-1.5 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="flex items-center gap-2">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-[#ff2a85] to-[#7c4dff] flex items-center justify-center text-white">
            <Mic className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-sm font-extrabold text-white">Create Voice Room</h2>
            <p className="text-[11px] text-slate-400">Launch an 8-seat interactive audio stage</p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-3 mt-1">
          <div>
            <label className="text-xs font-semibold text-slate-300 mb-1 block">Room Title:</label>
            <input
              id="new-room-title-input"
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full bg-[#1b0f36] border border-[#3b2368] rounded-xl px-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-hidden focus:border-[#7c4dff]"
              required
            />
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-300 mb-1 block">Category:</label>
            <div className="grid grid-cols-2 gap-2">
              {CATEGORIES.map((cat) => (
                <button
                  key={cat}
                  type="button"
                  onClick={() => setCategory(cat)}
                  className={`p-2 rounded-xl text-xs font-bold border transition-all text-left ${
                    category === cat
                      ? 'bg-[#7c4dff] text-white border-[#9a70ff] shadow'
                      : 'bg-[#1b0f36] border-[#371e63] text-slate-300 hover:bg-[#26154c]'
                  }`}
                >
                  {cat}
                </button>
              ))}
            </div>
          </div>

          <button
            id="confirm-create-room-btn"
            type="submit"
            className="w-full py-2.5 rounded-xl bg-gradient-to-r from-[#ff2a85] to-[#7c4dff] text-white font-extrabold text-xs shadow-[0_4px_16px_rgba(255,42,133,0.4)] active:scale-95 transition-all mt-2"
          >
            Start Voice Party Now ✨
          </button>
        </form>
      </div>
    </div>
  );
};
