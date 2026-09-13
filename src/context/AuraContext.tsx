import React, { createContext, useContext, useState, useEffect, useCallback, ReactNode } from 'react';
import {
  User,
  VoiceRoom,
  RoomSeat,
  ChatMessage,
  VirtualGift,
  StoreItem,
  OfficialFrameDef,
  OfficialFrameAssignment,
  AdminLinkUser,
  AuditLogEntity,
  Moment,
  RechargePackage,
  AuraTask,
  NotificationItem,
  LuckyBag,
  AdminRoleType,
  TabType,
} from '../types';
import {
  OFFICIAL_FRAMES,
  INITIAL_USERS,
  INITIAL_ROOMS,
  VIRTUAL_GIFTS,
  STORE_ITEMS,
  RECHARGE_PACKAGES,
  INITIAL_TASKS,
  INITIAL_MOMENTS,
  INITIAL_FRAME_ASSIGNMENTS,
  INITIAL_LINK_USERS,
  INITIAL_AUDIT_LOGS,
  INITIAL_NOTIFICATIONS,
} from '../data/seedData';
import { soundManager } from '../utils/audio';

const STORAGE_KEY = 'aura_live_app_state_v1';

interface AuraContextType {
  currentUser: User | null;
  allUsers: User[];
  rooms: VoiceRoom[];
  activeRoom: VoiceRoom | null;
  activeRoomSeats: RoomSeat[];
  roomMessages: ChatMessage[];
  activeLuckyBag: LuckyBag | null;
  floatingGiftAlert: { sender: string; gift: VirtualGift; target: string } | null;
  officialFrames: OfficialFrameDef[];
  frameAssignments: OfficialFrameAssignment[];
  adminLinkUsers: AdminLinkUser[];
  auditLogs: AuditLogEntity[];
  storeItems: StoreItem[];
  moments: Moment[];
  notifications: NotificationItem[];
  tasks: AuraTask[];
  unreadNotifCount: number;
  currentTab: TabType;
  setCurrentTab: (tab: TabType) => void;

  // Auth & Session
  login: (idOrEmail: string) => { success: boolean; message?: string };
  register: (username: string, gender: 'Female' | 'Male', customId?: string) => { success: boolean; message?: string };
  loginWithGoogle: (email: string, name: string) => void;
  logout: () => void;
  switchUser: (userId: string) => void;
  updateProfile: (updates: Partial<User>) => void;

  // Room & Audio Stage
  enterRoom: (roomId: string) => void;
  leaveRoom: () => void;
  takeSeat: (seatIndex: number) => boolean;
  leaveSeat: () => void;
  toggleMic: () => void;
  toggleSeatLock: (seatIndex: number) => void;
  hostTakeDownUser: (seatIndex: number) => void;
  hostMuteAll: () => void;
  hostLockEmptySeats: () => void;
  hostUnlockAllSeats: () => void;
  hostClearChat: () => void;
  hostCloseRoom: (roomId: string) => void;
  createRoom: (title: string, category: string) => string;
  sendRoomMessage: (text: string) => void;
  sendGift: (gift: VirtualGift, targetUserId: string) => boolean;
  dropLuckyBag: (totalCoins: number, maxClaimers: number) => boolean;
  claimLuckyBag: () => { success: boolean; coinsWon?: number; message?: string };
  playRoomSound: (soundKey: string) => void;

  // Official Frame Management
  sendOfficialFrame: (targetUserId: string, frameId: string, days: number) => { success: boolean; message: string };
  revokeOfficialFrame: (assignmentId: string) => boolean;

  // Admin & Management
  assignUserRole: (targetUserId: string, newRole: AdminRoleType) => { success: boolean; message: string };
  banUser: (userId: string, isBanned: boolean) => void;
  adjustUserBalance: (userId: string, deltaCoins: number, deltaDiamonds: number) => void;
  addLinkUser: (data: Partial<AdminLinkUser>) => void;
  updateLinkUserWork: (id: string, updates: Partial<AdminLinkUser>) => void;
  broadcastAnnouncement: (title: string, message: string) => void;

  // Store & Wallet
  buyStoreItem: (itemId: string) => boolean;
  equipStoreItem: (itemId: string, category: string) => void;
  rechargeCoins: (pkg: RechargePackage) => void;
  convertDiamondsToCoins: (diamonds: number) => boolean;

  // Social & Community
  likeMoment: (momentId: string) => void;
  addMomentComment: (momentId: string, content: string) => void;
  postMoment: (content: string, mediaUrl?: string) => void;
  followUser: (targetUserId: string) => void;
  claimTaskReward: (taskId: string) => boolean;
  claimDailyTaskReward: (taskId: string) => boolean;
  markNotificationsAsRead: () => void;
}

const AuraContext = createContext<AuraContextType | null>(null);

function createInitialSeats(roomId: string, owner?: User): RoomSeat[] {
  const seats: RoomSeat[] = [];
  for (let i = 0; i < 8; i++) {
    if (i === 0 && owner) {
      seats.push({
        seatIndex: 0,
        roomId,
        userId: owner.id,
        username: owner.username,
        avatarUrl: owner.avatarUrl,
        vipLevel: owner.vipLevel,
        role: owner.role,
        equippedFrameId: owner.equippedFrameId,
        isLocked: false,
        isMuted: false,
        isSpeaking: true,
      });
    } else {
      seats.push({
        seatIndex: i,
        roomId,
        vipLevel: 0,
        isLocked: false,
        isMuted: false,
        isSpeaking: false,
      });
    }
  }
  return seats;
}

