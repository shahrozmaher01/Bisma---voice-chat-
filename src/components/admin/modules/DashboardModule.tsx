import React, { useState, useEffect } from 'react';
import {
  Users,
  Mic,
  Briefcase,
  Crown,
  Coins,
  Gem,
  CreditCard,
  DollarSign,
  TrendingUp,
  Activity,
  Radio,
  ArrowUpRight,
  ArrowDownRight,
  ShieldCheck,
  AlertTriangle,
  Download,
  Calendar,
  Sparkles,
} from 'lucide-react';
import { useAura } from '../../../context/AuraContext';
import { adminBackend } from '../../../services/adminBackendService';
import { AdminNavModule } from '../AdminLayout';

interface DashboardModuleProps {
  onNavigate: (module: AdminNavModule) => void;
  isDarkMode: boolean;
}

export const DashboardModule: React.FC<DashboardModuleProps> = ({ onNavigate, isDarkMode }) => {
  const { allUsers, rooms } = useAura();
  const [timeframe, setTimeframe] = useState<'today' | '7d' | '30d' | 'all'>('7d');
  const [hosts, setHosts] = useState(adminBackend.getHosts());
  const [agents, setAgents] = useState(adminBackend.getAgents());
  const [withdrawals, setWithdrawals] = useState(adminBackend.getWithdrawals());
  const [payments, setPayments] = useState(adminBackend.getPayments());
  const [reports, setReports] = useState(adminBackend.getReports());

  useEffect(() => {
    const syncData = () => {
      setHosts(adminBackend.getHosts());
      setAgents(adminBackend.getAgents());
      setWithdrawals(adminBackend.getWithdrawals());
      setPayments(adminBackend.getPayments());
      setReports(adminBackend.getReports());
    };
    return adminBackend.subscribe(syncData);
  }, []);

  // Compute calculated metrics
  const totalUsersCount = allUsers.length;
  const onlineUsersEstimate = Math.max(14, Math.floor(allUsers.length * 0.72));
  const newUsersToday = Math.max(3, Math.floor(allUsers.length * 0.15));
  const activeRooms = rooms.filter((r) => r.isActive);
  const activeHosts = hosts.filter((h) => h.status === 'APPROVED');
  const vipUsersCount = allUsers.filter((u) => u.vipLevel > 0).length;
  const totalCoins = allUsers.reduce((acc, u) => acc + u.coins, 0);
  const totalDiamonds = allUsers.reduce((acc, u) => acc + u.diamonds, 0);
  const totalRevenueUsd = payments.reduce((acc, p) => (p.status === 'SUCCESS' ? acc + p.amountUsd : acc), 0) + 1850;
  const pendingWithdrawalUsd = withdrawals
    .filter((w) => w.status === 'PENDING')
    .reduce((acc, w) => acc + w.amountUsd, 0);
  const unresolvedReports = reports.filter((r) => r.status === 'PENDING');

  const handleExportDashboard = () => {
    const summaryRow = [
      {
        Metric: 'Total Users',
        Value: totalUsersCount,
      },
      {
        Metric: 'Estimated Online Users',
        Value: onlineUsersEstimate,
      },
      {
        Metric: 'Active Voice Rooms',
        Value: activeRooms.length,
      },
      {
        Metric: 'Total Hosts',
        Value: hosts.length,
      },
      {
        Metric: 'Total Agencies',
        Value: agents.length,
      },
      {
        Metric: 'VIP Users Count',
        Value: vipUsersCount,
      },
      {
        Metric: 'Total Coins in Circulation',
        Value: totalCoins,
      },
      {
        Metric: 'Total Diamonds in Circulation',
        Value: totalDiamonds,
      },
      {
        Metric: 'Total Revenue (USD)',
        Value: totalRevenueUsd,
      },
      {
        Metric: 'Pending Withdrawals (USD)',
        Value: pendingWithdrawalUsd,
      },
      {
        Metric: 'Export Timestamp',
        Value: new Date().toISOString(),
      },
    ];
    adminBackend.exportCsv('aura_dashboard_metrics', summaryRow);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Top Banner & Date Selector */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <span>Executive Command Center</span>
            <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-gradient-to-r from-rose-500 to-purple-600 text-white shadow-xs">
              LIVE TELEMETRY
            </span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Real-time multi-dimensional pulse of AURA Live voice streaming and creator economy.
          </p>
        </div>

        <div className="flex items-center gap-2">
          {/* Timeframe selector */}
          <div
            className={`flex items-center p-1 rounded-xl border text-xs font-bold ${
              isDarkMode ? 'bg-[#180d34] border-[#371f61]' : 'bg-slate-100 border-slate-200'
            }`}
          >
            {(['today', '7d', '30d', 'all'] as const).map((t) => (
              <button
                key={t}
                onClick={() => setTimeframe(t)}
                className={`px-3 py-1 rounded-lg uppercase text-[10px] transition-all ${
                  timeframe === t
                    ? isDarkMode
                      ? 'bg-purple-600 text-white shadow-xs'
                      : 'bg-white text-purple-700 shadow-xs'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                {t === 'all' ? 'All Time' : t}
              </button>
            ))}
          </div>

          <button
            onClick={handleExportDashboard}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-bold transition-all shadow-xs ${
              isDarkMode
                ? 'border-purple-500/40 bg-purple-500/10 text-purple-300 hover:bg-purple-500/20'
                : 'border-purple-300 bg-purple-50 text-purple-700 hover:bg-purple-100'
            }`}
          >
            <Download className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">Export CSV</span>
          </button>
        </div>
      </div>

      {/* KPI STAT CARDS GRID */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3.5">
        {/* Total Users */}
        <div
          onClick={() => onNavigate('users')}
          className={`p-4 rounded-2xl border cursor-pointer hover:scale-[1.01] transition-all group ${cardBg}`}
        >
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>Total Registered</span>
            <div className="w-8 h-8 rounded-xl bg-purple-500/20 text-purple-400 flex items-center justify-center group-hover:bg-purple-500 group-hover:text-white transition-colors">
              <Users className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black mt-2">{totalUsersCount.toLocaleString()}</div>
          <div className="flex items-center gap-1.5 text-[11px] text-emerald-400 font-semibold mt-1">
            <TrendingUp className="w-3 h-3" />
            <span>+{newUsersToday} today</span>
            <span className="text-slate-400 font-normal">({onlineUsersEstimate} online)</span>
          </div>
        </div>

        {/* Active Voice Rooms */}
        <div
          onClick={() => onNavigate('rooms')}
          className={`p-4 rounded-2xl border cursor-pointer hover:scale-[1.01] transition-all group ${cardBg}`}
        >
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>Active Voice Rooms</span>
            <div className="w-8 h-8 rounded-xl bg-rose-500/20 text-rose-400 flex items-center justify-center group-hover:bg-rose-500 group-hover:text-white transition-colors">
              <Radio className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black mt-2">{activeRooms.length}</div>
          <div className="flex items-center gap-1.5 text-[11px] text-rose-400 font-semibold mt-1">
            <span className="w-2 h-2 rounded-full bg-rose-500 animate-ping" />
            <span>Streaming live now</span>
          </div>
        </div>

        {/* Total Hosts */}
        <div
          onClick={() => onNavigate('hosts')}
          className={`p-4 rounded-2xl border cursor-pointer hover:scale-[1.01] transition-all group ${cardBg}`}
        >
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>Verified Hosts</span>
            <div className="w-8 h-8 rounded-xl bg-cyan-500/20 text-cyan-400 flex items-center justify-center group-hover:bg-cyan-500 group-hover:text-white transition-colors">
              <Mic className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black mt-2">{hosts.length}</div>
          <div className="flex items-center gap-1 text-[11px] text-cyan-400 font-semibold mt-1">
            <span>{activeHosts.length} approved active</span>
          </div>
        </div>

        {/* Total Agencies */}
        <div
          onClick={() => onNavigate('agents')}
          className={`p-4 rounded-2xl border cursor-pointer hover:scale-[1.01] transition-all group ${cardBg}`}
        >
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>Agencies / BD</span>
            <div className="w-8 h-8 rounded-xl bg-amber-500/20 text-amber-400 flex items-center justify-center group-hover:bg-amber-500 group-hover:text-white transition-colors">
              <Briefcase className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black mt-2">{agents.length}</div>
          <div className="text-[11px] text-amber-400 font-semibold mt-1">
            {agents.reduce((a, b) => a + b.hostCount, 0)} talent managed
          </div>
        </div>

        {/* Total Revenue */}
        <div
          onClick={() => onNavigate('finance_pnl')}
          className={`p-4 rounded-2xl border cursor-pointer hover:scale-[1.01] transition-all group ${cardBg}`}
        >
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>Gross Inflow Revenue</span>
            <div className="w-8 h-8 rounded-xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center group-hover:bg-emerald-500 group-hover:text-white transition-colors">
              <DollarSign className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black mt-2 text-emerald-400">
            ${totalRevenueUsd.toLocaleString()}
          </div>
          <div className="flex items-center gap-1 text-[11px] text-emerald-400 font-semibold mt-1">
            <ArrowUpRight className="w-3 h-3" />
            <span>+18.4% this month</span>
          </div>
        </div>

        {/* Total Coins in Vault */}
        <div
          onClick={() => onNavigate('economy')}
          className={`p-4 rounded-2xl border cursor-pointer hover:scale-[1.01] transition-all group ${cardBg}`}
        >
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>Coins in Circulation</span>
            <div className="w-8 h-8 rounded-xl bg-amber-500/20 text-amber-400 flex items-center justify-center group-hover:bg-amber-500 group-hover:text-white transition-colors">
              <Coins className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black mt-2 text-amber-300">
            {totalCoins.toLocaleString()}
          </div>
          <div className="text-[11px] text-slate-400 mt-1">1 USD = 30,000 Coins</div>
        </div>

        {/* Creator Diamonds */}
        <div
          onClick={() => onNavigate('withdrawals')}
          className={`p-4 rounded-2xl border cursor-pointer hover:scale-[1.01] transition-all group ${cardBg}`}
        >
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>Creator Diamonds</span>
            <div className="w-8 h-8 rounded-xl bg-cyan-500/20 text-cyan-400 flex items-center justify-center group-hover:bg-cyan-500 group-hover:text-white transition-colors">
              <Gem className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black mt-2 text-cyan-300">
            {totalDiamonds.toLocaleString()}
          </div>
          <div className="text-[11px] text-slate-400 mt-1">
            ${pendingWithdrawalUsd.toFixed(2)} pending payout
          </div>
        </div>

        {/* VIP Members */}
        <div
          onClick={() => onNavigate('vip')}
          className={`p-4 rounded-2xl border cursor-pointer hover:scale-[1.01] transition-all group ${cardBg}`}
        >
          <div className="flex items-center justify-between text-xs font-bold text-slate-400">
            <span>VIP Subscriptions</span>
            <div className="w-8 h-8 rounded-xl bg-purple-500/20 text-purple-400 flex items-center justify-center group-hover:bg-purple-500 group-hover:text-white transition-colors">
              <Crown className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black mt-2 text-purple-300">{vipUsersCount}</div>
          <div className="text-[11px] text-purple-400 mt-1">VIP 1 to VIP 7 active</div>
        </div>
      </div>

      {/* REVENUE & COIN FLOW VISUAL BARS */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        {/* Weekly Revenue Trend Bar Chart Simulation */}
        <div className={`p-5 rounded-2xl border lg:col-span-2 ${cardBg}`}>
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-bold flex items-center gap-2">
                <TrendingUp className="w-4 h-4 text-emerald-400" />
                <span>Revenue & Coin Purchasing Velocity</span>
              </h3>
              <p className="text-xs text-slate-400">Daily transaction volume over the past 7 days</p>
            </div>
            <span className="text-xs font-mono font-bold text-emerald-400 bg-emerald-500/10 px-2.5 py-1 rounded-full border border-emerald-500/30">
              Avg: $420/day
            </span>
          </div>

          <div className="h-44 flex items-end justify-between gap-3 pt-4 px-2">
            {[
              { day: 'Mon', rev: 320, coins: '9.6M' },
              { day: 'Tue', rev: 450, coins: '13.5M' },
              { day: 'Wed', rev: 380, coins: '11.4M' },
              { day: 'Thu', rev: 520, coins: '15.6M' },
              { day: 'Fri', rev: 680, coins: '20.4M' },
              { day: 'Sat', rev: 890, coins: '26.7M' },
              { day: 'Sun', rev: 740, coins: '22.2M' },
            ].map((item, idx) => {
              const heightPercent = Math.min(100, Math.max(20, (item.rev / 900) * 100));
              return (
                <div key={idx} className="flex-1 flex flex-col items-center gap-2 h-full justify-end group">
                  <span className="text-[10px] font-mono text-slate-400 group-hover:text-emerald-400 transition-colors">
                    ${item.rev}
                  </span>
                  <div className="w-full max-w-[42px] bg-slate-700/30 rounded-t-xl overflow-hidden relative flex items-end h-full">
                    <div
                      style={{ height: `${heightPercent}%` }}
                      className="w-full bg-gradient-to-t from-[#7c4dff] to-[#00e5ff] rounded-t-xl group-hover:from-emerald-500 group-hover:to-cyan-400 transition-all duration-300"
                    />
                  </div>
                  <span className="text-[11px] font-bold text-slate-400">{item.day}</span>
                </div>
              );
            })}
          </div>
        </div>

        {/* Pending Action Alerts Box */}
        <div className={`p-5 rounded-2xl border flex flex-col justify-between ${cardBg}`}>
          <div>
            <h3 className="text-sm font-bold flex items-center gap-2 mb-1">
              <AlertTriangle className="w-4 h-4 text-amber-400" />
              <span>Pending Operational Tasks</span>
            </h3>
            <p className="text-xs text-slate-400 mb-4">Requires immediate review from staff</p>

            <div className="space-y-2.5">
              <div
                onClick={() => onNavigate('withdrawals')}
                className={`p-3 rounded-xl border flex items-center justify-between cursor-pointer hover:border-amber-400 transition-colors ${
                  isDarkMode ? 'bg-[#180e30] border-[#351e5e]' : 'bg-slate-50 border-slate-200'
                }`}
              >
                <div>
                  <div className="text-xs font-bold text-amber-300">Pending Withdrawals</div>
                  <div className="text-[11px] text-slate-400">
                    {withdrawals.filter((w) => w.status === 'PENDING').length} requests awaiting payout
                  </div>
                </div>
                <span className="px-2 py-0.5 rounded-full bg-amber-500/20 text-amber-400 text-xs font-black">
                  ${pendingWithdrawalUsd.toFixed(0)}
                </span>
              </div>

              <div
                onClick={() => onNavigate('reports')}
                className={`p-3 rounded-xl border flex items-center justify-between cursor-pointer hover:border-rose-400 transition-colors ${
                  isDarkMode ? 'bg-[#180e30] border-[#351e5e]' : 'bg-slate-50 border-slate-200'
                }`}
              >
                <div>
                  <div className="text-xs font-bold text-rose-300">User & Room Reports</div>
                  <div className="text-[11px] text-slate-400">{unresolvedReports.length} tickets open</div>
                </div>
                <span className="px-2 py-0.5 rounded-full bg-rose-500/20 text-rose-400 text-xs font-black">
                  {unresolvedReports.length} Pending
                </span>
              </div>

              <div
                onClick={() => onNavigate('hosts')}
                className={`p-3 rounded-xl border flex items-center justify-between cursor-pointer hover:border-cyan-400 transition-colors ${
                  isDarkMode ? 'bg-[#180e30] border-[#351e5e]' : 'bg-slate-50 border-slate-200'
                }`}
              >
                <div>
                  <div className="text-xs font-bold text-cyan-300">Host Auditions</div>
                  <div className="text-[11px] text-slate-400">
                    {hosts.filter((h) => h.status === 'PENDING').length} applications awaiting decision
                  </div>
                </div>
                <span className="px-2 py-0.5 rounded-full bg-cyan-500/20 text-cyan-400 text-xs font-black">
                  {hosts.filter((h) => h.status === 'PENDING').length} Review
                </span>
              </div>
            </div>
          </div>

          <button
            onClick={() => onNavigate('owner_control')}
            className="w-full mt-4 py-2 px-3 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 text-white font-bold text-xs shadow-md hover:opacity-95 transition-all text-center"
          >
            Launch Owner Master Switchboard →
          </button>
        </div>
      </div>

      {/* QUICK JUMP SHORTCUTS BAR */}
      <div className={`p-4 rounded-2xl border ${cardBg}`}>
        <div className="text-xs font-extrabold uppercase tracking-wider text-slate-400 mb-3">
          Quick Administrative Access
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-6 gap-2">
          {[
            { label: 'Manage Users', module: 'users' as AdminNavModule, icon: Users },
            { label: 'Live Stages', module: 'live_monitoring' as AdminNavModule, icon: Radio },
            { label: 'Add Agency', module: 'agents' as AdminNavModule, icon: Briefcase },
            { label: 'Approve Payouts', module: 'withdrawals' as AdminNavModule, icon: CreditCard },
            { label: 'Currency Rates', module: 'economy' as AdminNavModule, icon: DollarSign },
            { label: 'System Settings', module: 'settings_health' as AdminNavModule, icon: ShieldCheck },
          ].map((sc, i) => {
            const Icon = sc.icon;
            return (
              <button
                key={i}
                onClick={() => onNavigate(sc.module)}
                className={`p-2.5 rounded-xl border flex items-center gap-2 text-xs font-bold transition-all ${
                  isDarkMode
                    ? 'bg-[#190e33] border-[#361e5b] hover:bg-[#25154b] text-slate-200'
                    : 'bg-slate-50 border-slate-200 hover:bg-slate-100 text-slate-700'
                }`}
              >
                <Icon className="w-3.5 h-3.5 text-purple-400" />
                <span className="truncate">{sc.label}</span>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
};
