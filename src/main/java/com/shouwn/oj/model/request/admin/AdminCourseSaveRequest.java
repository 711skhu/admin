package com.shouwn.oj.model.request.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 강좌 생성, 수정시 정보
 */

@NoArgsConstructor
@Setter
@Getter
public class AdminCourseSaveRequest {

    private String courseName;
    private String courseDescription;

    @Builder
    public AdminCourseSaveRequest(String courseName, String courseDescription){
        this.courseName = courseName;
        this.courseDescription = courseDescription;
    }
}
