package com.shouwn.oj.config;

import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.security.JwtProperties;
import com.shouwn.oj.security.config.EnableJwtSecurity;
import com.shouwn.oj.security.config.JwtSecurityConfigurerAdapter;
import com.shouwn.oj.service.member.AdminService;
import com.shouwn.oj.service.member.MemberAuthService;

import org.springframework.context.annotation.Configuration;

@Configuration
@EnableJwtSecurity
public class SecurityConfig extends JwtSecurityConfigurerAdapter {

	private final AdminService adminService;

	private final AdminJwtProperties jwtProperties;

	public SecurityConfig(AdminService adminService, AdminJwtProperties jwtProperties) {
		this.adminService = adminService;
		this.jwtProperties = jwtProperties;
	}

	@Override
	public MemberAuthService<Admin> memberServiceUsingSecurity() {
		return this.adminService;
	}

	@Override
	public JwtProperties jwtProperties() {
		return this.jwtProperties;
	}
}
