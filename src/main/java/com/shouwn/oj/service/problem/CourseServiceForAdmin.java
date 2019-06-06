package com.shouwn.oj.service.problem;

import java.util.ArrayList;
import java.util.List;

import com.shouwn.oj.exception.AlreadyExistException;
import com.shouwn.oj.exception.AuthenticationFailedException;
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
	 * admin id 에 해당하는 관리자의 강좌 리스트를 가져오는 메소드
	 *
	 * @param adminId
	 * @return
	 * @throws NotFoundException adminId 로 해당 강좌를 찾을 수 없을 때 발생하는 예외
	 */
	@Transactional(readOnly = true)
	public List<Course> getCourseList(Long adminId) {

		Admin professor = adminService.findById(adminId).orElseThrow(() -> new NotFoundException("해당 교수가 존재하지 않습니다."));

		List<Course> courseList = professor.getCourses();

		if (courseList == null) {
			throw new NotFoundException("해당 교수의 강좌가 없습니다.");
		}

		return courseList;
	}

	/**
	 * admin Id 에 해당하는 관리자로 강좌를 생성하는 메소드
	 *
	 * @param adminId 강좌를 생성하는 admin 의 id
	 * @param name,   description
	 * @return 생성된 Course 정보 AdminCourseResponse
	 * @throws NotFoundException     adminId 로 Admin 을 찾을 수 없을 때 발생하는 예외
	 * @throws AlreadyExistException 강좌 이름 중복시 발생하는 예외
	 */
	public void makeCourse(Long adminId, String name, String description) {
		Admin professor = adminService.findById(adminId).orElseThrow(() -> new NotFoundException("해당 교수가 존재하지 않습니다."));

		List<Course> courseList = professor.getCourses();

		if(courseList == null){
			courseList = new ArrayList<>();
		}else if (courseList.stream().anyMatch(c -> StringUtils.equals(c.getName(), name))) {
			throw new AlreadyExistException(name + "라는 이름의 강의를 이미 만들었습니다.");
		}

		Course course = Course.builder()
				.name(name)
				.description(description)
				.professor(professor)
				.build();

		courseService.saveCourse(course);

		courseList.add(course);

		professor.setCourses(courseList);

		adminService.updateAdmin(professor);
	}

	/**
	 * course id에 해당하는 강좌를 조회하는 메소드.
	 *
	 * @param courseId
	 * @return
	 * @throws NotFoundException courseId 로 Course 을 찾을 수 없을 때 발생하는 예외
	 */
	@Transactional(readOnly = true)
	public Course getCourse(Long courseId) {
		return courseService.findCourseById(courseId).orElseThrow(() -> new NotFoundException("해당 강좌가 존재하지 않습니다."));
	}

	/**
	 * admin id 에 해당하는 관리자로 강좌를 수정하는 메소드
	 *
	 * @param adminId
	 * @param courseId
	 * @param name,    description, enabled
	 * @return 수정된 Course 정보 AdminCourseResponse
	 * @throws NotFoundException             courseId 로 Course 을 찾을 수 없을 때 발생하는 예외
	 * @throws AuthenticationFailedException 해당 강좌의 생성자가 아닌 다른 사람이 수정하려고 할 때 발생하는 예외
	 * @throws AlreadyExistException         강좌 이름 중복시 발생하는 예외
	 */
	@Transactional
	public void updateCourse(Long adminId, Long courseId, String name, String description, Boolean enabled) {
		Admin professor = adminService.findById(adminId).orElseThrow(() -> new NotFoundException("해당 교수가 존재하지 않습니다."));
		Course course = courseService.findCourseById(courseId).orElseThrow(() -> new NotFoundException("해당 강좌가 존재하지 않습니다."));

		List<Course> courseList = professor.getCourses();

		if (course.getProfessor().getId() != adminId) {
			throw new AuthenticationFailedException("강좌 생성자만 수정할 수 있음.");
		}
		if (courseList.stream().anyMatch(c -> StringUtils.equals(c.getName(), name))) {
			throw new AlreadyExistException(name + "라는 이름의 강의를 이미 만들었습니다.");
		}

		course.setName(name);
		course.setDescription(description);

		course.activeCourse(enabled);

		courseService.saveCourse(course);
	}

	/**
	 * 추후 개발
	 * admin id 에 해당하는 관리자로 강좌를 삭제하는 메소드
	 *
	 * @param adminId
	 * @param courseId
	 * @throws NotFoundException courseId 로 Course 을 찾을 수 없을 때 발생하는 예외
	 */
	@Transactional
	public void deleteCourse(Long adminId, Long courseId) {
		Course course = courseService.findCourseById(courseId).orElseThrow(() -> new NotFoundException("해당 강좌가 존재하지 않습니다."));
		if (course.getProfessor().getId() != adminId) {
			throw new AuthenticationFailedException("강좌 생성자만 수정할 수 있음.");
		}
		// TODO 학생 입장에서 해당 course를 삭제
		// studentService.deleteCourse(courseId);
	}
}
