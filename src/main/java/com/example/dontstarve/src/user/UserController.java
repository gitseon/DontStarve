package com.example.dontstarve.src.user;

import com.example.dontstarve.config.BaseException;
import com.example.dontstarve.config.BaseResponse;
import com.example.dontstarve.src.user.model.GetUserRes;
import com.example.dontstarve.src.user.model.UserDto;
import com.example.dontstarve.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import static com.example.dontstarve.config.BaseResponseStatus.*;
import static com.example.dontstarve.utils.ValidationRegex.*;

@RestController
@Component
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 회원가입 API
     * [POST] /users
     * idx 반환
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<Integer> signUp(@RequestBody UserDto userDto) throws BaseException {

        // email null validation
        if (userDto.getEmail().equals(null) || userDto.getEmail().equals("")) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        // email form validation
        if (!isRegexEmail(userDto.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        int idx = userService.save(userDto);
        return new BaseResponse<>(idx);
    }


    /**
     * 회원 정보 수정 API
     * [PATCH] /users/:id
     * user 정보 수정(Email, password, name ,,,)
     */
    @ResponseBody
    @PatchMapping("/{userId}")
    public BaseResponse<String> modifyUserInfo(@PathVariable("userId") int userId, @RequestBody UserDto userDto) throws BaseException {
        try {
            // jwt에서 idx 추출.
            int userIdByJwt = jwtTokenProvider.getUserId();
            // userIdx와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            
            String result = userService.update(userId, userDto);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 정보 조회 API
     * [GET] /users/:userId
     * user 정보 반환
     */
    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<GetUserRes> userInfo(@PathVariable("userId") int userId) throws BaseException {
        // jwt에서 idx 추출.
        int userIdByJwt = jwtTokenProvider.getUserId();
        // userIdx와 접근한 유저가 같은지 확인
        if(userId != userIdByJwt){
            return new BaseResponse<>(INVALID_USER_JWT);
        }

        try {
            GetUserRes getUserRes = userService.loadUserByUserId(userId);
            return new BaseResponse<>(getUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }





    /*@GetMapping("/logout") // logout by GET 요청
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder
                .getContext().getAuthentication());
        return "redirect:/login";
    }*/
}
