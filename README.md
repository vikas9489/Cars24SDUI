# Cars24 SDUI Assignment

**Demo video:** https://drive.google.com/file/d/1QNzCw2Nz0_uXhzRlDnU6FobS0DlNWeRZ/view?usp=sharing

## 1. Screen chosen

The Cars24 **home/landing page** (Chandigarh location shown in the
reference screenshot). It clears the complexity bar easily:

- 9+ distinct section types: location/search header, icon tab row, gradient
  banners with sub-card rails, icon grids, a "View all" section wrapper,
  car card rail, car card grid, full-bleed promo banners, a location card,
  and a footer.
- Horizontal rail: "Trending new cars". Vertical grid: "Used cars you'll
  love".
- Interactive SDUI actions, three ways: the Wishlisted/Hot Deals chip
  toggle swaps the entire car list content via a `SET_STATE` + `conditional`
  pair (not hardcoded), every car card tap fires a `NAVIGATE` action with
  params, and "Call us now" on the showroom card fires `OPEN_SHEET` into a
  real `ModalBottomSheet`.

It was picked specifically because it's dense enough to stress-test
generalization for the surprise-screen round, not because it was the
easiest thing to reproduce.

## 2. Setup

- **Requirements:** Android Studio with AGP 9.1.0 / Kotlin 2.0.21 support,
  Android SDK with `compileSdk`/`targetSdk` 37 installed, `minSdk` 24. Any
  JDK 21 works (the JBR bundled with Android Studio is fine).
- **Open & run:** clone, open in Android Studio, let Gradle sync, run the
  `app` config on a device/emulator — this launches `MainActivity` (the
  real SDUI-driven home screen, rendered from `assets/sdui/home_screen.json`).
- **Static twin** (Part 2's perf baseline, no SDUI at all):
  ```
  adb shell am start -n com.vikas.cars24sdui/.staticscreen.StaticMainActivity
  ```
- **Coverage dry-run** (a second, unseen-by-this-project Cars24 screen
  rendered through the same engine — see COVERAGE.md):
  ```
  adb shell am start -n com.vikas.cars24sdui/.coverage.SearchScreenPreviewActivity
  ```
- **Unit tests:** `./gradlew testDebugUnitTest` — schema/parser
  consistency checks, `ActionDispatcher`, `TemplateResolver`.
- **Release build for benchmarking** (debug-signed for local install,
  not a distributable release — see PERF.md):
  ```
  ./gradlew assembleRelease
  adb install -r app/build/outputs/apk/release/app-release.apk
  ```

## 3. Architecture overview

```
sdui/model/       SduiScreen, SduiNode, SduiAction — the generic wire model
sdui/registry/     ComponentRegistry, ComponentRenderer, UnknownComponentFallback
sdui/components/   one file per renderer (header_bar, chip_tab_row, ...)
sdui/engine/       SduiViewModel (state map), ActionDispatcher, TemplateResolver, SduiScreenHost
staticscreen/      hand-coded twin of the home screen, no SDUI (Part 2 baseline)
coverage/          renders search_screen.json through the real engine (Part 3 proof)
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

`SduiViewModel` is a real `androidx.lifecycle.ViewModel` (obtained via the
standard `viewModel()` composable in `SduiScreenHost`), not a plain
`remember { mutableStateOf(...) }` — so the Wishlisted/Hot Deals selection
and any other SDUI-driven state survive a configuration change (e.g.
rotation) instead of resetting. `StaticHomeScreen` deliberately doesn't use
one: its two bits of local UI state have no complex data flow behind them,
so a ViewModel would be ceremony without benefit there — a real difference
between the two implementations, not an oversight.

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

- **No real navigation graph.** `NAVIGATE` actions surface as a Snackbar
  ("Navigate → route params") rather than pushing a real destination
  screen, except for one case wired for real: the home screen's search
  tap (`route: "search"`) launches `SearchScreenPreviewActivity` — the
  same screen used for the COVERAGE.md dry-run — via an
  `onNavigateRoute` interception hook on `SduiScreenHost`. Building a
  full multi-screen nav graph wasn't the point of the assignment — the
  action *firing correctly with the right route/params* is what's being
  tested, and one real end-to-end hop plus that verification is enough
  to prove the mechanism.
- **No R8/minification on the benchmarked release build.** Left off
  specifically to avoid destabilizing PERF.md's numbers this late in the
  timeline; noted in PERF.md as an unmeasured further optimization.
- **Perf measured manually (5 cold-start runs + `dumpsys gfxinfo`), not
  with Macrobenchmark.** Macrobenchmark's `BenchmarkRule`/`JankStats`
  would give statistically stronger percentile data across many more
  iterations; the manual approach is honest about being a smaller sample.
- **Unknown *component type* degrades loudly (visible fallback box);
  unknown *style value inside a known component* currently degrades
  silently** (found during the coverage dry-run — an unrecognized
  `chip_tab_row` style value falls through to the default look rather
  than a visible marker). Both are safe, but only one is easy to spot in
  a real rollout. Left as-is rather than adding a second fallback
  mechanism for every prop-level enum in the schema.
- **Single platform (Android/Compose).** The assignment explicitly frames
  a second platform as bonus, not baseline, and rewards depth over
  breadth on one stack — that's where the time went.
- **No automated visual regression testing.** Every renderer and bug fix
  was verified on a real physical device by hand (see AI_WORKFLOW.md —
  this is also what caught the two real bugs that unit tests missed), but
  there's no screenshot-diff CI step.
- **Versioning is a check + fallback, not a staged rollout.** The client
  correctly refuses to render a payload it can't understand and degrades
  gracefully on new/unknown types, but there's no percentage-based
  server-side version gating — out of scope for a client-side assignment.

## Related docs

- [PERF.md](PERF.md) — performance methodology and results
- [COVERAGE.md](COVERAGE.md) — schema coverage claim
- [AI_WORKFLOW.md](AI_WORKFLOW.md) — AI collaboration evidence
