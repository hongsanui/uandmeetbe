package com.project.uandmeetbe.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * <h1>JwtTokenProvider</h1>
 * <p>
 *     토큰을 생성하고 검증하는 클래스입니다.
 *     해당 컴포넌트는 필터클래스에서 사전 검증을 거칩니다.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    //todo 토큰 키를 암호화 하자!
    //액세스 토큰 키
    private String accessSecretKey = "accesstokentest";

    //리프레시 토큰 키
    private String refreshSecretKey = "refreshtokentest";

    //액세스 토큰 유효시간 = 30분
    private long accessTokenValidTime = 30 * 60 * 1000L;

    //리프레시 토큰 유효시간 = 7일
    private long refreshTokenValidTime = 7 * 24 * 60 * 60 * 1000L;

    private final UserDetailsService userDetailsService;

    //객체 초기화, secretKey 를 base64로 인코딩
    @PostConstruct
    protected void init(){
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.getBytes());
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes());
    }

    //액세스토큰,리프레시 토큰 생성
    public Token createAccessToken(String email, List<String> roles){

        //JWT 페이로드에 저장되는 정보단위, 여기서 member를 식별하는 값을 넣는다.
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles",roles);  //정보는 ("Key",value) 쌍으로 저장된다.
        Date now = new Date();

        //Access Token
        String accessToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
        //Refresh Token
        String refreshToken =  Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .key(email)
                .build();
    }

    //Access Token 재생성

    public String recreationAccessToken(String email, Object roles){

        //JWT 페이로드에 저장되는 정보단위, 여기서 member를 식별하는 값을 넣는다.
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles",roles);  //정보는 ("Key",value) 쌍으로 저장된다.
        Date now = new Date();

        //Access Token
        String accessToken = Jwts.builder()
                .setClaims(claims)  // 정보 저장
                .setIssuedAt(now)   // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))  //만료시간 설정
                .signWith(SignatureAlgorithm.HS512, accessSecretKey)    // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();

        return accessToken;
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) throws ParseException {

        return Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // JWT 토큰에서 인증 정보 조회, 실제 db에서 토큰 정보에 대한 정보를 가져온다. 그리고 userdetails에 대한 정보를 넘겨준다.
    public Authentication getAuthentication(String token) throws ParseException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        log.info("토큰 인증 정보 조회");
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Request의 Header에서 token 값을 가져옵니다. "Authorization" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {

        log.info("토큰 값 가져오기 = {}", request.getHeader("Authorization"));
        return request.getHeader("Authorization");
    }

    // 토큰의 유효성 + 만료일자 확인, 이 부분에서 토큰에 대한 유효성 검증을 하게 된다.
    public boolean validateToken(String jwtToken) throws ParseException, SignatureException {


        // 토큰 만료시 jwtExceptionFilter의 expired exception으로 던져짐, 토큰 변조 시 jwtexceiption으로 던져짐
        Jws<Claims> claims = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(jwtToken);
        log.info("claims의 get email = {}", claims.getBody().get("sub"));

        return !claims.getBody().getExpiration().before(new Date());

    }

    public String validateRefreshToken(RefreshToken refreshTokenObj){


        // refresh 객체에서 refreshToken 추출
        String refreshToken = refreshTokenObj.getRefreshToken();


        try {
            // 검증
            log.info("validateRefreshToken 리프레시 토큰 추출까지 통과");
            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken);

            //refresh 토큰의 만료시간이 지나지 않았을 경우, 새로운 access 토큰을 생성합니다.
            if (!claims.getBody().getExpiration().before(new Date())) {
                return recreationAccessToken(claims.getBody().get("sub").toString(), claims.getBody().get("roles"));
            }
        }catch (Exception e) {

            //refresh 토큰이 만료되었을 경우, 로그인이 필요합니다.

            return null;
        }

        return null;
    }
    // jwt 토큰 payload 복호화 코드입니다. jjwt 라이브러리의 함수는 ExpiredException 에러가 터져 사용하지 않습니다.
    public JSONObject getUserInfoFromJwtToken(String jwtToken) throws ParseException {
        String[] chunks = jwtToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(payload);

        return jsonObject;
    }

    //jwt 복호화 후, 유저 정보 받아오기
    public Claims getClaims(String accessToken) throws SignatureException{

        Jws<Claims> claims = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(accessToken);

        return claims.getBody();
    }

}
