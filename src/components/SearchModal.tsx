import React, { useState, useMemo } from 'react';
import {
  X,
  Search,
  Hash,
  Radio,
  Users,
  UserCheck,
  UserPlus,
  ArrowRight,
  Sparkles,
  Flame,
  Volume2,
  ExternalLink,
} from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { AvatarWithFrame } from './AvatarWithFrame';
import { OFFICIAL_FRAMES } from '../data/seedData';
import { User, VoiceRoom } from '../types';

export type SearchCategory = 'id' | 'room' | 'user';

interface SearchModalProps {
  isOpen: boolean;
  onClose: () => void;
  onOpenUserProfile: (userId: string) => void;
  initialCategory?: SearchCategory;
  initialQuery?: string;
  onOpenCreateId?: () => void;
}

export const SearchModal: React.FC<SearchModalProps> = ({
  isOpen,
  onClose,
  onOpenUserProfile,
  initialCategory = 'id',
  initialQuery = '',
  onOpenCreateId,
}) => {
  const { allUsers, rooms, currentUser, followUser, enterRoom } = useAura();

  const [category, setCategory] = useState<SearchCategory>(initialCategory);
  const [query, setQuery] = useState(initialQuery);

  // Synchronize when opened with initial values
  React.useEffect(() => {
    if (isOpen) {
      if (initialCategory) setCategory(initialCategory);
      if (initialQuery !== undefined) setQuery(initialQuery);
    }
  }, [isOpen, initialCategory, initialQuery]);

  const cleanQuery = query.trim().toLowerCase();

  // 1. ID Number Search: matches users by ID and rooms by ID
  const matchedUsersById = useMemo(() => {
    if (!cleanQuery) return [];
    return allUsers.filter(
      (u) =>
        u.id.toLowerCase() === cleanQuery ||
        u.id.toLowerCase().includes(cleanQuery)
    );
  }, [allUsers, cleanQuery]);

  const matchedRoomsById = useMemo(() => {
    if (!cleanQuery) return [];
    return rooms.filter((r) => {
      const numericRoomId = r.id.replace('room_', '').toLowerCase();
      return (
        numericRoomId === cleanQuery ||
        r.id.toLowerCase() === cleanQuery ||
        numericRoomId.includes(cleanQuery)
      );
    });
  }, [rooms, cleanQuery]);

  // 2. Room Search: matches rooms by Title, Category, Host, or ID
  const matchedRooms = useMemo(() => {
    if (!cleanQuery) return rooms;
    return rooms.filter((r) => {
      const numericRoomId = r.id.replace('room_', '').toLowerCase();
      return (
        r.title.toLowerCase().includes(cleanQuery) ||
        r.category.toLowerCase().includes(cleanQuery) ||
        r.ownerName.toLowerCase().includes(cleanQuery) ||
        numericRoomId.includes(cleanQuery) ||
        r.country.toLowerCase().includes(cleanQuery)
      );
    });
  }, [rooms, cleanQuery]);

  // 3. User Search: matches users by Username, Bio, Country, Role, or ID
  const matchedUsers = useMemo(() => {
    if (!cleanQuery) return allUsers;
    return allUsers.filter(
      (u) =>
        u.username.toLowerCase().includes(cleanQuery) ||
        u.id.toLowerCase().includes(cleanQuery) ||
        u.country.toLowerCase().includes(cleanQuery) ||
        u.role.toLowerCase().includes(cleanQuery) ||
        u.bio.toLowerCase().includes(cleanQuery)
    );
  }, [allUsers, cleanQuery]);

  if (!isOpen) return null;

  return (
    <div
      id="search-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/80 backdrop-blur-xs flex items-center justify-center p-3 sm:p-5 animate-in fade-in duration-150"
      onClick={onClose}
    >
      <div
        id="search-modal"
        className="w-full max-w-lg bg-[#110826] border border-[#442875] rounded-3xl p-4 sm:p-5 shadow-2xl flex flex-col gap-3 relative max-h-[92vh] overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Modal Top Bar */}
        <div className="flex items-center justify-between pb-2 border-b border-[#251545]">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-xl bg-[#7c4dff]/20 text-[#00e5ff] flex items-center justify-center border border-[#7c4dff]/40">
              <Search className="w-4 h-4" />
            </div>
            <div>
              <h2 className="text-sm font-black text-white">Search System</h2>
              <p className="text-[10px] text-slate-400">Search by ID Number, Room, or User</p>
            </div>
          </div>
          <button
            id="close-search-btn"
            onClick={onClose}
            className="p-1.5 rounded-full text-slate-400 hover:text-white hover:bg-[#251549] transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Category Tabs: ID Number, Room, User */}
        <div className="grid grid-cols-3 gap-1.5 p-1 bg-[#1a0f36] rounded-2xl border border-[#331c5c]">
          <button
            id="search-tab-id"
            onClick={() => setCategory('id')}
            className={`flex items-center justify-center gap-1.5 py-2 rounded-xl text-xs font-bold transition-all ${
              category === 'id'
                ? 'bg-gradient-to-r from-[#7c4dff] to-[#ff2a85] text-white shadow-md'
                : 'text-slate-400 hover:text-white hover:bg-[#251549]'
            }`}
          >
            <Hash className="w-3.5 h-3.5" />
            <span>ID Number</span>
          </button>

          <button
            id="search-tab-room"
            onClick={() => setCategory('room')}
            className={`flex items-center justify-center gap-1.5 py-2 rounded-xl text-xs font-bold transition-all ${
              category === 'room'
                ? 'bg-gradient-to-r from-[#7c4dff] to-[#00e5ff] text-white shadow-md'
                : 'text-slate-400 hover:text-white hover:bg-[#251549]'
            }`}
          >
            <Radio className="w-3.5 h-3.5" />
            <span>Room</span>
          </button>

          <button
            id="search-tab-user"
            onClick={() => setCategory('user')}
            className={`flex items-center justify-center gap-1.5 py-2 rounded-xl text-xs font-bold transition-all ${
              category === 'user'
                ? 'bg-gradient-to-r from-[#00e5ff] to-[#7c4dff] text-black font-black shadow-md'
                : 'text-slate-400 hover:text-white hover:bg-[#251549]'
            }`}
          >
            <Users className="w-3.5 h-3.5" />
            <span>User</span>
          </button>
        </div>

        {/* Search Input Bar */}
        <div className="relative">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
          <input
            id="search-query-input"
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder={
              category === 'id'
                ? 'Enter User ID or Room ID (e.g. 565656565666555 or 782194)...'
                : category === 'room'
                ? 'Search rooms by title, category, host name...'
                : 'Search users by username, role, country...'
            }
            className="w-full bg-[#1b0f36] border border-[#3b2368] focus:border-[#7c4dff] rounded-2xl pl-9 pr-9 py-2.5 text-xs text-white placeholder-slate-500 focus:outline-hidden"
            autoFocus
          />
          {query && (
            <button
              onClick={() => setQuery('')}
              className="absolute right-3 top-3 text-slate-400 hover:text-white"
            >
              <X className="w-3.5 h-3.5" />
            </button>
          )}
        </div>

        {/* RESULTS CONTAINER */}
        <div className="flex-1 overflow-y-auto space-y-3 pr-1 max-h-[60vh]">
          {/* ================= CATEGORY: ID NUMBER ================= */}
          {category === 'id' && (
            <div className="space-y-3">
              {!cleanQuery ? (
                <div className="p-6 text-center text-slate-400 text-xs">
                  <Hash className="w-8 h-8 text-slate-600 mx-auto mb-2 opacity-50" />
                  <p className="font-semibold text-slate-300">Search by exact ID Number</p>
                  <p className="text-[11px] text-slate-500 mt-1">
                    Type a User ID or Room ID to find exact profile and live audio room.
                  </p>
                </div>
              ) : matchedUsersById.length === 0 && matchedRoomsById.length === 0 ? (
                <div className="p-6 text-center text-slate-400 text-xs bg-[#160b2d] rounded-2xl border border-[#2b1750]">
                  <p className="font-bold text-slate-300">No User or Room found with ID #{query}</p>
                  <p className="text-[11px] text-slate-500 mt-1">
                    Check if the ID was typed correctly or create a new User ID.
                  </p>
                  {onOpenCreateId && (
                    <button
                      onClick={() => {
                        onClose();
                        onOpenCreateId();
                      }}
                      className="mt-3 inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-gradient-to-r from-[#7c4dff] to-[#00e5ff] text-black font-bold text-xs"
                    >
                      <Sparkles className="w-3.5 h-3.5" />
                      <span>Create New User ID</span>
                    </button>
                  )}
                </div>
              ) : (
                <>
                  {/* Matched Users by ID */}
                  {matchedUsersById.length > 0 && (
                    <div className="space-y-2">
                      <div className="text-[11px] font-bold text-[#00e5ff] uppercase tracking-wider flex items-center gap-1.5">
                        <Users className="w-3.5 h-3.5" />
                        <span>Users with ID ({matchedUsersById.length})</span>
                      </div>
                      {matchedUsersById.map((user) => renderUserCard(user))}
                    </div>
                  )}

                  {/* Matched Rooms by ID */}
                  {matchedRoomsById.length > 0 && (
                    <div className="space-y-2 pt-2">
                      <div className="text-[11px] font-bold text-amber-400 uppercase tracking-wider flex items-center gap-1.5">
                        <Radio className="w-3.5 h-3.5" />
                        <span>Rooms with ID ({matchedRoomsById.length})</span>
                      </div>
                      {matchedRoomsById.map((room) => renderRoomCard(room))}
                    </div>
                  )}
                </>
              )}
            </div>
          )}

          {/* ================= CATEGORY: ROOM ================= */}
          {category === 'room' && (
            <div className="space-y-2">
              <div className="flex items-center justify-between text-[11px] text-slate-400 pb-1">
                <span>Rooms found ({matchedRooms.length})</span>
                <span className="text-emerald-400 font-semibold">
                  {matchedRooms.filter((r) => r.isActive).length} LIVE
                </span>
              </div>
              {matchedRooms.length === 0 ? (
                <div className="p-6 text-center text-slate-400 text-xs bg-[#160b2d] rounded-2xl border border-[#2b1750]">
                  No rooms matching "{query}". Try a different title or category.
                </div>
              ) : (
                matchedRooms.map((room) => renderRoomCard(room))
              )}
            </div>
          )}

          {/* ================= CATEGORY: USER ================= */}
          {category === 'user' && (
            <div className="space-y-2">
              <div className="text-[11px] text-slate-400 pb-1">
                Users found ({matchedUsers.length})
              </div>
              {matchedUsers.length === 0 ? (
                <div className="p-6 text-center text-slate-400 text-xs bg-[#160b2d] rounded-2xl border border-[#2b1750]">
                  No users found matching "{query}".
                </div>
              ) : (
                matchedUsers.map((user) => renderUserCard(user))
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );

  // Helper function to render User Card
  function renderUserCard(user: User) {
    const isSelf = currentUser?.id === user.id;
    const officialFrame = OFFICIAL_FRAMES.find((f) => f.id === user.equippedFrameId);

    return (
      <div
        key={user.id}
        id={`search-user-card-${user.id}`}
        className="p-3 rounded-2xl bg-[#190e33] hover:bg-[#20123f] border border-[#341d5e] flex items-center justify-between gap-3 transition-colors"
      >
        <div
          className="flex items-center gap-3 min-w-0 cursor-pointer flex-1"
          onClick={() => {
            onClose();
            onOpenUserProfile(user.id);
          }}
        >
          <AvatarWithFrame
            avatarUrl={user.avatarUrl}
            size={46}
            equippedFrameId={user.equippedFrameId}
            vipLevel={user.vipLevel}
            role={user.role}
          />
          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-1.5">
              <span className="text-xs font-bold text-white truncate">{user.username}</span>
              {user.gender === 'Female' ? (
                <span className="text-[10px]">♀️</span>
              ) : user.gender === 'Male' ? (
                <span className="text-[10px]">♂️</span>
              ) : null}
              <span className="px-1.5 py-[0.5px] rounded bg-[#7c4dff]/30 text-[#00e5ff] text-[9px] font-extrabold border border-[#7c4dff]/50">
                {user.role}
              </span>
            </div>
            <div className="flex items-center gap-2 text-[10px] text-slate-400 font-mono mt-0.5">
              <span className="text-amber-300 font-bold">ID: {user.id}</span>
              <span>•</span>
              <span className="truncate">{user.country}</span>
            </div>
            <p className="text-[10px] text-slate-400 truncate max-w-[200px] sm:max-w-xs mt-0.5">
              {user.bio}
            </p>
          </div>
        </div>

        <div className="flex items-center gap-1.5 flex-shrink-0">
          {!isSelf && (
            <button
              id={`follow-search-user-${user.id}`}
              onClick={() => followUser(user.id)}
              className="p-2 rounded-xl bg-[#231445] hover:bg-[#321c61] text-pink-400 border border-[#442875] text-xs transition-colors"
              title="Follow"
            >
              <UserPlus className="w-3.5 h-3.5" />
            </button>
          )}
          <button
            id={`view-profile-search-user-${user.id}`}
            onClick={() => {
              onClose();
              onOpenUserProfile(user.id);
            }}
            className="px-2.5 py-1.5 rounded-xl bg-gradient-to-r from-[#7c4dff] to-[#00e5ff] text-black font-extrabold text-[11px] shadow hover:opacity-90 flex items-center gap-1 transition-transform active:scale-95"
          >
            <span>Profile</span>
            <ArrowRight className="w-3 h-3" />
          </button>
        </div>
      </div>
    );
  }

  // Helper function to render Room Card
  function renderRoomCard(room: VoiceRoom) {
    const numericRoomId = room.id.replace('room_', '');

    return (
      <div
        key={room.id}
        id={`search-room-card-${room.id}`}
        className="p-3 rounded-2xl bg-[#190e33] hover:bg-[#20123f] border border-[#341d5e] flex items-center justify-between gap-3 transition-colors"
      >
        <div className="flex items-center gap-3 min-w-0 flex-1">
          <img
            src={room.coverUrl}
            alt={room.title}
            className="w-12 h-12 rounded-xl object-cover border border-[#442875] flex-shrink-0"
            referrerPolicy="no-referrer"
          />
          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-1.5">
              <span className="text-xs font-bold text-white truncate">{room.title}</span>
              {room.isActive ? (
                <span className="px-1.5 py-[0.5px] rounded bg-emerald-500/20 text-emerald-400 text-[9px] font-bold border border-emerald-500/30 flex items-center gap-1">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
                  LIVE
                </span>
              ) : (
                <span className="px-1.5 py-[0.5px] rounded bg-slate-700/50 text-slate-400 text-[9px] font-bold">
                  CLOSED
                </span>
              )}
            </div>
            <div className="flex items-center gap-2 text-[10px] text-slate-400 mt-0.5">
              <span className="font-mono text-cyan-300 font-bold">ID: {numericRoomId}</span>
              <span>•</span>
              <span>Host: {room.ownerName}</span>
            </div>
            <div className="flex items-center gap-2 text-[10px] text-slate-400 mt-0.5">
              <span className="text-amber-400 font-semibold">{room.category}</span>
              <span>•</span>
              <span className="flex items-center gap-1 text-emerald-400 font-bold">
                <Users className="w-3 h-3" />
                {room.onlineCount} online
              </span>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-1.5 flex-shrink-0">
          {room.isActive ? (
            <button
              id={`enter-search-room-${room.id}`}
              onClick={() => {
                enterRoom(room.id);
                onClose();
              }}
              className="px-3 py-1.5 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-400 text-black font-extrabold text-[11px] shadow-[0_2px_10px_rgba(16,185,129,0.3)] hover:opacity-90 flex items-center gap-1 transition-transform active:scale-95"
            >
              <span>Enter Room</span>
              <Volume2 className="w-3 h-3" />
            </button>
          ) : (
            <span className="text-[11px] text-slate-500 italic px-2">Offline</span>
          )}
        </div>
      </div>
    );
  }
};
