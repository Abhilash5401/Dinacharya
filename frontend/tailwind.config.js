/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Warm Neutral Luxury Palette
        linen: '#FBF9F5',
        biscuit: {
          DEFAULT: '#F3E2C4',
          light: '#F8EDD8',
          dark: '#E6D0A8',
          crust: '#C4A574',
        },
        sand: '#F4F1EA',
        ivory: '#FFFFFF',
        charcoal: {
          DEFAULT: '#222521',
          muted: '#5C5E58',
          light: '#8A8C86',
        },
        terracotta: {
          DEFAULT: '#C57A44',
          dark: '#A86535',
          light: '#E8C4A8',
        },
        gold: {
          DEFAULT: '#D4AF37',
          dark: '#B8962E',
          light: '#F5E6B8',
        },
        // Semantic aliases
        surface: {
          DEFAULT: '#FBF9F5',
          dim: '#F4F1EA',
          bright: '#FFFFFF',
          'container-lowest': '#FFFFFF',
          'container-low': '#F4F1EA',
          container: '#EDE9E1',
          'container-high': '#E8E4DC',
          'container-highest': '#DDD8CE',
        },
        'on-surface': {
          DEFAULT: '#222521',
          variant: '#5C5E58',
        },
        'inverse-surface': '#222521',
        'inverse-on-surface': '#FBF9F5',
        outline: {
          DEFAULT: '#8A8C86',
          variant: '#E8E4DC',
        },
        primary: {
          DEFAULT: '#C57A44',
          container: '#E8C4A8',
          fixed: '#F5E6B8',
          'fixed-dim': '#D4AF37',
        },
        'on-primary': {
          DEFAULT: '#FFFFFF',
          container: '#222521',
          fixed: '#222521',
          'fixed-variant': '#A86535',
        },
        'inverse-primary': '#D4AF37',
        'surface-tint': '#C57A44',
        secondary: {
          DEFAULT: '#222521',
          container: '#F4F1EA',
          fixed: '#F5E6B8',
          'fixed-dim': '#D4AF37',
        },
        'on-secondary': {
          DEFAULT: '#FBF9F5',
          container: '#222521',
          fixed: '#222521',
          'fixed-variant': '#5C5E58',
        },
        tertiary: {
          DEFAULT: '#D4AF37',
          container: '#F5E6B8',
          fixed: '#F5E6B8',
          'fixed-dim': '#E8C4A8',
        },
        'on-tertiary': {
          DEFAULT: '#222521',
          container: '#222521',
          fixed: '#222521',
          'fixed-variant': '#A86535',
        },
        error: {
          DEFAULT: '#B54A4A',
          container: '#F5DEDE',
        },
        success: {
          DEFAULT: '#16a34a',
          container: '#d1fae5',
        },
        warning: {
          DEFAULT: '#d97706',
          container: '#fef3c7',
        },
        'on-error': {
          DEFAULT: '#FFFFFF',
          container: '#8B3333',
        },
        background: '#FBF9F5',
        'on-background': '#222521',
        'surface-variant': '#F4F1EA',
        warm: {
          border: '#E8E4DC',
          'border-strong': '#DDD8CE',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        display: ['Cormorant Garamond', 'Georgia', 'serif'],
      },
      fontSize: {
        'display-lg': ['36px', { lineHeight: '44px', letterSpacing: '-0.02em', fontWeight: '600' }],
        'headline-md': ['24px', { lineHeight: '32px', letterSpacing: '-0.01em', fontWeight: '600' }],
        'headline-md-mobile': ['20px', { lineHeight: '28px', fontWeight: '600' }],
        'headline-sm': ['20px', { lineHeight: '28px', fontWeight: '600' }],
        'title-lg': ['18px', { lineHeight: '24px', fontWeight: '600' }],
        'body-lg': ['16px', { lineHeight: '24px', fontWeight: '400' }],
        'body-md': ['14px', { lineHeight: '20px', fontWeight: '400' }],
        'label-md': ['12px', { lineHeight: '16px', letterSpacing: '0.08em', fontWeight: '600' }],
        'label-sm': ['11px', { lineHeight: '14px', fontWeight: '500' }],
      },
      spacing: {
        'unit': '8px',
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
        '2xl': '1rem',
        'full': '9999px',
      },
      boxShadow: {
        'card': '0 1px 3px rgba(34, 37, 33, 0.06), 0 1px 2px rgba(34, 37, 33, 0.04)',
        'card-hover': '0 8px 24px rgba(34, 37, 33, 0.08), 0 2px 8px rgba(34, 37, 33, 0.04)',
        'panel': '0 2px 8px rgba(34, 37, 33, 0.05)',
        'elevated': '0 12px 32px rgba(34, 37, 33, 0.1)',
      },
    },
  },
  plugins: [],
}
