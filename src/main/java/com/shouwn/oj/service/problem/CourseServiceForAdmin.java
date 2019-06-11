package com.shouwn.oj.service.problem;

import java.util.List;

import com.shouwn.oj.exception.AlreadyExistException;
import com.shouwn.oj.exception.AuthenticationFailedException;
import com.shouwn.oj.exception.IllegalStateException;
import com.shouwn.oj.exception.NotFoundException;
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
	 * admin id 로 Admin을 찾으며 예외처리하는 메소드(Course 관련 작업에서 전제조건이기 때문에 IllegalStateException)
	 *
	 * @param adminId
	 * @return admin id 로 찾은 Admin
	 * @throws IllegalStateException adminId 로 Admin을 찾을 수 없을 때 발생하는 예외
	 */
	private Admin adminFindByIdOrThrowIllegalState(Long adminId) {
		return adminService.findById(adminId).orElseThrow(() -> new IllegalStateException("해당 교수가 존재하지 않습니다."));
	}

	/**
	 * course id 로 Course을 찾으며 예외처리하는 메소드
	 *
	 * @param courseId
	 * @return course id 로 찾은 Course
	 * @throws NotFoundException courseId로 Course를 찾을 수 없을 때 발생하는 예외
	 */
	private Course courseFindByIdOrThrowNotFound(Long courseId) {
		return courseService.findCourseById(courseId).orElseThrow(() -> new NotFoundException("해당 강좌가 존재하지 않습니다."));
	}

	/**
	 * 강좌 이름 중복 확인
	 *
	 * @param courseList
	 * @param name
	 * @throws AlreadyExistException 강좌 이름 중복시 발생하는 예외
	 */
	private void checkCourseName(List<Course> courseList, String name) {
		if (courseList.stream().anyMatch(c -> StringUtils.equals(c.getName(), name))) {
			throw new AlreadyExistException(name + "라는 이름의 강의를 이미 만들었습니다.");
		}
	}

	/**
	 * admin id 에 해당하는 관리자의 강좌 리스트를 가져오는 메소드
	 *
	 * @param adminId
	 * @return admin id 에 해당하는 관리자의 강좌 리스트
	 */
	public List<Course> getCourseList(Long adminId) {

		Admin professor = adminFindByIdOrThrowIllegalState(adminId);

		return professor.getCourses();
	}

	/**
	 * admin Id 에 해당하는 관리자로 강좌를 생성하는 메소드
	 *
	 * @param adminId     강좌를 생성하는 admin 의 id
	 * @param name        생성하려는 강좌의 이름
	 * @param description 생성하려는 강좌의 설명
	 */
	public void makeCourse(Long adminId, String name, String description) {

		Admin professor = adminFindByIdOrThrowIllegalState(adminId);

		checkCourseName(professor.getCourses(), name);

		Course course = Course.builder()
				.name(name)
				.description(description)
				.enabled(false)
				.professor(professor)
				.build();

		courseService.saveCourse(course);

		professor.getCourses().add(course);
	}

	/**
	 * course id에 해당하는 강좌를 조회하는 메소드.
	 *
	 * @param courseId
	 * @return course id에 해당하는 강좌
	 */
	public Course getCourse(Long courseId) {
		return courseFindByIdOrThrowNotFound(courseId);
	}

	/**
	 * admin id 에 해당하는 관리자로 강좌를 수정하는 메소드
	 *
	 * @param adminId
	 * @param courseId
	 * @param name
	 * @param description
	 * @param enabled
	 * @throws AuthenticationFailedException 해당 강좌의 생성자가 아닌 다른 사람이 수정하려고 할 때 발생하는 예외
	 */
	@Transactional
	public void updateCourse(Long adminId, Long courseId, String name, String description, Boolean enabled) {

		Admin professor = adminFindByIdOrThrowIllegalState(adminId);
		Course course = courseFindByIdOrThrowNotFound(courseId);

		if (course.getProfessor().getId() != adminId) {
			throw new AuthenticationFailedException("강좌 생성자만 수정할 수 있음.");
		}

		checkCourseName(professor.getCourses(), name);

		course.setName(name);
		course.setDescription(description);

		course.activeCourse(enabled);
	}

	/**
	 * 추후 개발
	 * admin id 에 해당하는 관리자로 강좌를 삭제하는 메소드
	 *
	 * @param adminId
	 * @param courseId
	 */
	@Transactional
	public void deleteCourse(Long adminId, Long courseId) {
		Course course = courseFindByIdOrThrowNotFound(courseId);

		if (course.getProfessor().getId() != adminId) {
			throw new AuthenticationFailedException("강좌 생성자만 수정할 수 있음.");
		}
		// TODO 학생 입장에서 해당 course를 삭제
		// studentService.deleteCourse(courseId);
	}
}
