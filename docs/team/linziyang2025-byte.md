---
layout: page
title: Lin Ziyang's Project Portfolio Page
---

### Project: TAssist

TAssist is a desktop app for CS2040S teaching assistants to track tutorial attendance, class participation, and
assignment submissions. The TA types commands, and a JavaFX GUI shows the results. Each tutorial group keeps its own
roster and weekly records, saved to a local file after every change, so a TA can switch groups between tutorials
without losing anything.

Given below are my contributions to the project.

* **Code contributed**: [RepoSense link](https://nus-cs2103-ay2627-s1.github.io/tp-dashboard/?search=linziyang2025-byte&breakdown=true)

* **New Feature**: Added `group export`, which saves the active group's records as a CSV file
  ([\#114](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/114)).
  * What it does: writes every student's weekly attendance and participation in the active group to
    `exports/<group>-records.csv`, so the TA can hand marks to the course coordinator without retyping them.
  * Highlights: the file follows RFC 4180 and starts with a UTF-8 byte order mark, so a spreadsheet opens it directly
    and shows names with accents or apostrophes correctly. A value that starts like a formula, such as the name
    `-Ann`, is written as text, so opening the file never runs it. `Lab 1` and `Lab-1` get separate files, and a
    failed export leaves any earlier file untouched.

* **New Feature**: Built the TAssist domain model and put it behind the `Model`
  ([\#75](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/75),
  [\#82](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/82)).
  * What it does: `TAssist`, `Group`, `GroupName`, `UniqueGroupList` and `Student` hold tutorial groups and their
    students, and `ModelManager` adds, removes and switches groups and students.
  * Highlights: the active group and its students are exposed as observable values, so every part of the UI follows
    whichever group is active without extra wiring.

* **New Feature**: Showed the active group and its students in the GUI
  ([\#83](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/83)).
  * Added the Students screen and the group chips in the top bar, with the active group's name on the Students panel.
    The feedback box grows to fit its message, and help appears only on the Help screen.

* **Enhancements**:
  * Completed the in-app help for every MVP command, listed topics and commands in the order a TA uses them, and
    made the User Guide and help agree. A test types every help example into a fresh TAssist to check that each one
    works ([\#96](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/96)).
  * Added a TAssist app icon for the window, and for the macOS Dock where Java supports it
    ([\#97](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/97)).
  * Fixed a crash at startup in a folder TAssist cannot write to: it now opens and logs to the console only
    ([\#113](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/113)).
  * Enabled assertions when running the app during development
    ([\#105](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/105)).

* **Documentation**:
  * User Guide:
    * Wrote the `group export` section and described the Students screen and workspace
      ([\#114](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/114),
      [\#83](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/83)).
    * Matched every command sample to the in-app help, and added notes on launching the JAR from a terminal and on
      read-only folders ([\#96](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/96),
      [\#113](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/113)).
  * Developer Guide:
    * Wrote the use cases and non-functional requirements
      ([\#40](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/40)).
    * Described the TAssist model, how the UI observes the active group, and how help is ordered and checked. Added
      the model class diagram, the active-group sequence diagram, and the help class diagram, and updated the UI class
      diagram for the Students screen ([\#75](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/75),
      [\#82](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/82),
      [\#83](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/83),
      [\#96](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/96)).
    * Explained the `group export` design with a sequence diagram, and added the end-to-end acceptance walkthrough
      to the manual-testing appendix ([\#114](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/114),
      [\#113](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/113)).

* **Team-based tasks**:
  * Updated the README and the first UI image for TAssist
    ([\#41](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/41)).
  * Ran the end-to-end acceptance pass across all features, and added an automated test of a TA's whole session:
    record a tutorial in every feature, restart, and find everything back
    ([\#113](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/113)).
  * Alpha-tested the product and reported 5 bugs
    ([\#157](https://github.com/AY2627S1-CS2103T-T12-4/tp/issues/157),
    [\#158](https://github.com/AY2627S1-CS2103T-T12-4/tp/issues/158),
    [\#159](https://github.com/AY2627S1-CS2103T-T12-4/tp/issues/159),
    [\#160](https://github.com/AY2627S1-CS2103T-T12-4/tp/issues/160),
    [\#161](https://github.com/AY2627S1-CS2103T-T12-4/tp/issues/161)).

* **Review contributions**:
  * PRs reviewed: [\#37](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/37),
    [\#74](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/74),
    [\#76](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/76),
    [\#84](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/84),
    [\#88](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/88),
    [\#106](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/106),
    [\#108](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/108),
    [\#110](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/110),
    [\#111](https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/111)
