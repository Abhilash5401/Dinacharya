import { Link } from 'react-router-dom';
import { useTeams, useDeleteTeam } from '@/hooks/useTeams';
import { useTasks } from '@/hooks/useTasks';
import { useState } from 'react';
import CreateTeamModal from '@/components/CreateTeamModal';
import CreateTaskModal from '@/components/CreateTaskModal';
import { TaskStatus, Team, UserRole } from '@/types';
import { useAuthStore } from '@/store/authStore';

function canDeleteTeam(team: Team, userId?: string, role?: string | null) {
  return userId === team.lead.id || role === UserRole.ADMIN;
}

export default function Dashboard() {
  const { data: teamsPage, isLoading } = useTeams();
  const [showCreateModal, setShowCreateModal] = useState(false);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-center">
          <span className="material-symbols-outlined text-[48px] text-charcoal-light animate-spin">refresh</span>
          <p className="text-charcoal-muted mt-2">Loading workspace...</p>
        </div>
      </div>
    );
  }

  const teams = teamsPage?.content || [];

  return (
    <div className="flex flex-col h-full">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-display-lg text-charcoal">Global Workspace</h1>
          <p className="text-body-lg text-charcoal-muted mt-1">Task Analytics</p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          className="btn btn-primary flex items-center gap-2"
        >
          <span className="material-symbols-outlined text-[18px]">add</span>
          New Report
        </button>
      </div>

      {teams.length === 0 ? (
        <div className="text-center py-16 card">
          <span className="material-symbols-outlined text-[64px] text-charcoal-light mb-4 block">groups</span>
          <p className="text-charcoal-muted text-body-lg mb-6">No teams yet. Create your first team to get started!</p>
          <button 
            onClick={() => setShowCreateModal(true)} 
            className="btn btn-primary inline-flex items-center gap-2"
          >
            <span className="material-symbols-outlined text-[18px]">add</span>
            Create Team
          </button>
        </div>
      ) : (
        <div className="flex-1 overflow-x-auto overflow-y-hidden custom-scroll pb-4">
          <div className="flex h-full gap-4">
            {teams.map((team) => (
              <TeamColumn key={team.id} team={team} />
            ))}
          </div>
        </div>
      )}

      {showCreateModal && (
        <CreateTeamModal onClose={() => setShowCreateModal(false)} />
      )}
    </div>
  );
}

