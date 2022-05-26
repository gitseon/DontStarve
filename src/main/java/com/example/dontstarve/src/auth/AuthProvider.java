package com.example.dontstarve.src.auth;

import com.example.dontstarve.config.BaseException;
import com.example.dontstarve.src.auth.model.AutoLoginRes;
import com.example.dontstarve.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static com.example.dontstarve.config.BaseResponseStatus.*;


@Service
public class AuthProvider {

    private final AuthRepository authRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthProvider(AuthRepository authRepository,
                        JwtTokenProvider jwtTokenProvider) {
        this.authRepository = authRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    // [GET] 자동로그인
    public AutoLoginRes autoLogIn() throws BaseException {
        try {
            return new AutoLoginRes(jwtTokenProvider.getUserId());
        } catch (Exception exception) {
            throw new BaseException(INVALID_JWT);
        }
    }
}
