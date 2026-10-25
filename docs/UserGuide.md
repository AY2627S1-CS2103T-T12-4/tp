---
layout: page
title: User Guide
---

TAssist is a **desktop application for CS2040S teaching assistants, optimized for typing commands**.
The Students screen shows the students in your active tutorial group.
You can create, list, switch, rename, and delete tutorial groups, add, list, and delete their students, record weekly attendance,
and record and list weekly participation scores. Commands for assignments are **coming soon**.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from the [team releases page](https://github.com/AY2627S1-CS2103T-T12-4/tp/releases), when available.
   If no release is available yet, build the project with `./gradlew shadowJar` and use `build/libs/tassist.jar`.

1. Copy the file to the folder you want to use as the _home folder_ for TAssist.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar tassist.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. On the first launch, TAssist starts with no data.<br>
   ![TAssist on the first launch, with no tutorial groups yet](images/TAssistUi.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the command reference.<br>
   Some example commands you can try:

   * `help` : Opens the command reference.

   * `group add g/T01` : Creates tutorial group T01 and makes it active.

   * `view attendance` : Opens the Attendance screen.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Using the workspace

The bar at the top shows the **tutorial groups** next to the TAssist name, with the active group highlighted, and
the **screens** below it. **Students**, **Attendance**, **Participation**, and **Assignments** are on the left;
**Groups**, **Help**, and **Storage** are on the right. A small orange dot marks a screen that is still **coming soon**.
Hover over a screen, or over a shortened group name, to see more. The tooltip of a screen names the command that does
the same, such as `view attendance`.

The implemented UI keeps the mock-up's command-first workspace, but uses a light group bar and a separate screen
navigation row in place of the mock-up's dark header and separate group strip. The [original UI mock-up](images/UiMockup.png)
is included for reference.

![TAssist displaying the Students screen for tutorial group T01](images/Ui.png)

Use the `view` command to open the same screens by typing. The command box and feedback remain visible on every screen.
Press **Escape** to return focus to the command box, or **F1** to open Help. Clicking a screen keeps your cursor in
the command box.

* **Students** is titled with the name of the active tutorial group and the number of students in it, and lists the
  students with their names and student IDs. It updates as soon as the active group or its students change. When there are no students to list, the screen says whether no group is
  active or the active group has no students yet. Hover over a shortened name to read it in full, and scroll to see
  more students.
* **Groups** shows a planned table with a **Coming soon** label. Use `group list` to see your groups in the feedback
  box, and `group add` or `group use` to change the active group shown in the top bar.
* **Attendance** shows a planned table with a **Coming soon** label and unavailable week controls.
  Use `attendance mark` and `attendance list`; the Attendance screen remains a preview.
* **Participation** shows a planned table with a **Coming soon** label and unavailable week controls.
  Use `participation set` and `participation list` to record and view real scores in the feedback box.
* **Assignments** shows a planned layout with a **Coming soon** label.
  It contains no simulated records, and assignment commands are not yet supported.
* **Help** provides an offline reference for currently supported commands. The `help` command opens it.
* **Storage** shows the configured local JSON file and explains automatic saving.

The feedback row below the screen tells you whether your last command worked: a check mark means it did, and a cross
on a red background means it failed. It grows to fit the message, up to six lines; only longer messages need
scrolling. The failed input stays in the
command box so you can correct it. A command stays on the current screen unless it opens another one, as `view` and
`help` do. Screen contents can be scrolled. Smaller windows use a compact layout.
The footer shows the local file path, reports successful saves or command failures, and reminds you of the F1 and
Escape shortcuts.
Opening a screen with `view` does not save or change data.

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `view SCREEN`, replace `SCREEN` with a screen name such as `attendance`.

* Items in square brackets are optional.<br>
  For example, `help [TOPIC]` can be used as `help student` or as `help`.

* `exit` ignores extra input: `exit 123` is interpreted as `exit`. `group list` and `student list` reject extra input.

* Leading and trailing spaces around a command are ignored. Unknown commands identify the unrecognized keyword
  and suggest entering `help`. They do not change or save your data. Assignment
  commands remain **Coming soon**.

* Command keywords and prefixes use lowercase letters. Group names ignore case and extra spaces.
  Separate command words and parameters with spaces, and specify `g/` exactly once for `group add` and `group use`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Opens the **Help** screen, which lists the format and examples of every available command.
The feedback box confirms with `Showing the command reference.`

Format: `help [TOPIC]`

* `TOPIC` is one of `group`, `student`, `attendance`, `participation`, or `assignment`. It is not case-sensitive.
* With a `TOPIC`, the Help screen opens at that topic's commands, for example `Showing student commands.`
  If no command of that topic exists yet, TAssist says so and opens the Help screen from the top.
* An unknown topic shows `Unknown help topic. Available topics: group, student, attendance, participation, assignment.`
  and does not change any data.

Examples:
* `help`
* `help student`


### Opening a screen: `view`

Format: `view SCREEN`

`SCREEN` is one of `students`, `groups`, `attendance`, `participation`, `assignments`, `help`, or `storage`.
Screen names ignore case. Specify exactly one screen name. Opening a screen does not change any data.
An unknown screen name reports `Unknown screen` and lists the valid names. Missing or multiple screen names report
an invalid command format and show the expected syntax.

Examples: `view students`, `view attendance`, `view storage`.

### Creating a tutorial group: `group add`

Creates an empty tutorial group and makes it active, including when another group is already active.
Existing groups and their students remain unchanged.

Format: `group add g/GROUP_NAME`

* Group names contain 1–60 letters, digits, spaces, or hyphens, with at least one letter or digit.
* Leading and trailing spaces are removed, and repeated spaces inside a name count as one space.
  The length limit applies after this normalization.
* Names ignore case when matching, but keep the capitalization entered for display. `T01` and `t01` are duplicates;
  `Tutorial 1` and ` tutorial   1 ` are also duplicates.

Example: `group add g/T01`

```text
Created tutorial group: T01.
Active group is now T01.
```

### Listing tutorial groups: `group list`

Lists all tutorial groups in creation order, with numbering and the active group's name.
It does not change which group is active or any students.

Format: `group list`

For example, after creating T01 and T02:

```text
1. T01
2. T02
Active group: T02
```

With no groups, the feedback is `No tutorial groups exist. Create one using: group add g/GROUP_NAME`.
If groups were loaded from a file without an active selection, the final line is `Active group: None`.
Extra input, such as `group list g/T01`, is rejected. Long lists can be scrolled in the feedback box.

### Switching tutorial groups: `group use`

Makes an existing tutorial group active. The highlighted group in the top bar and the Students screen update
immediately. Each group's students and records stay separate. Selecting the already active group also succeeds.

Format: `group use g/GROUP_NAME`

Example: `group use g/t01` switches to a group saved as T01 and shows `Switched active group to T01.`

### Renaming a tutorial group: `group rename`

Changes the name of an existing tutorial group, for example to fix a typo. The group keeps its students, records,
position in the list, and active status.

Format: `group rename g/GROUP_NAME n/NEW_NAME`

* The new name follows the same rules as in `group add`. It must not match another group, ignoring case.
* Changing only the capitalization or spacing of a group's own name, such as `T01` to `t01`, is allowed.

Example: `group rename g/T 01 n/T01` shows `Renamed tutorial group T 01 to T01.`

### Deleting a tutorial group: `group delete`

Deletes a tutorial group together with all its students and their records. **This cannot be undone**, so check the
name with `group list` first. Other groups are unchanged. If the deleted group was active, no group is active
afterwards; select one with `group use`.

Format: `group delete g/GROUP_NAME`

Example: `group delete g/T 01` shows `Deleted tutorial group T 01 and its 0 student(s).`

#### Tutorial-group command errors

Rejected group commands do not change the groups, the active selection, or the saved data file.

Problem | Feedback and correction
--------|------------------------
Duplicate name | `A tutorial group named T01 already exists.` Use a different name, or switch with `group use g/T01`. This also applies to `group rename` when `n/` names another group.
Unknown group | `Tutorial group T03 does not exist.` Check `group list`, or create it with `group add g/T03`. `group use`, `group rename`, and `group delete` all report it.
Missing or blank name | `Missing group name. Usage: group add g/GROUP_NAME` (or the corresponding `group use` or `group delete` usage). Supply a name after `g/`. `group rename` needs both `g/` and `n/`.
Invalid name | `Invalid group name. Use 1-60 letters, digits, spaces, or hyphens.` Follow the naming rules above.
Repeated or unknown prefix | The feedback identifies the repeated or unknown prefix. Use `g/` exactly once (and `n/` once for `group rename`).
Missing or unknown subcommand | The feedback lists the valid subcommands: `add`, `delete`, `list`, `rename`, and `use`.

### Recording or correcting attendance: `attendance mark`

Records whether a student in the active tutorial group attended a teaching week.
The student must already be in that group. Add one with `student add` if needed.

Format: `attendance mark id/STUDENT_ID w/WEEK s/STATUS`

* `WEEK` is a whole number from 1 to 13. `STATUS` is `present` or `absent`, ignoring case.
* Student IDs ignore case. Supply each prefix exactly once, in any order, separated by spaces.
* Repeating a mark for the same student and week replaces the earlier mark, even if the status is the same.
  Other students, weeks, and participation records are preserved.

For Alice Tan in active group T01, `attendance mark id/A0123456X w/3 s/present` shows
`Recorded Alice Tan as present for Week 3.` Re-entering it with `s/absent` shows
`Updated Alice Tan's attendance for Week 3 to absent.`

Invalid commands do not change or save data. An invalid week shows `Invalid week. Enter a number from 1 to 13.`;
an invalid status shows `Invalid attendance status. Use present or absent.` An unknown student shows
`Student does not exist in the active group.` With no active group, use `group add g/GROUP_NAME` or
`group use g/GROUP_NAME` first. Missing, repeated, or unknown prefixes are identified in the feedback.

### Listing weekly attendance: `attendance list`

Lists attendance for the active tutorial group in student order, without changing any records.

Format: `attendance list w/WEEK`

For example, `attendance list w/3` shows the following in the scrollable feedback box:

```text
Attendance for T01, Week 3:
Alice Tan | A0123456X | Present
Ben Lim | A0234567Y | Not recorded
```

Each student appears with `Present`, `Absent`, or `Not recorded`. If no attendance has been recorded for that
week, including when the group has no students, the feedback is
`No attendance records have been recorded for Week 3.` Records in other groups or weeks are not included.
Use `group use g/GROUP_NAME` to choose another group before listing it.

Specify `w/` exactly once with a whole number from 1 to 13. Missing, repeated, or extra parameters are rejected;
invalid weeks and no active group have the same errors as `attendance mark`.

### Recording participation: `participation set`

Records a score for one student in the active tutorial group. Select a group with `group use` first.

Format: `participation set id/STUDENT_ID w/WEEK s/SCORE`

* `WEEK` is a whole number from 1 to 13. `SCORE` is a whole number from 0 to 5, including zero.
* Student IDs ignore case. The ID must belong to the active group.
* Supply each prefix exactly once, in any order, with spaces between parameters.
* Setting the same student and week again replaces the score, including when the value is unchanged.
  Other weeks, other students, and other groups are unaffected.

Example: `participation set id/A0123456X w/3 s/2` shows
`Recorded participation score 2 for Alice Tan in Week 3.`
Then `participation set id/A0123456X w/3 s/4` shows
`Updated Alice Tan's participation score for Week 3 to 4.`

### Listing participation: `participation list`

Lists every student in the active group in roster order, with their score for the requested week.
Scores appear in the feedback box; scroll it for long lists. The Participation table remains a preview.

Format: `participation list w/WEEK`

Example: `participation list w/3`

```text
Participation for T01, Week 3:
Alice Tan | A0123456X | 2
Ben Lim | A0234567Y | Not recorded
```

Students without a score show `Not recorded`, even if no scores have been entered that week.
A recorded zero shows `0`. An empty group shows `No students in T01.`
Listing does not change scores. Successful score changes are saved automatically and restored when you reopen TAssist.

#### Participation command errors

Rejected input leaves the existing scores and saved data file unchanged.

Problem | Feedback and correction
--------|------------------------
No active group | `No active tutorial group. Create or select a group first.` Use `group add` or `group use`.
Unknown student | `Student does not exist in the active group.` Check the ID and selected group.
Invalid or blank week | `Invalid week. Enter a number from 1 to 13.` Supply digits only, without a sign or decimal point.
Invalid or blank score | `Invalid participation score. Enter a whole number from 0 to 5.` Supply one of 0, 1, 2, 3, 4, or 5.
Missing parameter | The feedback names the missing prefixes. Include `id/`, `w/`, and `s/` for setting; `w/` for listing.
Repeated or unknown prefix | The feedback names the prefix. Use only the documented prefixes, once each.

### Adding a student: `student add`

Adds a student to the active group and updates the Students screen immediately.
Create or select a group first with `group add` or `group use`.

Format: `student add n/NAME id/STUDENT_ID`

* Names contain 1–60 letters (including accented letters), spaces, apostrophes, or hyphens, with at least one letter.
  Leading and trailing spaces are removed and repeated spaces are collapsed; the limit applies after normalization.
* IDs contain 1–20 letters and digits, including at least one digit. IDs ignore case and are displayed in uppercase.
* An ID must be unique within the active group. The same ID may occur in another group.
  Students with the same name and different IDs are allowed.
* Supply each prefix exactly once, in either order, with a space before each parameter.

Example: `student add n/Alice Tan id/A0123456X` shows `Added Alice Tan to T01.` when T01 is active.

### Listing students: `student list`

Lists the active group's students in roster order in the feedback box. It takes no parameters.

Format: `student list`

```text
Students in T01:
1. Alice Tan | A0123456X
2. Ben Lim | A0234567Y
```

An empty group shows `No students in T01. Add one using: student add n/NAME id/STUDENT_ID`.
Long rosters can be scrolled in the feedback box or viewed on the Students screen with `view students`.

### Deleting a student: `student delete`

Removes the student and their weekly records from the active group immediately.
The same student in another group is unaffected. Remaining students are renumbered without changing their order.

Format: `student delete id/STUDENT_ID`

Example: `student delete id/a0123456x` shows `Removed Alice Tan from T01.`

#### Student command errors

Rejected input leaves the roster and saved file unchanged.

Problem | Feedback and correction
--------|------------------------
No active group | `No active tutorial group. Create or select a group first.` Use `group add` or `group use`.
Duplicate ID | `Student ID A0123456X already exists in T01.` Check `student list` before adding.
Unknown ID when deleting | `Student ID A0123456X does not exist in T01.` Check the ID and active group.
Missing parameter | `Missing required parameter. Usage: student add n/NAME id/STUDENT_ID` (or the deletion format). Supply all required prefixes.
Invalid or blank value | The feedback identifies the invalid student name or ID and gives its rules.
Repeated or unknown prefix | The feedback identifies the prefix. Use only the command's documented prefixes, once each.
Extra input to `student list` | `This command takes no parameters. Remove the extra input.`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

TAssist saves your tutorial groups, their students, attendance, and participation scores, and the active group after successful commands, except `view`,
which only changes the visible screen. You do not need to save manually. A failed command does not indicate a
successful save; read the feedback.

### Editing the data file

TAssist data is saved as `data/tassist.json` in the home folder, which is the folder containing the JAR file.
Preferences (`preferences.json`) and the log file are saved in that same folder. TAssist does not read or write
files outside it, including when the JAR is started from another directory.
The Storage screen shows the absolute path. Close the app before editing this JSON file manually.
If the file is missing, TAssist starts with no data and creates the file after the first successful command
other than `view`.

The file looks like this:

```json
{
  "activeGroup" : "T01",
  "groups" : [ {
    "name" : "T01",
    "students" : [ {
      "name" : "Alice Tan",
      "studentId" : "A0123456X",
      "attendance" : { "3" : "PRESENT" },
      "participation" : { "3" : 2 }
    } ]
  } ]
}
```

* `activeGroup` is the name of the active group, or `null` if no group is active. It must match the name of one of
  the groups, ignoring case.
* Each group needs a `name`. No two groups may have the same name, ignoring case.
* Each student needs a `name` and a `studentId`. No two students in a group may have the same student ID, ignoring
  case.
* A student's optional `participation` map stores week numbers as keys and scores as values, such as `"3" : 2`.
  Weeks must be 1–13 and scores must be whole numbers from 0 to 5. A week or score outside that range makes the
  file invalid. An omitted map means no scores have been recorded.
* Group names, student names, and student IDs follow the same rules as when you type them in a command.
* `attendance` maps week numbers (`"1"` through `"13"`) to `"PRESENT"` or `"ABSENT"`.
  Status values ignore case and surrounding spaces when loaded. An omitted attendance map means no attendance
  records for that student. Invalid weeks or statuses make the file invalid.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, TAssist starts with no data at the next run, and the terminal and the log file `tassist.log.0` say why the file could not be read. The invalid file remains on disk until a successful command other than `view` saves over it, so close TAssist and fix the file before entering such a command. We recommend backing up the file before editing it.
</div>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer, then copy `data/tassist.json` from your previous TAssist home folder into the `data` folder of the new home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Help** | `help [TOPIC]`<br> e.g., `help student`
**View** | `view SCREEN`<br> e.g., `view attendance`
**Add group** | `group add g/GROUP_NAME`<br> e.g., `group add g/T01`
**List groups** | `group list`
**Switch group** | `group use g/GROUP_NAME`<br> e.g., `group use g/T02`
**Rename group** | `group rename g/GROUP_NAME n/NEW_NAME`<br> e.g., `group rename g/T 01 n/T01`
**Delete group** | `group delete g/GROUP_NAME`<br> e.g., `group delete g/T02`
**Add student** | `student add n/NAME id/STUDENT_ID`<br> e.g., `student add n/Alice Tan id/A0123456X`
**List students** | `student list`
**Delete student** | `student delete id/STUDENT_ID`<br> e.g., `student delete id/A0123456X`
**Mark attendance** | `attendance mark id/STUDENT_ID w/WEEK s/STATUS`<br> e.g., `attendance mark id/A0123456X w/3 s/present`
**List attendance** | `attendance list w/WEEK`<br> e.g., `attendance list w/3`
**Set participation** | `participation set id/STUDENT_ID w/WEEK s/SCORE`<br> e.g., `participation set id/A0123456X w/3 s/2`
**List participation** | `participation list w/WEEK`<br> e.g., `participation list w/3`
**Exit** | `exit`
