package com.shri.secureservice.dto.request;

import lombok.Data;

@Data
public class CheckRoleRequest {
	private String authToken;
	private String rolename;
}
