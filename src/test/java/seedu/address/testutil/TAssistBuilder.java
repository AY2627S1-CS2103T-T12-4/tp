package seedu.address.testutil;

import seedu.address.model.TAssist;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

/**
 * A utility class to help with building TAssist objects.
 * Example usage: <br>
 *     {@code TAssist tAssist = new TAssistBuilder().withGroup(group).withActiveGroup(group.getName()).build();}
 */
public class TAssistBuilder {

    private final TAssist tAssist;
    private GroupName activeGroupName;

    /**
     * Creates a {@code TAssistBuilder} with no groups and no active group.
     */
    public TAssistBuilder() {
        tAssist = new TAssist();
    }

    /**
     * Initializes the TAssistBuilder with a copy of the data in {@code tAssistToCopy}.
     */
    public TAssistBuilder(TAssist tAssistToCopy) {
        tAssist = new TAssist(tAssistToCopy);
    }

    /**
     * Adds {@code group} to the {@code TAssist} that we are building.
     */
    public TAssistBuilder withGroup(Group group) {
        tAssist.addGroup(group);
        return this;
    }

    /**
     * Makes the group named {@code groupName} the active group of the {@code TAssist} that we are building.
     * The group must be added by the time {@link #build()} is called.
     */
    public TAssistBuilder withActiveGroup(GroupName groupName) {
        activeGroupName = groupName;
        return this;
    }

    /**
     * Returns the {@code TAssist} that we have built, with its active group set if one was named.
     */
    public TAssist build() {
        if (activeGroupName != null) {
            tAssist.setActiveGroup(activeGroupName);
        }
        return tAssist;
    }

}
