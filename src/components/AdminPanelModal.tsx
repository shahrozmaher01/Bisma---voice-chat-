import React, { useState } from 'react';
import { AdminRole } from '../types/admin';
import { AdminLayout, AdminNavModule } from './admin/AdminLayout';
import { DashboardModule } from './admin/modules/DashboardModule';
import { UserManagementModule } from './admin/modules/UserManagementModule';
import { HostManagementModule } from './admin/modules/HostManagementModule';
import { AgentManagementModule } from './admin/modules/AgentManagementModule';
import { RolePermissionsModule } from './admin/modules/RolePermissionsModule';
import { VoiceRoomModule } from './admin/modules/VoiceRoomModule';
import { LiveMonitoringModule } from './admin/modules/LiveMonitoringModule';
import { EconomyModule } from './admin/modules/EconomyModule';
import { GiftManagementModule } from './admin/modules/GiftManagementModule';
import { VipManagementModule } from './admin/modules/VipManagementModule';
import { WithdrawalModule } from './admin/modules/WithdrawalModule';
import { PaymentModule } from './admin/modules/PaymentModule';
import { ModerationModule } from './admin/modules/ModerationModule';
import { ChatModerationModule } from './admin/modules/ChatModerationModule';
import { ContentNoticeModule } from './admin/modules/ContentNoticeModule';
import { RewardsEventsModule } from './admin/modules/RewardsEventsModule';
import { FraudSecurityModule } from './admin/modules/FraudSecurityModule';
import { CountriesLanguagesModule } from './admin/modules/CountriesLanguagesModule';
import { AnalyticsFinanceModule } from './admin/modules/AnalyticsFinanceModule';
import { AuditLogModule } from './admin/modules/AuditLogModule';
import { SettingsHealthModule } from './admin/modules/SettingsHealthModule';
import { OwnerControlModule } from './admin/modules/OwnerControlModule';

interface AdminPanelModalProps {
  isOpen: boolean;
  onClose: () => void;
  onOpenOfficialFrames: () => void;
  onOpenCreateId?: () => void;
}

export const AdminPanelModal: React.FC<AdminPanelModalProps> = ({
  isOpen,
  onClose,
  onOpenOfficialFrames,
  onOpenCreateId,
}) => {
  const [currentModule, setCurrentModule] = useState<AdminNavModule>('dashboard');
  const [activeRole, setActiveRole] = useState<AdminRole>('Owner');
  const [isFullscreen, setIsFullscreen] = useState(true);
  const [isDarkMode, setIsDarkMode] = useState(true);

  if (!isOpen) return null;

  const renderModuleContent = () => {
    switch (currentModule) {
      case 'dashboard':
        return <DashboardModule onNavigate={(mod) => setCurrentModule(mod)} isDarkMode={isDarkMode} />;
      case 'users':
        return <UserManagementModule isDarkMode={isDarkMode} />;
      case 'hosts':
        return <HostManagementModule isDarkMode={isDarkMode} />;
      case 'agents':
        return <AgentManagementModule isDarkMode={isDarkMode} />;
      case 'roles_permissions':
        return (
          <RolePermissionsModule
            activeRole={activeRole}
            onChangeRole={setActiveRole}
            isDarkMode={isDarkMode}
          />
        );
      case 'rooms':
        return <VoiceRoomModule isDarkMode={isDarkMode} />;
      case 'live_monitoring':
        return <LiveMonitoringModule isDarkMode={isDarkMode} />;
      case 'economy':
        return <EconomyModule isDarkMode={isDarkMode} />;
      case 'gifts':
        return <GiftManagementModule isDarkMode={isDarkMode} />;
      case 'vip':
        return <VipManagementModule isDarkMode={isDarkMode} />;
      case 'withdrawals':
        return <WithdrawalModule isDarkMode={isDarkMode} />;
      case 'payments':
        return <PaymentModule isDarkMode={isDarkMode} />;
      case 'reports':
        return <ModerationModule isDarkMode={isDarkMode} />;
      case 'chat_moderation':
        return <ChatModerationModule isDarkMode={isDarkMode} />;
      case 'content_banners':
      case 'push_notifications':
        return <ContentNoticeModule isDarkMode={isDarkMode} />;
      case 'rewards_events':
        return <RewardsEventsModule isDarkMode={isDarkMode} />;
      case 'fraud_security':
        return <FraudSecurityModule isDarkMode={isDarkMode} />;
      case 'countries_languages':
        return <CountriesLanguagesModule isDarkMode={isDarkMode} />;
      case 'finance_pnl':
      case 'analytics':
        return <AnalyticsFinanceModule isDarkMode={isDarkMode} />;
      case 'audit_logs':
        return <AuditLogModule isDarkMode={isDarkMode} />;
      case 'settings_health':
        return <SettingsHealthModule isDarkMode={isDarkMode} />;
      case 'owner_control':
        return <OwnerControlModule isDarkMode={isDarkMode} />;
      default:
        return <DashboardModule onNavigate={(mod) => setCurrentModule(mod)} isDarkMode={isDarkMode} />;
    }
  };

  return (
    <div
      className={`fixed inset-0 z-50 flex items-center justify-center ${
        isFullscreen ? 'p-0' : 'p-2 sm:p-4 bg-black/80 backdrop-blur-sm'
      }`}
    >
      <div
        className={`w-full h-full overflow-hidden transition-all duration-200 ${
          isFullscreen ? 'rounded-none' : 'max-w-7xl max-h-[92vh] rounded-3xl border border-[#3b2367] shadow-2xl'
        }`}
      >
        <AdminLayout
          currentModule={currentModule}
          onSelectModule={setCurrentModule}
          activeRole={activeRole}
          onChangeRole={setActiveRole}
          onClose={onClose}
          isFullscreen={isFullscreen}
          onToggleFullscreen={() => setIsFullscreen(!isFullscreen)}
          isDarkMode={isDarkMode}
          onToggleDarkMode={() => setIsDarkMode(!isDarkMode)}
          onOpenOfficialFrames={onOpenOfficialFrames}
        >
          {renderModuleContent()}
        </AdminLayout>
      </div>
    </div>
  );
};
