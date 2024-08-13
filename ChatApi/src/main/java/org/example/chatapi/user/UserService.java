package org.example.chatapi.user;

import lombok.RequiredArgsConstructor;
import org.example.chatapi.admin.BlockRequest;
import org.example.chatapi.user.dto.UserDto;
import org.example.chatapi.user.mapper.UserMapper;
import org.example.chatapi.user.model.UserModel;
import org.example.chatapi.user.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;

    public List<UserDto> findAll() {
        return userRepo.findAll().stream().map(userMapper::entityToDto).collect(Collectors.toList());
    }

    public UserDto updBlockUser(BlockRequest request) {
        UserModel userModel = userRepo.findByUsername(request.getUsername()).orElseThrow();
        userModel.setIsBlocked(request.getIsBlocked());
        userRepo.save(userModel);
        return userMapper.entityToDto(userModel);
    }
}