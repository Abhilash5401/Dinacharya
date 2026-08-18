import { useAuthStore } from '@/store/authStore';

export default function Profile() {
  const user = useAuthStore((state) => state.user);

  if (!user) return null;

  return (
    <div className="max-w-2xl space-y-6">
      <h1 className="text-display-lg text-charcoal">Profile</h1>
      <div className="card space-y-6">
        <div className="flex items-center gap-4 pb-6 border-b border-warm-border">
          <div className="avatar-lg bg-terracotta border-terracotta-dark">
            {user.name.charAt(0).toUpperCase()}
          </div>
          <div>
            <p className="font-display text-2xl text-charcoal">{user.name}</p>
            <p className="text-body-md text-charcoal-muted">{user.email}</p>
          </div>
        </div>
        <div>
          <label className="block text-label-md text-charcoal-muted mb-1">Name</label>
          <p className="text-body-lg text-charcoal">{user.name}</p>
        </div>
        <div>
          <label className="block text-label-md text-charcoal-muted mb-1">Email</label>
          <p className="text-body-lg text-charcoal">{user.email}</p>
        </div>
        <div>
          <label className="block text-label-md text-charcoal-muted mb-1">Role</label>
          <span className="badge badge-secondary">{user.role}</span>
        </div>
        {user.department && (
          <div>
            <label className="block text-label-md text-charcoal-muted mb-1">Department</label>
            <p className="text-body-lg text-charcoal">{user.department}</p>
          </div>
        )}
        {user.bio && (
          <div>
            <label className="block text-label-md text-charcoal-muted mb-1">Bio</label>
            <p className="text-body-lg text-charcoal">{user.bio}</p>
          </div>
        )}
      </div>
    </div>
  );
}
