package com.shouwn.oj.service.member;

import com.shouwn.oj.exception.member.MemberException;
import com.shouwn.oj.exception.member.PasswordIncorrectException;
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
	 * @throws MemberException UsernameExistException 이미 아이디가 존재할 때 발생하는 예외
	 *                         PasswordStrengthLeakException 비밀번호가 약할 때 발생하는 예외
	 *                         EmailExistException 이메일이 이미 존재할 때 발생하는 예외
	 */
	public Admin makeAdmin(String name,
						   String username,
						   String rawPassword,
						   String email) throws MemberException {
		return adminService.makeAdmin(name, username, rawPassword, email);
	}

	public Admin login(String username, String rawPassword) throws MemberException {
		Admin admin = adminService.findByUsername(username);

		if (!adminService.isCorrectPassword(admin, rawPassword)) {
			throw new PasswordIncorrectException("패스워드가 맞지 않습니다.");
		}

		return admin;
	}

	public Admin findById(Long id) {
		return adminService.findById(id);
	}
}
