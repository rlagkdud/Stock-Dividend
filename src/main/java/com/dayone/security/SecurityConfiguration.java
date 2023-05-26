package com.dayone.security;//package com.dayone.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final JwtAuthenticationFilter authenticationFilter;

    //     어떤 경로를 허용하고 어떤 권한을 필요로 할건지 정하기
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // rest API로 jwt 인증방식 구현할때 붙여주는 부분
                .csrf().disable()// rest API로 jwt 인증방식 구현할때 붙여주는 부분
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)// rest API로 jwt 인증방식 구현할때 붙여주는 부분
                .and() // 회원가입과 로그인 전에는 권한제어 X
                    .authorizeRequests()
                        .antMatchers("/**/signup", "/**/signin").permitAll()
                .and()
                    .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

    // 개발 관련 경로 API에 접근 할 수 있도록 -> 개발 편의성 증가를 위해
    // h2-console로 접근하는 API까지 인증을 필요로 하면 개발 번거로움
    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/h2-console/**");
    }

    //     spring boot 2.X 부터 추가해 줘야함.
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
