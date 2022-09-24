package com.shri.secureservice.dto.request;

import lombok.Data;

@Data
public class AuthenticateRequest {
	private String username;
	private String password;
}
