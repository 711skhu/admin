package com.shouwn.oj.config.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JwtTestConfig {

	@Bean
	public JwtTestProperties jwtTestProperties() {
		return new JwtTestProperties();
	}
}
