import React, { useState } from 'react';
import {
  X,
  Shield,
  Users,
  Activity,
  Award,
  FileText,
  Radio,
  Sliders,
  CheckCircle,
  AlertCircle,
  Plus,
  Coins,
  Gem,
  Search,
  Lock,
  Unlock,
  Megaphone,
} from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { AdminRoleType } from '../types';

interface AdminPanelModalProps {
  isOpen: boolean;
  onClose: () => void;
  onOpenOfficialFrames: () => void;
}

type AdminTab = 'dashboard' | 'users' | 'link_users' | 'audit' | 'broadcast';

const ALL_ROLES: AdminRoleType[] = [
  'Super Admin',
  'Admin',
  'Manager',
  'Admin Leader',
  'BD Leader',
  'BD',
  'Agency Leader',
  'Agency',
  'Super Coin Reseller',
  'Coin Reseller',
  'CS Leader',
  'CS',
  'Official',
  'Host',
  'User',
];

export const AdminPanelModal: React.FC<AdminPanelModalProps> = ({
  isOpen,
  onClose,
  onOpenOfficialFrames,
}) => {
  const {
    currentUser,
    allUsers,
    rooms,
    adminLinkUsers,
    auditLogs,
    assignUserRole,
    banUser,
    adjustUserBalance,
    addLinkUser,
    broadcastAnnouncement,
  } = useAura();

  const [currentTab, setCurrentTab] = useState<AdminTab>('dashboard');
  const [searchUserQuery, setSearchUserQuery] = useState('');
  const [selectedUserForAction, setSelectedUserForAction] = useState<string | null>(null);
  const [coinDelta, setCoinDelta] = useState<string>('1000');
  const [diamondDelta, setDiamondDelta] = useState<string>('100');

  // Broadcast state
  const [broadcastTitle, setBroadcastTitle] = useState('');
  const [broadcastMessage, setBroadcastMessage] = useState('');
  const [broadcastSuccess, setBroadcastSuccess] = useState(false);

  // New Link User form state
  const [showAddLinkForm, setShowAddLinkForm] = useState(false);
  const [newLinkUserId, setNewLinkUserId] = useState('');
  const [newLinkWork, setNewLinkWork] = useState('');
  const [newLinkCategory, setNewLinkCategory] = useState('Voice Hosting');
  const [newLinkHours, setNewLinkHours] = useState('40');
  const [newLinkDiamonds, setNewLinkDiamonds] = useState('20000');

  if (!isOpen) return null;

  // Overview metrics
  const totalCoinsInCirculation = allUsers.reduce((acc, u) => acc + u.coins, 0);
  const totalDiamondsVault = allUsers.reduce((acc, u) => acc + u.diamonds, 0);
  const activeRoomsCount = rooms.filter((r) => r.isActive).length;

  const filteredUsers = allUsers.filter(
    (u) =>
      u.username.toLowerCase().includes(searchUserQuery.toLowerCase()) ||
      u.id.toLowerCase().includes(searchUserQuery.toLowerCase()) ||
      u.role.toLowerCase().includes(searchUserQuery.toLowerCase())
  );

  const handleBroadcast = (e: React.FormEvent) => {
    e.preventDefault();
    if (!broadcastTitle.trim() || !broadcastMessage.trim()) return;
    broadcastAnnouncement(broadcastTitle.trim(), broadcastMessage.trim());
    setBroadcastSuccess(true);
    setBroadcastTitle('');
    setBroadcastMessage('');
    setTimeout(() => setBroadcastSuccess(false), 3000);
  };

  const handleAddLinkUser = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newLinkUserId.trim() || !newLinkWork.trim()) return;

    const userObj = allUsers.find((u) => u.id === newLinkUserId.trim());
    addLinkUser({
      userId: newLinkUserId.trim(),
      userName: userObj ? userObj.username : `User ${newLinkUserId}`,
      userAvatar: userObj ? userObj.avatarUrl : undefined,
      assignedWork: newLinkWork.trim(),
      workCategory: newLinkCategory,
      targetHours: parseFloat(newLinkHours) || 40,
      targetDiamonds: parseInt(newLinkDiamonds, 10) || 20000,
    });

    setShowAddLinkForm(false);
    setNewLinkUserId('');
    setNewLinkWork('');
  };

  return (
    <div
      id="admin-panel-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/80 backdrop-blur-xs flex items-center justify-center p-3 sm:p-5 animate-in fade-in duration-200"
      onClick={onClose}
    >
      <div
        id="admin-panel-modal"
        className="w-full max-w-5xl bg-[#110924] border border-[#442875] rounded-3xl p-4 sm:p-6 shadow-2xl flex flex-col max-h-[92vh] overflow-hidden relative"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Panel Header */}
        <div className="flex items-center justify-between border-b border-[#2d1b4e] pb-3">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-2xl bg-gradient-to-tr from-rose-500 via-[#7c4dff] to-[#00e5ff] flex items-center justify-center shadow-lg">
              <Shield className="w-5 h-5 text-white" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-base sm:text-lg font-extrabold text-white">
                  Executive Admin & Official Panel
                </h2>
                <span className="px-2 py-0.5 rounded-full bg-rose-500/20 text-rose-300 text-[10px] font-bold border border-rose-500/30">
                  Root Authority
                </span>
              </div>
              <p className="text-xs text-slate-400">
                Operating as: <span className="text-[#00e5ff] font-bold">{currentUser?.username}</span> ({currentUser?.role})
              </p>
            </div>
          </div>
          <button
            id="close-admin-panel-btn"
            onClick={onClose}
            className="p-1.5 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab Navigation */}
        <div className="flex items-center gap-2 border-b border-[#2d1b4e] pt-3 pb-2 overflow-x-auto text-xs font-bold">
          <button
            id="admin-tab-dashboard"
            onClick={() => setCurrentTab('dashboard')}
            className={`px-3 py-1.5 rounded-xl transition-colors flex items-center gap-1.5 flex-shrink-0 ${
              currentTab === 'dashboard'
                ? 'bg-[#7c4dff] text-white shadow'
                : 'text-slate-400 hover:text-white hover:bg-[#1f123c]'
            }`}
          >
            <Activity className="w-4 h-4" />
            <span>Dashboard</span>
          </button>

          <button
            id="admin-tab-users"
            onClick={() => setCurrentTab('users')}
            className={`px-3 py-1.5 rounded-xl transition-colors flex items-center gap-1.5 flex-shrink-0 ${
              currentTab === 'users'
                ? 'bg-[#7c4dff] text-white shadow'
                : 'text-slate-400 hover:text-white hover:bg-[#1f123c]'
            }`}
          >
            <Users className="w-4 h-4" />
            <span>Users & Roles</span>
          </button>

          <button
            id="admin-tab-link-users"
            onClick={() => setCurrentTab('link_users')}
            className={`px-3 py-1.5 rounded-xl transition-colors flex items-center gap-1.5 flex-shrink-0 ${
              currentTab === 'link_users'
                ? 'bg-[#7c4dff] text-white shadow'
                : 'text-slate-400 hover:text-white hover:bg-[#1f123c]'
            }`}
          >
            <Award className="w-4 h-4" />
            <span>Admin Link Users ({adminLinkUsers.length})</span>
          </button>

          <button
            id="admin-tab-frames-shortcut"
            onClick={onOpenOfficialFrames}
            className="px-3 py-1.5 rounded-xl transition-colors flex items-center gap-1.5 flex-shrink-0 text-cyan-300 hover:text-white hover:bg-[#1f123c]"
          >
            <Sliders className="w-4 h-4" />
            <span>Official 1 Frames</span>
          </button>

          <button
            id="admin-tab-audit"
            onClick={() => setCurrentTab('audit')}
            className={`px-3 py-1.5 rounded-xl transition-colors flex items-center gap-1.5 flex-shrink-0 ${
              currentTab === 'audit'
                ? 'bg-[#7c4dff] text-white shadow'
                : 'text-slate-400 hover:text-white hover:bg-[#1f123c]'
            }`}
          >
            <FileText className="w-4 h-4" />
            <span>Audit Logs ({auditLogs.length})</span>
          </button>

          <button
            id="admin-tab-broadcast"
            onClick={() => setCurrentTab('broadcast')}
            className={`px-3 py-1.5 rounded-xl transition-colors flex items-center gap-1.5 flex-shrink-0 ${
              currentTab === 'broadcast'
                ? 'bg-[#7c4dff] text-white shadow'
                : 'text-slate-400 hover:text-white hover:bg-[#1f123c]'
            }`}
          >
            <Megaphone className="w-4 h-4" />
            <span>Broadcast</span>
          </button>
        </div>

        {/* Tab Content Container */}
        <div className="flex-1 overflow-y-auto py-4">
          {/* TAB 1: DASHBOARD */}
          {currentTab === 'dashboard' && (
            <div className="space-y-4">
              {/* KPI Cards Grid */}
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                <div className="bg-[#190e33] border border-[#371f5c] rounded-2xl p-3.5 flex flex-col">
                  <span className="text-[11px] font-bold text-slate-400">Total Registered Users</span>
                  <span className="text-xl font-extrabold text-white mt-1">
                    {allUsers.length.toLocaleString()}
                  </span>
                  <span className="text-[10px] text-emerald-400 mt-0.5">● 100% Active Directory</span>
                </div>

                <div className="bg-[#190e33] border border-[#371f5c] rounded-2xl p-3.5 flex flex-col">
                  <span className="text-[11px] font-bold text-slate-400">Active Live Rooms</span>
                  <span className="text-xl font-extrabold text-[#00e5ff] mt-1">
                    {activeRoomsCount}
                  </span>
                  <span className="text-[10px] text-slate-400 mt-0.5">Across Pakistan & Global</span>
                </div>

                <div className="bg-[#190e33] border border-[#371f5c] rounded-2xl p-3.5 flex flex-col">
                  <span className="text-[11px] font-bold text-slate-400">Total Circulating Coins</span>
                  <span className="text-xl font-extrabold text-amber-300 mt-1">
                    {totalCoinsInCirculation.toLocaleString()}
                  </span>
                  <span className="text-[10px] text-amber-400/80 mt-0.5">In-App Liquid Currency</span>
                </div>

                <div className="bg-[#190e33] border border-[#371f5c] rounded-2xl p-3.5 flex flex-col">
                  <span className="text-[11px] font-bold text-slate-400">Diamonds Reserve</span>
                  <span className="text-xl font-extrabold text-cyan-300 mt-1">
                    {totalDiamondsVault.toLocaleString()}
                  </span>
                  <span className="text-[10px] text-cyan-400/80 mt-0.5">Creator Earnings Vault</span>
                </div>
              </div>

              {/* System Infrastructure Status Banner */}
              <div className="bg-[#190e33] border border-[#371f5c] rounded-2xl p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-400 flex items-center justify-center">
                    <Radio className="w-5 h-5 animate-pulse" />
                  </div>
                  <div>
                    <h3 className="text-xs font-extrabold text-white">
                      AURA Live Web Server • ONLINE
                    </h3>
                    <p className="text-[11px] text-slate-400">
                      Standard Port: 3000 • Ingress Proxy: Active • Voice Signaling Engine: Operational
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <span className="px-2.5 py-1 rounded-full bg-emerald-500/20 text-emerald-300 text-[11px] font-bold border border-emerald-500/30">
                    STATUS: HEALTHY
                  </span>
                </div>
              </div>

              {/* Recent Administrative Actions Table preview */}
              <div className="bg-[#190e33] border border-[#371f5c] rounded-2xl p-4">
                <h3 className="text-xs font-bold text-white uppercase tracking-wider mb-2">
                  Recent Audit Operations
                </h3>
                <div className="space-y-1.5 text-xs">
                  {auditLogs.slice(0, 4).map((log) => (
                    <div
                      key={log.id}
                      className="flex items-center justify-between p-2 rounded-xl bg-[#130a26] border border-[#2b184a]"
                    >
                      <div className="flex items-center gap-2">
                        <span className="font-bold text-cyan-300">{log.adminName}</span>
                        <span className="text-slate-400">{log.action}</span>
                        <span className="text-white font-semibold">{log.targetName}</span>
                      </div>
                      <span className="text-[10px] text-slate-500">
                        {new Date(log.timestamp).toLocaleTimeString()}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* TAB 2: USERS & ROLES */}
          {currentTab === 'users' && (
            <div className="space-y-4">
              {/* Search Bar */}
              <div className="flex items-center gap-2">
                <div className="relative flex-1">
                  <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                  <input
                    id="admin-search-users-input"
                    type="text"
                    value={searchUserQuery}
                    onChange={(e) => setSearchUserQuery(e.target.value)}
                    placeholder="Search users by name, ID, or current role..."
                    className="w-full bg-[#190e33] border border-[#371f5c] rounded-xl pl-9 pr-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-hidden"
                  />
                </div>
              </div>

              {/* Users Table */}
              <div className="bg-[#190e33] border border-[#371f5c] rounded-2xl p-4 overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead>
                    <tr className="border-b border-[#2d1b4e] text-slate-400 text-[11px]">
                      <th className="pb-2">User Details</th>
                      <th className="pb-2">Role</th>
                      <th className="pb-2">Balances</th>
                      <th className="pb-2">VIP</th>
                      <th className="pb-2">Status</th>
                      <th className="pb-2 text-right">Quick Actions</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-[#251545]">
                    {filteredUsers.map((user) => (
                      <tr key={user.id} className="hover:bg-[#20123f]/50">
                        <td className="py-2.5">
                          <div className="font-bold text-white flex items-center gap-2">
                            <span>{user.username}</span>
                          </div>
                          <div className="text-[10px] text-slate-400">ID: {user.id}</div>
                        </td>
                        <td className="py-2.5">
                          <select
                            id={`role-select-${user.id}`}
                            value={user.role}
                            onChange={(e) =>
                              assignUserRole(user.id, e.target.value as AdminRoleType)
                            }
                            className="bg-[#120a24] border border-[#381f60] rounded-lg px-2 py-1 text-xs text-cyan-300 font-bold focus:outline-hidden"
                          >
                            {ALL_ROLES.map((r) => (
                              <option key={r} value={r}>
                                {r}
                              </option>
                            ))}
                          </select>
                        </td>
                        <td className="py-2.5">
                          <div className="text-amber-300 font-bold">
                            {user.coins.toLocaleString()} C
                          </div>
                          <div className="text-cyan-300 text-[10px]">
                            {user.diamonds.toLocaleString()} D
                          </div>
                        </td>
                        <td className="py-2.5">
                          <span className="px-1.5 py-0.5 rounded bg-gradient-to-r from-amber-500 to-rose-500 text-black text-[9px] font-extrabold">
                            V{user.vipLevel}
                          </span>
                        </td>
                        <td className="py-2.5">
                          <span
                            className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                              user.isBanned
                                ? 'bg-rose-500/20 text-rose-300 border border-rose-500/30'
                                : 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                            }`}
                          >
                            {user.isBanned ? 'Banned' : 'Active'}
                          </span>
                        </td>
                        <td className="py-2.5 text-right space-x-1.5">
                          {/* Ban / Unban */}
                          <button
                            id={`ban-btn-${user.id}`}
                            onClick={() => banUser(user.id, !user.isBanned)}
                            className={`px-2 py-1 rounded-lg text-[11px] font-bold border transition-colors ${
                              user.isBanned
                                ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40 hover:bg-emerald-500/30'
                                : 'bg-rose-500/20 text-rose-300 border-rose-500/40 hover:bg-rose-500/30'
                            }`}
                          >
                            {user.isBanned ? 'Unban' : 'Ban'}
                          </button>

                          {/* Adjust Balance Modal opener */}
                          <button
                            id={`adjust-balance-${user.id}`}
                            onClick={() => setSelectedUserForAction(user.id)}
                            className="px-2 py-1 rounded-lg bg-[#251549] text-amber-300 hover:bg-[#341e66] border border-[#3e236f] text-[11px] font-bold transition-colors"
                          >
                            Coins/Diamonds
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Adjust Balance Dialog */}
              {selectedUserForAction && (
                <div
                  className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4"
                  onClick={() => setSelectedUserForAction(null)}
                >
                  <div
                    className="w-full max-w-sm bg-[#160c2b] border border-[#442875] rounded-3xl p-5 shadow-2xl flex flex-col gap-3"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <h3 className="text-sm font-bold text-white">
                      Adjust User Currency
                    </h3>
                    <div>
                      <label className="text-xs text-slate-300 mb-1 block">Coins to Add/Deduct:</label>
                      <input
                        type="number"
                        value={coinDelta}
                        onChange={(e) => setCoinDelta(e.target.value)}
                        className="w-full bg-[#120a24] border border-[#381f60] rounded-xl px-3 py-1.5 text-xs text-white"
                      />
                    </div>
                    <div>
                      <label className="text-xs text-slate-300 mb-1 block">Diamonds to Add/Deduct:</label>
                      <input
                        type="number"
                        value={diamondDelta}
                        onChange={(e) => setDiamondDelta(e.target.value)}
                        className="w-full bg-[#120a24] border border-[#381f60] rounded-xl px-3 py-1.5 text-xs text-white"
                      />
                    </div>
                    <div className="flex gap-2 mt-2">
                      <button
                        onClick={() => setSelectedUserForAction(null)}
                        className="flex-1 py-2 rounded-xl bg-[#20123f] text-slate-300 font-bold text-xs"
                      >
                        Cancel
                      </button>
                      <button
                        onClick={() => {
                          adjustUserBalance(
                            selectedUserForAction,
                            parseInt(coinDelta, 10) || 0,
                            parseInt(diamondDelta, 10) || 0
                          );
                          setSelectedUserForAction(null);
                        }}
                        className="flex-1 py-2 rounded-xl bg-gradient-to-r from-amber-500 to-rose-500 text-black font-extrabold text-xs shadow"
                      >
                        Apply Balance
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* TAB 3: ADMIN LINK USERS */}
          {currentTab === 'link_users' && (
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-xs font-bold text-white uppercase tracking-wider">
                    Supervised Host & Agent Contracts
                  </h3>
                  <p className="text-[11px] text-slate-400">
                    Track live hours, diamond targets, and operational task metrics
                  </p>
                </div>
                <button
                  id="add-link-user-btn"
                  onClick={() => setShowAddLinkForm(!showAddLinkForm)}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-[#7c4dff] text-white font-bold text-xs shadow hover:bg-[#6c3df5] transition-colors"
                >
                  <Plus className="w-4 h-4" />
                  <span>Assign New Contract</span>
                </button>
              </div>

              {/* Add form */}
              {showAddLinkForm && (
                <form
                  onSubmit={handleAddLinkUser}
                  className="bg-[#190e33] border border-[#371f5c] rounded-2xl p-4 flex flex-col gap-3 animate-in fade-in duration-150"
                >
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <div>
                      <label className="text-xs text-slate-300 mb-1 block">User ID:</label>
                      <input
                        type="text"
                        value={newLinkUserId}
                        onChange={(e) => setNewLinkUserId(e.target.value)}
                        placeholder="e.g. usr_78912"
                        className="w-full bg-[#120a24] border border-[#381f60] rounded-xl px-3 py-1.5 text-xs text-white"
                        required
                      />
                    </div>
                    <div>
                      <label className="text-xs text-slate-300 mb-1 block">Work Role / Task:</label>
                      <input
                        type="text"
                        value={newLinkWork}
                        onChange={(e) => setNewLinkWork(e.target.value)}
                        placeholder="e.g. Star Singer Host"
                        className="w-full bg-[#120a24] border border-[#381f60] rounded-xl px-3 py-1.5 text-xs text-white"
                        required
                      />
                    </div>
                    <div>
                      <label className="text-xs text-slate-300 mb-1 block">Target Monthly Hours:</label>
                      <input
                        type="number"
                        value={newLinkHours}
                        onChange={(e) => setNewLinkHours(e.target.value)}
                        className="w-full bg-[#120a24] border border-[#381f60] rounded-xl px-3 py-1.5 text-xs text-white"
                      />
                    </div>
                    <div>
                      <label className="text-xs text-slate-300 mb-1 block">Target Monthly Diamonds:</label>
                      <input
                        type="number"
                        value={newLinkDiamonds}
                        onChange={(e) => setNewLinkDiamonds(e.target.value)}
                        className="w-full bg-[#120a24] border border-[#381f60] rounded-xl px-3 py-1.5 text-xs text-white"
                      />
                    </div>
                  </div>
                  <div className="flex justify-end gap-2 mt-1">
                    <button
                      type="button"
                      onClick={() => setShowAddLinkForm(false)}
                      className="px-3 py-1.5 rounded-xl bg-[#20123f] text-slate-300 text-xs font-semibold"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="px-4 py-1.5 rounded-xl bg-[#00e5ff] text-black font-extrabold text-xs shadow"
                    >
                      Save Contract
                    </button>
                  </div>
                </form>
              )}

              {/* Link Users Grid */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {adminLinkUsers.map((link) => {
                  const hoursPercent = Math.min(100, Math.round((link.completedHours / link.targetHours) * 100));
                  const diamondPercent = Math.min(100, Math.round((link.earnedDiamonds / link.targetDiamonds) * 100));

                  return (
                    <div
                      key={link.id}
                      className="bg-[#190e33] border border-[#371f5c] rounded-2xl p-4 flex flex-col justify-between gap-3"
                    >
                      <div className="flex items-center gap-3">
                        <img
                          src={link.userAvatar}
                          alt={link.userName}
                          className="w-12 h-12 rounded-xl object-cover border border-[#442875]"
                        />
                        <div className="min-w-0 flex-1">
                          <h4 className="text-xs font-extrabold text-white truncate">
                            {link.userName}
                          </h4>
                          <span className="text-[10px] text-cyan-300 font-semibold block">
                            {link.assignedWork}
                          </span>
                          <span className="text-[10px] text-slate-400">ID: {link.userId}</span>
                        </div>
                        <span className="px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-300 text-[10px] font-bold border border-emerald-500/30">
                          {link.status}
                        </span>
                      </div>

                      {/* Progress Metrics */}
                      <div className="space-y-2">
                        <div>
                          <div className="flex justify-between text-[10px] font-bold text-slate-400 mb-0.5">
                            <span>Broadcast Hours:</span>
                            <span className="text-emerald-400">
                              {link.completedHours} / {link.targetHours} hrs ({hoursPercent}%)
                            </span>
                          </div>
                          <div className="w-full bg-[#120a24] rounded-full h-1.5 overflow-hidden">
                            <div
                              className="bg-emerald-400 h-full rounded-full"
                              style={{ width: `${hoursPercent}%` }}
                            />
                          </div>
                        </div>

                        <div>
                          <div className="flex justify-between text-[10px] font-bold text-slate-400 mb-0.5">
                            <span>Diamonds Target:</span>
                            <span className="text-cyan-400">
                              {link.earnedDiamonds.toLocaleString()} / {link.targetDiamonds.toLocaleString()} ({diamondPercent}%)
                            </span>
                          </div>
                          <div className="w-full bg-[#120a24] rounded-full h-1.5 overflow-hidden">
                            <div
                              className="bg-cyan-400 h-full rounded-full"
                              style={{ width: `${diamondPercent}%` }}
                            />
                          </div>
                        </div>
                      </div>

                      <div className="text-[10px] text-slate-400 border-t border-[#2d1b4e] pt-2 flex justify-between">
                        <span>Joined: {link.joinedDate}</span>
                        <span className="text-amber-300 font-semibold">{link.activityInfo}</span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* TAB 4: AUDIT LOGS */}
          {currentTab === 'audit' && (
            <div className="bg-[#190e33] border border-[#371f5c] rounded-2xl p-4 overflow-x-auto">
              <h3 className="text-xs font-bold text-white uppercase tracking-wider mb-3">
                Full Security Audit Trail ({auditLogs.length} Records)
              </h3>
              <table className="w-full text-left text-xs">
                <thead>
                  <tr className="border-b border-[#2d1b4e] text-slate-400 text-[11px]">
                    <th className="pb-2">Timestamp</th>
                    <th className="pb-2">Admin</th>
                    <th className="pb-2">Action</th>
                    <th className="pb-2">Target</th>
                    <th className="pb-2">Details / Result</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#251545]">
                  {auditLogs.map((log) => (
                    <tr key={log.id} className="hover:bg-[#20123f]/50">
                      <td className="py-2 text-[11px] text-slate-400">
                        {new Date(log.timestamp).toLocaleString()}
                      </td>
                      <td className="py-2">
                        <span className="font-bold text-white">{log.adminName}</span>
                        <span className="text-[10px] text-cyan-300 block">{log.adminRole}</span>
                      </td>
                      <td className="py-2">
                        <span className="px-2 py-0.5 rounded-full bg-[#7c4dff]/20 text-[#b388ff] text-[10px] font-bold border border-[#7c4dff]/40">
                          {log.action}
                        </span>
                      </td>
                      <td className="py-2 font-semibold text-slate-200">{log.targetName}</td>
                      <td className="py-2 text-slate-300 text-[11px]">
                        {log.newValue || log.previousValue || 'Success'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* TAB 5: SYSTEM BROADCAST */}
          {currentTab === 'broadcast' && (
            <div className="max-w-md mx-auto bg-[#190e33] border border-[#371f5c] rounded-2xl p-4 flex flex-col gap-3">
              <div className="flex items-center gap-2">
                <Megaphone className="w-5 h-5 text-amber-400" />
                <h3 className="text-sm font-bold text-white">Push System Announcement</h3>
              </div>
              <p className="text-xs text-slate-400">
                Broadcast an official announcement banner to all online users.
              </p>

              <form onSubmit={handleBroadcast} className="space-y-3">
                <div>
                  <label className="text-xs text-slate-300 mb-1 block">Title:</label>
                  <input
                    id="broadcast-title-input"
                    type="text"
                    value={broadcastTitle}
                    onChange={(e) => setBroadcastTitle(e.target.value)}
                    placeholder="e.g. Weekend Singing Tournament Announcement"
                    className="w-full bg-[#120a24] border border-[#381f60] rounded-xl px-3 py-1.5 text-xs text-white"
                    required
                  />
                </div>
                <div>
                  <label className="text-xs text-slate-300 mb-1 block">Message Content:</label>
                  <textarea
                    id="broadcast-content-input"
                    rows={4}
                    value={broadcastMessage}
                    onChange={(e) => setBroadcastMessage(e.target.value)}
                    placeholder="Enter details to be displayed to users..."
                    className="w-full bg-[#120a24] border border-[#381f60] rounded-xl px-3 py-1.5 text-xs text-white"
                    required
                  />
                </div>

                {broadcastSuccess && (
                  <div className="p-2.5 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2">
                    <CheckCircle className="w-4 h-4 text-emerald-400" />
                    <span>Announcement broadcasted successfully to all users!</span>
                  </div>
                )}

                <button
                  id="send-broadcast-btn"
                  type="submit"
                  className="w-full py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-rose-500 text-black font-extrabold text-xs shadow-md hover:opacity-95 transition-opacity"
                >
                  Send Broadcast to Platform
                </button>
              </form>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
