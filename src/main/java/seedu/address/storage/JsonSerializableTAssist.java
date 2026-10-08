package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.ReadOnlyTAssist;
import seedu.address.model.TAssist;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

/**
 * An immutable TAssist that is serializable to JSON format.
 * A missing group list is read as no groups, and a missing active group as no active group.
 */
@JsonRootName(value = "tassist")
class JsonSerializableTAssist {

    public static final String MESSAGE_EMPTY_GROUP = "The group list has an empty entry.";
    public static final String MESSAGE_DUPLICATE_GROUP = "There is more than one group named %s.";
    public static final String MESSAGE_UNKNOWN_ACTIVE_GROUP = "The active group %s is not in the group list.";

    private final String activeGroup;
    private final List<JsonAdaptedGroup> groups = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableTAssist} with the given active group and groups.
     */
    @JsonCreator
    public JsonSerializableTAssist(@JsonProperty("activeGroup") String activeGroup,
            @JsonProperty("groups") List<JsonAdaptedGroup> groups) {
        this.activeGroup = activeGroup;
        if (groups != null) {
            this.groups.addAll(groups);
        }
    }

    /**
     * Converts a given {@code ReadOnlyTAssist} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableTAssist}.
     */
    public JsonSerializableTAssist(ReadOnlyTAssist source) {
        activeGroup = source.getActiveGroup().map(group -> group.getName().fullName).orElse(null);
        groups.addAll(source.getGroupList().stream().map(JsonAdaptedGroup::new).toList());
    }

    /**
     * Converts this TAssist into the model's {@code TAssist} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public TAssist toModelType() throws IllegalValueException {
        TAssist tAssist = new TAssist();
        for (JsonAdaptedGroup jsonAdaptedGroup : groups) {
            addGroup(tAssist, jsonAdaptedGroup);
        }
        if (activeGroup != null) {
            tAssist.setActiveGroup(toHeldGroupName(tAssist, activeGroup));
        }
        return tAssist;
    }

    private static void addGroup(TAssist tAssist, JsonAdaptedGroup jsonAdaptedGroup) throws IllegalValueException {
        if (jsonAdaptedGroup == null) {
            throw new IllegalValueException(MESSAGE_EMPTY_GROUP);
        }
        Group group = jsonAdaptedGroup.toModelType();
        if (tAssist.hasGroup(group.getName())) {
            throw new IllegalValueException(String.format(MESSAGE_DUPLICATE_GROUP, group.getName()));
        }
        tAssist.addGroup(group);
    }

    /**
     * Returns the name of the group in {@code tAssist} that {@code name} refers to.
     *
     * @throws IllegalValueException if no group in {@code tAssist} has that name.
     */
    private static GroupName toHeldGroupName(TAssist tAssist, String name) throws IllegalValueException {
        if (!GroupName.isValidGroupName(name) || !tAssist.hasGroup(new GroupName(name))) {
            throw new IllegalValueException(String.format(MESSAGE_UNKNOWN_ACTIVE_GROUP, name));
        }
        return new GroupName(name);
    }

}
