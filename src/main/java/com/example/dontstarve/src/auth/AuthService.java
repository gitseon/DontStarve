package com.example.dontstarve.src.auth;

import com.example.dontstarve.src.auth.model.LoginDto;
import com.example.dontstarve.src.auth.model.LoginRes;
import com.example.dontstarve.src.user.User;
import com.example.dontstarve.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(AuthRepository authRepository,
                       JwtTokenProvider jwtTokenProvider,
                       PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    // [POST] 로그인
    public LoginRes login(LoginDto loginDto) {
        User user = authRepository.findById(loginDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 id 입니다."));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        String jwt = jwtTokenProvider.createToken(user.getEmail(), user.getAuth(), user.getIdx());
        return new LoginRes(jwt);
    }



}
