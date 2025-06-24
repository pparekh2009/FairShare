package com.priyanshparekh.fairshareapi.groupmember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberKey> {
    List<GroupMember> findAllByUserId(Long userId);

    List<GroupMember> findAllByGroupId(Long groupId);
}
