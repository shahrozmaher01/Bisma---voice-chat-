import React, { useState } from 'react';
import {
  Gift,
  Search,
  Plus,
  Trash2,
  CheckCircle2,
  Coins,
  Sparkles,
  Zap,
  Eye,
  X,
  Volume2,
} from 'lucide-react';
import { VirtualGift } from '../../../types';
import { VIRTUAL_GIFTS } from '../../../data/seedData';
import { adminBackend } from '../../../services/adminBackendService';

interface GiftManagementModuleProps {
  isDarkMode: boolean;
}

export const GiftManagementModule: React.FC<GiftManagementModuleProps> = ({ isDarkMode }) => {
  const [localGifts, setLocalGifts] = useState<VirtualGift[]>(() => {
    try {
      const saved = localStorage.getItem('aura_custom_gifts_v1');
      if (saved) return JSON.parse(saved);
    } catch {}
    return VIRTUAL_GIFTS;
  });

  const [searchQuery, setSearchQuery] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [notice, setNotice] = useState<string | null>(null);

  // New gift form
  const [name, setName] = useState('');
  const [iconEmoji, setIconEmoji] = useState('👑');
  const [costCoins, setCostCoins] = useState('5000');
  const [animationType, setAnimationType] = useState<VirtualGift['animationType']>('bloom');
  const [isLucky, setIsLucky] = useState(false);
  const [multiplierMax, setMultiplierMax] = useState('500');

  const filteredGifts = localGifts.filter((g) =>
    g.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleSaveGifts = (updated: VirtualGift[]) => {
    setLocalGifts(updated);
    try {
      localStorage.setItem('aura_custom_gifts_v1', JSON.stringify(updated));
    } catch {}
  };

  const handleCreateGift = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;

    const coins = parseInt(costCoins) || 100;
    const newGift: VirtualGift = {
      id: `g_${Date.now()}`,
      name: name.trim(),
      iconEmoji: iconEmoji.trim() || '🎁',
      costCoins: coins,
      charmPoints: Math.max(1, Math.floor(coins / 10)),
      animationType,
      isLucky,
      multiplierMax: isLucky ? parseInt(multiplierMax) || 500 : undefined,
    };

    const updated = [newGift, ...localGifts];
    handleSaveGifts(updated);
    setShowCreateModal(false);
    setName('');
    setNotice(`Virtual Gift "${newGift.name}" added to catalog!`);
    setTimeout(() => setNotice(null), 3000);
  };

  const handleDeleteGift = (id: string) => {
    const updated = localGifts.filter((g) => g.id !== id);
    handleSaveGifts(updated);
    setNotice('Gift deleted from catalog.');
    setTimeout(() => setNotice(null), 3000);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <Gift className="w-6 h-6 text-pink-400" />
            <span>Virtual Gifts & SVGA Animation Studio</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Configure virtual gifts, set coin prices, define charm point values, and manage lucky multiplier rates.
          </p>
        </div>

        <button
          onClick={() => setShowCreateModal(true)}
          className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-gradient-to-r from-pink-500 to-rose-500 text-white font-extrabold text-xs shadow-md hover:opacity-95"
        >
          <Plus className="w-4 h-4" />
          <span>Create New Gift</span>
        </button>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* FILTER & SEARCH */}
      <div className={`p-4 rounded-2xl border flex flex-col sm:flex-row gap-3 items-center justify-between ${cardBg}`}>
        <div
          className={`w-full sm:w-80 flex items-center gap-2 px-3 py-2 rounded-xl border text-xs ${
            isDarkMode ? 'bg-[#180e30] border-[#361f5c]' : 'bg-slate-100 border-slate-200'
          }`}
        >
          <Search className="w-4 h-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search gifts by name or emoji..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-transparent border-none outline-none text-xs"
          />
        </div>
      </div>

      {/* GIFTS GRID */}
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-3.5">
        {filteredGifts.map((gift) => (
          <div
            key={gift.id}
            className={`p-4 rounded-2xl border flex flex-col justify-between items-center text-center relative group transition-all duration-200 hover:scale-[1.02] ${cardBg}`}
          >
            <div className="w-full flex justify-end">
              <button
                onClick={() => handleDeleteGift(gift.id)}
                className="p-1 rounded-lg text-slate-500 hover:text-rose-400 opacity-0 group-hover:opacity-100 transition-opacity"
                title="Delete Gift"
              >
                <Trash2 className="w-3.5 h-3.5" />
              </button>
            </div>

            <div className="text-4xl sm:text-5xl my-2">{gift.iconEmoji}</div>

            <div className="w-full">
              <h4 className="font-extrabold text-xs text-white truncate">{gift.name}</h4>

              <div className="mt-1 flex items-center justify-center gap-1 text-amber-300 font-black text-xs">
                <Coins className="w-3 h-3 text-amber-400" />
                <span>{gift.costCoins.toLocaleString()}</span>
              </div>

              <div className="text-[10px] text-slate-400 mt-0.5">
                +{gift.charmPoints} Charm XP
              </div>

              {gift.isLucky && (
                <span className="mt-1.5 inline-block px-2 py-0.5 rounded-full text-[9px] font-black bg-amber-500/20 text-amber-300 border border-amber-500/40">
                  LUCKY (up to {gift.multiplierMax}x)
                </span>
              )}
            </div>
          </div>
        ))}
      </div>

      {/* CREATE GIFT MODAL */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-xs">
          <div className={`w-full max-w-md p-6 rounded-3xl border shadow-2xl ${cardBg}`}>
            <div className="flex items-center justify-between pb-3 border-b border-inherit mb-4">
              <h3 className="font-black text-base text-white flex items-center gap-2">
                <Gift className="w-5 h-5 text-pink-400" />
                <span>Publish New Virtual Gift</span>
              </h3>
              <button onClick={() => setShowCreateModal(false)} className="text-slate-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateGift} className="space-y-3.5 text-xs">
              <div>
                <label className="block text-slate-400 font-bold mb-1">Gift Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Golden Phoenix"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                    isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
                  }`}
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-400 font-bold mb-1">Icon / Emoji</label>
                  <input
                    type="text"
                    required
                    value={iconEmoji}
                    onChange={(e) => setIconEmoji(e.target.value)}
                    className={`w-full px-3 py-2 rounded-xl border outline-none text-xl text-center ${
                      isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
                    }`}
                  />
                </div>

                <div>
                  <label className="block text-slate-400 font-bold mb-1">Price (Coins)</label>
                  <input
                    type="number"
                    required
                    value={costCoins}
                    onChange={(e) => setCostCoins(e.target.value)}
                    className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                      isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
                    }`}
                  />
                </div>
              </div>

              <div>
                <label className="block text-slate-400 font-bold mb-1">Animation Type</label>
                <select
                  value={animationType}
                  onChange={(e) => setAnimationType(e.target.value as any)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                    isDarkMode ? 'bg-[#180e30] border-[#3a2062] text-white' : 'bg-slate-100 border-slate-300'
                  }`}
                >
                  <option value="bloom">Bloom (Particles)</option>
                  <option value="hearts">Hearts Shower</option>
                  <option value="drive">Drive (Luxury Car Across Screen)</option>
                  <option value="cruise">Cruise (Mega Yacht Across Stage)</option>
                  <option value="launch">Launch (Space Rocket)</option>
                  <option value="royalty">Royalty (Golden Throne & Confetti)</option>
                  <option value="box">Box (Mystery Opening)</option>
                  <option value="wheel">Wheel (Fortune Spin)</option>
                </select>
              </div>

              <div className="flex items-center justify-between p-3 rounded-xl border border-inherit">
                <div>
                  <div className="font-bold text-slate-200">Lucky Gift Mode</div>
                  <div className="text-[10px] text-slate-400">Allows users to win up to 1000x multiplier</div>
                </div>
                <input
                  type="checkbox"
                  checked={isLucky}
                  onChange={(e) => setIsLucky(e.target.checked)}
                  className="w-4 h-4 accent-pink-500"
                />
              </div>

              <div className="pt-2 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="px-4 py-2 rounded-xl text-slate-400 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-gradient-to-r from-pink-500 to-rose-500 text-white font-extrabold shadow-md"
                >
                  Create Gift
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
