package org.example.chatapi.user.repo;


import org.example.chatapi.user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String username);
    Boolean existsByUsername(String username);
}
