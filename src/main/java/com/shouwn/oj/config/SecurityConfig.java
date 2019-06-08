package com.shouwn.oj.config;

import com.shouwn.oj.security.JwtProperties;
import com.shouwn.oj.security.config.EnableJwtSecurity;
import com.shouwn.oj.security.config.JwtSecurityConfigurerAdapter;

import org.springframework.context.annotation.Configuration;

@Configuration
@EnableJwtSecurity
public class SecurityConfig extends JwtSecurityConfigurerAdapter {

	private final AdminJwtProperties jwtProperties;

	public SecurityConfig(AdminJwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	@Override
	public JwtProperties jwtProperties() {
		return this.jwtProperties;
	}
}
