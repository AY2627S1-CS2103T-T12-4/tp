---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of contacts
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Manage contacts faster than with a typical mouse-driven GUI application.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                     | So that I can…​                                                        |
| -------- | ------------------------------------------ | ------------------------------ | ---------------------------------------------------------------------- |
| `* * *`  | new user                                   | see usage instructions         | refer to instructions when I forget how to use the App                 |
| `* * *`  | user                                       | add a new person               |                                                                        |
| `* * *`  | user                                       | delete a person                | remove entries that I no longer need                                   |
| `* * *`  | user                                       | find a person by name          | locate details of persons without having to go through the entire list |
| `* *`    | user                                       | hide private contact details   | minimize chance of someone else seeing them by accident                |
| `*`      | user with many persons in the address book | sort persons by name           | locate a person easily                                                 |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is `TAssist` and the **Actor** is the `TA`, unless specified otherwise)

The following extensions apply to every use case that changes or views records of a tutorial group, and are not repeated below:

* \*a. There is no active tutorial group.
    * \*a1. TAssist shows an error message asking the TA to create or select a group first.

      Use case ends.

* \*b. The TA's command is missing a required parameter, contains an unknown prefix, or repeats a parameter.
    * \*b1. TAssist shows an error message with the correct command format. No data is changed.

      Use case resumes from the step at which the command was entered.

**Use case: UC01 - Set up a tutorial group**

**MSS**

1.  TA requests to create a tutorial group with a given name.
2.  TAssist creates the group, makes it the active group, and shows a confirmation.
3.  TA requests to list all tutorial groups.
4.  TAssist shows all tutorial groups and indicates the active group.

    Use case ends.

**Extensions**

* 1a. The group name is invalid.
    * 1a1. TAssist shows an error message stating the valid group name format.

      Use case resumes at step 1.

* 1b. A group with the same name (case-insensitive) already exists.
    * 1b1. TAssist shows an error message stating that the group already exists.

      Use case ends.

* 3a. No tutorial group exists.
    * 3a1. TAssist informs the TA that there are no groups and shows how to create one.

      Use case ends.

**Use case: UC02 - Switch to another tutorial group**

**MSS**

1.  TA requests to switch to a tutorial group with a given name.
2.  TAssist makes that group the active group and shows a confirmation.

    Use case ends.

**Extensions**

* 1a. No group with the given name (case-insensitive) exists.
    * 1a1. TAssist shows an error message stating that the group does not exist.

      Use case resumes at step 1.

**Use case: UC03 - Add a student to the active group**

**MSS**

1.  TA requests to add a student with a given name and student ID.
2.  TAssist adds the student to the active group and shows a confirmation.

    Use case ends.

**Extensions**

* 1a. The name or student ID is invalid.
    * 1a1. TAssist shows an error message stating the valid format.

      Use case resumes at step 1.

* 1b. A student with the same student ID (case-insensitive) already exists in the active group.
    * 1b1. TAssist shows an error message stating that the student ID already exists in the group.

      Use case ends.

* 1c. A student with the same name but a different student ID exists in the active group.
    * 1c1. TAssist adds the student as a separate student.

      Use case resumes at step 2.

**Use case: UC04 - Remove a student from the active group**

**MSS**

1.  TA requests to list the students in the active group.
2.  TAssist shows the name and student ID of every student in the group.
3.  TA requests to delete a specific student by student ID.
4.  TAssist removes the student and the student's attendance, participation and assignment records from the group, and shows a confirmation.

    Use case ends.

**Extensions**

* 2a. The group has no students.

  Use case ends.

* 3a. No student in the active group has the given student ID.
    * 3a1. TAssist shows an error message stating that the student does not exist in the group.

      Use case resumes at step 2.

**Use case: UC05 - Take attendance during a tutorial**

**Preconditions:** The active group has at least one student.

**MSS**

1.  TA requests to mark a student as present or absent for a given week.
2.  TAssist records the attendance and shows a confirmation.
3.  TA repeats steps 1-2 until every student in the group has been marked.
4.  TA requests to view the group's attendance for that week.
5.  TAssist shows the attendance status of every student in the group for that week.

    Use case ends.

**Extensions**

* 1a. No student in the active group has the given student ID.
    * 1a1. TAssist shows an error message stating that the student does not exist in the group.

      Use case resumes at step 1.

* 1b. The week is not a whole number from 1 to 13.
    * 1b1. TAssist shows an error message stating the valid range of weeks.

      Use case resumes at step 1.

* 1c. The status is not `present` or `absent`.
    * 1c1. TAssist shows an error message stating the valid statuses.

      Use case resumes at step 1.

* 1d. The student already has an attendance record for that week (e.g. the TA is correcting a mistake).
    * 1d1. TAssist replaces the existing record with the new status and shows that the record was updated.

      Use case resumes at step 3.

* 4a. No attendance has been recorded for that week.
    * 4a1. TAssist informs the TA that there are no attendance records for that week.

      Use case ends.

* 5a. Some students have no attendance record for that week.
    * 5a1. TAssist shows those students as not recorded.

      Use case ends.

**Use case: UC06 - Record class participation**

**Preconditions:** The active group has at least one student.

**MSS**

1.  TA requests to set a student's participation score for a given week.
2.  TAssist records the score and shows a confirmation.
3.  TA requests to view the group's participation scores for that week.
4.  TAssist shows the participation score of every student in the group for that week.

    Use case ends.

**Extensions**

* 1a. No student in the active group has the given student ID.
    * 1a1. TAssist shows an error message stating that the student does not exist in the group.

      Use case resumes at step 1.

