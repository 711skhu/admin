package com.shouwn.oj.service;

import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.repository.problem.CourseRepository;
import com.shouwn.oj.service.member.AdminService;
import com.shouwn.oj.service.problem.CourseServiceForAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CourseServiceForAdmin.class)
public class CourseServiceForAdminTest {

    @Mock
    private AdminService adminService;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceForAdmin courseServiceForAdmin;

    private Admin admin;
    private Course course;

    /**
     * Admin professor = adminService.findById(adminId);
     *
     * 		Course course = Course.builder()
     * 				.name(courseName)
     * 				.description(courseDescription)
     * 				.professor(professor)
     * 				.build();
     *
     * 		courseRepository.save(course);
     */

    @BeforeEach
    public void init(){
        this.admin = Admin.builder()
                .name("test_user")
                .
    }
    @Test
    public void makeCourse(){

    }
}
