import React, { useState } from 'react';
import {
  Users,
  Search,
  Filter,
  Download,
  Shield,
  Ban,
  Coins,
  Gem,
  Crown,
  Edit,
  CheckCircle2,
  X,
  Lock,
  Unlock,
  AlertCircle,
  Eye,
  Sliders,
  Award,
} from 'lucide-react';
import { useAura } from '../../../context/AuraContext';
import { User, AdminRoleType } from '../../../types';
import { adminBackend } from '../../../services/adminBackendService';

interface UserManagementModuleProps {
  isDarkMode: boolean;
}

export const UserManagementModule: React.FC<UserManagementModuleProps> = ({ isDarkMode }) => {
  const { allUsers, assignUserRole, banUser, adjustUserBalance, updateProfile } = useAura();

  const [searchQuery, setSearchQuery] = useState('');
  const [roleFilter, setRoleFilter] = useState<string>('ALL');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [vipFilter, setVipFilter] = useState<string>('ALL');

  // Modal states
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showBalanceModal, setShowBalanceModal] = useState(false);
  const [showRoleModal, setShowRoleModal] = useState(false);

  // Form states
  const [deltaCoins, setDeltaCoins] = useState('10000');
  const [deltaDiamonds, setDeltaDiamonds] = useState('500');
  const [adjustmentReason, setAdjustmentReason] = useState('Staff promotional grant');
  const [newRole, setNewRole] = useState<AdminRoleType>('User');
  const [editUsername, setEditUsername] = useState('');
  const [editBio, setEditBio] = useState('');
  const [editVip, setEditVip] = useState(0);
  const [actionSuccess, setActionSuccess] = useState<string | null>(null);

  const filteredUsers = allUsers.filter((u) => {
    const matchesSearch =
      u.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.id.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (u.email && u.email.toLowerCase().includes(searchQuery.toLowerCase())) ||
      (u.agencyName && u.agencyName.toLowerCase().includes(searchQuery.toLowerCase()));

    const matchesRole = roleFilter === 'ALL' || u.role === roleFilter;
    const matchesStatus =
      statusFilter === 'ALL' ||
      (statusFilter === 'BANNED' && u.isBanned) ||
      (statusFilter === 'ACTIVE' && !u.isBanned);
    const matchesVip =
      vipFilter === 'ALL' ||
      (vipFilter === 'VIP' && u.vipLevel > 0) ||
      (vipFilter === 'NON_VIP' && u.vipLevel === 0);

    return matchesSearch && matchesRole && matchesStatus && matchesVip;
  });

  const handleExportCsv = () => {
    const exportData = filteredUsers.map((u) => ({
      ID: u.id,
      Username: u.username,
      Role: u.role,
      Email: u.email || 'N/A',
      Country: u.country,
      Coins: u.coins,
      Diamonds: u.diamonds,
      VipLevel: u.vipLevel,
      UserLevel: u.userLevel,
      RichLevel: u.richLevel,
      CharmLevel: u.charmLevel,
      Status: u.isBanned ? 'BANNED' : 'ACTIVE',
      Agency: u.agencyName || 'None',
      CreatedAt: new Date(u.createdAt).toISOString(),
    }));
    adminBackend.exportCsv('aura_users_directory', exportData);
  };

  const handleApplyBalance = (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedUser) return;
    const coins = parseInt(deltaCoins, 10) || 0;
    const diamonds = parseInt(deltaDiamonds, 10) || 0;

    adjustUserBalance(selectedUser.id, coins, diamonds);
    setShowBalanceModal(false);
    setActionSuccess(`Successfully updated ${selectedUser.username}'s balance!`);
    setTimeout(() => setActionSuccess(null), 3500);
  };

  const handleApplyRole = (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedUser) return;
    assignUserRole(selectedUser.id, newRole);
    setShowRoleModal(false);
    setActionSuccess(`Assigned role "${newRole}" to ${selectedUser.username}!`);
    setTimeout(() => setActionSuccess(null), 3500);
  };

  const handleToggleBan = (user: User) => {
    const willBan = !user.isBanned;
    banUser(user.id, willBan);
    setActionSuccess(`User ${user.username} is now ${willBan ? 'BANNED' : 'UNBANNED'}.`);
    setTimeout(() => setActionSuccess(null), 3500);
  };

  const handleSaveProfileEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedUser) return;
    updateProfile({
      username: editUsername.trim() || selectedUser.username,
      bio: editBio.trim() || selectedUser.bio,
      vipLevel: editVip,
    });
    setShowEditModal(false);
    setActionSuccess(`Updated profile for ${selectedUser.username}.`);
    setTimeout(() => setActionSuccess(null), 3500);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Module Title Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <Users className="w-6 h-6 text-purple-400" />
            <span>User Management & Security Controls</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Search, inspect, adjust balances, assign roles, and administer account sanctions.
          </p>
        </div>

        <button
          onClick={handleExportCsv}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-bold transition-all shadow-xs ${
            isDarkMode
              ? 'border-purple-500/40 bg-purple-500/10 text-purple-300 hover:bg-purple-500/20'
              : 'border-purple-300 bg-purple-50 text-purple-700 hover:bg-purple-100'
          }`}
        >
          <Download className="w-3.5 h-3.5" />
          <span>Export Directory ({filteredUsers.length})</span>
        </button>
      </div>

      {/* Success Notification */}
      {actionSuccess && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{actionSuccess}</span>
        </div>
      )}

      {/* SEARCH & FILTERS BAR */}
      <div className={`p-4 rounded-2xl border flex flex-col md:flex-row gap-3 items-center justify-between ${cardBg}`}>
        {/* Search */}
        <div
          className={`w-full md:w-80 flex items-center gap-2 px-3 py-2 rounded-xl border text-xs ${
            isDarkMode ? 'bg-[#180e30] border-[#361f5c]' : 'bg-slate-100 border-slate-200'
          }`}
        >
          <Search className="w-4 h-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search by ID, name, email, agency..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-transparent border-none outline-none text-xs"
          />
          {searchQuery && (
            <button onClick={() => setSearchQuery('')} className="text-slate-400 hover:text-white">
              <X className="w-3.5 h-3.5" />
            </button>
          )}
        </div>

        {/* Filter Dropdowns */}
        <div className="w-full md:w-auto flex flex-wrap items-center gap-2 text-xs">
          <select
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            className={`px-3 py-2 rounded-xl border font-semibold outline-none ${
              isDarkMode ? 'bg-[#180e30] border-[#361f5c] text-slate-200' : 'bg-slate-100 border-slate-200 text-slate-800'
            }`}
          >
            <option value="ALL">All Roles</option>
            <option value="Super Admin">Super Admin</option>
            <option value="Admin">Admin</option>
            <option value="Host">Host</option>
            <option value="Agency Leader">Agency Leader</option>
            <option value="User">User</option>
          </select>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className={`px-3 py-2 rounded-xl border font-semibold outline-none ${
              isDarkMode ? 'bg-[#180e30] border-[#361f5c] text-slate-200' : 'bg-slate-100 border-slate-200 text-slate-800'
            }`}
          >
            <option value="ALL">All Statuses</option>
            <option value="ACTIVE">Active Only</option>
            <option value="BANNED">Banned Only</option>
          </select>

          <select
            value={vipFilter}
            onChange={(e) => setVipFilter(e.target.value)}
            className={`px-3 py-2 rounded-xl border font-semibold outline-none ${
              isDarkMode ? 'bg-[#180e30] border-[#361f5c] text-slate-200' : 'bg-slate-100 border-slate-200 text-slate-800'
            }`}
          >
            <option value="ALL">All VIP Tiers</option>
            <option value="VIP">VIP Members Only</option>
            <option value="NON_VIP">Regular Users</option>
          </select>
        </div>
      </div>

      {/* USERS DATA TABLE */}
      <div className={`rounded-2xl border overflow-hidden ${cardBg}`}>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead
              className={`border-b text-[11px] font-extrabold uppercase tracking-wider ${
                isDarkMode ? 'bg-[#180d33] border-[#2f1954] text-slate-400' : 'bg-slate-100 border-slate-200 text-slate-600'
              }`}
            >
              <tr>
                <th className="py-3 px-4">User</th>
                <th className="py-3 px-4">ID & Country</th>
                <th className="py-3 px-4">Role & Agency</th>
                <th className="py-3 px-4">Coins / Diamonds</th>
                <th className="py-3 px-4">VIP & Levels</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-inherit">
              {filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan={7} className="py-8 text-center text-slate-400">
                    No users matching criteria.
                  </td>
                </tr>
              ) : (
                filteredUsers.map((u) => (
                  <tr
                    key={u.id}
                    className={`transition-colors ${
                      isDarkMode ? 'hover:bg-[#1a0f38]' : 'hover:bg-slate-50'
                    }`}
                  >
                    {/* User info */}
                    <td className="py-3 px-4">
                      <div className="flex items-center gap-3">
                        <img
                          src={u.avatarUrl}
                          alt={u.username}
                          className="w-9 h-9 rounded-full object-cover border border-purple-500/40"
                        />
                        <div className="min-w-0">
                          <div className="font-bold flex items-center gap-1.5 truncate">
                            <span>{u.username}</span>
                            {u.vipLevel > 0 && (
                              <span className="text-[10px] font-black text-amber-400 bg-amber-500/20 px-1 rounded border border-amber-500/30">
                                VIP {u.vipLevel}
                              </span>
                            )}
                          </div>
                          <div className="text-[10px] text-slate-400 truncate">{u.email || 'No email registered'}</div>
                        </div>
                      </div>
                    </td>

                    {/* ID & Country */}
                    <td className="py-3 px-4 font-mono text-[11px]">
                      <div>{u.id}</div>
                      <div className="text-[10px] text-slate-400 font-sans">{u.country}</div>
                    </td>

                    {/* Role & Agency */}
                    <td className="py-3 px-4">
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-purple-500/20 text-purple-300 border border-purple-500/30">
                        {u.role}
                      </span>
                      {u.agencyName && (
                        <div className="text-[10px] text-slate-400 mt-1 truncate">🏢 {u.agencyName}</div>
                      )}
                    </td>

                    {/* Coins / Diamonds */}
                    <td className="py-3 px-4">
                      <div className="flex items-center gap-1 text-amber-400 font-bold">
                        <Coins className="w-3 h-3" />
                        <span>{u.coins.toLocaleString()}</span>
                      </div>
                      <div className="flex items-center gap-1 text-cyan-400 font-bold mt-0.5">
                        <Gem className="w-3 h-3" />
                        <span>{u.diamonds.toLocaleString()}</span>
                      </div>
                    </td>

                    {/* VIP & Levels */}
                    <td className="py-3 px-4">
                      <div className="text-[11px] font-bold">User Lv.{u.userLevel}</div>
                      <div className="text-[10px] text-slate-400">
                        Rich Lv.{u.richLevel} • Charm Lv.{u.charmLevel}
                      </div>
                    </td>

                    {/* Status */}
                    <td className="py-3 px-4">
                      {u.isBanned ? (
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-rose-500/20 text-rose-400 border border-rose-500/30">
                          BANNED
                        </span>
                      ) : (
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                          ACTIVE
                        </span>
                      )}
                    </td>

                    {/* Actions */}
                    <td className="py-3 px-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        {/* Adjust Balance */}
                        <button
                          onClick={() => {
                            setSelectedUser(u);
                            setShowBalanceModal(true);
                          }}
                          className="p-1.5 rounded-lg border border-amber-500/40 text-amber-300 hover:bg-amber-500/20 transition-colors"
                          title="Adjust Coins & Diamonds"
                        >
                          <Coins className="w-3.5 h-3.5" />
                        </button>

                        {/* Assign Role */}
                        <button
                          onClick={() => {
                            setSelectedUser(u);
                            setNewRole(u.role);
                            setShowRoleModal(true);
                          }}
                          className="p-1.5 rounded-lg border border-purple-500/40 text-purple-300 hover:bg-purple-500/20 transition-colors"
                          title="Assign Role"
                        >
                          <Shield className="w-3.5 h-3.5" />
                        </button>

                        {/* Ban / Unban */}
                        <button
                          onClick={() => handleToggleBan(u)}
                          className={`p-1.5 rounded-lg border transition-colors ${
                            u.isBanned
                              ? 'border-emerald-500/40 text-emerald-300 hover:bg-emerald-500/20'
                              : 'border-rose-500/40 text-rose-300 hover:bg-rose-500/20'
                          }`}
                          title={u.isBanned ? 'Unban Account' : 'Ban Account'}
                        >
                          {u.isBanned ? <Unlock className="w-3.5 h-3.5" /> : <Ban className="w-3.5 h-3.5" />}
                        </button>

                        {/* Edit Profile */}
                        <button
                          onClick={() => {
                            setSelectedUser(u);
                            setEditUsername(u.username);
                            setEditBio(u.bio);
                            setEditVip(u.vipLevel);
                            setShowEditModal(true);
                          }}
                          className="p-1.5 rounded-lg border border-slate-500/40 text-slate-300 hover:bg-slate-500/20 transition-colors"
                          title="Edit Profile"
                        >
                          <Edit className="w-3.5 h-3.5" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* MODAL: ADJUST BALANCE */}
      {showBalanceModal && selectedUser && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4">
          <div
            className={`w-full max-w-md rounded-2xl border p-5 shadow-2xl ${
              isDarkMode ? 'bg-[#150a2b] border-[#391e63]' : 'bg-white border-slate-200'
            }`}
          >
            <div className="flex items-center justify-between pb-3 border-b border-inherit">
              <h3 className="text-sm font-bold flex items-center gap-2">
                <Coins className="w-4 h-4 text-amber-400" />
                <span>Adjust Balances: {selectedUser.username}</span>
              </h3>
              <button onClick={() => setShowBalanceModal(false)} className="text-slate-400 hover:text-white">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleApplyBalance} className="space-y-4 pt-4 text-xs">
              <div>
                <label className="block text-slate-400 font-bold mb-1">
                  Add/Deduct Coins (e.g. +30000 or -5000)
                </label>
                <input
                  type="number"
                  value={deltaCoins}
                  onChange={(e) => setDeltaCoins(e.target.value)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none font-mono ${
                    isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                  }`}
                />
              </div>

              <div>
                <label className="block text-slate-400 font-bold mb-1">
                  Add/Deduct Diamonds (e.g. +1000 or -200)
                </label>
                <input
                  type="number"
                  value={deltaDiamonds}
                  onChange={(e) => setDeltaDiamonds(e.target.value)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none font-mono ${
                    isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                  }`}
                />
              </div>

              <div>
                <label className="block text-slate-400 font-bold mb-1">Audit Log Reason / Notes</label>
                <input
                  type="text"
                  value={adjustmentReason}
                  onChange={(e) => setAdjustmentReason(e.target.value)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none ${
                    isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                  }`}
                  placeholder="Reason for adjustment..."
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowBalanceModal(false)}
                  className="px-4 py-2 rounded-xl border border-slate-500/40 text-slate-300 hover:bg-slate-500/20 font-bold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-gradient-to-r from-amber-500 to-amber-600 text-black font-extrabold shadow-md"
                >
                  Apply Balance Adjustment
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: ASSIGN ROLE */}
      {showRoleModal && selectedUser && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4">
          <div
            className={`w-full max-w-md rounded-2xl border p-5 shadow-2xl ${
              isDarkMode ? 'bg-[#150a2b] border-[#391e63]' : 'bg-white border-slate-200'
            }`}
          >
            <div className="flex items-center justify-between pb-3 border-b border-inherit">
              <h3 className="text-sm font-bold flex items-center gap-2">
                <Shield className="w-4 h-4 text-purple-400" />
                <span>Assign System Role: {selectedUser.username}</span>
              </h3>
              <button onClick={() => setShowRoleModal(false)} className="text-slate-400 hover:text-white">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleApplyRole} className="space-y-4 pt-4 text-xs">
              <div>
                <label className="block text-slate-400 font-bold mb-1">Select Authority Tier</label>
                <select
                  value={newRole}
                  onChange={(e) => setNewRole(e.target.value as AdminRoleType)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none font-semibold ${
                    isDarkMode ? 'bg-[#1b0d36] border-[#442377] text-white' : 'bg-slate-100 border-slate-300 text-black'
                  }`}
                >
                  <option value="Super Admin">Super Admin (Full Root Authority)</option>
                  <option value="Admin">Admin (General Administration)</option>
                  <option value="Manager">Manager (Operations)</option>
                  <option value="Admin Leader">Admin Leader</option>
                  <option value="BD Leader">BD Leader</option>
                  <option value="BD">BD Officer</option>
                  <option value="Agency Leader">Agency Leader</option>
                  <option value="Agency">Agency</option>
                  <option value="Host">Host (Verified Audio Creator)</option>
                  <option value="Super Coin Reseller">Super Coin Reseller</option>
                  <option value="Coin Reseller">Coin Reseller</option>
                  <option value="CS Leader">CS Leader</option>
                  <option value="CS">Customer Support</option>
                  <option value="Official">Official Staff</option>
                  <option value="User">Standard User</option>
                </select>
              </div>

              <div className="flex items-center justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowRoleModal(false)}
                  className="px-4 py-2 rounded-xl border border-slate-500/40 text-slate-300 hover:bg-slate-500/20 font-bold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 text-white font-extrabold shadow-md"
                >
                  Update User Authority
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: EDIT USER PROFILE */}
      {showEditModal && selectedUser && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4">
          <div
            className={`w-full max-w-md rounded-2xl border p-5 shadow-2xl ${
              isDarkMode ? 'bg-[#150a2b] border-[#391e63]' : 'bg-white border-slate-200'
            }`}
          >
            <div className="flex items-center justify-between pb-3 border-b border-inherit">
              <h3 className="text-sm font-bold flex items-center gap-2">
                <Edit className="w-4 h-4 text-cyan-400" />
                <span>Edit Profile: {selectedUser.username}</span>
              </h3>
              <button onClick={() => setShowEditModal(false)} className="text-slate-400 hover:text-white">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSaveProfileEdit} className="space-y-4 pt-4 text-xs">
              <div>
                <label className="block text-slate-400 font-bold mb-1">Display Username</label>
                <input
                  type="text"
                  value={editUsername}
                  onChange={(e) => setEditUsername(e.target.value)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                    isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                  }`}
                />
              </div>

              <div>
                <label className="block text-slate-400 font-bold mb-1">Bio Statement</label>
                <textarea
                  rows={2}
                  value={editBio}
                  onChange={(e) => setEditBio(e.target.value)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none ${
                    isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                  }`}
                />
              </div>

              <div>
                <label className="block text-slate-400 font-bold mb-1">VIP Tier (0 = None, 1 - 7 = VIP)</label>
                <input
                  type="number"
                  min={0}
                  max={7}
                  value={editVip}
                  onChange={(e) => setEditVip(parseInt(e.target.value, 10) || 0)}
                  className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                    isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                  }`}
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowEditModal(false)}
                  className="px-4 py-2 rounded-xl border border-slate-500/40 text-slate-300 hover:bg-slate-500/20 font-bold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 text-white font-extrabold shadow-md"
                >
                  Save Profile Details
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
