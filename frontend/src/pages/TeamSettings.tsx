import { useParams, useNavigate } from 'react-router-dom';
import { useTeam, useDeleteTeam } from '@/hooks/useTeams';
import { useAuthStore } from '@/store/authStore';
import { UserRole } from '@/types';

export default function TeamSettings() {
  const { teamId } = useParams();
  const navigate = useNavigate();
  const { data: team } = useTeam(teamId!);
  const deleteTeam = useDeleteTeam();
  const user = useAuthStore((state) => state.user);

  if (!team) {
    return (
      <div className="flex items-center justify-center h-full">
        <span className="material-symbols-outlined text-[48px] text-charcoal-light animate-spin">refresh</span>
      </div>
    );
  }

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
    <div>
      <h1 className="text-display-lg text-charcoal mb-6">Team Settings: {team.name}</h1>
      <div className="card">
        <h2 className="font-display text-xl text-charcoal mb-4">Members ({team.members.length})</h2>
        <div className="space-y-2">
          {team.members.map((member) => (
            <div key={member.id} className="flex items-center justify-between p-4 bg-sand rounded-xl border border-warm-border">
              <div className="flex items-center gap-3">
                <div className="avatar-sm bg-charcoal">
                  {member.name.charAt(0).toUpperCase()}
                </div>
                <div>
                  <p className="font-medium text-charcoal">{member.name}</p>
                  <p className="text-body-md text-charcoal-muted">{member.email}</p>
                </div>
              </div>
              {member.id === team.lead.id && (
                <span className="badge badge-secondary">Team Lead</span>
              )}
            </div>
          ))}
        </div>
      </div>

      {canDelete && (
        <div className="card mt-6 border-error/20">
          <h2 className="font-display text-xl text-charcoal mb-2">Danger Zone</h2>
          <p className="text-body-md text-charcoal-muted mb-4">
            Permanently delete this team and all of its tasks. This action cannot be undone.
          </p>
          <button
            type="button"
            onClick={handleDelete}
            disabled={deleteTeam.isPending}
            className="btn btn-secondary text-error border-error/30 hover:bg-error-container flex items-center gap-2 disabled:opacity-50"
          >
            <span className="material-symbols-outlined text-[18px]">delete</span>
            {deleteTeam.isPending ? 'Deleting...' : 'Delete Team'}
          </button>
        </div>
      )}
    </div>
  );
}
