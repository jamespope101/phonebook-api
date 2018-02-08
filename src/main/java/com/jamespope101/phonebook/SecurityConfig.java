package com.jamespope101.phonebook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Created by jpope on 08/02/2018.
 */
@Configuration
@Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String READER_ROLE = "READER";
    private static final String ALL_RESOURCES = "/phonebook/**";

    @Value("${auth.admin.username}")
    private String adminUsername;
    @Value("${auth.admin.password}")
    private String adminPassword;
    @Value("${auth.reader.username}")
    private String readerUsername;
    @Value("${auth.reader.password}")
    private String readerPassword;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.requiresChannel()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, ALL_RESOURCES).hasAnyRole(READER_ROLE, ADMIN_ROLE)
            .antMatchers(HttpMethod.POST, ALL_RESOURCES).hasRole(ADMIN_ROLE)
            .antMatchers(HttpMethod.PUT, ALL_RESOURCES).hasRole(ADMIN_ROLE)
            .antMatchers(HttpMethod.DELETE, ALL_RESOURCES).hasRole(ADMIN_ROLE)
            .anyRequest().permitAll().and().httpBasic().realmName("Phonebook-API")
            .and().csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // OK to throw Exception
        auth.inMemoryAuthentication()
            .withUser(adminUsername).password(adminPassword).roles(ADMIN_ROLE).and()
            .withUser(readerUsername).password(readerPassword).roles(READER_ROLE);
    }

}

