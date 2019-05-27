package com.shouwn.oj.service.problem;

import javax.persistence.EntityNotFoundException;

import com.shouwn.oj.model.entity.member.Admin;
import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.model.request.admin.AdminCourseSaveRequest;
import com.shouwn.oj.model.response.admin.AdminCourseResponse;
import com.shouwn.oj.service.member.AdminService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class CourseServiceForAdmin {

    private final AdminService adminService;
    private final CourseService courseService;

    public CourseServiceForAdmin(AdminService adminService, CourseService courseService) {
        this.adminService = adminService;
        this.courseService = courseService;
    }

    private AdminCourseResponse responseDto(Course course) {

        AdminCourseResponse response = AdminCourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .enabled(course.getEnabled())
                .activeTime(course.getActiveDate())
                .problems(course.getProblems())
                .build();

        return response;
    }

    /**
     * admin id 에 해당하는 관리자의 강좌 리스트를 가져오는 메소드
     *
     * @param adminId
     * @return
     */
    public List<AdminCourseResponse> getCourseList(Long adminId) throws EntityNotFoundException {

        List<Course> courseList = courseService.findCourseByAdminId(adminId);
        List<AdminCourseResponse> adminCourseLists = new ArrayList<>();

        for (Course c : courseList) {
            adminCourseLists.add(responseDto(c));
        }
        return adminCourseLists;
    }

    /**
     * admin Id 에 해당하는 관리자로 강좌를 생성하는 메소드
     *
     * @param adminId 강좌를 생성하는 admin 의 id
     * @param dto     강좌 생성 req dto ( 강좌이름, 강좌내용)
     * @return 생성된 Course 정보 AdminCourseResponse
     * @throws EntityNotFoundException adminId 로 Admin 을 찾을 수 없을 때 발생하는 예외
     */
    public AdminCourseResponse makeCourse(Long adminId, AdminCourseSaveRequest dto) throws EntityNotFoundException {

        Admin professor = adminService.findById(adminId);
        Course course = professor.makeCourse(dto.getName(), dto.getDescription());

        return responseDto(course);
    }

    /**
     * admin id 에 해당하는 관리자로 강좌를 수정하는 메소드
     *
     * @param adminId
     * @param courseId
     * @param dto
     * @return 수정된 Course 정보 AdminCourseResponse
     * @throws EntityNotFoundException
     */
    public AdminCourseResponse updateCourse(Long adminId, Long courseId, AdminCourseSaveRequest dto) throws EntityNotFoundException {

        Admin professor = adminService.findById(adminId);
        Course findCourse = courseService.findCourseById(courseId); // 해당 강좌 존재 유무
        Course course = professor.updateCourse(findCourse.getId(), dto.getName(), dto.getDescription(), dto.getEnabled());

        return responseDto(course);
    }

    /**
     * 추후 개발
     * admin id 에 해당하는 관리자로 강좌를 삭제하는 메소드
     *
     * @param adminId
     * @param courseId
     * @throws EntityNotFoundException
     */
    public void deleteCourse(Long adminId, Long courseId) throws EntityNotFoundException {

        Admin professor = adminService.findById(adminId);

        professor.deleteCourse(courseId);

        // TODO 학생 입장에서 해당 course를 삭제
        // studentService.deleteCourse(courseId);
    }
}
