# Page Design Prototype

═ ╔ ╤ ╗  ╚ ╧ ╝ ╠ ╬ ╣

╭ ╮ ╰ ╯ ─ │ ├ ┤ ┬ ┴ ┼

## Initial Proposal

┌────────────────┬───────────────────────────────────────────┐
│  AURORA STORE  │ Home / Outerwear / Coats                  │
├────────────────┼───────────────────────────────────────────┤
│ v Outerwear 12 │  COATS  (12 items)                        │
│   * Coats    4 │  ┌──────────────┬───────────┬──────────┐  │
│   - Jackets  8 │  │ Item         │ Price     │ Stock    │  │
│ > Bags       7 │  ├──────────────┼───────────┼──────────┤  │
│ v Home       2 │  │ Alpaca Coat  │ $189.00   │ 3 left   │  │
│   - Candles  2 │  │ Wool Coat    │ $129.00   │ 4 left   │  │
│                │  │ Rain Parka   │ $95.00    │ 9 left   │  │
│                │  └──────────────┴───────────┴──────────┘  │
│                │   Showing 1-3 of 12                       │
├ FILTERS ───────┤                                           │
│  [x] In stock  │                                           │
│  [ ] On sale   │                                           │
│                ├── 1-12 of 42 ────── goto: ____   [ go ] ──┤
│                │ [ <- ]   1  [ 2 ]  3  4   [ -> ]          │ 
└────────────────┴───────────────────────────────────────────┘

## Proposal Iteration 001 

┌────────────────┬───────────────────────────────────────────┐
│  AURORA STORE  │ [ Home ] / [ Outerwear ] / [ Coats ]      │
├────────────────┼───────────────────────────────────────────┤
│ v Outerwear 12 │   COATS (12 items)                        │
│   * Coats    4 │  ┌──────────────┬───────────┬──────────┐  │
│   - Jackets  8 │  │ Item         │ Price     │ Stock    │  │
│ > Bags       7 │  ├──────────────┼───────────┼──────────┤  │
│ v Home       2 │  │«Alpaca Coat» │ $189.00   │ 3 left   │  │
│   - Candles  2 │  │ Wool Coat    │ $129.00   │ 4 left   │  │
│                │  │ Rain Parka   │ $95.00    │ 9 left   │  │
│                │  └──────────────┴───────────┴──────────┘  │
│                │   Showing 1-3 of 12                       │
├ FILTERS ───────┤                                           │
│  [x] In stock  │                                           │
│  [ ] On sale   ├── page 2 / 4 ─────────────────────────────┤
│  [ clear ]     │   [ <- ]   1 ‹2› 3 4   [ -> ]             │
├────────────────┴───────────────────────────────────────────┤
│  HOME      ↑↓ browse · ENTER open · Tab zones · / search   │
└────────────────────────────────────────────────────────────┘

### Proposal 001 considerations and standarizations accross the project

#### Key 001#001
[] Denotes Actionable
Denotes Actionable: Component which may be interacted with however 
but not focused to be interactived with immediatly.
« » Denotes Focused Actionable Component, additionally implementation 
should ensure this component blinks approximately every 3/8 of a second. 
Going from visible to invisible in 3/8s, and invisible to visible in 3/8s.  
Interactable components which can be actionated via |Enter|
v, >, -, * Are inherently considered as Actionable without the explicit ||.  
‹› Denotes selection marker when the [Interactive Area] was previously on focus. 
for the inherently actionable symbols ‹› cannot be used. It is not neccesary 
for user interaction to happen for ‹› to appear, as by default every [Interactive Area]
should have a default option which is selected first.

#### Key 001#002
Each independent enclosed area with pipes (─ │ ├ ┤ ┬ ┴ ┼) can be considered either interactable or 
non-interatable. Interactable areas can be focused, non-interactable areas cannot be 
focus for user interaction are they should contain none.

note: Something is consider *Enclosed* if it has at least 4 right conner at 90deg angles from 
each other. (─ │ ├ ┤ ┬ ┴ ┼) nested within enclosure are not considered their own focus area 
and would be handle  differently.



#### Key 001#003
Perma Size Window Component, such as the header, or keyboard tool 
should not grow in any form unless the entire program growths.

