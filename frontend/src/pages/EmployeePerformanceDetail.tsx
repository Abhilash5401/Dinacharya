import { useMemo } from 'react';
import { Link, useParams } from 'react-router-dom';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import {
  useEmployeePerformanceDetail,
  useEmployeePerformanceTrend,
} from '@/hooks/usePerformanceAnalytics';

function formatMonthLabel(value: string) {
  const date = new Date(value);
  return date.toLocaleDateString(undefined, { month: 'short', year: 'numeric' });
}

function toLocalIsoDate(date: Date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function currentMonthRange() {
  const now = new Date();
  const start = new Date(now.getFullYear(), now.getMonth(), 1);
  return { from: toLocalIsoDate(start), to: toLocalIsoDate(now) };
}

export default function EmployeePerformanceDetail() {
  const { userId } = useParams<{ userId: string }>();
  const defaultRange = useMemo(() => currentMonthRange(), []);

  const { data: employee, isLoading } = useEmployeePerformanceDetail(
    userId,
    defaultRange.from,
    defaultRange.to
  );
  const { data: trend } = useEmployeePerformanceTrend(userId);

  const trendData =
    trend?.points.map((point) => ({
      label: formatMonthLabel(point.periodStart),
      performanceIndex: point.performanceIndex,
      rollingIndex: point.rollingIndex ?? point.performanceIndex,
      productivityScore: point.productivityScore,
      efficiencyScore: point.efficiencyScore,
      disciplineScore: point.disciplineScore,
    })) ?? [];

  const scoreBreakdown = employee
    ? [
        { name: 'Productivity', score: employee.productivityScore, fill: '#6B8F71' },
        { name: 'Task Completion', score: employee.disciplineScore, fill: '#D4AF37' },
        { name: 'On-time Delivery', score: employee.efficiencyScore, fill: '#C57A44' },
      ]
    : [];

  if (isLoading && !employee) {
    return (
      <div className="flex items-center justify-center h-full">
        <span className="material-symbols-outlined text-[48px] text-charcoal-light animate-spin">
          refresh
        </span>
      </div>
    );
  }

  if (!employee) {
    return (
      <div className="card text-center py-12">
        <p className="text-body-lg text-charcoal-muted mb-4">Employee performance not found.</p>
        <Link to="/performance" className="btn btn-secondary">
          Back to overview
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="tms-header-light rounded-xl px-6 py-5 border border-warm-border">
        <Link
          to="/performance"
          className="inline-flex items-center gap-1 text-sm text-charcoal-muted hover:text-charcoal mb-3"
        >
          <span className="material-symbols-outlined text-[18px]">arrow_back</span>
          Back to team performance
        </Link>
        <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
          <div>
            <h1 className="text-2xl font-semibold text-charcoal">{employee.userName}</h1>
            <p className="text-body-md text-charcoal-muted mt-1">
              {employee.department || 'No department'} · {employee.periodStart} to {employee.periodEnd}
            </p>
          </div>
          <div className="flex flex-col items-start md:items-end gap-3">
            <Link to={`/attendance/${userId}`} className="btn btn-secondary">
              View attendance dashboard
            </Link>
            <div className="text-right">
              <p className="text-label-md text-charcoal-muted">Performance Index</p>
              <p className="text-3xl font-semibold text-charcoal">{employee.performanceIndex}</p>
              {employee.rollingIndex != null && (
                <p className="text-sm text-charcoal-muted">
                  3-month rolling: {employee.rollingIndex}
                </p>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="card">
          <p className="text-label-md text-charcoal-muted mb-2">Productivity</p>
          <p className="text-2xl font-semibold text-[#6B8F71]">{employee.productivityScore}</p>
        </div>
        <div className="card">
          <p className="text-label-md text-charcoal-muted mb-2">Task Completion</p>
          <p className="text-2xl font-semibold text-[#D4AF37]">{employee.disciplineScore}%</p>
          <p className="text-sm text-charcoal-muted mt-1">
            {employee.tasksCompleted}/{employee.tasksAssigned} tasks
          </p>
        </div>
        <div className="card">
          <p className="text-label-md text-charcoal-muted mb-2">On-time Delivery</p>
          <p className="text-2xl font-semibold text-terracotta">{employee.efficiencyScore}%</p>
          <p className="text-sm text-charcoal-muted mt-1">{employee.onTimeTasks} on time</p>
        </div>
        <div className="card">
          <p className="text-label-md text-charcoal-muted mb-2">Tasks completed</p>
          <p className="text-2xl font-semibold text-charcoal">{employee.tasksCompleted}</p>
          <p className="text-sm text-charcoal-muted mt-1">{employee.onTimeTasks} on time</p>
        </div>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-lg font-semibold text-charcoal mb-4">Score breakdown</h2>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={scoreBreakdown}>
              <CartesianGrid strokeDasharray="3 3" stroke="#E8E4DC" />
              <XAxis dataKey="name" tick={{ fill: '#5C5E58' }} />
              <YAxis domain={[0, 100]} tick={{ fill: '#5C5E58' }} />
              <Tooltip />
              <Bar dataKey="score" name="Score" radius={[6, 6, 0, 0]}>
                {scoreBreakdown.map((entry) => (
                  <Cell key={entry.name} fill={entry.fill} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="card">
          <h2 className="text-lg font-semibold text-charcoal mb-4">Performance trend</h2>
          <ResponsiveContainer width="100%" height={280}>
            <LineChart data={trendData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#E8E4DC" />
              <XAxis dataKey="label" tick={{ fill: '#5C5E58' }} />
              <YAxis domain={[0, 100]} tick={{ fill: '#5C5E58' }} />
              <Tooltip />
              <Legend />
              <Line
                type="monotone"
                dataKey="performanceIndex"
                stroke="#C57A44"
                name="Performance Index"
                strokeWidth={2}
              />
              <Line
                type="monotone"
                dataKey="rollingIndex"
                stroke="#5C7A99"
                name="3-month rolling"
                strokeWidth={2}
                strokeDasharray="5 5"
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="card">
        <h2 className="text-lg font-semibold text-charcoal mb-4">Activity summary</h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="rounded-lg border border-warm-border p-4">
            <p className="text-label-md text-charcoal-muted">Tasks completed</p>
            <p className="text-xl font-semibold text-charcoal mt-1">{employee.tasksCompleted}</p>
          </div>
          <div className="rounded-lg border border-warm-border p-4">
            <p className="text-label-md text-charcoal-muted">On-time completion</p>
            <p className="text-xl font-semibold text-charcoal mt-1">
              {employee.tasksCompleted > 0
                ? `${Math.round((employee.onTimeTasks / employee.tasksCompleted) * 100)}%`
                : '—'}
            </p>
          </div>
          <div className="rounded-lg border border-warm-border p-4">
            <p className="text-label-md text-charcoal-muted">Attendance</p>
            <p className="text-xl font-semibold text-charcoal mt-1">
              {employee.attendanceDays}/{employee.workingDays} days
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
