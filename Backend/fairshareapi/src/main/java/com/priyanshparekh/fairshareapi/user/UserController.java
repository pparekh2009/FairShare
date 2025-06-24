package com.priyanshparekh.fairshareapi.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserRequestDTO> addUser(@RequestBody UserRequestDTO user) {
        return ResponseEntity.ok(userService.addUser(user));
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    @GetMapping("/test")
    public ResponseEntity<String> testCall() {
        return ResponseEntity.ok("Call successfully");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }
}
