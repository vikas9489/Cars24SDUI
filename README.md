# Cars24 SDUI Assignment

> Status: in progress. This README is being filled in incrementally as the
> system is built — see git history for the actual build order.

## 1. Screen chosen

The Cars24 **home/landing page** (Chandigarh location shown in the
reference screenshot). It clears the complexity bar easily:

- 9+ distinct section types: location/search header, icon tab row, gradient
  banners with sub-card rails, icon grids, a "View all" section wrapper,
  car card rail, car card grid, full-bleed promo banners, a location card,
  and a footer.
- Horizontal rail: "Trending new cars". Vertical grid: "Used cars you'll
  love".
- Interactive SDUI action, twice over: the Wishlisted/Hot Deals chip
  toggle swaps the entire car list content via a `SET_STATE` + `conditional`
  pair (not hardcoded), and every car card tap fires a `NAVIGATE` action
  with params.

It was picked specifically because it's dense enough to stress-test
generalization for the surprise-screen round, not because it was the
easiest thing to reproduce.

## 2. Setup

TODO: how to open/build/run (Android Studio version, JDK, minSdk/target).

## 3. Architecture overview

```
sdui/model/       SduiScreen, SduiNode, SduiAction — the generic wire model
sdui/registry/     ComponentRegistry (type string -> @Composable renderer)     [next]
sdui/components/   one file per renderer (header_bar, chip_tab_row, ...)       [next]
sdui/engine/       SduiViewModel (state map), ActionDispatcher, SduiScreen()   [next]
static/            hand-coded twin of the home screen, no SDUI                [next]
```

`SduiNode` is intentionally **not** a sealed class per component. It's:

```kotlin
data class SduiNode(
    val id: String,
    val type: String,          // looked up in ComponentRegistry at render time
    val props: JsonObject,     // raw — each renderer parses its own shape
    val children: List<SduiNode>,
    val actions: Map<String, SduiAction>
)
```

A registry maps `type` -> a `@Composable` renderer. An unrecognized `type`
just fails that map lookup and renders `UnknownComponentFallback` — it
never fails JSON deserialization, because deserialization doesn't know or
care what `type` values exist. That single design choice is what makes the
"unknown component fallback" requirement trivial instead of a special case.

Page-level interactivity is one small state map (`Map<String, JsonElement>`)
per screen, owned by `SduiViewModel`. Components bind to it via
`"{{state.key}}"` templates; a `chip_tab_row`'s `onSelect` action dispatches
`SET_STATE`, and a `conditional` node re-picks its child whenever the bound
state key changes. No custom expression language — just template
substitution and one switch-node type.

## 4. Schema design rationale

The schema optimizes for **forward compatibility over expressiveness**:

- Generic `type` + raw `props` (not a polymorphic sealed hierarchy) so new
  component types never require a client schema change, only a new
  renderer + registry entry.
- Actions are a flat envelope (`{ type, key, value, route, params,
  sheetId }`) with the same philosophy — an unrecognized action `type` is a
  no-op in `ActionDispatcher`, not a crash.
- Per-item taps inside a rail/grid (e.g. one car card) are embedded inline
  as an `onTap` field on that item's own JSON object, parsed with the same
  `SduiAction` shape by the renderer that owns the item — this keeps the
  top-level `SduiNode.actions` map reserved for node-level events (like a
  chip row's `onSelect`) instead of overloading it with per-item semantics.
- `conditional` is a normal `SduiNode` whose `children` are tagged with a
  `caseKey` prop, not a special JSON shape — so the parser stays a single
  uniform `SduiNode` decoder with zero type-specific branches.

## 5. Versioning story

`SduiScreen` carries `version` and `minSupportedVersion`. The client has a
compile-time `SDUI_SCHEMA_VERSION` constant. Before rendering, it checks
`payload.minSupportedVersion <= SDUI_SCHEMA_VERSION`; if the server payload
needs a newer engine than the installed app ships, the client shows an
"update the app" state instead of guessing at an unfamiliar schema shape.

Within a compatible schema version, most drift is absorbed for free:

- **New server, old client**: new section types the old client doesn't
  recognize degrade to `UnknownComponentFallback` — the page still renders,
  just missing that one section.
- **New client, old server**: new/renamed props are additive by
  convention (renderers read with defaults), so an old payload still
  renders correctly on a newer client.
- A breaking schema *shape* change (not just new component types) is the
  only case that bumps `SDUI_SCHEMA_VERSION` / `minSupportedVersion`.

Implementation here is the check + fallback UI; a full staged-rollout
story (percentage-based version gating server-side) is scoped out — see
Trade-offs below.

## 6. Trade-offs / what was cut

TODO: honest list of what was scoped out given the 72h timebox, and why.

## Related docs

- [PERF.md](PERF.md) — performance methodology and results
- [COVERAGE.md](COVERAGE.md) — schema coverage claim
- [AI_WORKFLOW.md](AI_WORKFLOW.md) — AI collaboration evidence
