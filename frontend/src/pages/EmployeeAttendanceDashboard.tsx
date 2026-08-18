import { useMemo, useState } from 'react';
import { Link, Navigate, useParams } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { isAdminUser } from '@/auth/roles';
import { useEmployeeAttendanceDashboard } from '@/hooks/useAttendanceAnalytics';

function currentYearRange() {
  const now = new Date();
  const start = new Date(now.getFullYear(), 0, 1);
  const toIso = (date: Date) => date.toISOString().slice(0, 10);
  return { from: toIso(start), to: toIso(now) };
}

function statusIcon(status: string) {
  switch (status) {
    case 'PRESENT':
      return { icon: 'check_circle', className: 'text-[#16a34a]' };
    case 'LATE':
      return { icon: 'schedule', className: 'text-[#d97706]' };
    default:
      return { icon: 'cancel', className: 'text-[#dc2626]' };
  }
}

export default function EmployeeAttendanceDashboardPage() {
  const { userId: routeUserId } = useParams<{ userId: string }>();
  const user = useAuthStore((state) => state.user);
  const role = useAuthStore((state) => state.getUserRole());
  const admin = isAdminUser(user, role);
  const userId = routeUserId || user?.id;
  const defaultRange = useMemo(() => currentYearRange(), []);
  const [expandedMonths, setExpandedMonths] = useState<Record<string, boolean>>({});

  const { data: dashboard, isLoading } = useEmployeeAttendanceDashboard(
    userId,
    defaultRange.from,
    defaultRange.to
  );

  if (!userId) {
    return <Navigate to="/login" replace />;
  }

  if (!admin && userId !== user?.id) {
    return <Navigate to={`/attendance/${user?.id}`} replace />;
  }

  const toggleMonth = (monthKey: string) => {
    setExpandedMonths((prev) => ({ ...prev, [monthKey]: !prev[monthKey] }));
  };

  const setAllExpanded = (expanded: boolean) => {
    if (!dashboard) return;
    const next: Record<string, boolean> = {};
    dashboard.months.forEach((month) => {
      next[month.monthKey] = expanded;
    });
    setExpandedMonths(next);
  };

  if (isLoading && !dashboard) {
    return (
      <div className="flex items-center justify-center h-full">
        <span className="material-symbols-outlined text-[48px] text-charcoal-light animate-spin">
          refresh
        </span>
      </div>
    );
  }

  if (!dashboard) {
    return (
      <div className="card text-center py-12">
        <p className="text-body-lg text-charcoal-muted mb-4">Attendance dashboard unavailable.</p>
        {admin ? (
          <Link to="/people" className="btn btn-secondary">
            Back to people
          </Link>
        ) : (
          <Link to="/work" className="btn btn-secondary">
            Back to daily tasks
          </Link>
        )}
      </div>
    );
  }

  const backLink = admin
    ? { to: '/people', label: 'Back to people' }
  : { to: '/work', label: 'Back to daily tasks' };

  return (
    <div className="space-y-6">
      <div className="tms-header-light rounded-xl px-6 py-5 border border-warm-border">
        <Link
          to={backLink.to}
          className="inline-flex items-center gap-1 text-sm text-charcoal-muted hover:text-charcoal mb-3"
        >
          <span className="material-symbols-outlined text-[18px]">arrow_back</span>
          {backLink.label}
        </Link>
        <div className="flex items-center gap-3">
          <span className="material-symbols-outlined text-[28px] text-terracotta">calendar_month</span>
          <div>
            <h1 className="text-2xl font-semibold text-charcoal">
              {admin && userId !== user?.id ? `${dashboard.userName}'s Attendance` : 'My Attendance'}
            </h1>
            <p className="text-body-md text-charcoal-muted mt-1">
              {dashboard.department || 'No department'} · {dashboard.periodStart} to {dashboard.periodEnd}
            </p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="card">
          <div className="flex items-center gap-2 text-label-md text-charcoal-muted mb-2">
            <span className="material-symbols-outlined text-[18px] text-terracotta">trending_up</span>
            Overall
          </div>
          <p className="text-3xl font-semibold text-terracotta">{dashboard.overallPercent}%</p>
          <div className="mt-3 h-2 rounded-full bg-sand overflow-hidden">
            <div
              className="h-full rounded-full bg-terracotta transition-all"
              style={{ width: `${Math.min(dashboard.overallPercent, 100)}%` }}
            />
          </div>
          <p className="text-sm text-charcoal-muted mt-2">{dashboard.totalSessions} sessions total</p>
        </div>

        <div className="card">
          <div className="flex items-center gap-2 text-label-md text-charcoal-muted mb-2">
            <span className="material-symbols-outlined text-[18px] text-[#16a34a]">check_circle</span>
            Present
          </div>
          <p className="text-3xl font-semibold text-[#16a34a]">{dashboard.presentCount}</p>
          <p className="text-sm text-charcoal-muted mt-2">sessions attended</p>
        </div>

        <div className="card">
          <div className="flex items-center gap-2 text-label-md text-charcoal-muted mb-2">
            <span className="material-symbols-outlined text-[18px] text-[#dc2626]">cancel</span>
            Absent
          </div>
          <p className="text-3xl font-semibold text-[#dc2626]">{dashboard.absentCount}</p>
          <p className="text-sm text-charcoal-muted mt-2">sessions missed</p>
        </div>

        <div className="card">
          <div className="flex items-center gap-2 text-label-md text-charcoal-muted mb-2">
            <span className="material-symbols-outlined text-[18px] text-[#d97706]">schedule</span>
            Late
          </div>
          <p className="text-3xl font-semibold text-[#d97706]">{dashboard.lateCount}</p>
          <p className="text-sm text-charcoal-muted mt-2">late check-ins</p>
        </div>
      </div>

      <div className="card">
        <div className="flex items-center justify-between gap-3 mb-4">
          <h2 className="text-lg font-semibold text-charcoal">Month-by-Month</h2>
          <div className="flex items-center gap-3 text-sm">
            <button
              type="button"
              className="text-terracotta hover:underline"
              onClick={() => setAllExpanded(true)}
            >
              Expand all
            </button>
            <button
              type="button"
              className="text-charcoal-muted hover:underline"
              onClick={() => setAllExpanded(false)}
            >
              Collapse all
            </button>
          </div>
        </div>

        {dashboard.months.length === 0 ? (
          <p className="text-body-md text-charcoal-muted py-8 text-center">
            No attendance sessions in this period yet.
          </p>
        ) : (
          <div className="space-y-3">
            {dashboard.months.map((month) => {
              const expanded = expandedMonths[month.monthKey] ?? false;
              const absentRatio =
                month.totalSessions > 0 ? (month.absentCount / month.totalSessions) * 100 : 0;

              return (
                <div key={month.monthKey} className="rounded-xl border border-warm-border overflow-hidden">
                  <button
                    type="button"
                    className="w-full flex items-center gap-4 px-4 py-3 text-left hover:bg-sand/40 transition-colors"
                    onClick={() => toggleMonth(month.monthKey)}
                  >
                    <span className="material-symbols-outlined text-charcoal-muted">
                      {expanded ? 'expand_more' : 'chevron_right'}
                    </span>
                    <div className="min-w-[140px] font-medium text-charcoal">{month.monthLabel}</div>
                    <div className="hidden sm:flex flex-1 items-center">
                      <div className="h-1.5 flex-1 rounded-full bg-sand overflow-hidden">
                        <div
                          className="h-full rounded-full bg-[#dc2626]/70"
                          style={{ width: `${absentRatio}%` }}
                        />
                      </div>
                    </div>
                    <div className="ml-auto flex items-center gap-3 text-sm shrink-0">
                      <span className="font-semibold text-charcoal">{month.attendancePercent}%</span>
                      <span className="text-[#16a34a] font-medium">{month.presentCount}P</span>
                      <span className="text-[#dc2626] font-medium">{month.absentCount}A</span>
                      <span className="text-charcoal-muted">/{month.totalSessions}</span>
                    </div>
                  </button>

                  {expanded && (
                    <div className="border-t border-warm-border bg-sand/20">
                      {month.days.map((day) => {
                        const status = statusIcon(day.status);
                        return (
                          <div
                            key={day.workDate}
                            className="flex items-center justify-between gap-4 px-4 py-3 border-b border-warm-border/60 last:border-b-0"
                          >
                            <span className="text-sm text-charcoal-muted min-w-[110px]">{day.dayLabel}</span>
                            <div className={`flex items-center gap-2 text-sm font-medium ${status.className}`}>
                              <span className="material-symbols-outlined text-[18px]">{status.icon}</span>
                              {day.note}
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>

      {admin && userId !== user?.id && (
        <div className="flex flex-wrap gap-3">
          <Link to={`/performance/${userId}`} className="btn btn-secondary">
            View performance analytics
          </Link>
        </div>
      )}
    </div>
  );
}
