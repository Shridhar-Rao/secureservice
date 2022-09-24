package com.shri.secureservice.service.impl;

import javax.transaction.Transactional;

import com.shri.secureservice.dto.RoleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shri.secureservice.dto.UserDto;
import com.shri.secureservice.entity.Role;
import com.shri.secureservice.entity.User;
import com.shri.secureservice.exception.BusinessException;
import com.shri.secureservice.repository.RoleRepository;
import com.shri.secureservice.repository.UserRepository;
import com.shri.secureservice.service.UserRoleService;

@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserDto createUser(UserDto userDto) {
		User existingUser = userRepository.findByUsername(userDto.getUsername());
		if(existingUser != null) {
			throw new BusinessException("Cannot create user as user already exists");
		}
		
		User newUser = userRepository.save(new User(userDto.getUsername(),
				passwordEncoder.encode(userDto.getPassword())));//password is encoded
		
		return userDto;
		
	}

	@Override
	public void deleteUser(String username) {
		User existingUser = userRepository.findByUsername(username);
		if(existingUser == null) {
			throw new BusinessException("Cannot delete user as user does not exist");
		}
		
		userRepository.delete(existingUser);
	}

	@Override
	public RoleDto createRole(RoleDto roleDto) {
		Role existingRole = roleRepository.findByRolename(roleDto.getRolename());
		if(existingRole != null) {
			throw new BusinessException("Cannot create role as role already exists");
		}

		Role newRole = roleRepository.save(new Role(roleDto.getRolename()));

		return roleDto;
		
	}

	@Override
	public void deleteRole(String rolename) {
		Role existingRole = roleRepository.findByRolename(rolename);
		if(existingRole == null) {
			throw new BusinessException("Cannot delete role as role does not exist");
		}

		roleRepository.delete(existingRole);
	}

	@Override
	public void addRoleToUser(String username, String rolename) {
		Role newRole = roleRepository.findByRolename(rolename);

		if(newRole == null) {
			throw new BusinessException("Cannot add role to user as role does not exist");
		}

		User existingUser = userRepository.findByUsername(username);

		if(existingUser == null) {
			throw new BusinessException("Cannot add role to user as user does not exist");
		}

		//since roles is a set, below will not do anything in case when role already exists for user
		existingUser.getRoles().add(newRole);
	}
}
