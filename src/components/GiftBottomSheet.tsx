import React, { useState } from 'react';
import { X, Gift, Coins, Sparkles, Check } from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { VIRTUAL_GIFTS } from '../data/seedData';
import { VirtualGift } from '../types';
import { AvatarWithFrame } from './AvatarWithFrame';

interface GiftBottomSheetProps {
  isOpen: boolean;
  onClose: () => void;
  onSelectUser: (userId: string) => void;
}

export const GiftBottomSheet: React.FC<GiftBottomSheetProps> = ({ isOpen, onClose }) => {
  const { currentUser, activeRoomSeats, sendGift, allUsers } = useAura();
  const [giftTab, setGiftTab] = useState<'standard' | 'lucky'>('standard');

  const standardGifts = VIRTUAL_GIFTS.filter((g) => !g.isLucky);
  const luckyGifts = VIRTUAL_GIFTS.filter((g) => g.isLucky);
  const currentGiftList = giftTab === 'standard' ? standardGifts : luckyGifts;

  const [selectedGift, setSelectedGift] = useState<VirtualGift>(standardGifts[0]);

  // Determine eligible receivers: any seat with an active user
  const occupiedSeats = activeRoomSeats.filter((s) => s.userId);
  const [selectedReceiverId, setSelectedReceiverId] = useState<string>(
    occupiedSeats[0]?.userId || ''
  );

  if (!isOpen) return null;

  const handleSend = () => {
    if (!selectedReceiverId && occupiedSeats[0]?.userId) {
      setSelectedReceiverId(occupiedSeats[0].userId);
    }
    const targetId = selectedReceiverId || occupiedSeats[0]?.userId || '';
    if (!targetId) {
      alert('Please select a receiver on mic to send this gift to.');
      return;
    }

    if ((currentUser?.coins || 0) < selectedGift.costCoins) {
      alert('Insufficient Coins! Please recharge your wallet to send this gift.');
      return;
    }

    sendGift(selectedGift, targetId);
    onClose();
  };

  return (
    <div
      id="gift-bottom-sheet-backdrop"
      className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-end justify-center animate-in fade-in duration-200"
      onClick={onClose}
    >
      <div
        id="gift-bottom-sheet"
        className="w-full max-w-lg bg-[#140b29] border-t border-[#3b2368] rounded-t-3xl p-4 shadow-2xl flex flex-col gap-3 max-h-[85vh] overflow-y-auto"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between border-b border-[#29174b] pb-2.5">
          <div className="flex items-center gap-2">
            <Gift className="w-5 h-5 text-[#ff2a85]" />
            <h3 className="text-sm font-bold text-white">Send Virtual Gifts ✨</h3>
          </div>
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-[#20123f] border border-amber-500/40 text-amber-300 font-bold text-xs">
              <Coins className="w-3.5 h-3.5 text-amber-400" />
              <span>{currentUser?.coins.toLocaleString() || '0'}</span>
            </div>
            <button
              id="close-gift-sheet-btn"
              onClick={onClose}
              className="p-1 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Tab switcher: Standard vs Lucky */}
        <div className="flex items-center gap-2">
          <button
            id="tab-standard-gifts"
            onClick={() => setGiftTab('standard')}
            className={`flex-1 py-1.5 text-xs font-bold rounded-xl transition-all ${
              giftTab === 'standard'
                ? 'bg-gradient-to-r from-[#ff2a85] to-[#7c4dff] text-white shadow'
                : 'bg-[#1e113b] text-slate-400 hover:text-white'
            }`}
          >
            Standard Gifts
          </button>
          <button
            id="tab-lucky-gifts"
            onClick={() => setGiftTab('lucky')}
            className={`flex-1 py-1.5 text-xs font-bold rounded-xl transition-all ${
              giftTab === 'lucky'
                ? 'bg-gradient-to-r from-amber-500 to-rose-500 text-white shadow'
                : 'bg-[#1e113b] text-slate-400 hover:text-white'
            }`}
          >
            Lucky Multipliers 🍀 (Up to 1000x)
          </button>
        </div>

        {/* Receiver selector from Mic Seats */}
        <div>
          <div className="text-[11px] font-semibold text-slate-400 mb-1.5 flex items-center justify-between">
            <span>Select Speaker on Mic:</span>
            <span className="text-cyan-400">{occupiedSeats.length} on stage</span>
          </div>
          {occupiedSeats.length === 0 ? (
            <div className="p-2.5 rounded-xl bg-[#1b1036] text-center text-xs text-slate-400">
              No speakers currently on mic.
            </div>
          ) : (
            <div className="flex items-center gap-2 overflow-x-auto pb-1">
              {occupiedSeats.map((seat) => {
                const isSelected = selectedReceiverId === seat.userId;
                return (
                  <button
                    key={seat.seatIndex}
                    id={`select-receiver-seat-${seat.seatIndex}`}
                    onClick={() => seat.userId && setSelectedReceiverId(seat.userId)}
                    className={`flex items-center gap-2 px-2.5 py-1.5 rounded-xl border transition-all flex-shrink-0 ${
                      isSelected
                        ? 'bg-[#7c4dff]/30 border-[#00e5ff] text-white shadow-[0_0_10px_rgba(0,229,255,0.3)]'
                        : 'bg-[#1d103b] border-[#36215f] text-slate-300 hover:bg-[#27164f]'
                    }`}
                  >
                    <AvatarWithFrame
                      avatarUrl={seat.avatarUrl}
                      size={28}
                      equippedFrameId={seat.equippedFrameId}
                    />
                    <div className="text-left">
                      <div className="text-xs font-bold truncate max-w-[80px]">
                        {seat.username}
                      </div>
                      <div className="text-[10px] text-cyan-300">
                        {seat.seatIndex === 0 ? '👑 Host' : `Mic #${seat.seatIndex + 1}`}
                      </div>
                    </div>
                    {isSelected && <Check className="w-3.5 h-3.5 text-[#00e5ff]" />}
                  </button>
                );
              })}
            </div>
          )}
        </div>

        {/* Gift Grid */}
        <div className="grid grid-cols-3 sm:grid-cols-4 gap-2 pt-1">
          {currentGiftList.map((gift) => {
            const isSelected = selectedGift.id === gift.id;
            return (
              <div
                key={gift.id}
                id={`gift-card-${gift.id}`}
                onClick={() => setSelectedGift(gift)}
                className={`relative flex flex-col items-center p-2.5 rounded-2xl cursor-pointer border transition-all ${
                  isSelected
                    ? 'bg-gradient-to-b from-[#ff2a85]/20 to-[#7c4dff]/30 border-[#ff2a85] scale-105 shadow-[0_0_12px_rgba(255,42,133,0.4)]'
                    : 'bg-[#1b0f36] border-[#311b58] hover:border-[#673ab7]'
                }`}
              >
                {gift.isLucky && (
                  <span className="absolute -top-1.5 -right-1 px-1.5 py-[0.5px] rounded-full bg-amber-500 text-black text-[9px] font-extrabold shadow">
                    {gift.multiplierMax}x
                  </span>
                )}
                <span className="text-3xl my-1 select-none animate-bounce" style={{ animationDuration: '2s' }}>
                  {gift.iconEmoji}
                </span>
                <span className="text-xs font-bold text-white text-center truncate w-full">
                  {gift.name.split(' (')[0]}
                </span>
                <div className="flex items-center gap-1 mt-1 text-[11px] font-extrabold text-amber-300">
                  <Coins className="w-3 h-3 text-amber-400" />
                  <span>{gift.costCoins.toLocaleString()}</span>
                </div>
              </div>
            );
          })}
        </div>

        {/* Bottom Action Footer */}
        <div className="flex items-center justify-between pt-2 border-t border-[#29174b] mt-1">
          <div className="flex flex-col">
            <span className="text-xs text-slate-400">Total Price:</span>
            <div className="flex items-center gap-1 font-bold text-sm text-amber-400">
              <Coins className="w-4 h-4" />
              <span>{selectedGift.costCoins.toLocaleString()} Coins</span>
            </div>
          </div>

          <button
            id="send-gift-action-btn"
            onClick={handleSend}
            disabled={occupiedSeats.length === 0}
            className="flex items-center gap-2 px-6 py-2.5 rounded-xl bg-gradient-to-r from-[#ff2a85] to-[#7c4dff] hover:opacity-95 active:scale-95 text-white font-bold text-xs shadow-[0_4px_16px_rgba(255,42,133,0.4)] disabled:opacity-50 disabled:cursor-not-allowed transition-all"
          >
            <Sparkles className="w-4 h-4" />
            <span>Send Gift</span>
          </button>
        </div>
      </div>
    </div>
  );
};
