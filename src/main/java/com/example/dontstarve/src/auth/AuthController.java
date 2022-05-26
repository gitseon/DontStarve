package com.example.dontstarve.src.auth;

import com.example.dontstarve.config.BaseException;
import com.example.dontstarve.config.BaseResponse;
import com.example.dontstarve.src.auth.model.AutoLoginRes;
import com.example.dontstarve.src.auth.model.LoginDto;
import com.example.dontstarve.src.auth.model.LoginRes;
import com.example.dontstarve.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import static com.example.dontstarve.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final AuthProvider authProvider;
    @Autowired
    private final AuthService authService;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthProvider authProvider,
                          AuthService authService,
                          JwtTokenProvider jwtTokenProvider){
        this.authProvider = authProvider;
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 로그인 API
     * [POST] /auth/login
     *
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<LoginRes> login(@RequestBody LoginDto loginDto) throws BaseException {
        // validation
        // email 값 존재 검사
        if (loginDto.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        LoginRes loginRes = authService.login(loginDto);
        return new BaseResponse<>(loginRes);
    }

    /**
     * 소셜로그인 - 인가 코드 받기 (kakao) API
     * [GET] /auth/kakao
     *
     */




    /**
     * 소셜로그인 - 토큰 발급 받기 (kakao) API
     * [GET] /auth/kakao/token
     *
     */
    @ResponseBody
    @GetMapping("/kakao/token")
    public BaseResponse<String> kakaoToken(String code) throws BaseException {
        try {
            // 토큰 발급
            String accessToken = authService.getKakaoToken(code);

            // 사용자 정보 가져오기 - 회원가입 진행
            authService.createKakaoUser(accessToken);

            return new BaseResponse<>("카카오 로그인 성공");

        } catch (Exception exception) {
            return new BaseResponse<>("카카오 로그인 실패");
        }
    }


    /**
     * 자동로그인 API
     * [GET] /auth/jwt
     *
     */
    @ResponseBody
    @GetMapping("/jwt")
    public BaseResponse<AutoLoginRes> autoLogIn() {
        try {
            AutoLoginRes autoLoginRes = authProvider.autoLogIn();
            return new BaseResponse<>(autoLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
