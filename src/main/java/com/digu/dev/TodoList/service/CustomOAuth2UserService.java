package com.digu.dev.TodoList.service;

import com.digu.dev.TodoList.model.UserEntity;
import com.digu.dev.TodoList.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId;
        String name;
        String email;

        if ("github".equals(registrationId)) {
            providerId = String.valueOf(attributes.get("id"));
            name = (String) attributes.get("name");
            if (name == null) {
                name = (String) attributes.get("login");
            }
            email = (String) attributes.get("email");
        } else {
            // google
            providerId = (String) attributes.get("sub");
            name = (String) attributes.get("name");
            email = (String) attributes.get("email");
        }

        final String finalProviderId = providerId;
        final String finalName = name;
        final String finalEmail = email;

        userRepository.findByProviderAndProviderId(registrationId, finalProviderId)
                .orElseGet(() -> createUser(registrationId, finalProviderId, finalName, finalEmail));

        return oAuth2User;
    }

    private UserEntity createUser(String provider, String providerId, String name, String email) {
        UserEntity user = new UserEntity();
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setName(name);
        user.setEmail(email);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
