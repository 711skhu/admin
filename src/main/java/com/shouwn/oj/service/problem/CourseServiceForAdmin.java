package com.shouwn.oj.service.problem;

import javax.persistence.EntityNotFoundException;

import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.repository.problem.CourseRepository;
import com.shouwn.oj.service.member.AdminService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CourseServiceForAdmin {

	private final AdminService adminService;

	private final CourseRepository courseRepository;

	public CourseServiceForAdmin(CourseRepository courseRepository,
							   AdminService adminService) {
		this.courseRepository = courseRepository;
		this.adminService = adminService;
	}

	/**
	 * admin Id 에 해당하는 관리자로 강좌를 생성하는 메소드
	 *
	 * @param adminId 강좌를 생성하는 admin 의 id
	 * @param courseName 강좌 이름
	 * @param courseDescription 강좌 설명
	 * @return 생성된 Course 객체
	 * @throws EntityNotFoundException adminId 로 Admin 을 찾을 수 없을 때 발생하는 예외
	 */
	public Course makeCourse(Long adminId, String courseName, String courseDescription) throws EntityNotFoundException {
		Admin professor = adminService.findAdminById(adminId);

		return professor.makeCourse(courseName, courseDescription);
	}
}
