package com.spelloverflow.data;

import com.spelloverflow.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // lookup method for testing if user was saved
    Optional<User> findByUsername(String username);
}
