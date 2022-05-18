package com.example.dontstarve.utils;

import com.example.dontstarve.utils.JwtFilter;
import com.example.dontstarve.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity // Spring Security를 활성화하는 어노테이션
@Configuration // Bean 관리하는 어노테이션
// 해당 클래스가 Spring Security 설정 파일로 역할을 하기 위해선 WebSecurityConfigurerAdapter 클래스를 상속해야 함
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /*@Autowired
    private final UserService userService; // 유저 정보를 가져올 클래스*/
    /*@Override
    // 인증을 무시할 경로 설정
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }

    @Override
    // http 관련 인증 설정 가능
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable().headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/users", "/user").permitAll() // 누구나 접근 가능
                .antMatchers("/").hasRole("USER") // USER, ADMIN 만 접근 가능
                .antMatchers("/admin").hasRole("ADMIN") // ADMIN 만 접근 가능
                .anyRequest().authenticated() // 나머지는 권한이 있기만 하면 접근 가능
                .and()
                .formLogin() // 로그인에 대한 설정
                .loginPage("/login") // 로그인 페이지 링크
                .defaultSuccessUrl("/") // 로그인 성공시 연결되는 주소
                .and()
                .logout() // 로그아웃 관련 설정
                .logoutSuccessUrl("/login") // 로그아웃 성공시 연결되는 주소
                .invalidateHttpSession(true) // 로그아웃시 저장해 둔 세션 날리기
        ;
    }

    @Override
    // 로그인 시 필요한 정보를 가져오기
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService) // 유저 정보는 userService 에서 가져온다
                .passwordEncoder(new BCryptPasswordEncoder()); // 패스워드 인코더는 passwordEncoder(BCrypt 사용)
    }*/
    /*
    private JwtFilter jwtFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/authenticate")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);;
    }

    @Bean
    // 비밀번호 암호화
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();*/

    private final JwtTokenProvider jwtTokenProvider;

    // 암호화에 필요한 PasswordEncoder 를 Bean 등록합니다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // authenticationManager를 Bean 등록합니다.
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // rest api 만을 고려하여 기본 설정은 해제하겠습니다.
                .csrf().disable() // csrf 보안 토큰 disable처리.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
                .and()
                .authorizeRequests() // 요청에 대한 사용권한 체크
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/com/example/dontstarve/src/user/**").hasRole("USER")
                .anyRequest().permitAll() // 그외 나머지 요청은 누구나 접근 가능
                .and()
                .addFilterBefore(new JwtFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
        // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
    }
}
