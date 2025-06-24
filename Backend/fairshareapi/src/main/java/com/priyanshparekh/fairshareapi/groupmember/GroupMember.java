package com.priyanshparekh.fairshareapi.groupmember;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "group_members")
@IdClass(GroupMemberKey.class)
public class GroupMember {

    @Id
    private Long userId;
    @Id
    private Long groupId;

    public GroupMember() {
    }

    public GroupMember(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
