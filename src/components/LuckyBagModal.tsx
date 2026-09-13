import React, { useState } from 'react';
import { X, Coins, Gift, Sparkles, Trophy } from 'lucide-react';
import { useAura } from '../context/AuraContext';

interface LuckyBagModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const LuckyBagModal: React.FC<LuckyBagModalProps> = ({ isOpen, onClose }) => {
  const { currentUser, activeLuckyBag, dropLuckyBag, claimLuckyBag } = useAura();
  const [totalCoins, setTotalCoins] = useState<number>(500);
  const [claimers, setClaimers] = useState<number>(5);
  const [claimStatus, setClaimStatus] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleDrop = () => {
    if ((currentUser?.coins || 0) < totalCoins) {
      alert('Insufficient Coins to drop this lucky bag!');
      return;
    }
    const success = dropLuckyBag(totalCoins, claimers);
    if (success) {
      onClose();
    }
  };

  const handleClaim = () => {
    const res = claimLuckyBag();
    if (res.success) {
      setClaimStatus(`🎉 Congratulations! You received ${res.coinsWon} Coins!`);
    } else {
      setClaimStatus(res.message || 'Unable to claim.');
    }
  };

  const hasClaimed = activeLuckyBag?.claimers.some((c) => c.userId === currentUser?.id);

  return (
    <div
      id="lucky-bag-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4 animate-in fade-in duration-150"
      onClick={onClose}
    >
      <div
        id="lucky-bag-modal"
        className="w-full max-w-sm bg-[#160c2b] border border-[#442875] rounded-3xl p-5 shadow-2xl relative"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          id="close-lucky-bag-modal"
          onClick={onClose}
          className="absolute top-4 right-4 p-1 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
        >
          <X className="w-5 h-5" />
        </button>

        {activeLuckyBag ? (
          /* Active Bag Claim Screen */
          <div className="flex flex-col items-center text-center gap-3 py-2">
            <div className="w-16 h-16 rounded-full bg-gradient-to-tr from-rose-500 via-amber-500 to-yellow-300 flex items-center justify-center shadow-[0_0_20px_rgba(244,63,94,0.6)] animate-pulse">
              <span className="text-3xl select-none">🧧</span>
            </div>

            <h3 className="text-base font-extrabold text-white">
              {activeLuckyBag.senderName}&apos;s Lucky Bag
            </h3>
            <p className="text-xs text-slate-300">
              Total {activeLuckyBag.totalCoins} Coins • {activeLuckyBag.remainingCoins} Coins Left
            </p>

            {claimStatus && (
              <div className="w-full p-2 rounded-xl bg-amber-500/20 border border-amber-500/40 text-amber-300 text-xs font-bold">
                {claimStatus}
              </div>
            )}

            {!hasClaimed && activeLuckyBag.remainingCoins > 0 ? (
              <button
                id="claim-lucky-bag-btn"
                onClick={handleClaim}
                className="w-full py-3 rounded-2xl bg-gradient-to-r from-rose-500 via-amber-500 to-yellow-400 text-black font-extrabold text-sm shadow-[0_4px_16px_rgba(244,63,94,0.4)] active:scale-95 transition-transform flex items-center justify-center gap-2"
              >
                <Sparkles className="w-4 h-4" />
                <span>Open Lucky Bag</span>
              </button>
            ) : (
              <div className="text-xs font-bold text-slate-400 py-1">
                {hasClaimed ? '✅ You have already opened this bag!' : 'Bag has been emptied.'}
              </div>
            )}

            {/* Claimers list */}
            {activeLuckyBag.claimers.length > 0 && (
              <div className="w-full mt-2 border-t border-[#2d1b4e] pt-2 text-left">
                <div className="text-[11px] font-bold text-slate-400 mb-1.5 flex items-center gap-1">
                  <Trophy className="w-3.5 h-3.5 text-amber-400" />
                  <span>Lucky Claimers ({activeLuckyBag.claimers.length}/{activeLuckyBag.totalClaimers}):</span>
                </div>
                <div className="max-h-28 overflow-y-auto space-y-1">
                  {activeLuckyBag.claimers.map((c, i) => (
                    <div
                      key={i}
                      className="flex items-center justify-between text-xs px-2 py-1 rounded bg-[#1e113b]"
                    >
                      <span className="font-medium text-slate-200">{c.userName}</span>
                      <span className="font-bold text-amber-400">+{c.coinsClaimed} Coins</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        ) : (
          /* Drop New Bag Form */
          <div className="flex flex-col gap-4">
            <div className="flex items-center gap-2">
              <span className="text-2xl">🧧</span>
              <div>
                <h3 className="text-sm font-bold text-white">Drop Lucky Coin Bag</h3>
                <p className="text-[11px] text-slate-400">Distribute random coins to room members</p>
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 mb-1 block">Total Coins Amount:</label>
              <div className="grid grid-cols-4 gap-2">
                {[100, 500, 1000, 2500].map((amt) => (
                  <button
                    key={amt}
                    id={`bag-amt-${amt}`}
                    onClick={() => setTotalCoins(amt)}
                    className={`py-2 rounded-xl text-xs font-bold border transition-all ${
                      totalCoins === amt
                        ? 'bg-amber-500 text-black border-amber-400 shadow'
                        : 'bg-[#20123f] border-[#3a2266] text-slate-300 hover:bg-[#2a1753]'
                    }`}
                  >
                    {amt}
                  </button>
                ))}
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 mb-1 block">Number of Claimers:</label>
              <div className="grid grid-cols-4 gap-2">
                {[3, 5, 8, 10].map((num) => (
                  <button
                    key={num}
                    id={`bag-claimers-${num}`}
                    onClick={() => setClaimers(num)}
                    className={`py-2 rounded-xl text-xs font-bold border transition-all ${
                      claimers === num
                        ? 'bg-[#7c4dff] text-white border-[#9e75ff] shadow'
                        : 'bg-[#20123f] border-[#3a2266] text-slate-300 hover:bg-[#2a1753]'
                    }`}
                  >
                    {num} People
                  </button>
                ))}
              </div>
            </div>

            <button
              id="submit-drop-bag-btn"
              onClick={handleDrop}
              className="w-full py-2.5 rounded-xl bg-gradient-to-r from-rose-500 via-amber-500 to-yellow-400 text-black font-extrabold text-xs shadow-[0_4px_16px_rgba(244,63,94,0.4)] active:scale-95 transition-transform flex items-center justify-center gap-2"
            >
              <Gift className="w-4 h-4" />
              <span>Drop Bag ({totalCoins} Coins)</span>
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