* 1b. The week is not a whole number from 1 to 13, or the score is not a whole number from 0 to 5.
    * 1b1. TAssist shows an error message stating the valid values. The existing score, if any, is unchanged.

      Use case resumes at step 1.

* 1c. The student already has a participation score for that week.
    * 1c1. TAssist replaces the previous score with the new score and shows that the score was updated.

      Use case resumes at step 3.

* 4a. Some students have no participation score for that week.
    * 4a1. TAssist shows those students as not recorded.

      Use case ends.

**Use case: UC07 - Track an assignment**

**Preconditions:** The active group has at least one student.

**MSS**

1.  TA requests to create an assignment with a given name.
2.  TAssist creates the assignment for the active group and shows a confirmation.
3.  TA requests to mark a student's assignment as submitted.
4.  TAssist records the submission status and shows a confirmation.
5.  TA requests to record a grade for that student's assignment.
6.  TAssist records the grade and shows a confirmation.
7.  TA repeats steps 3-6 for other students.
8.  TA requests to view the records of the assignment.
9.  TAssist shows the submission status and grade of every student in the group for that assignment.

    Use case ends.

**Extensions**

* 1a. The assignment name is invalid.
    * 1a1. TAssist shows an error message stating the valid assignment name format.

      Use case resumes at step 1.

* 1b. An assignment with the same name (case-insensitive) already exists in the active group.
    * 1b1. TAssist shows an error message stating that the assignment already exists.

      Use case ends.

* 3a. The assignment does not exist in the active group.
    * 3a1. TAssist shows an error message stating that the assignment does not exist.

      Use case resumes at step 3.

* 3b. No student in the active group has the given student ID.
    * 3b1. TAssist shows an error message stating that the student does not exist in the group.

      Use case resumes at step 3.

* 3c. The submission status is not `yes` or `no`.
    * 3c1. TAssist shows an error message stating the valid statuses.

      Use case resumes at step 3.

* 5a. The grade is not a whole number from 0 to 100.
    * 5a1. TAssist shows an error message stating the valid range of grades.

      Use case resumes at step 5.

* 5b. The student's assignment has not been marked as submitted.
    * 5b1. TAssist shows an error message asking the TA to mark the assignment as submitted first.

      Use case resumes at step 3.

* 5c. The student already has a grade for that assignment (e.g. the TA is correcting a mistake).
    * 5c1. TAssist replaces the previous grade with the new grade.

      Use case resumes at step 6.

* 9a. Some students have not submitted the assignment.
    * 9a1. TAssist shows those students as not submitted, without a grade.

      Use case ends.

**Use case: UC08 - Get help on a command**

**MSS**

1.  TA requests help on a specific topic (e.g. attendance).
2.  TAssist shows the command formats and examples for that topic.

    Use case ends.

**Extensions**

* 1a. The TA does not specify a topic.
    * 1a1. TAssist shows the formats of all commands.

      Use case ends.

* 1b. The given topic is not a valid topic.
    * 1b1. TAssist shows an error message listing the valid topics.

      Use case ends.

**Use case: UC09 - Export a group's records**

**Preconditions:** The active group has at least one student.

**MSS**

1.  TA requests to export the records of the active group.
2.  TAssist saves the group's students, attendance, participation scores, and assignment records to a CSV file, and shows the location of the file.
3.  TA sends the file to the course coordinator.

    Use case ends.

**Extensions**

* 2a. TAssist is unable to write the file.
    * 2a1. TAssist shows an error message stating that the export failed. No data is changed.

      Use case ends.

**Use case: UC10 - Resume work after restarting TAssist**

**MSS**

1.  TA closes TAssist after recording data.
2.  TA launches TAssist again.
3.  TAssist loads the saved data and shows the tutorial groups and records as they were when TAssist was closed.

    Use case ends.

**Extensions**

* 3a. No saved data is found.
    * 3a1. TAssist starts with no data and informs the TA.

      Use case ends.

* 3b. The saved data cannot be read.
    * 3b1. TAssist shows an error message and does not overwrite the saved data file.

      Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  Should be able to hold up to 10 tutorial groups of 30 students each, with records for 13 weeks and 20 assignments per group, without noticeable sluggishness in performance for typical usage.
3.  Should show the result of any command within 1 second when holding the amount of data stated above.
4.  A TA with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
5.  A TA who is familiar with the commands should be able to record attendance for a group of 20 students within 2 minutes, so that attendance can be taken during a tutorial.
6.  Should be used by a single TA on their own computer; it does not need to support multiple users or sharing data between users.
7.  Should work without an internet connection, and should not send any data to an external server, so that student records stay on the TA's computer.
8.  Should save data automatically after every successful command that changes data, so that no recorded data is lost when TAssist is closed or stops unexpectedly after that command.
9.  A command that fails should not change any data, either in the app or in the data file.
10. Should store data locally in a human-editable text file, so that an advanced user can inspect or repair the data without using TAssist.
11. Should not depend on a database management system.
12. Should be distributed as a single JAR file of at most 100MB that runs without an installer.
13. The GUI should work well (i.e. no resolution-related inconveniences) for standard screen resolutions of 1920x1080 and higher, and for screen scales of 100% and 125%. It should be usable (i.e. all functions can be used, even if the layout is not optimal) for resolutions of 1280x720 and higher, and for screen scales of 150%.
14. Every error message should state what is wrong with the command, so that a TA can correct the command without referring to the User Guide.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Private contact detail**: A contact detail that is not meant to be shared with others

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
