import React, { useState, useEffect } from 'react';
import {
  LayoutDashboard,
  Users,
  Mic,
  Shield,
  Briefcase,
  Sliders,
  DollarSign,
  Gift,
  Crown,
  CreditCard,
  FileText,
  AlertTriangle,
  Radio,
  Bell,
  Settings,
  Flame,
  Search,
  Moon,
  Sun,
  Maximize2,
  Minimize2,
  X,
  ExternalLink,
  ChevronDown,
  ChevronRight,
  TrendingUp,
  Award,
  Layers,
  Activity,
  UserCheck,
  Zap,
  Lock,
  MessageSquare,
  Globe,
  PieChart,
  LogOut,
  Smartphone,
  Sparkles,
} from 'lucide-react';
import { AdminRole } from '../../types/admin';
import { useAura } from '../../context/AuraContext';
import { adminBackend } from '../../services/adminBackendService';

export type AdminNavModule =
  | 'dashboard'
  | 'live_monitoring'
  | 'owner_control'
  | 'users'
  | 'hosts'
  | 'agents'
  | 'roles_permissions'
  | 'rooms'
  | 'reports'
  | 'chat_moderation'
  | 'content_banners'
  | 'push_notifications'
  | 'economy'
  | 'gifts'
  | 'vip'
  | 'withdrawals'
  | 'payments'
  | 'finance_pnl'
  | 'rewards_events'
  | 'fraud_security'
  | 'countries_languages'
  | 'analytics'
  | 'audit_logs'
  | 'settings_health';

interface AdminLayoutProps {
  currentModule: AdminNavModule;
  onSelectModule: (module: AdminNavModule) => void;
  activeRole: AdminRole;
  onChangeRole: (role: AdminRole) => void;
  onClose: () => void;
  isFullscreen: boolean;
  onToggleFullscreen: () => void;
  isDarkMode: boolean;
  onToggleDarkMode: () => void;
  onOpenOfficialFrames?: () => void;
  children: React.ReactNode;
}

