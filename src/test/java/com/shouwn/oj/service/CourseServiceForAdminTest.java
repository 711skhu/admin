package com.shouwn.oj.service;

import com.shouwn.oj.config.repository.RepositoryTestConfig;
import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.entity.member.Student;
import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.model.request.admin.AdminCourseSaveRequest;
import com.shouwn.oj.model.response.admin.AdminCourseList;
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
    private List<Course> courseList =  new ArrayList<>();

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
                .courseName("test")
                .courseDescription("test description")
                .build();

    }

    @Test
    void getCourseList(){
        when(adminService.findById(anyLong()))
                .thenReturn(makeAdmin);

        Admin a = adminService.findById(anyLong());

        courseList.add(this.c);

        when(courseService.findCourseByAdminId(a.getId()))
                .thenReturn(this.courseList);

        List<Course> findCourse = courseService.findCourseByAdminId(a.getId());
        List<AdminCourseList> test = new ArrayList<>();

        for(Course course : findCourse){
            AdminCourseList listDto = AdminCourseList.builder()
                    .courseId(course.getId())
                    .courseName(course.getName())
                    .courseDescription(course.getDescription())
                    .enabled(course.getEnabled())
                    .build();
            test.add(listDto);
        }

        assertEquals(courseList.get(0).getId(), test.get(0).getCourseId());
        assertEquals(courseList.get(0).getName(), test.get(0).getCourseName());
        assertEquals(courseList.get(0).getDescription(), test.get(0).getCourseDescription());

        Mockito.verify(adminService).findById(anyLong());
        Mockito.verify(courseService).findCourseByAdminId(anyLong());
    }

    @Test
    void makeCourse() {
        when(adminService.findById(anyLong()))
                .thenReturn(makeAdmin);

        Admin admin = adminService.findById(anyLong());

        Course course = admin.makeCourse(dto.getCourseName(), dto.getCourseDescription());

        assertEquals(this.c.getName(), course.getName());
        assertEquals(this.c.getDescription(), course.getDescription());
        assertEquals(this.c.getEnabled(), course.getEnabled());
        assertEquals(this.c.getProfessor(), course.getProfessor());

        Mockito.verify(adminService).findById(anyLong());
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

        Admin admin = adminService.findById(anyLong());

        when(courseService.findCourseById(anyLong()))
                .thenReturn(this.c);

        Course findCourse = courseService.findCourseById(anyLong());

        dto.setCourseName("test update");
        findCourse.setName("test update");

        List<Course> courseList = new ArrayList<>();
        courseList.add(findCourse);
        admin.setCourses(courseList);

        Course course = admin.updateCourse(findCourse.getId(), dto.getCourseName(), dto.getCourseDescription());

        assertEquals(dto.getCourseName(), course.getName());
        assertEquals(dto.getCourseDescription(), course.getDescription());

        Mockito.verify(adminService).findById(anyLong());
        Mockito.verify(courseService).findCourseById(anyLong());
    }

    @Test
    void activeCourse() {
        when(adminService.findById(anyLong()))
                .thenReturn(makeAdmin);

        Admin admin = adminService.findById(anyLong());

        when(courseService.findCourseById(anyLong()))
                .thenReturn(this.c);

        Course findCourse = courseService.findCourseById(anyLong());

        findCourse.setEnabled(true);
        findCourse.setActiveDate(LocalDateTime.now());

        Course activeCourse = admin.activeCourse(findCourse.getId(),true);

        assertEquals(findCourse.getEnabled(), activeCourse.getEnabled());
        assertEquals(findCourse.getActiveDate(), activeCourse.getActiveDate());

        Mockito.verify(adminService).findById(anyLong());
        Mockito.verify(courseService).findCourseById(anyLong());
    }

    @Test
    void inactiveCourse(){
        when(adminService.findById(anyLong()))
                .thenReturn(makeAdmin);

        Admin admin = adminService.findById(anyLong());

        when(courseService.findCourseById(anyLong()))
                .thenReturn(this.c);

        Course findCourse = courseService.findCourseById(anyLong());

        Student student = Student.builder()
                .name("student test")
                .username("student userName test")
                .email("student@naver.com")
                .password("1234512345")
                .build();

        List<Student> students = new ArrayList<>();
        students.add(student);

        findCourse.setEnabled(false);
        findCourse.setStudents(students);
        Course inActiveCourse = admin.activeCourse(findCourse.getId(),false);

        System.out.println("student 삭제확인: " + inActiveCourse.getStudents().get(0));
        assertEquals(findCourse.getEnabled(), inActiveCourse.getEnabled());

        Mockito.verify(adminService).findById(anyLong());
        Mockito.verify(courseService).findCourseById(anyLong());
    }

    @Test
    void deleteCourse() {
    }
}