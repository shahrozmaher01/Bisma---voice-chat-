import {
  AdminRole,
  RolePermissions,
  HostProfile,
  AgentProfile,
  WithdrawalRequest,
  PaymentTransaction,
  FinancialLedgerEntry,
  VipPackageItem,
  EventItem,
  AdminMission,
  ReportTicket,
  AppBanner,
  PushNotificationRecord,
  BlockedWord,
  FraudAlert,
  ReferralRecord,
  SystemSettingsState,
  SystemHealthMetrics,
} from '../types/admin';
import { VirtualGift } from '../types';

export const DEFAULT_ROLE_PERMISSIONS: Record<AdminRole, RolePermissions> = {
  Owner: {
    users: { view: true, edit: true, ban: true, adjustBalance: true, assignVip: true, resetRestrictions: true },
    hosts: { view: true, approve: true, edit: true, suspend: true, manageCommission: true },
    agents: { view: true, create: true, approve: true, edit: true, suspend: true },
    rooms: { view: true, lock: true, close: true, mute: true, moderate: true },
    finance: { viewLedger: true, approveWithdrawals: true, refunds: true, editPricing: true },
    gifts: { view: true, create: true, edit: true, delete: true },
    vip: { view: true, assign: true, editPackages: true },
    events: { view: true, manage: true },
    tasks: { view: true, manage: true },
    moderation: { view: true, resolve: true, ban: true },
    content: { view: true, editBanners: true, sendPush: true, editPolicies: true },
    chat: { view: true, moderate: true, editBlockedWords: true },
    fraud: { view: true, resolveAlerts: true },
    settings: { view: true, editGeneral: true, editOwnerOnly: true },
  },
  'Super Admin': {
    users: { view: true, edit: true, ban: true, adjustBalance: true, assignVip: true, resetRestrictions: true },
    hosts: { view: true, approve: true, edit: true, suspend: true, manageCommission: true },
    agents: { view: true, create: true, approve: true, edit: true, suspend: true },
    rooms: { view: true, lock: true, close: true, mute: true, moderate: true },
    finance: { viewLedger: true, approveWithdrawals: true, refunds: true, editPricing: false },
    gifts: { view: true, create: true, edit: true, delete: true },
    vip: { view: true, assign: true, editPackages: true },
    events: { view: true, manage: true },
    tasks: { view: true, manage: true },
    moderation: { view: true, resolve: true, ban: true },
    content: { view: true, editBanners: true, sendPush: true, editPolicies: true },
    chat: { view: true, moderate: true, editBlockedWords: true },
    fraud: { view: true, resolveAlerts: true },
    settings: { view: true, editGeneral: true, editOwnerOnly: false },
  },
  Admin: {
    users: { view: true, edit: true, ban: true, adjustBalance: true, assignVip: true, resetRestrictions: false },
    hosts: { view: true, approve: true, edit: true, suspend: true, manageCommission: false },
    agents: { view: true, create: false, approve: true, edit: true, suspend: false },
    rooms: { view: true, lock: true, close: true, mute: true, moderate: true },
    finance: { viewLedger: true, approveWithdrawals: false, refunds: false, editPricing: false },
    gifts: { view: true, create: false, edit: false, delete: false },
    vip: { view: true, assign: true, editPackages: false },
    events: { view: true, manage: true },
    tasks: { view: true, manage: false },
    moderation: { view: true, resolve: true, ban: true },
    content: { view: true, editBanners: true, sendPush: true, editPolicies: false },
    chat: { view: true, moderate: true, editBlockedWords: true },
    fraud: { view: true, resolveAlerts: true },
    settings: { view: true, editGeneral: false, editOwnerOnly: false },
  },
  Moderator: {
    users: { view: true, edit: false, ban: false, adjustBalance: false, assignVip: false, resetRestrictions: false },
    hosts: { view: true, approve: false, edit: false, suspend: false, manageCommission: false },
    agents: { view: false, create: false, approve: false, edit: false, suspend: false },
    rooms: { view: true, lock: true, close: true, mute: true, moderate: true },
    finance: { viewLedger: false, approveWithdrawals: false, refunds: false, editPricing: false },
    gifts: { view: true, create: false, edit: false, delete: false },
    vip: { view: true, assign: false, editPackages: false },
    events: { view: true, manage: false },
    tasks: { view: false, manage: false },
    moderation: { view: true, resolve: true, ban: false },
    content: { view: true, editBanners: false, sendPush: false, editPolicies: false },
    chat: { view: true, moderate: true, editBlockedWords: true },
    fraud: { view: false, resolveAlerts: false },
    settings: { view: false, editGeneral: false, editOwnerOnly: false },
  },
  'Finance Manager': {
    users: { view: true, edit: false, ban: false, adjustBalance: true, assignVip: false, resetRestrictions: false },
    hosts: { view: true, approve: false, edit: false, suspend: false, manageCommission: true },
    agents: { view: true, create: false, approve: false, edit: false, suspend: false },
    rooms: { view: false, lock: false, close: false, mute: false, moderate: false },
    finance: { viewLedger: true, approveWithdrawals: true, refunds: true, editPricing: true },
    gifts: { view: true, create: false, edit: false, delete: false },
    vip: { view: true, assign: false, editPackages: true },
    events: { view: false, manage: false },
    tasks: { view: false, manage: false },
    moderation: { view: false, resolve: false, ban: false },
    content: { view: false, editBanners: false, sendPush: false, editPolicies: false },
    chat: { view: false, moderate: false, editBlockedWords: false },
    fraud: { view: true, resolveAlerts: true },
    settings: { view: true, editGeneral: false, editOwnerOnly: false },
  },
  'Host Manager': {
    users: { view: true, edit: false, ban: false, adjustBalance: false, assignVip: false, resetRestrictions: false },
    hosts: { view: true, approve: true, edit: true, suspend: true, manageCommission: false },
    agents: { view: true, create: false, approve: false, edit: false, suspend: false },
    rooms: { view: true, lock: false, close: false, mute: false, moderate: true },
    finance: { viewLedger: false, approveWithdrawals: false, refunds: false, editPricing: false },
    gifts: { view: true, create: false, edit: false, delete: false },
    vip: { view: false, assign: false, editPackages: false },
    events: { view: true, manage: true },
    tasks: { view: true, manage: true },
    moderation: { view: true, resolve: false, ban: false },
    content: { view: false, editBanners: false, sendPush: false, editPolicies: false },
    chat: { view: false, moderate: false, editBlockedWords: false },
    fraud: { view: false, resolveAlerts: false },
    settings: { view: false, editGeneral: false, editOwnerOnly: false },
  },
  'Agent Manager': {
    users: { view: true, edit: false, ban: false, adjustBalance: false, assignVip: false, resetRestrictions: false },
    hosts: { view: true, approve: true, edit: true, suspend: false, manageCommission: true },
    agents: { view: true, create: true, approve: true, edit: true, suspend: true },
    rooms: { view: false, lock: false, close: false, mute: false, moderate: false },
    finance: { viewLedger: true, approveWithdrawals: false, refunds: false, editPricing: false },
    gifts: { view: false, create: false, edit: false, delete: false },
    vip: { view: false, assign: false, editPackages: false },
    events: { view: false, manage: false },
    tasks: { view: false, manage: false },
    moderation: { view: false, resolve: false, ban: false },
    content: { view: false, editBanners: false, sendPush: false, editPolicies: false },
    chat: { view: false, moderate: false, editBlockedWords: false },
    fraud: { view: false, resolveAlerts: false },
    settings: { view: false, editGeneral: false, editOwnerOnly: false },
  },
  'Support Staff': {
    users: { view: true, edit: false, ban: false, adjustBalance: false, assignVip: false, resetRestrictions: false },
    hosts: { view: true, approve: false, edit: false, suspend: false, manageCommission: false },
    agents: { view: false, create: false, approve: false, edit: false, suspend: false },
    rooms: { view: true, lock: false, close: false, mute: true, moderate: false },
    finance: { viewLedger: false, approveWithdrawals: false, refunds: false, editPricing: false },
    gifts: { view: true, create: false, edit: false, delete: false },
    vip: { view: false, assign: false, editPackages: false },
    events: { view: false, manage: false },
    tasks: { view: false, manage: false },
    moderation: { view: true, resolve: true, ban: false },
    content: { view: true, editBanners: false, sendPush: false, editPolicies: false },
    chat: { view: true, moderate: true, editBlockedWords: false },
    fraud: { view: false, resolveAlerts: false },
    settings: { view: false, editGeneral: false, editOwnerOnly: false },
  },
};

