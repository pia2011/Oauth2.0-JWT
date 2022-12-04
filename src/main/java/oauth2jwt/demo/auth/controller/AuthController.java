package oauth2jwt.demo.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth2jwt.demo.auth.dto.AuthResponse;
import oauth2jwt.demo.auth.jwt.AuthToken;
import oauth2jwt.demo.auth.jwt.AuthTokenProvider;
import oauth2jwt.demo.auth.jwt.JwtHeaderUtil;
import oauth2jwt.demo.auth.service.AuthService;
import oauth2jwt.demo.common.dto.ApiResponse;
import oauth2jwt.demo.user.enumerate.RoleType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthTokenProvider authTokenProvider;
    private final AuthService authService;

    @GetMapping("/success")
    public ResponseEntity<AuthResponse> signinSuccess(HttpServletRequest request){
        log.info("controller 실행");
        String id = (String) request.getAttribute("id");
        RoleType role = (RoleType) request.getAttribute("role");

        AuthResponse authResponse = authService.createAndSaveToken(id, role);
        return ApiResponse.success(authResponse);
    }

    @Operation(summary = "ATK 갱신 요청", description = "RTK 받아서 ATK 재발급")
    @GetMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request){
        String token = JwtHeaderUtil.getToken(request);
        AuthToken authToken = authTokenProvider.convertToken(token);

        if(!authToken.validate()){
            return ApiResponse.forbidden(null);
        }

        AuthResponse authResponse = authService.findAndCreateToken(token);
        if(authResponse == null){
            return ApiResponse.forbidden(null);
        }

        return ApiResponse.success(authResponse);
    }
}
