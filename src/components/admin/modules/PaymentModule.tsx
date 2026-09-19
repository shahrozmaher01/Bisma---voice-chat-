import React, { useState, useEffect } from 'react';
import {
  Activity,
  Search,
  Download,
  CheckCircle2,
  XCircle,
  RotateCcw,
  Coins,
  DollarSign,
  Smartphone,
  CreditCard,
} from 'lucide-react';
import { PaymentTransaction } from '../../../types/admin';
import { adminBackend } from '../../../services/adminBackendService';

interface PaymentModuleProps {
  isDarkMode: boolean;
}

export const PaymentModule: React.FC<PaymentModuleProps> = ({ isDarkMode }) => {
  const [payments, setPayments] = useState<PaymentTransaction[]>(adminBackend.getPayments());
  const [searchQuery, setSearchQuery] = useState('');
  const [gatewayFilter, setGatewayFilter] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [notice, setNotice] = useState<string | null>(null);

  useEffect(() => {
    const sync = () => setPayments(adminBackend.getPayments());
    return adminBackend.subscribe(sync);
  }, []);

  const filteredPayments = payments.filter((p) => {
    const matchesSearch =
      p.userName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.paymentGatewayRef.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.id.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesGateway = gatewayFilter === 'ALL' || p.provider === gatewayFilter;
    const matchesStatus = statusFilter === 'ALL' || p.status === statusFilter;
    return matchesSearch && matchesGateway && matchesStatus;
  });

  const handleRefund = (tx: PaymentTransaction) => {
    adminBackend.refundPayment(tx.id, 'Customer requested transaction rollback');
    setNotice(`Transaction ${tx.paymentGatewayRef} marked as refunded.`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleExportCsv = () => {
    const data = filteredPayments.map((p) => ({
      TxID: p.id,
      GatewayRef: p.paymentGatewayRef,
      User: p.userName,
      Coins: p.coinsAwarded,
      AmountUSD: p.amountUsd,
      Gateway: p.provider,
      Status: p.status,
      IP: p.ipAddress,
      Timestamp: new Date(p.timestamp).toISOString(),
    }));
    adminBackend.exportCsv('aura_payment_transactions', data);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <Activity className="w-6 h-6 text-emerald-400" />
            <span>Inbound Gateway Payments & Top-Ups</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Monitor Google Play, Stripe, EasyPaisa, JazzCash, and Crypto deposits in real-time.
          </p>
        </div>

        <button
          onClick={handleExportCsv}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-bold transition-all shadow-xs ${
            isDarkMode
              ? 'border-emerald-500/40 bg-emerald-500/10 text-emerald-300 hover:bg-emerald-500/20'
              : 'border-emerald-300 bg-emerald-50 text-emerald-700 hover:bg-emerald-100'
          }`}
        >
          <Download className="w-3.5 h-3.5" />
          <span>Export Payments CSV</span>
        </button>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* FILTER BAR */}
      <div className={`p-4 rounded-2xl border flex flex-col sm:flex-row gap-3 items-center justify-between ${cardBg}`}>
        <div
          className={`w-full sm:w-80 flex items-center gap-2 px-3 py-2 rounded-xl border text-xs ${
            isDarkMode ? 'bg-[#180e30] border-[#361f5c]' : 'bg-slate-100 border-slate-200'
          }`}
        >
          <Search className="w-4 h-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search by gateway ref, user..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-transparent border-none outline-none text-xs"
          />
        </div>

        <div className="flex items-center gap-2 text-xs">
          <select
            value={gatewayFilter}
            onChange={(e) => setGatewayFilter(e.target.value)}
            className={`px-3 py-2 rounded-xl border font-semibold outline-none ${
              isDarkMode ? 'bg-[#180e30] border-[#361f5c] text-slate-200' : 'bg-slate-100 border-slate-200 text-slate-800'
            }`}
          >
            <option value="ALL">All Gateways</option>
            <option value="Google Play IAP">Google Play</option>
            <option value="EasyPaisa">EasyPaisa</option>
            <option value="JazzCash">JazzCash</option>
            <option value="Stripe">Stripe</option>
            <option value="Crypto">Crypto</option>
          </select>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className={`px-3 py-2 rounded-xl border font-semibold outline-none ${
              isDarkMode ? 'bg-[#180e30] border-[#361f5c] text-slate-200' : 'bg-slate-100 border-slate-200 text-slate-800'
            }`}
          >
            <option value="ALL">All Statuses</option>
            <option value="SUCCESS">Success Only</option>
            <option value="REFUNDED">Refunded</option>
            <option value="FAILED">Failed</option>
          </select>
        </div>
      </div>

      {/* TABLE */}
      <div className={`rounded-2xl border overflow-hidden ${cardBg}`}>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead
              className={`border-b text-[11px] font-extrabold uppercase tracking-wider ${
                isDarkMode ? 'bg-[#180d33] border-[#2f1954] text-slate-400' : 'bg-slate-100 border-slate-200 text-slate-600'
              }`}
            >
              <tr>
                <th className="py-3 px-4">Gateway Ref & Date</th>
                <th className="py-3 px-4">Purchaser</th>
                <th className="py-3 px-4">Coins Credited</th>
                <th className="py-3 px-4">Amount USD</th>
                <th className="py-3 px-4">Gateway</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-inherit">
              {filteredPayments.map((p) => (
                <tr
                  key={p.id}
                  className={`transition-colors ${
                    isDarkMode ? 'hover:bg-[#1a0f38]' : 'hover:bg-slate-50'
                  }`}
                >
                  <td className="py-3 px-4">
                    <div className="font-mono font-bold text-white">{p.paymentGatewayRef}</div>
                    <div className="text-[10px] text-slate-500 font-sans">
                      {new Date(p.timestamp).toLocaleString()}
                    </div>
                  </td>

                  <td className="py-3 px-4 font-bold text-slate-200">{p.userName}</td>

                  <td className="py-3 px-4 font-bold text-amber-300">
                    +{p.coinsAwarded.toLocaleString()} Coins
                  </td>

                  <td className="py-3 px-4 font-black text-emerald-400 text-sm">
                    ${p.amountUsd.toFixed(2)}
                  </td>

                  <td className="py-3 px-4">
                    <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-purple-500/20 text-purple-300 border border-purple-500/30">
                      {p.provider}
                    </span>
                  </td>

                  <td className="py-3 px-4">
                    {p.status === 'SUCCESS' && (
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                        SUCCESS
                      </span>
                    )}
                    {p.status === 'REFUNDED' && (
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-amber-500/20 text-amber-400 border border-amber-500/30">
                        REFUNDED
                      </span>
                    )}
                    {p.status === 'FAILED' && (
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-rose-500/20 text-rose-400 border border-rose-500/30">
                        FAILED
                      </span>
                    )}
                  </td>

                  <td className="py-3 px-4 text-right">
                    {p.status === 'SUCCESS' && (
                      <button
                        onClick={() => handleRefund(p)}
                        className="p-1.5 rounded-lg border border-rose-500/30 text-rose-300 hover:bg-rose-500/20"
                        title="Process Refund"
                      >
                        <RotateCcw className="w-3.5 h-3.5" />
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
