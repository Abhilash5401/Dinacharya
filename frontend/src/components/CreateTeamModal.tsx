import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { toast } from 'react-toastify';
import { useCreateTeam } from '@/hooks/useTeams';

const teamSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters'),
  description: z.string().optional(),
});

type TeamForm = z.infer<typeof teamSchema>;

interface Props {
  onClose: () => void;
}

export default function CreateTeamModal({ onClose }: Props) {
  const createTeam = useCreateTeam();
  const { register, handleSubmit, formState: { errors } } = useForm<TeamForm>({
    resolver: zodResolver(teamSchema),
  });

  const onSubmit = (data: TeamForm) => {
    createTeam.mutate(data, {
      onSuccess: () => {
        onClose();
      },
      onError: (error) => {
        console.error('Error creating team:', error);
        toast.error('Could not create team. Please try again.');
      }
    });
  };

  return (
    <div className="modal-overlay">
      <div className="modal p-6 w-full max-w-md">
        <h2 className="font-display text-2xl text-charcoal mb-6">Create New Team</h2>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-label-md text-charcoal-muted mb-2">Team Name</label>
            <input {...register('name')} className="input" />
            {errors.name && <p className="mt-2 text-label-sm text-error">{errors.name.message}</p>}
          </div>
          <div>
            <label className="block text-label-md text-charcoal-muted mb-2">Description</label>
            <textarea {...register('description')} className="input rounded-lg" rows={3} />
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" onClick={onClose} className="btn btn-secondary">Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={createTeam.isPending}>
              {createTeam.isPending ? 'Creating...' : 'Create'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
