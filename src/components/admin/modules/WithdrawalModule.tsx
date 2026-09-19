import React, { useState, useEffect } from 'react';
import {
  CreditCard,
  Search,
  CheckCircle2,
  XCircle,
  Clock,
  DollarSign,
  Gem,
  Download,
  Filter,
  AlertTriangle,
  FileCheck,
  Building,
  Smartphone,
  ExternalLink,
} from 'lucide-react';
import { WithdrawalRequest } from '../../../types/admin';
import { adminBackend } from '../../../services/adminBackendService';

interface WithdrawalModuleProps {
  isDarkMode: boolean;
}

export const WithdrawalModule: React.FC<WithdrawalModuleProps> = ({ isDarkMode }) => {
  const [withdrawals, setWithdrawals] = useState<WithdrawalRequest[]>(adminBackend.getWithdrawals());
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [selectedRequest, setSelectedRequest] = useState<WithdrawalRequest | null>(null);
  const [txRefInput, setTxRefInput] = useState('');
  const [rejectReasonInput, setRejectReasonInput] = useState('');
  const [notice, setNotice] = useState<string | null>(null);

  useEffect(() => {
    const sync = () => setWithdrawals(adminBackend.getWithdrawals());
    return adminBackend.subscribe(sync);
  }, []);

  const filteredWithdrawals = withdrawals.filter((w) => {
    const matchesSearch =
      w.userName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      w.id.toLowerCase().includes(searchQuery.toLowerCase()) ||
      w.accountDetails.accountNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      w.paymentMethod.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || w.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const handleApprove = (reqId: string) => {
    adminBackend.processWithdrawal(reqId, 'APPROVED', 'admin_1', 'Finance Officer', 'Approved for dispatch');
    setNotice(`Withdrawal request approved! Moving to dispatch.`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleComplete = (reqId: string) => {
    const ref = txRefInput.trim() || `TX-${Date.now().toString().slice(-6)}`;
    adminBackend.processWithdrawal(reqId, 'COMPLETED', 'admin_1', 'Finance Officer', 'Disbursed', ref);
    setTxRefInput('');
    setSelectedRequest(null);
    setNotice(`Withdrawal completed! Payment reference: ${ref}`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleReject = (reqId: string) => {
    const reason = rejectReasonInput.trim() || 'Bank account details mismatch or suspicious activity';
    adminBackend.processWithdrawal(reqId, 'REJECTED', 'admin_1', 'Finance Officer', reason);
    setRejectReasonInput('');
    setSelectedRequest(null);
    setNotice(`Withdrawal rejected. Creator diamonds safely refunded.`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleExportCsv = () => {
    const data = filteredWithdrawals.map((w) => ({
      RequestID: w.id,
      Username: w.userName,
      Diamonds: w.amountDiamonds,
      AmountUSD: w.amountUsd,
      FeeUSD: w.feeUsd,
      FinalUSD: w.netPayoutUsd,
      Method: w.paymentMethod,
      AccountTitle: w.accountDetails.accountTitle,
      AccountNumber: w.accountDetails.accountNumber,
      Status: w.status,
      TxRef: w.transactionRef || 'N/A',
      RequestDate: new Date(w.requestedAt).toISOString(),
    }));
    adminBackend.exportCsv('aura_withdrawals_ledger', data);
  };

  const totalPendingUsd = withdrawals
    .filter((w) => w.status === 'PENDING')
    .reduce((acc, w) => acc + w.amountUsd, 0);

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <CreditCard className="w-6 h-6 text-amber-400" />
            <span>Withdrawal Requests & Creator Payouts</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Audit diamond cashouts, verify bank/Easypaisa accounts, approve disbursements, and maintain remittance compliance.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <span className="px-3 py-1 rounded-full text-xs font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30">
            Pending Queue: ${totalPendingUsd.toFixed(2)} USD
          </span>

          <button
            onClick={handleExportCsv}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-bold transition-all shadow-xs ${
              isDarkMode
                ? 'border-amber-500/40 bg-amber-500/10 text-amber-300 hover:bg-amber-500/20'
                : 'border-amber-300 bg-amber-50 text-amber-700 hover:bg-amber-100'
            }`}
          >
            <Download className="w-3.5 h-3.5" />
            <span>Export CSV</span>
          </button>
        </div>
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
            placeholder="Search by creator, ID, account, method..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-transparent border-none outline-none text-xs"
          />
        </div>

        <div className="flex items-center gap-2 text-xs">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className={`px-3 py-2 rounded-xl border font-semibold outline-none ${
              isDarkMode ? 'bg-[#180e30] border-[#361f5c] text-slate-200' : 'bg-slate-100 border-slate-200 text-slate-800'
            }`}
          >
            <option value="ALL">All Statuses</option>
            <option value="PENDING">Pending Review</option>
            <option value="APPROVED">Approved (Ready for Dispatch)</option>
            <option value="COMPLETED">Completed</option>
            <option value="REJECTED">Rejected</option>
          </select>
        </div>
      </div>

      {/* WITHDRAWALS TABLE */}
      <div className={`rounded-2xl border overflow-hidden ${cardBg}`}>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead
              className={`border-b text-[11px] font-extrabold uppercase tracking-wider ${
                isDarkMode ? 'bg-[#180d33] border-[#2f1954] text-slate-400' : 'bg-slate-100 border-slate-200 text-slate-600'
              }`}
            >
              <tr>
                <th className="py-3 px-4">Request / User</th>
                <th className="py-3 px-4">Diamonds & USD</th>
                <th className="py-3 px-4">Disbursement Method</th>
                <th className="py-3 px-4">Account Information</th>
                <th className="py-3 px-4">Status & Reference</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-inherit">
              {filteredWithdrawals.length === 0 ? (
                <tr>
                  <td colSpan={6} className="py-8 text-center text-slate-400">
                    No withdrawal requests matching criteria.
                  </td>
                </tr>
              ) : (
                filteredWithdrawals.map((w) => (
                  <tr
                    key={w.id}
                    className={`transition-colors ${
                      isDarkMode ? 'hover:bg-[#1a0f38]' : 'hover:bg-slate-50'
                    }`}
                  >
                    <td className="py-3 px-4">
                      <div className="font-extrabold text-white">{w.userName}</div>
                      <div className="text-[10px] text-slate-400 font-mono">REQ-{w.id}</div>
                      <div className="text-[10px] text-slate-500">
                        {new Date(w.requestedAt).toLocaleDateString()}
                      </div>
                    </td>

                    <td className="py-3 px-4">
                      <div className="font-bold text-cyan-300 flex items-center gap-1">
                        <Gem className="w-3 h-3" />
                        <span>{w.amountDiamonds.toLocaleString()}</span>
                      </div>
                      <div className="font-black text-emerald-400 text-sm mt-0.5">
                        ${w.netPayoutUsd.toFixed(2)} USD
                      </div>
                      <div className="text-[10px] text-slate-500">Fee: ${w.feeUsd.toFixed(2)}</div>
                    </td>

                    <td className="py-3 px-4">
                      <div className="font-bold text-slate-200 flex items-center gap-1.5">
                        {w.paymentMethod.includes('Bank') ? (
                          <Building className="w-3.5 h-3.5 text-blue-400" />
                        ) : (
                          <Smartphone className="w-3.5 h-3.5 text-emerald-400" />
                        )}
                        <span>{w.paymentMethod}</span>
                      </div>
                    </td>

                    <td className="py-3 px-4">
                      <div className="font-bold text-slate-200">{w.accountDetails.accountTitle}</div>
                      <div className="text-[11px] font-mono text-slate-400">{w.accountDetails.accountNumber}</div>
                    </td>

                    <td className="py-3 px-4">
                      {w.status === 'PENDING' && (
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-amber-500/20 text-amber-400 border border-amber-500/30">
                          PENDING
                        </span>
                      )}
                      {w.status === 'APPROVED' && (
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-blue-500/20 text-blue-400 border border-blue-500/30">
                          APPROVED
                        </span>
                      )}
                      {w.status === 'COMPLETED' && (
                        <div>
                          <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                            COMPLETED
                          </span>
                          {w.transactionRef && (
                            <div className="text-[10px] font-mono text-slate-400 mt-0.5">
                              Ref: {w.transactionRef}
                            </div>
                          )}
                        </div>
                      )}
                      {w.status === 'REJECTED' && (
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-rose-500/20 text-rose-400 border border-rose-500/30">
                          REJECTED
                        </span>
                      )}
                    </td>

                    <td className="py-3 px-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        {w.status === 'PENDING' && (
                          <>
                            <button
                              onClick={() => handleApprove(w.id)}
                              className="px-2.5 py-1 rounded-lg bg-blue-600 hover:bg-blue-500 text-white font-bold text-[11px]"
                            >
                              Approve
                            </button>
                            <button
                              onClick={() => handleReject(w.id)}
                              className="px-2.5 py-1 rounded-lg bg-rose-500/20 text-rose-300 hover:bg-rose-500/30 font-bold text-[11px]"
                            >
                              Reject
                            </button>
                          </>
                        )}

                        {w.status === 'APPROVED' && (
                          <button
                            onClick={() => handleComplete(w.id)}
                            className="px-2.5 py-1 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-[11px]"
                          >
                            Mark Completed
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
