package oauth2jwt.demo.user.controller;

import lombok.extern.slf4j.Slf4j;
import oauth2jwt.demo.auth.oauth.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

    @GetMapping("/user")
    public void checkUser(@AuthenticationPrincipal UserPrincipal userPrincipal){
        log.info(userPrincipal.getUsername());
        log.info(userPrincipal.getAttributes().toString());
    }
}
