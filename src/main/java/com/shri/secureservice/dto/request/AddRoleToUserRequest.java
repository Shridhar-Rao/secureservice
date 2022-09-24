package com.shri.secureservice.dto.request;

import lombok.Data;

@Data
public class AddRoleToUserRequest {
	private String username;
	private String rolename;
}
