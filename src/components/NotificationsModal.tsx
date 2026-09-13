import React, { useEffect } from 'react';
import { X, Bell, Shield, Sparkles, Megaphone, Check } from 'lucide-react';
import { useAura } from '../context/AuraContext';

interface NotificationsModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const NotificationsModal: React.FC<NotificationsModalProps> = ({ isOpen, onClose }) => {
  const { notifications, markNotificationsAsRead } = useAura();

  useEffect(() => {
    if (isOpen) {
      markNotificationsAsRead();
    }
  }, [isOpen, markNotificationsAsRead]);

  if (!isOpen) return null;

  return (
    <div
      id="notifications-modal-backdrop"
      className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-3 sm:p-5 animate-in fade-in duration-150"
      onClick={onClose}
    >
      <div
        id="notifications-modal"
        className="w-full max-w-md bg-[#130a26] border border-[#442875] rounded-3xl p-4 sm:p-5 shadow-2xl flex flex-col max-h-[85vh] overflow-hidden relative"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between border-b border-[#2b174a] pb-3">
          <div className="flex items-center gap-2">
            <Bell className="w-5 h-5 text-[#00e5ff]" />
            <h2 className="text-base font-extrabold text-white">System Notifications</h2>
          </div>
          <button
            id="close-notifs-btn"
            onClick={onClose}
            className="p-1.5 rounded-full text-slate-400 hover:text-white hover:bg-[#251549]"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto space-y-2.5 py-3 pr-1">
          {notifications.map((n) => (
            <div
              key={n.id}
              className="p-3 rounded-2xl bg-[#1a0f33] border border-[#341d5b] flex items-start gap-3"
            >
              <div className="w-8 h-8 rounded-xl bg-[#7c4dff]/20 text-[#00e5ff] flex items-center justify-center flex-shrink-0 mt-0.5">
                {n.type === 'announcement' ? (
                  <Megaphone className="w-4 h-4 text-amber-400" />
                ) : n.type === 'official' ? (
                  <Shield className="w-4 h-4 text-[#00e5ff]" />
                ) : (
                  <Sparkles className="w-4 h-4 text-[#ff2a85]" />
                )}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between gap-1">
                  <h4 className="text-xs font-bold text-white truncate">{n.title}</h4>
                  <span className="text-[10px] text-slate-500">
                    {new Date(n.timestamp).toLocaleDateString()}
                  </span>
                </div>
                <p className="text-xs text-slate-300 mt-1 leading-relaxed">{n.message}</p>
                <div className="text-[10px] text-slate-400 mt-1 font-semibold">
                  From: {n.senderName}
                </div>
              </div>
            </div>
          ))}

          {notifications.length === 0 && (
            <div className="py-8 text-center text-xs text-slate-500">
              No notifications yet.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
