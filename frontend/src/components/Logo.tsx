interface LogoProps {
  size?: 'sm' | 'md' | 'lg';
  showText?: boolean;
  stacked?: boolean;
  className?: string;
}

const sizes = {
  sm: 'w-8 h-8',
  md: 'w-10 h-10',
  lg: 'w-16 h-16',
};

export default function Logo({ size = 'md', showText = false, stacked = false, className = '' }: LogoProps) {
  if (stacked) {
    return (
      <div className={`flex flex-col items-center text-center ${className}`}>
        <img
          src="/dinacharya-logo.png"
          alt="Dinacharya logo"
          className={`${sizes[size]} rounded-xl object-cover shadow-sm`}
        />
        {showText && (
          <>
            <h2 className="mt-3 font-display text-[28px] leading-8 text-charcoal font-semibold tracking-tight">
              Dinacharya
            </h2>
            <p className="mt-0.5 text-sm font-semibold text-charcoal-muted">Workspace</p>
          </>
        )}
      </div>
    );
  }

  return (
    <div className={`flex items-center gap-3 ${className}`}>
      <img
        src="/dinacharya-logo.png"
        alt="Dinacharya logo"
        className={`${sizes[size]} rounded-lg object-cover shadow-sm shrink-0`}
      />
      {showText && (
        <div>
          <h2 className="font-display text-xl text-charcoal font-semibold tracking-tight">Dinacharya</h2>
          <p className="text-label-sm text-charcoal-muted normal-case tracking-normal">Workspace</p>
        </div>
      )}
    </div>
  );
}
