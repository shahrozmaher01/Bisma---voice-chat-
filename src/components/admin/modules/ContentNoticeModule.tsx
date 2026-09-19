import React, { useState } from 'react';
import {
  Smartphone,
  Bell,
  Plus,
  Trash2,
  CheckCircle2,
  Image,
  Send,
  ExternalLink,
  Users,
  Eye,
  Radio,
} from 'lucide-react';

interface ContentNoticeModuleProps {
  isDarkMode: boolean;
}

export const ContentNoticeModule: React.FC<ContentNoticeModuleProps> = ({ isDarkMode }) => {
  // Banners
  const [banners, setBanners] = useState([
    {
      id: '1',
      title: 'Ramadan Voice Carnival 2026',
      imageUrl: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800',
      actionUrl: '#carnival',
      isActive: true,
    },
    {
      id: '2',
      title: 'Top Gifter VIP Championship',
      imageUrl: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=800',
      actionUrl: '#vip',
      isActive: true,
    },
  ]);

  const [newBannerTitle, setNewBannerTitle] = useState('');
  const [newBannerImage, setNewBannerImage] = useState('');

  // Push broadcast
  const [pushAudience, setPushAudience] = useState<'ALL' | 'ONLINE' | 'VIP' | 'HOSTS'>('ALL');
  const [pushTitle, setPushTitle] = useState('');
  const [pushMessage, setPushMessage] = useState('');
  const [broadcastLogs, setBroadcastLogs] = useState<{ title: string; audience: string; time: string }[]>([
    { title: 'Server upgrade completed', audience: 'ALL', time: '1 hour ago' },
  ]);

  const [notice, setNotice] = useState<string | null>(null);

  const handleAddBanner = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newBannerTitle.trim()) return;
    const newB = {
      id: Date.now().toString(),
      title: newBannerTitle.trim(),
      imageUrl: newBannerImage.trim() || 'https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=800',
      actionUrl: '#promo',
      isActive: true,
    };
    setBanners([newB, ...banners]);
    setNewBannerTitle('');
    setNewBannerImage('');
    setNotice(`New carousel banner published!`);
    setTimeout(() => setNotice(null), 3000);
  };

  const handleDeleteBanner = (id: string) => {
    setBanners(banners.filter((b) => b.id !== id));
    setNotice(`Banner removed.`);
    setTimeout(() => setNotice(null), 3000);
  };

  const handleSendPush = (e: React.FormEvent) => {
    e.preventDefault();
    if (!pushTitle.trim() || !pushMessage.trim()) return;
    setBroadcastLogs([{ title: pushTitle.trim(), audience: pushAudience, time: 'Just now' }, ...broadcastLogs]);
    setNotice(`Push broadcast successfully dispatched to ${pushAudience}!`);
    setPushTitle('');
    setPushMessage('');
    setTimeout(() => setNotice(null), 3500);
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div>
        <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
          <Smartphone className="w-6 h-6 text-cyan-400" />
          <span>Banners, Carousel Ads & Push Broadcasts</span>
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Control home screen carousel slides, broadcast push alerts to devices, and notify users of events.
        </p>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* 2 COLUMNS: BANNERS & PUSH BROADCAST */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Carousel Banners */}
        <div className={`p-5 rounded-2xl border ${cardBg}`}>
          <div className="flex items-center justify-between pb-3 border-b border-inherit mb-4">
            <h3 className="font-extrabold text-sm flex items-center gap-2 text-white">
              <Image className="w-4 h-4 text-cyan-400" />
              <span>In-App Home Banners ({banners.length})</span>
            </h3>
          </div>

          <form onSubmit={handleAddBanner} className="space-y-3 mb-5 text-xs">
            <input
              type="text"
              required
              placeholder="Banner Campaign Title..."
              value={newBannerTitle}
              onChange={(e) => setNewBannerTitle(e.target.value)}
              className={`w-full px-3 py-2 rounded-xl border outline-none ${
                isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
              }`}
            />
            <input
              type="url"
              placeholder="Image URL (https://...)"
              value={newBannerImage}
              onChange={(e) => setNewBannerImage(e.target.value)}
              className={`w-full px-3 py-2 rounded-xl border outline-none ${
                isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
              }`}
            />
            <button
              type="submit"
              className="w-full py-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 text-white font-extrabold shadow-md hover:opacity-95 text-xs"
            >
              + Add to Banner Carousel
            </button>
          </form>

          {/* Existing Banners */}
          <div className="space-y-3">
            {banners.map((banner) => (
              <div
                key={banner.id}
                className={`p-3 rounded-xl border flex items-center gap-3 relative group ${
                  isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-50 border-slate-200'
                }`}
              >
                <img
                  src={banner.imageUrl}
                  alt={banner.title}
                  className="w-20 h-12 rounded-lg object-cover border border-purple-500/30 flex-shrink-0"
                />
                <div className="min-w-0 flex-1">
                  <div className="font-bold text-xs text-white truncate">{banner.title}</div>
                  <span className="text-[10px] text-emerald-400 font-semibold">Active in App</span>
                </div>
                <button
                  onClick={() => handleDeleteBanner(banner.id)}
                  className="p-1.5 rounded-lg text-rose-400 hover:bg-rose-500/20 transition-colors"
                  title="Remove Banner"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Push Notification Broadcast */}
        <div className={`p-5 rounded-2xl border ${cardBg}`}>
          <div className="flex items-center justify-between pb-3 border-b border-inherit mb-4">
            <h3 className="font-extrabold text-sm flex items-center gap-2 text-white">
              <Bell className="w-4 h-4 text-purple-400" />
              <span>Instant Push Notification Dispatcher</span>
            </h3>
          </div>

          <form onSubmit={handleSendPush} className="space-y-3 text-xs">
            <div>
              <label className="block text-slate-400 font-bold mb-1">Target Device Audience</label>
              <select
                value={pushAudience}
                onChange={(e) => setPushAudience(e.target.value as any)}
                className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                  isDarkMode ? 'bg-[#180e30] border-[#3a2062] text-white' : 'bg-slate-100 border-slate-300'
                }`}
              >
                <option value="ALL">All Registered Users (Global Broadcast)</option>
                <option value="ONLINE">Currently Online Users Only</option>
                <option value="VIP">VIP Members Only</option>
                <option value="HOSTS">Verified Creators & Hosts Only</option>
              </select>
            </div>

            <div>
              <label className="block text-slate-400 font-bold mb-1">Notification Title</label>
              <input
                type="text"
                required
                placeholder="e.g. 🔥 Weekend Gift Carnival has begun!"
                value={pushTitle}
                onChange={(e) => setPushTitle(e.target.value)}
                className={`w-full px-3 py-2 rounded-xl border outline-none font-bold ${
                  isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
                }`}
              />
            </div>

            <div>
              <label className="block text-slate-400 font-bold mb-1">Message Body</label>
              <textarea
                rows={3}
                required
                placeholder="Join the battle rooms now and double your diamond rewards..."
                value={pushMessage}
                onChange={(e) => setPushMessage(e.target.value)}
                className={`w-full px-3 py-2 rounded-xl border outline-none ${
                  isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
                }`}
              />
            </div>

            <button
              type="submit"
              className="w-full py-2.5 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 text-white font-extrabold shadow-md hover:opacity-95 text-xs flex items-center justify-center gap-1.5"
            >
              <Send className="w-3.5 h-3.5" />
              <span>Send Push Notification</span>
            </button>
          </form>

          {/* Broadcast logs */}
          <div className="mt-4 pt-3 border-t border-inherit">
            <span className="text-[10px] font-extrabold uppercase text-slate-400">Recent Dispatches</span>
            <div className="space-y-1.5 mt-2 text-xs">
              {broadcastLogs.map((log, lIdx) => (
                <div key={lIdx} className="flex items-center justify-between text-slate-300">
                  <span className="truncate">{log.title}</span>
                  <span className="text-[10px] text-slate-500 font-mono">{log.time}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
