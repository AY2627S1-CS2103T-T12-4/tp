---
layout: page
title: Arav's Project Portfolio Page
---

### Project: TAssist

TAssist is a desktop app for CS2040S teaching assistants to track tutorial attendance, class participation, and
assignment submissions.

Given below are my contributions to the project.

* **Code contributed**: [RepoSense link][reposense]

* **Features**:
  * `assignment submit` and `assignment grade`, with each student's assignment record and its storage, and each
    student's status and grade in `assignment list` ([#107][pr107]).
  * The weekly record that attendance and participation share, which checks the week and stores one value per
    student per week ([#89][pr89]).
  * Confirmation before commands that remove data, now used by `student delete`, `group delete` and
    `assignment delete` ([#106][pr106]).
  * The first version of the in-app help, where each command registers a help entry with its parser
    ([#78][pr78]).

* **Documentation**:
  * UG: the `assignment grade` section, the first version of the `assignment submit` section, the confirmation step
    of `student delete`, and the assignment records in "Editing the data file".
  * DG: the weekly record mechanism and where per-student records are stored, the assignment commands and their
    manual tests, and how the UI differs from the mock-up ([#90][pr90]).

* **Team tasks**: alpha testing (10 bugs reported, 4 of them fixed), and reviewing teammates' PRs
  ([#77][pr77], [#108][pr108], [#186][pr186], [#187][pr187]).

[reposense]: https://nus-cs2103-ay2627-s1.github.io/tp-dashboard/?search=arav31&breakdown=true
[pr77]: https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/77
[pr78]: https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/78
[pr89]: https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/89
[pr90]: https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/90
[pr106]: https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/106
[pr107]: https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/107
[pr108]: https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/108
[pr186]: https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/186
[pr187]: https://github.com/AY2627S1-CS2103T-T12-4/tp/pull/187
