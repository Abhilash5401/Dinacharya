import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { apiClient } from '@/api/client';
import {
  AttendanceRecord,
  AttendanceFilters,
  CreateAttendanceRequest,
  UpdateAttendanceRequest,
  Page,
} from '@/types';
import { AxiosError } from 'axios';
import { toast } from 'react-toastify';

export const useAttendance = (filters: AttendanceFilters) => {
  return useQuery({
    queryKey: [
      'attendance',
      filters.date,
      filters.department,
      filters.status,
      filters.search,
      filters.page ?? 0,
      filters.size ?? 10,
    ],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (filters.date) params.append('date', filters.date);
      if (filters.department) params.append('department', filters.department);
      if (filters.status) params.append('status', filters.status);
      if (filters.search) params.append('search', filters.search);
      params.append('page', String(filters.page ?? 0));
      params.append('size', String(filters.size ?? 10));

      const response = await apiClient.get<Page<AttendanceRecord>>(
        `/moderator/attendance?${params}`
      );
      return response.data;
    },
    placeholderData: keepPreviousData,
  });
};

export const useCreateAttendance = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateAttendanceRequest) => {
      const response = await apiClient.post<AttendanceRecord>('/moderator/attendance', data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['attendance'] });
      toast.success('Attendance record added');
    },
    onError: (error: AxiosError<{ detail?: string }>) => {
      const message = error.response?.data?.detail || 'Failed to add attendance record';
      toast.error(message);
    },
  });
};

export const useUpdateAttendance = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, data }: { id: string; data: UpdateAttendanceRequest }) => {
      const response = await apiClient.put<AttendanceRecord>(`/moderator/attendance/${id}`, data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['attendance'] });
      toast.success('Attendance updated');
    },
    onError: () => {
      toast.error('Failed to update attendance');
    },
  });
};

export const useDeleteAttendance = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      await apiClient.delete(`/moderator/attendance/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['attendance'] });
      toast.success('Attendance record removed');
    },
    onError: () => {
      toast.error('Failed to delete attendance record');
    },
  });
};
