export type AdminRole =
  | 'Owner'
  | 'Super Admin'
  | 'Admin'
  | 'Moderator'
  | 'Finance Manager'
  | 'Host Manager'
  | 'Agent Manager'
  | 'Support Staff';

export interface RolePermissions {
  users: {
    view: boolean;
    edit: boolean;
    ban: boolean;
    adjustBalance: boolean;
    assignVip: boolean;
    resetRestrictions: boolean;
  };
  hosts: {
    view: boolean;
    approve: boolean;
    edit: boolean;
    suspend: boolean;
    manageCommission: boolean;
  };
  agents: {
    view: boolean;
    create: boolean;
    approve: boolean;
    edit: boolean;
    suspend: boolean;
  };
  rooms: {
    view: boolean;
    lock: boolean;
    close: boolean;
    mute: boolean;
    moderate: boolean;
  };
  finance: {
    viewLedger: boolean;
    approveWithdrawals: boolean;
    refunds: boolean;
    editPricing: boolean;
  };
  gifts: {
    view: boolean;
    create: boolean;
    edit: boolean;
    delete: boolean;
  };
  vip: {
    view: boolean;
    assign: boolean;
    editPackages: boolean;
  };
  events: {
    view: boolean;
    manage: boolean;
  };
  tasks: {
    view: boolean;
    manage: boolean;
  };
  moderation: {
    view: boolean;
    resolve: boolean;
    ban: boolean;
  };
  content: {
    view: boolean;
    editBanners: boolean;
    sendPush: boolean;
    editPolicies: boolean;
  };
  chat: {
    view: boolean;
    moderate: boolean;
    editBlockedWords: boolean;
  };
  fraud: {
    view: boolean;
    resolveAlerts: boolean;
  };
  settings: {
    view: boolean;
    editGeneral: boolean;
    editOwnerOnly: boolean;
  };
}

export interface AdminUserRecord {
  id: string;
  username: string;
  email: string;
  phone?: string;
  avatarUrl: string;
  role: AdminRole;
  customPermissions?: Partial<RolePermissions>;
  status: 'ACTIVE' | 'SUSPENDED';
  lastLogin: string;
  createdAt: string;
  twoFactorEnabled: boolean;
}

export interface HostProfile {
  id: string;
  userId: string;
  username: string;
  avatarUrl: string;
  agencyId?: string;
  agencyName?: string;
  level: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED' | 'BANNED';
  isLive: boolean;
  currentRoomId?: string;
  totalRoomHours: number;
  totalReceivedGiftsCoins: number;
  totalEarnedDiamonds: number;
  withdrawableDiamonds: number;
  commissionRate: number; // e.g. 0.65
  monthlyTargetHours: number;
  warningCount: number;
  rank: number;
  appliedDate: string;
  approvedDate?: string;
  nationalIdDocument?: string;
  notes?: string;
}

export interface AgentProfile {
  id: string;
  name: string;
  code: string;
  ownerUserId: string;
  ownerUsername: string;
  avatarUrl: string;
  status: 'ACTIVE' | 'PENDING' | 'SUSPENDED';
  hostCount: number;
  totalEarningsUsd: number;
  pendingPayoutUsd: number;
  commissionPercent: number; // e.g. 10%
  tier: 'Diamond Elite' | 'Gold Platinum' | 'Silver Rising' | 'Standard';
  country: string;
  contactEmail: string;
  contactPhone: string;
  createdDate: string;
  assignedHostIds: string[];
}

export interface WithdrawalRequest {
  id: string;
  recipientType: 'USER' | 'HOST' | 'AGENT';
  userId: string;
  userName: string;
  amountDiamonds: number;
  amountUsd: number;
  feeUsd: number;
  netPayoutUsd: number;
  paymentMethod: 'Bank Transfer' | 'EasyPaisa' | 'JazzCash' | 'PayPal' | 'Crypto (USDT)' | 'Wire Transfer';
  accountDetails: {
    accountNumber: string;
    accountTitle: string;
    bankName?: string;
    routingOrIban?: string;
  };
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'COMPLETED';
  adminNotes?: string;
  processedByAdminId?: string;
  processedByAdminName?: string;
  requestedAt: number;
  processedAt?: number;
  transactionRef?: string;
}

export interface PaymentTransaction {
  id: string;
  userId: string;
  userName: string;
  packageId: string;
  packageTitle: string;
  amountUsd: number;
  coinsAwarded: number;
  provider: 'Google Play IAP' | 'Apple Pay' | 'Stripe' | 'EasyPaisa' | 'JazzCash' | 'Crypto';
  status: 'SUCCESS' | 'FAILED' | 'REFUNDED' | 'CHARGEBACK';
  paymentGatewayRef: string;
  timestamp: number;
  ipAddress: string;
  refundReason?: string;
}

export interface FinancialLedgerEntry {
  id: string;
  type: 'INFLOW_PURCHASE' | 'OUTFLOW_WITHDRAWAL' | 'HOST_PAYOUT' | 'AGENT_COMMISSION' | 'REFUND' | 'ADMIN_ADJUSTMENT';
  amountUsd: number;
  coinsDelta?: number;
  diamondsDelta?: number;
  referenceId: string;
  description: string;
  actorAdminId?: string;
  timestamp: number;
}

export interface VipPackageItem {
  level: number;
  title: string;
  badge: string;
  color: string;
  priceUsdMonthly: number;
  priceCoinsMonthly: number;
  privileges: string[];
  activeSubscribers: number;
  dailyBonusCoins: number;
}

