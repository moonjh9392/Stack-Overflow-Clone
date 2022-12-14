package com.pre21.security.filter;

import com.pre21.security.jwt.JwtTokenizer;
import com.pre21.security.utils.CustomAuthorityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.pre21.security.utils.JwtConstants.*;


/**
 * Jwt 검증을 위한 클래스
 * @author mozzi327
 */

@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    /**
     * 사용자 요청에 대한 권한 인증 메서드
     * - 쿠키에 저장된 리프레시 토큰을 가져와 데이터베이스에 리프레시 토큰이 존재하는지 확인하고, 액세스 토큰이 유효한지 확인한다.
     * @param req 요청
     * @param res 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블렛에저 지원하는 예외(요청 응답에 대한 예외)
     * @throws IOException 입력 예외
     * @author mozzi327
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String refreshToken = jwtTokenizer.isExistRefresh(req.getCookies());
            jwtTokenizer.verifiedExistRefresh(refreshToken);
            Map<String, Object> claims = verifyJws(req);
            setAuthenticationToContext(claims);
        } catch (SignatureException se) {
            req.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {
            req.setAttribute("exception", ee);
        } catch (Exception e) {
            req.setAttribute("exception", e);
        }

        filterChain.doFilter(req, res);
    }


    /**
     * 헤더에 엑세스 토큰이 존재하는지 유무를 확인하는 메서드
     * @param req 요청
     * @return boolean(액세스 토큰 유무 확인)
     * @author mozzi327
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String authentication = req.getHeader(AUTHORIZATION);
        return authentication == null || !authentication.startsWith(BEARER);
    }


    /**
     * 요청에서 claims 정보를 추출하는 메서드
     * @param req 요청
     * @return Map(String, Object) - claims 정보
     * @author mozzi327
     */
    private Map<String, Object> verifyJws(HttpServletRequest req) {
        String jws = req.getHeader(AUTHORIZATION).replace(BEARER, "");
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        return jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();
    }


    /**
     * 추출한 claims 정보를 SecurityContextHolder context에 등록하는 메서드
     * @param claims claims 정보
     * @author mozzi327
     */
    private void setAuthenticationToContext(Map<String, Object> claims) {
        String email = (String) claims.get("username");
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List) claims.get("roles"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
