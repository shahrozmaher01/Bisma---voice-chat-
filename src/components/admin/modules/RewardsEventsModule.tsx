import React, { useState } from 'react';
import {
  Award,
  Calendar,
  CheckCircle2,
  Plus,
  Trash2,
  Coins,
  Gem,
  Sparkles,
  Trophy,
  Zap,
} from 'lucide-react';

interface RewardsEventsModuleProps {
  isDarkMode: boolean;
}

export const RewardsEventsModule: React.FC<RewardsEventsModuleProps> = ({ isDarkMode }) => {
  const [activeTab, setActiveTab] = useState<'events' | 'tasks' | 'levels'>('events');

  // Events
  const [events, setEvents] = useState([
    {
      id: '1',
      title: '🎙️ Ramadan Voice Battle Arena',
      dateRange: 'Mar 15 - Apr 15, 2026',
      prizePoolCoins: 50000000,
      status: 'LIVE',
      participants: 420,
    },
    {
      id: '2',
      title: '💎 Golden Crown Gifter League',
      dateRange: 'Apr 01 - Apr 30, 2026',
      prizePoolCoins: 100000000,
      status: 'UPCOMING',
      participants: 180,
    },
  ]);

  // Tasks
  const [tasks, setTasks] = useState([
    { id: '1', name: 'Stay 30 minutes in any voice stage', rewardCoins: 500, type: 'Daily User' },
    { id: '2', name: 'Send 5 gifts in a room', rewardCoins: 1500, type: 'Daily User' },
    { id: '3', name: 'Host 2 hours of live audio stream', rewardDiamonds: 300, type: 'Host Mission' },
    { id: '4', name: 'Invite a new friend to join AURA', rewardCoins: 5000, type: 'Referral' },
  ]);

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div>
        <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
          <Award className="w-6 h-6 text-amber-400" />
          <span>Events, Tournaments, Missions & Level Curve</span>
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Orchestrate voice competitions, configure daily quests, and balance the gamification economy.
        </p>
      </div>

      {/* Tabs */}
      <div
        className={`flex items-center p-1 rounded-xl border w-fit text-xs font-bold ${
          isDarkMode ? 'bg-[#180e30] border-[#371f5c]' : 'bg-slate-100 border-slate-200'
        }`}
      >
        <button
          onClick={() => setActiveTab('events')}
          className={`px-4 py-1.5 rounded-lg transition-all ${
            activeTab === 'events'
              ? isDarkMode
                ? 'bg-purple-600 text-white shadow-xs'
                : 'bg-white text-purple-700 shadow-xs'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Tournaments & Events
        </button>
        <button
          onClick={() => setActiveTab('tasks')}
          className={`px-4 py-1.5 rounded-lg transition-all ${
            activeTab === 'tasks'
              ? isDarkMode
                ? 'bg-purple-600 text-white shadow-xs'
                : 'bg-white text-purple-700 shadow-xs'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Daily Quests & Missions
        </button>
        <button
          onClick={() => setActiveTab('levels')}
          className={`px-4 py-1.5 rounded-lg transition-all ${
            activeTab === 'levels'
              ? isDarkMode
                ? 'bg-purple-600 text-white shadow-xs'
                : 'bg-white text-purple-700 shadow-xs'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Level & XP Equations
        </button>
      </div>

      {/* TAB CONTENT: EVENTS */}
      {activeTab === 'events' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {events.map((ev) => (
            <div key={ev.id} className={`p-5 rounded-2xl border flex flex-col justify-between ${cardBg}`}>
              <div>
                <div className="flex items-center justify-between pb-3 border-b border-inherit">
                  <h3 className="font-black text-sm text-white">{ev.title}</h3>
                  <span
                    className={`px-2 py-0.5 rounded-full text-[10px] font-black border ${
                      ev.status === 'LIVE'
                        ? 'bg-rose-500/20 text-rose-300 border-rose-500/30 animate-pulse'
                        : 'bg-purple-500/20 text-purple-300 border-purple-500/30'
                    }`}
                  >
                    {ev.status}
                  </span>
                </div>

                <div className="py-3 space-y-2 text-xs">
                  <div className="flex items-center justify-between text-slate-300">
                    <span className="text-slate-400">Duration:</span>
                    <span className="font-bold">{ev.dateRange}</span>
                  </div>

                  <div className="flex items-center justify-between">
                    <span className="text-slate-400">Prize Pool:</span>
                    <span className="font-black text-amber-300 flex items-center gap-1">
                      <Coins className="w-3 h-3" />
                      <span>{ev.prizePoolCoins.toLocaleString()} Coins</span>
                    </span>
                  </div>

                  <div className="flex items-center justify-between text-slate-300">
                    <span className="text-slate-400">Contestants:</span>
                    <span className="font-bold text-cyan-400">{ev.participants} streamers</span>
                  </div>
                </div>
              </div>

              <div className="pt-3 border-t border-inherit flex items-center justify-between">
                <span className="text-[10px] text-slate-500">Live Leaderboard Synced</span>
                <button className="px-3 py-1.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs">
                  Manage Tournament
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* TAB CONTENT: TASKS */}
      {activeTab === 'tasks' && (
        <div className={`p-5 rounded-2xl border ${cardBg}`}>
          <div className="flex items-center justify-between pb-3 border-b border-inherit mb-4">
            <h3 className="font-extrabold text-sm text-white">Daily & Weekly Rewards Catalog</h3>
          </div>

          <div className="space-y-3">
            {tasks.map((task) => (
              <div
                key={task.id}
                className={`p-3 rounded-xl border flex items-center justify-between text-xs ${
                  isDarkMode ? 'bg-[#180e30] border-[#361f5c]' : 'bg-slate-50 border-slate-200'
                }`}
              >
                <div>
                  <div className="font-bold text-slate-200">{task.name}</div>
                  <span className="text-[10px] font-semibold text-purple-400">{task.type}</span>
                </div>

                <div className="text-right">
                  {task.rewardCoins && (
                    <span className="font-bold text-amber-300 flex items-center gap-1">
                      <Coins className="w-3 h-3" />
                      <span>+{task.rewardCoins} Coins</span>
                    </span>
                  )}
                  {task.rewardDiamonds && (
                    <span className="font-bold text-cyan-300 flex items-center gap-1">
                      <Gem className="w-3 h-3" />
                      <span>+{task.rewardDiamonds} Diamonds</span>
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB CONTENT: LEVEL FORMULAS */}
      {activeTab === 'levels' && (
        <div className={`p-5 rounded-2xl border ${cardBg}`}>
          <h3 className="font-extrabold text-sm text-white mb-2">Gamification Equations</h3>
          <p className="text-xs text-slate-400 mb-4">
            Levels are calculated smoothly based on activity, gift volume, and room listening hours.
          </p>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs">
            <div className={`p-4 rounded-xl border ${isDarkMode ? 'bg-[#180e30] border-[#361f5c]' : 'bg-slate-50 border-slate-200'}`}>
              <div className="font-bold text-purple-400 text-sm mb-1">User Level</div>
              <p className="text-slate-300 mb-2">Earned through chat messages, time in rooms, and daily logins.</p>
              <div className="font-mono text-[11px] text-slate-400 bg-black/40 p-2 rounded-lg">
                XP_required = Level^2 * 100
              </div>
            </div>

            <div className={`p-4 rounded-xl border ${isDarkMode ? 'bg-[#180e30] border-[#361f5c]' : 'bg-slate-50 border-slate-200'}`}>
              <div className="font-bold text-amber-400 text-sm mb-1">Rich Level</div>
              <p className="text-slate-300 mb-2">Earned exclusively by spending coins on virtual gifts.</p>
              <div className="font-mono text-[11px] text-slate-400 bg-black/40 p-2 rounded-lg">
                1 Coin Spent = 1 Rich XP
              </div>
            </div>

            <div className={`p-4 rounded-xl border ${isDarkMode ? 'bg-[#180e30] border-[#361f5c]' : 'bg-slate-50 border-slate-200'}`}>
              <div className="font-bold text-cyan-400 text-sm mb-1">Charm Level</div>
              <p className="text-slate-300 mb-2">Earned by streamers receiving gifts and diamonds from fans.</p>
              <div className="font-mono text-[11px] text-slate-400 bg-black/40 p-2 rounded-lg">
                1 Diamond Received = 1 Charm XP
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
