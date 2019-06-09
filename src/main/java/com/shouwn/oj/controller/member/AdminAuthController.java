package com.shouwn.oj.controller.member;

import com.shouwn.oj.exception.InvalidParameterException;
import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.request.admin.AdminSignUpRequest;
import com.shouwn.oj.model.request.auth.ReissueTokenRequest;
import com.shouwn.oj.model.request.member.MemberLoginRequest;
import com.shouwn.oj.model.response.ApiDataBuilder;
import com.shouwn.oj.model.response.ApiResponse;
import com.shouwn.oj.model.response.CommonResponse;
import com.shouwn.oj.security.JwtContext;
import com.shouwn.oj.security.JwtProvider;
import com.shouwn.oj.service.member.AdminAuthServiceForAdmin;
import com.shouwn.oj.service.member.AdminServiceForAdmin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/auth")
public class AdminAuthController {

	private final JwtProvider jwtProvider;

	private final AdminServiceForAdmin adminService;

	private final AdminAuthServiceForAdmin adminAuthService;

	public AdminAuthController(JwtProvider jwtProvider,
							   AdminServiceForAdmin adminService,
							   AdminAuthServiceForAdmin adminAuthService) {
		this.jwtProvider = jwtProvider;
		this.adminService = adminService;
		this.adminAuthService = adminAuthService;
	}

	@PostMapping("token")
	public ApiResponse<?> reissueToken(@RequestBody ReissueTokenRequest reissueTokenRequest) {
		JwtContext tokenContext = jwtProvider.getContextFromJwt(reissueTokenRequest.getRefreshToken());

		if (!tokenContext.isRefreshToken()) {
			throw new InvalidParameterException("리프레시 토큰이 아닙니다.");
		}

		String token = jwtProvider.generateJwt(tokenContext);

		return CommonResponse.builder()
				.status(HttpStatus.CREATED)
				.message("토큰 재발급 완료")
				.data(new ApiDataBuilder().addData("token", token).packaging())
				.build();
	}

	@PostMapping("signUp")
	public ApiResponse<?> signUp(@RequestBody AdminSignUpRequest signUpRequest) {
		adminAuthService.checkPasswordStrength(signUpRequest.getPassword());

		adminService.makeAdmin(
				signUpRequest.getName(),
				signUpRequest.getUsername(),
				adminAuthService.passwordEncode(signUpRequest.getPassword()),
				signUpRequest.getEmail()
		);

		return CommonResponse.builder()
				.status(HttpStatus.CREATED)
				.message("관리자 생성 성공")
				.build();
	}

	@PostMapping("login")
	public ApiResponse<?> login(@RequestBody MemberLoginRequest loginRequest) {
		Admin admin = adminAuthService.login(loginRequest.getUsername(), loginRequest.getPassword());

		String token = jwtProvider.generateJwt(admin.getId(), admin.getRole());
		String refreshToken = jwtProvider.generateRefreshJwt(admin.getId(), admin.getRole());

		return CommonResponse.builder()
				.status(HttpStatus.CREATED)
				.message("로그인 성공")
				.data(new ApiDataBuilder()
						.addData("token", token)
						.addData("refreshToken", refreshToken)
						.packaging())
				.build();
	}
}
