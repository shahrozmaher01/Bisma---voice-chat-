import React, { useState } from 'react';
import {
  DollarSign,
  Coins,
  Gem,
  Plus,
  Edit,
  Trash2,
  CheckCircle2,
  AlertTriangle,
  TrendingUp,
  Percent,
  RefreshCw,
  Save,
  ShieldCheck,
} from 'lucide-react';
import { useAura } from '../../../context/AuraContext';
import { adminBackend } from '../../../services/adminBackendService';

interface EconomyModuleProps {
  isDarkMode: boolean;
}

export const EconomyModule: React.FC<EconomyModuleProps> = ({ isDarkMode }) => {
  const { allUsers, adjustUserBalance } = useAura();
  const [exchangeRate, setExchangeRate] = useState('30000');
  const [diamondRate, setDiamondRate] = useState('200'); // 200 diamonds = $1
  const [hostSplit, setHostSplit] = useState('65');
  const [agencySplit, setAgencySplit] = useState('12');
  const [platformFee, setPlatformFee] = useState('23');

  // Coin packages
  const [packages, setPackages] = useState([
    { id: '1', usd: 0.99, coins: 30000, tag: 'Starter' },
    { id: '2', usd: 4.99, coins: 150000, tag: 'Popular' },
    { id: '3', usd: 9.99, coins: 300000, tag: 'Best Value' },
    { id: '4', usd: 19.99, coins: 600000, tag: 'Hot' },
    { id: '5', usd: 49.99, coins: 1500000, tag: 'VIP Choice' },
    { id: '6', usd: 99.99, coins: 3000000, tag: 'King Pack' },
  ]);

  const [notice, setNotice] = useState<string | null>(null);

  const handleSaveRates = (e: React.FormEvent) => {
    e.preventDefault();
    adminBackend.updateSettings({
      usdToCoinRate: parseFloat(exchangeRate) || 30000,
      defaultHostCommissionPercent: parseFloat(hostSplit) || 65,
      defaultAgentCommissionPercent: parseFloat(agencySplit) || 12,
    });
    setNotice(`Monetary rates saved successfully! (1 USD = ${exchangeRate} Coins)`);
    setTimeout(() => setNotice(null), 3500);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <DollarSign className="w-6 h-6 text-amber-400" />
            <span>Coin Economy, Diamond Valuation & Rate Engine</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Set global coin purchasing rates, creator diamond conversion, host/agency revenue splits, and store bundles.
          </p>
        </div>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* CORE RATES FORM */}
      <form onSubmit={handleSaveRates} className={`p-5 rounded-2xl border ${cardBg}`}>
        <div className="flex items-center justify-between pb-3 border-b border-inherit mb-4">
          <h3 className="font-extrabold text-sm flex items-center gap-2 text-white">
            <Coins className="w-4 h-4 text-amber-400" />
            <span>Official Currency Pegs</span>
          </h3>
          <button
            type="submit"
            className="flex items-center gap-1.5 px-4 py-1.5 rounded-xl bg-gradient-to-r from-amber-500 to-amber-600 text-black font-extrabold text-xs shadow-md hover:opacity-95"
          >
            <Save className="w-3.5 h-3.5" />
            <span>Save Rate Policies</span>
          </button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 text-xs">
          <div>
            <label className="block text-slate-400 font-bold mb-1">
              Coin Exchange Rate (Per $1.00 USD)
            </label>
            <div className="flex items-center gap-2">
              <input
                type="number"
                value={exchangeRate}
                onChange={(e) => setExchangeRate(e.target.value)}
                className={`w-full px-3 py-2 rounded-xl border outline-none font-black text-amber-300 text-sm ${
                  isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
                }`}
              />
              <span className="text-slate-400 font-bold">Coins</span>
            </div>
            <p className="text-[10px] text-slate-500 mt-1">Official platform peg: 30,000 Coins / $1</p>
          </div>

          <div>
            <label className="block text-slate-400 font-bold mb-1">
              Diamond Payout Rate (Per $1.00 USD)
            </label>
            <div className="flex items-center gap-2">
              <input
                type="number"
                value={diamondRate}
                onChange={(e) => setDiamondRate(e.target.value)}
                className={`w-full px-3 py-2 rounded-xl border outline-none font-black text-cyan-300 text-sm ${
                  isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
                }`}
              />
              <span className="text-slate-400 font-bold">Diamonds</span>
            </div>
            <p className="text-[10px] text-slate-500 mt-1">1 Diamond = $0.005 USD payout</p>
          </div>

          <div>
            <label className="block text-slate-400 font-bold mb-1">Host Talent Split (%)</label>
            <input
              type="number"
              value={hostSplit}
              onChange={(e) => setHostSplit(e.target.value)}
              className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
              }`}
            />
            <p className="text-[10px] text-slate-500 mt-1">Percentage given to stream host</p>
          </div>

          <div>
            <label className="block text-slate-400 font-bold mb-1">Agency Commission (%)</label>
            <input
              type="number"
              value={agencySplit}
              onChange={(e) => setAgencySplit(e.target.value)}
              className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
              }`}
            />
            <p className="text-[10px] text-slate-500 mt-1">Default talent director fee</p>
          </div>
        </div>
      </form>

      {/* COIN RECHARGE PACKAGES */}
      <div className={`p-5 rounded-2xl border ${cardBg}`}>
        <div className="flex items-center justify-between pb-3 border-b border-inherit mb-4">
          <h3 className="font-extrabold text-sm flex items-center gap-2 text-white">
            <Coins className="w-4 h-4 text-amber-400" />
            <span>Storefront Coin Bundles</span>
          </h3>
          <span className="text-xs font-bold text-slate-400">{packages.length} Active SKUs</span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
          {packages.map((pkg) => (
            <div
              key={pkg.id}
              className={`p-4 rounded-xl border flex items-center justify-between ${
                isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-50 border-slate-200'
              }`}
            >
              <div>
                <div className="flex items-center gap-2">
                  <span className="font-black text-amber-400 text-sm">
                    {pkg.coins.toLocaleString()} Coins
                  </span>
                  {pkg.tag && (
                    <span className="text-[9px] font-black uppercase px-1.5 py-0.2 rounded bg-amber-500/20 text-amber-300 border border-amber-500/30">
                      {pkg.tag}
                    </span>
                  )}
                </div>
                <div className="text-xs font-bold text-slate-300 mt-1">${pkg.usd.toFixed(2)} USD</div>
              </div>

              <div className="p-2 rounded-xl bg-amber-500/10 text-amber-400 border border-amber-500/20 font-mono text-xs font-black">
                SKU-{pkg.id}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
