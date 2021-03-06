package com.shouwn.oj.controller.problem;

import java.util.List;
import java.util.stream.Collectors;

import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.model.request.admin.AdminCourseSaveRequest;
import com.shouwn.oj.model.response.ApiResponse;
import com.shouwn.oj.model.response.CommonResponse;
import com.shouwn.oj.model.response.admin.AdminCourseResponse;
import com.shouwn.oj.service.problem.CourseServiceForAdmin;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController("courses")
@PreAuthorize("isAuthenticated()")
public class CourseController {

	private final CourseServiceForAdmin courseServiceForAdmin;

	public CourseController(CourseServiceForAdmin courseServiceForAdmin) {
		this.courseServiceForAdmin = courseServiceForAdmin;
	}

	private AdminCourseResponse getCourseResponseDto(Course course) {

		AdminCourseResponse response = AdminCourseResponse.builder()
				.id(course.getId())
				.name(course.getName())
				.description(course.getDescription())
				.enabled(course.getEnabled())
				.activeTime(course.getActiveDate())
				.build();

		return response;
	}

	@GetMapping
	public ApiResponse<?> getCourseList(@RequestAttribute Long requesterId) {
		List<Course> courseLists = courseServiceForAdmin.getCourseList(requesterId);

		List<AdminCourseResponse> result = courseLists.stream().map(this::getCourseResponseDto)
				.collect(Collectors.toList());

		return CommonResponse.builder()
				.status(HttpStatus.OK)
				.message("해당 교수의 강좌 목록 리스트 성공")
				.data(result)
				.build();
	}

	@PostMapping
	public ApiResponse<?> makeCourse(@RequestAttribute Long requesterId,
									 @RequestBody AdminCourseSaveRequest dto) {

		courseServiceForAdmin.makeCourse(requesterId, dto.getName(), dto.getDescription());

		return CommonResponse.builder()
				.status(HttpStatus.CREATED)
				.message("강좌 생성 성공")
				.build();
	}

	@GetMapping("/{courseId}")
	public ApiResponse<?> getCourse(@PathVariable Long courseId) {

		Course course = courseServiceForAdmin.getCourse(courseId);

		return CommonResponse.builder()
				.status(HttpStatus.OK)
				.message("해당 강좌 get 성공")
				.data(getCourseResponseDto(course))
				.build();
	}

	@PutMapping("/{courseId}")
	public ApiResponse<?> updateCourse(@RequestAttribute Long requesterId,
									   @PathVariable Long courseId,
									   @RequestBody AdminCourseSaveRequest dto) {

		courseServiceForAdmin.updateCourse(requesterId, courseId, dto.getName(),
				dto.getDescription(), dto.getEnabled());

		return CommonResponse.builder()
				.status(HttpStatus.OK)
				.message("강좌 수정 성공")
				.build();
	}

	@DeleteMapping("/{courseId}")
	public ApiResponse<?> deleteCourse(@RequestAttribute Long requesterId,
									   @PathVariable Long courseId) {

		courseServiceForAdmin.deleteCourse(requesterId, courseId);

		return CommonResponse.builder()
				.status(HttpStatus.NO_CONTENT)
				.message("강좌 삭제 성공")
				.build();
	}
}
