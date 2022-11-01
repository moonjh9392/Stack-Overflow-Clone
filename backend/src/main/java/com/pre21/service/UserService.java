package com.pre21.service;

import com.pre21.dto.AuthDto;
import com.pre21.entity.RefreshToken;
import com.pre21.entity.User;
import com.pre21.exception.BusinessLogicException;
import com.pre21.exception.ExceptionCode;
import com.pre21.repository.RefreshTokenRepository;
import com.pre21.repository.UserRepository;
import com.pre21.security.jwt.JwtTokenizer;
import com.pre21.security.utils.CustomAuthorityUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenizer jwtTokenizer;


    // 회원가입
    public void createUser(User user) {
        verifyExistsEmail(user.getEmail());
        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        userRepository.save(user);
    }




    // 사용자 로그아웃
    public void logoutUser(String refreshToken) {
        // 토큰이 있는지 확인한 후 삭제
        RefreshToken findToken = checkExistToken(refreshToken);
        refreshTokenRepository.delete(findToken);
    }


    // 토큰 생성 메서드를 호출하여 리스폰즈를 생성 후 리턴
    public AuthDto.Response reIssueAccessToken(String refreshToken) {
        RefreshToken findRefreshToken = checkExistToken(refreshToken);

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        Map<String, Object> claims = jwtTokenizer.getClaims(refreshToken, base64EncodedSecretKey).getBody();

        String email = (String) claims.get("sub");
        List<String> roles = userRepository.findByEmail(email).get().getRoles();

        User findUser = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        AuthDto.Token reIssueToken = createReIssueToken(email, roles, findRefreshToken.getTokenValue());

        AuthDto.Response response = AuthDto.Response.builder()
                .accessToken(reIssueToken.getAccessToken())
                .nickname(findUser.getNickname())
                .email(findUser.getEmail())
                .build();

        refreshTokenRepository.deleteRefreshTokenByTokenEmail(email);
        refreshTokenRepository.save(new RefreshToken(reIssueToken.getRefreshToken(), email));

        return response;
    }



    // 토큰이 존재하는지 확인
    private RefreshToken checkExistToken(String refreshToken) {
        return refreshTokenRepository
                .findRefreshTokenByTokenValue(refreshToken)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.TOKEN_NOT_FOUND));
    }



    // 토큰 재생성 로직
    private AuthDto.Token createReIssueToken(String email, List<String> roles, String refreshToken) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", email);
        claims.put("roles", roles);

        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        String accessToken = jwtTokenizer.generateAccessToken(claims, email, expiration, base64EncodedSecretKey);

        return AuthDto.Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    // 해당 이메일이 존재하는지 확인
    private void verifyExistsEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.USER_EXISTS);
        }
    }
}