const STORAGE_PREFIX = 'aura_admin_v2_';

function loadOrInit<T>(key: string, initial: T): T {
  try {
    const raw = localStorage.getItem(STORAGE_PREFIX + key);
    if (!raw) {
      localStorage.setItem(STORAGE_PREFIX + key, JSON.stringify(initial));
      return initial;
    }
    return JSON.parse(raw);
  } catch {
    return initial;
  }
}

function save<T>(key: string, data: T): void {
  try {
    localStorage.setItem(STORAGE_PREFIX + key, JSON.stringify(data));
  } catch (err) {
    console.error('Failed to save to storage:', key, err);
  }
}

// Initial System Settings
const INITIAL_SETTINGS: SystemSettingsState = {
  appName: 'AURA Live',
  platformTitle: 'AURA Live Voice Chat & Social Entertainment',
  logoUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=120',
  maintenanceMode: false,
  maintenanceMessage: 'Platform is undergoing routine maintenance. Audio rooms will resume shortly.',
  registrationOpen: true,
  guestAccessAllowed: true,
  currencySymbol: '$',
  usdToCoinRate: 30000,
  diamondToCoinRate: 10,
  diamondCashoutRateUsd: 1000,
  minWithdrawalUsd: 20,
  maxWithdrawalUsd: 5000,
  dailyWithdrawalLimitUsd: 10000,
  defaultHostCommissionPercent: 65,
  defaultAgentCommissionPercent: 12,
  platformFeePercent: 10,
  autoSpamFilterActive: true,
  privateChatMonitoringPolicy: 'FLAGGED_ONLY',
  featureFlags: {
    enableLuckyBags: true,
    enableMoments: true,
    enableWithdrawals: true,
    enableStorePurchases: true,
    enableReferralSystem: true,
    enableVoiceEffects: true,
    enableOfficialFrameTransfers: true,
    enableLiveRoomCreations: true,
  },
};