export const AuraProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  // Load or fallback to initial seeds
  const [users, setUsers] = useState<User[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_users`);
    return saved ? JSON.parse(saved) : INITIAL_USERS;
  });

  const [currentUserId, setCurrentUserId] = useState<string>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_uid`);
    return saved || '565656565666555'; // Default to Sherry (Super Admin / Owner)
  });

  const [rooms, setRooms] = useState<VoiceRoom[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_rooms`);
    return saved ? JSON.parse(saved) : INITIAL_ROOMS;
  });

  const [activeRoomId, setActiveRoomId] = useState<string | null>(null);
  const [seats, setSeats] = useState<RoomSeat[]>([]);
  const [roomMessages, setRoomMessages] = useState<ChatMessage[]>([]);
  const [activeLuckyBag, setActiveLuckyBag] = useState<LuckyBag | null>(null);
  const [floatingGiftAlert, setFloatingGiftAlert] = useState<{ sender: string; gift: VirtualGift; target: string } | null>(null);

  const [frameAssignments, setFrameAssignments] = useState<OfficialFrameAssignment[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_assignments`);
    return saved ? JSON.parse(saved) : INITIAL_FRAME_ASSIGNMENTS;
  });

  const [adminLinkUsers, setAdminLinkUsers] = useState<AdminLinkUser[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_link_users`);
    return saved ? JSON.parse(saved) : INITIAL_LINK_USERS;
  });

  const [auditLogs, setAuditLogs] = useState<AuditLogEntity[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_audits`);
    return saved ? JSON.parse(saved) : INITIAL_AUDIT_LOGS;
  });

  const [storeItems, setStoreItems] = useState<StoreItem[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_store`);
    return saved ? JSON.parse(saved) : STORE_ITEMS;
  });

  const [moments, setMoments] = useState<Moment[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_moments`);
    return saved ? JSON.parse(saved) : INITIAL_MOMENTS;
  });

  const [notifications, setNotifications] = useState<NotificationItem[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_notifs`);
    return saved ? JSON.parse(saved) : INITIAL_NOTIFICATIONS;
  });

  const [tasks, setTasks] = useState<AuraTask[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_tasks`);
    return saved ? JSON.parse(saved) : INITIAL_TASKS;
  });

  const [currentTab, setCurrentTab] = useState<TabType>('party');

  // Current User instance
  const currentUser = users.find((u) => u.id === currentUserId) || users[0] || null;

  // Synchronize storage
  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_users`, JSON.stringify(users));
  }, [users]);

  useEffect(() => {
    if (currentUserId) {
      localStorage.setItem(`${STORAGE_KEY}_uid`, currentUserId);
    }
  }, [currentUserId]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_rooms`, JSON.stringify(rooms));
  }, [rooms]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_assignments`, JSON.stringify(frameAssignments));
  }, [frameAssignments]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_link_users`, JSON.stringify(adminLinkUsers));
  }, [adminLinkUsers]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_audits`, JSON.stringify(auditLogs));
  }, [auditLogs]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_store`, JSON.stringify(storeItems));
  }, [storeItems]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_moments`, JSON.stringify(moments));
  }, [moments]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_notifs`, JSON.stringify(notifications));
  }, [notifications]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_tasks`, JSON.stringify(tasks));
  }, [tasks]);

  // Periodic simulated room chatter & speaking wave activity when inside a room
  useEffect(() => {
    if (!activeRoomId) return;

    const chatterInterval = setInterval(() => {
      // Toggle speaking waveform for occupied seats to simulate live voice chat
      setSeats((prev) =>
        prev.map((s) => {
          if (!s.userId || s.isMuted) return { ...s, isSpeaking: false };
          // Random speaking activity
          return { ...s, isSpeaking: Math.random() > 0.4 };
        })
      );
    }, 2500);

    return () => clearInterval(chatterInterval);
  }, [activeRoomId]);

  // Periodic expiration cleaner for official frames
  useEffect(() => {
    const now = Date.now();
    let hasExpired = false;
    const updated = frameAssignments.map((a) => {
      if (a.status === 'Active' && a.expiryDate <= now) {
        hasExpired = true;
        return { ...a, status: 'Expired' as const, remainingDays: 0 };
      }
      return a;
    });

    if (hasExpired) {
      setFrameAssignments(updated);
      // Also unequip from user if currently equipped
      setUsers((prev) =>
        prev.map((u) => {
          const expiredAss = updated.find((a) => a.userId === u.id && a.status === 'Expired');
          if (expiredAss && u.equippedFrameId === expiredAss.frameId) {
            return { ...u, equippedFrameId: undefined };
          }
          return u;
        })
      );
    }
  }, [frameAssignments]);

  // Auth Functions
  const login = useCallback(
    (idOrEmail: string) => {
      const q = idOrEmail.trim().toLowerCase();
      const found = users.find((u) => u.id === q || (u.email && u.email.toLowerCase() === q));
      if (!found) {
        return { success: false, message: `Account with ID or Email "${idOrEmail}" not found.` };
      }
      if (found.isBanned) {
        return { success: false, message: 'This account is suspended. Please contact platform support.' };
      }
      setCurrentUserId(found.id);
      return { success: true };
    },
    [users]
  );

  const register = useCallback(
    (username: string, gender: 'Female' | 'Male', customId?: string) => {
      const cleanName = username.trim();
      if (!cleanName) return { success: false, message: 'Please enter a name.' };

      let assignedId = customId?.trim();
      if (!assignedId || users.some((u) => u.id === assignedId)) {
        assignedId = Math.floor(100000 + Math.random() * 900000).toString();
      }

      const avatar =
        gender === 'Female'
          ? 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300'
          : 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300';

      const newUser: User = {
        id: assignedId,
        username: cleanName,
        avatarUrl: avatar,
        gender,
        dateOfBirth: '2001-01-01',
        bio: 'Hey there! I am using AURA Live Voice Chat ✨',
        country: '🇵🇰 Pakistan',
        language: 'English',
        coins: 2000,
        diamonds: 50,
        userLevel: 1,
        richLevel: 0,
        charmLevel: 0,
        vipLevel: 0,
        followersCount: 0,
        followingCount: 0,
        friendsCount: 0,
        visitorsCount: 0,
        equippedFrameId: 'frame_neon_circle',
        role: 'User',
        isBanned: false,
        accountStatus: 'ACTIVE',
        authProvider: 'aura_id',
        createdAt: Date.now(),
        lastLoginAt: Date.now(),
      };

      // Provision permanent room
      const userRoom: VoiceRoom = {
        id: `room_${assignedId}`,
        title: `${cleanName}'s Voice Room 🎙️`,
        description: `Welcome to ${cleanName}'s official room on AURA Live!`,
        coverUrl: avatar,
        ownerId: assignedId,
        ownerName: cleanName,
        ownerAvatar: avatar,
        seatCount: 8,
        onlineCount: 1,
        isLocked: false,
        category: 'Chat & Friends',
        country: '🇵🇰 Pakistan',
        announcement: 'Welcome everyone! Tap a seat to speak.',
        adminUserIds: [assignedId],
        isActive: true,
        createdAt: Date.now(),
      };

      setUsers((prev) => [newUser, ...prev]);
      setRooms((prev) => [userRoom, ...prev]);
      setCurrentUserId(assignedId);

      return { success: true };
    },
    [users]
  );

  const loginWithGoogle = useCallback(
    (email: string, name: string) => {
      const cleanEmail = email.trim().toLowerCase();
      const existing = users.find((u) => u.email?.toLowerCase() === cleanEmail);
      if (existing) {
        setCurrentUserId(existing.id);
        return;
      }

      const id = Math.floor(100000 + Math.random() * 900000).toString();
      const newUser: User = {
        id,
        username: name || 'Google User',
        avatarUrl: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=300',
        gender: 'Not specified',
        dateOfBirth: '2000-01-01',
        email: cleanEmail,
        bio: 'Connected via Google Account ✨',
        country: '🌐 Global',
        language: 'English',
        coins: 2500,
        diamonds: 100,
        userLevel: 2,
        richLevel: 0,
        charmLevel: 0,
        vipLevel: 1,
        followersCount: 0,
        followingCount: 0,
        friendsCount: 0,
        visitorsCount: 0,
        role: 'User',
        isBanned: false,
        accountStatus: 'ACTIVE',
        authProvider: 'google',
        createdAt: Date.now(),
        lastLoginAt: Date.now(),
      };

      const userRoom: VoiceRoom = {
        id: `room_${id}`,
        title: `${name}'s Lounge 🌟`,
        description: `Official permanent room for ${name}`,
        coverUrl: newUser.avatarUrl,
        ownerId: id,
        ownerName: name,
        ownerAvatar: newUser.avatarUrl,
        seatCount: 8,
        onlineCount: 1,
        isLocked: false,
        category: 'Global & English',
        country: '🌐 Global',
        announcement: 'Welcome! Grab a seat and join the vibe.',
        adminUserIds: [id],
        isActive: true,
        createdAt: Date.now(),
      };

      setUsers((prev) => [newUser, ...prev]);
      setRooms((prev) => [userRoom, ...prev]);
      setCurrentUserId(id);
    },
    [users]
  );

  const logout = useCallback(() => {
    // Switch to first demo guest or null
    const other = users.find((u) => u.id !== currentUserId) || users[0];
    if (other) {
      setCurrentUserId(other.id);
    }
  }, [users, currentUserId]);

  const switchUser = useCallback((userId: string) => {
    setCurrentUserId(userId);
  }, []);

  const updateProfile = useCallback(
    (updates: Partial<User>) => {
      if (!currentUserId) return;
      setUsers((prev) => prev.map((u) => (u.id === currentUserId ? { ...u, ...updates } : u)));
    },
    [currentUserId]
  );

  // Voice Room Actions
  const activeRoom = rooms.find((r) => r.id === activeRoomId) || null;

  const enterRoom = useCallback(
    (roomId: string) => {
      const room = rooms.find((r) => r.id === roomId);
      if (!room) return;

      setActiveRoomId(roomId);
      const owner = users.find((u) => u.id === room.ownerId);
      const initialSeats = createInitialSeats(roomId, owner);

      // Add a couple of other active users to the seats to make the room feel alive
      const otherUsers = users.filter((u) => u.id !== room.ownerId);
      if (otherUsers[0]) {
        initialSeats[1] = {
          seatIndex: 1,
          roomId,
          userId: otherUsers[0].id,
          username: otherUsers[0].username,
          avatarUrl: otherUsers[0].avatarUrl,
          vipLevel: otherUsers[0].vipLevel,
          role: otherUsers[0].role,
          equippedFrameId: otherUsers[0].equippedFrameId,
          isLocked: false,
          isMuted: false,
          isSpeaking: true,
        };
      }
      if (otherUsers[1]) {
        initialSeats[2] = {
          seatIndex: 2,
          roomId,
          userId: otherUsers[1].id,
          username: otherUsers[1].username,
          avatarUrl: otherUsers[1].avatarUrl,
          vipLevel: otherUsers[1].vipLevel,
          role: otherUsers[1].role,
          equippedFrameId: otherUsers[1].equippedFrameId,
          isLocked: false,
          isMuted: true,
          isSpeaking: false,
        };
      }

      setSeats(initialSeats);

      // Initial room chat messages
      setRoomMessages([
        {
          id: 'sys_1',
          targetId: roomId,
          isRoomChat: true,
          senderId: 'system',
          senderName: 'System',
          senderAvatar: '',
          senderVip: 0,
          content: `📢 Welcome to ${room.title}! Follow community guidelines.`,
          timestamp: Date.now() - 300000,
        },
        {
          id: 'sys_entry',
          targetId: roomId,
          isRoomChat: true,
          senderId: currentUser?.id || 'guest',
          senderName: currentUser?.username || 'Guest',
          senderAvatar: currentUser?.avatarUrl || '',
          senderVip: currentUser?.vipLevel || 0,
          content: 'entered the room ✨',
          timestamp: Date.now(),
        },
      ]);

      // Increase room viewer count
      setRooms((prev) =>
        prev.map((r) => (r.id === roomId ? { ...r, onlineCount: Math.max(r.onlineCount, 1) + 1 } : r))
      );
    },
    [rooms, users, currentUser]
  );

  const leaveRoom = useCallback(() => {
    setActiveRoomId(null);
    setSeats([]);
    setRoomMessages([]);
    setActiveLuckyBag(null);
  }, []);

  const takeSeat = useCallback(
    (seatIndex: number): boolean => {
      if (!currentUser || !activeRoomId) return false;

      const targetSeat = seats.find((s) => s.seatIndex === seatIndex);
      if (!targetSeat || targetSeat.isLocked || targetSeat.userId) {
        return false;
      }

      // If user already on a seat, remove from existing
      setSeats((prev) =>
        prev.map((s) => {
          if (s.userId === currentUser.id) {
            return {
              ...s,
              userId: undefined,
              username: undefined,
              avatarUrl: undefined,
              vipLevel: 0,
              isSpeaking: false,
              isMuted: false,
            };
          }
          if (s.seatIndex === seatIndex) {
            return {
              ...s,
              userId: currentUser.id,
              username: currentUser.username,
              avatarUrl: currentUser.avatarUrl,
              vipLevel: currentUser.vipLevel,
              role: currentUser.role,
              equippedFrameId: currentUser.equippedFrameId,
              isLocked: false,
              isMuted: false,
              isSpeaking: true,
            };
          }
          return s;
        })
      );

      soundManager.playMicToggle(false);

      // Post in room chat
      setRoomMessages((prev) => [
        ...prev,
        {
          id: `seat_${Date.now()}`,
          targetId: activeRoomId,
          isRoomChat: true,
          senderId: currentUser.id,
          senderName: currentUser.username,
          senderAvatar: currentUser.avatarUrl,
          senderVip: currentUser.vipLevel,
          content: `took Seat #${seatIndex + 1} 🎙️`,
          timestamp: Date.now(),
        },
      ]);

      return true;
    },
    [currentUser, activeRoomId, seats]
  );

  const leaveSeat = useCallback(() => {
    if (!currentUser || !activeRoomId) return;

    setSeats((prev) =>
      prev.map((s) => {
        if (s.userId === currentUser.id) {
          return {
            ...s,
            userId: undefined,
            username: undefined,
            avatarUrl: undefined,
            vipLevel: 0,
            isSpeaking: false,
            isMuted: false,
          };
        }
        return s;
      })
    );

    soundManager.playMicToggle(true);
  }, [currentUser, activeRoomId]);

  const toggleMic = useCallback(() => {
    if (!currentUser || !activeRoomId) return;

    setSeats((prev) =>
      prev.map((s) => {
        if (s.userId === currentUser.id) {
          const nextMuted = !s.isMuted;
          soundManager.playMicToggle(nextMuted);
          return {
            ...s,
            isMuted: nextMuted,
            isSpeaking: nextMuted ? false : true,
          };
        }
        return s;
      })
    );
  }, [currentUser, activeRoomId]);

  const toggleSeatLock = useCallback((seatIndex: number) => {
    setSeats((prev) =>
      prev.map((s) => (s.seatIndex === seatIndex ? { ...s, isLocked: !s.isLocked } : s))
    );
  }, []);

  const hostTakeDownUser = useCallback(
    (seatIndex: number) => {
      const target = seats.find((s) => s.seatIndex === seatIndex);
      if (!target || !target.userId) return;

      setSeats((prev) =>
        prev.map((s) =>
          s.seatIndex === seatIndex
            ? {
                ...s,
                userId: undefined,
                username: undefined,
                avatarUrl: undefined,
                vipLevel: 0,
                isSpeaking: false,
                isMuted: false,
              }
            : s
        )
      );

      if (activeRoomId) {
        setRoomMessages((prev) => [
          ...prev,
          {
            id: `kick_${Date.now()}`,
            targetId: activeRoomId,
            isRoomChat: true,
            senderId: 'system',
            senderName: 'System',
            senderAvatar: '',
            senderVip: 0,
            content: `📢 ${target.username} was moved to audience by Host.`,
            timestamp: Date.now(),
          },
        ]);
      }
    },
    [seats, activeRoomId]
  );

  const hostMuteAll = useCallback(() => {
    setSeats((prev) =>
      prev.map((s) => (s.seatIndex !== 0 && s.userId ? { ...s, isMuted: true, isSpeaking: false } : s))
    );
    if (activeRoomId) {
      setRoomMessages((prev) => [
        ...prev,
        {
          id: `muteall_${Date.now()}`,
          targetId: activeRoomId,
          isRoomChat: true,
          senderId: 'system',
          senderName: 'System',
          senderAvatar: '',
          senderVip: 0,
          content: '🔇 Host has muted all speaker microphones.',
          timestamp: Date.now(),
        },
      ]);
    }
  }, [activeRoomId]);

  const hostLockEmptySeats = useCallback(() => {
    setSeats((prev) => prev.map((s) => (!s.userId ? { ...s, isLocked: true } : s)));
  }, []);

  const hostUnlockAllSeats = useCallback(() => {
    setSeats((prev) => prev.map((s) => ({ ...s, isLocked: false })));
  }, []);

  const hostClearChat = useCallback(() => {
    if (!activeRoomId) return;
    setRoomMessages([
      {
        id: `clear_${Date.now()}`,
        targetId: activeRoomId,
        isRoomChat: true,
        senderId: 'system',
        senderName: 'System',
        senderAvatar: '',
        senderVip: 0,
        content: '🧹 Chat stream was cleared by Host.',
        timestamp: Date.now(),
      },
    ]);
  }, [activeRoomId]);

  const hostCloseRoom = useCallback(
    (roomId: string) => {
      setRooms((prev) => prev.map((r) => (r.id === roomId ? { ...r, isActive: false, onlineCount: 0 } : r)));
      if (activeRoomId === roomId) {
        leaveRoom();
      }
    },
    [activeRoomId, leaveRoom]
  );

  const createRoom = useCallback(
    (title: string, category: string) => {
      if (!currentUser) return '';
      const newRoomId = `room_${Math.random().toString(36).substring(2, 8)}`;
      const newRoom: VoiceRoom = {
        id: newRoomId,
        title: title.trim() || `${currentUser.username}'s Room`,
        description: 'Welcome to our voice room! Enjoy the music and chat.',
        coverUrl: currentUser.avatarUrl,
        ownerId: currentUser.id,
        ownerName: currentUser.username,
        ownerAvatar: currentUser.avatarUrl,
        seatCount: 8,
        onlineCount: 1,
        isLocked: false,
        category: category || 'Chat & Friends',
        country: currentUser.country,
        announcement: 'Welcome everyone! Tap an open seat to join mic.',
        adminUserIds: [currentUser.id],
        isActive: true,
        createdAt: Date.now(),
      };

      setRooms((prev) => [newRoom, ...prev]);
      enterRoom(newRoomId);
      return newRoomId;
    },
    [currentUser, enterRoom]
  );

  const sendRoomMessage = useCallback(
    (text: string) => {
      if (!currentUser || !activeRoomId || !text.trim()) return;

      const newMsg: ChatMessage = {
        id: `msg_${Date.now()}`,
        targetId: activeRoomId,
        isRoomChat: true,
        senderId: currentUser.id,
        senderName: currentUser.username,
        senderAvatar: currentUser.avatarUrl,
        senderVip: currentUser.vipLevel,
        senderRole: currentUser.role,
        content: text.trim(),
        timestamp: Date.now(),
      };

      setRoomMessages((prev) => [...prev, newMsg]);
    },
    [currentUser, activeRoomId]
  );

  const sendGift = useCallback(
    (gift: VirtualGift, targetUserId: string): boolean => {
      if (!currentUser || !activeRoomId) return false;
      if (currentUser.coins < gift.costCoins) return false;

      // Deduct coins from sender, increase rich points
      const senderNewCoins = currentUser.coins - gift.costCoins;
      const senderNewRichLevel = Math.min(50, Math.floor(currentUser.richLevel + gift.costCoins / 500));

      const target = users.find((u) => u.id === targetUserId);
      const targetName = target ? target.username : 'Host';

      // 1 diamond per 10 coins ratio gross value
      const grossDiamonds = Math.max(1, Math.floor(gift.costCoins / 10));

      setUsers((prev) =>
        prev.map((u) => {
          if (u.id === currentUser.id) {
            return {
              ...u,
              coins: senderNewCoins,
              richLevel: senderNewRichLevel,
            };
          }
          if (target && u.id === target.id) {
            return {
              ...u,
              diamonds: u.diamonds + grossDiamonds,
              charmLevel: Math.min(50, u.charmLevel + Math.max(1, Math.floor(gift.charmPoints / 5))),
            };
          }
          return u;
        })
      );

      soundManager.playGiftChime();

      // Trigger floating banner alert
      setFloatingGiftAlert({
        sender: currentUser.username,
        gift,
        target: targetName,
      });
      setTimeout(() => setFloatingGiftAlert(null), 3500);

      // Post in room chat
      setRoomMessages((prev) => [
        ...prev,
        {
          id: `gift_${Date.now()}`,
          targetId: activeRoomId,
          isRoomChat: true,
          senderId: currentUser.id,
          senderName: currentUser.username,
          senderAvatar: currentUser.avatarUrl,
          senderVip: currentUser.vipLevel,
          content: `sent ${gift.name} to ${targetName}!`,
          giftName: gift.name,
          giftIcon: gift.iconEmoji,
          giftCount: 1,
          timestamp: Date.now(),
        },
      ]);

      return true;
    },
    [currentUser, activeRoomId, users]
  );

  const dropLuckyBag = useCallback(
    (totalCoins: number, maxClaimers: number): boolean => {
      if (!currentUser || !activeRoomId) return false;
      if (currentUser.coins < totalCoins || totalCoins < 50) return false;

      // Deduct coins
      setUsers((prev) =>
        prev.map((u) => (u.id === currentUser.id ? { ...u, coins: u.coins - totalCoins } : u))
      );

      const bag: LuckyBag = {
        id: `bag_${Date.now()}`,
        roomId: activeRoomId,
        senderId: currentUser.id,
        senderName: currentUser.username,
        totalCoins,
        remainingCoins: totalCoins,
        totalClaimers: maxClaimers,
        claimers: [],
        expiresAt: Date.now() + 180000,
      };

      setActiveLuckyBag(bag);
      soundManager.playSoundEffect('bell');

      setRoomMessages((prev) => [
        ...prev,
        {
          id: `bag_msg_${Date.now()}`,
          targetId: activeRoomId,
          isRoomChat: true,
          senderId: currentUser.id,
          senderName: currentUser.username,
          senderAvatar: currentUser.avatarUrl,
          senderVip: currentUser.vipLevel,
          content: `dropped a Lucky Coin Bag 🧧 containing ${totalCoins} Coins for ${maxClaimers} lucky people!`,
          timestamp: Date.now(),
        },
      ]);

      return true;
    },
    [currentUser, activeRoomId]
  );

  const claimLuckyBag = useCallback((): { success: boolean; coinsWon?: number; message?: string } => {
    if (!currentUser || !activeLuckyBag) {
      return { success: false, message: 'No active lucky bag in room.' };
    }

    if (activeLuckyBag.claimers.some((c) => c.userId === currentUser.id)) {
      return { success: false, message: 'You already claimed this lucky bag!' };
    }

    if (
      activeLuckyBag.claimers.length >= activeLuckyBag.totalClaimers ||
      activeLuckyBag.remainingCoins <= 0
    ) {
      return { success: false, message: 'All lucky coins have been claimed!' };
    }

    const remainingClaimers = activeLuckyBag.totalClaimers - activeLuckyBag.claimers.length;
    let share = 0;
    if (remainingClaimers === 1) {
      share = activeLuckyBag.remainingCoins;
    } else {
      const avg = Math.floor(activeLuckyBag.remainingCoins / remainingClaimers);
      share = Math.max(1, Math.floor(avg * (0.6 + Math.random() * 0.8)));
      share = Math.min(share, activeLuckyBag.remainingCoins);
    }

    const updatedBag: LuckyBag = {
      ...activeLuckyBag,
      remainingCoins: activeLuckyBag.remainingCoins - share,
      claimers: [
        ...activeLuckyBag.claimers,
        { userId: currentUser.id, userName: currentUser.username, coinsClaimed: share },
      ],
    };

    setActiveLuckyBag(updatedBag);
    setUsers((prev) => prev.map((u) => (u.id === currentUser.id ? { ...u, coins: u.coins + share } : u)));
    soundManager.playSoundEffect('magic');

    return { success: true, coinsWon: share, message: `You won ${share} Coins! 🧧` };
  }, [currentUser, activeLuckyBag]);

  const playRoomSound = useCallback((soundKey: string) => {
    soundManager.playSoundEffect(soundKey);
  }, []);

  // Official Frame System
  const sendOfficialFrame = useCallback(
    (targetUserId: string, frameId: string, days: number): { success: boolean; message: string } => {
      const trimmedId = targetUserId.trim();
      if (!trimmedId) return { success: false, message: 'User ID is required.' };
      if (days <= 0) return { success: false, message: 'Duration must be at least 1 day.' };

      const frameDef = OFFICIAL_FRAMES.find((f) => f.id === frameId);
      if (!frameDef) return { success: false, message: 'Invalid official frame.' };

      // Rule: Prevent sending conflicting frames to the same user at the same time
      const activeExisting = frameAssignments.find(
        (a) => a.userId === trimmedId && a.status === 'Active' && a.expiryDate > Date.now()
      );
      if (activeExisting) {
        return {
          success: false,
          message: `Conflict Prevention: User ID "${trimmedId}" already has active frame "${activeExisting.frameName}" valid until ${activeExisting.expiryDateFormatted}. Please revoke the existing frame first.`,
        };
      }

      // Check or create target user
      let targetUser = users.find((u) => u.id === trimmedId);
      if (!targetUser) {
        // Register placeholder official user
        targetUser = {
          id: trimmedId,
          username: `Official Member ${trimmedId}`,
          avatarUrl: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=300',
          gender: 'Not specified',
          dateOfBirth: '2000-01-01',
          bio: 'Official Staff Member',
          country: '🇵🇰 Pakistan',
          language: 'English',
          coins: 5000,
          diamonds: 500,
          userLevel: 5,
          richLevel: 2,
          charmLevel: 2,
          vipLevel: 2,
          followersCount: 10,
          followingCount: 5,
          friendsCount: 2,
          visitorsCount: 15,
          equippedFrameId: frameId,
          role: 'Official',
          isBanned: false,
          accountStatus: 'ACTIVE',
          authProvider: 'aura_id',
          createdAt: Date.now(),
          lastLoginAt: Date.now(),
        };
        setUsers((prev) => [targetUser!, ...prev]);
      } else {
        // Equip frame to target user
        setUsers((prev) =>
          prev.map((u) => (u.id === trimmedId ? { ...u, equippedFrameId: frameId } : u))
        );
      }

      const now = Date.now();
      const expiry = now + days * 86400000;
      const sendDateFormatted = new Date(now).toISOString().slice(0, 16).replace('T', ' ');
      const expiryDateFormatted = new Date(expiry).toISOString().slice(0, 16).replace('T', ' ');

      const assignment: OfficialFrameAssignment = {
        id: `frame_assign_${Date.now()}`,
        userId: trimmedId,
        userName: targetUser.username,
        frameId,
        frameName: frameDef.name,
        days,
        sendDate: now,
        expiryDate: expiry,
        status: 'Active',
        adminId: currentUser?.id || '565656565666555',
        adminName: currentUser?.username || 'Sherry',
        sendDateFormatted,
        expiryDateFormatted,
        remainingDays: days,
      };

      setFrameAssignments((prev) => [assignment, ...prev]);

      // Record Audit Log
      const audit: AuditLogEntity = {
        id: `audit_${Date.now()}`,
        adminId: currentUser?.id || '565656565666555',
        adminName: currentUser?.username || 'Sherry',
        adminRole: currentUser?.role || 'Super Admin',
        action: 'SEND_OFFICIAL_FRAME',
        targetType: 'User',
        targetId: trimmedId,
        targetName: targetUser.username,
        previousValue: null,
        newValue: `Granted ${frameDef.name} for ${days} days (Expires ${expiryDateFormatted})`,
        isSuccess: true,
        timestamp: Date.now(),
      };
      setAuditLogs((prev) => [audit, ...prev]);

      // System notification
      const notif: NotificationItem = {
        id: `notif_${Date.now()}`,
        type: 'official',
        title: `Official Frame Granted: ${frameDef.name}`,
        message: `You were granted the official "${frameDef.name}" identity frame for ${days} days by ${currentUser?.username}.`,
        senderName: 'Official 1 Operations',
        timestamp: Date.now(),
        isRead: false,
      };
      setNotifications((prev) => [notif, ...prev]);

      return {
        success: true,
        message: `Successfully sent ${frameDef.name} (${days} Days) to User ID ${trimmedId} (${targetUser.username})!`,
      };
    },
    [frameAssignments, users, currentUser]
  );

  const revokeOfficialFrame = useCallback(
    (assignmentId: string): boolean => {
      const assignment = frameAssignments.find((a) => a.id === assignmentId);
      if (!assignment) return false;

      setFrameAssignments((prev) =>
        prev.map((a) => (a.id === assignmentId ? { ...a, status: 'Revoked', remainingDays: 0 } : a))
      );

      // Unequip from user
      setUsers((prev) =>
        prev.map((u) =>
          u.id === assignment.userId && u.equippedFrameId === assignment.frameId
            ? { ...u, equippedFrameId: undefined }
            : u
        )
      );

      // Log audit
      const audit: AuditLogEntity = {
        id: `audit_revoke_${Date.now()}`,
        adminId: currentUser?.id || '565656565666555',
        adminName: currentUser?.username || 'Sherry',
        adminRole: currentUser?.role || 'Super Admin',
        action: 'REVOKE_OFFICIAL_FRAME',
        targetType: 'User',
        targetId: assignment.userId,
        targetName: assignment.userName,
        previousValue: assignment.frameName,
        newValue: 'Revoked by Admin',
        isSuccess: true,
        timestamp: Date.now(),
      };
      setAuditLogs((prev) => [audit, ...prev]);

      return true;
    },
    [frameAssignments, currentUser]
  );

  // Admin Operations
  const assignUserRole = useCallback(
    (targetUserId: string, newRole: AdminRoleType): { success: boolean; message: string } => {
      const targetUser = users.find((u) => u.id === targetUserId);
      if (!targetUser) return { success: false, message: 'Target user not found.' };

      const oldRole = targetUser.role;
      setUsers((prev) => prev.map((u) => (u.id === targetUserId ? { ...u, role: newRole } : u)));

      const audit: AuditLogEntity = {
        id: `audit_role_${Date.now()}`,
        adminId: currentUser?.id || '565656565666555',
        adminName: currentUser?.username || 'Sherry',
        adminRole: currentUser?.role || 'Super Admin',
        action: 'ASSIGN_USER_ROLE',
        targetType: 'Role',
        targetId: targetUserId,
        targetName: targetUser.username,
        previousValue: oldRole,
        newValue: newRole,
        isSuccess: true,
        timestamp: Date.now(),
      };
      setAuditLogs((prev) => [audit, ...prev]);

      return { success: true, message: `Updated ${targetUser.username}'s role to "${newRole}".` };
    },
    [users, currentUser]
  );

  const banUser = useCallback(
    (userId: string, isBanned: boolean) => {
      setUsers((prev) =>
        prev.map((u) => (u.id === userId ? { ...u, isBanned, accountStatus: isBanned ? 'BANNED' : 'ACTIVE' } : u))
      );
    },
    []
  );

  const adjustUserBalance = useCallback((userId: string, deltaCoins: number, deltaDiamonds: number) => {
    setUsers((prev) =>
      prev.map((u) => {
        if (u.id === userId) {
          return {
            ...u,
            coins: Math.max(0, u.coins + deltaCoins),
            diamonds: Math.max(0, u.diamonds + deltaDiamonds),
          };
        }
        return u;
      })
    );
  }, []);

  const addLinkUser = useCallback(
    (data: Partial<AdminLinkUser>) => {
      const newLink: AdminLinkUser = {
        id: `link_${Date.now()}`,
        adminId: currentUser?.id || '565656565666555',
        userId: data.userId || 'usr_new',
        userName: data.userName || 'New Linked Host',
        userAvatar: data.userAvatar || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150',
        status: data.status || 'Active',
        assignedWork: data.assignedWork || 'Live Voice Host',
        workStatus: data.workStatus || 'In Progress',
        workCategory: data.workCategory || 'Voice Hosting',
        targetHours: data.targetHours || 40,
        completedHours: data.completedHours || 0,
        targetDiamonds: data.targetDiamonds || 20000,
        earnedDiamonds: data.earnedDiamonds || 0,
        activityInfo: data.activityInfo || 'Assigned via Official Panel',
        lastActive: 'Just now',
        joinedDate: new Date().toISOString().slice(0, 10),
        notes: data.notes || '',
        updatedAt: Date.now(),
      };
      setAdminLinkUsers((prev) => [newLink, ...prev]);
    },
    [currentUser]
  );

  const updateLinkUserWork = useCallback((id: string, updates: Partial<AdminLinkUser>) => {
    setAdminLinkUsers((prev) =>
      prev.map((l) => (l.id === id ? { ...l, ...updates, updatedAt: Date.now() } : l))
    );
  }, []);

  const broadcastAnnouncement = useCallback(
    (title: string, message: string) => {
      const notif: NotificationItem = {
        id: `ann_${Date.now()}`,
        type: 'announcement',
        title: `📢 ${title}`,
        message,
        senderName: 'AURA Live Executive Office 👑',
        timestamp: Date.now(),
        isRead: false,
      };
      setNotifications((prev) => [notif, ...prev]);

      const audit: AuditLogEntity = {
        id: `audit_ann_${Date.now()}`,
        adminId: currentUser?.id || 'Owner',
        adminName: currentUser?.username || 'Owner',
        adminRole: 'Super Admin',
        action: 'BROADCAST_ANNOUNCEMENT',
        targetType: 'Notification',
        targetId: 'ALL',
        targetName: title,
        previousValue: null,
        newValue: message,
        isSuccess: true,
        timestamp: Date.now(),
      };
      setAuditLogs((prev) => [audit, ...prev]);
    },
    [currentUser]
  );

  // Store
  const buyStoreItem = useCallback(
    (itemId: string): boolean => {
      if (!currentUser) return false;
      const item = storeItems.find((s) => s.id === itemId);
      if (!item || currentUser.coins < item.price) return false;

      // Deduct coins
      setUsers((prev) =>
        prev.map((u) => (u.id === currentUser.id ? { ...u, coins: u.coins - item.price } : u))
      );

      // Mark owned
      setStoreItems((prev) =>
        prev.map((s) => (s.id === itemId ? { ...s, isOwned: true } : s))
      );

      soundManager.playSoundEffect('magic');
      return true;
    },
    [currentUser, storeItems]
  );

  const equipStoreItem = useCallback(
    (itemId: string, category: string) => {
      if (!currentUser) return;
      setStoreItems((prev) =>
        prev.map((s) => (s.category === category ? { ...s, isEquipped: s.id === itemId } : s))
      );

      setUsers((prev) =>
        prev.map((u) => {
          if (u.id !== currentUser.id) return u;
          if (category === 'Frame') return { ...u, equippedFrameId: itemId };
          if (category === 'Bubble') return { ...u, equippedBubbleId: itemId };
          if (category === 'Ride') return { ...u, equippedRideId: itemId };
          return u;
        })
      );
    },
    [currentUser]
  );

  // Recharge & Wallet
  const rechargeCoins = useCallback(
    (pkg: RechargePackage) => {
      if (!currentUser) return;
      setUsers((prev) =>
        prev.map((u) =>
          u.id === currentUser.id
            ? {
                ...u,
                coins: u.coins + pkg.coins,
                diamonds: u.diamonds + pkg.bonusDiamonds,
                vipLevel: Math.max(u.vipLevel, 1),
              }
            : u
        )
      );
      soundManager.playSoundEffect('bell');
    },
    [currentUser]
  );

  const convertDiamondsToCoins = useCallback(
    (diamonds: number): boolean => {
      if (!currentUser || currentUser.diamonds < diamonds || diamonds <= 0) return false;
      // 1 Diamond gives 8 Coins
      const coinsGained = diamonds * 8;
      setUsers((prev) =>
        prev.map((u) =>
          u.id === currentUser.id
            ? {
                ...u,
                diamonds: u.diamonds - diamonds,
                coins: u.coins + coinsGained,
              }
            : u
        )
      );
      soundManager.playSoundEffect('bell');
      return true;
    },
    [currentUser]
  );

  // Moments & Social
  const likeMoment = useCallback(
    (momentId: string) => {
      setMoments((prev) =>
        prev.map((m) => {
          if (m.id === momentId) {
            const nextLiked = !m.isLiked;
            return {
              ...m,
              isLiked: nextLiked,
              likesCount: nextLiked ? m.likesCount + 1 : m.likesCount - 1,
            };
          }
          return m;
        })
      );
    },
    []
  );

  const addMomentComment = useCallback(
    (momentId: string, content: string) => {
      if (!currentUser || !content.trim()) return;
      const newComment = {
        id: `cmt_${Date.now()}`,
        userId: currentUser.id,
        userName: currentUser.username,
        userAvatar: currentUser.avatarUrl,
        content: content.trim(),
        timestamp: Date.now(),
      };

      setMoments((prev) =>
        prev.map((m) => (m.id === momentId ? { ...m, comments: [...m.comments, newComment] } : m))
      );
    },
    [currentUser]
  );

  const postMoment = useCallback(
    (content: string, mediaUrl?: string) => {
      if (!currentUser || !content.trim()) return;
      const newMoment: Moment = {
        id: `m_${Date.now()}`,
        userId: currentUser.id,
        userName: currentUser.username,
        userAvatar: currentUser.avatarUrl,
        vipLevel: currentUser.vipLevel,
        content: content.trim(),
        mediaUrls: mediaUrl ? [mediaUrl] : [],
        likesCount: 0,
        isLiked: false,
        comments: [],
        timestamp: Date.now(),
      };
      setMoments((prev) => [newMoment, ...prev]);
    },
    [currentUser]
  );

  const followUser = useCallback((targetUserId: string) => {
    setUsers((prev) =>
      prev.map((u) => {
        if (u.id === targetUserId) {
          return { ...u, followersCount: u.followersCount + 1 };
        }
        return u;
      })
    );
  }, []);

  const claimTaskReward = useCallback(
    (taskId: string): boolean => {
      if (!currentUser) return false;
      const task = tasks.find((t) => t.id === taskId);
      if (!task || task.isClaimed) return false;

      setTasks((prev) => prev.map((t) => (t.id === taskId ? { ...t, isClaimed: true } : t)));
      setUsers((prev) =>
        prev.map((u) =>
          u.id === currentUser.id
            ? {
                ...u,
                coins: u.coins + task.rewardCoins,
                diamonds: u.diamonds + task.rewardDiamonds,
              }
            : u
        )
      );

      soundManager.playSoundEffect('magic');
      return true;
    },
    [currentUser, tasks]
  );

  const markNotificationsAsRead = useCallback(() => {
    setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
  }, []);

  const unreadNotifCount = notifications.filter((n) => !n.isRead).length;

  return (
    <AuraContext.Provider
      value={{
        currentUser,
        allUsers: users,
        rooms,
        activeRoom,
        activeRoomSeats: seats,
        roomMessages,
        activeLuckyBag,
        floatingGiftAlert,
        officialFrames: OFFICIAL_FRAMES,
        frameAssignments,
        adminLinkUsers,
        auditLogs,
        storeItems,
        moments,
        notifications,
        tasks,
        unreadNotifCount,
        currentTab,
        setCurrentTab,

        login,
        register,
        loginWithGoogle,
        logout,
        switchUser,
        updateProfile,

        enterRoom,
        leaveRoom,
        takeSeat,
        leaveSeat,
        toggleMic,
        toggleSeatLock,
        hostTakeDownUser,
        hostMuteAll,
        hostLockEmptySeats,
        hostUnlockAllSeats,
        hostClearChat,
        hostCloseRoom,
        createRoom,
        sendRoomMessage,
        sendGift,
        dropLuckyBag,
        claimLuckyBag,
        playRoomSound,

        sendOfficialFrame,
        revokeOfficialFrame,

        assignUserRole,
        banUser,
        adjustUserBalance,
        addLinkUser,
        updateLinkUserWork,
        broadcastAnnouncement,

        buyStoreItem,
        equipStoreItem,
        rechargeCoins,
        convertDiamondsToCoins,

        likeMoment,
        addMomentComment,
        postMoment,
        followUser,
        claimTaskReward,
        claimDailyTaskReward: claimTaskReward,
        markNotificationsAsRead,
      }}
    >
      {children}
    </AuraContext.Provider>
  );
};

export const useAura = (): AuraContextType => {
  const context = useContext(AuraContext);
  if (!context) {
    throw new Error('useAura must be used within an AuraProvider');
  }
  return context;
};
