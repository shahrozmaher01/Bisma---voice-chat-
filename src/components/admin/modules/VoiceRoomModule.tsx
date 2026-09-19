import React, { useState } from 'react';
import {
  Radio,
  Search,
  Lock,
  Unlock,
  Volume2,
  VolumeX,
  XCircle,
  Megaphone,
  Users,
  Eye,
  CheckCircle2,
  AlertTriangle,
  X,
} from 'lucide-react';
import { useAura } from '../../../context/AuraContext';
import { VoiceRoom } from '../../../types';

interface VoiceRoomModuleProps {
  isDarkMode: boolean;
}

export const VoiceRoomModule: React.FC<VoiceRoomModuleProps> = ({ isDarkMode }) => {
  const { rooms, hostCloseRoom, enterRoom } = useAura();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedRoom, setSelectedRoom] = useState<VoiceRoom | null>(null);
  const [showAnnouncementModal, setShowAnnouncementModal] = useState(false);
  const [announcementText, setAnnouncementText] = useState('');
  const [notice, setNotice] = useState<string | null>(null);

  // Local state for toggling locked rooms
  const [lockedRoomIds, setLockedRoomIds] = useState<string[]>([]);

  const filteredRooms = rooms.filter((r) => {
    return (
      r.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.ownerName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.id.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.category.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  const handleToggleLock = (roomId: string) => {
    setLockedRoomIds((prev) =>
      prev.includes(roomId) ? prev.filter((id) => id !== roomId) : [...prev, roomId]
    );
    setNotice(`Room security lock toggled.`);
    setTimeout(() => setNotice(null), 3000);
  };

  const handleForceClose = (room: VoiceRoom) => {
    hostCloseRoom(room.id);
    setNotice(`Emergency shutdown executed on room "${room.title}".`);
    setTimeout(() => setNotice(null), 3500);
  };

  const handleSendAnnouncement = (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedRoom || !announcementText.trim()) return;
    setShowAnnouncementModal(false);
    setNotice(`Broadcasted announcement to "${selectedRoom.title}": ${announcementText}`);
    setAnnouncementText('');
    setTimeout(() => setNotice(null), 3500);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <Radio className="w-6 h-6 text-rose-400" />
            <span>Voice Room Operations & Moderation</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Monitor real-time audio rooms, lock stages, force close abusive rooms, and send emergency alerts.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <span className="px-3 py-1 rounded-full text-xs font-bold bg-rose-500/20 text-rose-300 border border-rose-500/30 flex items-center gap-1.5">
            <span className="w-2 h-2 rounded-full bg-rose-500 animate-ping" />
            <span>{rooms.filter((r) => r.isActive).length} Active Live Rooms</span>
          </span>
        </div>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* SEARCH BAR */}
      <div className={`p-4 rounded-2xl border flex items-center justify-between ${cardBg}`}>
        <div
          className={`w-full md:w-80 flex items-center gap-2 px-3 py-2 rounded-xl border text-xs ${
            isDarkMode ? 'bg-[#180e30] border-[#361f5c]' : 'bg-slate-100 border-slate-200'
          }`}
        >
          <Search className="w-4 h-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search room by title, owner, category, ID..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-transparent border-none outline-none text-xs"
          />
        </div>
      </div>

      {/* ROOMS GRID */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredRooms.map((room) => {
          const isLocked = lockedRoomIds.includes(room.id) || room.isLocked;
          return (
            <div
              key={room.id}
              className={`rounded-2xl border overflow-hidden flex flex-col justify-between ${cardBg}`}
            >
              {/* Room Cover & Header */}
              <div className="relative h-28 w-full bg-slate-800">
                <img
                  src={room.coverUrl}
                  alt={room.title}
                  className="w-full h-full object-cover opacity-80"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-[#120826] via-transparent to-black/50" />

                {/* Badges on cover */}
                <div className="absolute top-2 left-2 flex items-center gap-1.5">
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-rose-600 text-white flex items-center gap-1">
                    <span className="w-1.5 h-1.5 rounded-full bg-white animate-pulse" />
                    <span>LIVE</span>
                  </span>
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-black/60 text-slate-200 backdrop-blur-xs">
                    {room.category}
                  </span>
                </div>

                <div className="absolute top-2 right-2">
                  {isLocked && (
                    <span className="p-1 rounded-full bg-amber-500 text-black shadow-md flex items-center justify-center">
                      <Lock className="w-3 h-3" />
                    </span>
                  )}
                </div>

                {/* Audience Count */}
                <div className="absolute bottom-2 right-2 flex items-center gap-1 text-[11px] font-bold text-white bg-black/60 px-2 py-0.5 rounded-full backdrop-blur-xs">
                  <Users className="w-3 h-3 text-cyan-400" />
                  <span>{room.onlineCount} online</span>
                </div>
              </div>

              {/* Room Content */}
              <div className="p-4 flex-1 flex flex-col justify-between">
                <div>
                  <h3 className="font-extrabold text-sm text-white truncate">{room.title}</h3>
                  <div className="flex items-center gap-2 mt-1">
                    <img
                      src={room.ownerAvatar}
                      alt={room.ownerName}
                      className="w-5 h-5 rounded-full object-cover border border-purple-500/40"
                    />
                    <span className="text-xs text-slate-300 font-semibold truncate">{room.ownerName}</span>
                    <span className="text-[10px] font-mono text-slate-500 truncate">ID: {room.id}</span>
                  </div>

                  <p className="text-[11px] text-slate-400 mt-2 line-clamp-2 italic">
                    &quot;{room.announcement || room.description || 'Welcome to the audio room!'}&quot;
                  </p>
                </div>

                {/* Room Admin Actions */}
                <div className="pt-4 mt-3 border-t border-inherit flex items-center justify-between gap-2">
                  <button
                    onClick={() => enterRoom(room.id)}
                    className="flex-1 py-1.5 px-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-[11px] flex items-center justify-center gap-1"
                  >
                    <Eye className="w-3.5 h-3.5" />
                    <span>Join Stage</span>
                  </button>

                  <button
                    onClick={() => handleToggleLock(room.id)}
                    className={`p-2 rounded-xl border transition-colors ${
                      isLocked
                        ? 'border-amber-500/40 text-amber-300 hover:bg-amber-500/20'
                        : 'border-slate-500/40 text-slate-300 hover:bg-slate-500/20'
                    }`}
                    title={isLocked ? 'Unlock Room' : 'Lock Room'}
                  >
                    {isLocked ? <Lock className="w-3.5 h-3.5" /> : <Unlock className="w-3.5 h-3.5" />}
                  </button>

                  <button
                    onClick={() => {
                      setSelectedRoom(room);
                      setShowAnnouncementModal(true);
                    }}
                    className="p-2 rounded-xl border border-cyan-500/40 text-cyan-300 hover:bg-cyan-500/20 transition-colors"
                    title="Broadcast Announcement"
                  >
                    <Megaphone className="w-3.5 h-3.5" />
                  </button>

                  <button
                    onClick={() => handleForceClose(room)}
                    className="p-2 rounded-xl border border-rose-500/40 text-rose-300 hover:bg-rose-500/20 transition-colors"
                    title="Force Close Room"
                  >
                    <XCircle className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* ANNOUNCEMENT MODAL */}
      {showAnnouncementModal && selectedRoom && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4">
          <div
            className={`w-full max-w-md rounded-2xl border p-5 shadow-2xl ${
              isDarkMode ? 'bg-[#150a2b] border-[#391e63]' : 'bg-white border-slate-200'
            }`}
          >
            <div className="flex items-center justify-between pb-3 border-b border-inherit">
              <h3 className="text-sm font-bold flex items-center gap-2">
                <Megaphone className="w-4 h-4 text-cyan-400" />
                <span>Room Announcement: {selectedRoom.title}</span>
              </h3>
              <button onClick={() => setShowAnnouncementModal(false)} className="text-slate-400 hover:text-white">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSendAnnouncement} className="space-y-4 pt-4 text-xs">
              <div>
                <label className="block text-slate-400 font-bold mb-1">Official Banner Message</label>
                <textarea
                  rows={3}
                  required
                  value={announcementText}
                  onChange={(e) => setAnnouncementText(e.target.value)}
                  placeholder="Official message to all speakers and listeners in this room..."
                  className={`w-full px-3 py-2 rounded-xl border outline-none font-medium ${
                    isDarkMode ? 'bg-[#1b0d36] border-[#442377]' : 'bg-slate-100 border-slate-300'
                  }`}
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowAnnouncementModal(false)}
                  className="px-4 py-2 rounded-xl border border-slate-500/40 text-slate-300 hover:bg-slate-500/20 font-bold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 text-white font-extrabold shadow-md"
                >
                  Broadcast to Room
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
