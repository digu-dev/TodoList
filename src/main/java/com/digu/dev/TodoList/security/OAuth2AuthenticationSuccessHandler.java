package com.digu.dev.TodoList.security;

import com.digu.dev.TodoList.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final String redirectUri;
    private final long jwtExpirationMs;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil,
                                              UserRepository userRepository,
                                              @Value("${app.oauth2.redirect-uri}") String redirectUri,
                                              @Value("${app.jwt.expiration-ms}") long jwtExpirationMs) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.redirectUri = redirectUri;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = authToken.getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String providerId;
        if ("github".equals(registrationId)) {
            providerId = String.valueOf(oAuth2User.getAttribute("id"));
        } else {
            providerId = (String) oAuth2User.getAttribute("sub");
        }

        String userId = userRepository.findByProviderAndProviderId(registrationId, providerId)
                .map(user -> user.getId().toString())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));

        String token = jwtUtil.generateToken(userId);

        Cookie cookie = new Cookie("auth_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpirationMs / 1000));
        response.addCookie(cookie);

        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}
