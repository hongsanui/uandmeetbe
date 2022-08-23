package com.project.uandmeetbe.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.uandmeetbe.code.Code;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        response.setCharacterEncoding("utf-8");

        log.info("JwtExceptionFilter 실행");
        try{
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e){
            //만료 에러
            request.setAttribute("exception", Code.EXPIRED_TOKEN.getCode());
            filterChain.doFilter(request,response);

        } catch (MalformedJwtException e){

            //변조 에러
            request.setAttribute("exception", Code.WRONG_TYPE_TOKEN.getCode());
            filterChain.doFilter(request,response);

        } catch (SignatureException e){
            //형식, 길이 에러
            request.setAttribute("exception", Code.WRONG_TYPE_TOKEN.getCode());
            filterChain.doFilter(request,response);
        }
        log.info("exception filter 실행");
    }
}