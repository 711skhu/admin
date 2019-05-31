package com.shouwn.oj.service.problem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.shouwn.oj.exception.AlreadyExistException;
import com.shouwn.oj.exception.AuthenticationFailedException;
import com.shouwn.oj.exception.IllegalStateException;
import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.service.member.AdminService;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseServiceForAdmin {

	private final AdminService adminService;
	private final CourseService courseService;

	public CourseServiceForAdmin(AdminService adminService, CourseService courseService) {
		this.adminService = adminService;
		this.courseService = courseService;
	}

	/**
	 * admin id 에 해당하는 관리자의 강좌 리스트를 가져오는 메소드
	 *
	 * @param adminId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Course> getCourseList(Long adminId) {

		List<Course> courseList = courseService.findCourseByAdminId(adminId);

		return courseList;
	}

	/**
	 * admin Id 에 해당하는 관리자로 강좌를 생성하는 메소드
	 *
	 * @param adminId 강좌를 생성하는 admin 의 id
	 * @param name,   description
	 * @return 생성된 Course 정보 AdminCourseResponse
	 * @throws IllegalStateException adminId 로 Admin 을 찾을 수 없을 때 발생하는 예외
	 * @throws AlreadyExistException 강좌 이름 중복시 발생하는 예외
	 */
	public void makeCourse(Long adminId, String name, String description) {
		Optional<Admin> professor = adminService.findById(adminId);

		List<Course> courseList = professor.get().getCourses();

		if (courseList.stream().anyMatch(c -> StringUtils.equals(c.getName(), name))) {
			throw new AlreadyExistException(name + "라는 이름의 강의를 이미 만들었습니다.");
		}

		Course course = Course.builder()
				.name(name)
				.description(description)
				.professor(professor.get())
				.build();

		courseService.saveCourse(course);
	}

	/**
	 * course id에 해당하는 강좌를 조회하는 메소드.
	 *
	 * @param adminId
	 * @param courseId
	 * @return
	 * @throws IllegalStateException courseId 로 Course 을 찾을 수 없을 때 발생하는 예외
	 */
	@Transactional(readOnly = true)
	public Course getCourse(Long adminId, Long courseId) {
		return courseService.findCourseById(courseId);
	}

	/**
	 * admin id 에 해당하는 관리자로 강좌를 수정하는 메소드
	 *
	 * @param adminId
	 * @param courseId
	 * @param name,    description, enabled
	 * @return 수정된 Course 정보 AdminCourseResponse
	 * @throws IllegalStateException         courseId 로 Course 을 찾을 수 없을 때 발생하는 예외
	 * @throws AuthenticationFailedException 해당 강좌의 생성자가 아닌 다른 사람이 수정하려고 할 때 발생하는 예외
	 */
	@Transactional
	public void updateCourse(Long adminId, Long courseId, String name, String description, Boolean enabled) {

		Course course = courseService.findCourseById(courseId);
		List<Course> courseList = courseService.findCourseByAdminId(adminId);
		if (course == null) {
			throw new IllegalStateException("해당 강좌가 존재하지 않습니다.");
		}
		if (course.getProfessor().getId() != adminId) {
			throw new AuthenticationFailedException("강좌 생성자만 수정할 수 있음.");
		}
		if (courseList.stream().anyMatch(c -> StringUtils.equals(c.getName(), name))) {
			throw new AlreadyExistException(name + "라는 이름의 강의를 이미 만들었습니다.");
		}

		course.setName(name);
		course.setDescription(description);

		// set enabled
		if (enabled == true) {
			course.setEnabled(true);
			course.setActiveDate(LocalDateTime.now());
		} else if (enabled == false) {
			course.setEnabled(false);
			course.getStudents().clear();
		}

		courseService.saveCourse(course);
	}

	/**
	 * 추후 개발
	 * admin id 에 해당하는 관리자로 강좌를 삭제하는 메소드
	 *
	 * @param adminId
	 * @param courseId
	 * @throws IllegalStateException courseId 로 Course 을 찾을 수 없을 때 발생하는 예외
	 */
	@Transactional
	public void deleteCourse(Long adminId, Long courseId) {
		Course course = courseService.findCourseById(courseId);
		// TODO 학생 입장에서 해당 course를 삭제
		// studentService.deleteCourse(courseId);
	}
}
