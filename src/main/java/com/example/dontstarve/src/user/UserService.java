package com.example.dontstarve.src.user;

import com.example.dontstarve.config.BaseException;
import com.example.dontstarve.src.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Component
@Transactional(rollbackFor = Exception.class)
public class UserService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 회원가입 - 유저정보저장, 유저 식별자 반환
    public int save(UserDto infoDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        infoDto.setPassword(encoder.encode(infoDto.getPassword()));
        User user = User.builder()
                .email(infoDto.getEmail())
                .password("{bcrypt}" + infoDto.getPassword())
                .name(infoDto.getName())
                .auth("USER")
                .status("active")
                .build();
        return userRepository.save(user).getIdx();
    }

    // 회원정보수정
    @Transactional(rollbackFor = Exception.class)
    public String update(int userId, UserDto userDto) throws BaseException {
        Optional<User> existing = userRepository.findById(userId);
        if (!existing.isPresent()) {
            return "정보 수정에 실패했습니다";
        }
        User user = existing.get();
        if (StringUtils.hasText(userDto.getEmail())) user.setEmail(userDto.getEmail());
        if (StringUtils.hasText(userDto.getName())) user.setName(userDto.getName());
        if (StringUtils.hasText(userDto.getPassword())) user.setPassword(userDto.getPassword());
        userRepository.save(user);
        return "정보 수정이 완료되었습니다.";
    }
}