// Initial Hosts
const INITIAL_HOSTS: HostProfile[] = [
  {
    id: 'host_01',
    userId: 'usr_78912',
    username: 'Ali Raza',
    avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300',
    agencyId: 'agency_01',
    agencyName: 'Bisma Star Agency',
    level: 25,
    status: 'APPROVED',
    isLive: true,
    currentRoomId: 'room_acoustic_guitar',
    totalRoomHours: 142.5,
    totalReceivedGiftsCoins: 890000,
    totalEarnedDiamonds: 28000,
    withdrawableDiamonds: 18400,
    commissionRate: 0.65,
    monthlyTargetHours: 60,
    warningCount: 0,
    rank: 1,
    appliedDate: '2025-01-02',
    approvedDate: '2025-01-03',
    notes: 'Premier acoustic voice host with spotless record.',
  },
  {
    id: 'host_02',
    userId: 'usr_45623',
    username: 'Bisma Noor',
    avatarUrl: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300',
    agencyId: 'agency_01',
    agencyName: 'Bisma Star Agency',
    level: 32,
    status: 'APPROVED',
    isLive: false,
    totalRoomHours: 210.0,
    totalReceivedGiftsCoins: 1650000,
    totalEarnedDiamonds: 42000,
    withdrawableDiamonds: 31000,
    commissionRate: 0.7,
    monthlyTargetHours: 40,
    warningCount: 0,
    rank: 2,
    appliedDate: '2024-12-15',
    approvedDate: '2024-12-16',
    notes: 'Agency owner and verified music talent.',
  },
  {
    id: 'host_03',
    userId: 'usr_88219',
    username: 'Zara Khan',
    avatarUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300',
    agencyId: 'agency_02',
    agencyName: 'Lahore Melody Group',
    level: 18,
    status: 'PENDING',
    isLive: false,
    totalRoomHours: 18.0,
    totalReceivedGiftsCoins: 95000,
    totalEarnedDiamonds: 6500,
    withdrawableDiamonds: 6500,
    commissionRate: 0.6,
    monthlyTargetHours: 40,
    warningCount: 0,
    rank: 8,
    appliedDate: '2025-02-18',
    notes: 'Audition demo submitted. Awaiting voice test review.',
  },
];

