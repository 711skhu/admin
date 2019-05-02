package com.shouwn.oj.model.response.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminInformation {

	private String name;

	private String username;

	private String email;
}
