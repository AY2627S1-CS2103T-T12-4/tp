---
layout: page
title: Testing guide
---

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<div markdown="span" class="alert alert-secondary">:link: **Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</div>

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`

--------------------------------------------------------------------------------------------------------------------

## Shared test fixtures for TAssist

Tests for the TAssist model use the builders and typical data in `seedu.address.testutil`, so that feature tests start from the same data instead of each creating their own.

* **Builders** create objects with sensible defaults, and each `withXYZ()` method changes one detail: `StudentBuilder` (default name `Alice Tan`, ID `A0123456X`), `GroupBuilder` (default name `T01`, no students) and `TAssistBuilder` (no groups, no active group).
* **Typical students** are in `TypicalStudents`: `ALICE`, `BEN`, `CARL`, `DANIEL` and `ELLE`. `FIONA` and `GEORGE` are not in any typical group, so tests can use them as new students.
* **Typical groups** are in `TypicalGroups`: `T01` (`ALICE`, `BEN`, `CARL`), `T02` (`DANIEL`, `ELLE`) and `T03` (no students). `NAME_T04` is not in the typical data, so tests can use it as a new or missing group.
* `TypicalGroups.getTypicalTAssist()` returns a `TAssist` with the three typical groups and no active group. Use `new TAssistBuilder(typicalTAssist).withActiveGroup(NAME_T01).build()` to start from a `TAssist` with an active group.

A `Student` is immutable, so `TypicalStudents` constants are shared. A `Group` and a `TAssist` are mutable, so `getT01()`, `getTypicalGroups()` and `getTypicalTAssist()` return new objects on every call. Never keep one in a static field of a test class, or one test can change the data of another.