// Initial Agencies
const INITIAL_AGENTS: AgentProfile[] = [
  {
    id: 'agency_01',
    name: 'Bisma Star Agency',
    code: 'BSA-900',
    ownerUserId: 'usr_45623',
    ownerUsername: 'Bisma Noor',
    avatarUrl: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300',
    status: 'ACTIVE',
    hostCount: 14,
    totalEarningsUsd: 14850.0,
    pendingPayoutUsd: 1250.0,
    commissionPercent: 12,
    tier: 'Diamond Elite',
    country: '🇵🇰 Pakistan',
    contactEmail: 'agency@bismastar.com',
    contactPhone: '+92 300 1234567',
    createdDate: '2024-11-10',
    assignedHostIds: ['usr_78912', 'usr_45623'],
  },
  {
    id: 'agency_02',
    name: 'Lahore Melody Group',
    code: 'LMG-442',
    ownerUserId: 'usr_99182',
    ownerUsername: 'Hamza Director',
    avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300',
    status: 'ACTIVE',
    hostCount: 9,
    totalEarningsUsd: 7420.0,
    pendingPayoutUsd: 580.0,
    commissionPercent: 10,
    tier: 'Gold Platinum',
    country: '🇵🇰 Pakistan',
    contactEmail: 'contact@lahoremelody.pk',
    contactPhone: '+92 321 9876543',
    createdDate: '2024-12-01',
    assignedHostIds: ['usr_88219'],
  },
];

// Initial Withdrawals
const INITIAL_WITHDRAWALS: WithdrawalRequest[] = [
  {
    id: 'wd_101',
    recipientType: 'HOST',
    userId: 'usr_78912',
    userName: 'Ali Raza',
    amountDiamonds: 15000,
    amountUsd: 150.0,
    feeUsd: 4.5,
    netPayoutUsd: 145.5,
    paymentMethod: 'EasyPaisa',
    accountDetails: {
      accountNumber: '0301-5551234',
      accountTitle: 'Ali Raza',
      bankName: 'Telenor Microfinance Bank',
    },
    status: 'PENDING',
    requestedAt: Date.now() - 3600000 * 4,
    adminNotes: 'Awaiting finance approval for weekly host earnings.',
  },
  {
    id: 'wd_100',
    recipientType: 'AGENT',
    userId: 'usr_45623',
    userName: 'Bisma Noor',
    amountDiamonds: 50000,
    amountUsd: 500.0,
    feeUsd: 15.0,
    netPayoutUsd: 485.0,
    paymentMethod: 'Bank Transfer',
    accountDetails: {
      accountNumber: 'PK36MEZN0000123456789012',
      accountTitle: 'Bisma Noor Official',
      bankName: 'Meezan Bank Ltd',
      routingOrIban: 'MEZNPKKA',
    },
    status: 'COMPLETED',
    requestedAt: Date.now() - 86400000 * 2,
    processedAt: Date.now() - 86400000,
    processedByAdminId: '565656565666555',
    processedByAdminName: 'Sherry',
    transactionRef: 'MEZN-TRX-881923',
    adminNotes: 'Verified agency performance target payout.',
  },
];

// Initial Payments
const INITIAL_PAYMENTS: PaymentTransaction[] = [
  {
    id: 'pay_9941',
    userId: 'usr_33109',
    userName: 'Usman VIP',
    packageId: 'pkg_titan',
    packageTitle: 'Titan Sovereign Vault',
    amountUsd: 100.0,
    coinsAwarded: 3000000,
    provider: 'Stripe',
    status: 'SUCCESS',
    paymentGatewayRef: 'ch_3Nxy828192AURA',
    timestamp: Date.now() - 3600000 * 2,
    ipAddress: '39.40.112.45',
  },
  {
    id: 'pay_9940',
    userId: 'usr_77123',
    userName: 'Farhan Sheikh',
    packageId: 'pkg_king',
    packageTitle: 'Imperial King Chest',
    amountUsd: 50.0,
    coinsAwarded: 1500000,
    provider: 'Google Play IAP',
    status: 'SUCCESS',
    paymentGatewayRef: 'GPA.3391-4921-9912',
    timestamp: Date.now() - 3600000 * 5,
    ipAddress: '182.185.22.9',
  },
  {
    id: 'pay_9939',
    userId: 'usr_10928',
    userName: 'Khurram',
    packageId: 'pkg_popular',
    packageTitle: 'Popular Host Pack',
    amountUsd: 5.0,
    coinsAwarded: 150000,
    provider: 'EasyPaisa',
    status: 'SUCCESS',
    paymentGatewayRef: 'EP-99120491',
    timestamp: Date.now() - 86400000,
    ipAddress: '39.51.88.19',
  },
];

