package com.blogs.blogs.security;

import com.blogs.blogs.entity.User;
import com.blogs.blogs.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String id = attributes.get("sub").toString();
        String name = attributes.get("name").toString();
        String email = attributes.get("email").toString();
        String picture = attributes.get("picture").toString();

        log.info("OAuth2 Login Attempt - ID: {}, Name: {}, Email: {}", id, name, email);

        // Save or update user in DB
        /*User user = userRepository.findByEmail(email).orElse(new User());
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPicture(picture);
        userRepository.save(user);*/

        Optional<User> flag = userRepository.findByEmail(email);
        if (flag.isEmpty()) {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setEmail(email);
            user.setPicture(picture);
            userRepository.save(user);
        }

        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("USER")),
                attributes, "sub");
    }
}

