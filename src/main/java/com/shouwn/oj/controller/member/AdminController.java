package com.shouwn.oj.controller.member;

import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.response.ApiResponse;
import com.shouwn.oj.model.response.CommonResponse;
import com.shouwn.oj.model.response.admin.AdminInformation;
import com.shouwn.oj.security.CurrentUser;
import com.shouwn.oj.service.member.AdminServiceForAdmin;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admins")
public class AdminController {

	private final AdminServiceForAdmin adminService;

	public AdminController(AdminServiceForAdmin adminService) {
		this.adminService = adminService;
	}

	@GetMapping("own")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<?> getSelfInformation(@CurrentUser Long memberId) {

		Admin myself = adminService.findById(memberId);

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
