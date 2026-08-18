import { useEffect, useMemo, useRef, useState } from 'react';
import { User } from '@/types';
import { useAuthStore } from '@/store/authStore';

function uniqueUsers(users: User[]) {
  const seen = new Set<string>();
  return users.filter((user) => {
    if (!user?.id || seen.has(user.id)) return false;
    seen.add(user.id);
    return true;
  });
}

function initials(name: string) {
  const parts = name.trim().split(/\s+/);
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return `${parts[0][0] || ''}${parts[1][0] || ''}`.toUpperCase();
}

interface Props {
  users: User[];
  value?: string;
  onChange: (userId: string) => void;
  disabled?: boolean;
}

export default function AssigneePicker({ users, value, onChange, disabled }: Props) {
  const currentUser = useAuthStore((state) => state.user);
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState('');
  const containerRef = useRef<HTMLDivElement>(null);
  const searchRef = useRef<HTMLInputElement>(null);

  const people = uniqueUsers(users);
  const selected = people.find((user) => user.id === value);

  const filtered = useMemo(() => {
    const term = query.trim().toLowerCase();
    const sorted = [...people].sort((a, b) => {
      if (a.id === currentUser?.id) return -1;
      if (b.id === currentUser?.id) return 1;
      return a.name.localeCompare(b.name);
    });
    if (!term) return sorted;
    return sorted.filter((user) =>
      user.name.toLowerCase().includes(term) ||
      user.email.toLowerCase().includes(term)
    );
  }, [people, query, currentUser?.id]);

  useEffect(() => {
    if (!open) return;
    searchRef.current?.focus();
    const onClick = (event: MouseEvent) => {
      if (!containerRef.current?.contains(event.target as Node)) {
        setOpen(false);
        setQuery('');
      }
    };
    document.addEventListener('mousedown', onClick);
    return () => document.removeEventListener('mousedown', onClick);
  }, [open]);

  const selectUser = (userId: string) => {
    onChange(userId);
    setOpen(false);
    setQuery('');
  };

  return (
    <div ref={containerRef} className="relative">
      <button
        type="button"
        disabled={disabled}
        onClick={() => setOpen((prev) => !prev)}
        className="flex items-center gap-2 px-2 py-1.5 rounded-full hover:bg-sand transition-colors disabled:opacity-50"
      >
        {selected ? (
          <>
            <span className="w-8 h-8 rounded-full bg-[#E07A3D] text-white text-label-sm font-semibold flex items-center justify-center">
              {initials(selected.name)}
            </span>
            <span className="text-body-md text-charcoal">{selected.name}</span>
          </>
        ) : (
          <>
            <span className="w-8 h-8 rounded-full border-2 border-[#7EB6E6] bg-sand flex items-center justify-center text-charcoal-muted">
              <span className="material-symbols-outlined text-[18px]">person</span>
            </span>
            <span className="text-body-md text-charcoal-muted">Assign</span>
          </>
        )}
        <span className="material-symbols-outlined text-[18px] text-charcoal-light">expand_more</span>
      </button>

      {open && (
        <div className="absolute z-[80] mt-2 w-[280px] rounded-xl bg-[#2F2F2F] shadow-elevated p-2">
          <input
            ref={searchRef}
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search for people"
            className="w-full rounded-md bg-[#2F2F2F] text-white placeholder:text-[#9A9A9A] px-3 py-2 text-body-md outline-none border border-[#7EB6E6]"
          />

          <div className="mt-2 max-h-56 overflow-y-auto custom-scroll">
            <button
              type="button"
              onClick={() => selectUser('')}
              className="w-full flex items-center gap-3 px-2 py-2 rounded-lg hover:bg-white/10 text-left"
            >
              <span className="w-8 h-8 rounded-full bg-[#5C5C5C] flex items-center justify-center text-[#C8C8C8]">
                <span className="material-symbols-outlined text-[18px]">person</span>
              </span>
              <span className="text-white text-body-md">Automatic</span>
            </button>

            {filtered.map((user) => (
              <button
                key={user.id}
                type="button"
                onClick={() => selectUser(user.id)}
                className="w-full flex items-center gap-3 px-2 py-2 rounded-lg hover:bg-white/10 text-left"
              >
                <span className="w-8 h-8 rounded-full bg-[#E07A3D] text-white text-label-sm font-semibold flex items-center justify-center shrink-0">
                  {initials(user.name)}
                </span>
                <span className="text-white text-body-md truncate">
                  {user.name}
                  {user.id === currentUser?.id && (
                    <span className="text-[#A3A3A3]"> (assign to me)</span>
                  )}
                </span>
              </button>
            ))}

            {filtered.length === 0 && (
              <p className="px-3 py-3 text-[#9A9A9A] text-body-md">No people found</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
