---
name: Ethereal Companion
colors:
  surface: '#fff8f2'
  surface-dim: '#dfd9d3'
  surface-bright: '#fff8f2'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f9f2ec'
  surface-container: '#f3ede7'
  surface-container-high: '#eee7e1'
  surface-container-highest: '#e8e1dc'
  on-surface: '#1e1b18'
  on-surface-variant: '#514345'
  inverse-surface: '#33302c'
  inverse-on-surface: '#f6f0ea'
  outline: '#847375'
  outline-variant: '#d6c2c3'
  surface-tint: '#874e58'
  primary: '#874e58'
  on-primary: '#ffffff'
  primary-container: '#ffb6c1'
  on-primary-container: '#7b444e'
  inverse-primary: '#fcb3be'
  secondary: '#655781'
  on-secondary: '#ffffff'
  secondary-container: '#deccfd'
  on-secondary-container: '#62547e'
  tertiary: '#42617d'
  on-tertiary: '#ffffff'
  tertiary-container: '#adcded'
  on-tertiary-container: '#385772'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffd9de'
  primary-fixed-dim: '#fcb3be'
  on-primary-fixed: '#360c17'
  on-primary-fixed-variant: '#6b3741'
  secondary-fixed: '#eaddff'
  secondary-fixed-dim: '#d0bfef'
  on-secondary-fixed: '#21143a'
  on-secondary-fixed-variant: '#4d4068'
  tertiary-fixed: '#cde5ff'
  tertiary-fixed-dim: '#aacae9'
  on-tertiary-fixed: '#001d32'
  on-tertiary-fixed-variant: '#2a4a64'
  background: '#fff8f2'
  on-background: '#1e1b18'
  surface-variant: '#e8e1dc'
typography:
  headline-lg:
    fontFamily: Nunito Sans
    fontSize: 32px
    fontWeight: '800'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Nunito Sans
    fontSize: 28px
    fontWeight: '800'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Nunito Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  body-lg:
    fontFamily: Nunito Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Nunito Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Nunito Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Nunito Sans
    fontSize: 12px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.03em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  unit: 4px
  container-padding-mobile: 24px
  container-padding-desktop: 48px
  gutter: 16px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 32px
---

## Brand & Style
The design system is centered on the "Saathi" (companion) philosophy—creating an interface that feels like a supportive, breathing entity rather than a cold utility. The aesthetic leans heavily into **Glassmorphism**, utilizing multi-layered translucency to evoke a sense of lightness and emotional safety. 

The target audience is women seeking a sophisticated yet approachable health companion. The UI should feel "alive" through the use of soft background gradients that shift slightly, simulating a gentle pulse. Every interaction should feel cushioned and premium, moving away from harsh edges toward a soft, organic digital environment.

## Colors
The palette is rooted in a **Warm Cream (#FDF6F0)** canvas, providing a human, paper-like warmth that prevents the glass effects from feeling too icy. 

- **Blush Pink** serves as the primary brand touchstone, used for active states and critical path actions.
- **Lavender and Baby Blue** act as secondary accents for data visualization (e.g., cycle phases) and secondary navigation.
- **Butter Yellow and Mint** provide soft, non-threatening feedback for mood tracking and health milestones.

Glass elements should use a semi-transparent white (`rgba(255, 255, 255, 0.4)`) as their base to interact beautifully with the warm background and underlying accent gradients.

## Typography
This design system utilizes **Nunito Sans** for its inherently friendly and rounded character. The typographic hierarchy is intentionally generous with line height to ensure readability and a "breathing" layout.

Headlines use a heavier weight (Bold/ExtraBold) to establish a clear hierarchy against the soft glass backgrounds. Body text is kept at a comfortable 16px-18px range to maintain accessibility. Letter spacing is slightly tightened on large headings to keep the rounded letters feeling cohesive and professional.

## Layout & Spacing
The layout follows a **Fluid Grid** model with a focus on mobile-first interaction. We utilize a 4-column grid for mobile and a 12-column grid for tablet/desktop views.

The spacing rhythm is "Airy." Margin and padding values are intentionally large (24px minimum for container edges) to prevent the glass elements from feeling cluttered. We prioritize vertical stacks with generous gaps (`stack-lg`) to give each health metric or insight its own dedicated "island" of focus.

## Elevation & Depth
Depth is the core differentiator of this design system. Rather than traditional black shadows, we use:

1.  **Backdrop Blur:** All containers must have a `backdrop-filter: blur(20px)`.
2.  **Frosted Glass Fill:** Backgrounds use a linear gradient of `white` at 60% opacity to 40% opacity.
3.  **Inner Glow Borders:** 1px solid or semi-transparent white borders (`rgba(255, 255, 255, 0.5)`) that act as a "highlight" on the top and left edges.
4.  **Diffusion:** Soft, low-opacity shadows using the color of the background (Warm Cream) rather than grey, set at 15% opacity with a 30px spread for high-level cards.

## Shapes
The shape language is ultra-soft and organic. 
- **Primary Cards:** Must use a corner radius of **32px**.
- **Secondary Elements / Inputs:** Use a corner radius of **20px**.
- **Buttons / Chips:** Use a full pill-shape (radius of 999px) to invite touch and reinforce the "friendly companion" persona.

Avoid any sharp 90-degree angles in the entire interface to maintain the emotional safety of the design.

## Components

### Buttons
Primary buttons are high-contrast (Blush Pink) with a white label. They should have a subtle "squish" animation on press. Secondary buttons use the frosted glass effect with a thin Blush Pink border.

### Cards
Cards are the primary content vehicle. They feature the 20px blur and 32px radius. To distinguish "today's status," the current day's card should have a 2px Blush Pink stroke, while others remain subtle with white strokes.

### Inputs
Text fields and dropdowns are "Ghost" style—fully transparent with a 1px white border and a subtle blur. Upon focus, the border transitions to Lavender and the background opacity increases slightly.

### Progress & Tracking
Cycle tracking visualizations should use organic, "blob" or circular shapes rather than hard bars. Use the Mint and Soft Red colors for "fertile" and "period" windows respectively, applying a soft outer glow to make them appear to radiate light from behind the glass.

### Chips & Tags
Used for mood or symptom tracking. These should be pill-shaped and utilize the pastel palette. When selected, they should glow with a soft drop shadow of their own color.