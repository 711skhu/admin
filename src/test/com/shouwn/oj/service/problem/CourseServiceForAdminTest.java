package com.shouwn.oj.service.problem;

import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.model.request.admin.AdminCourseSaveRequest;
import com.shouwn.oj.model.response.admin.AdminCourseSaveResponse;
import com.shouwn.oj.repository.member.AdminRepository;
import com.shouwn.oj.repository.problem.CourseRepository;
import com.shouwn.oj.service.member.AdminService;
import com.shouwn.oj.service.member.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CourseServiceForAdmin.class)
class CourseServiceForAdminTest {

    @Mock
    private AdminService adminService;
    @Mock
    private CourseService courseService;
    @Mock
    private StudentService studentService;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private CourseRepository courseRepository;

    private CourseServiceForAdmin courseServiceForAdmin;

    @Autowired
    CourseServiceForAdminTest(CourseServiceForAdmin courseServiceForAdmin) {
        this.courseServiceForAdmin = courseServiceForAdmin;
    }

    private Admin admin;
    private Course course;

    CourseServiceForAdminTest() {
    }

    @BeforeEach
    void setUp() {
        this.admin = Admin.builder()
                .username("test_user")
                .password("1234")
                .build();
    }

    @Test
    void makeCourse() {
        AdminCourseSaveRequest dto = AdminCourseSaveRequest.builder()
                .courseName("test course name")
                .courseDescription("test description")
                .build();

        Mockito.when(adminService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(this.admin);
        Admin admin = adminService.findById(this.admin.getId());
        assertEquals(this.admin, admin);
        Mockito.verify(adminService).findById(this.admin.getId());

        Course course = admin.makeCourse(dto.getCourseName(), dto.getCourseDescription());
        assertEquals(dto.getCourseName(), course.getName());
        assertEquals(dto.getCourseDescription(), course.getDescription());
    }

    @Test
    void updateCourse() {
    }

    @Test
    void activeCourse() {
    }

    @Test
    void deleteCourse() {
    }
}