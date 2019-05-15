package com.shouwn.oj.model.response.admin;

import com.shouwn.oj.model.entity.problem.Problem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 프로젝트 생성, 수정 후 바로 프로젝트 상세 페이지의 정보를 보여주기 위함.
 *
 * 추후 상의 후 수정해야 할 것 : 상세 페이지에서 무엇을 보여줄지..
 */
@Getter
public class AdminCourseSaveResponse {

    private Long courseId;
    private String courseName;
    private String courseDescription;
    private Boolean enabled;
    private List<Problem> problems; // TODO 프로젝트 상세 페이지에서 무엇을 어디까지 보여줄지 확실하지 않습니다.

    @Builder
    AdminCourseSaveResponse(Long courseId, String courseName, String courseDescription,
                            Boolean enabled, List<Problem> problems){
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.enabled = enabled;
        this.problems = problems;
    }
}
