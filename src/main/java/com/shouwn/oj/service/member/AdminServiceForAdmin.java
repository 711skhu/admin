package com.shouwn.oj.service.member;

import java.util.Optional;

import com.shouwn.oj.exception.AlreadyExistException;
import com.shouwn.oj.exception.NotFoundException;
import com.shouwn.oj.model.entity.member.Admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceForAdmin {

	private final AdminService adminService;

	public AdminServiceForAdmin(AdminService adminService) {
		this.adminService = adminService;
	}

	/**
	 * 관리자를 생성하는 메소드
	 *
	 * @param name     관리자 이름
	 * @param username 관리자 아이디
	 * @param password 관리자 패스워드
	 * @param email    관리자 이메일
	 * @return 생성된 관리자 객체
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Admin makeAdmin(String name,
						   String username,
						   String password,
						   String email) {
		Admin admin = Admin.builder()
				.name(name)
				.username(username)
				.password(password)
				.email(email)
				.build();

		checkPossibleToMake(admin);

		return adminService.save(admin);
	}

	private void checkPossibleToMake(Admin admin) {
		if (adminService.isRegisteredUsername(admin.getUsername())) {
			throw new AlreadyExistException(admin.getUsername() + " 은 이미 등록된 아이디입니다.");
		}

		if (adminService.isRegisteredEmail(admin.getEmail())) {
			throw new AlreadyExistException(admin.getEmail() + " 은 이미 등록된 이메일입니다.");
		}
	}

	public Admin findById(Long id) {
		Optional<Admin> admin = adminService.findById(id);

		if (!admin.isPresent()) {
			throw new NotFoundException(id + "에 해당하는 유저가 없습니다.");
		}

		return admin.get();
	}
}
