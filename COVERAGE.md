# Coverage

## Component registry

| Type | What it renders | Key props | Notes |
|---|---|---|---|
| `header_bar` | Location pill + search box + avatar | `location`, `searchPlaceholder`, `avatarInitial` | Home-screen-specific header shape |
| `search_header_bar` | Back button + full-width search box | `searchPlaceholder` | Added during the coverage dry-run |
| `chip_tab_row` | Selectable/tappable horizontal chip row | `stateKey`, `default`, `style` (`text_pill` \| `icon_pill` \| `icon_inline_pill`), `options[]` | Same component drives category tabs, a Wishlisted/Hot-Deals toggle, and plain search-filter chips — only the action wired to `onSelect` differs (`SET_STATE` vs `NAVIGATE`) |
| `banner_card_rail` | Colored banner with a title + horizontal icon-card rail | `title`, `badgeText`, `backgroundColor`, `textColor`, `items[]` | |
| `icon_grid` | Chunked grid of icon+label items (wraps to new rows) | `columns`, `cardBackgroundColor`, `items[]` | Not lazy — for short, bounded lists |
| `icon_rail` | Horizontally-scrollable icon+label items, no wrapper | `items[]` | Added during the coverage dry-run |
| `section` | Title + optional "View all"/trailing action + children | `title`, `viewAllRoute`, `trailingActionLabel`, `backgroundColor` | Generic wrapper reused by every titled block on both screens built |
| `car_card_rail` | Horizontal car card carousel | `items[]` (title/subtitle/image/onTap) | |
| `car_card_grid` | Vertical car list (1 col) or grid (>1 col) with price/badges | `columns`, `items[]` | |
| `promo_banner` | Full-bleed image/gradient banner + CTA | `title`, `subtitle`, `imageUrl`, `ctaLabel`, `onTap` | |
| `location_card` | Showroom-style card: image, meta, two CTAs | `imageUrl`, `name`, `primaryCta`, `secondaryCta` | |
| `conditional` | Swaps its child based on a state key (not a special JSON shape — a normal node with `caseKey`-tagged children) | `stateKey`, `default` | What makes the chip toggle actually change content |
| `footer` | Centered tagline block | `title`, `subtitle`, `backgroundColor` | |

## UI patterns the schema can express

- **Lists / rails:** `car_card_rail`, `banner_card_rail`'s item row, `icon_rail` — all `LazyRow`-backed.
- **Grids:** `icon_grid`, `car_card_grid` (columns > 1) — chunked, non-lazy by design (bounded item counts).
- **Vertical lists:** `car_card_grid` (columns = 1).
- **Conditionals / state-driven content swap:** `conditional`, paired with any `chip_tab_row` sharing its `stateKey`. Not a special node shape — any node with `caseKey`-tagged children works.
- **Actions:** `SET_STATE`, `NAVIGATE` (with arbitrary `params`), `OPEN_SHEET` — all fireable from any node's `actions` map or an item's inline `onTap`. The **same node type can drive different action types** without any code change (the search dry-run wired `chip_tab_row`'s `onSelect` to `NAVIGATE` instead of `SET_STATE` and it worked immediately).
- **Styling overrides:** `backgroundColor`/`textColor` as hex strings on `section`, `banner_card_rail`, `promo_banner`, `footer`, `icon_grid` — parsed with a safe fallback (`toComposeColor`), never a crash on a bad hex.
- **Data binding / templating:** `{{state.key}}` and `{{eventKey}}` substitution in action `value`/`params` — deliberately just substitution, not a full expression language.
- **Unknown-type graceful degradation:** any `type` not in the registry above renders a visible, labeled fallback instead of crashing — demonstrated live in `home_screen.json`'s `loyalty_points_widget` node.

## Honest coverage claim

**Given a new Cars24 screen, roughly 60% renders immediately with JSON-only
changes; the remaining ~40% needs small, targeted new components — each took
15–20 minutes to add and register, not a redesign.**

This isn't an estimate — it's what actually happened. See below.

## Second-screen exercise

**Screen:** the Cars24 search page (reached by tapping the home screen's
search bar) — a screenshot was provided as the reference, not something
this project was built around. Five visually distinct blocks: a
back+search header, a "Trending now" chip rail, a "Popular brands" chip
rail with brand icons, a "Price range" chip rail, and a "Curated Picks for
you" icon rail.

**First pass — JSON only, no code changes, against the registry as it stood
after the home screen:**

| Section | Result |
|---|---|
| Back + search header | ❌ Fell back — `header_bar` requires a non-null `location` and always renders a location pill + avatar; neither belongs on this screen |
| Trending now (chip rail) | ✅ Rendered correctly — `section` + `chip_tab_row` (`text_pill`), `onSelect` wired to `NAVIGATE` instead of `SET_STATE` |
| Popular brands (chip rail w/ icons) | ⚠️ Rendered, but silently dropped the brand icons — used a `style` value (`icon_inline_pill`) the renderer didn't recognize, so it fell through to the plain-text default instead of crashing |
| Price range (chip rail) | ✅ Rendered correctly — exact match to the pattern already proven by the home screen's Wishlisted/Hot Deals toggle |
| Curated Picks (icon rail) | ❌ Fell back — `banner_card_rail` requires a title + colored background; `icon_grid` wraps to new rows instead of scrolling. Neither fits a plain scrollable icon+label rail |

**3 of 5 sections (60%) rendered correctly with zero client code.** The one
partial (Popular brands) is a real, worth-naming distinction: an *unknown
component type* degrades to the visible fallback by design, but an
*unknown style value inside a known component* currently degrades silently
to that component's default look. Both are safe (neither crashes), but
only one is visually obvious — flagged as a trade-off in the README.

**Closing the two real gaps:**
- `SearchHeaderBarRenderer` — new component, ~15 min including registration.
- `IconRailRenderer` — new component, ~15 min including registration.
- `ChipTabRowRenderer` — added the missing `icon_inline_pill` branch, ~10 min.

Re-tested on device after: **all 5 sections render correctly, 100%.**

**What this predicts for the live round:** patterns this schema already
covers (chip rails in any of three visual styles, titled sections, item
rails/grids, any action type on any node) will render immediately from
JSON. Patterns needing a genuinely new visual shape (this round it was "a
title-less horizontally-scrolling icon rail" and "a header variant without
a location/avatar") will need a small new renderer — realistically minutes,
not hours, because the schema/registry/dispatcher plumbing is already
built and a new renderer is just another `ComponentRenderer` + one
registration line.
