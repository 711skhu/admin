package com.shouwn.oj.service.problem;

import javax.persistence.EntityNotFoundException;

import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.model.request.admin.AdminCourseSaveRequest;
import com.shouwn.oj.model.response.admin.AdminCourseSaveResponse;
import com.shouwn.oj.repository.problem.CourseRepository;
import com.shouwn.oj.service.member.AdminService;

import com.shouwn.oj.service.member.StudentService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CourseServiceForAdmin {

	private final AdminService adminService;
	private final CourseService courseService;
	private final StudentService studentService;
	private final CourseRepository courseRepository;

	public CourseServiceForAdmin(CourseRepository courseRepository,
							     AdminService adminService,
								 CourseService courseService,
								 StudentService studentService) {
		this.courseRepository = courseRepository;
		this.adminService = adminService;
		this.courseService = courseService;
		this.studentService = studentService;
	}

	private AdminCourseSaveResponse responseDto(Course course){

		AdminCourseSaveResponse response = AdminCourseSaveResponse.builder()
				.courseId(course.getId())
				.courseName(course.getName())
				.courseDescription(course.getDescription())
				.enabled(course.getEnabled())
				.problems(course.getProblems())
				.build();

		return response;
	}

	/**
	 * admin Id 에 해당하는 관리자로 강좌를 생성하는 메소드
	 *
	 * @param adminId 강좌를 생성하는 admin 의 id
	 * @param dto 강좌 생성 req dto ( 강좌이름, 강좌내용)
	 * @return 생성된 Course 정보 AdminCourseSaveResponse
	 * @throws EntityNotFoundException adminId 로 Admin 을 찾을 수 없을 때 발생하는 예외
	 */
	public AdminCourseSaveResponse makeCourse(Long adminId, AdminCourseSaveRequest dto) throws EntityNotFoundException {

		Admin professor = adminService.findById(adminId);
		Course course = professor.makeCourse(dto);

		return responseDto(course);
	}

	/**
	 * admin id 에 해당하는 관리자로 강좌를 수정하는 메소드
	 *
	 * @param adminId
	 * @param courseId
	 * @param dto
	 * @return 수정된 Course 정보 AdminCourseSaveResponse
	 * @throws EntityNotFoundException
	 */
	public AdminCourseSaveResponse updateCourse(Long adminId, Long courseId, AdminCourseSaveRequest dto) throws EntityNotFoundException{

		Admin professor = adminService.findById(adminId);
		Course findCourse = courseService.findCourseById(courseId); // 해당 강좌 존재 유무
		Course course = professor.updateCourse(findCourse.getId(),dto);

		return responseDto(course);
	}

	/**
	 * 상세페이지가 아닌, 강좌 리스트에서 admin id 에 해당하는 관리자로 활성화,비활성화 작업 메소드
	 *
	 * @param adminId
	 * @param courseId
	 * @param enabled
	 * @throws EntityNotFoundException
	 */
	public void inactiveCourse(Long adminId, Long courseId, Boolean enabled) throws EntityNotFoundException{

		Admin professor = adminService.findById(adminId);

		professor.inactiveCourse(courseId, enabled);
	}

	/**
	 * admin id 에 해당하는 관리자로 강좌를 삭제하는 메소드
	 * @param adminId
	 * @param courseId
	 * @throws EntityNotFoundException
	 */
	public void deleteCourse(Long adminId, Long courseId) throws EntityNotFoundException{

		Admin professor = adminService.findById(adminId);

		professor.deleteCourse(courseId);

		// TODO 학생 입장에서 해당 course를 삭제
		// studentService.deleteCourse(courseId);

	}
}
