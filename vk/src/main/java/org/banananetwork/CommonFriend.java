package org.banananetwork;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author karyakin dmitry
 *         date 30.07.15.
 */
public class CommonFriend {

    private long ownerId;
    private long friendId;
    private long groupId;
    private Timestamp lastUpdated;

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getFriendId() {
        return friendId;
    }

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