export interface EventItem {
  id: string;
  title: string;
  description: string;
  bannerUrl: string;
  startDate: string;
  endDate: string;
  rules: string;
  rewardPoolCoins: number;
  rewardPoolDiamonds: number;
  category: 'Voice Battle' | 'Gifting Carnival' | 'Agency Championship' | 'Festive Celebration';
  isActive: boolean;
  participantsCount: number;
  leaderboardTopUser?: string;
}

export interface AdminMission {
  id: string;
  category: 'daily' | 'weekly' | 'host' | 'user' | 'agent';
  title: string;
  description: string;
  targetCount: number;
  rewardCoins: number;
  rewardDiamonds: number;
  isEnabled: boolean;
  iconEmoji: string;
}

export interface ReportTicket {
  id: string;
  targetType: 'USER' | 'VOICE_ROOM' | 'HOST' | 'MOMENT' | 'CHAT_MESSAGE';
  targetId: string;
  targetName: string;
  reporterId: string;
  reporterName: string;
  category: 'Abuse & Harassment' | 'Underage User' | 'Fraud / Scam' | 'Inappropriate Audio' | 'Spam Bot';
  description: string;
  evidenceUrl?: string;
  status: 'PENDING' | 'INVESTIGATING' | 'RESOLVED' | 'DISMISSED';
  assignedAdminName?: string;
  adminNotes?: string;
  actionTaken?: 'NONE' | 'WARNING_SENT' | 'MUTED_24H' | 'TEMPORARY_SUSPENSION' | 'PERMANENT_BAN';
  createdAt: number;
  resolvedAt?: number;
}

export interface AppBanner {
  id: string;
  title: string;
  imageUrl: string;
  linkUrl: string;
  placement: 'Home Carousel' | 'Party Header' | 'Store Spotlight' | 'Popup Notice';
  isActive: boolean;
  displayOrder: number;
  startsAt: string;
  endsAt: string;
}

export interface PushNotificationRecord {
  id: string;
  title: string;
  body: string;
  targetAudience: 'ALL_USERS' | 'ONLINE_USERS' | 'VIP_USERS' | 'HOSTS_ONLY' | 'AGENTS_ONLY' | 'SPECIFIC_COUNTRY';
  targetCountry?: string;
  recipientCount: number;
  status: 'SENT' | 'SCHEDULED' | 'FAILED';
  scheduledTime?: string;
  sentAt: number;
  sentByAdminName: string;
}

export interface BlockedWord {
  id: string;
  keyword: string;
  category: 'Profanity' | 'Scam / Phishing' | 'Hate Speech' | 'External Links';
  severity: 'WARN' | 'BLOCK' | 'AUTO_MUTE';
  createdAt: number;
}

export interface FraudAlert {
  id: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  type: 'MULTI_ACCOUNT_DEVICE' | 'SUSPICIOUS_TRANSFER' | 'ABNORMAL_GIFT_VELOCITY' | 'IP_COUNTRY_MISMATCH' | 'CHARGEBACK_RISK';
  userId: string;
  userName: string;
  riskScore: number; // 0 to 100
  details: string;
  deviceFingerprint: string;
  ipAddress: string;
  status: 'OPEN' | 'INVESTIGATING' | 'RESOLVED' | 'CLEARED';
  detectedAt: number;
}

export interface ReferralRecord {
  id: string;
  referralCode: string;
  referrerUserId: string;
  referrerName: string;
  referredUserId: string;
  referredName: string;
  commissionEarnedCoins: number;
  referredDepositUsd: number;
  isFraudFlagged: boolean;
  createdAt: number;
}

export interface SystemSettingsState {
  appName: string;
  platformTitle: string;
  logoUrl: string;
  maintenanceMode: boolean;
  maintenanceMessage: string;
  registrationOpen: boolean;
  guestAccessAllowed: boolean;
  currencySymbol: string;
  usdToCoinRate: number; // e.g. 30000
  diamondToCoinRate: number; // e.g. 1 diamond = 10 coins
  diamondCashoutRateUsd: number; // e.g. 1000 diamonds = $1 USD
  minWithdrawalUsd: number;
  maxWithdrawalUsd: number;
  dailyWithdrawalLimitUsd: number;
  defaultHostCommissionPercent: number; // e.g. 65%
  defaultAgentCommissionPercent: number; // e.g. 12%
  platformFeePercent: number; // e.g. 10%
  autoSpamFilterActive: boolean;
  privateChatMonitoringPolicy: 'ENCRYPTED_PRIVATE' | 'FLAGGED_ONLY' | 'STRICT_AUDIT';
  featureFlags: {
    enableLuckyBags: boolean;
    enableMoments: boolean;
    enableWithdrawals: boolean;
    enableStorePurchases: boolean;
    enableReferralSystem: boolean;
    enableVoiceEffects: boolean;
    enableOfficialFrameTransfers: boolean;
    enableLiveRoomCreations: boolean;
  };
}

export interface SystemHealthMetrics {
  apiLatencyMs: number;
  webrtcAudioStatus: 'OPERATIONAL' | 'DEGRADED' | 'INCIDENT';
  databaseSyncStatus: 'HEALTHY' | 'SYNCING' | 'DISCONNECTED';
  activeSockets: number;
  errorLogCount24h: number;
  uptimePercentage: number;
  serverCpuLoadPercent: number;
  serverMemoryUsedMb: number;
}
