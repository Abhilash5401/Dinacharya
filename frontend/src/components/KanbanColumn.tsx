import { useDroppable } from '@dnd-kit/core';
import { Task, TaskStatus } from '@/types';
import TaskCard from './TaskCard';

interface Props {
  id: TaskStatus;
  title: string;
  tasks: Task[];
  count?: number;
  onAddTask?: () => void;
}

export default function KanbanColumn({ id, title, tasks, count, onAddTask }: Props) {
  const { setNodeRef, isOver } = useDroppable({ id });

  const getBadgeColor = (status: TaskStatus) => {
    switch (status) {
      case TaskStatus.TODO:
        return 'badge-neutral';
      case TaskStatus.IN_PROGRESS:
        return 'badge-primary';
      case TaskStatus.IN_REVIEW:
        return 'badge-secondary';
      case TaskStatus.DONE:
        return 'badge-success';
      default:
        return 'badge-neutral';
    }
  };

  return (
    <section
      ref={setNodeRef}
      className={`list-container h-full flex-shrink-0 transition-all ${
        isOver ? 'ring-2 ring-terracotta/40 bg-sand' : ''
      }`}
    >
      <div className="list-header flex justify-between items-center">
        <div className="flex items-center gap-2">
          <h3 className="text-headline-sm text-charcoal">{title}</h3>
          <span className={`badge ${getBadgeColor(id)} px-2 py-1 rounded-full`}>
            {count || tasks.length}
          </span>
        </div>
        <button className="text-charcoal-muted hover:text-terracotta transition-colors">
          <span className="material-symbols-outlined text-[20px]">more_horiz</span>
        </button>
      </div>
      
      <div className="p-2 flex-1 overflow-y-auto custom-scroll flex flex-col gap-2">
        {tasks.map((task) => (
          <TaskCard key={task.id} task={task} />
        ))}
        {tasks.length === 0 && (
          <p className="text-center text-charcoal-light text-body-md py-8">No tasks</p>
        )}
      </div>

      <div className="p-2 border-t border-warm-border bg-sand/80 rounded-b-xl">
        <button
          type="button"
          onClick={onAddTask}
          className="w-full py-2 text-charcoal-muted hover:bg-ivory hover:text-charcoal rounded-lg text-label-md transition-colors flex items-center justify-center gap-1"
        >
          <span className="material-symbols-outlined text-[18px]">add</span>
          Add Task
        </button>
      </div>
    </section>
  );
}
