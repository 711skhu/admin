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
    private Boolean enabled; // TODO 강좌 비활성화 여부는 강좌 수정 페이지에서도 가능한건지요..?

    @Builder
    public AdminCourseSaveRequest(String courseName, String courseDescription, Boolean enabled){
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.enabled = enabled;
    }
}
