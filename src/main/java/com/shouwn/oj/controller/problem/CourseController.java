package com.shouwn.oj.controller.problem;

import javax.persistence.EntityNotFoundException;

import com.shouwn.oj.model.entity.problem.Course;
import com.shouwn.oj.model.request.admin.AdminCourseSaveRequest;
import com.shouwn.oj.model.response.ApiResponse;
import com.shouwn.oj.model.response.CommonResponse;
import com.shouwn.oj.model.response.admin.AdminCourseList;
import com.shouwn.oj.model.response.admin.AdminCourseSaveResponse;
import com.shouwn.oj.service.problem.CourseService;
import com.shouwn.oj.service.problem.CourseServiceForAdmin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("course")
public class CourseController {

    private final CourseService courseService;
    private final CourseServiceForAdmin courseServiceForAdmin;

    public CourseController(CourseService courseService,
                            CourseServiceForAdmin courseServiceForAdmin) {
        this.courseService = courseService;
        this.courseServiceForAdmin = courseServiceForAdmin;
    }

    // get courseList
    @GetMapping
    public ApiResponse<?> getCourseList(@RequestParam Long requesterId){
        List<AdminCourseList> courseLists = courseServiceForAdmin.getCourseList(requesterId);

        return CommonResponse.builder()
                .status(HttpStatus.OK)
                .message("해당 교수의 강좌 목록 리스트 성공")
                .data(courseLists)
                .build();
    }

    // make course
    @PostMapping
    public ApiResponse<?> makeCourse(@RequestParam Long requesterId, // TODO 일반 adminId 가 아니라 Spring Security 에 의한 사용자 id가 필요.@RequestParam아닌 다른 어노테이션임.
                                     @RequestBody AdminCourseSaveRequest dto) {

        AdminCourseSaveResponse newCourse = courseServiceForAdmin.makeCourse(requesterId, dto);

        return CommonResponse.builder()
                .status(HttpStatus.CREATED)
                .message("강좌 생성 성공")
                .data(newCourse)
                .build();
    }

    //TODO 추후 알맞은 dto로 수정. 상세 페이지에 아직 어떤 내용이 들어있는지 모름.
    // get course
    @GetMapping("/{courseId}")
    public ApiResponse<?> getCourse(@PathVariable Long courseId) {

        Course course;

        try {
            course = courseService.findCourseById(courseId);
        } catch (EntityNotFoundException e) {
            return CommonResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(courseId + "에 해당하는 강좌가 없습니다.")
                    .build();
        }

        return CommonResponse.builder()
                .status(HttpStatus.OK)
                .message("강좌 찾기 성공")
                .data(course)
                .build();
    }

    // update course
    @PutMapping("/{courseId}")
    public ApiResponse<?> updateCourse(@RequestParam Long requesterId, // TODO 일반 adminId 가 아니라 Spring Security 에 의한 사용자 id가 필요.@RequestParam아닌 다른 어노테이션임.
                                       @PathVariable Long courseId,
                                       @RequestBody AdminCourseSaveRequest dto) {

        AdminCourseSaveResponse  updateCourse;

        try {
            updateCourse = courseServiceForAdmin.updateCourse(requesterId, courseId, dto);


        }catch (EntityNotFoundException e) {
            return CommonResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(courseId + "에 해당하는 강좌가 없습니다.")
                    .build();
        }
        return CommonResponse.builder()
                .status(HttpStatus.OK)
                .message("강좌 수정 성공")
                .data(updateCourse)
                .build();
    }

    // active course.
    @PutMapping("/{courseId}/{enabled}")
    public ApiResponse<?> activeCourse(@RequestParam Long requesterId, // TODO 일반 adminId 가 아니라 Spring Security 에 의한 사용자 id가 필요.@RequestParam아닌 다른 어노테이션임.
                                         @PathVariable Long courseId,
                                         @PathVariable Boolean enabled){

        try{
            courseServiceForAdmin.activeCourse(requesterId, courseId, enabled);
        }catch (EntityNotFoundException e) {
            return CommonResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(courseId + "에 해당하는 강좌가 없습니다.")
                    .build();
        }
        return CommonResponse.builder()
                .status(HttpStatus.OK)
                .message("활성화/비활성화 작업 성공")
                .build();
    }

    //delete course
    @DeleteMapping("/{courseId}")
    public ApiResponse<?> deleteCourse(@RequestParam Long requesterId, // TODO 일반 adminId 가 아니라 Spring Security 에 의한 사용자 id가 필요.@RequestParam아닌 다른 어노테이션임.
                                       @PathVariable Long courseId){
        try{
            courseServiceForAdmin.deleteCourse(requesterId, courseId);
        }catch (EntityNotFoundException e) {
            return CommonResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(courseId + "에 해당하는 강좌가 없습니다.")
                    .build();
        }
        return  CommonResponse.builder()
                .status(HttpStatus.NO_CONTENT)
                .message("강좌 삭제 성공")
                .build();
    }
}
