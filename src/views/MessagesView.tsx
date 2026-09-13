import React, { useState } from 'react';
import { MessageSquare, Send, Shield, Sparkles, Search, UserCheck } from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { AvatarWithFrame } from '../components/AvatarWithFrame';

interface MessagesViewProps {
  onOpenUserProfile: (userId: string) => void;
}

export const MessagesView: React.FC<MessagesViewProps> = ({ onOpenUserProfile }) => {
  const { allUsers, currentUser } = useAura();
  const [selectedUser, setSelectedUser] = useState(
    allUsers.find((u) => u.id !== currentUser?.id) || allUsers[0]
  );
  const [chatHistories, setChatHistories] = useState<
    Record<string, { id: string; sender: 'me' | 'them'; text: string; time: string }[]>
  >({
    usr_45623: [
      { id: '1', sender: 'them', text: 'Hey! Join my singing room later tonight! 🎤', time: '10:14 AM' },
      { id: '2', sender: 'me', text: 'Sure thing, save a VIP seat for me! ✨', time: '10:15 AM' },
    ],
    '565656565666555': [
      {
        id: '1',
        sender: 'them',
        text: 'Welcome to AURA Voice Platform! Let me know if you need any official frame or agency assistance.',
        time: '9:00 AM',
      },
    ],
  });
  const [inputText, setInputText] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const currentChat = chatHistories[selectedUser.id] || [
    {
      id: 'welcome',
      sender: 'them',
      text: `Hello! Nice to connect with you on AURA Voice.`,
      time: 'Just now',
    },
  ];

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputText.trim()) return;

    const newMsg = {
      id: Date.now().toString(),
      sender: 'me' as const,
      text: inputText.trim(),
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setChatHistories((prev) => ({
      ...prev,
      [selectedUser.id]: [...(prev[selectedUser.id] || []), newMsg],
    }));

    setInputText('');
  };

  const otherUsers = allUsers.filter(
    (u) =>
      u.id !== currentUser?.id &&
      (u.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
        u.role.toLowerCase().includes(searchQuery.toLowerCase()))
  );

  return (
    <div id="messages-view-container" className="max-w-5xl mx-auto px-4 py-4 pb-24 h-[calc(100vh-140px)]">
      <div className="h-full bg-[#170e30] border border-[#341e5e] rounded-3xl overflow-hidden shadow-2xl flex flex-col md:flex-row">
        {/* Left: Chat Contacts List */}
        <div className="w-full md:w-80 border-b md:border-b-0 md:border-r border-[#2d1952] flex flex-col">
          {/* Contacts Header & Search */}
          <div className="p-3 border-b border-[#2d1952] space-y-2">
            <h2 className="text-sm font-extrabold text-white flex items-center gap-1.5">
              <MessageSquare className="w-4 h-4 text-[#00e5ff]" />
              <span>Direct Messages</span>
            </h2>
            <div className="relative">
              <Search className="w-3.5 h-3.5 absolute left-2.5 top-2.5 text-slate-400" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search friends or admins..."
                className="w-full bg-[#120a24] border border-[#341d5b] rounded-xl pl-8 pr-3 py-1.5 text-xs text-white placeholder-slate-500 focus:outline-hidden"
              />
            </div>
          </div>

          {/* Contacts Scroll List */}
          <div className="flex-1 overflow-y-auto divide-y divide-[#231342]">
            {otherUsers.map((user) => {
              const isSelected = user.id === selectedUser.id;
              const lastMsg = chatHistories[user.id]?.[chatHistories[user.id].length - 1];

              return (
                <div
                  key={user.id}
                  id={`contact-${user.id}`}
                  onClick={() => setSelectedUser(user)}
                  className={`p-3 flex items-center gap-2.5 cursor-pointer transition-colors ${
                    isSelected ? 'bg-[#25144b]' : 'hover:bg-[#1d1039]'
                  }`}
                >
                  <AvatarWithFrame
                    avatarUrl={user.avatarUrl}
                    size={40}
                    equippedFrameId={user.equippedFrameId}
                    vipLevel={user.vipLevel}
                  />
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between">
                      <h4 className="text-xs font-bold text-white truncate">{user.username}</h4>
                      <span className="text-[10px] text-slate-500">
                        {lastMsg ? lastMsg.time : 'Active'}
                      </span>
                    </div>
                    <p className="text-[11px] text-slate-400 truncate mt-0.5">
                      {lastMsg ? lastMsg.text : user.bio}
                    </p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Right: Active Chat Conversation */}
        <div className="flex-1 flex flex-col bg-[#120a24]">
          {/* Active Contact Header */}
          <div className="p-3 border-b border-[#2d1952] flex items-center justify-between bg-[#190e33]">
            <div
              className="flex items-center gap-2.5 cursor-pointer"
              onClick={() => onOpenUserProfile(selectedUser.id)}
            >
              <AvatarWithFrame
                avatarUrl={selectedUser.avatarUrl}
                size={36}
                equippedFrameId={selectedUser.equippedFrameId}
                vipLevel={selectedUser.vipLevel}
              />
              <div>
                <h3 className="text-xs font-extrabold text-white flex items-center gap-1.5 hover:text-[#00e5ff] transition-colors">
                  <span>{selectedUser.username}</span>
                  <span className="text-[9px] font-bold px-1.5 py-0.5 rounded bg-[#7c4dff]/30 text-cyan-300">
                    {selectedUser.role}
                  </span>
                </h3>
                <span className="text-[10px] text-emerald-400 font-semibold">● Online</span>
              </div>
            </div>

            <button
              onClick={() => onOpenUserProfile(selectedUser.id)}
              className="px-3 py-1 rounded-xl bg-[#251549] text-xs font-bold text-slate-300 hover:text-white border border-[#3b216b]"
            >
              Profile
            </button>
          </div>

          {/* Chat Messages Stream */}
          <div className="flex-1 overflow-y-auto p-4 space-y-3">
            {currentChat.map((msg) => {
              const isMe = msg.sender === 'me';
              return (
                <div
                  key={msg.id}
                  className={`flex flex-col ${isMe ? 'items-end' : 'items-start'}`}
                >
                  <div
                    className={`max-w-xs sm:max-w-md px-3.5 py-2 rounded-2xl text-xs leading-relaxed ${
                      isMe
                        ? 'bg-gradient-to-r from-[#7c4dff] to-[#ff2a85] text-white rounded-tr-xs'
                        : 'bg-[#1e113c] border border-[#381f63] text-slate-200 rounded-tl-xs'
                    }`}
                  >
                    {msg.text}
                  </div>
                  <span className="text-[9px] text-slate-500 mt-1 px-1">{msg.time}</span>
                </div>
              );
            })}
          </div>

          {/* Message Input Box */}
          <form
            onSubmit={handleSendMessage}
            className="p-3 border-t border-[#2d1952] bg-[#190e33] flex items-center gap-2"
          >
            <input
              id="direct-message-input"
              type="text"
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              placeholder={`Send message to ${selectedUser.username}...`}
              className="flex-1 bg-[#120a24] border border-[#371f5c] focus:border-[#7c4dff] rounded-2xl px-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-hidden"
            />
            <button
              id="send-dm-btn"
              type="submit"
              disabled={!inputText.trim()}
              className="p-2.5 rounded-xl bg-gradient-to-r from-[#ff2a85] to-[#7c4dff] text-white font-bold disabled:opacity-50 transition-opacity"
            >
              <Send className="w-4 h-4" />
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};
