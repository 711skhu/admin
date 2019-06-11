package com.shouwn.oj.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.shouwn.oj.exception.AlreadyExistException;
import com.shouwn.oj.exception.AuthenticationFailedException;
import com.shouwn.oj.exception.IllegalStateException;
import com.shouwn.oj.exception.NotFoundException;
import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.entity.member.Student;
import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.model.request.admin.AdminCourseSaveRequest;
import com.shouwn.oj.service.member.AdminService;
import com.shouwn.oj.service.problem.CourseService;
import com.shouwn.oj.service.problem.CourseServiceForAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceForAdminTest {

	@Mock
	private AdminService adminService;

	@Mock
	private CourseService courseService;

	@Mock
	private Student mockStudent;

	@InjectMocks
	private CourseServiceForAdmin courseServiceForAdmin;

	private Admin admin;
	private Course course;
	private AdminCourseSaveRequest dto;
	private List<Course> courseList = new ArrayList<>();

	@BeforeEach
	void setUp() {

		// admin 생성
		this.admin = Admin.builder()
				.name("test_admin")
				.username("test_admin_userName")
				.password("123412345")
				.email("testAdmin@naver.com")
				.build();
		admin.setId(1L);

		// course 생성
		this.course = Course.builder()
				.name("test")
				.description("test description")
				.enabled(false)
				.professor(this.admin)
				.build();
		this.course.setId(1L);

		courseList.add(this.course);
		this.admin.setCourses(courseList);

		// req dto
		this.dto = AdminCourseSaveRequest.builder()
				.name("test_dto")
				.description("test description_dto")
				.enabled(true)
				.build();

	}

	/**
	 * 교수 조회시 해당 교수 없을 때. IllegalStateException (강좌관련 전제조건이라 IllegalState)
	 */
	@Test
	void adminThrowIllegalState() {

		assertThrows(IllegalStateException.class, () -> ReflectionTestUtils.invokeMethod(courseServiceForAdmin, "adminFindByIdOrThrowIllegalState", 3L));

		verify(adminService).findById(3L);
	}

	/**
	 * 강좌 조회시 해당 강좌 없을 때. NotFoundException
	 */
	@Test
	void courseThrowNotFound() {

		assertThrows(NotFoundException.class, () -> ReflectionTestUtils.invokeMethod(courseServiceForAdmin, "courseFindByIdOrThrowNotFound", 3L));

		verify(courseService).findCourseById(3L);
	}

	@Test
	void getCourseList() {

		when(adminService.findById(anyLong()))
				.thenReturn(Optional.of(this.admin));

		List<Course> result = courseServiceForAdmin.getCourseList(this.admin.getId());

		assertEquals(courseList.get(0).getId(), result.get(0).getId());
		assertEquals(courseList.get(0).getName(), result.get(0).getName());
		assertEquals(courseList.get(0).getDescription(), result.get(0).getDescription());
		assertEquals(courseList.get(0).getEnabled(), result.get(0).getEnabled());

		verify(adminService).findById(this.admin.getId());
	}

	@Test
	void makeCourseSuccess() {

		when(adminService.findById(anyLong()))
				.thenReturn(Optional.of(this.admin));

		final ArgumentCaptor<Course> saveCourseCaptor = ArgumentCaptor.forClass(Course.class);

		courseServiceForAdmin.makeCourse(this.admin.getId(), this.dto.getName(), this.dto.getDescription());

		assertEquals(this.admin.getCourses().get(1).getName(), this.dto.getName());
		assertEquals(this.admin.getCourses().get(1).getEnabled(), false);

		verify(adminService).findById(this.admin.getId());
		verify(courseService).saveCourse(saveCourseCaptor.capture());
	}

	@Test
	void updateCourseSuccess() {

		when(adminService.findById(anyLong()))
				.thenReturn(Optional.of(this.admin));

		when(courseService.findCourseById(anyLong()))
				.thenReturn(Optional.of(this.course));

		dto.setName("test update name");
		dto.setDescription("test update description");

		courseServiceForAdmin.updateCourse(this.admin.getId(), this.course.getId(),
				this.dto.getName(), this.dto.getDescription(), this.dto.getEnabled());

		assertEquals(this.course.getName(), this.dto.getName());
		assertEquals(this.course.getDescription(), this.dto.getDescription());
		assertEquals(this.course.getEnabled(), this.dto.getEnabled());

		verify(adminService).findById(this.admin.getId());
		verify(courseService).findCourseById(this.course.getId());
	}

	/**
	 * 해당 강좌의 생성자만 수정할 수 있음. AuthenticationFailedException
	 */
	@Test
	void updateCourseThrowAuthenticationFailedException() {

		when(adminService.findById(anyLong()))
				.thenReturn(Optional.of(this.admin));

		when(courseService.findCourseById(anyLong()))
				.thenReturn(Optional.of(this.course));

		assertThrows(AuthenticationFailedException.class, ()
				-> courseServiceForAdmin.updateCourse(3L, this.course.getId(),
				this.dto.getName(), this.dto.getDescription(), this.dto.getEnabled()));

		verify(adminService).findById(3L);
		verify(courseService).findCourseById(this.course.getId());
		verify(courseService, Mockito.times(0)).saveCourse(this.course);
	}


	/**
	 * 강좌 생성,수정시 같은 이름 있을 때. AlreadyExistException
	 */
	@Test
	void courseThrowAlreadyExistException() {

		this.dto.setName(this.course.getName());

		assertThrows(AlreadyExistException.class, () -> ReflectionTestUtils.invokeMethod(courseServiceForAdmin, "checkCourseName", this.admin.getCourses(), this.dto.getName()));
	}

	/**
	 * 비활성화 Test
	 */
	@Test
	void inactiveCourse() {
		when(adminService.findById(anyLong()))
				.thenReturn(Optional.of(this.admin));

		when(courseService.findCourseById(anyLong()))
				.thenReturn(Optional.of(this.course));

		// set enabled false
		this.dto.setEnabled(false);
		this.course.setName("inactive test");
		this.course.setDescription("inactive test des");
		this.course.setEnabled(false);

		// add student
		List<Student> students = new ArrayList<>();
		students.add(mockStudent);
		this.course.setStudents(students);

		courseServiceForAdmin.updateCourse(this.admin.getId(), this.course.getId(),
				this.dto.getName(), this.dto.getDescription(), this.dto.getEnabled());

		assertTrue(this.course.getStudents().size() == 0);

		verify(adminService).findById(this.admin.getId());
		verify(courseService).findCourseById(this.course.getId());
	}

	// 추후 개발
	@Test
	void deleteCourse() {
	}
}