package com.priyanshparekh.fairshareapi.group;

import com.priyanshparekh.fairshareapi.groupmember.GroupMember;
import com.priyanshparekh.fairshareapi.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/users/{user-id}/groups")
    public ResponseEntity<List<Group>> getGroups(@PathVariable(value = "user-id") Long userId) {
        return ResponseEntity.ok(groupService.getGroups(userId));
    }

    @PostMapping("/users/{user-id}/groups")
    public ResponseEntity<Group> addGroup(@PathVariable(value = "user-id") Long userId, @RequestBody Group group) {
        return ResponseEntity.ok(groupService.addGroup(userId, group));
    }

    @PostMapping("/groups/{group-id}/members")
    public ResponseEntity<List<GroupMember>> addUsersToGroup(@PathVariable(value = "group-id") Long groupId, @RequestBody List<User> users) {
        return ResponseEntity.ok(groupService.addMembers(users, groupId));
    }

    @GetMapping("/groups/{group-id}/members")
    public ResponseEntity<List<User>> getGroupMembers(@PathVariable(value = "group-id") Long groupId) {
        return ResponseEntity.ok(groupService.getMembers(groupId));
    }
}
