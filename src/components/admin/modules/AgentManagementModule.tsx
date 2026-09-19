import React, { useState, useEffect } from 'react';
import {
  Briefcase,
  Search,
  Plus,
  Download,
  CheckCircle2,
  Users,
  DollarSign,
  Edit,
  ShieldAlert,
  Percent,
  X,
  Phone,
  Mail,
  Globe,
} from 'lucide-react';
import { AgentProfile } from '../../../types/admin';
import { adminBackend } from '../../../services/adminBackendService';

interface AgentManagementModuleProps {
  isDarkMode: boolean;
}

export const AgentManagementModule: React.FC<AgentManagementModuleProps> = ({ isDarkMode }) => {
  const [agents, setAgents] = useState<AgentProfile[]>(adminBackend.getAgents());
  const [searchQuery, setSearchQuery] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [selectedAgent, setSelectedAgent] = useState<AgentProfile | null>(null);
  const [showEditModal, setShowEditModal] = useState(false);
  const [notice, setNotice] = useState<string | null>(null);

  // New Agency Form
  const [name, setName] = useState('');
  const [code, setCode] = useState('');
  const [ownerUserId, setOwnerUserId] = useState('');
  const [ownerUsername, setOwnerUsername] = useState('');
  const [commissionPercent, setCommissionPercent] = useState('12');
  const [tier, setTier] = useState<AgentProfile['tier']>('Gold Platinum');
  const [country, setCountry] = useState('🇵🇰 Pakistan');
  const [contactEmail, setContactEmail] = useState('');
  const [contactPhone, setContactPhone] = useState('');

  useEffect(() => {
    const sync = () => setAgents(adminBackend.getAgents());
    return adminBackend.subscribe(sync);
  }, []);

  const filteredAgents = agents.filter((a) => {
    return (
      a.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      a.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
      a.ownerUsername.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  const handleCreateAgent = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !code.trim() || !ownerUsername.trim()) return;

    adminBackend.createAgent({
      name: name.trim(),
      code: code.trim().toUpperCase(),
      ownerUserId: ownerUserId.trim() || `usr_${Date.now().toString().slice(-5)}`,
      ownerUsername: ownerUsername.trim(),
      avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300',
      status: 'ACTIVE',
      commissionPercent: parseInt(commissionPercent, 10) || 10,
      tier,
      country,
      contactEmail: contactEmail.trim(),
      contactPhone: contactPhone.trim(),
      assignedHostIds: [],
    });

    setShowCreateModal(false);
    setName('');
    setCode('');
    setOwnerUsername('');
    setNotice(`Created new agency: ${name.trim()} (${code.trim().toUpperCase()})`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleToggleSuspend = (agent: AgentProfile) => {
    const nextStatus = agent.status === 'SUSPENDED' ? 'ACTIVE' : 'SUSPENDED';
    adminBackend.updateAgent(agent.id, { status: nextStatus });
    setNotice(`Agency ${agent.name} status updated to ${nextStatus}.`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleExportCsv = () => {
    const data = filteredAgents.map((a) => ({
      ID: a.id,
      Code: a.code,
      Name: a.name,
      Owner: a.ownerUsername,
      Status: a.status,
      HostCount: a.hostCount,
      CommissionPercent: `${a.commissionPercent}%`,
      TotalEarningsUsd: a.totalEarningsUsd,
      PendingPayoutUsd: a.pendingPayoutUsd,
      Tier: a.tier,
      Country: a.country,
      CreatedDate: a.createdDate,
    }));
    adminBackend.exportCsv('aura_agencies_roster', data);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <Briefcase className="w-6 h-6 text-amber-400" />
            <span>Agency Network & Partner Management</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Oversee talent agencies, recruit directors, allocate hosts, and monitor commission distributions.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => setShowCreateModal(true)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-gradient-to-r from-amber-500 to-amber-600 text-black font-extrabold text-xs shadow-md hover:opacity-95 transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Register Agency</span>
          </button>

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

      {/* SEARCH BAR */}
      <div className={`p-4 rounded-2xl border flex items-center justify-between ${cardBg}`}>
        <div
          className={`w-full md:w-80 flex items-center gap-2 px-3 py-2 rounded-xl border text-xs ${
            isDarkMode ? 'bg-[#180e30] border-[#361f5c]' : 'bg-slate-100 border-slate-200'
          }`}
        >
          <Search className="w-4 h-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search agency by name, code, owner..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-transparent border-none outline-none text-xs"
          />
        </div>
      </div>

      {/* AGENCIES GRID / CARDS */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filteredAgents.map((agency) => (
          <div key={agency.id} className={`p-5 rounded-2xl border flex flex-col justify-between ${cardBg}`}>
            <div>
              {/* Card Header */}
              <div className="flex items-center justify-between pb-3 border-b border-inherit">
                <div className="flex items-center gap-3">
                  <img
                    src={agency.avatarUrl}
                    alt={agency.name}
                    className="w-12 h-12 rounded-2xl object-cover border border-amber-500/40"
                  />
                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="font-extrabold text-sm text-white">{agency.name}</h3>
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-mono font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30">
                        {agency.code}
                      </span>
                    </div>
                    <div className="text-[11px] text-slate-400">
                      Owner: <span className="text-cyan-300 font-semibold">{agency.ownerUsername}</span>
                    </div>
                  </div>
                </div>

                <span
                  className={`px-2 py-0.5 rounded-full text-[10px] font-black border ${
                    agency.status === 'ACTIVE'
                      ? 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30'
                      : 'bg-rose-500/20 text-rose-400 border-rose-500/30'
                  }`}
                >
                  {agency.status}
                </span>
              </div>

              {/* Stats row */}
              <div className="grid grid-cols-3 gap-2 py-3 text-xs">
                <div>
                  <div className="text-[10px] text-slate-400">Managed Hosts</div>
                  <div className="font-extrabold text-white text-base mt-0.5 flex items-center gap-1">
                    <Users className="w-3.5 h-3.5 text-cyan-400" />
                    <span>{agency.hostCount}</span>
                  </div>
                </div>

                <div>
                  <div className="text-[10px] text-slate-400">Commission %</div>
                  <div className="font-extrabold text-amber-300 text-base mt-0.5">
                    {agency.commissionPercent}%
                  </div>
                </div>

                <div>
                  <div className="text-[10px] text-slate-400">Total Revenue</div>
                  <div className="font-extrabold text-emerald-400 text-base mt-0.5">
                    ${agency.totalEarningsUsd.toLocaleString()}
                  </div>
                </div>
              </div>

              {/* Contact Info */}
              <div className="text-[11px] text-slate-400 space-y-1 pt-2 border-t border-inherit">
                <div className="flex items-center gap-1.5">
                  <Globe className="w-3 h-3 text-slate-500" />
                  <span>{agency.country}</span>
                  <span className="text-slate-600">•</span>
                  <span className="text-purple-300 font-bold">{agency.tier}</span>
                </div>
                <div className="flex items-center gap-3">
                  <span className="flex items-center gap-1 text-slate-300">
                    <Mail className="w-3 h-3 text-slate-500" />
                    <span>{agency.contactEmail}</span>
                  </span>
                  <span className="flex items-center gap-1 text-slate-300">
                    <Phone className="w-3 h-3 text-slate-500" />
                    <span>{agency.contactPhone}</span>
                  </span>
                </div>
              </div>
            </div>

            {/* Actions Footer */}
            <div className="flex items-center justify-between pt-4 mt-3 border-t border-inherit text-xs">
              <span className="text-[10px] text-slate-500">Registered: {agency.createdDate}</span>

              <div className="flex items-center gap-2">
                <button
                  onClick={() => handleToggleSuspend(agency)}
                  className={`px-3 py-1.5 rounded-xl border text-[11px] font-bold transition-colors ${
                    agency.status === 'SUSPENDED'
                      ? 'border-emerald-500/40 text-emerald-300 hover:bg-emerald-500/20'
                      : 'border-rose-500/40 text-rose-300 hover:bg-rose-500/20'
                  }`}
                >
                  {agency.status === 'SUSPENDED' ? 'Reactivate' : 'Suspend'}
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* CREATE AGENCY MODAL */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4">
          <div
            className={`w-full max-w-lg rounded-2xl border p-5 shadow-2xl ${
              isDarkMode ? 'bg-[#150a2b] border-[#391e63]' : 'bg-white border-slate-200'
            }`}
          >
            <div className="flex items-center justify-between pb-3 border-b border-inherit">
              <h3 className="text-sm font-bold flex items-center gap-2">
                <Briefcase className="w-4 h-4 text-amber-400" />
                <span>Register New Talent Agency</span>
              </h3>
              <button onClick={() => setShowCreateModal(false)} className="text-slate-400 hover:text-white">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleCreateAgent} className="space-y-3 pt-4 text-xs">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-400 font-bold mb-1">Agency Name</label>
                  <input
                    type="text"
                    required
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="e.g. Lahore Elite Talent"
                    className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                      isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                    }`}
                  />
                </div>

                <div>
                  <label className="block text-slate-400 font-bold mb-1">Agency Code</label>
                  <input
                    type="text"
                    required
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                    placeholder="e.g. LET-100"
                    className={`w-full px-3 py-2 rounded-xl border outline-none font-mono font-bold uppercase ${
                      isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                    }`}
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-400 font-bold mb-1">Director Username</label>
                  <input
                    type="text"
                    required
                    value={ownerUsername}
                    onChange={(e) => setOwnerUsername(e.target.value)}
                    placeholder="Username of agency director"
                    className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                      isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                    }`}
                  />
                </div>

                <div>
                  <label className="block text-slate-400 font-bold mb-1">Commission Rate (%)</label>
                  <input
                    type="number"
                    min={5}
                    max={40}
                    value={commissionPercent}
                    onChange={(e) => setCommissionPercent(e.target.value)}
                    className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                      isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                    }`}
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-400 font-bold mb-1">Agency Tier</label>
                  <select
                    value={tier}
                    onChange={(e) => setTier(e.target.value as any)}
                    className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                      isDarkMode ? 'bg-[#1b0d36] border-[#442377] text-white' : 'bg-slate-100 border-slate-300'
                    }`}
                  >
                    <option value="Diamond Elite">Diamond Elite</option>
                    <option value="Gold Platinum">Gold Platinum</option>
                    <option value="Silver Rising">Silver Rising</option>
                    <option value="Standard">Standard Partner</option>
                  </select>
                </div>

                <div>
                  <label className="block text-slate-400 font-bold mb-1">Country</label>
                  <input
                    type="text"
                    value={country}
                    onChange={(e) => setCountry(e.target.value)}
                    className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                      isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                    }`}
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-400 font-bold mb-1">Contact Email</label>
                  <input
                    type="email"
                    value={contactEmail}
                    onChange={(e) => setContactEmail(e.target.value)}
                    placeholder="agency@aura.live"
                    className={`w-full px-3 py-2 rounded-xl border outline-none ${
                      isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                    }`}
                  />
                </div>

                <div>
                  <label className="block text-slate-400 font-bold mb-1">Contact Phone</label>
                  <input
                    type="text"
                    value={contactPhone}
                    onChange={(e) => setContactPhone(e.target.value)}
                    placeholder="+92 300 0000000"
                    className={`w-full px-3 py-2 rounded-xl border outline-none ${
                      isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                    }`}
                  />
                </div>
              </div>

              <div className="flex items-center justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="px-4 py-2 rounded-xl border border-slate-500/40 text-slate-300 hover:bg-slate-500/20 font-bold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-gradient-to-r from-amber-500 to-amber-600 text-black font-extrabold shadow-md"
                >
                  Create Agency Contract
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
