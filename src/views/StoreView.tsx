import React, { useState } from 'react';
import { ShoppingBag, Sparkles, Coins, Check, CheckCircle2 } from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { STORE_ITEMS } from '../data/seedData';
import { StoreItem, StoreItemCategory } from '../types';
import { AvatarWithFrame } from '../components/AvatarWithFrame';

interface StoreViewProps {
  onOpenWallet: () => void;
}

const CATEGORIES: { id: StoreItemCategory; label: string }[] = [
  { id: 'Frame', label: 'Frames 👑' },
  { id: 'Bubble', label: 'Chat Bubbles 💬' },
  { id: 'Ride', label: 'Entry Rides 🏎️' },
  { id: 'Wallpaper', label: 'Room Themes 🌌' },
];

export const StoreView: React.FC<StoreViewProps> = ({ onOpenWallet }) => {
  const { currentUser, storeItems, buyStoreItem, equipStoreItem } = useAura();
  const allItems = storeItems && storeItems.length > 0 ? storeItems : STORE_ITEMS;
  const [selectedCategory, setSelectedCategory] = useState<StoreItemCategory>('Frame');
  const [previewItem, setPreviewItem] = useState<StoreItem>(allItems[0]);
  const [notice, setNotice] = useState<string | null>(null);

  const filteredItems = allItems.filter((item) => item.category === selectedCategory);

  const isOwned = (itemId: string) => {
    const item = allItems.find((s) => s.id === itemId);
    return item?.isOwned ?? false;
  };

  const isEquipped = (item: StoreItem) => {
    if (item.category === 'Frame') return currentUser?.equippedFrameId === item.id;
    if (item.category === 'Bubble') return currentUser?.equippedBubbleId === item.id;
    if (item.category === 'Ride') return currentUser?.equippedRideId === item.id;
    if (item.category === 'Wallpaper') return currentUser?.equippedWallpaperId === item.id;
    return false;
  };

  const handleBuy = (item: StoreItem) => {
    const success = buyStoreItem(item.id);
    if (success) {
      setNotice(`Unlocked ${item.name}! You can equip it now.`);
      setTimeout(() => setNotice(null), 3500);
    }
  };

  const handleEquip = (item: StoreItem) => {
    equipStoreItem(item.id, item.category);
    setNotice(`Equipped ${item.name}!`);
    setTimeout(() => setNotice(null), 3000);
  };

  return (
    <div id="store-view-container" className="max-w-5xl mx-auto px-4 py-4 space-y-4 pb-24">
      {/* Top Banner & Wallet Shortcut */}
      <div className="bg-gradient-to-r from-[#201042] via-[#2d1659] to-[#1a0f33] border border-[#442875] rounded-3xl p-4 sm:p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-3 shadow-xl">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-[#ff2a85] via-[#7c4dff] to-[#00e5ff] flex items-center justify-center text-white shadow-lg">
            <ShoppingBag className="w-6 h-6" />
          </div>
          <div>
            <h1 className="text-base sm:text-lg font-black text-white flex items-center gap-2">
              <span>Avatar & Room Mall</span>
              <span className="px-2 py-0.5 rounded-full bg-amber-500/20 text-amber-300 text-[10px] font-bold border border-amber-500/30">
                VIP Boutique
              </span>
            </h1>
            <p className="text-xs text-slate-300">
              Personalize your identity with animated frames, high-octane entrance rides, and themed bubbles
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <div className="px-3 py-1.5 rounded-2xl bg-[#120a24] border border-[#392062] flex items-center gap-1.5">
            <Coins className="w-4 h-4 text-amber-400" />
            <span className="text-xs font-black text-amber-300">
              {currentUser?.coins.toLocaleString() || 0}
            </span>
          </div>
          <button
            id="store-recharge-coins-btn"
            onClick={onOpenWallet}
            className="px-3.5 py-1.5 rounded-2xl bg-gradient-to-r from-amber-500 to-rose-500 text-black font-extrabold text-xs shadow hover:opacity-95"
          >
            + Top Up
          </button>
        </div>
      </div>

      {/* Live Preview Area */}
      <div className="bg-[#170e30] border border-[#341e5e] rounded-3xl p-4 flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <AvatarWithFrame
            avatarUrl={currentUser?.avatarUrl}
            size={60}
            equippedFrameId={
              previewItem.category === 'Frame'
                ? previewItem.id
                : currentUser?.equippedFrameId
            }
            vipLevel={currentUser?.vipLevel}
          />
          <div>
            <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">
              Item Preview:
            </span>
            <h3 className="text-sm font-extrabold text-white flex items-center gap-2 mt-0.5">
              <span>{previewItem.name}</span>
              <span className="text-xl">{previewItem.previewIcon}</span>
            </h3>
            <p className="text-xs text-slate-400 mt-0.5">{previewItem.description}</p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          {isOwned(previewItem.id) ? (
            isEquipped(previewItem) ? (
              <span className="px-4 py-2 rounded-xl bg-emerald-500/20 text-emerald-300 text-xs font-bold border border-emerald-500/40 flex items-center gap-1.5">
                <CheckCircle2 className="w-4 h-4" />
                <span>Currently Equipped</span>
              </span>
            ) : (
              <button
                id="preview-equip-btn"
                onClick={() => handleEquip(previewItem)}
                className="px-5 py-2 rounded-xl bg-[#7c4dff] hover:bg-[#6c3df5] text-white font-bold text-xs shadow-md transition-all"
              >
                Equip This Item
              </button>
            )
          ) : (
            <button
              id="preview-buy-btn"
              onClick={() => handleBuy(previewItem)}
              className="px-5 py-2 rounded-xl bg-gradient-to-r from-amber-500 to-rose-500 text-black font-extrabold text-xs shadow-lg flex items-center gap-1.5 hover:opacity-95 active:scale-95 transition-all"
            >
              <Coins className="w-3.5 h-3.5" />
              <span>Buy for {previewItem.price.toLocaleString()} Coins</span>
            </button>
          )}
        </div>
      </div>

      {/* Notification banner */}
      {notice && (
        <div className="p-3 rounded-2xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <Check className="w-4 h-4 text-emerald-400 flex-shrink-0" />
          <span>{notice}</span>
        </div>
      )}

      {/* Category Tabs */}
      <div className="flex gap-2 overflow-x-auto pb-1">
        {CATEGORIES.map((cat) => (
          <button
            key={cat.id}
            id={`store-tab-${cat.id}`}
            onClick={() => setSelectedCategory(cat.id)}
            className={`px-4 py-2 rounded-2xl text-xs font-bold transition-all flex-shrink-0 ${
              selectedCategory === cat.id
                ? 'bg-[#7c4dff] text-white shadow-md'
                : 'bg-[#180e33] border border-[#311b58] text-slate-400 hover:text-white'
            }`}
          >
            {cat.label}
          </button>
        ))}
      </div>

      {/* Items Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3.5">
        {filteredItems.map((item) => {
          const owned = isOwned(item.id);
          const equipped = isEquipped(item);
          const isSelected = previewItem.id === item.id;

          return (
            <div
              key={item.id}
              id={`store-item-${item.id}`}
              onClick={() => setPreviewItem(item)}
              className={`p-4 rounded-3xl bg-[#170e30] border cursor-pointer transition-all flex flex-col justify-between gap-3 ${
                isSelected
                  ? 'border-[#00e5ff] shadow-[0_0_20px_rgba(0,229,255,0.2)]'
                  : 'border-[#331d5b] hover:border-[#7c4dff]'
              }`}
            >
              <div className="flex flex-col items-center text-center gap-2">
                <span className="text-4xl select-none animate-pulse">{item.previewIcon}</span>
                <div>
                  <h4 className="text-xs font-extrabold text-white">{item.name}</h4>
                  <p className="text-[10px] text-slate-400 mt-0.5 line-clamp-2">
                    {item.description}
                  </p>
                </div>
              </div>

              <div className="pt-2 border-t border-[#29174c] flex items-center justify-between">
                <div className="flex items-center gap-1 text-xs font-black text-amber-300">
                  <Coins className="w-3.5 h-3.5 text-amber-400" />
                  <span>{item.price.toLocaleString()}</span>
                </div>

                {owned ? (
                  equipped ? (
                    <span className="px-2 py-1 rounded-xl bg-emerald-500/20 text-emerald-300 text-[10px] font-bold">
                      Equipped
                    </span>
                  ) : (
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        handleEquip(item);
                      }}
                      className="px-2.5 py-1 rounded-xl bg-[#7c4dff] text-white text-[10px] font-bold hover:bg-[#6c3df5]"
                    >
                      Equip
                    </button>
                  )
                ) : (
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleBuy(item);
                    }}
                    className="px-2.5 py-1 rounded-xl bg-gradient-to-r from-amber-500 to-rose-500 text-black text-[10px] font-extrabold hover:opacity-95"
                  >
                    Buy
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
