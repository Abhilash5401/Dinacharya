/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Kinetic Enterprise - Navy-based Corporate palette
        surface: {
          DEFAULT: '#f7f9fb',
          dim: '#d8dadc',
          bright: '#f7f9fb',
          'container-lowest': '#ffffff',
          'container-low': '#f2f4f6',
          container: '#eceef0',
          'container-high': '#e6e8ea',
          'container-highest': '#e0e3e5',
        },
        'on-surface': {
          DEFAULT: '#191c1e',
          variant: '#45464d',
        },
        'inverse-surface': '#2d3133',
        'inverse-on-surface': '#eff1f3',
        outline: {
          DEFAULT: '#76777d',
          variant: '#c6c6cd',
        },
        'surface-tint': '#565e74',
        primary: {
          DEFAULT: '#0F172A',
          container: '#131b2e',
          fixed: '#dae2fd',
          'fixed-dim': '#bec6e0',
        },
        'on-primary': {
          DEFAULT: '#ffffff',
          container: '#7c839b',
          fixed: '#131b2e',
          'fixed-variant': '#3f465c',
        },
        'inverse-primary': '#bec6e0',
        secondary: {
          DEFAULT: '#505f76',
          container: '#d0e1fb',
          fixed: '#d3e4fe',
          'fixed-dim': '#b7c8e1',
        },
        'on-secondary': {
          DEFAULT: '#ffffff',
          container: '#54647a',
          fixed: '#0b1c30',
          'fixed-variant': '#38485d',
        },
        tertiary: {
          DEFAULT: '#000000',
          container: '#002113',
          fixed: '#6ffbbe',
          'fixed-dim': '#4edea3',
        },
        'on-tertiary': {
          DEFAULT: '#ffffff',
          container: '#009668',
          fixed: '#002113',
          'fixed-variant': '#005236',
        },
        error: {
          DEFAULT: '#ba1a1a',
          container: '#ffdad6',
        },
        'on-error': {
          DEFAULT: '#ffffff',
          container: '#93000a',
        },
        success: {
          DEFAULT: '#10b981',
          container: '#d1fae5',
        },
        'on-success': {
          DEFAULT: '#ffffff',
          container: '#047857',
        },
        warning: {
          DEFAULT: '#f59e0b',
          container: '#fef3c7',
        },
        'on-warning': {
          DEFAULT: '#ffffff',
          container: '#b45309',
        },
        background: '#f7f9fb',
        'on-background': '#191c1e',
        'surface-variant': '#e0e3e5',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      fontSize: {
        'display-lg': ['32px', { lineHeight: '40px', letterSpacing: '-0.02em', fontWeight: '700' }],
        'headline-lg': ['24px', { lineHeight: '32px', letterSpacing: '-0.01em', fontWeight: '600' }],
        'headline-lg-mobile': ['20px', { lineHeight: '28px', fontWeight: '600' }],
        'title-md': ['18px', { lineHeight: '24px', fontWeight: '600' }],
        'body-lg': ['16px', { lineHeight: '24px', fontWeight: '400' }],
        'body-md': ['14px', { lineHeight: '20px', fontWeight: '400' }],
        'label-md': ['12px', { lineHeight: '16px', letterSpacing: '0.01em', fontWeight: '500' }],
        'label-sm': ['11px', { lineHeight: '14px', fontWeight: '600' }],
      },
      spacing: {
        'base': '4px',
        'xs': '4px',
        'sm': '8px',
        'md': '16px',
        'lg': '24px',
        'xl': '32px',
        'gutter': '16px',
        'margin-mobile': '16px',
        'margin-desktop': '32px',
      },
      borderRadius: {
        'sm': '0.125rem',
        DEFAULT: '0.25rem',
        'md': '0.375rem',
        'lg': '0.5rem',
        'xl': '0.75rem',
        'full': '9999px',
      },
      boxShadow: {
        'card': '0 1px 3px rgba(15, 23, 42, 0.08)',
        'card-hover': '0 4px 8px rgba(15, 23, 42, 0.12)',
        'panel': '0 1px 3px rgba(15, 23, 42, 0.06)',
        'elevated': '0 20px 40px rgba(15, 23, 42, 0.2)',
      },
    },
  },
  plugins: [],
}