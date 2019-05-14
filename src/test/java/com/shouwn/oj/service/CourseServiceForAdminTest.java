package com.shouwn.oj.service;

import com.shouwn.oj.config.repository.RepositoryTestConfig;
import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.model.request.admin.AdminCourseSaveRequest;
import com.shouwn.oj.model.response.admin.AdminCourseSaveResponse;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @InjectMocks
    private CourseServiceForAdmin courseServiceForAdmin;

    private Admin makeAdmin;
    private Course c;
    private AdminCourseSaveRequest dto;

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

        //TODO : Error - actually there were zero interactions with this mock
        //Mockito.verify(adminService).makeAdmin("test_user", "test id", "123412345", "naver.com");

        makeAdmin = adminService.makeAdmin(anyString(), anyString(), anyString(), anyString());

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
                .courseName("test")
                .courseDescription("test description")
                .build();

    }

    @Test
    void makeCourse() {

        when(adminService.findById(anyLong()))
                .thenReturn(makeAdmin);

        Course course = makeAdmin.makeCourse(dto.getCourseName(), dto.getCourseDescription());

        assertEquals(this.c.getName(), course.getName());
        assertEquals(this.c.getDescription(), course.getDescription());
        assertEquals(this.c.getEnabled(), course.getEnabled());
        assertEquals(this.c.getProfessor(), course.getProfessor());


        // TODO : 에러 However, there was exactly 1 interaction with this mock: adminService.makeAdmin("", "", "", "");
        //Mockito.verify(adminService).findById(makeAdmin.getId());
    }


    /**
     * 강좌 생성시 같은 이름 있을 때. EntityExistsException
     */
    @Test
    void makeCourseThrowEntityExistsException() {
        List<Course> courses = new ArrayList<>();
        courses.add(this.c);
        makeAdmin.setCourses(courses);

        assertThrows(EntityExistsException.class, () -> makeAdmin.makeCourse(dto.getCourseName(), dto.getCourseDescription()));
    }

    @Test
    void updateCourse() {

        when(adminService.findById(anyLong()))
                .thenReturn(makeAdmin);

        when(courseService.findCourseById(anyLong()))
                .thenReturn(this.c);

        dto.setCourseName("test update");
        c.setName("test update");

        List<Course> courseList = new ArrayList<>();
        courseList.add(this.c);
        makeAdmin.setCourses(courseList);

        Course course = makeAdmin.updateCourse(c.getId(), dto.getCourseName(), dto.getCourseDescription());

        assertEquals(dto.getCourseName(), course.getName());
        assertEquals(dto.getCourseDescription(), course.getDescription());

        // TODO
        //Mockito.verify(adminService).findById(makeAdmin.getId());
        //Mockito.verify(courseService).findCourseById(this.c.getId());
    }

    @Test
    void activeCourse() {
    }

    @Test
    void deleteCourse() {
    }
}