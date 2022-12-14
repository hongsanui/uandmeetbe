package com.project.uandmeetbe.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.uandmeetbe.security.jwt.JwtProperties;
import com.project.uandmeetbe.security.jwt.filter.JwtAuthenticationFilter;
import com.project.uandmeetbe.security.jwt.filter.JwtRefreshFilter;
import com.project.uandmeetbe.security.jwt.handler.CustomLogoutHandler;
import com.project.uandmeetbe.security.jwt.handler.CustomLogoutSuccessHandler;
import com.project.uandmeetbe.security.jwt.handler.JwtAuthenticationFailureHandler;
import com.project.uandmeetbe.security.jwt.handler.JwtTokenRenewalExceptionHandler;
import com.project.uandmeetbe.security.jwt.matcher.FilterSkipMatcher;
import com.project.uandmeetbe.security.jwt.matcher.LogoutRequestMatcher;
import com.project.uandmeetbe.security.jwt.provider.JwtAuthenticationProvider;
import com.project.uandmeetbe.security.jwt.service.JwtService;
import com.project.uandmeetbe.security.jwt.service.RefreshTokenService;
import com.project.uandmeetbe.security.oauth2.handler.OAuth2LoginAuthenticationSuccessHandler;
import com.project.uandmeetbe.security.oauth2.service.CustomOAuth2UserService;
import com.project.uandmeetbe.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.Filter;
import java.util.List;

/**
 * <h1>SecurityConfig</h1>
 * <p>Spring Security ?????? ?????? ?????????</p>
 * <p>??? ???????????? ?????? ????????? ?????? ????????? ????????????.</p>
 * <ul>
 *     <li>????????? ????????? ?????? URL ?????? ??????</li>
 *     <li>security filter ?????? ??? ???????????? ??????</li>
 * </ul>
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // OAuth2 Beans
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2LoginAuthenticationSuccessHandler oAuth2LoginAuthenticationSuccessHandler;

    // jwt beans
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService<User> jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenRenewalExceptionHandler jwtTokenRenewalExceptionHandler;
    @Autowired
    private CustomLogoutHandler customLogoutHandler;
    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    // jwt ?????? ??????
    public Filter jwtRefreshFilter() throws Exception {
        return new JwtRefreshFilter(refreshTokenService,
                jwtProperties,
                jwtService,
                objectMapper,
                new AntPathRequestMatcher("/api/refresh"),
                jwtTokenRenewalExceptionHandler);
    }

    // jwt ?????? ??????
    public Filter jwtAuthenticationFilter() throws Exception {
        FilterSkipMatcher filterSkipMatcher = new FilterSkipMatcher(
                List.of("/api/refresh", "/api/logout"),
                List.of("/api/**")
        );
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(filterSkipMatcher);
        jwtAuthenticationFilter.setAuthenticationManager(super.authenticationManager());
        jwtAuthenticationFilter.setAuthenticationFailureHandler(jwtAuthenticationFailureHandler);
        return jwtAuthenticationFilter;
    }

    // authentication manager setting
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().disable();
        http.httpBasic().disable();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors();

        //logout
        http.logout()
                .logoutRequestMatcher(new LogoutRequestMatcher())
                .addLogoutHandler(customLogoutHandler)
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .permitAll();

        // OAuth2 filter chain configuration
        http.oauth2Login()
                .successHandler(oAuth2LoginAuthenticationSuccessHandler)
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        // JWT Authentication filter chain configuration
        http.addFilterBefore(jwtAuthenticationFilter(), OAuth2AuthorizationRequestRedirectFilter.class);
        http.addFilterBefore(jwtRefreshFilter(), JwtAuthenticationFilter.class);

        // URL security
        http.authorizeRequests()
                .expressionHandler(expressionHandler())
                .antMatchers("/api/a").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/api/user").authenticated()
                .antMatchers("/api/room").permitAll()
                .antMatchers("/api/test").anonymous()
                .antMatchers("/api/websocket").authenticated();

        http.headers().frameOptions().disable();
    }

    // TODO nginx ??? ????????? ????????? ?????? ?????? ????????? ??? ??????
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // role hierarchy expression handler
    @Bean
    public SecurityExpressionHandler<FilterInvocation> expressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_USER > ROLE_ANONYMOUS");
        return roleHierarchy;
    }
}
