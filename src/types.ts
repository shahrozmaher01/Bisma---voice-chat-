export type AdminRoleType =
  | 'Super Admin'
  | 'Admin'
  | 'Manager'
  | 'Admin Leader'
  | 'BD Leader'
  | 'BD'
  | 'Agency Leader'
  | 'Agency'
  | 'Super Coin Reseller'
  | 'Coin Reseller'
  | 'CS Leader'
  | 'CS'
  | 'Official'
  | 'Host'
  | 'User';

export interface User {
  id: string;
  username: string;
  avatarUrl: string;
  gender: 'Female' | 'Male' | 'Not specified';
  dateOfBirth: string;
  email?: string;
  bio: string;
  country: string;
  language: string;
  coins: number;
  diamonds: number;
  userLevel: number;
  richLevel: number;
  charmLevel: number;
  vipLevel: number;
  followersCount: number;
  followingCount: number;
  friendsCount: number;
  visitorsCount: number;
  equippedFrameId?: string;
  equippedBubbleId?: string;
  equippedRideId?: string;
  equippedWallpaperId?: string;
  agencyId?: string;
  agencyName?: string;
  role: AdminRoleType;
  isBanned: boolean;
  accountStatus: 'ACTIVE' | 'BANNED' | 'MUTED';
  authProvider: 'aura_id' | 'google';
  createdAt: number;
  lastLoginAt: number;
}

export interface VoiceRoom {
  id: string;
  title: string;
  description: string;
  coverUrl: string;
  wallpaperUrl?: string;
  ownerId: string;
  ownerName: string;
  ownerAvatar: string;
  seatCount: number; // usually 8
  onlineCount: number;
  isLocked: boolean;
  category: string;
  country: string;
  announcement: string;
  adminUserIds: string[]; // comma or array
  isFeatured?: boolean;
  isActive: boolean;
  createdAt: number;
}

export interface RoomSeat {
  seatIndex: number; // 0 is Host, 1-6 is regular, 7 is VIP seat 8
  roomId: string;
  userId?: string;
  username?: string;
  avatarUrl?: string;
  vipLevel: number;
  role?: AdminRoleType;
  equippedFrameId?: string;
  isLocked: boolean;
  isMuted: boolean;
  isSpeaking: boolean;
}

export interface ChatMessage {
  id: string;
  targetId: string; // roomId or direct chat userId
  isRoomChat: boolean;
  senderId: string;
  senderName: string;
  senderAvatar: string;
  senderVip: number;
  senderRole?: AdminRoleType;
  content: string;
  giftName?: string;
  giftIcon?: string;
  giftCount?: number;
  timestamp: number;
}

export interface VirtualGift {
  id: string;
  name: string;
  iconEmoji: string;
  costCoins: number;
  charmPoints: number;
  animationType: 'bloom' | 'hearts' | 'drive' | 'cruise' | 'launch' | 'royalty' | 'box' | 'wheel';
  isLucky?: boolean;
  multiplierMax?: number;
}

export type TabType = 'party' | 'moments' | 'messages' | 'store' | 'profile';

export type StoreItemCategory = 'Frame' | 'Bubble' | 'Ride' | 'Wallpaper';

export interface StoreItem {
  id: string;
  name: string;
  category: StoreItemCategory;
  price: number;
  previewIcon: string;
  durationDays: number;
  isOwned: boolean;
  isEquipped: boolean;
  description: string;
  rarity?: 'Common' | 'Rare' | 'Epic' | 'Legendary' | 'Official';
}

export interface OfficialFrameDef {
  id: string;
  name: string;
  badgeLabel: string;
  description: string;
  primaryColor: string;
  secondaryColor: string;
  iconEmoji: string;
  isImportantOfficial: boolean;
}

export interface OfficialFrameAssignment {
  id: string;
  userId: string;
  userName: string;
  frameId: string;
  frameName: string;
  days: number;
  sendDate: number;
  expiryDate: number;
  status: 'Active' | 'Expired' | 'Revoked';
  adminId: string;
  adminName: string;
  sendDateFormatted: string;
  expiryDateFormatted: string;
  remainingDays: number;
}

export interface AdminLinkUser {
  id: string;
  adminId: string;
  userId: string;
  userName: string;
  userAvatar: string;
  status: string;
  assignedWork: string;
  workStatus: string;
  workCategory: string;
  targetHours: number;
  completedHours: number;
  targetDiamonds: number;
  earnedDiamonds: number;
  activityInfo: string;
  lastActive: string;
  joinedDate: string;
  notes: string;
  updatedAt: number;
}

export interface AuditLogEntity {
  id: string;
  adminId: string;
  adminName: string;
  adminRole: string;
  action: string;
  targetType: string;
  targetId: string;
  targetName: string;
  previousValue?: string | null;
  newValue?: string | null;
  isSuccess: boolean;
  timestamp: number;
}

export interface MomentComment {
  id: string;
  userId: string;
  userName: string;
  userAvatar: string;
  content: string;
  timestamp: number;
}

export interface Moment {
  id: string;
  userId: string;
  userName: string;
  userAvatar: string;
  vipLevel: number;
  content: string;
  mediaUrls: string[];
  likesCount: number;
  isLiked: boolean;
  comments: MomentComment[];
  timestamp: number;
}

export interface FriendItem {
  id: string;
  friendId: string;
  friendName: string;
  friendAvatar: string;
  friendVip: number;
  isOnline: boolean;
  lastMessage?: string;
  lastMessageTime?: number;
  unreadCount: number;
}

export interface NotificationItem {
  id: string;
  type: 'system' | 'gift' | 'room' | 'official' | 'announcement';
  title: string;
  message: string;
  senderName: string;
  timestamp: number;
  isRead: boolean;
}

export interface RechargePackage {
  id: string;
  title: string;
  coins: number;
  bonusDiamonds: number;
  priceUsd: number;
  tag?: string;
}

export interface AuraTask {
  id: string;
  title: string;
  description: string;
  rewardCoins: number;
  rewardDiamonds: number;
  targetCount: number;
  currentCount: number;
  isCompleted: boolean;
  isClaimed: boolean;
  iconEmoji: string;
}

export interface LuckyBag {
  id: string;
  roomId: string;
  senderId: string;
  senderName: string;
  totalCoins: number;
  remainingCoins: number;
  totalClaimers: number;
  claimers: { userId: string; userName: string; coinsClaimed: number }[];
  expiresAt: number;
}