export const AdminLayout: React.FC<AdminLayoutProps> = ({
  currentModule,
  onSelectModule,
  activeRole,
  onChangeRole,
  onClose,
  isFullscreen,
  onToggleFullscreen,
  isDarkMode,
  onToggleDarkMode,
  onOpenOfficialFrames,
  children,
}) => {
  const { currentUser, allUsers } = useAura();
  const [searchQuery, setSearchQuery] = useState('');
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);
  const [showRoleMenu, setShowRoleMenu] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [unresolvedReportsCount, setUnresolvedReportsCount] = useState(0);
  const [pendingWithdrawalsCount, setPendingWithdrawalsCount] = useState(0);

  useEffect(() => {
    const updateCounts = () => {
      const reports = adminBackend.getReports().filter((r) => r.status === 'PENDING');
      const wds = adminBackend.getWithdrawals().filter((w) => w.status === 'PENDING');
      setUnresolvedReportsCount(reports.length);
      setPendingWithdrawalsCount(wds.length);
    };
    updateCounts();
    return adminBackend.subscribe(updateCounts);
  }, []);

  const ALL_ROLES: AdminRole[] = [
    'Owner',
    'Super Admin',
    'Admin',
    'Moderator',
    'Finance Manager',
    'Host Manager',
    'Agent Manager',
    'Support Staff',
  ];

  const NAV_SECTIONS: {
    title: string;
    items: {
      id: AdminNavModule;
      label: string;
      icon: React.ElementType;
      badge?: number | string;
      badgeColor?: string;
    }[];
  }[] = [
    {
      title: 'COMMAND CENTER',
      items: [
        { id: 'dashboard', label: 'Overview Dashboard', icon: LayoutDashboard },
        { id: 'live_monitoring', label: 'Live Stage Radar', icon: Radio, badge: 'LIVE', badgeColor: 'bg-rose-500' },
        { id: 'owner_control', label: 'Owner Master Control', icon: Crown, badge: 'ROOT', badgeColor: 'bg-amber-500' },
      ],
    },
    {
      title: 'PEOPLE & ACCOUNTS',
      items: [
        { id: 'users', label: 'User Directory', icon: Users, badge: allUsers.length },
        { id: 'hosts', label: 'Host Management', icon: Mic },
        { id: 'agents', label: 'Agency Network', icon: Briefcase },
        { id: 'roles_permissions', label: 'Roles & Permissions', icon: Shield },
      ],
    },
    {
      title: 'VOICE & COMMUNITY',
      items: [
        { id: 'rooms', label: 'Voice Rooms', icon: Layers },
        {
          id: 'reports',
          label: 'Moderation & Reports',
          icon: AlertTriangle,
          badge: unresolvedReportsCount > 0 ? unresolvedReportsCount : undefined,
          badgeColor: 'bg-rose-500',
        },
        { id: 'chat_moderation', label: 'Chat Filter & Blacklist', icon: MessageSquare },
        { id: 'content_banners', label: 'Banners & Carousel', icon: Smartphone },
        { id: 'push_notifications', label: 'Push Broadcasts', icon: Bell },
      ],
    },
    {
      title: 'ECONOMY & REVENUE',
      items: [
        { id: 'economy', label: 'Currency & Rates', icon: DollarSign },
        { id: 'gifts', label: 'Virtual Gifts Catalog', icon: Gift },
        { id: 'vip', label: 'VIP Memberships', icon: Sparkles },
        {
          id: 'withdrawals',
          label: 'Withdrawal Approvals',
          icon: CreditCard,
          badge: pendingWithdrawalsCount > 0 ? pendingWithdrawalsCount : undefined,
          badgeColor: 'bg-amber-500',
        },
        { id: 'payments', label: 'Payment Transactions', icon: Activity },
        { id: 'finance_pnl', label: 'Financial Ledger & P&L', icon: PieChart },
      ],
    },
    {
      title: 'ENGAGEMENT & GROWTH',
      items: [
        { id: 'rewards_events', label: 'Events, Tasks & Levels', icon: Award },
        { id: 'fraud_security', label: 'Fraud & Risk Center', icon: Lock },
        { id: 'countries_languages', label: 'Regions & Currencies', icon: Globe },
        { id: 'analytics', label: 'Business Intelligence', icon: TrendingUp },
      ],
    },
    {
      title: 'GOVERNANCE & SYSTEM',
      items: [
        { id: 'audit_logs', label: 'Audit Trail (Immutable)', icon: FileText },
        { id: 'settings_health', label: 'Settings & Telemetry', icon: Settings },
      ],
    },
  ];

  // Quick module search filter
  const filteredSections = searchQuery.trim()
    ? NAV_SECTIONS.map((sec) => ({
        ...sec,
        items: sec.items.filter((item) =>
          item.label.toLowerCase().includes(searchQuery.toLowerCase())
        ),
      })).filter((sec) => sec.items.length > 0)
    : NAV_SECTIONS;

  return (
    <div
      className={`flex h-screen w-full select-none overflow-hidden font-sans transition-colors duration-200 ${
        isDarkMode ? 'bg-[#0a0515] text-slate-100' : 'bg-slate-50 text-slate-900'
      }`}
    >
      {/* SIDEBAR NAVIGATION */}
      <aside
        className={`flex flex-col border-r transition-all duration-200 z-30 ${
          isSidebarCollapsed ? 'w-18' : 'w-64 sm:w-72'
        } ${isDarkMode ? 'bg-[#0e071e] border-[#261545]' : 'bg-white border-slate-200 shadow-sm'}`}
      >
        {/* Brand Header */}
        <div className="flex h-16 items-center justify-between px-4 border-b border-inherit">
          <div className="flex items-center gap-3 min-w-0">
            <div className="w-10 h-10 rounded-2xl bg-gradient-to-tr from-[#ff2a85] via-[#7c4dff] to-[#00e5ff] p-0.5 shadow-lg flex-shrink-0">
              <div className="w-full h-full bg-[#0e071e] rounded-[14px] flex items-center justify-center">
                <Crown className="w-5 h-5 text-amber-400" />
              </div>
            </div>
            {!isSidebarCollapsed && (
              <div className="flex flex-col truncate">
                <div className="flex items-center gap-1.5">
                  <span className="font-black text-sm tracking-wider uppercase bg-gradient-to-r from-white via-slate-200 to-purple-300 bg-clip-text text-transparent">
                    AURA OWNER
                  </span>
                  <span className="text-[9px] font-black px-1.5 py-0.2 rounded bg-rose-500/20 text-rose-400 border border-rose-500/30">
                    V2
                  </span>
                </div>
                <span className="text-[10px] text-slate-400 truncate">
                  Enterprise Control Center
                </span>
              </div>
            )}
          </div>

          <button
            onClick={() => setIsSidebarCollapsed(!isSidebarCollapsed)}
            className={`p-1.5 rounded-xl text-slate-400 hover:text-white transition-colors ${
              isDarkMode ? 'hover:bg-[#1f123a]' : 'hover:bg-slate-100'
            }`}
            title="Toggle Sidebar"
          >
            {isSidebarCollapsed ? <ChevronRight className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
          </button>
        </div>

        {/* Search Filter in Sidebar */}
        {!isSidebarCollapsed && (
          <div className="p-3 border-b border-inherit">
            <div
              className={`flex items-center gap-2 px-3 py-1.5 rounded-xl border text-xs ${
                isDarkMode
                  ? 'bg-[#150b2b] border-[#311c59] text-slate-200 focus-within:border-[#7c4dff]'
                  : 'bg-slate-100 border-slate-200 text-slate-700'
              }`}
            >
              <Search className="w-3.5 h-3.5 text-slate-400" />
              <input
                type="text"
                placeholder="Quick jump..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full bg-transparent border-none outline-none text-xs"
              />
              {searchQuery && (
                <button onClick={() => setSearchQuery('')} className="text-slate-400 hover:text-white">
                  <X className="w-3 h-3" />
                </button>
              )}
            </div>
          </div>
        )}

        {/* Navigation List */}
        <div className="flex-1 overflow-y-auto px-2 py-3 space-y-4">
          {filteredSections.map((sec, sIdx) => (
            <div key={sIdx} className="space-y-1">
              {!isSidebarCollapsed && (
                <div className="px-3 text-[10px] font-extrabold uppercase tracking-wider text-slate-500">
                  {sec.title}
                </div>
              )}
              {sec.items.map((item) => {
                const Icon = item.icon;
                const isActive = currentModule === item.id;
                return (
                  <button
                    key={item.id}
                    onClick={() => onSelectModule(item.id)}
                    title={isSidebarCollapsed ? item.label : undefined}
                    className={`w-full flex items-center justify-between px-3 py-2 rounded-xl text-xs font-semibold transition-all group ${
                      isActive
                        ? isDarkMode
                          ? 'bg-gradient-to-r from-[#7c4dff] to-[#5b25db] text-white shadow-lg shadow-purple-900/30'
                          : 'bg-purple-600 text-white shadow-md'
                        : isDarkMode
                        ? 'text-slate-400 hover:text-slate-200 hover:bg-[#1c1038]'
                        : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                    }`}
                  >
                    <div className="flex items-center gap-2.5 min-w-0">
                      <Icon className={`w-4 h-4 flex-shrink-0 ${isActive ? 'text-white' : 'text-slate-400 group-hover:text-purple-400'}`} />
                      {!isSidebarCollapsed && <span className="truncate">{item.label}</span>}
                    </div>
                    {!isSidebarCollapsed && item.badge !== undefined && (
                      <span
                        className={`px-1.5 py-0.5 rounded-full text-[9px] font-black ${
                          item.badgeColor || (isDarkMode ? 'bg-[#29174d] text-slate-300' : 'bg-slate-200 text-slate-700')
                        }`}
                      >
                        {item.badge}
                      </span>
                    )}
                  </button>
                );
              })}
            </div>
          ))}
        </div>

        {/* Sidebar Footer: Official Frames Shortcut & System Status */}
        <div className={`p-3 border-t border-inherit ${isDarkMode ? 'bg-[#0a0515]' : 'bg-slate-100/60'}`}>
          {onOpenOfficialFrames && (
            <button
              onClick={onOpenOfficialFrames}
              className={`w-full flex items-center justify-center gap-2 py-2 px-3 rounded-xl border text-xs font-bold transition-colors mb-2 ${
                isDarkMode
                  ? 'border-cyan-500/30 bg-cyan-500/10 text-cyan-300 hover:bg-cyan-500/20'
                  : 'border-cyan-600 bg-cyan-50 text-cyan-800'
              }`}
            >
              <Sliders className="w-3.5 h-3.5" />
              {!isSidebarCollapsed && <span>Official 1 Frames</span>}
            </button>
          )}

          {!isSidebarCollapsed && (
            <div className="flex items-center justify-between text-[11px] text-slate-400 px-1">
              <div className="flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
                <span>Audio Engine 99.98%</span>
              </div>
              <span className="text-[10px] font-mono text-purple-400">LAT: 24ms</span>
            </div>
          )}
        </div>
      </aside>

      {/* MAIN VIEW AREA */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* TOP APP BAR */}
        <header
          className={`h-16 flex items-center justify-between px-4 sm:px-6 border-b z-20 transition-colors ${
            isDarkMode ? 'bg-[#0e071e]/90 border-[#261545] backdrop-blur-md' : 'bg-white border-slate-200 shadow-xs'
          }`}
        >
          {/* Breadcrumbs & Active Module */}
          <div className="flex items-center gap-2 sm:gap-3 min-w-0">
            <span className="text-xs text-slate-400 font-medium hidden sm:inline">Owner Suite</span>
            <span className="text-slate-500 hidden sm:inline">/</span>
            <h1 className="text-sm sm:text-base font-extrabold capitalize truncate">
              {currentModule.replace(/_/g, ' ')}
            </h1>
          </div>

          {/* Right Header Utilities */}
          <div className="flex items-center gap-2 sm:gap-3">
            {/* Role Switcher Pill */}
            <div className="relative">
              <button
                onClick={() => setShowRoleMenu(!showRoleMenu)}
                className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full border text-xs font-bold transition-all shadow-sm ${
                  isDarkMode
                    ? 'bg-[#180e32] border-[#3f256e] text-purple-300 hover:border-purple-400'
                    : 'bg-slate-100 border-slate-300 text-slate-700 hover:bg-slate-200'
                }`}
                title="Change active simulation role"
              >
                <Shield className="w-3.5 h-3.5 text-purple-400" />
                <span className="max-w-[100px] truncate">{activeRole}</span>
                <ChevronDown className="w-3 h-3 text-slate-400" />
              </button>

              {showRoleMenu && (
                <div
                  className={`absolute right-0 mt-2 w-56 rounded-2xl border p-1.5 shadow-2xl z-50 animate-in fade-in zoom-in-95 duration-150 ${
                    isDarkMode ? 'bg-[#160c2d] border-[#3b2367] text-white' : 'bg-white border-slate-200 text-slate-800'
                  }`}
                >
                  <div className="px-3 py-1.5 text-[10px] font-extrabold uppercase tracking-wider text-slate-400 border-b border-inherit">
                    Simulate Admin Role
                  </div>
                  <div className="py-1 space-y-0.5">
                    {ALL_ROLES.map((role) => (
                      <button
                        key={role}
                        onClick={() => {
                          onChangeRole(role);
                          setShowRoleMenu(false);
                        }}
                        className={`w-full flex items-center justify-between px-3 py-1.5 rounded-xl text-xs text-left transition-colors ${
                          activeRole === role
                            ? isDarkMode
                              ? 'bg-purple-600 text-white font-bold'
                              : 'bg-purple-100 text-purple-900 font-bold'
                            : isDarkMode
                            ? 'hover:bg-[#251549] text-slate-300'
                            : 'hover:bg-slate-100 text-slate-700'
                        }`}
                      >
                        <span>{role}</span>
                        {activeRole === role && <UserCheck className="w-3.5 h-3.5" />}
                      </button>
                    ))}
                  </div>
                </div>
              )}
            </div>

            {/* Dark / Light Toggle */}
            <button
              onClick={onToggleDarkMode}
              className={`p-2 rounded-xl border text-slate-400 hover:text-white transition-colors ${
                isDarkMode ? 'border-[#301a57] bg-[#170c30] hover:bg-[#24134a]' : 'border-slate-200 bg-slate-100 hover:bg-slate-200'
              }`}
              title={isDarkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
            >
              {isDarkMode ? <Sun className="w-4 h-4 text-amber-400" /> : <Moon className="w-4 h-4 text-slate-700" />}
            </button>

            {/* Fullscreen Toggle */}
            <button
              onClick={onToggleFullscreen}
              className={`p-2 rounded-xl border text-slate-400 hover:text-white transition-colors hidden sm:flex ${
                isDarkMode ? 'border-[#301a57] bg-[#170c30] hover:bg-[#24134a]' : 'border-slate-200 bg-slate-100 hover:bg-slate-200'
              }`}
              title={isFullscreen ? 'Exit Fullscreen' : 'Expand Fullscreen'}
            >
              {isFullscreen ? <Minimize2 className="w-4 h-4" /> : <Maximize2 className="w-4 h-4" />}
            </button>

            {/* Close / Return to Voice Chat */}
            <button
              onClick={onClose}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-bold transition-all shadow-sm ${
                isDarkMode
                  ? 'border-rose-500/30 bg-rose-500/10 text-rose-300 hover:bg-rose-500/20'
                  : 'border-rose-300 bg-rose-50 text-rose-700 hover:bg-rose-100'
              }`}
              title="Return to User App"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span>Back to App</span>
            </button>
          </div>
        </header>

        {/* SCROLLABLE MODULE VIEW CONTAINER */}
        <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 relative">
          <div className="max-w-7xl mx-auto">{children}</div>
        </main>
      </div>
    </div>
  );
};
