package com.shouwn.oj.model.response.admin;


import lombok.Builder;
import lombok.Getter;

/**
 * 해당 교수의 수강 목록.
 */
@Getter
public class AdminCourseList {
    private Long courseId;
    private String courseName;
    private String courseDescription;
    private Boolean enabled;

    @Builder
    AdminCourseList(Long courseId, String courseName, String courseDescription,
                    Boolean enabled){
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.enabled = enabled;
    }
}
