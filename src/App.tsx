import React, { useState } from 'react';
import { useAura } from './context/AuraContext';
import { Navigation } from './components/Navigation';
import { VoiceRoomModal } from './components/VoiceRoomModal';
import { OfficialFrameManagerModal } from './components/OfficialFrameManagerModal';
import { AdminPanelModal } from './components/AdminPanelModal';
import { RankingsModal } from './components/RankingsModal';
import { WalletModal } from './components/WalletModal';
import { UserProfileModal } from './components/UserProfileModal';
import { CreateRoomModal } from './components/CreateRoomModal';
import { NotificationsModal } from './components/NotificationsModal';
import { PartyView } from './views/PartyView';
import { MomentsView } from './views/MomentsView';
import { MessagesView } from './views/MessagesView';
import { StoreView } from './views/StoreView';
import { ProfileView } from './views/ProfileView';

export function App() {
  const { currentTab, activeRoom } = useAura();

  const [isOfficialFramesOpen, setIsOfficialFramesOpen] = useState(false);
  const [isAdminPanelOpen, setIsAdminPanelOpen] = useState(false);
  const [isRankingsOpen, setIsRankingsOpen] = useState(false);
  const [isWalletOpen, setIsWalletOpen] = useState(false);
  const [isCreateRoomOpen, setIsCreateRoomOpen] = useState(false);
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const [inspectedUserId, setInspectedUserId] = useState<string | null>(null);

  const handleOpenUserProfile = (userId: string) => {
    setInspectedUserId(userId);
  };

  return (
    <div className="min-h-screen bg-[#0d071a] text-slate-100 flex flex-col font-sans selection:bg-[#7c4dff] selection:text-white">
      {/* Top Application Header & Bottom Tab Bar */}
      <Navigation
        onOpenRankings={() => setIsRankingsOpen(true)}
        onOpenWallet={() => setIsWalletOpen(true)}
        onOpenNotifications={() => setIsNotificationsOpen(true)}
        onOpenAdminPanel={() => setIsAdminPanelOpen(true)}
        onOpenOfficialFrames={() => setIsOfficialFramesOpen(true)}
        onOpenCreateRoom={() => setIsCreateRoomOpen(true)}
      />

      {/* Primary Tab Content Views */}
      <main className="flex-1 overflow-x-hidden">
        {currentTab === 'party' && (
          <PartyView
            onOpenUserProfile={handleOpenUserProfile}
            onOpenCreateRoom={() => setIsCreateRoomOpen(true)}
            onOpenRankings={() => setIsRankingsOpen(true)}
          />
        )}

        {currentTab === 'moments' && (
          <MomentsView onOpenUserProfile={handleOpenUserProfile} />
        )}

        {currentTab === 'messages' && (
          <MessagesView onOpenUserProfile={handleOpenUserProfile} />
        )}

        {currentTab === 'store' && (
          <StoreView onOpenWallet={() => setIsWalletOpen(true)} />
        )}

        {currentTab === 'profile' && (
          <ProfileView
            onOpenWallet={() => setIsWalletOpen(true)}
            onOpenOfficialFrames={() => setIsOfficialFramesOpen(true)}
            onOpenAdminPanel={() => setIsAdminPanelOpen(true)}
            onOpenRankings={() => setIsRankingsOpen(true)}
          />
        )}
      </main>

      {/* Active Voice Room Overlay (Interactive Audio Stage) */}
      {activeRoom && <VoiceRoomModal onOpenUserProfile={handleOpenUserProfile} />}

      {/* Global Modals */}
      <OfficialFrameManagerModal
        isOpen={isOfficialFramesOpen}
        onClose={() => setIsOfficialFramesOpen(false)}
      />

      <AdminPanelModal
        isOpen={isAdminPanelOpen}
        onClose={() => setIsAdminPanelOpen(false)}
        onOpenOfficialFrames={() => {
          setIsAdminPanelOpen(false);
          setIsOfficialFramesOpen(true);
        }}
      />

      <RankingsModal
        isOpen={isRankingsOpen}
        onClose={() => setIsRankingsOpen(false)}
        onOpenUserProfile={handleOpenUserProfile}
      />

      <WalletModal
        isOpen={isWalletOpen}
        onClose={() => setIsWalletOpen(false)}
      />

      <UserProfileModal
        userId={inspectedUserId}
        onClose={() => setInspectedUserId(null)}
      />

      <CreateRoomModal
        isOpen={isCreateRoomOpen}
        onClose={() => setIsCreateRoomOpen(false)}
      />

      <NotificationsModal
        isOpen={isNotificationsOpen}
        onClose={() => setIsNotificationsOpen(false)}
      />
    </div>
  );
}

export default App;
