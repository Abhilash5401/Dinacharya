import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { useTasks } from '@/hooks/useTasks';
import { useTeam, useDeleteTeam } from '@/hooks/useTeams';
import { useWebSocket } from '@/hooks/useWebSocket';
import { useState } from 'react';
import KanbanBoard from '@/components/KanbanBoard';
import CreateTaskModal from '@/components/CreateTaskModal';
import { useAuthStore } from '@/store/authStore';
import { UserRole } from '@/types';

export default function TeamBoard() {
  const { teamId } = useParams<{ teamId: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const { data: team } = useTeam(teamId!);
  const { data: tasksPage, isLoading } = useTasks({ teamId, size: 100 });
  const [showCreateModal, setShowCreateModal] = useState(
    (location.state as { openCreateTask?: boolean } | null)?.openCreateTask ?? false
  );
  const deleteTeam = useDeleteTeam();
  const user = useAuthStore((state) => state.user);
  
  useWebSocket(teamId);

  if (isLoading || !team) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-center">
          <span className="material-symbols-outlined text-[48px] text-charcoal-light animate-spin">refresh</span>
          <p className="text-charcoal-muted mt-2">Loading board...</p>
        </div>
      </div>
    );
  }

  const tasks = tasksPage?.content || [];
  const canDelete = user?.id === team.lead.id || user?.role === UserRole.ADMIN;

  const handleDelete = () => {
    if (!confirm(`Delete "${team.name}"? This will permanently remove the team and all its tasks.`)) {
      return;
    }
    deleteTeam.mutate(team.id, {
      onSuccess: () => navigate('/teams'),
    });
  };

  return (
    <div className="flex flex-col h-full">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-display-lg text-charcoal">{team.name}</h1>
          <p className="text-body-lg text-charcoal-muted mt-1">{team.description}</p>
        </div>
        <div className="flex items-center gap-2">
          {canDelete && (
            <button
              type="button"
              onClick={handleDelete}
              disabled={deleteTeam.isPending}
              className="btn btn-secondary text-error border-error/30 hover:bg-error-container flex items-center gap-2 disabled:opacity-50"
            >
              <span className="material-symbols-outlined text-[18px]">delete</span>
              {deleteTeam.isPending ? 'Deleting...' : 'Delete Team'}
            </button>
          )}
          <button 
            onClick={() => setShowCreateModal(true)} 
            className="btn btn-primary flex items-center gap-2"
          >
            <span className="material-symbols-outlined text-[18px]">add</span>
            New Task
          </button>
        </div>
      </div>

      <div className="flex-1 overflow-hidden">
        <KanbanBoard tasks={tasks} onAddTask={() => setShowCreateModal(true)} />
      </div>

      {showCreateModal && (
        <CreateTaskModal teamId={teamId!} onClose={() => setShowCreateModal(false)} />
      )}
    </div>
  );
}
