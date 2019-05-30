package com.shouwn.oj.model.response.admin;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 프로젝트 생성, 수정 후 바로 프로젝트 상세 페이지의 정보를 보여주기 위함.
 */
@Getter
public class AdminCourseResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private LocalDateTime activeTime;

    // TODO 프로젝트 상세 페이지에서 문제 타입별로 문제 총 수, 현재 오픈한 문제 수 필드 추가해야함.

    @Builder
    AdminCourseResponse(Long id, String name, String description,
                        Boolean enabled, LocalDateTime activeTime){
        this.id = id;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.activeTime = activeTime;
    }
}
