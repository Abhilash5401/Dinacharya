import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '@/api/client';
import { Team, CreateTeamRequest, Page } from '@/types';
import { toast } from 'react-toastify';

export const useTeams = (page = 0, size = 20) => {
  return useQuery({
    queryKey: ['teams', page, size],
    queryFn: async () => {
      const response = await apiClient.get<Page<Team>>(`/teams?page=${page}&size=${size}`);
      return response.data;
    },
  });
};

export const useTeam = (id: string) => {
  return useQuery({
    queryKey: ['teams', id],
    queryFn: async () => {
      const response = await apiClient.get<Team>(`/teams/${id}`);
      return response.data;
    },
    enabled: !!id,
  });
};

export const useCreateTeam = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateTeamRequest) => {
      const response = await apiClient.post<Team>('/teams', data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teams'] });
      toast.success('Team created');
    },
  });
};

export const useAddTeamMember = (teamId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (userId: string) => {
      const response = await apiClient.post<Team>(`/teams/${teamId}/members`, { userId });
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teams', teamId] });
      toast.success('Member added');
    },
  });
};

export const useRemoveTeamMember = (teamId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (userId: string) => {
      await apiClient.delete(`/teams/${teamId}/members/${userId}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teams', teamId] });
      toast.success('Member removed');
    },
  });
};

export const useDeleteTeam = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (teamId: string) => {
      await apiClient.delete(`/teams/${teamId}`);
    },
    onSuccess: (_, teamId) => {
      queryClient.invalidateQueries({ queryKey: ['teams'] });
      queryClient.removeQueries({ queryKey: ['teams', teamId] });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
      toast.success('Team deleted');
    },
    onError: () => {
      toast.error('Failed to delete team');
    },
  });
};
