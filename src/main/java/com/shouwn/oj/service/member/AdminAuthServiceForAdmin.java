package com.shouwn.oj.service.member;

import java.util.Optional;

import com.shouwn.oj.exception.AuthenticationFailedException;
import com.shouwn.oj.exception.IllegalStateException;
import com.shouwn.oj.exception.NotFoundException;
import com.shouwn.oj.model.entity.member.Admin;

import org.springframework.stereotype.Service;

@Service
public class AdminAuthServiceForAdmin {

	private final AdminAuthService adminAuthService;

	private final AdminService adminService;

	public AdminAuthServiceForAdmin(AdminAuthService adminAuthService, AdminService adminService) {
		this.adminAuthService = adminAuthService;
		this.adminService = adminService;
	}

	public Admin login(String username, String rawPassword) {
		Optional<Admin> admin = adminService.findByUsername(username);

		if (!admin.isPresent()) {
			throw new NotFoundException(username + "에 해당하는 유저가 없습니다.");
		}

		if (!adminAuthService.isCorrectPassword(admin.get(), rawPassword)) {
			throw new AuthenticationFailedException("비밀번호가 다릅니다.");
		}

		return admin.get();
	}

	public void checkPasswordStrength(String rawPassword) {
		if (adminAuthService.isPasswordStrengthWeak(rawPassword)) {
			throw new IllegalStateException("비밀번호 강도가 약합니다.");
		}
	}

	public String passwordEncode(String rawPassword) {
		return adminAuthService.passwordEncode(rawPassword);
	}
}