// Initial VIP Packages
const INITIAL_VIP_PACKAGES: VipPackageItem[] = [
  {
    level: 1,
    title: 'VIP 1 - Bronze Star',
    badge: '👑 VIP 1',
    color: '#CD7F32',
    priceUsdMonthly: 2.99,
    priceCoinsMonthly: 90000,
    privileges: ['Bronze Nameplate', 'Daily +500 Coins', 'Standard Chat Bubble'],
    activeSubscribers: 142,
    dailyBonusCoins: 500,
  },
  {
    level: 3,
    title: 'VIP 3 - Silver Knight',
    badge: '👑 VIP 3',
    color: '#C0C0C0',
    priceUsdMonthly: 9.99,
    priceCoinsMonthly: 300000,
    privileges: ['Silver Shield Badge', 'Daily +2,000 Coins', 'Mic Priority Seat 8', 'Special Join Sound'],
    activeSubscribers: 88,
    dailyBonusCoins: 2000,
  },
  {
    level: 5,
    title: 'VIP 5 - Gold Sovereign',
    badge: '👑 VIP 5',
    color: '#FFD700',
    priceUsdMonthly: 29.99,
    priceCoinsMonthly: 900000,
    privileges: ['Golden Animated Aura', 'Daily +8,000 Coins', 'Kick Immunity', 'Exclusive Dragon Ride'],
    activeSubscribers: 45,
    dailyBonusCoins: 8000,
  },
  {
    level: 7,
    title: 'VIP 7 - Diamond Monarch',
    badge: '👑 VIP 7',
    color: '#00E5FF',
    priceUsdMonthly: 99.99,
    priceCoinsMonthly: 3000000,
    privileges: ['Royal Diamond Crown', 'Daily +30,000 Coins', 'Server-Wide Join Announcement', 'Direct Concierge Support'],
    activeSubscribers: 12,
    dailyBonusCoins: 30000,
  },
];

// Initial Events
const INITIAL_EVENTS: EventItem[] = [
  {
    id: 'evt_01',
    title: 'Spring Gifting Carnival 2025',
    description: 'Compete for top gifting glory across all voice rooms! Massive diamond and coin rewards for top 10 hosts and spenders.',
    bannerUrl: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600',
    startDate: '2025-03-01',
    endDate: '2025-03-15',
    rules: 'Every gift sent generates 1 point per 100 coins. Double points in official partner rooms.',
    rewardPoolCoins: 5000000,
    rewardPoolDiamonds: 100000,
    category: 'Gifting Carnival',
    isActive: true,
    participantsCount: 840,
    leaderboardTopUser: 'Sherry (Owner)',
  },
  {
    id: 'evt_02',
    title: 'Voice Idol Championship Season 1',
    description: 'Solo acoustic and vocal live battle. Judged by top agency directors with official host contracts awarded.',
    bannerUrl: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600',
    startDate: '2025-03-10',
    endDate: '2025-03-25',
    rules: 'Mic battle duration 3 minutes per round. Audience votes with gift reactions.',
    rewardPoolCoins: 2000000,
    rewardPoolDiamonds: 50000,
    category: 'Voice Battle',
    isActive: true,
    participantsCount: 320,
    leaderboardTopUser: 'Ali Raza',
  },
];

// Initial Reports
const INITIAL_REPORTS: ReportTicket[] = [
  {
    id: 'rep_001',
    targetType: 'USER',
    targetId: 'usr_spammer_99',
    targetName: 'BotAccount_81',
    reporterId: 'usr_45623',
    reporterName: 'Bisma Noor',
    category: 'Spam Bot',
    description: 'User is posting advertising telegram links in multiple public rooms simultaneously.',
    status: 'PENDING',
    createdAt: Date.now() - 3600000 * 3,
  },
  {
    id: 'rep_002',
    targetType: 'VOICE_ROOM',
    targetId: 'room_late_night_chat',
    targetName: 'Late Night Talk Lounge',
    reporterId: 'usr_78912',
    reporterName: 'Ali Raza',
    category: 'Abuse & Harassment',
    description: 'Unmuted speaker abusing mic rules and creating noisy disturbance.',
    status: 'INVESTIGATING',
    assignedAdminName: 'Sherry',
    adminNotes: 'Reviewing audio stage recordings.',
    createdAt: Date.now() - 3600000 * 6,
  },
];

