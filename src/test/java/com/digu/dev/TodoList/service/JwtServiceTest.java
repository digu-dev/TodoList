package com.digu.dev.TodoList.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // "my-super-secret-key-for-jwt-auth" base64 encoded (32 bytes)
        String secret = Base64.getEncoder().encodeToString("my-super-secret-key-for-jwt-auth".getBytes());
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    private Authentication buildAuthentication(String username, String role) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    @Test
    void generateToken_returnsNonBlankToken() {
        Authentication auth = buildAuthentication("alice", "USER");

        String token = jwtService.generateToken(auth);

        assertThat(token).isNotBlank();
    }

    @Test
    void generateToken_containsSubjectClaim() {
        Authentication auth = buildAuthentication("alice", "USER");

        String token = jwtService.generateToken(auth);
        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("alice");
    }

    @Test
    void generateToken_containsRolesClaim() {
        Authentication auth = buildAuthentication("alice", "ADMIN");

        String token = jwtService.generateToken(auth);
        var jwt = jwtService.decodeToken(token);

        assertThat(jwt.getClaimAsStringList("roles")).contains("ROLE_ADMIN");
    }

    @Test
    void isTokenValid_returnsTrueForValidTokenAndMatchingUser() {
        Authentication auth = buildAuthentication("bob", "USER");
        String token = jwtService.generateToken(auth);
        UserDetails userDetails = User.builder()
                .username("bob")
                .password("pw")
                .roles("USER")
                .build();

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_returnsFalseForWrongUser() {
        Authentication auth = buildAuthentication("bob", "USER");
        String token = jwtService.generateToken(auth);
        UserDetails wrongUser = User.builder()
                .username("alice")
                .password("pw")
                .roles("USER")
                .build();

        assertThat(jwtService.isTokenValid(token, wrongUser)).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseForTokenSignedWithDifferentKey() {
        JwtService otherService = new JwtService();
        String differentSecret = Base64.getEncoder().encodeToString("different-secret-key-for-tests!!".getBytes());
        ReflectionTestUtils.setField(otherService, "secret", differentSecret);
        ReflectionTestUtils.setField(otherService, "expirationMs", 3600000L);

        Authentication auth = buildAuthentication("bob", "USER");
        String tokenFromOtherService = otherService.generateToken(auth);
        UserDetails userDetails = User.builder()
                .username("bob")
                .password("pw")
                .roles("USER")
                .build();

        assertThat(jwtService.isTokenValid(tokenFromOtherService, userDetails)).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseForInvalidToken() {
        UserDetails userDetails = User.builder()
                .username("bob")
                .password("pw")
                .roles("USER")
                .build();

        assertThat(jwtService.isTokenValid("invalid.token.here", userDetails)).isFalse();
    }

    @Test
    void decodeToken_returnsJwtWithCorrectSubject() {
        Authentication auth = buildAuthentication("carol", "SUPERVISOR");
        String token = jwtService.generateToken(auth);

        var jwt = jwtService.decodeToken(token);

        assertThat(jwt.getSubject()).isEqualTo("carol");
        assertThat(jwt.getIssuer()).hasToString("http://localhost");
    }

}
