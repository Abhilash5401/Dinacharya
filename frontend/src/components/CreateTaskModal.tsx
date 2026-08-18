import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { toast } from 'react-toastify';
import { useCreateTask } from '@/hooks/useTasks';
import { useTeam } from '@/hooks/useTeams';
import { useUsers } from '@/hooks/useUsers';
import { TaskPriority, User } from '@/types';
import AssigneePicker from '@/components/AssigneePicker';

const taskSchema = z.object({
  title: z.string().min(2, 'Title required'),
  description: z.string().optional(),
  priority: z.nativeEnum(TaskPriority),
  deadline: z.string().optional(),
  assignedToId: z.string().optional(),
});

type TaskForm = z.infer<typeof taskSchema>;

function uniqueUsers(users: User[]) {
  const seen = new Set<string>();
  return users.filter((user) => {
    if (seen.has(user.id)) return false;
    seen.add(user.id);
    return true;
  });
}

export default function CreateTaskModal({ teamId, onClose }: { teamId: string; onClose: () => void }) {
  const createTask = useCreateTask();
  const { data: team } = useTeam(teamId);
  const { data: usersPage } = useUsers(0, 200);
  const { register, handleSubmit, setValue, watch, formState: { errors } } = useForm<TaskForm>({
    resolver: zodResolver(taskSchema),
    defaultValues: { priority: TaskPriority.MEDIUM, assignedToId: '' },
  });
  const assignedToId = watch('assignedToId');

  const assignees = uniqueUsers([
    ...(team?.members || []),
    ...(team?.lead ? [team.lead] : []),
    ...(usersPage?.content || []),
  ]);

  const onSubmit = async (data: TaskForm) => {
    try {
      await createTask.mutateAsync({
        ...data,
        teamId,
        assignedToId: data.assignedToId || undefined,
        deadline: data.deadline || undefined,
      });
      onClose();
    } catch {
      toast.error('Could not create task. Please try again.');
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal p-6 w-full max-w-lg">
        <h2 className="font-display text-2xl text-charcoal mb-6">Create Task</h2>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-label-md text-charcoal-muted mb-2">Title</label>
            <input {...register('title')} className="input" />
            {errors.title && <p className="mt-2 text-label-sm text-error">{errors.title.message}</p>}
          </div>
          <div>
            <label className="block text-label-md text-charcoal-muted mb-2">Description</label>
            <textarea {...register('description')} className="input rounded-lg" rows={3} />
          </div>
          <div>
            <label className="block text-label-md text-charcoal-muted mb-2">Assign to</label>
            <AssigneePicker
              users={assignees}
              value={assignedToId}
              onChange={(userId) => setValue('assignedToId', userId)}
            />
          </div>
          <div>
            <label className="block text-label-md text-charcoal-muted mb-2">Priority</label>
            <select {...register('priority')} className="input">
              {Object.values(TaskPriority).map((p) => <option key={p} value={p}>{p}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-label-md text-charcoal-muted mb-2">Deadline</label>
            <input {...register('deadline')} type="datetime-local" className="input" />
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" onClick={onClose} className="btn btn-secondary">Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={createTask.isPending}>
              {createTask.isPending ? 'Creating...' : 'Create'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
