package oauth2jwt.demo;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger2Config {

    @Bean
    public OpenAPI openAPI(@Value("springdoc.api-docs.version") String springdocVersion) {
        Info info = new Info()
                .title("Oauth2.0-JWT")
                .version(springdocVersion)
                .description("Oauth2.0과 JWT를 활용해서 로그인 인증 방식 구현");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
