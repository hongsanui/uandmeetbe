package com.project.uandmeetbe.security.jwt.provider;

import com.project.uandmeetbe.security.jwt.JwtProperties;
import com.project.uandmeetbe.security.jwt.exception.JwtExpiredTokenException;
import com.project.uandmeetbe.security.jwt.exception.JwtModulatedTokenException;
import com.project.uandmeetbe.security.jwt.model.UserContext;
import com.project.uandmeetbe.security.jwt.service.JwtService;
import com.project.uandmeetbe.security.jwt.token.JwtPostAuthenticationToken;
import com.project.uandmeetbe.security.jwt.token.JwtPreAuthenticationToken;
import com.project.uandmeetbe.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * <h1>JwtAuthenticationProvider</h1>
 * <p>
 *     JWT 토큰 문자열을 검증하고 계정 엔티티로 바꾸는 작업을 관리한다.
 * </p>
 */
@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService<User> jwtService;
    private final JwtProperties jwtProperties;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = (String) authentication.getPrincipal();
        try {
            Claims claims = jwtService.verifyToken(token, jwtProperties.getAccessTokenSigningKey());

            User verifiedUser = jwtService.convertUserModel(claims);
            UserContext context = new UserContext(verifiedUser);

            return new JwtPostAuthenticationToken(context);

        } catch (SignatureException | MalformedJwtException | MissingClaimException ex) {
            throw new JwtModulatedTokenException("변조된 JWT 토큰입니다.");

        } catch (ExpiredJwtException ex) {
            throw new JwtExpiredTokenException("만료된 JWT 토큰입니다.");
        }
    }

    // JwtPreAuthenticationToken 타입만 처리 가능하도록 설정
    @Override
    public boolean supports(Class<?> authentication) {
        return JwtPreAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
