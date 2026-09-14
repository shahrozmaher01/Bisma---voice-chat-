import React, { useState } from 'react';
import { X, UserPlus, Sparkles, Check, Globe } from 'lucide-react';
import { useAura } from '../context/AuraContext';

interface CreateIdModalProps {
  isOpen: boolean;
  onClose: () => void;
  onCreated?: (userId: string) => void;
}

const PRESET_AVATARS = [
  'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300',
  'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300',
  'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300',
  'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300',
  'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300',
  'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=300',
];

const COUNTRIES = [
  '🇵🇰 Pakistan',
  '🌐 Global',
  '🇸🇦 Saudi Arabia',
  '🇦🇪 United Arab Emirates',
  '🇹🇷 Turkey',
  '🇬🇧 United Kingdom',
  '🇺🇸 United States',
  '🇪🇬 Egypt',
];

export const CreateIdModal: React.FC<CreateIdModalProps> = ({ isOpen, onClose, onCreated }) => {
  const { createUserId } = useAura();

  const [username, setUsername] = useState('');
  const [gender, setGender] = useState<'Female' | 'Male' | 'Not specified'>('Female');
  const [customId, setCustomId] = useState('');
  const [country, setCountry] = useState(COUNTRIES[0]);
  const [selectedAvatar, setSelectedAvatar] = useState(PRESET_AVATARS[0]);
  const [customAvatarUrl, setCustomAvatarUrl] = useState('');
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg(null);

    const effectiveAvatar = customAvatarUrl.trim() || selectedAvatar;

    // Call createUserId - strictly no coin fields or coin parameters
    const res = createUserId({
      username: username.trim(),
      gender,
      customId: customId.trim() || undefined,
      country,
      avatarUrl: effectiveAvatar,
    });

    if (!res.success) {
      setErrorMsg(res.message || 'Failed to create User ID.');
      return;
    }

    if (res.user && onCreated) {
      onCreated(res.user.id);
    }

    onClose();
  };

  return (
    <div
      id="create-id-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/80 backdrop-blur-xs flex items-center justify-center p-3 sm:p-5 animate-in fade-in duration-150"
      onClick={onClose}
    >
      <div
        id="create-id-modal"
        className="w-full max-w-md bg-[#130a26] border border-[#442875] rounded-3xl p-5 shadow-2xl flex flex-col gap-3 relative max-h-[92vh] overflow-y-auto"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          id="close-create-id-btn"
          onClick={onClose}
          className="absolute top-4 right-4 p-1.5 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Modal Header */}
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-2xl bg-gradient-to-tr from-[#7c4dff] to-[#00e5ff] flex items-center justify-center text-white shadow-lg">
            <UserPlus className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-base font-extrabold text-white">Create New User ID</h2>
            <p className="text-[11px] text-slate-400">Register a new profile for voice party and chat</p>
          </div>
        </div>

        {errorMsg && (
          <div className="p-2.5 rounded-xl bg-rose-500/20 border border-rose-500/40 text-rose-300 text-xs font-semibold">
            {errorMsg}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-3 mt-1">
          {/* Avatar Picker */}
          <div>
            <label className="text-xs font-semibold text-slate-300 mb-1.5 block">
              Choose Profile Avatar:
            </label>
            <div className="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-none">
              {PRESET_AVATARS.map((url, idx) => (
                <button
                  key={idx}
                  type="button"
                  onClick={() => {
                    setSelectedAvatar(url);
                    setCustomAvatarUrl('');
                  }}
                  className={`relative w-12 h-12 rounded-2xl overflow-hidden border-2 flex-shrink-0 transition-transform ${
                    selectedAvatar === url && !customAvatarUrl
                      ? 'border-[#00e5ff] scale-105 shadow-[0_0_12px_rgba(0,229,255,0.4)]'
                      : 'border-[#371f5c] opacity-70 hover:opacity-100'
                  }`}
                >
                  <img src={url} alt="preset avatar" className="w-full h-full object-cover" />
                  {selectedAvatar === url && !customAvatarUrl && (
                    <div className="absolute inset-0 bg-[#00e5ff]/20 flex items-center justify-center">
                      <Check className="w-4 h-4 text-white drop-shadow" />
                    </div>
                  )}
                </button>
              ))}
            </div>
          </div>

          {/* Username */}
          <div>
            <label className="text-xs font-semibold text-slate-300 mb-1 block">
              User Name / Nickname <span className="text-rose-400">*</span>:
            </label>
            <input
              id="new-id-username-input"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="e.g. Moonlight Star"
              className="w-full bg-[#1b0f36] border border-[#3b2368] focus:border-[#7c4dff] rounded-xl px-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-hidden"
              required
            />
          </div>

          {/* Gender & Custom ID row */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs font-semibold text-slate-300 mb-1 block">Gender:</label>
              <select
                id="new-id-gender-select"
                value={gender}
                onChange={(e) => setGender(e.target.value as any)}
                className="w-full bg-[#1b0f36] border border-[#3b2368] focus:border-[#7c4dff] rounded-xl px-3 py-2 text-xs text-white focus:outline-hidden"
              >
                <option value="Female">Female ♀️</option>
                <option value="Male">Male ♂️</option>
                <option value="Not specified">Not specified 🌟</option>
              </select>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 mb-1 block">
                Custom ID <span className="text-slate-500 font-normal">(Optional)</span>:
              </label>
              <input
                id="new-id-custom-id-input"
                type="text"
                value={customId}
                onChange={(e) => setCustomId(e.target.value)}
                placeholder="Auto (e.g. 782194)"
                className="w-full bg-[#1b0f36] border border-[#3b2368] focus:border-[#7c4dff] rounded-xl px-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-hidden font-mono"
              />
            </div>
          </div>

          {/* Country */}
          <div>
            <label className="text-xs font-semibold text-slate-300 mb-1 block">Country / Region:</label>
            <select
              id="new-id-country-select"
              value={country}
              onChange={(e) => setCountry(e.target.value)}
              className="w-full bg-[#1b0f36] border border-[#3b2368] focus:border-[#7c4dff] rounded-xl px-3 py-2 text-xs text-white focus:outline-hidden"
            >
              {COUNTRIES.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          </div>

          {/* Submission Button */}
          <button
            id="create-id-submit-btn"
            type="submit"
            className="w-full py-2.5 rounded-xl bg-gradient-to-r from-[#7c4dff] to-[#00e5ff] text-black font-black text-xs shadow-[0_4px_16px_rgba(124,77,255,0.4)] hover:opacity-95 active:scale-95 transition-all mt-2"
          >
            Create User ID Now ✨
          </button>
        </form>
      </div>
    </div>
  );
};