// Team Column Component
function TeamColumn({ team }: { team: Team }) {
  const { data: tasksPage } = useTasks({ teamId: team.id, size: 50 });
  const deleteTeam = useDeleteTeam();
  const user = useAuthStore((state) => state.user);
  const [showCreateTaskModal, setShowCreateTaskModal] = useState(false);
  const tasks = tasksPage?.content || [];

  const tasksByStatus = {
    TODO: tasks.filter(t => t.status === TaskStatus.TODO).length,
    IN_PROGRESS: tasks.filter(t => t.status === TaskStatus.IN_PROGRESS).length,
    IN_REVIEW: tasks.filter(t => t.status === TaskStatus.IN_REVIEW).length,
    DONE: tasks.filter(t => t.status === TaskStatus.DONE).length,
  };

  const totalTasks = tasks.length;

  const getStatusBadge = () => {
    if (tasksByStatus.DONE === totalTasks && totalTasks > 0) {
      return 'badge-success';
    } else if (tasksByStatus.IN_PROGRESS > 0) {
      return 'badge-primary';
    }
    return 'badge-neutral';
  };

  const getStatusText = () => {
    if (tasksByStatus.DONE === totalTasks && totalTasks > 0) return 'Completed';
    if (tasksByStatus.IN_PROGRESS > 0) return 'In Progress';
    return 'Planning';
  };

  const STATUS_COLORS: Record<string, string> = {
    [TaskStatus.TODO]: '#8A8C86',
    [TaskStatus.IN_PROGRESS]: '#C57A44',
    [TaskStatus.IN_REVIEW]: '#D4AF37',
    [TaskStatus.DONE]: '#6B8F71',
  };

  const handleDelete = () => {
    if (!confirm(`Delete "${team.name}"? This will permanently remove the team and all its tasks.`)) {
      return;
    }
    deleteTeam.mutate(team.id);
  };

  return (
    <section className="list-container h-full flex-shrink-0">
      <div className="list-header flex justify-between items-center">
        <div className="flex items-center gap-2">
          <h3 className="text-headline-sm text-charcoal">{team.name}</h3>
          <span className={`badge ${getStatusBadge()} px-2 py-1 rounded-full`}>
            {totalTasks}
          </span>
        </div>
        <div className="flex items-center gap-1">
          {canDeleteTeam(team, user?.id, user?.role) && (
            <button
              type="button"
              onClick={handleDelete}
              disabled={deleteTeam.isPending}
              className="text-charcoal-muted hover:text-error transition-colors p-1 disabled:opacity-50"
              title="Delete team"
            >
              <span className="material-symbols-outlined text-[20px]">delete</span>
            </button>
          )}
          <Link to={`/teams/${team.id}`}>
            <button type="button" className="text-charcoal-muted hover:text-terracotta transition-colors p-1">
              <span className="material-symbols-outlined text-[20px]">more_horiz</span>
            </button>
          </Link>
        </div>
      </div>
      
      <div className="p-2 flex-1 overflow-y-auto custom-scroll flex flex-col gap-2">
        {tasks.slice(0, 3).map((task) => (
          <Link key={task.id} to={`/teams/${team.id}`}>
            <article className="card cursor-pointer group relative">
              <div 
                className="absolute top-0 left-0 w-1 h-full rounded-l-xl" 
                style={{ backgroundColor: STATUS_COLORS[task.status] }}
              />

              <div className="flex justify-between items-start mb-2">
                <span className={`badge ${getStatusBadge()} text-label-sm px-2 py-0.5 rounded-sm`}>
                  {getStatusText()}
                </span>
                {task.assignedTo ? (
                  <div className="flex items-center gap-2" title={`Assigned to ${task.assignedTo.name}`}>
                    <div className="avatar-sm bg-terracotta border-terracotta-dark">
                      {task.assignedTo.name.charAt(0).toUpperCase()}
                    </div>
                    <span className="text-label-sm text-charcoal-muted max-w-[90px] truncate">
                      {task.assignedTo.name}
                    </span>
                  </div>
                ) : (
                  <span className="text-label-sm text-charcoal-light">Unassigned</span>
                )}
              </div>

              <h4 className="text-title-lg text-charcoal mb-1">{task.title}</h4>
              
              {task.description && (
                <p className="text-body-md text-charcoal-muted mb-3 line-clamp-2">{task.description}</p>
              )}
              
              <div className="flex justify-between items-center pt-2 border-t border-warm-border">
                <div className="flex items-center text-charcoal-muted gap-3">
                  {task.commentCount! > 0 && (
                    <div className="flex items-center gap-1">
                      <span className="material-symbols-outlined text-[16px]">chat_bubble_outline</span>
                      <span className="text-label-sm">{task.commentCount}</span>
                    </div>
                  )}
                  {task.attachmentCount! > 0 && (
                    <div className="flex items-center gap-1">
                      <span className="material-symbols-outlined text-[16px]">attachment</span>
                      <span className="text-label-sm">{task.attachmentCount}</span>
                    </div>
                  )}
                </div>
                <button className="text-terracotta text-label-md hover:text-terracotta-dark transition-colors">
                  Details
                </button>
              </div>
            </article>
          </Link>
        ))}

        {tasks.length === 0 && (
          <p className="text-center text-charcoal-light text-body-md py-8">No tasks</p>
        )}

        {tasks.length > 3 && (
          <Link to={`/teams/${team.id}`} className="text-center">
            <button className="text-terracotta text-label-md hover:text-terracotta-dark transition-colors">
              +{tasks.length - 3} more tasks
            </button>
          </Link>
        )}
      </div>

      <div className="p-2 border-t border-warm-border bg-sand/80 rounded-b-xl">
        <button
          type="button"
          onClick={() => setShowCreateTaskModal(true)}
          className="w-full py-2 text-charcoal-muted hover:bg-ivory hover:text-charcoal rounded-lg text-label-md transition-colors flex items-center justify-center gap-1"
        >
          <span className="material-symbols-outlined text-[18px]">add</span>
          Add Task
        </button>
      </div>

      {showCreateTaskModal && (
        <CreateTaskModal teamId={team.id} onClose={() => setShowCreateTaskModal(false)} />
      )}
    </section>
  );
}
