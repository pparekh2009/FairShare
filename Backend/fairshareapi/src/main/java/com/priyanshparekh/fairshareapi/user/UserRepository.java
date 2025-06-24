package com.priyanshparekh.fairshareapi.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    List<User> findAllByUsernameStartsWith(String query);

    List<User> findAllByIdIn(List<Long> userIds);
}
