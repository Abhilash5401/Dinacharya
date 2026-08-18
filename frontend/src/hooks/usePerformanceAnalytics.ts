import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '@/api/client';
import { EmployeePerformanceOverview, EmployeePerformanceTrend, EmployeePerformance } from '@/types';
import { toast } from 'react-toastify';

export function useEmployeePerformance(from?: string, to?: string, department?: string) {
  return useQuery({
    queryKey: ['employee-performance', from, to, department],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (from) params.set('from', from);
      if (to) params.set('to', to);
      if (department) params.set('department', department);
      const query = params.toString();
      const response = await apiClient.get<EmployeePerformanceOverview>(
        `/analytics/performance/employees${query ? `?${query}` : ''}`
      );
      return response.data;
    },
    staleTime: 60 * 1000,
  });
}

export function useEmployeePerformanceTrend(userId?: string, months = 6) {
  return useQuery({
    queryKey: ['employee-performance-trend', userId, months],
    queryFn: async () => {
      const response = await apiClient.get<EmployeePerformanceTrend>(
        `/analytics/performance/employees/${userId}/trend?months=${months}`
      );
      return response.data;
    },
    enabled: Boolean(userId),
    staleTime: 60 * 1000,
  });
}

export function useEmployeePerformanceDetail(userId?: string, from?: string, to?: string) {
  return useQuery({
    queryKey: ['employee-performance-detail', userId, from, to],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (from) params.set('from', from);
      if (to) params.set('to', to);
      const query = params.toString();
      const response = await apiClient.get<EmployeePerformance>(
        `/analytics/performance/employees/${userId}${query ? `?${query}` : ''}`
      );
      return response.data;
    },
    enabled: Boolean(userId),
    staleTime: 60 * 1000,
  });
}

export function useRecomputePerformance() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      await apiClient.post('/analytics/performance/compute');
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['employee-performance'] });
      queryClient.invalidateQueries({ queryKey: ['employee-performance-detail'] });
      queryClient.invalidateQueries({ queryKey: ['employee-performance-trend'] });
      toast.success('Performance scores updated');
    },
    onError: () => {
      toast.error('Failed to recompute performance scores');
    },
  });
}
