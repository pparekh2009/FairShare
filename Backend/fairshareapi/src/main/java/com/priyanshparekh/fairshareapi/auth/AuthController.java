package com.priyanshparekh.fairshareapi.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final SignUpService signUpService;

    @Autowired
    public AuthController(SignUpService signUpService) {
        this.signUpService = signUpService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        signUpService.signUp(signUpRequestDTO);
        return ResponseEntity.ok("User signed up");
    }
}
