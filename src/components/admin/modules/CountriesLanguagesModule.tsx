import React, { useState } from 'react';
import {
  Globe,
  Plus,
  CheckCircle2,
  DollarSign,
  Languages,
  ToggleLeft,
  ToggleRight,
} from 'lucide-react';

interface CountriesLanguagesModuleProps {
  isDarkMode: boolean;
}

export const CountriesLanguagesModule: React.FC<CountriesLanguagesModuleProps> = ({ isDarkMode }) => {
  const [regions, setRegions] = useState([
    { code: 'PK', country: 'Pakistan', currency: 'PKR', symbol: '₨', exchangeToUsd: 278.5, active: true, localPayments: 'EasyPaisa, JazzCash, Meezan Bank' },
    { code: 'SA', country: 'Saudi Arabia', currency: 'SAR', symbol: '﷼', exchangeToUsd: 3.75, active: true, localPayments: 'Mada, Apple Pay, STC Pay' },
    { code: 'AE', country: 'United Arab Emirates', currency: 'AED', symbol: 'د.إ', exchangeToUsd: 3.67, active: true, localPayments: 'Apple Pay, Card' },
    { code: 'US', country: 'United States', currency: 'USD', symbol: '$', exchangeToUsd: 1.0, active: true, localPayments: 'Google Play, Stripe, PayPal' },
    { code: 'BD', country: 'Bangladesh', currency: 'BDT', symbol: '৳', exchangeToUsd: 119.2, active: true, localPayments: 'bKash, Nagad' },
    { code: 'EG', country: 'Egypt', currency: 'EGP', symbol: 'E£', exchangeToUsd: 48.3, active: true, localPayments: 'Vodafone Cash, Fawry' },
  ]);

  const [notice, setNotice] = useState<string | null>(null);

  const handleToggle = (code: string) => {
    setRegions(regions.map((r) => (r.code === code ? { ...r, active: !r.active } : r)));
    setNotice(`Updated regional availability.`);
    setTimeout(() => setNotice(null), 2500);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div>
        <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
          <Globe className="w-6 h-6 text-cyan-400" />
          <span>Regional Gateways, Localization & Local Currency</span>
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Configure country availability, local payment routes, localized currency symbols, and multi-lingual options.
        </p>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* REGIONS GRID */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {regions.map((reg) => (
          <div key={reg.code} className={`p-5 rounded-2xl border flex flex-col justify-between ${cardBg}`}>
            <div>
              <div className="flex items-center justify-between pb-3 border-b border-inherit">
                <div>
                  <h3 className="font-extrabold text-sm text-white">{reg.country}</h3>
                  <span className="text-[10px] font-mono text-slate-400">
                    {reg.currency} ({reg.symbol})
                  </span>
                </div>

                <button
                  onClick={() => handleToggle(reg.code)}
                  className={`text-xs font-bold px-2 py-0.5 rounded-full border ${
                    reg.active
                      ? 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30'
                      : 'bg-slate-500/20 text-slate-400 border-slate-500/30'
                  }`}
                >
                  {reg.active ? 'ENABLED' : 'DISABLED'}
                </button>
              </div>

              <div className="py-3 space-y-1.5 text-xs text-slate-300">
                <div className="flex items-center justify-between">
                  <span className="text-slate-400">Forex vs USD:</span>
                  <span className="font-mono font-bold">1 USD = {reg.exchangeToUsd} {reg.currency}</span>
                </div>

                <div>
                  <span className="text-slate-400">Allowed Gateways:</span>
                  <div className="text-cyan-300 font-semibold mt-0.5">{reg.localPayments}</div>
                </div>
              </div>
            </div>

            <div className="pt-3 border-t border-inherit text-[10px] text-slate-500">
              Auto-IP Geolocation Active
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
