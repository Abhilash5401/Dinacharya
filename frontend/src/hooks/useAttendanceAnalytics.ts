import { useQuery } from '@tanstack/react-query';
import { apiClient } from '@/api/client';
import { EmployeeAttendanceDashboard } from '@/types';

export function useEmployeeAttendanceDashboard(userId?: string, from?: string, to?: string) {
  return useQuery({
    queryKey: ['employee-attendance-dashboard', userId, from, to],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (from) params.set('from', from);
      if (to) params.set('to', to);
      const query = params.toString();
      const response = await apiClient.get<EmployeeAttendanceDashboard>(
        `/analytics/attendance/employees/${userId}${query ? `?${query}` : ''}`
      );
      return response.data;
    },
    enabled: Boolean(userId),
    staleTime: 60 * 1000,
  });
}
