package com.example.dontstarve.src.auth;

import com.example.dontstarve.config.BaseException;
import com.example.dontstarve.src.auth.model.LoginDto;
import com.example.dontstarve.src.auth.model.LoginRes;
import com.example.dontstarve.src.user.User;
import com.example.dontstarve.utils.JwtTokenProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.dontstarve.config.BaseResponseStatus.*;


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
    public LoginRes login(LoginDto loginDto) throws BaseException{
        /*User user = authRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 id 입니다."));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        String jwt = jwtTokenProvider.createToken(user.getEmail(), user.getAuth(), user.getUserId());
        return new LoginRes(jwt);*/
        try {

            User user = authRepository.findByEmail(loginDto.getEmail());
            if (user.equals(null)) {
                throw new BaseException(NOT_EXISTS_USER);
            }
            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                throw new BaseException(FAILED_TO_LOGIN);
            }
            String jwt = jwtTokenProvider.createToken(user.getEmail(), user.getAuth(), user.getUserId());
            return new LoginRes(jwt);

        } catch (BaseException exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 소셜 로그인 (카카오) 토큰 발급
    public String getKakaoToken(String code) throws IOException {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id="); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=https://dev.dogi.shop/auth/kakao/token"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            // int responseCode = conn.getResponseCode();
            // System.out.println("responseCode : " + responseCode);
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            //System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonElement element = JsonParser.parseString(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            //System.out.println("access_token : " + access_Token);
            //System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;

    }

    // 소셜 로그인 (카카오) 사용자 정보 가져오기
    public void createKakaoUser(String token) throws BaseException {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            //System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            //System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonElement element = JsonParser.parseString(result);

            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            boolean hasNickName = element.getAsJsonObject().get("properties").getAsJsonObject().get("has_nickname").getAsBoolean();
            String nickname;
            if (hasNickName) {
                nickname = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
            }

            boolean hasAges = element.getAsJsonObject().get("properties").getAsJsonObject().get("has_age_range").getAsBoolean();
            //System.out.println("id : " + id);
            //System.out.println("email : " + email);


            br.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
