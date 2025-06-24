package com.priyanshparekh.fairshareapi.group;

import com.priyanshparekh.fairshareapi.groupmember.GroupMember;
import com.priyanshparekh.fairshareapi.groupmember.GroupMemberRepository;
import com.priyanshparekh.fairshareapi.user.User;
import com.priyanshparekh.fairshareapi.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GroupService(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }

    public List<Group> getGroups(Long userId) {
        List<GroupMember> groupMembers = groupMemberRepository.findAllByUserId(userId);
        List<Long> groupIds = groupMembers.stream().map(GroupMember::getGroupId).toList();
        logger.info("GroupService: getGroups: groupIds: {}", Arrays.toString(groupIds.toArray()));
        return groupRepository.findAllByIdIn(groupIds);
    }

    public Group addGroup(Long userId, Group group) {
        Group savedGroup = groupRepository.save(group);

        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(savedGroup.getId());
        groupMember.setUserId(userId);

        groupMemberRepository.save(groupMember);

        return savedGroup;
    }

    public List<GroupMember> addMembers(List<User> users, Long groupId) {
        List<GroupMember> groupMembers = users.stream().map(user -> new GroupMember(user.getId(), groupId)).toList();

        logger.info("groupService: addMembers: users size: {}", users.size());
        logger.info("groupService: addMembers: groupMembers size: {}", groupMembers.size());

        return groupMemberRepository.saveAll(groupMembers);
    }

    public List<User> getMembers(Long groupId) {
        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(groupId);

        List<Long> userIds = groupMembers.stream().map(GroupMember::getUserId).toList();

        return userRepository.findAllByIdIn(userIds);
    }
}
