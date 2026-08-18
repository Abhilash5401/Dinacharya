import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '@/api/client';
import { Comment, Page } from '@/types';
import { toast } from 'react-toastify';
import TeamAttendanceSection from '@/components/TeamAttendanceSection';

export default function ModeratorPanel() {
  const queryClient = useQueryClient();

  const { data: flaggedPage } = useQuery({
    queryKey: ['flagged-comments'],
    queryFn: async () => {
      const res = await apiClient.get<Page<Comment>>('/moderator/flagged-comments?size=50');
      return res.data;
    },
  });

  const resolveMutation = useMutation({
    mutationFn: async (commentId: string) => {
      await apiClient.post(`/moderator/comments/${commentId}/resolve`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['flagged-comments'] });
      toast.success('Comment resolved');
    },
  });

  const flaggedComments = flaggedPage?.content || [];

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-display-lg text-charcoal">Moderator Panel</h1>
        <p className="text-body-lg text-charcoal-muted mt-1">
          Manage team attendance and review flagged content
        </p>
      </div>

      <TeamAttendanceSection />

      <div className="card">
        <h2 className="font-display text-xl text-charcoal mb-4">Flagged Comments ({flaggedComments.length})</h2>

        {flaggedComments.length === 0 ? (
          <p className="text-charcoal-muted text-center py-8">No flagged comments</p>
        ) : (
          <div className="space-y-3">
            {flaggedComments.map((comment) => (
              <div key={comment.id} className="border border-error/30 bg-error-container/30 rounded-xl p-4">
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <p className="font-medium text-charcoal">{comment.author.name}</p>
                    <p className="text-body-md text-charcoal-muted">{comment.author.email}</p>
                  </div>
                  <button
                    onClick={() => resolveMutation.mutate(comment.id)}
                    className="btn btn-primary text-sm"
                  >
                    Resolve
                  </button>
                </div>
                <p className="text-charcoal mt-2">{comment.content}</p>
                <p className="text-label-sm text-charcoal-light mt-2">
                  {new Date(comment.createdAt).toLocaleString()}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
