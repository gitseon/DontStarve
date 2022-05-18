package com.example.dontstarve.src.user;

import com.example.dontstarve.config.BaseException;
import com.example.dontstarve.config.BaseResponse;
import com.example.dontstarve.utils.JwtTokenProvider;
import com.example.dontstarve.src.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.example.dontstarve.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@Component
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserService userService;


    /**
     * 회원가입 API
     * [POST] /users
     * idx 반환
     */
    /*@PostMapping("/users")
    public String signup(@RequestBody UserDto infoDto) {
        userService.save(infoDto);
        return "redirect:/login";
    }*/
    @PostMapping("")
    public BaseResponse<Integer> signUp(@RequestBody UserDto userDto) {

        if (userDto.getEmail().equals(null) || userDto.getEmail().equals("")) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        int idx = userService.save(userDto);
        return new BaseResponse<>(idx);
    }


    /**
     * 유저 정보 수정 API
     * [PATCH] /users/:id
     * user 정보 수정(Email, password, name ,,,)
     */
    @ResponseBody
    @PatchMapping("/{userId}")
    public BaseResponse<String> modifyUserInfo(@PathVariable("userId") int userId, @RequestBody UserDto userDto) throws BaseException {
        try {
            String result = userService.update(userId, userDto);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    @GetMapping("/logout") // logout by GET 요청
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder
                .getContext().getAuthentication());
        return "redirect:/login";
    }
}
