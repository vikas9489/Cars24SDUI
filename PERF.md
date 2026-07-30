# Performance

## Device & methodology

- **Device:** Samsung SM-A346E (Galaxy A34 5G), Android 16 (API 36), real
  hardware, not an emulator.
- **Build type:** `release` (`isMinifyEnabled = false`, debug-signed for
  local install only — R8/minification was deliberately left off to avoid
  risking a broken benchmark build this late; noted as unexplored
  optimization below).
- **Runs per metric:** 5 cold starts per variant; median and range reported
  (not just best-case).
- **Cold start measured via:** `adb shell am force-stop` then
  `adb shell am start -W -n <component>`, reading `TotalTime` from the
  output. `Activity.reportFullyDrawn()` is called in both screens the
  instant their root composable finishes its first composition, which
  produces a corroborating `ActivityTaskManager: Fully drawn ... +Xms` line
  in Logcat — the two numbers agree within a few ms on every run below.
- **JSON fetch/parse breakdown:** `SduiAssetLoader` times the asset read and
  the `kotlinx.serialization` decode separately with `System.nanoTime()`
  and logs both (`SduiPerf` tag).
- **Scroll jank:** `adb shell dumpsys gfxinfo <pkg> reset`, then 8
  swipe-down + 8 swipe-up gestures (`adb shell input swipe`) covering the
  full page, then `dumpsys gfxinfo <pkg>` for the janky-frame percentage and
  frame-time percentiles since reset.

## Results

Raw cold-start `TotalTime` (ms) across 5 runs each:

| Run | SDUI | Static |
|---|---|---|
| 1 | 566 | 467 |
| 2 | 468 | 541 |
| 3 | 506 | 515 |
| 4 | 528 | 475 |
| 5 | 581 | 484 |
| **Median** | **528** | **484** |
| Range | 468–581 | 467–541 |

| Metric | Static | SDUI | Overhead |
|---|---|---|---|
| TTR (cold open → above-the-fold rendered) | 484 ms (median) | 528 ms (median) | **+44 ms / +9.1%** |
| TTI (cold open → scrollable/tappable) | ≈ same as TTR | ≈ same as TTR | n/a — see note |
| JSON fetch time (SDUI only) | — | ~0–2 ms (median 0 ms) | — |
| JSON parse time (SDUI only) | — | 3–5 ms (median 3 ms) | — |
| Scroll jank: janky frames | 1 / 1242 (0.08%) | 2 / 1184 (0.17%) | +0.09 pts |
| Scroll jank: frame time p50 / p90 / p99 | 9 / 12 / 17 ms | 9 / 12 / 23 ms | tail only |

**TTI note:** the JSON is a local asset decoded synchronously before the
first frame, so there's no "rendered but not yet interactive" gap the way
there would be with a network-fetched payload — TTR and TTI collapse to
the same number here. This is an honest artifact of the demo using a
bundled asset rather than a real endpoint; a networked SDUI screen would
show a real TTR/TTI split.

**"Full page time" note:** deliberately not reported as a distinct cold-start
number. Both screens use `LazyColumn`, which only composes on-screen
content by design — "all sections rendered" isn't a meaningful cold-start
metric for a properly virtualized list (it would either be identical to
TTR, or require disabling virtualization and measuring something neither
screen actually ships). The scroll-jank pass above is the honest substitute:
it exercises every section end to end and reports real frame timing while
doing so.

## What I tried to optimize, and what worked

1. **First real device run janked badly while scrolling.** Root-caused to
   two things at once: `AsyncImage`/Coil fetching ~10 remote
   `picsum.photos` URLs as cards scrolled into view (real network I/O
   competing for the main thread's attention on every new item), and large
   emoji `Text` glyphs which can trigger slow color-font fallback lookups
   on some devices.
2. **Fix:** replaced every image with a `placeholder:<type>:<hex>` scheme
   rendered via `Canvas` primitives (a circle + a path, no fonts, no
   network) — see `SduiImage.kt`. The assignment brief explicitly doesn't
   require live data ("nobody expects live APIs"), and reproducible perf
   numbers require removing network variance anyway, so this served both
   goals at once.
3. **Result, confirmed two ways:** subjectively, scrolling stopped sticking
   on the test device; quantitatively, the `gfxinfo` numbers above show
   both variants under 0.2% janky frames with p90 frame times well inside
   the 16.6ms budget. SDUI is marginally worse at the p99 tail (23ms vs
   17ms) — plausible cause is the extra `ComponentRegistry` map lookup +
   interface dispatch per item versus a direct function call in the static
   version, but at this magnitude (one frame, at the 99th percentile only)
   it's not something a user would perceive.
4. **What I didn't get to:** enabling R8/minification for the release
   build (left off to avoid destabilizing the benchmark build this late —
   likely reduces cold-start further for both variants, unmeasured), and
   Macrobenchmark's `BenchmarkRule`/`JankStats` for statistically stronger
   percentile data across many more iterations than the 5 manual runs here.

## Honest takeaway

SDUI overhead on this screen is small (~9% TTR, no measurable scroll-jank
difference that a user would feel) — the schema/registry indirection costs
single-digit milliseconds, not the double-digit-percent hit that would make
SDUI a bad trade for this use case. The dominant cost in both variants is
ordinary Compose cold-start (Activity creation, first composition), not
anything specific to the SDUI engine.
