import { UserRole } from '@/types';
import { User } from '@/types';

export function isAdminUser(user?: { role?: string | UserRole } | null, fallbackRole?: string | null) {
  const role = user?.role || fallbackRole;
  return role === UserRole.ADMIN || role === UserRole.MODERATOR;
}

export function homePath(user?: { role?: string | UserRole } | null, fallbackRole?: string | null) {
  return isAdminUser(user, fallbackRole) ? '/tasks' : '/work';
}
