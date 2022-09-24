package com.shri.secureservice.controller;

import com.shri.secureservice.dto.*;
import com.shri.secureservice.dto.request.AddRoleToUserRequest;
import com.shri.secureservice.dto.request.CreateUserRequest;
import com.shri.secureservice.dto.response.RoleResponse;
import com.shri.secureservice.dto.response.UserResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.shri.secureservice.service.UserRoleService;

@RestController
public class UserRoleController {
	
	@Autowired
	private UserRoleService userRoleService;
	
	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse createUser(@RequestBody CreateUserRequest userRequest) {
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userRequest, userDto);
		
		UserDto createdUserDto = userRoleService.createUser(userDto);
		
		UserResponse userResponse = new UserResponse();
		BeanUtils.copyProperties(createdUserDto, userResponse);
		
		return userResponse;
	}
	
	@DeleteMapping("/users/{username}")
	public OperationStatusModel deleteUser(@PathVariable String username) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE_USER.name());

		userRoleService.deleteUser(username);

		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	@PostMapping("/roles")
	@ResponseStatus(HttpStatus.CREATED)
	public RoleResponse createRole(@RequestBody RoleDto roleDto) {
		RoleDto createdRoleDto = userRoleService.createRole(roleDto);

		RoleResponse roleResponse = new RoleResponse();
		BeanUtils.copyProperties(createdRoleDto, roleResponse);

		return roleResponse;
	}

	@DeleteMapping("/roles/{rolename}")
	public OperationStatusModel deleteRole(@PathVariable String rolename) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE_ROLE.name());

		userRoleService.deleteRole(rolename);

		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	@PostMapping("/addroletouser")
	public OperationStatusModel addRoleToUser(@RequestBody AddRoleToUserRequest addRoleToUserRequest){
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.ADD_ROLE_TO_USER.name());

		userRoleService.addRoleToUser(addRoleToUserRequest.getUsername(),addRoleToUserRequest.getRolename());

		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

}
