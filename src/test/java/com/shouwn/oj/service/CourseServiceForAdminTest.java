package com.shouwn.oj.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.shouwn.oj.exception.AlreadyExistException;
import com.shouwn.oj.exception.AuthenticationFailedException;
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
		final ArgumentCaptor<Admin> updateAdminCaptor = ArgumentCaptor.forClass(Admin.class);

		courseServiceForAdmin.makeCourse(this.admin.getId(), this.dto.getName(), this.dto.getDescription());

		verify(adminService).findById(this.admin.getId());
		verify(courseService).saveCourse(saveCourseCaptor.capture());
		verify(adminService).updateAdmin(updateAdminCaptor.capture());
	}

	/**
	 * 강좌 생성시 같은 이름 있을 때. AlreadyExistException
	 */
	@Test
	void makeCourseThrowAlreadyExistException() {

		when(adminService.findById(anyLong()))
				.thenReturn(Optional.of(this.admin));

		this.dto.setName(this.course.getName());

		assertThrows(AlreadyExistException.class, ()
				-> courseServiceForAdmin.makeCourse(this.admin.getId(), this.dto.getName(), this.dto.getDescription()));

		verify(courseService, Mockito.times(0)).saveCourse(this.course);
	}

	@Test
	void updateCourseSuccess() {

		when(adminService.findById(anyLong()))
				.thenReturn(Optional.of(this.admin));

		when(courseService.findCourseById(anyLong()))
				.thenReturn(Optional.of(this.course));

		final ArgumentCaptor<Course> saveCaptor = ArgumentCaptor.forClass(Course.class);

		dto.setName("test update name");
		dto.setDescription("test update description");

		courseServiceForAdmin.updateCourse(this.admin.getId(), this.course.getId(),
				this.dto.getName(), this.dto.getDescription(), this.dto.getEnabled());

		verify(adminService).findById(this.admin.getId());
		verify(courseService).findCourseById(this.course.getId());
		verify(courseService).saveCourse(saveCaptor.capture());
	}

	/**
	 * 강좌 조회시 해당 강좌 없을 때. IllegalStateException
	 */
	@Test
	void updateCourseThrowNotFoundException() {

		when(adminService.findById(anyLong()))
				.thenReturn(Optional.of(this.admin));

		assertThrows(NotFoundException.class, ()
				-> courseServiceForAdmin.updateCourse(this.admin.getId(), 3L,
				this.dto.getName(), this.dto.getDescription(), this.dto.getEnabled()));

		verify(adminService).findById(this.admin.getId());
		verify(courseService).findCourseById(3L);
		verify(courseService, Mockito.times(0)).saveCourse(this.course);
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
	 * 강좌 수정시 같은 이름 있을 때. AlreadyExistException
	 */
	@Test
	void updateCourseThrowAlreadyExistException() {
		when(adminService.findById(anyLong()))
				.thenReturn(Optional.of(this.admin));

		when(courseService.findCourseById(anyLong()))
				.thenReturn(Optional.of(this.course));

		this.dto.setName(this.course.getName());

		assertThrows(AlreadyExistException.class, ()
				-> courseServiceForAdmin.updateCourse(this.admin.getId(), this.course.getId(),
				this.dto.getName(), this.dto.getDescription(), this.dto.getEnabled()));

		verify(adminService).findById(this.admin.getId());
		verify(courseService).findCourseById(this.course.getId());
		verify(courseService, Mockito.times(0)).saveCourse(this.course);
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

		final ArgumentCaptor<Course> saveCaptor = ArgumentCaptor.forClass(Course.class);

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
		verify(courseService).saveCourse(saveCaptor.capture());
	}

	// 추후 개발
	@Test
	void deleteCourse() {
	}
}