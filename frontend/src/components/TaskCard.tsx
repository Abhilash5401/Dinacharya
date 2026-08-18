import { useDraggable } from '@dnd-kit/core';
import { Task, TaskPriority, TaskStatus } from '@/types';
import { useState } from 'react';
import TaskDetailModal from './TaskDetailModal';

const PRIORITY_COLORS = {
  [TaskPriority.LOW]: 'badge-neutral',
  [TaskPriority.MEDIUM]: 'badge-primary',
  [TaskPriority.HIGH]: 'badge-warning',
  [TaskPriority.URGENT]: 'badge-danger',
};

const STATUS_COLORS = {
  [TaskStatus.TODO]: '#8A8C86',
  [TaskStatus.IN_PROGRESS]: '#C57A44',
  [TaskStatus.IN_REVIEW]: '#D4AF37',
  [TaskStatus.DONE]: '#6B8F71',
};

interface Props {
  task: Task;
  isDragging?: boolean;
}

export default function TaskCard({ task, isDragging }: Props) {
  const { attributes, listeners, setNodeRef, transform } = useDraggable({ id: task.id });
  const [showDetail, setShowDetail] = useState(false);

  const style = transform
    ? { transform: `translate3d(${transform.x}px, ${transform.y}px, 0)` }
    : undefined;

  return (
    <>
      <article
        ref={setNodeRef}
        style={style}
        {...listeners}
        {...attributes}
        onClick={() => setShowDetail(true)}
        className={`card cursor-pointer group relative ${
          isDragging ? 'opacity-50 shadow-elevated' : ''
        }`}
      >
        <div 
          className="absolute top-0 left-0 w-1 h-full rounded-l-xl" 
          style={{ backgroundColor: STATUS_COLORS[task.status] }}
        />

        <div className="flex justify-between items-start mb-2">
          <span className={`badge ${PRIORITY_COLORS[task.priority]} text-label-sm px-2 py-0.5 rounded-sm`}>
            {task.priority === TaskPriority.LOW && 'Low'}
            {task.priority === TaskPriority.MEDIUM && 'Medium'}
            {task.priority === TaskPriority.HIGH && 'High'}
            {task.priority === TaskPriority.URGENT && 'Urgent'}
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

      {showDetail && (
        <TaskDetailModal taskId={task.id} onClose={() => setShowDetail(false)} />
      )}
    </>
  );
}
