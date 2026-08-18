import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { apiClient } from '@/api/client';
import { TaskAnalytics, Workload } from '@/types';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

const COLORS = ['#8A8C86', '#C57A44', '#D4AF37', '#6B8F71'];

export default function Analytics() {
  const { teamId } = useParams();

  const { data: analytics } = useQuery({
    queryKey: ['analytics', teamId],
    queryFn: async () => {
      const res = await apiClient.get<TaskAnalytics>(`/teams/${teamId}/analytics/status-counts`);
      return res.data;
    },
  });

  const { data: workload } = useQuery({
    queryKey: ['workload', teamId],
    queryFn: async () => {
      const res = await apiClient.get<Workload[]>(`/teams/${teamId}/analytics/workload`);
      return res.data;
    },
  });

  if (!analytics || !workload) {
    return (
      <div className="flex items-center justify-center h-full">
        <span className="material-symbols-outlined text-[48px] text-charcoal-light animate-spin">refresh</span>
      </div>
    );
  }

  const statusData = Object.entries(analytics.statusCounts).map(([name, value]) => ({ name, value }));

  return (
    <div className="space-y-6">
      <h1 className="text-display-lg text-charcoal">Team Analytics</h1>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="card">
          <p className="text-label-md text-charcoal-muted mb-2">Total Tasks</p>
          <p className="font-display text-4xl text-charcoal">{analytics.totalTasks}</p>
        </div>
        <div className="card">
          <p className="text-label-md text-charcoal-muted mb-2">Completed</p>
          <p className="font-display text-4xl text-[#6B8F71]">{analytics.completedTasks}</p>
        </div>
        <div className="card">
          <p className="text-label-md text-charcoal-muted mb-2">Overdue</p>
          <p className="font-display text-4xl text-error">{analytics.overdueTasks}</p>
        </div>
        <div className="card">
          <p className="text-label-md text-charcoal-muted mb-2">Completion Rate</p>
          <p className="font-display text-4xl text-terracotta">
            {analytics.totalTasks > 0 ? Math.round((analytics.completedTasks / analytics.totalTasks) * 100) : 0}%
          </p>
        </div>
      </div>

      <div className="card">
        <h2 className="font-display text-xl text-charcoal mb-4">Task Status Distribution</h2>
        <ResponsiveContainer width="100%" height={300}>
          <PieChart>
            <Pie data={statusData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={100} label>
              {statusData.map((_, index) => <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />)}
            </Pie>
            <Tooltip />
            <Legend />
          </PieChart>
        </ResponsiveContainer>
      </div>

      <div className="card">
        <h2 className="font-display text-xl text-charcoal mb-4">Team Workload</h2>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={workload}>
            <CartesianGrid strokeDasharray="3 3" stroke="#E8E4DC" />
            <XAxis dataKey="userName" tick={{ fill: '#5C5E58' }} />
            <YAxis tick={{ fill: '#5C5E58' }} />
            <Tooltip />
            <Legend />
            <Bar dataKey="assignedTasks" fill="#C57A44" name="Assigned" />
            <Bar dataKey="completedTasks" fill="#6B8F71" name="Completed" />
            <Bar dataKey="inProgressTasks" fill="#D4AF37" name="In Progress" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
