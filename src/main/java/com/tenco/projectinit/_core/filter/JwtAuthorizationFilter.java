package com.tenco.projectinit._core.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenco.projectinit._core.errors.exception.Exception401;
import com.tenco.projectinit._core.utils.JwtTokenUtils;
import com.tenco.projectinit.repository.entity.Partner;
import com.tenco.projectinit.repository.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.io.PrintWriter;
public class JwtAuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;


        AntPathMatcher antPathMatcher = new AntPathMatcher();
        if (
                !(request.getRequestURI().toString().equals("/api/users/login")
                        || antPathMatcher.match("/h2-console/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/mng/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/dist/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/assets/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/images/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/css/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/js/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/scss/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/plugins/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/api/users/sms-send",request.getRequestURI().toString())
                        ||antPathMatcher.match("/api/users/sms-check",request.getRequestURI().toString())
                        ||antPathMatcher.match("/api/users/join",request.getRequestURI().toString())
                        ||antPathMatcher.match("/api/category/first",request.getRequestURI().toString())
                        ||antPathMatcher.match("/api/category/second",request.getRequestURI().toString())
                        ||antPathMatcher.match("/api/option",request.getRequestURI().toString())
                        ||antPathMatcher.match("/reservation/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/api/notice/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/api/info",request.getRequestURI().toString())
                        ||antPathMatcher.match("/api/partner/**",request.getRequestURI().toString())
                        ||antPathMatcher.match("/api/**",request.getRequestURI().toString())

                )
        )
        {
            String jwt = request.getHeader("Authorization");
            if (jwt == null || jwt.isEmpty()) {
                onError(response, "토큰이 없습니다");
                return;
            }


            try {
                DecodedJWT decodedJWT = JwtTokenUtils.verify(jwt);

                int id = decodedJWT.getClaim("id").asInt();
                String userId = decodedJWT.getClaim("tel").asString();
                String picUrl = decodedJWT.getClaim("picUrl").asString();
                HttpSession session = request.getSession();
                if(picUrl == null|| picUrl.isEmpty()){
                    User sessionUser = User.builder().id(id).tel(userId).build();
                    session.setAttribute("sessionUser", sessionUser);
                    System.out.println("SessionUser: " + sessionUser);
                    System.out.println(sessionUser + "담기냐??");
                    Partner partner = Partner.builder().id(id).tel(userId).picUrl(picUrl).build();
                    session.setAttribute("partner", partner);
                }else{
                    Partner partner = Partner.builder().id(id).tel(userId).picUrl(picUrl).build();
                    session.setAttribute("partner", partner);
                }



            } catch (SignatureVerificationException | JWTDecodeException e1) {
                onError(response, "토큰 검증 실패");
            } catch (TokenExpiredException e2) {
                onError(response, "토큰 시간 만료");
            }
        }
        chain.doFilter(request, response);
    }

    // ExceptionHandler를 호출할 수 없다. 왜? Filter니까!! DS전에 작동하니까!!
    private void onError(HttpServletResponse response, String msg) {
        Exception401 e401 = new Exception401(msg);

        try {
            String body = new ObjectMapper().writeValueAsString(e401.body());
            response.setStatus(e401.status().value());
            // response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter out = response.getWriter();
            out.println(body);
        } catch (Exception e) {
            System.out.println("파싱 에러가 날 수 없음");
        }
    }
}