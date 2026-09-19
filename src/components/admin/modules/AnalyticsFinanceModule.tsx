import React, { useState } from 'react';
import {
  PieChart,
  TrendingUp,
  DollarSign,
  Download,
  Calendar,
  ArrowUpRight,
  ArrowDownRight,
  Coins,
  Gem,
  Activity,
} from 'lucide-react';
import { adminBackend } from '../../../services/adminBackendService';

interface AnalyticsFinanceModuleProps {
  isDarkMode: boolean;
}

export const AnalyticsFinanceModule: React.FC<AnalyticsFinanceModuleProps> = ({ isDarkMode }) => {
  const [period, setPeriod] = useState<'7d' | '30d' | 'all'>('30d');

  const grossRevenue = 18450.0;
  const creatorPayouts = 8240.0;
  const agencyCommissions = 1860.0;
  const paymentGatewayFees = 553.5;
  const serverInfrastructure = 340.0;
  const netProfit = grossRevenue - (creatorPayouts + agencyCommissions + paymentGatewayFees + serverInfrastructure);
  const profitMargin = ((netProfit / grossRevenue) * 100).toFixed(1);

  const handleExportPnl = () => {
    const data = [
      { LineItem: 'Gross Coin Recharge Revenue', AmountUSD: grossRevenue, Category: 'Revenue' },
      { LineItem: 'Creator Diamond Cashouts', AmountUSD: -creatorPayouts, Category: 'Expense' },
      { LineItem: 'Agency Network Commissions', AmountUSD: -agencyCommissions, Category: 'Expense' },
      { LineItem: 'Payment Gateway Processing Fees (3%)', AmountUSD: -paymentGatewayFees, Category: 'Expense' },
      { LineItem: 'Audio WebRTC Cloud Servers', AmountUSD: -serverInfrastructure, Category: 'Expense' },
      { LineItem: 'Net Operating Profit', AmountUSD: netProfit, Category: 'Net Income' },
      { LineItem: 'Operating Margin %', AmountUSD: `${profitMargin}%`, Category: 'KPI' },
    ];
    adminBackend.exportCsv('aura_pnl_statement', data);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <PieChart className="w-6 h-6 text-emerald-400" />
            <span>Financial Ledger, Profit & Loss (P&L) & Business Intelligence</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Real-time balance sheet, gross margins, talent payouts, and enterprise unit economics.
          </p>
        </div>

        <button
          onClick={handleExportPnl}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-bold transition-all shadow-xs ${
            isDarkMode
              ? 'border-emerald-500/40 bg-emerald-500/10 text-emerald-300 hover:bg-emerald-500/20'
              : 'border-emerald-300 bg-emerald-50 text-emerald-700 hover:bg-emerald-100'
          }`}
        >
          <Download className="w-3.5 h-3.5" />
          <span>Export P&L Statement (CSV)</span>
        </button>
      </div>

      {/* P&L SUMMARY CARDS */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3.5">
        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">Gross Platform Revenue</span>
          <div className="text-2xl font-black text-emerald-400 mt-1">
            ${grossRevenue.toLocaleString()}
          </div>
          <div className="flex items-center gap-1 text-[11px] text-emerald-400 mt-0.5">
            <ArrowUpRight className="w-3 h-3" />
            <span>+24.8% growth</span>
          </div>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">Host & Agency Payouts</span>
          <div className="text-2xl font-black text-cyan-400 mt-1">
            ${(creatorPayouts + agencyCommissions).toLocaleString()}
          </div>
          <span className="text-[11px] text-slate-400">54.7% revenue share</span>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">Net Operating Profit</span>
          <div className="text-2xl font-black text-amber-300 mt-1">
            ${netProfit.toLocaleString()}
          </div>
          <span className="text-[11px] text-amber-400 font-bold">{profitMargin}% Net Margin</span>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">ARPPU (Avg Revenue)</span>
          <div className="text-2xl font-black text-purple-400 mt-1">$42.80</div>
          <span className="text-[11px] text-purple-300">Per paying creator/user</span>
        </div>
      </div>

      {/* P&L LINE ITEMS TABLE */}
      <div className={`p-6 rounded-2xl border ${cardBg}`}>
        <h3 className="font-extrabold text-sm text-white mb-4">Detailed Accounting Breakdown</h3>

        <div className="space-y-3 text-xs">
          <div className="flex items-center justify-between py-2 border-b border-inherit">
            <span className="font-bold text-slate-200">1. Gross Inbound Coin Purchases</span>
            <span className="font-black text-emerald-400">+${grossRevenue.toFixed(2)} USD</span>
          </div>

          <div className="flex items-center justify-between py-2 border-b border-inherit">
            <span className="text-slate-300">2. Less: Creator Diamond Disbursements (65% share)</span>
            <span className="font-mono text-rose-400">-${creatorPayouts.toFixed(2)} USD</span>
          </div>

          <div className="flex items-center justify-between py-2 border-b border-inherit">
            <span className="text-slate-300">3. Less: Agency Management Commissions (12% tier)</span>
            <span className="font-mono text-rose-400">-${agencyCommissions.toFixed(2)} USD</span>
          </div>

          <div className="flex items-center justify-between py-2 border-b border-inherit">
            <span className="text-slate-300">4. Less: Payment Processing Fees (Google Play, Stripe, EasyPaisa 3%)</span>
            <span className="font-mono text-rose-400">-${paymentGatewayFees.toFixed(2)} USD</span>
          </div>

          <div className="flex items-center justify-between py-2 border-b border-inherit">
            <span className="text-slate-300">5. Less: Voice Server Cluster & WebRTC Bandwidth</span>
            <span className="font-mono text-rose-400">-${serverInfrastructure.toFixed(2)} USD</span>
          </div>

          <div className="flex items-center justify-between pt-3 text-sm font-black">
            <span className="text-white">NET OWNER PROFIT:</span>
            <span className="text-emerald-400 text-base">+${netProfit.toFixed(2)} USD</span>
          </div>
        </div>
      </div>
    </div>
  );
};
