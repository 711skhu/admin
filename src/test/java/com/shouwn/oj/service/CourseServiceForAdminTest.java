package com.shouwn.oj.service;

import com.shouwn.oj.config.repository.RepositoryTestConfig;
import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.entity.member.Student;
import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.model.request.admin.AdminCourseSaveRequest;
import com.shouwn.oj.model.response.admin.AdminCourseResponse;
import com.shouwn.oj.service.member.AdminService;
import com.shouwn.oj.service.problem.CourseService;
import com.shouwn.oj.service.problem.CourseServiceForAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(RepositoryTestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourseServiceForAdminTest {

    @Mock
    private AdminService adminService;

    @Mock
    private CourseService courseService;

    @Mock
    private Student mockStudent;

    @InjectMocks
    private CourseServiceForAdmin courseServiceForAdmin;

    private Admin makeAdmin;
    private Course c;
    private AdminCourseSaveRequest dto;
    private List<Course> courseList = new ArrayList<>();

    @BeforeEach
    void setUp() {

        // admin 생성
        Admin admin = Admin.builder()
                .name("test_user")
                .username("test id")
                .password("123412345")
                .email("naver.com")
                .build();

        admin.setId(1L);

        when(adminService.makeAdmin(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(admin);

        makeAdmin = adminService.makeAdmin(anyString(), anyString(), anyString(), anyString());

        Mockito.verify(adminService).makeAdmin(anyString(), anyString(), anyString(), anyString());

        // course 생성
        this.c = Course.builder()
                .name("test")
                .description("test description")
                .enabled(false)
                .professor(makeAdmin)
                .build();
        this.c.setId(1L);

        // req dto
        this.dto = AdminCourseSaveRequest.builder()
                .name("test")
                .description("test description")
                .enabled(false)
                .build();

    }

    @Test
    void getCourseList() {

        courseList.add(this.c);

        when(courseService.findCourseByAdminId(anyLong()))
                .thenReturn(this.courseList);

        List<AdminCourseResponse> result = courseServiceForAdmin.getCourseList(makeAdmin.getId());

        assertEquals(courseList.get(0).getId(), result.get(0).getId());
        assertEquals(courseList.get(0).getName(), result.get(0).getName());
        assertEquals(courseList.get(0).getDescription(), result.get(0).getDescription());
        assertEquals(courseList.get(0).getEnabled(), result.get(0).getEnabled());

        Mockito.verify(courseService).findCourseByAdminId(this.makeAdmin.getId());
    }

    @Test
    void makeCourse() {
        when(adminService.findById(anyLong()))
                .thenReturn(makeAdmin);

        AdminCourseResponse result = courseServiceForAdmin.makeCourse(this.makeAdmin.getId(), this.dto);

        assertEquals(this.c.getName(), result.getName());
        assertEquals(this.c.getDescription(), result.getDescription());
        assertEquals(this.c.getEnabled(), result.getEnabled());

        Mockito.verify(adminService).findById(this.makeAdmin.getId());
    }

    /**
     * 강좌 생성시 같은 이름 있을 때. EntityExistsException
     */
    @Test
    void makeCourseThrowEntityExistsException() {
        List<Course> courses = new ArrayList<>();
        courses.add(this.c);
        makeAdmin.setCourses(courses);

        assertThrows(EntityExistsException.class, () -> makeAdmin.makeCourse(dto.getName(), dto.getDescription()));
    }

    @Test
    void updateCourse() {

        when(adminService.findById(anyLong()))
                .thenReturn(makeAdmin);

        when(courseService.findCourseById(anyLong()))
                .thenReturn(this.c);

        dto.setName("test update");
        this.c.setName(dto.getName());

        // add to admin
        courseList.clear();
        courseList.add(this.c);

        this.makeAdmin.setCourses(courseList);

        //result
        AdminCourseResponse result = courseServiceForAdmin.updateCourse(this.makeAdmin.getId(), this.c.getId(), this.dto);

        assertEquals(this.c.getId(), result.getId());
        assertEquals(this.c.getName(), result.getName());
        assertEquals(this.c.getDescription(), result.getDescription());

        Mockito.verify(adminService).findById(this.makeAdmin.getId());
        Mockito.verify(courseService).findCourseById(this.c.getId());
    }

    @Test
    void activeCourse() {
        when(adminService.findById(anyLong()))
                .thenReturn(makeAdmin);

        when(courseService.findCourseById(anyLong()))
                .thenReturn(this.c);

        // set enabled true
        this.dto.setEnabled(true);

        this.c.setEnabled(true);
        this.c.setActiveDate(LocalDateTime.now());

        // add to admin
        courseList.clear();
        courseList.add(this.c);
        this.makeAdmin.setCourses(courseList);

        AdminCourseResponse result = courseServiceForAdmin.updateCourse(this.makeAdmin.getId(), this.c.getId(), this.dto);

        assertEquals(this.c.getEnabled(), result.getEnabled());
        assertEquals(this.c.getActiveDate(), result.getActiveTime());

        Mockito.verify(adminService).findById(this.makeAdmin.getId());
        Mockito.verify(courseService).findCourseById(this.c.getId());
    }

    @Test
    void inactiveCourse() {
        when(adminService.findById(anyLong()))
                .thenReturn(makeAdmin);

        when(courseService.findCourseById(anyLong()))
                .thenReturn(this.c);

        // set enabled false
        this.dto.setEnabled(false);

        this.c.setEnabled(false);

        // add to admin
        courseList.clear();
        courseList.add(this.c);
        this.makeAdmin.setCourses(courseList);

        List<Student> students = new ArrayList<>();
        students.add(mockStudent);
        this.c.setStudents(students);

        AdminCourseResponse result = courseServiceForAdmin.updateCourse(this.makeAdmin.getId(), this.c.getId(), this.dto);

        assertEquals(this.c.getEnabled(), result.getEnabled());
        assertTrue(this.c.getStudents().size()==0);

        Mockito.verify(adminService).findById(this.makeAdmin.getId());
        Mockito.verify(courseService).findCourseById(this.c.getId());
    }

    // 추후 개발
    @Test
    void deleteCourse() {
    }
}