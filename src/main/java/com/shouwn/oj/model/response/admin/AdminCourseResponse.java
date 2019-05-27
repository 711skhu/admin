package com.shouwn.oj.model.response.admin;

import com.shouwn.oj.model.entity.problem.Problem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 프로젝트 생성, 수정 후 바로 프로젝트 상세 페이지의 정보를 보여주기 위함.
 *
 * 추후 상의 후 수정해야 할 것 : 상세 페이지에서 무엇을 보여줄지..
 */
@Getter
public class AdminCourseResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private LocalDateTime activeTime;
    private List<Problem> problems; // TODO 프로젝트 상세 페이지에서 무엇을 어디까지 보여줄지 확실하지 않습니다.

    @Builder
    AdminCourseResponse(Long id, String name, String description,
                        Boolean enabled, LocalDateTime activeTime, List<Problem> problems){
        this.id = id;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.activeTime = activeTime;
        this.problems = problems;
    }
}
