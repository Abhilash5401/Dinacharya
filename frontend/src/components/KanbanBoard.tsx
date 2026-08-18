import { DndContext, DragEndEvent, DragOverlay, DragStartEvent } from '@dnd-kit/core';
import { useState } from 'react';
import { Task, TaskStatus } from '@/types';
import { useUpdateTaskStatus } from '@/hooks/useTasks';
import KanbanColumn from './KanbanColumn';
import TaskCard from './TaskCard';

const COLUMNS = [
  { id: TaskStatus.TODO, title: 'To Do', count: 0 },
  { id: TaskStatus.IN_PROGRESS, title: 'In Progress', count: 0 },
  { id: TaskStatus.IN_REVIEW, title: 'In Review', count: 0 },
  { id: TaskStatus.DONE, title: 'Done', count: 0 },
];

interface Props {
  tasks: Task[];
  onAddTask?: () => void;
}

export default function KanbanBoard({ tasks, onAddTask }: Props) {
  const [activeTask, setActiveTask] = useState<Task | null>(null);
  const updateStatus = useUpdateTaskStatus(activeTask?.id || '');

  const handleDragStart = (event: DragStartEvent) => {
    const task = tasks.find((t) => t.id === event.active.id);
    setActiveTask(task || null);
  };

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;
    if (!over || active.id === over.id) {
      setActiveTask(null);
      return;
    }

    const newStatus = over.id as TaskStatus;
    const task = tasks.find((t) => t.id === active.id);
    
    if (task && task.status !== newStatus) {
      updateStatus.mutate({ status: newStatus });
    }
    setActiveTask(null);
  };

  // Get task count per column
  const getColumnTasks = (status: TaskStatus) => tasks.filter((task) => task.status === status);

  return (
    <DndContext onDragStart={handleDragStart} onDragEnd={handleDragEnd}>
      <div className="flex h-full gap-4 overflow-x-auto overflow-y-hidden custom-scroll pb-4">
        {COLUMNS.map((column) => {
          const columnTasks = getColumnTasks(column.id);
          return (
            <KanbanColumn
              key={column.id}
              id={column.id}
              title={column.title}
              tasks={columnTasks}
              count={columnTasks.length}
              onAddTask={onAddTask}
            />
          );
        })}
      </div>
      <DragOverlay>
        {activeTask && <TaskCard task={activeTask} isDragging />}
      </DragOverlay>
    </DndContext>
  );
}
