---
name: Kinetic Enterprise
colors:
  surface: '#f7f9fb'
  surface-dim: '#d8dadc'
  surface-bright: '#f7f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f6'
  surface-container: '#eceef0'
  surface-container-high: '#e6e8ea'
  surface-container-highest: '#e0e3e5'
  on-surface: '#191c1e'
  on-surface-variant: '#45464d'
  inverse-surface: '#2d3133'
  inverse-on-surface: '#eff1f3'
  outline: '#76777d'
  outline-variant: '#c6c6cd'
  surface-tint: '#565e74'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#131b2e'
  on-primary-container: '#7c839b'
  inverse-primary: '#bec6e0'
  secondary: '#505f76'
  on-secondary: '#ffffff'
  secondary-container: '#d0e1fb'
  on-secondary-container: '#54647a'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#002113'
  on-tertiary-container: '#009668'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dae2fd'
  primary-fixed-dim: '#bec6e0'
  on-primary-fixed: '#131b2e'
  on-primary-fixed-variant: '#3f465c'
  secondary-fixed: '#d3e4fe'
  secondary-fixed-dim: '#b7c8e1'
  on-secondary-fixed: '#0b1c30'
  on-secondary-fixed-variant: '#38485d'
  tertiary-fixed: '#6ffbbe'
  tertiary-fixed-dim: '#4edea3'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#005236'
  background: '#f7f9fb'
  on-background: '#191c1e'
  surface-variant: '#e0e3e5'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  title-md:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 32px
---

## Brand & Style

This design system is built for high-stakes enterprise productivity, focusing on cognitive clarity, speed of interaction, and professional reliability. The brand personality is disciplined, efficient, and sophisticated. 

The design style follows a **Corporate / Modern** aesthetic with a heavy emphasis on **Minimalism**. It utilizes expansive whitespace to reduce visual noise, ensuring that users can focus on complex task hierarchies without distraction. Subtle depth is used not for decoration, but to establish a clear functional mental model of the application's information architecture. The emotional response should be one of "controlled focus"—providing the user with a sense of mastery over their workload through a clean, systematic interface.

## Colors

The palette is anchored by a deep navy (`#0F172A`), used for primary actions and high-level navigation to project authority and stability. Surfaces rely on a scale of cool, soft grays to create a layered hierarchy without the harshness of pure black text on white.

Vibrant functional accents are used sparingly but purposefully:
- **Emerald Green**: Indicates completed tasks, successful syncs, and positive progress.
- **Amber**: Signals pending items, high-priority warnings, or items requiring review.
- **Slate Blue/Gray**: Used for secondary metadata and inactive states to recede into the background.

The default mode is `light`, optimized for legibility in well-lit office environments, though the token structure supports a future dark mode implementation using the same tonal logic.

## Typography

The design system utilizes **Inter** for all typographic roles. Its tall x-height and systematic spacing make it ideal for data-heavy enterprise screens where legibility at small sizes is paramount.

- **Headlines**: Use tighter letter spacing and heavier weights to create a strong visual anchor for page sections.
- **Body Text**: Standardized at 14px and 16px to ensure comfortable long-form reading of task descriptions.
- **Labels**: Small, medium-weight caps or high-contrast labels are used for metadata like "Due Date" or "Assignee" to differentiate them from actionable content.
- **Mobile Scaling**: Headlines automatically scale down on mobile viewports to prevent awkward line breaks while maintaining the same weight-based hierarchy.

## Layout & Spacing

This design system employs a **Fluid Grid** model based on a 4px baseline shift. All spacing tokens are multiples of 4, ensuring a mathematically consistent rhythm throughout the UI.

- **Mobile**: A 4-column grid with 16px outer margins and 16px gutters.
- **Tablet/Desktop**: An 8 or 12-column grid that expands fluidly, but caps the maximum content width for readability (1200px max-width).
- **Vertical Spacing**: Use `lg` (24px) for separating major sections and `md` (16px) for spacing elements within a card or list item. 

The layout relies on "Safe Areas" for mobile notch and home indicators, ensuring critical actions (like the "New Task" button) are always accessible within the thumb zone.

## Elevation & Depth

Hierarchy is established through **Tonal Layers** and extremely subtle **Ambient Shadows**. 

- **Level 0 (Base)**: The background (`#F8FAFC`) acts as the canvas.
- **Level 1 (Cards/Lists)**: White surfaces (`#FFFFFF`) with a 1px border of `#E2E8F0` or a very soft, diffused shadow (0px 1px 3px rgba(15, 23, 42, 0.08)).
- **Level 2 (Modals/Overlays)**: Elevated surfaces used for task creation or filtering, featuring a more pronounced shadow to indicate temporary focus.

Avoid heavy shadows or "neomorphic" effects. Depth should feel like stacked sheets of paper—clean, thin, and physical.

## Shapes

The design system adopts a **Soft** shape language. 
- **Standard UI Elements**: (Buttons, Input Fields, Checkboxes) use a `0.25rem` (4px) radius. This provides a professional, "exact" feel while remaining approachable.
- **Containers**: Cards and Modals use `0.5rem` (8px) for a clear, distinct boundary.
- **Avatars/Badges**: Small status badges and user avatars may use "full" rounding (pill-shaped) to distinguish human/status elements from structural UI elements.

## Components

- **Buttons**: Primary buttons are solid Navy (`#0F172A`) with white text. Secondary buttons are outlined or ghost-style using `#64748B`. Touch targets are a minimum of 44x44px.
- **Task Cards**: Featuring a white background, a 4px left-accent border for priority (e.g., Amber for high priority), and clear 16px padding. 
- **Input Fields**: Labeled with `label-md` typography. The border color shifts from Light Gray to Navy on focus, with a 2px stroke.
- **Chips/Status Badges**: Small, semi-transparent backgrounds with high-contrast text (e.g., Emerald text on a 10% opacity Emerald background) for status indicators.
- **Navigation**: A persistent bottom navigation bar on mobile for primary views (Inbox, Projects, Calendar, Profile), utilizing icons with 24px bounding boxes.
- **Lists**: Dense, high-information lists with subtle dividers (`#F1F5F9`) between rows. Each row should include a large checkbox for satisfying "task complete" interactions.