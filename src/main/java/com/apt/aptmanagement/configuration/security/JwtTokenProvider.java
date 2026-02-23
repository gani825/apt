package com.apt.aptmanagement.configuration.security;

import com.apt.aptmanagement.configuration.constants.ConstJwt;
import com.apt.aptmanagement.configuration.model.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.util.Date;

// JWT 토큰 생성(generateToken)과 파싱(getJwtUserFromToken) 담당
@Slf4j
@Component
public class JwtTokenProvider {

    // 객체 ↔ JSON 직렬화/역직렬화 담당
    private final ObjectMapper objectMapper;

    // JWT 관련 설정값 (issuer, secretKey, 유효시간 등)
    private final ConstJwt constJwt;

    // JWT 서명(Signature)에 사용할 HMAC 비밀 키
    private final SecretKey secretKey;

    public JwtTokenProvider(ObjectMapper objectMapper, ConstJwt constJwt) {
        this.objectMapper = objectMapper;
        this.constJwt = constJwt;
        // Base64 인코딩된 secretKey를 디코딩해서 HMAC 서명 키 생성
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(constJwt.getSecretKey()));
        log.info("JwtTokenProvider 초기화 완료 - constJwt: {}", constJwt);
    }

    // Access Token 생성 (짧은 유효시간, 실제 API 인증에 사용)
    public String generateAccessToken(JwtUser jwtUser) {
        return generateToken(jwtUser, constJwt.getAccessTokenValidityMilliseconds());
    }

    // Refresh Token 생성 (긴 유효시간, AT 만료 시 재발급에 사용)
    public String generateRefreshToken(JwtUser jwtUser) {
        return generateToken(jwtUser, constJwt.getRefreshTokenValidityMilliseconds());
    }

    // JWT 토큰 생성 공통 메서드
    public String generateToken(JwtUser jwtUser, long tokenValidityMilliseconds) {
        Date now = new Date();
        return Jwts.builder()
                .header()
                    .type(constJwt.getBearerFormat())
                .and()
                .issuer(constJwt.getIssuer())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + tokenValidityMilliseconds))
                // 사용자 정보를 JSON 문자열로 변환해서 claim에 저장
                .claim(constJwt.getClaimKey(), makeClaimByUserToJson(jwtUser))
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 JwtUser 객체 추출 (쿠키에서 꺼낸 AT 검증 시 사용)
    public JwtUser getJwtUserFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // claim에서 JSON 문자열로 저장된 사용자 정보를 JwtUser 객체로 역직렬화
            String claimJson = claims.get(constJwt.getClaimKey(), String.class);
            return objectMapper.readValue(claimJson, JwtUser.class);
        } catch (Exception e) {
            log.error("JWT 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    // JwtUser 객체를 JSON 문자열로 직렬화 (claim 저장 용도)
    public String makeClaimByUserToJson(JwtUser jwtUser) {
        try {
            return objectMapper.writeValueAsString(jwtUser);
        } catch (Exception e) {
            throw new RuntimeException("JwtUser 직렬화 실패", e);
        }
    }

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
