import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useResetPassword, useValidateResetToken } from '@/hooks/useAuth';
import Logo from '@/components/Logo';

const schema = z.object({
  newPassword: z.string().min(8, 'Password must be at least 8 characters'),
  confirmPassword: z.string().min(1, 'Confirm your password'),
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: 'Passwords do not match',
  path: ['confirmPassword'],
});

type FormValues = z.infer<typeof schema>;

export default function ResetPassword() {
  const [params] = useSearchParams();
  const token = params.get('token') || '';
  const navigate = useNavigate();
  const { data, isLoading } = useValidateResetToken(token);
  const resetPassword = useResetPassword();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
  });

  const onSubmit = async (values: FormValues) => {
    await resetPassword.mutateAsync({ token, newPassword: values.newPassword });
    navigate('/login');
  };

  const invalid = !token || (data && !data.valid);

  return (
    <div className="min-h-screen flex items-center justify-center py-12 px-4 bg-linen">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <div className="flex justify-center mb-6">
            <Logo size="lg" />
          </div>
          <h2 className="text-display-lg text-charcoal">Set a new password</h2>
        </div>

        <div className="bg-ivory rounded-2xl p-8 shadow-elevated border border-warm-border">
          {isLoading ? (
            <p className="text-charcoal-muted">Checking reset link…</p>
          ) : invalid ? (
            <div className="space-y-4">
              <p className="text-body-md text-charcoal">
                This reset link is invalid or has expired.
              </p>
              <Link to="/forgot-password" className="btn btn-primary inline-flex">
                Request a new link
              </Link>
            </div>
          ) : (
            <form className="space-y-5" onSubmit={handleSubmit(onSubmit)}>
              <div>
                <label className="block text-label-md text-charcoal-muted mb-2">New password</label>
                <input
                  {...register('newPassword')}
                  type="password"
                  autoComplete="new-password"
                  className="input"
                  placeholder="At least 8 characters"
                />
                {errors.newPassword && (
                  <p className="mt-2 text-label-sm text-error">{errors.newPassword.message}</p>
                )}
              </div>
              <div>
                <label className="block text-label-md text-charcoal-muted mb-2">Confirm password</label>
                <input
                  {...register('confirmPassword')}
                  type="password"
                  autoComplete="new-password"
                  className="input"
                  placeholder="Repeat password"
                />
                {errors.confirmPassword && (
                  <p className="mt-2 text-label-sm text-error">{errors.confirmPassword.message}</p>
                )}
              </div>
              <button
                type="submit"
                disabled={resetPassword.isPending}
                className="w-full btn btn-primary py-3"
              >
                {resetPassword.isPending ? 'Saving…' : 'Update password'}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
