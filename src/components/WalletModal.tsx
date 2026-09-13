import React, { useState } from 'react';
import { X, Wallet, Coins, Gem, ArrowRightLeft, Sparkles, Check } from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { RECHARGE_PACKAGES } from '../data/seedData';
import { RechargePackage } from '../types';

interface WalletModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const WalletModal: React.FC<WalletModalProps> = ({ isOpen, onClose }) => {
  const { currentUser, rechargeCoins, convertDiamondsToCoins } = useAura();
  const [activeTab, setActiveTab] = useState<'recharge' | 'convert'>('recharge');
  const [convertDiamondsInput, setConvertDiamondsInput] = useState('100');
  const [statusNotice, setStatusNotice] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleRecharge = (pkg: RechargePackage) => {
    rechargeCoins(pkg);
    setStatusNotice(`Successfully purchased ${pkg.coins.toLocaleString()} Coins!`);
    setTimeout(() => setStatusNotice(null), 3500);
  };

  const handleConvert = (e: React.FormEvent) => {
    e.preventDefault();
    const amt = parseInt(convertDiamondsInput, 10);
    if (isNaN(amt) || amt <= 0) return;
    if ((currentUser?.diamonds || 0) < amt) {
      alert('Insufficient diamonds in your balance.');
      return;
    }

    const success = convertDiamondsToCoins(amt);
    if (success) {
      setStatusNotice(`Successfully converted ${amt} Diamonds into ${amt * 8} Coins!`);
      setTimeout(() => setStatusNotice(null), 3500);
    }
  };

  return (
    <div
      id="wallet-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-3 sm:p-5 animate-in fade-in duration-150"
      onClick={onClose}
    >
      <div
        id="wallet-modal"
        className="w-full max-w-md bg-[#130a26] border border-[#442875] rounded-3xl p-4 sm:p-5 shadow-2xl flex flex-col max-h-[88vh] overflow-hidden relative"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between border-b border-[#2b174a] pb-3">
          <div className="flex items-center gap-2">
            <Wallet className="w-5 h-5 text-amber-400" />
            <h2 className="text-base font-extrabold text-white">Wallet & Balances</h2>
          </div>
          <button
            id="close-wallet-btn"
            onClick={onClose}
            className="p-1.5 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Current Balance Banner */}
        <div className="grid grid-cols-2 gap-3 my-3">
          <div className="p-3 rounded-2xl bg-gradient-to-br from-amber-500/20 to-yellow-600/10 border border-amber-500/40 flex flex-col">
            <span className="text-[11px] font-bold text-amber-300 flex items-center gap-1">
              <Coins className="w-3.5 h-3.5" />
              <span>Coins Balance</span>
            </span>
            <span className="text-xl font-black text-amber-300 mt-1">
              {currentUser?.coins.toLocaleString() || 0}
            </span>
            <span className="text-[10px] text-amber-400/80">Used for virtual gifts & games</span>
          </div>

          <div className="p-3 rounded-2xl bg-gradient-to-br from-cyan-500/20 to-blue-600/10 border border-cyan-500/40 flex flex-col">
            <span className="text-[11px] font-bold text-cyan-300 flex items-center gap-1">
              <Gem className="w-3.5 h-3.5" />
              <span>Diamonds Balance</span>
            </span>
            <span className="text-xl font-black text-cyan-300 mt-1">
              {currentUser?.diamonds.toLocaleString() || 0}
            </span>
            <span className="text-[10px] text-cyan-400/80">Host & creator received gifts</span>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex gap-2 mb-3">
          <button
            id="wallet-tab-recharge"
            onClick={() => setActiveTab('recharge')}
            className={`flex-1 py-2 rounded-xl text-xs font-bold transition-all ${
              activeTab === 'recharge'
                ? 'bg-gradient-to-r from-amber-500 to-rose-500 text-black shadow font-extrabold'
                : 'bg-[#1a0f33] text-slate-400 hover:text-white'
            }`}
          >
            Recharge Coins 🪙
          </button>
          <button
            id="wallet-tab-convert"
            onClick={() => setActiveTab('convert')}
            className={`flex-1 py-2 rounded-xl text-xs font-bold transition-all ${
              activeTab === 'convert'
                ? 'bg-gradient-to-r from-cyan-500 to-blue-500 text-black shadow font-extrabold'
                : 'bg-[#1a0f33] text-slate-400 hover:text-white'
            }`}
          >
            Convert Diamonds 💎
          </button>
        </div>

        {/* Status notice */}
        {statusNotice && (
          <div className="p-2.5 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 mb-3 animate-in fade-in">
            <Check className="w-4 h-4 text-emerald-400 flex-shrink-0" />
            <span>{statusNotice}</span>
          </div>
        )}

        {/* Content */}
        <div className="flex-1 overflow-y-auto space-y-2.5 pr-1">
          {activeTab === 'recharge' && (
            <div className="space-y-2">
              <span className="text-xs font-bold text-slate-400 block mb-1">
                Official In-App Coin Packs:
              </span>
              {RECHARGE_PACKAGES.map((pkg) => (
                <div
                  key={pkg.id}
                  id={`pkg-${pkg.id}`}
                  onClick={() => handleRecharge(pkg)}
                  className="flex items-center justify-between p-3 rounded-2xl bg-[#1a0f33] border border-[#351d5c] hover:border-[#ffd700]/60 cursor-pointer transition-all hover:scale-[1.01]"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-xl bg-amber-500/20 text-amber-400 flex items-center justify-center font-bold text-lg">
                      🪙
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-bold text-xs text-white">{pkg.title}</span>
                        {pkg.tag && (
                          <span className="px-1.5 py-[0.5px] rounded bg-amber-500 text-black text-[9px] font-black">
                            {pkg.tag}
                          </span>
                        )}
                      </div>
                      <div className="text-[11px] text-amber-300 font-semibold mt-0.5">
                        {pkg.coins.toLocaleString()} Coins + {pkg.bonusDiamonds} Bonus Diamonds
                      </div>
                    </div>
                  </div>
                  <div className="px-3 py-1.5 rounded-xl bg-gradient-to-r from-amber-500 to-rose-500 text-black text-xs font-black shadow-md">
                    ${pkg.priceUsd}
                  </div>
                </div>
              ))}
            </div>
          )}

          {activeTab === 'convert' && (
            <form onSubmit={handleConvert} className="p-4 rounded-2xl bg-[#1a0f33] border border-[#351d5c] space-y-3">
              <div className="flex items-center gap-2 text-cyan-300 font-bold text-xs">
                <ArrowRightLeft className="w-4 h-4" />
                <span>Exchange Diamonds to Coins (1 Diamond = 8 Coins)</span>
              </div>

              <div>
                <label className="text-xs text-slate-300 mb-1 block">Diamonds to Convert:</label>
                <input
                  type="number"
                  value={convertDiamondsInput}
                  onChange={(e) => setConvertDiamondsInput(e.target.value)}
                  className="w-full bg-[#120a24] border border-[#381f60] rounded-xl px-3 py-2 text-xs text-white"
                  min="1"
                />
              </div>

              <div className="p-2.5 rounded-xl bg-[#120a24] border border-[#2d1b4e] flex justify-between text-xs font-semibold">
                <span className="text-slate-400">Coins You Will Receive:</span>
                <span className="text-amber-300 font-extrabold">
                  {(parseInt(convertDiamondsInput, 10) || 0) * 8} Coins 🪙
                </span>
              </div>

              <button
                type="submit"
                className="w-full py-2.5 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-500 text-black font-extrabold text-xs shadow hover:opacity-95 transition-opacity"
              >
                Confirm Conversion
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};
