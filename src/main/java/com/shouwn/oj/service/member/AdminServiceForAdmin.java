package com.shouwn.oj.service.member;

import java.util.Optional;

import com.shouwn.oj.exception.AuthenticationFailedException;
import com.shouwn.oj.exception.NotFoundException;
import com.shouwn.oj.model.entity.member.Admin;

import org.springframework.stereotype.Service;

@Service
public class AdminServiceForAdmin {

	private final AdminService adminService;

	public AdminServiceForAdmin(AdminService adminService) {
		this.adminService = adminService;
	}

	/**
	 * 관리자를 생성하는 메소드
	 *
	 * @param name        관리자 이름
	 * @param username    관리자 아이디
	 * @param rawPassword 관리자 패스워드 (인코딩 되지 않은)
	 * @param email       관리자 이메일
	 * @return 생성된 관리자 객체
	 */
	public Admin makeAdmin(String name,
						   String username,
						   String rawPassword,
						   String email) {
		return adminService.makeAdmin(name, username, rawPassword, email);
	}

	public Admin login(String username, String rawPassword) {
		Optional<Admin> admin = adminService.findByUsername(username);

		if (!admin.isPresent()) {
			throw new NotFoundException(username + "에 해당하는 유저가 없습니다.");
		}

		if (!adminService.isCorrectPassword(admin.get(), rawPassword)) {
			throw new AuthenticationFailedException("비밀번호가 다릅니다.");
		}

		return admin.get();
	}

	public Admin findById(Long id) {
		Optional<Admin> admin = adminService.findById(id);

		if (!admin.isPresent()) {
			throw new NotFoundException(id + "에 해당하는 유저가 없습니다.");
		}

		return admin.get();
	}
}
