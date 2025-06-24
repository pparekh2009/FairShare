package com.priyanshparekh.fairshareapi.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    UserRequestDTO addUser(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setName(userRequestDTO.getName());
        byte[] imageBytes = Base64.getDecoder().decode(userRequestDTO.getProfilePic());
        logger.info("UserService: addUser: image bytes: {}", imageBytes);
        user.setProfilePic(imageBytes);

        User savedUser = userRepository.save(user);

        UserRequestDTO userResponseDTO = new UserRequestDTO();
        userResponseDTO.setUsername(savedUser.getUsername());
        userResponseDTO.setEmail(savedUser.getEmail());
        userResponseDTO.setName(savedUser.getName());
        String base64String = Base64.getEncoder().encodeToString(user.getProfilePic());
        userResponseDTO.setProfilePic(base64String);
        return userResponseDTO;
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findAllByUsernameStartsWith(query);
    }
}
