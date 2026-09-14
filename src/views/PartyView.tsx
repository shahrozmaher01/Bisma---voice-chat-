import React, { useState } from 'react';
import {
  Mic,
  Users,
  Flame,
  Globe,
  Sparkles,
  Trophy,
  Volume2,
  Lock,
  Search,
  Plus,
} from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { VoiceRoom } from '../types';
import { AvatarWithFrame } from '../components/AvatarWithFrame';

interface PartyViewProps {
  onOpenUserProfile: (userId: string) => void;
  onOpenCreateRoom: () => void;
  onOpenRankings: () => void;
  onOpenSearch?: (category?: 'id' | 'room' | 'user') => void;
}

const CATEGORY_TABS = [
  { id: 'all', label: 'Explore All' },
  { id: 'singing', label: 'Singing & Chill 🎵' },
  { id: 'chat', label: 'Chat & Friends ☕' },
  { id: 'global', label: 'Global & English 🌐' },
  { id: 'poetry', label: 'Poetry & Art 📖' },
];

export const PartyView: React.FC<PartyViewProps> = ({
  onOpenUserProfile,
  onOpenCreateRoom,
  onOpenRankings,
  onOpenSearch,
}) => {
  const { rooms, enterRoom } = useAura();
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');

  const activeRooms = rooms.filter((r) => r.isActive);

  const filteredRooms = activeRooms.filter((r) => {
    const numericId = r.id.replace('room_', '').toLowerCase();
    const query = searchQuery.toLowerCase();
    const matchesSearch =
      !query ||
      r.title.toLowerCase().includes(query) ||
      r.ownerName.toLowerCase().includes(query) ||
      r.category.toLowerCase().includes(query) ||
      numericId.includes(query) ||
      r.id.toLowerCase().includes(query);

    if (!matchesSearch) return false;
    if (selectedCategory === 'all') return true;
    if (selectedCategory === 'singing') return r.category.includes('Singing') || r.category.includes('Music');
    if (selectedCategory === 'chat') return r.category.includes('Chat');
    if (selectedCategory === 'global') return r.category.includes('Global');
    if (selectedCategory === 'poetry') return r.category.includes('Poetry');
    return true;
  });

  const featuredRoom = activeRooms.find((r) => r.isFeatured) || activeRooms[0];

  return (
    <div id="party-view-container" className="max-w-5xl mx-auto px-4 py-4 space-y-5 pb-24">
      {/* Featured Room Hero Banner */}
      {featuredRoom && (
        <div
          id={`hero-featured-room-${featuredRoom.id}`}
          onClick={() => enterRoom(featuredRoom.id)}
          className="relative rounded-3xl overflow-hidden cursor-pointer group border border-[#4a2682] shadow-2xl transition-transform hover:scale-[1.01]"
        >
          {/* Background image & gradient */}
          <div
            className="absolute inset-0 bg-cover bg-center transition-transform duration-500 group-hover:scale-105"
            style={{ backgroundImage: `url(${featuredRoom.coverUrl})` }}
          />
          <div className="absolute inset-0 bg-gradient-to-t from-[#0c071a] via-[#0c071a]/70 to-transparent" />

          {/* Hero Content */}
          <div className="relative z-10 p-5 sm:p-7 flex flex-col justify-end min-h-[190px] sm:min-h-[220px]">
            <div className="flex items-center gap-2 mb-2">
              <span className="px-2.5 py-0.5 rounded-full bg-gradient-to-r from-amber-500 to-rose-500 text-black text-[10px] font-black tracking-wider flex items-center gap-1 shadow">
                <Flame className="w-3 h-3 text-black" />
                FEATURED OFFICIAL STAGE
              </span>
              <span className="px-2 py-0.5 rounded-full bg-black/60 text-emerald-300 text-[10px] font-bold border border-emerald-500/30 flex items-center gap-1">
                <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
                {featuredRoom.onlineCount} Online
              </span>
            </div>

            <h1 className="text-xl sm:text-2xl font-black text-white drop-shadow-md">
              {featuredRoom.title}
            </h1>
            <p className="text-xs text-slate-300 mt-1 max-w-lg line-clamp-1">
              {featuredRoom.announcement}
            </p>

            <div className="flex items-center justify-between mt-3 pt-3 border-t border-white/10">
              <div className="flex items-center gap-2">
                <AvatarWithFrame
                  avatarUrl={featuredRoom.ownerAvatar}
                  size={32}
                  equippedFrameId="frame_super_admin"
                />
                <span className="text-xs font-bold text-white drop-shadow">
                  Host: {featuredRoom.ownerName}
                </span>
              </div>

              <button
                id="hero-enter-room-btn"
                className="px-4 py-2 rounded-xl bg-gradient-to-r from-[#ff2a85] to-[#7c4dff] text-white font-extrabold text-xs shadow-lg flex items-center gap-1.5 group-hover:opacity-95 transition-opacity"
              >
                <Mic className="w-3.5 h-3.5" />
                <span>Join Stage</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Category Pills & Search */}
      <div className="space-y-3">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          {/* Category Tabs */}
          <div className="flex items-center gap-1.5 overflow-x-auto pb-1 scrollbar-none">
            {CATEGORY_TABS.map((tab) => (
              <button
                key={tab.id}
                id={`cat-tab-${tab.id}`}
                onClick={() => setSelectedCategory(tab.id)}
                className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all flex-shrink-0 ${
                  selectedCategory === tab.id
                    ? 'bg-[#7c4dff] text-white shadow-md'
                    : 'bg-[#190e33] border border-[#321d58] text-slate-400 hover:text-white'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>

          {/* Search Box & Advanced Search Button */}
          <div className="flex items-center gap-1.5 w-full sm:w-auto">
            <div className="relative flex-1 sm:w-60">
              <Search className="w-3.5 h-3.5 absolute left-3 top-2.5 text-slate-400" />
              <input
                id="search-party-rooms-input"
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search rooms, ID, host..."
                className="w-full bg-[#180e33] border border-[#371f5c] focus:border-[#7c4dff] rounded-xl pl-9 pr-3 py-1.5 text-xs text-white placeholder-slate-500 focus:outline-hidden"
              />
            </div>
            {onOpenSearch && (
              <button
                id="party-open-search-modal-btn"
                onClick={() => onOpenSearch('id')}
                className="px-3 py-1.5 rounded-xl bg-[#221342] hover:bg-[#2e1a58] border border-[#442875] text-[#00e5ff] text-xs font-bold flex items-center gap-1.5 transition-colors flex-shrink-0"
                title="Search by ID Number, Room, or User"
              >
                <Search className="w-3.5 h-3.5" />
                <span>Search</span>
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Voice Rooms Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3.5">
        {filteredRooms.map((room) => {
          const numericRoomId = room.id.replace('room_', '');
          return (
            <div
              key={room.id}
              id={`room-card-${room.id}`}
              onClick={() => enterRoom(room.id)}
              className="bg-[#170e30] border border-[#321d59] hover:border-[#7c4dff]/80 rounded-2xl overflow-hidden cursor-pointer transition-all hover:-translate-y-1 hover:shadow-[0_8px_25px_rgba(124,77,255,0.25)] flex flex-col justify-between group"
            >
              {/* Top Cover Thumbnail & Info */}
              <div className="relative h-28 overflow-hidden">
                <img
                  src={room.coverUrl}
                  alt={room.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                  referrerPolicy="no-referrer"
                  loading="lazy"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-[#170e30] via-transparent to-black/30" />

                {/* Online Count Pill */}
                <div className="absolute top-2 right-2 px-2 py-0.5 rounded-full bg-black/60 backdrop-blur-xs text-[10px] font-bold text-emerald-300 border border-emerald-500/30 flex items-center gap-1">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
                  <span>{room.onlineCount} Online</span>
                </div>

                {/* Category Tag */}
                <div className="absolute top-2 left-2 px-2 py-0.5 rounded-full bg-black/60 backdrop-blur-xs text-[10px] font-bold text-slate-300">
                  {room.category}
                </div>

                {/* Host Avatar on edge */}
                <div className="absolute -bottom-1 left-3">
                  <AvatarWithFrame
                    avatarUrl={room.ownerAvatar}
                    size={36}
                    equippedFrameId={room.ownerId === '565656565666555' ? 'frame_super_admin' : 'frame_host'}
                  />
                </div>
              </div>

              {/* Room Details Bottom */}
              <div className="p-3 pt-2 flex flex-col gap-1.5">
                <div className="flex items-center justify-between gap-1">
                  <h3 className="text-xs font-extrabold text-white truncate group-hover:text-[#00e5ff] transition-colors">
                    {room.title}
                  </h3>
                  <span className="text-[10px] font-mono text-cyan-300 font-bold bg-[#110722] px-1.5 py-0.5 rounded border border-[#2b174b] flex-shrink-0">
                    ID: {numericRoomId}
                  </span>
                </div>
                <p className="text-[11px] text-slate-400 line-clamp-1">
                  {room.announcement}
                </p>

                <div className="flex items-center justify-between text-[10px] text-slate-500 pt-2 border-t border-[#271647]">
                  <span>Host: <strong className="text-slate-300 font-semibold">{room.ownerName}</strong></span>
                  <span className="flex items-center gap-1">
                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
                    <span className="text-emerald-400 font-bold">LIVE</span>
                    <span>• {room.country}</span>
                  </span>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {filteredRooms.length === 0 && (
        <div className="py-12 text-center text-xs text-slate-500 space-y-3">
          <p>No voice rooms found matching &quot;{searchQuery}&quot;.</p>
          <div className="flex items-center justify-center gap-2">
            <button
              onClick={onOpenCreateRoom}
              className="px-4 py-2 rounded-xl bg-gradient-to-r from-[#ff2a85] to-[#7c4dff] text-white font-bold text-xs shadow hover:opacity-95"
            >
              Open Your Room
            </button>
            {onOpenSearch && (
              <button
                onClick={() => onOpenSearch('id')}
                className="px-4 py-2 rounded-xl bg-[#20123f] border border-[#3e246c] text-[#00e5ff] font-bold text-xs hover:bg-[#2b1855]"
              >
                Search by ID
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
