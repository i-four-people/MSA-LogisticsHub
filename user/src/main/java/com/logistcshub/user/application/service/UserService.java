package com.logistcshub.user.application.service;

import com.logistcshub.user.application.dtos.MyInfoDto;
import com.logistcshub.user.domain.model.User;
import com.logistcshub.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public MyInfoDto getMyInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("등록하지 않은 유저입니다."));

        return MyInfoDto.of(user);
    }
}
