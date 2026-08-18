import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link } from 'react-router-dom';
import { useState } from 'react';
import { useForgotPassword } from '@/hooks/useAuth';
import Logo from '@/components/Logo';

const schema = z.object({
  email: z.string().email('Invalid email address'),
});

type FormValues = z.infer<typeof schema>;

export default function ForgotPassword() {
  const [submitted, setSubmitted] = useState(false);
  const [mailReady, setMailReady] = useState(true);
  const forgotPassword = useForgotPassword();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
  });

  const onSubmit = async (data: FormValues) => {
    try {
      const result = await forgotPassword.mutateAsync(data.email.trim().toLowerCase());
      setSubmitted(true);
      setMailReady(result.mailReady !== false);
    } catch {
      // toast from hook
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center py-12 px-4 bg-linen">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <div className="flex justify-center mb-6">
            <Logo size="lg" />
          </div>
          <h2 className="text-display-lg text-charcoal">Forgot password</h2>
          <p className="mt-2 text-body-md text-charcoal-muted">
            Remembered it?{' '}
            <Link to="/login" className="text-terracotta hover:text-terracotta-dark font-medium">
              Sign in
            </Link>
          </p>
        </div>

        <div className="bg-ivory rounded-2xl p-8 shadow-elevated border border-warm-border">
          {submitted ? (
            <div className="space-y-3 text-body-md text-charcoal">
              <p>
                If that email is in our system, we created a reset link. It expires in 15 minutes.
              </p>
              {mailReady ? (
                <p>Check your inbox and spam folder.</p>
              ) : (
                <p>
                  Email sending is not configured yet, so nothing was mailed.
                  Open the backend console and look for a line starting with
                  {' '}<strong>Mail is not ready. Password reset link</strong>, then open that URL.
                </p>
              )}
            </div>
          ) : (
            <form className="space-y-5" onSubmit={handleSubmit(onSubmit)} autoComplete="off">
              <div>
                <label className="block text-label-md text-charcoal-muted mb-2">Work email</label>
                <input
                  {...register('email')}
                  type="email"
                  autoComplete="username"
                  className="input"
                  placeholder="you@company.com"
                />
                {errors.email && <p className="mt-2 text-label-sm text-error">{errors.email.message}</p>}
              </div>
              <button
                type="submit"
                disabled={forgotPassword.isPending}
                className="w-full btn btn-primary py-3"
              >
                {forgotPassword.isPending ? 'Sending…' : 'Send reset link'}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
