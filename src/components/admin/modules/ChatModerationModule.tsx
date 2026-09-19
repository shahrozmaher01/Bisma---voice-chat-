import React, { useState } from 'react';
import {
  MessageSquare,
  Plus,
  Trash2,
  ShieldCheck,
  AlertTriangle,
  CheckCircle2,
  Sliders,
  Send,
} from 'lucide-react';

interface ChatModerationModuleProps {
  isDarkMode: boolean;
}

export const ChatModerationModule: React.FC<ChatModerationModuleProps> = ({ isDarkMode }) => {
  const [blacklist, setBlacklist] = useState<string[]>([
    'scam',
    'free coins',
    'whatsapp',
    'cheat',
    'hack',
    'telegram',
    'crypto giveaway',
    'password',
    'bank pin',
  ]);
  const [newWord, setNewWord] = useState('');
  const [autoMuteOnTrigger, setAutoMuteOnTrigger] = useState(true);
  const [blockExternalLinks, setBlockExternalLinks] = useState(true);
  const [spamSensitivity, setSpamSensitivity] = useState(3); // msgs per 5 sec

  // Sandbox testing
  const [testMessage, setTestMessage] = useState('');
  const [testResult, setTestResult] = useState<{ allowed: boolean; flaggedWords: string[] } | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const handleAddWord = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newWord.trim()) return;
    const word = newWord.trim().toLowerCase();
    if (!blacklist.includes(word)) {
      setBlacklist([...blacklist, word]);
      setNotice(`Added "${word}" to prohibited keywords blacklist.`);
      setTimeout(() => setNotice(null), 3000);
    }
    setNewWord('');
  };

  const handleRemoveWord = (word: string) => {
    setBlacklist(blacklist.filter((w) => w !== word));
    setNotice(`Removed "${word}" from blacklist.`);
    setTimeout(() => setNotice(null), 3000);
  };

  const handleTestFilter = (e: React.FormEvent) => {
    e.preventDefault();
    const lower = testMessage.toLowerCase();
    const flagged = blacklist.filter((w) => lower.includes(w));
    setTestResult({
      allowed: flagged.length === 0,
      flaggedWords: flagged,
    });
  };

  const cardBg = isDarkMode ? 'bg-[#120826] border-[#2b174e]' : 'bg-white border-slate-200 shadow-xs';

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Title */}
      <div>
        <h2 className="text-xl sm:text-2xl font-black flex items-center gap-2">
          <MessageSquare className="w-6 h-6 text-purple-400" />
          <span>Chat Security, Profanity Filters & Anti-Spam</span>
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Prohibit illegal solicitations, block phishing links, and auto-moderate voice room text chats.
        </p>
      </div>

      {notice && (
        <div className="p-3 rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs font-bold flex items-center gap-2 animate-in fade-in">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{notice}</span>
        </div>
      )}

      {/* FILTER CONTROLS */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Blacklist Words Manager */}
        <div className={`p-5 rounded-2xl border ${cardBg}`}>
          <h3 className="font-extrabold text-sm flex items-center gap-2 text-white mb-3">
            <ShieldCheck className="w-4 h-4 text-purple-400" />
            <span>Prohibited Words & Phrases ({blacklist.length})</span>
          </h3>

          <form onSubmit={handleAddWord} className="flex items-center gap-2 mb-4">
            <input
              type="text"
              placeholder="Add keyword or spam pattern..."
              value={newWord}
              onChange={(e) => setNewWord(e.target.value)}
              className={`flex-1 px-3 py-2 rounded-xl border text-xs outline-none ${
                isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
              }`}
            />
            <button
              type="submit"
              className="px-3 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs flex items-center gap-1 shadow-xs"
            >
              <Plus className="w-4 h-4" />
              <span>Add</span>
            </button>
          </form>

          <div className="flex flex-wrap gap-2 max-h-56 overflow-y-auto">
            {blacklist.map((word) => (
              <span
                key={word}
                className="flex items-center gap-1 px-2.5 py-1 rounded-xl text-xs font-bold bg-purple-500/20 text-purple-300 border border-purple-500/30"
              >
                <span>{word}</span>
                <button onClick={() => handleRemoveWord(word)} className="hover:text-rose-400">
                  <Trash2 className="w-3 h-3" />
                </button>
              </span>
            ))}
          </div>
        </div>

        {/* Global Chat Sensitivity Rules & Sandbox */}
        <div className="space-y-4">
          <div className={`p-5 rounded-2xl border ${cardBg}`}>
            <h3 className="font-extrabold text-sm flex items-center gap-2 text-white mb-3">
              <Sliders className="w-4 h-4 text-cyan-400" />
              <span>Auto-Enforcement Policies</span>
            </h3>

            <div className="space-y-3 text-xs">
              <div className="flex items-center justify-between">
                <div>
                  <div className="font-bold text-slate-200">Auto-Mute on Violation</div>
                  <div className="text-[10px] text-slate-400">Instantly mutes user for 15 minutes</div>
                </div>
                <input
                  type="checkbox"
                  checked={autoMuteOnTrigger}
                  onChange={(e) => setAutoMuteOnTrigger(e.target.checked)}
                  className="w-4 h-4 accent-purple-600"
                />
              </div>

              <div className="flex items-center justify-between">
                <div>
                  <div className="font-bold text-slate-200">Block External URL Links</div>
                  <div className="text-[10px] text-slate-400">Drops messages containing .com, http, t.me</div>
                </div>
                <input
                  type="checkbox"
                  checked={blockExternalLinks}
                  onChange={(e) => setBlockExternalLinks(e.target.checked)}
                  className="w-4 h-4 accent-purple-600"
                />
              </div>
            </div>
          </div>

          {/* Test Filter Sandbox */}
          <div className={`p-5 rounded-2xl border ${cardBg}`}>
            <h3 className="font-extrabold text-sm flex items-center gap-2 text-white mb-2">
              <span>Filter Simulator Sandbox</span>
            </h3>

            <form onSubmit={handleTestFilter} className="flex gap-2">
              <input
                type="text"
                placeholder="Test a chat sentence..."
                value={testMessage}
                onChange={(e) => setTestMessage(e.target.value)}
                className={`flex-1 px-3 py-2 rounded-xl border text-xs outline-none ${
                  isDarkMode ? 'bg-[#180e30] border-[#3a2062]' : 'bg-slate-100 border-slate-300'
                }`}
              />
              <button
                type="submit"
                className="px-3 py-2 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white font-bold text-xs"
              >
                Inspect
              </button>
            </form>

            {testResult && (
              <div
                className={`mt-3 p-2.5 rounded-xl border text-xs font-bold ${
                  testResult.allowed
                    ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/30'
                    : 'bg-rose-500/20 text-rose-300 border-rose-500/30'
                }`}
              >
                {testResult.allowed
                  ? '✅ Message passes safety filter!'
                  : `❌ Blocked! Triggered words: ${testResult.flaggedWords.join(', ')}`}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
