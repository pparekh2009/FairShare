package com.priyanshparekh.fairshareapi.groupmember;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

public class GroupMemberKey implements Serializable {

    Long userId;
    Long groupId;

    public GroupMemberKey() {} // no-args
    public GroupMemberKey(Long userId, Long groupId) { // all-args
        this.userId = userId;
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GroupMemberKey)) return false;

//        return groupId.equals(((GroupMemberKey) obj).groupId) && Objects.equals(userId, ((GroupMemberKey) obj).userId);
        GroupMemberKey that = (GroupMemberKey) obj;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, groupId);
    }
}
