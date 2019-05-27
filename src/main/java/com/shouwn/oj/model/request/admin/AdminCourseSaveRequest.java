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

    private String name;
    private String description;
    private Boolean enabled;

    @Builder
    public AdminCourseSaveRequest(String name, String description, Boolean enabled){
        this.name = name;
        this.description = description;
        this.enabled = enabled;
    }
}
