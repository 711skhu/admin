package com.shouwn.oj.config;

import com.shouwn.oj.security.JwtProperties;
import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter

@ConfigurationProperties(prefix = "security.jwt")
public class AdminJwtProperties implements JwtProperties {

	private String secretKey;
}
