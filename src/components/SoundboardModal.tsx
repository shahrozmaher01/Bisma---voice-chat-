import React from 'react';
import { X, Volume2, Sparkles } from 'lucide-react';
import { useAura } from '../context/AuraContext';

interface SoundboardModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const SOUNDS = [
  { id: 'applause', name: 'Applause 👏', icon: '👏', color: 'from-blue-600 to-indigo-600' },
  { id: 'cheers', name: 'Cheering 🥳', icon: '🎉', color: 'from-emerald-600 to-teal-600' },
  { id: 'airhorn', name: 'Airhorn 📢', icon: '📯', color: 'from-amber-600 to-orange-600' },
  { id: 'victory', name: 'Victory Bell 🔔', icon: '🔔', color: 'from-yellow-500 to-amber-600' },
  { id: 'kiss', name: 'Romantic Kiss 💋', icon: '💋', color: 'from-rose-500 to-pink-600' },
  { id: 'magic', name: 'Magic Chime ✨', icon: '✨', color: 'from-purple-600 to-violet-600' },
];

export const SoundboardModal: React.FC<SoundboardModalProps> = ({ isOpen, onClose }) => {
  const { playRoomSound, sendRoomMessage } = useAura();

  if (!isOpen) return null;

  const triggerSound = (soundId: string, name: string) => {
    playRoomSound(soundId);
    sendRoomMessage(`triggered sound effect: ${name}`);
  };

  return (
    <div
      id="soundboard-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4 animate-in fade-in duration-150"
      onClick={onClose}
    >
      <div
        id="soundboard-modal"
        className="w-full max-w-sm bg-[#160c2b] border border-[#442875] rounded-3xl p-5 shadow-2xl relative"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          id="close-soundboard-btn"
          onClick={onClose}
          className="absolute top-4 right-4 p-1 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="flex items-center gap-2 mb-3">
          <Volume2 className="w-5 h-5 text-[#00e5ff]" />
          <div>
            <h3 className="text-sm font-bold text-white">Live Voice Soundboard</h3>
            <p className="text-[11px] text-slate-400">Play real-time acoustic sound effects</p>
          </div>
        </div>

        <div className="grid grid-cols-2 gap-2.5">
          {SOUNDS.map((s) => (
            <button
              key={s.id}
              id={`sound-btn-${s.id}`}
              onClick={() => triggerSound(s.id, s.name)}
              className={`flex items-center gap-2 p-3 rounded-2xl bg-gradient-to-r ${s.color} text-white font-bold text-xs shadow-md hover:scale-105 active:scale-95 transition-all text-left`}
            >
              <span className="text-xl select-none">{s.icon}</span>
              <span className="truncate">{s.name.split(' ')[0]}</span>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};
