# AI Workflow

This is a real log of what happened in this build, not a reconstruction —
prompts, outputs, and outcomes below are drawn from the actual session
(Claude Code), commit by commit. Tool stack: Claude Code as the sole AI
tool, working directly against this repo with shell/adb access (git,
gradlew, and — notably — a real connected device for testing and
measurement, not just code generation).

## Tool stack

- **Claude Code**, with direct shell access to the repo, `git`, `gradlew`,
  and `adb` against a physical Android device (Samsung SM-A346E). This
  mattered more than expected — see Story 3 and the AI failure below,
  both of which were only catchable by actually running the app, not by
  reading code.
- **Context/rules files:** no repo-specific `CLAUDE.md` was written for
  this project; direction was given conversationally. In hindsight, a
  short rules file pinning the schema conventions (generic `type` +
  `props`, action envelope shape, `{{...}}` templating only) would have
  saved re-explaining them across the session — noted as a gap, not
  fixed retroactively.

## Prompt → outcome stories

### Story 1 — pushing the schema design toward generalization, not just this screen

- **Prompt:** given the assignment brief and a screenshot of the Cars24
  home screen, I asked for "complete workflow how we are going to follow
  what we will build first and flow what architecture we are using."
- **What AI produced:** its first pass toward an architecture leaned
  toward mapping literal, screen-specific components (a component for
  "Buy car banner," another for "Sell your car banner," etc.) before
  explicitly stopping itself mid-response and reasoning that this would
  score badly on the assignment's coverage requirement — a new screen's
  banners wouldn't match hardcoded per-section types. It proposed instead
  a set of ~9 generic primitives (`banner_card_rail`, `icon_grid`,
  `section`, etc.) driven by props, not names.
- **What I kept / rejected:** kept the generalized version. This wasn't
  something I had to catch and correct — the self-correction happened
  before I saw the naive version — but I did push on it by making the
  generalization requirement explicit up front. It paid off directly: the
  coverage dry-run (Story 3) reused the same `chip_tab_row` component for
  three structurally different purposes on a screen this project wasn't
  built for.

### Story 2 — scroll jank: the fix that only half-worked, and what fixed it for real

- **Prompt:** "whern i scroll the screen UI get janks and stuck" (after
  the first full render was up on-device).
- **What AI produced:** diagnosed two plausible causes — ~10 remote
  `picsum.photos` images loading over the network as cards scrolled into
  view, and large emoji `Text` glyphs (used for the image placeholders)
  that can trigger slow color-font fallback on some devices. First fix
  replaced the network images with a local `placeholder:type:hex` scheme,
  but *kept* emoji for the placeholder icon itself.
- **What I rejected / what happened next:** I re-tested and reported "its
  not getting stuck" only after a *second* round — the emoji-based first
  fix wasn't reported as fixed; I asked to "makeit smooth" again, which
  prompted rewriting the placeholder to pure `Canvas` primitives (a
  circle + a path, no fonts/glyphs at all) instead of emoji. That version
  is what actually resolved it, confirmed both subjectively (your report)
  and later quantitatively (`gfxinfo`: <0.2% janky frames, see PERF.md).
  The lesson embedded in the code comment on `SduiImage.kt` is specifically
  about not trusting a partial fix without re-measuring.

### Story 3 — proving coverage instead of estimating it

- **Prompt:** asked to explain what `COVERAGE.md`'s "honest coverage
  claim" actually requires, then agreed to a real dry-run rather than a
  guessed percentage.
- **What AI produced:** wrote `search_screen.json` against the existing
  registry with no changes made in advance, rendered it through a
  dedicated `SearchScreenPreviewActivity`, and reported the real result:
  3 of 5 sections rendered correctly with zero code (reusing
  `chip_tab_row`, including firing a `NAVIGATE` action from a component
  originally designed for `SET_STATE` — the generic action envelope held
  up under a use case it wasn't specifically designed for), 1 rendered
  but silently dropped an icon (an unrecognized `style` value falling
  through to a default rather than the type-level fallback), and 2 fell
  back to the unknown-component placeholder.
- **What I kept:** all of it, then asked to close the two real gaps
  rather than just document them, since the rubric explicitly rewards
  "fast, calm extension when code is needed." Two new renderers
  (`SearchHeaderBarRenderer`, `IconRailRenderer`) plus one new style
  branch took about 40 minutes combined, re-verified on-device
  afterward. The COVERAGE.md percentage is measured, not asserted.

## One AI failure

- **What went wrong:** `TemplateResolver.kt`'s regex patterns for
  `{{state.x}}`/`{{eventKey}}` substitution used an unescaped closing
  `}}` — e.g. `Regex("""^\{\{\s*state\.([a-zA-Z0-9_]+)\s*}}$""")`. This
  compiled fine and passed every unit test locally. The moment a chip was
  tapped on the real device, the app crashed with
  `PatternSyntaxException: Syntax error in regexp pattern`. Android's
  on-device ICU-backed regex engine rejects a bare `}` that desktop
  `java.util.regex` (and the host JVM the unit tests run on) accepts
  without complaint — a genuine platform-API gotcha, not something either
  the code review or the test suite surfaced.
- **How it was caught:** only by running on a real device and tapping the
  interactive element — you reported "getting crashed on each click"
  with the full stack trace, which pointed straight at
  `TemplateResolver.kt:7`'s `Regex.<clinit>` failing at class-init time.
- **What changed:** escaped the closing braces (`\}\}`) in both regex
  patterns, and added a comment on the fix explaining *why* a passing
  test suite didn't catch it — the host-JVM-vs-on-device-ICU regex engine
  difference is exactly the kind of thing that looks fine everywhere
  except where it actually runs. This is also why every renderer batch
  in this repo was verified on-device before being called done, not just
  unit-tested.

## Verification strategy for AI-generated code

- **Unit tests for anything with a decidable right answer:** schema
  decoding + cross-reference consistency (component/action-type
  allowlists, chip↔conditional state-key coverage, content spot-checks
  against the reference screenshot — `SduiScreenParsingTest.kt`), and
  pure logic (`TemplateResolverTest.kt`, `ActionDispatcherTest.kt`).
  `testOptions.unitTests.isReturnDefaultValues = true` was added
  specifically so Android stub calls (`Log.w`) don't crash host-JVM tests.
- **On-device verification for everything with a runtime/visual
  component**, not emulator-only, not "it compiled." Every renderer
  batch, every bug fix, and the perf numbers themselves were checked
  against a real physical device — this is precisely what caught the
  regex crash and confirmed (twice, after one false start) that the
  scroll-jank fix actually worked.
- **Real measurement over estimation for anything perf- or
  coverage-related:** PERF.md's numbers come from `adb shell am start -W`
  and `dumpsys gfxinfo`, not a guess; COVERAGE.md's percentage comes from
  an actual second screen rendered through the real engine, not a
  reasoned estimate of what "should" work.
