package cz.trask.tjech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration

public class SecurityConfiguration {

    Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Value("#{'${cert.whiteList}'.split(';')}")
    private String[] certWhitelist;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("*").hasAuthority("ROLE_USER")
                        .anyRequest().authenticated())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .x509(conf -> conf.x509PrincipalExtractor(X509Certificate::getSubjectX500Principal)
                .userDetailsService(userDetailsService()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            {
                logger.info("Users: " + Arrays.toString(certWhitelist));
            }
            @Override
            public User loadUserByUsername(String username) {
                if (username != null) {
                    for (String cert : certWhitelist)
                        if (cert.equals(username))
                            return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
                    else {
                            logger.warn("User not found : " + username);
                            return new User(username, "", Collections.emptyList());
                        }
                }
                return null;
            }
        };
    }
}
