import React, { useState, useEffect } from 'react';
import {
  Radio,
  Users,
  Mic,
  Coins,
  Gem,
  Gift,
  Activity,
  Zap,
  Volume2,
  Shield,
  Eye,
  TrendingUp,
} from 'lucide-react';
import { useAura } from '../../../context/AuraContext';
import { adminBackend } from '../../../services/adminBackendService';

interface LiveMonitoringModuleProps {
  isDarkMode: boolean;
}

export const LiveMonitoringModule: React.FC<LiveMonitoringModuleProps> = ({ isDarkMode }) => {
  const { rooms, enterRoom } = useAura();
  const [tickerGifts, setTickerGifts] = useState<
    { id: string; sender: string; gift: string; target: string; coins: number; time: string }[]
  >([
    { id: '1', sender: 'Sherry (Owner)', gift: '👑 Imperial Crown', target: 'Bisma Noor', coins: 50000, time: 'Just now' },
    { id: '2', sender: 'Ali Raza', gift: '🎸 Electric Guitar', target: 'Zara Khan', coins: 12000, time: '30s ago' },
    { id: '3', sender: 'Usman VIP', gift: '🚀 Rocket Launch', target: 'Ali Raza', coins: 35000, time: '1m ago' },
  ]);

  const [activeSocketCount, setActiveSocketCount] = useState(148);
  const [latencyMs, setLatencyMs] = useState(24);

  useEffect(() => {
    const timer = setInterval(() => {
      // Simulate live random updates
      setLatencyMs(20 + Math.floor(Math.random() * 8));
      setActiveSocketCount((prev) => prev + (Math.random() > 0.5 ? 1 : -1));
    }, 4000);
    return () => clearInterval(timer);
  }, []);

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
            <Radio className="w-6 h-6 text-rose-500 animate-pulse" />
            <span>Live Audio Radar & Real-Time Monitoring</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Streaming heartbeat, active microphone telemetry, live gifting ticker, and connection stability.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 text-xs font-mono font-bold">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping" />
            <span>LATENCY: {latencyMs}ms</span>
          </div>
          <div className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-purple-500/20 text-purple-300 border border-purple-500/30 text-xs font-mono font-bold">
            <Zap className="w-3.5 h-3.5 text-purple-400" />
            <span>SOCKETS: {activeSocketCount}</span>
          </div>
        </div>
      </div>

      {/* TOP RADAR METRICS */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3.5">
        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">Live Stages</span>
          <div className="text-2xl font-black text-rose-400 mt-1">{rooms.filter((r) => r.isActive).length}</div>
          <span className="text-[11px] text-emerald-400">All channels 100% healthy</span>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">Total Mic Speakers</span>
          <div className="text-2xl font-black text-cyan-400 mt-1">16</div>
          <span className="text-[11px] text-cyan-300">8 VIP Stage seats active</span>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">Gift Velocity / Min</span>
          <div className="text-2xl font-black text-amber-400 mt-1">48,200 Coins</div>
          <span className="text-[11px] text-amber-300">Peak gifting hour active</span>
        </div>

        <div className={`p-4 rounded-2xl border ${cardBg}`}>
          <span className="text-xs font-bold text-slate-400">WebRTC Codec</span>
          <div className="text-2xl font-black text-purple-400 mt-1">Opus 48kHz</div>
          <span className="text-[11px] text-purple-300">Zero packet drop detected</span>
        </div>
      </div>

      {/* 2-COLUMN VIEW: ACTIVE ROOMS RADAR + LIVE GIFT STREAM */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Active Stages Radar */}
        <div className={`p-5 rounded-2xl border lg:col-span-2 ${cardBg}`}>
          <div className="flex items-center justify-between pb-3 border-b border-inherit mb-4">
            <h3 className="font-extrabold text-sm flex items-center gap-2">
              <Radio className="w-4 h-4 text-rose-400" />
              <span>Real-Time Voice Stages Matrix</span>
            </h3>
            <span className="text-[11px] font-mono text-slate-400">Refreshing Live</span>
          </div>

          <div className="space-y-3">
            {rooms.map((room) => (
              <div
                key={room.id}
                className={`p-3.5 rounded-xl border flex flex-col sm:flex-row sm:items-center justify-between gap-3 ${
                  isDarkMode ? 'bg-[#180e32] border-[#371f5f]' : 'bg-slate-50 border-slate-200'
                }`}
              >
                <div className="flex items-center gap-3">
                  <img
                    src={room.coverUrl}
                    alt={room.title}
                    className="w-12 h-12 rounded-xl object-cover border border-purple-500/40"
                  />
                  <div>
                    <div className="font-extrabold text-sm text-white flex items-center gap-2">
                      <span>{room.title}</span>
                      <span className="text-[9px] font-black bg-rose-500/20 text-rose-300 px-1.5 py-0.2 rounded border border-rose-500/30">
                        {room.category}
                      </span>
                    </div>
                    <div className="text-[11px] text-slate-400 flex items-center gap-2 mt-0.5">
                      <span>Host: {room.ownerName}</span>
                      <span>•</span>
                      <span className="text-cyan-400 font-bold">{room.onlineCount} listeners</span>
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-2 self-end sm:self-center">
                  <div className="flex items-center gap-1 text-[11px] font-mono text-emerald-400 bg-emerald-500/10 px-2 py-1 rounded-lg">
                    <Volume2 className="w-3.5 h-3.5" />
                    <span>Active Audio</span>
                  </div>
                  <button
                    onClick={() => enterRoom(room.id)}
                    className="px-3 py-1.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs flex items-center gap-1 shadow-xs"
                  >
                    <Eye className="w-3.5 h-3.5" />
                    <span>Watch Stage</span>
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Live Gifting & Financial Stream */}
        <div className={`p-5 rounded-2xl border ${cardBg}`}>
          <div className="flex items-center justify-between pb-3 border-b border-inherit mb-4">
            <h3 className="font-extrabold text-sm flex items-center gap-2">
              <Gift className="w-4 h-4 text-amber-400" />
              <span>Live Gift Stream Ticker</span>
            </h3>
            <span className="w-2 h-2 rounded-full bg-amber-400 animate-ping" />
          </div>

          <div className="space-y-3">
            {tickerGifts.map((tg) => (
              <div
                key={tg.id}
                className={`p-3 rounded-xl border text-xs ${
                  isDarkMode ? 'bg-[#180e32] border-[#371f5f]' : 'bg-slate-50 border-slate-200'
                }`}
              >
                <div className="flex items-center justify-between font-bold">
                  <span className="text-purple-300">{tg.sender}</span>
                  <span className="text-[10px] text-slate-500">{tg.time}</span>
                </div>
                <div className="text-slate-200 my-1 font-semibold">
                  Sent {tg.gift} to <span className="text-cyan-300">{tg.target}</span>
                </div>
                <div className="flex items-center gap-1 text-[11px] font-black text-amber-400">
                  <Coins className="w-3 h-3" />
                  <span>+{tg.coins.toLocaleString()} Coins</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