// Initial Banners
const INITIAL_BANNERS: AppBanner[] = [
  {
    id: 'ban_01',
    title: 'New Exchange Rate: $1 = 30,000 Coins!',
    imageUrl: 'https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=800',
    linkUrl: '#wallet',
    placement: 'Home Carousel',
    isActive: true,
    displayOrder: 1,
    startsAt: '2025-01-01',
    endsAt: '2025-12-31',
  },
  {
    id: 'ban_02',
    title: 'Voice Idol 2025 Tournament Live Auditions',
    imageUrl: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800',
    linkUrl: '#events',
    placement: 'Party Header',
    isActive: true,
    displayOrder: 2,
    startsAt: '2025-03-01',
    endsAt: '2025-03-31',
  },
];

// Initial Blocked Words
const INITIAL_BLOCKED_WORDS: BlockedWord[] = [
  { id: 'bw_1', keyword: 'free coins hack', category: 'Scam / Phishing', severity: 'AUTO_MUTE', createdAt: Date.now() },
  { id: 'bw_2', keyword: 'telegram crypto', category: 'Scam / Phishing', severity: 'BLOCK', createdAt: Date.now() },
  { id: 'bw_3', keyword: 'whatsapp cheat', category: 'Profanity', severity: 'WARN', createdAt: Date.now() },
];

// Initial Fraud Alerts
const INITIAL_FRAUD_ALERTS: FraudAlert[] = [
  {
    id: 'fa_101',
    severity: 'HIGH',
    type: 'MULTI_ACCOUNT_DEVICE',
    userId: 'usr_suspect_41',
    userName: 'User_41X',
    riskScore: 88,
    details: 'Device fingerprint matches 6 distinct accounts registered in past 2 hours.',
    deviceFingerprint: 'DEV_ANDR_SM_G998B_99218',
    ipAddress: '182.180.19.44',
    status: 'OPEN',
    detectedAt: Date.now() - 3600000 * 2,
  },
  {
    id: 'fa_102',
    severity: 'MEDIUM',
    type: 'ABNORMAL_GIFT_VELOCITY',
    userId: 'usr_bot_90',
    userName: 'RapidSpender_09',
    riskScore: 65,
    details: 'Transferred 2,000,000 coins across 4 rooms within 90 seconds.',
    deviceFingerprint: 'DEV_WEB_CHROME_LINUX_33',
    ipAddress: '103.255.4.12',
    status: 'INVESTIGATING',
    detectedAt: Date.now() - 3600000 * 5,
  },
];

// Initial Push Records
const INITIAL_PUSH_LOGS: PushNotificationRecord[] = [
  {
    id: 'push_1',
    title: 'Grand Gala Voice Party Tonight! 🎉',
    body: 'Join the Owner room with Sherry and top talent for lucky bags and diamond giveaways.',
    targetAudience: 'ALL_USERS',
    recipientCount: 4820,
    status: 'SENT',
    sentAt: Date.now() - 86400000,
    sentByAdminName: 'Sherry',
  },
];

class AdminBackendService {
  private hosts: HostProfile[];
  private agents: AgentProfile[];
  private withdrawals: WithdrawalRequest[];
  private payments: PaymentTransaction[];
  private vipPackages: VipPackageItem[];
  private events: EventItem[];
  private reports: ReportTicket[];
  private banners: AppBanner[];
  private blockedWords: BlockedWord[];
  private fraudAlerts: FraudAlert[];
  private pushLogs: PushNotificationRecord[];
  private settings: SystemSettingsState;
  private listeners: (() => void)[] = [];

  constructor() {
    this.hosts = loadOrInit('hosts', INITIAL_HOSTS);
    this.agents = loadOrInit('agents', INITIAL_AGENTS);
    this.withdrawals = loadOrInit('withdrawals', INITIAL_WITHDRAWALS);
    this.payments = loadOrInit('payments', INITIAL_PAYMENTS);
    this.vipPackages = loadOrInit('vip', INITIAL_VIP_PACKAGES);
    this.events = loadOrInit('events', INITIAL_EVENTS);
    this.reports = loadOrInit('reports', INITIAL_REPORTS);
    this.banners = loadOrInit('banners', INITIAL_BANNERS);
    this.blockedWords = loadOrInit('blocked_words', INITIAL_BLOCKED_WORDS);
    this.fraudAlerts = loadOrInit('fraud_alerts', INITIAL_FRAUD_ALERTS);
    this.pushLogs = loadOrInit('push_logs', INITIAL_PUSH_LOGS);
    this.settings = loadOrInit('settings', INITIAL_SETTINGS);
  }

  public subscribe(listener: () => void): () => void {
    this.listeners.push(listener);
    return () => {
      this.listeners = this.listeners.filter((l) => l !== listener);
    };
  }

