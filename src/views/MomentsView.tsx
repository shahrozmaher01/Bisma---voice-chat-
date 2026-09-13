import React, { useState } from 'react';
import {
  Camera,
  Heart,
  MessageCircle,
  Share2,
  Send,
  Sparkles,
  Plus,
  Image as ImageIcon,
} from 'lucide-react';
import { useAura } from '../context/AuraContext';
import { AvatarWithFrame } from '../components/AvatarWithFrame';

interface MomentsViewProps {
  onOpenUserProfile: (userId: string) => void;
}

const PRESET_MOMENT_IMAGES = [
  'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600',
  'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600',
  'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600',
  'https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=600',
];

export const MomentsView: React.FC<MomentsViewProps> = ({ onOpenUserProfile }) => {
  const { moments, likeMoment, addMomentComment, postMoment, currentUser } = useAura();
  const [showPostComposer, setShowPostComposer] = useState(false);
  const [newPostContent, setNewPostContent] = useState('');
  const [newPostImage, setNewPostImage] = useState(PRESET_MOMENT_IMAGES[0]);
  const [expandedComments, setExpandedComments] = useState<Record<string, boolean>>({});
  const [commentInputs, setCommentInputs] = useState<Record<string, string>>({});

  const handleCreatePost = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPostContent.trim()) return;
    postMoment(newPostContent.trim(), newPostImage);
    setNewPostContent('');
    setShowPostComposer(false);
  };

  const handleAddComment = (momentId: string) => {
    const text = commentInputs[momentId] || '';
    if (!text.trim()) return;
    addMomentComment(momentId, text.trim());
    setCommentInputs((prev) => ({ ...prev, [momentId]: '' }));
  };

  return (
    <div id="moments-view-container" className="max-w-xl mx-auto px-4 py-4 space-y-4 pb-24">
      {/* Create Moment Top Box */}
      <div className="bg-[#170e30] border border-[#341e5e] rounded-3xl p-4 shadow-md">
        <div className="flex items-center gap-3">
          <AvatarWithFrame
            avatarUrl={currentUser?.avatarUrl}
            size={40}
            equippedFrameId={currentUser?.equippedFrameId}
            vipLevel={currentUser?.vipLevel}
          />
          <button
            id="open-moment-composer-btn"
            onClick={() => setShowPostComposer(!showPostComposer)}
            className="flex-1 text-left px-4 py-2.5 rounded-full bg-[#120a24] border border-[#2f1954] text-xs text-slate-400 hover:text-white hover:border-[#7c4dff] transition-all"
          >
            Share what is on your mind or post a photo...
          </button>
        </div>

        {/* Expanded Composer */}
        {showPostComposer && (
          <form onSubmit={handleCreatePost} className="mt-3 pt-3 border-t border-[#29164d] space-y-3">
            <textarea
              id="new-moment-textarea"
              rows={3}
              value={newPostContent}
              onChange={(e) => setNewPostContent(e.target.value)}
              placeholder="What's happening in your live voice journey? Share moments, songs, and thoughts..."
              className="w-full bg-[#120a24] border border-[#381f60] focus:border-[#7c4dff] rounded-2xl p-3 text-xs text-white placeholder-slate-500 focus:outline-hidden"
              required
            />

            <div>
              <label className="text-[11px] font-semibold text-slate-400 block mb-1.5 flex items-center gap-1">
                <ImageIcon className="w-3.5 h-3.5 text-[#00e5ff]" />
                <span>Attach Photo (Select Preset):</span>
              </label>
              <div className="grid grid-cols-4 gap-2">
                {PRESET_MOMENT_IMAGES.map((img, i) => (
                  <img
                    key={i}
                    src={img}
                    alt="Preset"
                    onClick={() => setNewPostImage(img)}
                    className={`h-16 w-full object-cover rounded-xl cursor-pointer border transition-all ${
                      newPostImage === img
                        ? 'border-[#ff2a85] scale-105 shadow-[0_0_8px_rgba(255,42,133,0.5)]'
                        : 'border-[#2d1b4e] opacity-70 hover:opacity-100'
                    }`}
                  />
                ))}
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-1">
              <button
                type="button"
                onClick={() => setShowPostComposer(false)}
                className="px-3 py-1.5 rounded-xl bg-[#20113f] text-slate-300 text-xs font-semibold"
              >
                Cancel
              </button>
              <button
                id="submit-moment-btn"
                type="submit"
                className="px-5 py-1.5 rounded-xl bg-gradient-to-r from-[#ff2a85] to-[#7c4dff] text-white font-bold text-xs shadow hover:opacity-95"
              >
                Post Moment
              </button>
            </div>
          </form>
        )}
      </div>

      {/* Moments Feed */}
      <div className="space-y-4">
        {moments.map((moment) => {
          const isCommentsOpen = !!expandedComments[moment.id];
          return (
            <div
              key={moment.id}
              id={`moment-card-${moment.id}`}
              className="bg-[#170e30] border border-[#341e5e] rounded-3xl p-4 shadow-lg space-y-3"
            >
              {/* Header */}
              <div className="flex items-center justify-between">
                <div
                  className="flex items-center gap-2.5 cursor-pointer"
                  onClick={() => onOpenUserProfile(moment.userId)}
                >
                  <AvatarWithFrame
                    avatarUrl={moment.userAvatar}
                    size={38}
                    vipLevel={moment.vipLevel}
                  />
                  <div>
                    <h3 className="text-xs font-bold text-white hover:text-[#00e5ff] transition-colors">
                      {moment.userName}
                    </h3>
                    <span className="text-[10px] text-slate-400">
                      {new Date(moment.timestamp).toLocaleTimeString([], {
                        hour: '2-digit',
                        minute: '2-digit',
                      })}
                    </span>
                  </div>
                </div>
              </div>

              {/* Text content */}
              <p className="text-xs text-slate-200 leading-relaxed">{moment.content}</p>

              {/* Attached Photos */}
              {moment.mediaUrls.length > 0 && (
                <div className="rounded-2xl overflow-hidden border border-[#2b174a]">
                  <img
                    src={moment.mediaUrls[0]}
                    alt="Moment Media"
                    className="w-full max-h-72 object-cover"
                    referrerPolicy="no-referrer"
                    loading="lazy"
                  />
                </div>
              )}

              {/* Actions: Likes and Comments */}
              <div className="flex items-center justify-between pt-2 border-t border-[#29174d] text-xs">
                <div className="flex items-center gap-4">
                  <button
                    id={`like-moment-${moment.id}`}
                    onClick={() => likeMoment(moment.id)}
                    className={`flex items-center gap-1.5 font-bold transition-all ${
                      moment.isLiked
                        ? 'text-rose-400 scale-110'
                        : 'text-slate-400 hover:text-rose-400'
                    }`}
                  >
                    <Heart
                      className={`w-4 h-4 ${moment.isLiked ? 'fill-rose-500 text-rose-500' : ''}`}
                    />
                    <span>{moment.likesCount}</span>
                  </button>

                  <button
                    id={`toggle-comments-${moment.id}`}
                    onClick={() =>
                      setExpandedComments((prev) => ({
                        ...prev,
                        [moment.id]: !prev[moment.id],
                      }))
                    }
                    className="flex items-center gap-1.5 text-slate-400 hover:text-cyan-300 font-bold transition-colors"
                  >
                    <MessageCircle className="w-4 h-4" />
                    <span>{moment.comments.length}</span>
                  </button>
                </div>
              </div>

              {/* Comments Section */}
              {isCommentsOpen && (
                <div className="pt-2 border-t border-[#251545] space-y-2.5">
                  <div className="space-y-1.5 max-h-44 overflow-y-auto">
                    {moment.comments.map((comment) => (
                      <div
                        key={comment.id}
                        className="p-2 rounded-xl bg-[#120a24] text-xs flex items-start gap-2"
                      >
                        <AvatarWithFrame
                          avatarUrl={comment.userAvatar}
                          size={24}
                          onClick={() => onOpenUserProfile(comment.userId)}
                        />
                        <div className="flex-1">
                          <span className="font-bold text-white mr-1.5">{comment.userName}:</span>
                          <span className="text-slate-300">{comment.content}</span>
                        </div>
                      </div>
                    ))}
                    {moment.comments.length === 0 && (
                      <div className="text-[11px] text-slate-500 py-1">
                        No comments yet. Be the first to comment!
                      </div>
                    )}
                  </div>

                  {/* Add comment input */}
                  <div className="flex items-center gap-2">
                    <input
                      type="text"
                      value={commentInputs[moment.id] || ''}
                      onChange={(e) =>
                        setCommentInputs((prev) => ({ ...prev, [moment.id]: e.target.value }))
                      }
                      onKeyDown={(e) => e.key === 'Enter' && handleAddComment(moment.id)}
                      placeholder="Write a comment..."
                      className="flex-1 bg-[#120a24] border border-[#321d59] rounded-xl px-3 py-1.5 text-xs text-white placeholder-slate-500 focus:outline-hidden"
                    />
                    <button
                      id={`send-comment-${moment.id}`}
                      onClick={() => handleAddComment(moment.id)}
                      className="p-1.5 rounded-xl bg-[#7c4dff] text-white hover:bg-[#6c3df5]"
                    >
                      <Send className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
