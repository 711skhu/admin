package com.shouwn.oj.controller.member;

import com.shouwn.oj.exception.member.*;
import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.request.admin.AdminSignUpRequest;
import com.shouwn.oj.model.request.member.MemberLoginRequest;
import com.shouwn.oj.model.response.ApiDataBuilder;
import com.shouwn.oj.model.response.ApiResponse;
import com.shouwn.oj.model.response.CommonResponse;
import com.shouwn.oj.model.response.admin.AdminInformation;
import com.shouwn.oj.security.JwtProvider;
import com.shouwn.oj.service.member.AdminServiceForAdmin;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admins")
public class AdminController {

	private final JwtProvider jwtProvider;

	private final AdminServiceForAdmin adminService;

	public AdminController(JwtProvider jwtProvider, AdminServiceForAdmin adminService) {
		this.jwtProvider = jwtProvider;
		this.adminService = adminService;
	}

	@PostMapping
	public ApiResponse<?> makeAdmin(@RequestBody AdminSignUpRequest signUpRequest) {
		try {
			Admin createdAdmin = adminService.makeAdmin(
					signUpRequest.getName(),
					signUpRequest.getUsername(),
					signUpRequest.getPassword(),
					signUpRequest.getEmail()
			);

			return CommonResponse.builder()
					.status(HttpStatus.CREATED)
					.message("관리자 생성 성공")
					.build();
		} catch (UsernameExistException e) {
			return CommonResponse.builder()
					.status(HttpStatus.CONFLICT)
					.message("아이디 중복")
					.build();
		} catch (PasswordStrengthLeakException e) {
			return CommonResponse.builder()
					.status(HttpStatus.PRECONDITION_FAILED)
					.message("비밀번호 강도가 약함")
					.build();
		} catch (EmailExistException e) {
			return CommonResponse.builder()
					.status(HttpStatus.CONFLICT)
					.message("이메일 중복")
					.build();
		}
	}

	@PostMapping("login")
	public ApiResponse<?> login(@RequestBody MemberLoginRequest loginRequest) {
		Admin admin;

		try {
			admin = adminService.login(loginRequest.getUsername(), loginRequest.getPassword());
		} catch (UsernameNotExistException e) {
			return CommonResponse.builder()
					.status(HttpStatus.PRECONDITION_FAILED)
					.message(loginRequest.getUsername() + " 에 해당하는 사용자 아이디가 없습니다.")
					.build();
		} catch (PasswordIncorrectException e) {
			return CommonResponse.builder()
					.status(HttpStatus.FORBIDDEN)
					.message("비밀번호가 다릅니다.")
					.build();
		}

		String jwt = jwtProvider.generateJwt(admin.getId());

		return CommonResponse.builder()
				.status(HttpStatus.CREATED)
				.message("로그인 성공")
				.data(new ApiDataBuilder().addData("token", jwt).packaging())
				.build();
	}

	@GetMapping("self")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<?> getSelfInformation(@RequestAttribute Long requesterId) {

		Admin myself = adminService.findById(requesterId);
		AdminInformation adminInformation = AdminInformation.builder()
				.name(myself.getName())
				.username(myself.getUsername())
				.email(myself.getEmail())
				.build();

		return CommonResponse.builder()
				.status(HttpStatus.OK)
				.message("개인정보 조회 성공")
				.data(adminInformation)
				.build();
	}
}