  private notify() {
    this.listeners.forEach((l) => l());
  }

  // Permissions check
  public hasPermission(
    role: AdminRole,
    module: keyof RolePermissions,
    action: string
  ): boolean {
    const roleConfig = DEFAULT_ROLE_PERMISSIONS[role];
    if (!roleConfig) return false;
    const modulePerms = roleConfig[module] as Record<string, boolean>;
    if (!modulePerms) return false;
    return Boolean(modulePerms[action]);
  }

  // System Settings
  public getSettings(): SystemSettingsState {
    return { ...this.settings };
  }

  public updateSettings(partial: Partial<SystemSettingsState>): void {
    this.settings = { ...this.settings, ...partial };
    save('settings', this.settings);
    this.notify();
  }

  // Hosts
  public getHosts(): HostProfile[] {
    return [...this.hosts];
  }

  public approveHost(hostId: string, notes?: string): void {
    this.hosts = this.hosts.map((h) =>
      h.id === hostId
        ? { ...h, status: 'APPROVED', approvedDate: new Date().toISOString().slice(0, 10), notes: notes || h.notes }
        : h
    );
    save('hosts', this.hosts);
    this.notify();
  }

  public rejectHost(hostId: string, reason?: string): void {
    this.hosts = this.hosts.map((h) =>
      h.id === hostId ? { ...h, status: 'REJECTED', notes: reason || h.notes } : h
    );
    save('hosts', this.hosts);
    this.notify();
  }

  public updateHost(hostId: string, updates: Partial<HostProfile>): void {
    this.hosts = this.hosts.map((h) => (h.id === hostId ? { ...h, ...updates } : h));
    save('hosts', this.hosts);
    this.notify();
  }

  // Agents
  public getAgents(): AgentProfile[] {
    return [...this.agents];
  }

  public createAgent(agent: Omit<AgentProfile, 'id' | 'createdDate' | 'totalEarningsUsd' | 'pendingPayoutUsd' | 'hostCount'>): void {
    const newAgent: AgentProfile = {
      ...agent,
      id: `agency_${Date.now()}`,
      createdDate: new Date().toISOString().slice(0, 10),
      totalEarningsUsd: 0,
      pendingPayoutUsd: 0,
      hostCount: agent.assignedHostIds.length,
    };
    this.agents = [newAgent, ...this.agents];
    save('agents', this.agents);
    this.notify();
  }

  public updateAgent(agentId: string, updates: Partial<AgentProfile>): void {
    this.agents = this.agents.map((a) => (a.id === agentId ? { ...a, ...updates } : a));
    save('agents', this.agents);
    this.notify();
  }

  // Withdrawals
  public getWithdrawals(): WithdrawalRequest[] {
    return [...this.withdrawals];
  }

  public processWithdrawal(
    id: string,
    status: 'APPROVED' | 'REJECTED' | 'COMPLETED',
    adminId: string,
    adminName: string,
    notes?: string,
    ref?: string
  ): void {
    this.withdrawals = this.withdrawals.map((w) =>
      w.id === id
        ? {
            ...w,
            status,
            processedAt: Date.now(),
            processedByAdminId: adminId,
            processedByAdminName: adminName,
            adminNotes: notes || w.adminNotes,
            transactionRef: ref || w.transactionRef,
          }
        : w
    );
    save('withdrawals', this.withdrawals);
    this.notify();
  }

  // Payments
  public getPayments(): PaymentTransaction[] {
    return [...this.payments];
  }

  public refundPayment(paymentId: string, reason: string): void {
    this.payments = this.payments.map((p) =>
      p.id === paymentId ? { ...p, status: 'REFUNDED', refundReason: reason } : p
    );
    save('payments', this.payments);
    this.notify();
  }

  // VIP
  public getVipPackages(): VipPackageItem[] {
    return [...this.vipPackages];
  }

  public updateVipPackage(level: number, updates: Partial<VipPackageItem>): void {
    this.vipPackages = this.vipPackages.map((v) => (v.level === level ? { ...v, ...updates } : v));
    save('vip', this.vipPackages);
    this.notify();
  }

  // Events
  public getEvents(): EventItem[] {
    return [...this.events];
  }

  public saveEvent(event: EventItem): void {
    const exists = this.events.some((e) => e.id === event.id);
    if (exists) {
      this.events = this.events.map((e) => (e.id === event.id ? event : e));
    } else {
      this.events = [event, ...this.events];
    }
    save('events', this.events);
    this.notify();
  }

