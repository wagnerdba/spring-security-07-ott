package com.wrtecnologia.ottdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@SpringBootApplication
public class OttDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OttDemoApplication.class, args);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(http -> http.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .oneTimeTokenLogin(configurer -> configurer
                        .tokenGenerationSuccessHandler((request, response, oneTimeToken) -> {
                            var token = oneTimeToken.getTokenValue();

                            var msg = "please go to http://localhost:8080/login/ott?token=" + token;
                            System.out.println(msg);

                            response.setContentType(MediaType.TEXT_HTML_VALUE);
                            response.getWriter().write("You've got console mail");
                        }))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Configura o BCrypt como codificador de senhas
    }

    @Bean
    InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        UserDetails user1 = User.builder()
                .username("wagnerpires")
                .password(passwordEncoder().encode("123456"))
                .roles("USER")
                .build();

        UserDetails user2 = User.builder()
                .username("miramarpires")
                .password(passwordEncoder().encode("123456"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user1, user2);
    }

    @Controller
    static class SecuredController {

        @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
        @ResponseBody
        public String helloPage(Principal principal) {
            return generateHtmlPage(principal.getName());
        }

        private String generateHtmlPage(String username) {
            return "<!DOCTYPE html>" +
                    "<html lang=\"pt-br\">" +
                    "<head>" +
                    "<meta charset=\"UTF-8\">" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "<title>Welcome</title>" +
                    "<style>" +
                    "body {" +
                    "  font-family: Arial, sans-serif;" +
                    "  background-color: #f4f4f9;" +
                    "  color: #333;" +
                    "  display: flex;" +
                    "  justify-content: center;" +
                    "  align-items: center;" +
                    "  height: 100vh;" +
                    "  margin: 0;" +
                    "}" +
                    ".container {" +
                    "  text-align: center;" +
                    "  background: #fff;" +
                    "  border-radius: 10px;" +
                    "  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);" +
                    "  padding: 20px;" +
                    "  max-width: 400px;" +
                    "  width: 90%;" +
                    "}" +
                    "h1 {" +
                    "  color: #4CAF50;" +
                    "  margin-bottom: 10px;" +
                    "}" +
                    "p {" +
                    "  font-size: 1.2em;" +
                    "  margin: 10px 0;" +
                    "}" +
                    ".footer {" +
                    "  margin-top: 20px;" +
                    "  font-size: 0.6em;" +
                    "  color: #aaa;" +
                    "}" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class=\"container\">" +
                    "<h1>BEM-VINDO, " + username + "!</h1>" +
                    "<p>Rota privada, acesso autorizado.</p><br>" +
                    "<div class=\"footer\">" +
                    "<p>WR Tecnologia em Sistemas - Demonstração de Login com Token</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
        }
    }
}
