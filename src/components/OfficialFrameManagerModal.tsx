import React, { useState } from 'react';
import {
  X,
  Sparkles,
  Shield,
  Clock,
  UserCheck,
  AlertTriangle,
  CheckCircle,
  Search,
  RotateCcw,
  Calendar,
} from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { OFFICIAL_FRAMES } from '../data/seedData';
import { OfficialFrameDef } from '../types';

interface OfficialFrameManagerModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const OfficialFrameManagerModal: React.FC<OfficialFrameManagerModalProps> = ({
  isOpen,
  onClose,
}) => {
  const {
    officialFrames,
    frameAssignments,
    sendOfficialFrame,
    revokeOfficialFrame,
    allUsers,
  } = useAura();

  const [targetUserId, setTargetUserId] = useState('');
  const [selectedFrameId, setSelectedFrameId] = useState(OFFICIAL_FRAMES[0].id);
  const [durationDays, setDurationDays] = useState<number>(30);
  const [customDays, setCustomDays] = useState('');
  const [feedback, setFeedback] = useState<{ success: boolean; message: string } | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  if (!isOpen) return null;

  const selectedFrame =
    officialFrames.find((f) => f.id === selectedFrameId) || officialFrames[0];

  const handleSendFrame = (e: React.FormEvent) => {
    e.preventDefault();
    setFeedback(null);

    const days = customDays ? parseInt(customDays, 10) : durationDays;
    if (!targetUserId.trim()) {
      setFeedback({ success: false, message: 'Please enter a target User ID.' });
      return;
    }
    if (isNaN(days) || days <= 0) {
      setFeedback({ success: false, message: 'Please specify a valid duration in days.' });
      return;
    }

    const res = sendOfficialFrame(targetUserId.trim(), selectedFrameId, days);
    setFeedback(res);
    if (res.success) {
      setTargetUserId('');
      setCustomDays('');
    }
  };

  const filteredAssignments = frameAssignments.filter(
    (a) =>
      a.userId.toLowerCase().includes(searchQuery.toLowerCase()) ||
      a.userName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      a.frameName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div
      id="official-frames-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-3 sm:p-5 animate-in fade-in duration-200"
      onClick={onClose}
    >
      <div
        id="official-frames-modal"
        className="w-full max-w-4xl bg-[#130b26] border border-[#442875] rounded-3xl p-4 sm:p-6 shadow-2xl flex flex-col max-h-[90vh] overflow-hidden relative"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between border-b border-[#2d1a4e] pb-3">
          <div className="flex items-center gap-2.5">
            <div className="w-10 h-10 rounded-2xl bg-gradient-to-tr from-[#00e5ff] via-[#7c4dff] to-[#ff2a85] flex items-center justify-center shadow-lg">
              <Sparkles className="w-5 h-5 text-white" />
            </div>
            <div>
              <h2 className="text-base sm:text-lg font-extrabold text-white flex items-center gap-2">
                <span>Official 1 Frame Management</span>
                <span className="px-2 py-0.5 rounded-full bg-[#00e5ff]/20 text-[#00e5ff] text-[10px] font-bold border border-[#00e5ff]/30">
                  Authority Panel
                </span>
              </h2>
              <p className="text-xs text-slate-400">
                Grant official status frames, manage expiry dates, and enforce conflict rules
              </p>
            </div>
          </div>
          <button
            id="close-official-frames-modal"
            onClick={onClose}
            className="p-1.5 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body with 2 columns on desktop */}
        <div className="flex-1 overflow-y-auto py-4 space-y-6">
          {/* Top Row: Frame Dispatch Form & Live Preview */}
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-4">
            {/* Dispatch Form (7 cols) */}
            <form
              onSubmit={handleSendFrame}
              className="lg:col-span-7 bg-[#1a0f33] border border-[#371f5c] rounded-2xl p-4 flex flex-col gap-3.5"
            >
              <h3 className="text-xs font-bold text-[#00e5ff] uppercase tracking-wider flex items-center gap-1.5">
                <Shield className="w-4 h-4" />
                <span>Grant Official Frame</span>
              </h3>

              {/* Target User Input with Quick Select from Existing Users */}
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">
                  Target User ID:
                </label>
                <div className="flex gap-2">
                  <input
                    id="target-user-id-input"
                    type="text"
                    value={targetUserId}
                    onChange={(e) => setTargetUserId(e.target.value)}
                    placeholder="Enter User ID (e.g. 565656565666555 or usr_45623)"
                    className="flex-1 bg-[#120a24] border border-[#381f60] focus:border-[#00e5ff] rounded-xl px-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-hidden"
                  />
                  <select
                    id="quick-select-user-dropdown"
                    onChange={(e) => setTargetUserId(e.target.value)}
                    className="bg-[#120a24] border border-[#381f60] rounded-xl px-2 py-2 text-xs text-slate-300 focus:outline-hidden"
                    value=""
                  >
                    <option value="" disabled>
                      Select User...
                    </option>
                    {allUsers.map((u) => (
                      <option key={u.id} value={u.id}>
                        {u.username} ({u.id})
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Official Frame Picker (14 frames) */}
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">
                  Select Official Frame ({officialFrames.length} Frames):
                </label>
                <select
                  id="select-official-frame-dropdown"
                  value={selectedFrameId}
                  onChange={(e) => setSelectedFrameId(e.target.value)}
                  className="w-full bg-[#120a24] border border-[#381f60] focus:border-[#00e5ff] rounded-xl px-3 py-2 text-xs text-white font-semibold focus:outline-hidden"
                >
                  {officialFrames.map((f) => (
                    <option key={f.id} value={f.id}>
                      {f.iconEmoji} {f.name} ({f.badgeLabel})
                    </option>
                  ))}
                </select>
              </div>

              {/* Duration Presets */}
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">
                  Duration (Days):
                </label>
                <div className="grid grid-cols-4 gap-2 mb-2">
                  {[7, 15, 30, 90].map((days) => (
                    <button
                      key={days}
                      type="button"
                      id={`preset-days-${days}`}
                      onClick={() => {
                        setDurationDays(days);
                        setCustomDays('');
                      }}
                      className={`py-1.5 rounded-xl text-xs font-bold border transition-all ${
                        durationDays === days && !customDays
                          ? 'bg-[#00e5ff] text-black border-[#00e5ff] shadow'
                          : 'bg-[#120a24] border-[#381f60] text-slate-300 hover:bg-[#20113f]'
                      }`}
                    >
                      {days} Days
                    </button>
                  ))}
                </div>
                <input
                  type="number"
                  value={customDays}
                  onChange={(e) => setCustomDays(e.target.value)}
                  placeholder="Or enter custom days (e.g. 180)..."
                  className="w-full bg-[#120a24] border border-[#381f60] focus:border-[#00e5ff] rounded-xl px-3 py-1.5 text-xs text-white placeholder-slate-500 focus:outline-hidden"
                />
              </div>

              {/* Feedback alert */}
              {feedback && (
                <div
                  className={`p-3 rounded-xl border text-xs font-semibold flex items-start gap-2 ${
                    feedback.success
                      ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-300'
                      : 'bg-rose-500/10 border-rose-500/30 text-rose-300'
                  }`}
                >
                  {feedback.success ? (
                    <CheckCircle className="w-4 h-4 text-emerald-400 flex-shrink-0 mt-0.5" />
                  ) : (
                    <AlertTriangle className="w-4 h-4 text-rose-400 flex-shrink-0 mt-0.5" />
                  )}
                  <span>{feedback.message}</span>
                </div>
              )}

              {/* Submit Button */}
              <button
                id="grant-official-frame-submit"
                type="submit"
                className="w-full py-2.5 rounded-xl bg-gradient-to-r from-[#00e5ff] via-[#7c4dff] to-[#ff2a85] text-white font-extrabold text-xs shadow-lg hover:opacity-95 active:scale-95 transition-all flex items-center justify-center gap-2"
              >
                <Sparkles className="w-4 h-4" />
                <span>Send Official Frame to User</span>
              </button>
            </form>

            {/* Selected Frame Visual Preview Card (5 cols) */}
            <div className="lg:col-span-5 bg-[#1a0f33] border border-[#371f5c] rounded-2xl p-4 flex flex-col items-center justify-center text-center gap-3">
              <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">
                Live Frame Preview
              </span>

              {/* Animated Avatar with Preview Frame */}
              <div
                className="relative rounded-full p-1 border-[3px] shadow-xl"
                style={{
                  borderColor: selectedFrame.primaryColor,
                  boxShadow: `0 0 20px ${selectedFrame.primaryColor}88`,
                  background: `linear-gradient(135deg, ${selectedFrame.primaryColor}33, ${selectedFrame.secondaryColor}33)`,
                }}
              >
                <img
                  src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300"
                  alt="Preview"
                  className="w-20 h-20 rounded-full object-cover"
                />
                <span className="absolute -top-3 -right-2 text-2xl animate-bounce select-none">
                  {selectedFrame.iconEmoji}
                </span>
              </div>

              <div>
                <h4 className="text-sm font-extrabold text-white flex items-center justify-center gap-1.5">
                  <span>{selectedFrame.name}</span>
                  <span
                    className="px-2 py-0.5 rounded-full text-[9px] font-black text-black"
                    style={{ backgroundColor: selectedFrame.primaryColor }}
                  >
                    {selectedFrame.badgeLabel}
                  </span>
                </h4>
                <p className="text-xs text-slate-400 mt-1 max-w-xs">{selectedFrame.description}</p>
              </div>

              <div className="flex items-center gap-2 text-[11px] text-slate-400">
                <Clock className="w-3.5 h-3.5 text-amber-400" />
                <span>
                  Granting: {customDays ? `${customDays} Days` : `${durationDays} Days`}
                </span>
              </div>
            </div>
          </div>

          {/* Active Frame Assignments Table */}
          <div className="bg-[#1a0f33] border border-[#371f5c] rounded-2xl p-4">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-3">
              <div className="flex items-center gap-2">
                <UserCheck className="w-4 h-4 text-emerald-400" />
                <h3 className="text-xs font-bold text-white uppercase tracking-wider">
                  Active & Historic Frame Grants ({frameAssignments.length})
                </h3>
              </div>

              {/* Search */}
              <div className="relative">
                <Search className="w-3.5 h-3.5 absolute left-2.5 top-2.5 text-slate-400" />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search user, ID, or frame..."
                  className="bg-[#120a24] border border-[#381f60] rounded-xl pl-8 pr-3 py-1 text-xs text-white placeholder-slate-500 focus:outline-hidden w-48 sm:w-56"
                />
              </div>
            </div>

            {/* Table */}
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead>
                  <tr className="border-b border-[#2f1b52] text-slate-400 text-[11px]">
                    <th className="pb-2 font-semibold">User</th>
                    <th className="pb-2 font-semibold">Frame</th>
                    <th className="pb-2 font-semibold">Days</th>
                    <th className="pb-2 font-semibold">Expiry Date</th>
                    <th className="pb-2 font-semibold">Status</th>
                    <th className="pb-2 font-semibold text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#251545]">
                  {filteredAssignments.map((ass) => {
                    const isRevoked = ass.status === 'Revoked';
                    const isExpired = ass.status === 'Expired';
                    return (
                      <tr key={ass.id} className="hover:bg-[#20123f]/50">
                        <td className="py-2.5">
                          <div className="font-bold text-white">{ass.userName}</div>
                          <div className="text-[10px] text-slate-400">ID: {ass.userId}</div>
                        </td>
                        <td className="py-2.5">
                          <span className="font-semibold text-cyan-300">{ass.frameName}</span>
                        </td>
                        <td className="py-2.5 text-slate-300">{ass.days}d</td>
                        <td className="py-2.5">
                          <div className="text-slate-300">{ass.expiryDateFormatted}</div>
                          {!isRevoked && !isExpired && (
                            <div className="text-[10px] text-emerald-400 font-semibold">
                              {ass.remainingDays} days remaining
                            </div>
                          )}
                        </td>
                        <td className="py-2.5">
                          <span
                            className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                              ass.status === 'Active'
                                ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/40'
                                : ass.status === 'Revoked'
                                ? 'bg-rose-500/20 text-rose-300 border border-rose-500/40'
                                : 'bg-slate-700/40 text-slate-400'
                            }`}
                          >
                            {ass.status}
                          </span>
                        </td>
                        <td className="py-2.5 text-right">
                          {ass.status === 'Active' ? (
                            <button
                              id={`revoke-frame-${ass.id}`}
                              onClick={() => {
                                if (
                                  confirm(`Are you sure you want to revoke ${ass.frameName} from ${ass.userName}?`)
                                ) {
                                  revokeOfficialFrame(ass.id);
                                }
                              }}
                              className="px-2.5 py-1 rounded-lg bg-rose-500/20 hover:bg-rose-500/30 text-rose-300 border border-rose-500/40 text-[11px] font-bold transition-colors"
                            >
                              Revoke
                            </button>
                          ) : (
                            <span className="text-[11px] text-slate-500">—</span>
                          )}
                        </td>
                      </tr>
                    );
                  })}
                  {filteredAssignments.length === 0 && (
                    <tr>
                      <td colSpan={6} className="py-4 text-center text-slate-500">
                        No frame assignments found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
