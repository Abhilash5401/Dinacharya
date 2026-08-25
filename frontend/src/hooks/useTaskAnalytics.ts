import { useQuery } from '@tanstack/react-query';
import { apiClient } from '@/api/client';
import { TaskCompletionAnalytics } from '@/types';

export function useTaskCompletionAnalytics(from?: string, to?: string, teamId?: string) {
  return useQuery({
    queryKey: ['task-completion-analytics', from, to, teamId],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (from) params.set('from', from);
      if (to) params.set('to', to);
      if (teamId) params.set('teamId', teamId);
      const query = params.toString();
      const response = await apiClient.get<TaskCompletionAnalytics>(
        `/analytics/tasks${query ? `?${query}` : ''}`
      );
      return response.data;
    },
    staleTime: 60 * 1000,
  });
}
