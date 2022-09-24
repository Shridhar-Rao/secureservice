package com.shri.secureservice.service;

import com.shri.secureservice.dto.RoleDto;
import com.shri.secureservice.dto.UserDto;
import com.shri.secureservice.entity.Role;
import com.shri.secureservice.entity.User;

public interface UserRoleService {
	//Should fail if the user already exists
	public UserDto createUser(UserDto userDto);
	
	//Should fail if the user doesn't exist
	public void deleteUser(String username);
	
	//Should fail if the role already exists
	public RoleDto createRole(RoleDto roleDto);
	
	//Should fail if the role doesn't exist
	public void deleteRole(String rolename);
	
	//If the role is already associated with the user, nothing should happen
	public void addRoleToUser(String username, String rolename);
}