  public deleteEvent(id: string): void {
    this.events = this.events.filter((e) => e.id !== id);
    save('events', this.events);
    this.notify();
  }

  // Reports
  public getReports(): ReportTicket[] {
    return [...this.reports];
  }

  public resolveReport(
    id: string,
    status: 'RESOLVED' | 'DISMISSED' | 'INVESTIGATING',
    actionTaken: ReportTicket['actionTaken'],
    notes?: string,
    adminName?: string
  ): void {
    this.reports = this.reports.map((r) =>
      r.id === id
        ? {
            ...r,
            status,
            actionTaken: actionTaken || r.actionTaken,
            adminNotes: notes || r.adminNotes,
            assignedAdminName: adminName || r.assignedAdminName,
            resolvedAt: status === 'RESOLVED' || status === 'DISMISSED' ? Date.now() : undefined,
          }
        : r
    );
    save('reports', this.reports);
    this.notify();
  }

  // Banners
  public getBanners(): AppBanner[] {
    return [...this.banners];
  }

  public saveBanner(banner: AppBanner): void {
    const exists = this.banners.some((b) => b.id === banner.id);
    if (exists) {
      this.banners = this.banners.map((b) => (b.id === banner.id ? banner : b));
    } else {
      this.banners = [banner, ...this.banners];
    }
    save('banners', this.banners);
    this.notify();
  }

  public deleteBanner(id: string): void {
    this.banners = this.banners.filter((b) => b.id !== id);
    save('banners', this.banners);
    this.notify();
  }

  // Blocked Words
  public getBlockedWords(): BlockedWord[] {
    return [...this.blockedWords];
  }

  public addBlockedWord(keyword: string, category: BlockedWord['category'], severity: BlockedWord['severity']): void {
    const newWord: BlockedWord = {
      id: `bw_${Date.now()}`,
      keyword: keyword.trim().toLowerCase(),
      category,
      severity,
      createdAt: Date.now(),
    };
    this.blockedWords = [newWord, ...this.blockedWords];
    save('blocked_words', this.blockedWords);
    this.notify();
  }

  public deleteBlockedWord(id: string): void {
    this.blockedWords = this.blockedWords.filter((b) => b.id !== id);
    save('blocked_words', this.blockedWords);
    this.notify();
  }

  // Fraud
  public getFraudAlerts(): FraudAlert[] {
    return [...this.fraudAlerts];
  }

  public resolveFraudAlert(id: string, status: FraudAlert['status']): void {
    this.fraudAlerts = this.fraudAlerts.map((f) => (f.id === id ? { ...f, status } : f));
    save('fraud_alerts', this.fraudAlerts);
    this.notify();
  }

  // Push
  public getPushLogs(): PushNotificationRecord[] {
    return [...this.pushLogs];
  }

  public sendPush(record: Omit<PushNotificationRecord, 'id' | 'sentAt' | 'status'>): void {
    const newRecord: PushNotificationRecord = {
      ...record,
      id: `push_${Date.now()}`,
      sentAt: Date.now(),
      status: 'SENT',
    };
    this.pushLogs = [newRecord, ...this.pushLogs];
    save('push_logs', this.pushLogs);
    this.notify();
  }

  // System Health
  public getSystemHealth(): SystemHealthMetrics {
    return {
      apiLatencyMs: Math.floor(22 + Math.random() * 8),
      webrtcAudioStatus: 'OPERATIONAL',
      databaseSyncStatus: 'HEALTHY',
      activeSockets: 142 + Math.floor(Math.random() * 10),
      errorLogCount24h: 0,
      uptimePercentage: 99.98,
      serverCpuLoadPercent: Math.floor(18 + Math.random() * 7),
      serverMemoryUsedMb: Math.floor(380 + Math.random() * 20),
    };
  }

  // CSV Export Utility
  public exportCsv(filename: string, rows: Record<string, any>[]): void {
    if (!rows || !rows.length) return;
    const headers = Object.keys(rows[0]);
    const csvContent = [
      headers.join(','),
      ...rows.map((row) =>
        headers
          .map((header) => {
            const val = row[header];
            if (val === null || val === undefined) return '""';
            const str = String(val).replace(/"/g, '""');
            return `"${str}"`;
          })
          .join(',')
      ),
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', `${filename}_${new Date().toISOString().slice(0, 10)}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  }
}

export const adminBackend = new AdminBackendService();
