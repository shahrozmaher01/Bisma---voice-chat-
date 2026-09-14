import React, { useState } from 'react';
import {
  Mic,
  Camera,
  MessageSquare,
  ShoppingBag,
  User as UserIcon,
  Shield,
  Coins,
  Gem,
  Bell,
  Trophy,
  ChevronDown,
  Sparkles,
  Plus,
  Search,
} from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { AvatarWithFrame } from './AvatarWithFrame';
import { TabType } from '../types';

export type { TabType };

interface NavigationProps {
  currentTab?: TabType;
  onTabChange?: (tab: TabType) => void;
  onOpenAdmin?: () => void;
  onOpenAdminPanel?: () => void;
  onOpenOfficialFrames?: () => void;
  onOpenRankings?: () => void;
  onOpenWallet?: () => void;
  onOpenCreateRoom?: () => void;
  onOpenNotifications?: () => void;
  onOpenSearch?: (category?: 'id' | 'room' | 'user') => void;
  onOpenCreateId?: () => void;
}

export const Navigation: React.FC<NavigationProps> = ({
  currentTab: propTab,
  onTabChange,
  onOpenAdmin,
  onOpenAdminPanel,
  onOpenOfficialFrames,
  onOpenRankings,
  onOpenWallet,
  onOpenCreateRoom,
  onOpenNotifications,
  onOpenSearch,
  onOpenCreateId,
}) => {
  const {
    currentUser,
    allUsers,
    switchUser,
    unreadNotifCount,
    currentTab: contextTab,
    setCurrentTab,
  } = useAura();

  const [showUserMenu, setShowUserMenu] = useState(false);

  const activeTab = propTab || contextTab || 'party';
  const handleTabChange = (tab: TabType) => {
    if (typeof onTabChange === 'function') {
      onTabChange(tab);
    }
    if (typeof setCurrentTab === 'function') {
      setCurrentTab(tab);
    }
  };

  const handleOpenAdmin = onOpenAdmin || onOpenAdminPanel;

  return (
    <>
      {/* Top Header Bar */}
      <header
        id="app-top-bar"
        className="sticky top-0 z-40 w-full bg-[#120b24]/90 backdrop-blur-md border-b border-[#2d1b4e] px-4 py-2.5 transition-all"
      >
        <div className="max-w-6xl mx-auto flex items-center justify-between gap-2">
          {/* Logo & Platform Badge */}
          <div className="flex items-center gap-2.5">
            <div
              id="brand-logo-pill"
              onClick={() => handleTabChange('party')}
              className="flex items-center gap-2 cursor-pointer group"
            >
              <div className="w-8 h-8 rounded-xl bg-gradient-to-tr from-[#ff2a85] via-[#7c4dff] to-[#00e5ff] p-[1.5px] shadow-[0_0_12px_rgba(255,42,133,0.4)]">
                <div className="w-full h-full bg-[#120b24] rounded-[10px] flex items-center justify-center">
                  <Mic className="w-4 h-4 text-[#ff2a85]" />
                </div>
              </div>
              <div className="flex flex-col">
                <span className="text-sm font-extrabold tracking-wider bg-gradient-to-r from-white via-[#f8fafc] to-[#b388ff] bg-clip-text text-transparent">
                  AURA LIVE
                </span>
                <span className="text-[10px] text-[#00e5ff] font-semibold -mt-1 tracking-tight">
                  Voice & Social
                </span>
              </div>
            </div>

            {/* Quick Demo Profile Switcher Pill */}
            <div className="relative">
              <button
                id="user-profile-switcher-btn"
                onClick={() => setShowUserMenu(!showUserMenu)}
                className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-[#1e1338] border border-[#3b2568] hover:border-[#7c4dff] text-xs font-medium text-slate-200 transition-colors shadow-sm"
              >
                <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
                <span className="max-w-[85px] sm:max-w-[120px] truncate text-[11px] font-semibold">
                  {currentUser?.username || 'Guest'}
                </span>
                <ChevronDown className="w-3 h-3 text-slate-400" />
              </button>

              {/* User Dropdown */}
              {showUserMenu && (
                <div
                  id="user-dropdown-menu"
                  className="absolute left-0 mt-2 w-64 bg-[#160c2b] border border-[#3d246c] rounded-2xl p-2 shadow-2xl z-50 animate-in fade-in slide-in-from-top-2 duration-150"
                >
                  <div className="px-3 py-1.5 text-[11px] font-bold text-slate-400 uppercase tracking-wider border-b border-[#2d1b4e]">
                    Switch Demo Account
                  </div>
                  <div className="max-h-60 overflow-y-auto space-y-1 py-1">
                    {allUsers.map((u) => (
                      <button
                        key={u.id}
                        id={`switch-user-${u.id}`}
                        onClick={() => {
                          switchUser(u.id);
                          setShowUserMenu(false);
                        }}
                        className={`w-full flex items-center gap-2.5 px-2.5 py-2 rounded-xl text-left text-xs transition-colors ${
                          u.id === currentUser?.id
                            ? 'bg-[#7c4dff]/20 text-[#00e5ff] font-bold border border-[#7c4dff]/40'
                            : 'text-slate-300 hover:bg-[#231444]'
                        }`}
                      >
                        <AvatarWithFrame
                          avatarUrl={u.avatarUrl}
                          size={28}
                          equippedFrameId={u.equippedFrameId}
                          vipLevel={u.vipLevel}
                        />
                        <div className="flex-1 min-w-0">
                          <div className="truncate font-semibold leading-tight">{u.username}</div>
                          <div className="text-[10px] text-slate-400 truncate">
                            {u.role} • ID: {u.id}
                          </div>
                        </div>
                      </button>
                    ))}
                  </div>
                  <div className="p-1 border-t border-[#2d1b4e]">
                    <button
                      id="nav-create-user-id-btn"
                      onClick={() => {
                        setShowUserMenu(false);
                        if (onOpenCreateId) onOpenCreateId();
                      }}
                      className="w-full flex items-center justify-center gap-1.5 py-2 px-3 rounded-xl bg-gradient-to-r from-[#7c4dff] to-[#00e5ff] text-black font-extrabold text-xs shadow hover:opacity-95 active:scale-95 transition-all"
                    >
                      <Plus className="w-3.5 h-3.5" />
                      <span>Create New User ID</span>
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Right Action Icons & Balances */}
          <div className="flex items-center gap-1.5 sm:gap-2">
            {/* Global Search Button */}
            <button
              id="nav-search-btn"
              onClick={() => onOpenSearch && onOpenSearch('id')}
              className="p-2 rounded-full bg-[#20123f] border border-[#442875] text-[#00e5ff] hover:bg-[#2b1855] hover:border-[#00e5ff] transition-all relative"
              title="Search ID, Room, or User"
            >
              <Search className="w-4 h-4" />
            </button>

            {/* Coins & Diamonds Pill */}
            <div
              id="wallet-quick-pill"
              onClick={onOpenWallet}
              className="flex items-center gap-2 px-2.5 py-1 rounded-full bg-[#1c1136] border border-[#392463] cursor-pointer hover:border-[#ffd700]/50 transition-all shadow-sm"
              title="Click to open Wallet & Recharge"
            >
              <div className="flex items-center gap-1 text-[11px] font-bold text-amber-300">
                <Coins className="w-3.5 h-3.5 text-amber-400" />
                <span>{currentUser?.coins.toLocaleString() || '0'}</span>
              </div>
              <div className="w-[1px] h-3 bg-[#392463]" />
              <div className="flex items-center gap-1 text-[11px] font-bold text-cyan-300">
                <Gem className="w-3.5 h-3.5 text-cyan-400" />
                <span>{currentUser?.diamonds.toLocaleString() || '0'}</span>
              </div>
            </div>

            {/* Official 1 Frame Manager shortcut */}
            <button
              id="nav-official-frames-btn"
              onClick={onOpenOfficialFrames}
              className="p-2 rounded-full bg-[#20123f] border border-[#442875] text-[#00e5ff] hover:bg-[#2b1855] hover:border-[#00e5ff] transition-all relative"
              title="Official 1 Frame Management"
            >
              <Sparkles className="w-4 h-4" />
            </button>

            {/* Admin / Official 1 & 2 Panel button */}
            <button
              id="nav-admin-panel-btn"
              onClick={handleOpenAdmin}
              className="p-2 rounded-full bg-[#20123f] border border-[#442875] text-[#ff2a85] hover:bg-[#2b1855] hover:border-[#ff2a85] transition-all relative"
              title="Executive Admin Panel"
            >
              <Shield className="w-4 h-4" />
            </button>

            {/* Rankings trophy */}
            <button
              id="nav-rankings-btn"
              onClick={onOpenRankings}
              className="p-2 rounded-full bg-[#20123f] border border-[#442875] text-amber-300 hover:bg-[#2b1855] hover:border-amber-400 transition-all hidden xs:flex"
              title="Rankings & Leaderboards"
            >
              <Trophy className="w-4 h-4" />
            </button>

            {/* Notifications */}
            <button
              id="nav-notifications-btn"
              onClick={onOpenNotifications}
              className="p-2 rounded-full bg-[#20123f] border border-[#442875] text-slate-300 hover:bg-[#2b1855] transition-all relative"
              title="Notifications"
            >
              <Bell className="w-4 h-4" />
              {unreadNotifCount > 0 && (
                <span className="absolute -top-1 -right-1 w-4 h-4 rounded-full bg-[#ff2a85] text-white text-[9px] font-bold flex items-center justify-center animate-bounce">
                  {unreadNotifCount}
                </span>
              )}
            </button>
          </div>
        </div>
      </header>

      {/* Floating Create Room FAB for mobile/desktop */}
      <button
        id="fab-create-room"
        onClick={onOpenCreateRoom}
        className="fixed bottom-20 right-4 sm:right-8 z-30 flex items-center gap-2 px-4 py-2.5 rounded-full bg-gradient-to-r from-[#ff2a85] to-[#7c4dff] text-white font-bold text-xs shadow-[0_4px_20px_rgba(255,42,133,0.5)] hover:scale-105 active:scale-95 transition-transform"
      >
        <Plus className="w-4 h-4" />
        <span>Create Room</span>
      </button>

      {/* Bottom Tab Navigation */}
      <nav
        id="app-bottom-nav"
        className="fixed bottom-0 left-0 right-0 z-40 bg-[#100922]/95 backdrop-blur-lg border-t border-[#2a1748] px-2 py-1.5 shadow-[0_-4px_20px_rgba(0,0,0,0.5)]"
      >
        <div className="max-w-md mx-auto flex items-center justify-around">
          {/* Party (Rooms) */}
          <button
            id="tab-party-btn"
            onClick={() => handleTabChange('party')}
            className={`flex flex-col items-center justify-center py-1 px-3 rounded-xl transition-all ${
              activeTab === 'party' ? 'text-[#ff2a85] scale-105' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Mic className="w-5 h-5 mb-0.5" />
            <span className="text-[11px] font-bold">Party</span>
          </button>

          {/* Moments */}
          <button
            id="tab-moments-btn"
            onClick={() => handleTabChange('moments')}
            className={`flex flex-col items-center justify-center py-1 px-3 rounded-xl transition-all ${
              activeTab === 'moments' ? 'text-[#7c4dff] scale-105' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Camera className="w-5 h-5 mb-0.5" />
            <span className="text-[11px] font-bold">Moments</span>
          </button>

          {/* Messages */}
          <button
            id="tab-messages-btn"
            onClick={() => handleTabChange('messages')}
            className={`flex flex-col items-center justify-center py-1 px-3 rounded-xl transition-all ${
              activeTab === 'messages' ? 'text-[#00e5ff] scale-105' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <MessageSquare className="w-5 h-5 mb-0.5" />
            <span className="text-[11px] font-bold">Chats</span>
          </button>

          {/* Store */}
          <button
            id="tab-store-btn"
            onClick={() => handleTabChange('store')}
            className={`flex flex-col items-center justify-center py-1 px-3 rounded-xl transition-all ${
              activeTab === 'store' ? 'text-[#ffd700] scale-105' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <ShoppingBag className="w-5 h-5 mb-0.5" />
            <span className="text-[11px] font-bold">Store</span>
          </button>

          {/* Profile */}
          <button
            id="tab-profile-btn"
            onClick={() => handleTabChange('profile')}
            className={`flex flex-col items-center justify-center py-1 px-3 rounded-xl transition-all ${
              activeTab === 'profile' ? 'text-white scale-105' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <UserIcon className="w-5 h-5 mb-0.5" />
            <span className="text-[11px] font-bold">Profile</span>
          </button>
        </div>
      </nav>
    </>
  );
};
