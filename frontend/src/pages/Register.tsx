import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { useRegister } from '@/hooks/useAuth';
import { homePath } from '@/auth/roles';
import Logo from '@/components/Logo';
import { DEPARTMENTS } from '@/constants/departments';

const registerSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  name: z.string().min(2, 'Name must be at least 2 characters'),
  department: z.string().optional(),
});

type RegisterForm = z.infer<typeof registerSchema>;

export default function Register() {
  const navigate = useNavigate();
  const registerMutation = useRegister();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
    defaultValues: { name: '', email: '', password: '', department: '' },
  });

  const onSubmit = async (data: RegisterForm) => {
    try {
      const result = await registerMutation.mutateAsync({
        ...data,
        email: data.email.trim().toLowerCase(),
      });
      navigate(homePath(result.user));
    } catch {
      // toast is shown by useRegister
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-linen py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <div className="flex justify-center mb-6">
            <Logo size="lg" />
          </div>
          <h2 className="text-display-lg text-charcoal">
            Create your account
          </h2>
          <p className="mt-2 text-body-md text-charcoal-muted">
            Already have an account?{' '}
            <Link to="/login" className="text-terracotta hover:text-terracotta-dark font-medium transition-colors">
              Sign in
            </Link>
          </p>
        </div>
        
        <div className="bg-ivory rounded-2xl p-8 shadow-elevated border border-warm-border">
          <form
            className="space-y-5"
            onSubmit={handleSubmit(onSubmit)}
            autoComplete="off"
          >
            <div>
              <label className="block text-label-md text-charcoal-muted mb-2">
                Full Name
              </label>
              <input
                {...register('name')}
                autoComplete="off"
                className="input"
                placeholder="Your name"
              />
              {errors.name && <p className="mt-2 text-label-sm text-error">{errors.name.message}</p>}
            </div>
            
            <div>
              <label className="block text-label-md text-charcoal-muted mb-2">
                Email
              </label>
              <input
                {...register('email')}
                type="email"
                autoComplete="off"
                data-1p-ignore
                data-lpignore="true"
                className="input"
                placeholder="you@company.com"
              />
              {errors.email && <p className="mt-2 text-label-sm text-error">{errors.email.message}</p>}
            </div>
            
            <div>
              <label className="block text-label-md text-charcoal-muted mb-2">
                Password
              </label>
              <input
                {...register('password')}
                type="password"
                autoComplete="new-password"
                data-1p-ignore
                data-lpignore="true"
                className="input"
                placeholder="Create a password"
              />
              {errors.password && <p className="mt-2 text-label-sm text-error">{errors.password.message}</p>}
            </div>
            
            <div>
              <label className="block text-label-md text-charcoal-muted mb-2">
                Department (Optional)
              </label>
              <select {...register('department')} className="input">
                <option value="">Select department</option>
                {DEPARTMENTS.map((department) => (
                  <option key={department} value={department}>
                    {department}
                  </option>
                ))}
              </select>
            </div>

            <button
              type="submit"
              disabled={registerMutation.isPending}
              className="w-full btn btn-primary py-3"
            >
              {registerMutation.isPending ? 'Creating account...' : 'Create account'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
