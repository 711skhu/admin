package com.shouwn.oj.controller.problem;

import javax.persistence.EntityNotFoundException;

import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.model.response.ApiResponse;
import com.shouwn.oj.model.response.CommonResponse;
import com.shouwn.oj.service.problem.CourseService;
import com.shouwn.oj.service.problem.CourseServiceForAdmin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriBuilder;

@RestController("course")
public class CourseController {

	private final CourseService courseService;
	private final CourseServiceForAdmin courseServiceForAdmin;

	public CourseController(CourseService courseService,
							CourseServiceForAdmin courseServiceForAdmin) {
		this.courseService = courseService;
		this.courseServiceForAdmin = courseServiceForAdmin;
	}

	@PostMapping
	public ApiResponse<?> makeCourse(@RequestParam Long requesterId, // TODO 일반 adminId 가 아니라 Spring Security 에 의한 사용자 id가 필요
									 @RequestParam String courseName,
									 @RequestParam String courseDescription,
									 UriBuilder uriBuilder) {

		Course newCourse = courseServiceForAdmin.makeCourse(requesterId, courseName, courseDescription);

		return CommonResponse.builder()
				.status(HttpStatus.CREATED.value())
				.message("강좌 생성 성공")
				.data(uriBuilder.path("/course/{id}").build(newCourse.getId()))
				.build();
	}

	@GetMapping("{courseId}")
	public ApiResponse<?> getCourse(@PathVariable Long courseId) {

		Course course;

		try {
			course = courseService.findCourseById(courseId);
		} catch (EntityNotFoundException e) {
			return CommonResponse.builder()
					.status(HttpStatus.NOT_FOUND.value())
					.message(courseId + "에 해당하는 강좌가 없습니다.")
					.build();
		}

		return CommonResponse.builder()
				.status(HttpStatus.OK.value())
				.message("강좌 찾기 성공")
				.data(course)
				.build();
	}
}
