# Performance

> Status: not yet measured. Numbers below are placeholders until the static
> twin and SDUI screen both exist. This file will be updated with real
> measurements, not filled in retroactively.

## Device & methodology

- Device: TODO (model, Android version)
- Build type: TODO (must be release, not debug)
- Runs per metric: TODO (recommend >= 5, report median + range)
- Cold start measured via: TODO (e.g. `adb shell am start -W`)
- TTR/TTI captured via: TODO (e.g. `reportFullyDrawn()`, Compose frame
  callbacks, Macrobenchmark)

## Results

| Metric | Static | SDUI | Overhead |
|---|---|---|---|
| TTR (cold open -> above-the-fold rendered) | TODO | TODO | TODO |
| TTI (cold open -> scrollable/tappable) | TODO | TODO | TODO |
| Full page time (all sections rendered) | TODO | TODO | TODO |
| JSON fetch time (SDUI only) | — | TODO | — |
| JSON parse time (SDUI only) | — | TODO | — |
| View-build time (SDUI only) | — | TODO | — |
| Dropped frames / jank while scrolling | TODO | TODO | TODO |

## What I tried to optimize, and what worked

TODO: measure -> optimize -> re-measure loop, documented honestly, including
things that did NOT help.
