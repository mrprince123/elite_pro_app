---
name: Vitality Core
colors:
  surface: '#fdf8fb'
  surface-dim: '#ddd9db'
  surface-bright: '#fdf8fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f7f2f5'
  surface-container: '#f1edef'
  surface-container-high: '#ebe7e9'
  surface-container-highest: '#e6e1e4'
  on-surface: '#1c1b1d'
  on-surface-variant: '#44474E'
  inverse-surface: '#313032'
  inverse-on-surface: '#f4eff2'
  outline: '#727785'
  outline-variant: '#c2c6d5'
  surface-tint: '#005ac1'
  primary: '#0058bd'
  on-primary: '#ffffff'
  primary-container: '#2771df'
  on-primary-container: '#fefcff'
  inverse-primary: '#adc6ff'
  secondary: '#006e2c'
  on-secondary: '#ffffff'
  secondary-container: '#86f898'
  on-secondary-container: '#00722f'
  tertiary: '#765700'
  on-tertiary: '#ffffff'
  tertiary-container: '#956e00'
  on-tertiary-container: '#fffbff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d8e2ff'
  primary-fixed-dim: '#adc6ff'
  on-primary-fixed: '#001a41'
  on-primary-fixed-variant: '#004494'
  secondary-fixed: '#89fa9b'
  secondary-fixed-dim: '#6ddd81'
  on-secondary-fixed: '#002108'
  on-secondary-fixed-variant: '#005320'
  tertiary-fixed: '#ffdfa0'
  tertiary-fixed-dim: '#fbbc05'
  on-tertiary-fixed: '#261a00'
  on-tertiary-fixed-variant: '#5c4300'
  background: '#fdf8fb'
  on-background: '#1c1b1d'
  surface-variant: '#E1E2EC'
  activity-blue: '#4285F4'
  activity-green: '#34A853'
  activity-yellow: '#FBBC04'
  activity-red: '#EA4335'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 57px
    fontWeight: '400'
    lineHeight: 64px
    letterSpacing: -0.25px
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '400'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '400'
    lineHeight: 36px
  headline-md:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '400'
    lineHeight: 36px
  title-lg:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: '500'
    lineHeight: 28px
  title-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: 0.15px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 8px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 24px
  container-max-width: 1200px
---

## Brand & Style

The design system is rooted in an "Approachable Medical" aesthetic—combining the clinical precision of health data with the warmth of a lifestyle companion. It follows a **Corporate / Modern** style heavily influenced by Material 3 principles, prioritizing clarity, accessibility, and high-readability.

The brand personality is encouraging and disciplined. It avoids overwhelming the user with complex charts, instead using purposeful whitespace and soft geometry to make health metrics feel achievable. The interface should feel like a premium physical product—smooth, responsive, and organized.

## Colors

This design system utilizes a dynamic color palette centered around the primary Google Blue. Colors are functional rather than purely decorative: Blue represents Heart Points, Green represents Steps, Yellow represents Energy/Calories, and Red represents critical alerts or high-intensity zones.

For both Light and Dark modes, the system employs **Material 3 Surface Containers**. Surfaces are not pure white or black but are tinted with the neutral color to reduce eye strain. 
- **Light Mode:** Uses high-luminance surfaces (#FEFBFF) with subtle grey-blue overlays for containers.
- **Dark Mode:** Uses deep charcoal surfaces (#1C1B1D) with tonal elevation layers to distinguish between the background and interactive cards.

## Typography

The typography system uses **Inter** to achieve a clean, systematic look that mirrors the clarity of medical readouts while remaining friendly for everyday use. 

- **Display & Headline:** Reserved for primary metrics (e.g., daily step count). These use a lighter weight to remain elegant even at large scales.
- **Title:** Used for card headers and section titles to provide clear information architecture.
- **Body:** Optimized for legibility in health insights and descriptions, using standard weights with generous line heights.
- **Labels:** Used for navigation, buttons, and overline text. These are often set in medium weights to stand out despite their small size.

## Layout & Spacing

The design system is built on a **standardized 8dp (8px) grid**, ensuring all elements align to a consistent rhythmic scale. 

- **Mobile:** 4-column fluid grid with 16px margins and 16px gutters.
- **Tablet:** 8-column fluid grid with 24px margins and 24px gutters.
- **Desktop:** 12-column fixed grid with a maximum content width of 1200px. 

Spacing is intentionally generous. Primary health cards should use "Vertical Stack" patterns on mobile, transitioning to a "Bento Box" style grid on desktop where metrics are grouped logically by activity type.

## Elevation & Depth

In accordance with Material 3, hierarchy is communicated through **Tonal Layers** rather than heavy shadows. 

1. **Level 0 (Base):** The primary background color.
2. **Level 1 (Card):** A slightly lighter/tinted surface container. Used for secondary information.
3. **Level 2 (Active Card):** Surfaces with a very soft, diffused ambient shadow (4px blur, 8% opacity) to indicate interactivity.
4. **Level 3 (Modals/FABs):** Distinctly elevated surfaces that sit atop the UI, using a combination of tonal shifts and subtle shadows.

Glassmorphism is used sparingly, only for persistent navigation bars to maintain context of the content scrolling underneath.

## Shapes

The shape language is **Highly Rounded**, emphasizing the lifestyle and approachable nature of the brand.

- **Primary Cards:** Use a 28dp radius, creating a soft "pill-like" appearance for container edges.
- **Buttons & Chips:** Use a full "Pill" shape (100px+) for high-action items, providing a friendly touch target.
- **Data Visualizations:** Progress rings and bars should have rounded caps to match the container geometry. 
- **Selection States:** Use rounded-rectangle "halos" to indicate focus and selection.

## Components

### Buttons
- **Primary FAB (Floating Action Button):** A large, rounded-square button with 28dp corners, used for "Start Workout."
- **Action Buttons:** Pill-shaped with a solid fill for primary actions and outlined for secondary actions.

### Cards
- **Metric Cards:** Use the 28dp radius. They should feature a primary icon in the top-left, the metric headline in the center, and a progress indicator (ring or sparkline) at the bottom.

### Chips
- **Activity Chips:** Small, pill-shaped filters used to toggle between data views (Day, Week, Month). Use a tonal background for inactive and primary color fill for active.

### Lists
- **Activity Feed:** Minimalist rows with large icons and clear title/subtitle pairings. Dividers should be full-width but very low contrast (Surface Variant).

### Input Fields
- **Filled Inputs:** Material 3 style with rounded top corners (4dp) and a thick bottom stroke. The background should be a subtle tonal shift from the surface.

### Additional Components
- **Progress Rings:** The signature "Heart Points" ring, featuring a 12dp stroke width with rounded ends and a semi-transparent track.