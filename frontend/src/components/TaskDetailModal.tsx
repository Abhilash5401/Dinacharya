import { useTask, useAssignTask } from '@/hooks/useTasks';
import { useComments, useCreateComment, useDeleteComment } from '@/hooks/useComments';
import { useTeam } from '@/hooks/useTeams';
import { useUsers } from '@/hooks/useUsers';
import { User } from '@/types';
import { useState } from 'react';
import AssigneePicker from '@/components/AssigneePicker';

function uniqueUsers(users: User[]) {
  const seen = new Set<string>();
  return users.filter((user) => {
    if (seen.has(user.id)) return false;
    seen.add(user.id);
    return true;
  });
}

export default function TaskDetailModal({ taskId, onClose }: { taskId: string; onClose: () => void }) {
  const { data: task } = useTask(taskId);
  const { data: comments } = useComments(taskId);
  const createComment = useCreateComment(taskId);
  const deleteComment = useDeleteComment();
  const assignTask = useAssignTask(taskId);
  const { data: team } = useTeam(task?.teamId || '');
  const { data: usersPage } = useUsers(0, 200);
  const [commentText, setCommentText] = useState('');

  const handleAddComment = async () => {
    if (!commentText.trim()) return;
    await createComment.mutateAsync({ content: commentText });
    setCommentText('');
  };

  if (!task) return null;

  const assignees = uniqueUsers([
    ...(team?.members || []),
    ...(team?.lead ? [team.lead] : []),
    ...(usersPage?.content || []),
  ]);

  return (
    <div className="modal-overlay" onClick={onClose} role="presentation">
      <div
        className="modal w-full max-w-3xl max-h-[90vh] overflow-y-auto custom-scroll"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="sticky top-0 bg-ivory border-b border-warm-border p-6 rounded-t-2xl">
          <div className="flex justify-between items-start">
            <div className="flex-1">
              <h2 className="font-display text-2xl text-charcoal">{task.title}</h2>
              <div className="flex items-center gap-4 mt-2 text-body-md text-charcoal-muted flex-wrap">
                <span className="badge badge-primary">{task.status}</span>
                <span className="badge badge-secondary">{task.priority}</span>
                <AssigneePicker
                  users={assignees}
                  value={task.assignedTo?.id}
                  disabled={assignTask.isPending}
                  onChange={(userId) => {
                    if (userId) assignTask.mutate(userId);
                  }}
                />
              </div>
            </div>
            <button onClick={onClose} className="text-charcoal-light hover:text-charcoal p-1 transition-colors">
              <span className="material-symbols-outlined">close</span>
            </button>
          </div>
        </div>

        <div className="p-6 space-y-6">
          {task.description && (
            <div>
              <h3 className="text-label-md text-charcoal-muted mb-2">Description</h3>
              <p className="text-body-md text-charcoal">{task.description}</p>
            </div>
          )}

          <div>
            <h3 className="text-label-md text-charcoal-muted mb-3">Comments ({comments?.length || 0})</h3>
            <div className="space-y-3 mb-4">
              {comments?.map((comment) => (
                <div key={comment.id} className="bg-sand rounded-xl p-4 border border-warm-border">
                  <div className="flex justify-between items-start mb-2">
                    <span className="font-medium text-body-md text-charcoal">{comment.author.name}</span>
                    <button
                      onClick={() => deleteComment.mutate(comment.id)}
                      className="text-error text-label-sm hover:underline"
                    >
                      Delete
                    </button>
                  </div>
                  <p className="text-charcoal-muted text-body-md">{comment.content}</p>
                  <span className="text-label-sm text-charcoal-light mt-2 block">{new Date(comment.createdAt).toLocaleString()}</span>
                </div>
              ))}
            </div>

            <div className="flex gap-2">
              <input
                value={commentText}
                onChange={(e) => setCommentText(e.target.value)}
                placeholder="Add a comment..."
                className="input flex-1"
                onKeyDown={(e) => e.key === 'Enter' && handleAddComment()}
              />
              <button onClick={handleAddComment} className="btn btn-primary">Post</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
