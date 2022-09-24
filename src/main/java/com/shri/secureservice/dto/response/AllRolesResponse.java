package com.shri.secureservice.dto.response;

import lombok.Data;

import java.util.Set;

@Data
public class AllRolesResponse {
	private Set<String> rolenames;
}
