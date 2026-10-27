---
layout: page
title: Lim En Xi's Project Portfolio Page
---

### Project: TAssist

TAssist is a desktop app for CS2040S teaching assistants to track tutorial attendance, class participation, and
assignment submissions. The TA types commands, and a JavaFX GUI shows the results. It is written in Java and evolved
from AddressBook-Level3.

Given below are my contributions to the project.

* **Code contributed**: [RepoSense link](https://nus-cs2103-ay2627-s1.github.io/tp-dashboard/?search=limenxi07&breakdown=true)

* **New Feature**: Built the TAssist desktop UI, replacing the AddressBook contact cards
  ([\#74](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/74)).
  * What it does: shows the active tutorial group's roster in a workspace with a group bar, screen navigation, a
    feedback row, and the command box. Screens not yet implemented are clearly labelled **Coming soon**.
  * Keyboard access: added `view SCREEN` so every screen can be opened by typing, F1 to open Help, and Escape to
    return to the command box, keeping the product CLI-first.
  * Highlights: the layout adapts to compact windows and long content, and a JavaFX smoke test checks every screen,
    both layouts, failed input, and feedback after navigation.

* **New Feature**: Added the student management commands `student add`, `student list`, and `student delete`
  ([\#91](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/91)).
  * Student IDs are unique within a group, ignoring case, while students with the same name are allowed. Deleting a
    student removes their records from that group only.
  * Rejected input never changes the roster or the saved file, which the integration tests check after restarting
    the app.

* **New Feature**: Added weekly participation scores with `participation set` and `participation list`
  ([\#92](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/92)).
  * Scores are whole numbers from 0 to 5. Listing a week tells a recorded zero apart from `Not recorded`, and setting
    the same week again replaces the score without touching other weeks, students, or groups.

* **New Feature**: Added command history: Up and Down in the command box recall earlier commands for editing
  ([\#108](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/108)).
  * Justification: TAs mark a whole group with near-identical commands, so recalling and editing the last one saves
    retyping. `CommandHistory` keeps the text being typed, so going past the newest command restores it.

* **Enhancements and bug fixes**:
  * Rejected fractional participation scores and grades in a hand-edited data file instead of silently truncating
    them, while still reading `5.0` as `5` ([\#137](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/137)).
  * Fixed assignment names that are equal ignoring case but hashed differently, which let a duplicate record silently
    replace a grade. Group and assignment names now share one case-insensitive hash
    ([\#152](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/152)).
  * Made `exit` reject extra input, like other commands without parameters
    ([\#138](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/138)), and made the unknown-student error name the
    student ID and group ([\#153](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/153)).
  * Removed duplicated code: shared active-group, student, and assignment lookups for 12 commands
    ([\#151](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/151)).

* **Documentation**:
  * User Guide:
    * Wrote the workspace overview, `view`, and command history descriptions
      ([\#74](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/74),
      [\#108](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/108)).
    * Documented the student and participation commands with their errors
      ([\#91](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/91),
      [\#92](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/92)), and updated the `help` section for the assignment
      commands ([\#101](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/101)).
  * Developer Guide:
    * Added the TAssist user stories ([\#39](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/39)).
    * Updated the UI component section and UI class diagram
      ([\#74](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/74),
      [\#108](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/108)), and wrote the implementation and manual-testing
      notes for the student and participation commands.
    * Added the `participation set` sequence diagram
      ([\#103](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/103)).
    * Rewrote the proposed undo/redo design for TAssist, with new class, sequence, and activity diagrams
      ([\#100](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/100)).

* **Team-based tasks**:
  * Alpha-tested the v1.4 JAR and reported 10 bugs (#115 to #124).
  * Opened follow-up tasks for code quality and authorship (#139, #141, #142) and a PPP task for each member (#143 to
    #147).
  * Tagged lines from a co-authored commit with `@@author` so that RepoSense credits them correctly
    ([\#140](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/140)).

* **Review contributions**:
  * PRs reviewed: [\#106](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/106),
    [\#107](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/107),
    [\#113](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/113),
    [\#114](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/114),
    [\#128](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/128),
    [\#129](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/129),
    [\#130](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/130),
    [\#131](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/131)
