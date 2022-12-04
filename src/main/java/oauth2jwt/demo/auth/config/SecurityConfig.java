package oauth2jwt.demo.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth2jwt.demo.auth.dto.AuthResponse;
import oauth2jwt.demo.auth.jwt.AuthToken;
import oauth2jwt.demo.auth.jwt.JwtAuthorizationFilter;
import oauth2jwt.demo.auth.jwt.AuthTokenProvider;
import oauth2jwt.demo.auth.oauth.UserPrincipal;
import oauth2jwt.demo.auth.service.AuthService;
import oauth2jwt.demo.auth.service.CustomOauth2UserServcie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOauth2UserServcie customOauth2UserService;

    private final CorsConfig corsConfig;
    private final AuthService authService;
    private final AuthTokenProvider authTokenProvider;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션 사용 안함 설정
        http.httpBasic().disable(); // Basic 인증 방식 사용 안함 설정
        http.formLogin().disable();
        http.addFilter(corsConfig.corsFilter());
        http.addFilter(new JwtAuthorizationFilter(authenticationManager(), authTokenProvider));
        http.authorizeRequests()
                .antMatchers("/user/**").access("hasRole('ROLE_USER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/auth/refresh").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(customOauth2UserService)
                .and()
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                        Authentication authentication) throws IOException, ServletException {
                        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
                        request.setAttribute("id", principal.getId().toString());
                        request.setAttribute("role", principal.getRole());
                        String targetUrl = "/auth/success";
                        RequestDispatcher dis = request.getRequestDispatcher(targetUrl); // 인증 성공 후 포워딩할 URL
                        dis.forward(request, response);
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                        AuthenticationException exception) throws IOException, ServletException {
                        log.info("인증 실패");
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorization");
                    }
                });
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
