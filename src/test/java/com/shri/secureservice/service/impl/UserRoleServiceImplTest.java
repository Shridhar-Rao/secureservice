package com.shri.secureservice.service.impl;

import com.shri.secureservice.dto.RoleDto;
import com.shri.secureservice.dto.UserDto;
import com.shri.secureservice.entity.Role;
import com.shri.secureservice.entity.Token;
import com.shri.secureservice.entity.User;
import com.shri.secureservice.exception.BusinessException;
import com.shri.secureservice.repository.RoleRepository;
import com.shri.secureservice.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    private UserDto userDto;

    private RoleDto roleDto;

    private User user;

    private Role role;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("password1");

        roleDto = new RoleDto();
        roleDto.setRolename("role1");

        role = new Role();
        role.setId(1l);
        role.setRolename("role1");

        user = new User();
        user.setId(1);
        user.setPassword("password1");
        user.setUsername("user1");
        user.setRoles(new HashSet<>());
    }

    @Test
    void givenUserDtoWhichDoesNotExists_WhenCreateUser_ThenCreateUser() {
        //given
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        //when
        UserDto returnedUserDto = userRoleService.createUser(userDto);

        //then
        Assertions.assertEquals(userDto.getUsername(),returnedUserDto.getUsername());
        Assertions.assertEquals(userDto.getPassword(),returnedUserDto.getPassword());
    }

    @Test
    void givenUserDtoWhichExists_WhenCreateUser_ThenThrowException() {
        //given
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);

        //when
        Assertions.assertThrows(BusinessException.class, () ->{
                    userRoleService.createUser(userDto);
                }
        );

        //then
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void givenRoleDtoWhichDoesNotExists_WhenCreateRole_ThenCreateRole() {
        //given
        Mockito.when(roleRepository.findByRolename(Mockito.anyString())).thenReturn(null);
        Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        //when
        RoleDto returnedRoleDto = userRoleService.createRole(roleDto);

        //then
        Assertions.assertEquals(roleDto.getRolename(), returnedRoleDto.getRolename());
    }

    @Test
    void givenRoleDtoWhichExists_WhenCreateRole_ThenThrowException() {
        //given
        Mockito.when(roleRepository.findByRolename(Mockito.anyString())).thenReturn(role);

        //when
        Assertions.assertThrows(BusinessException.class, () ->{
                    userRoleService.createRole(roleDto);
                }
        );

        //then
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
    }

    @Test
    void givenValidUsernameAndValidRole_WhenAddRoleToUser_ThenAddRoleToUser() {
        //given
        Role newRole = new Role();
        newRole.setId(2l);
        newRole.setRolename("role2");

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(roleRepository.findByRolename(Mockito.anyString())).thenReturn(newRole);

        //when
        userRoleService.addRoleToUser("user1","role2");

        //then
        Assertions.assertTrue(user.getRoles().contains(newRole));
    }

    @Test
    void givenInValidRole_WhenAddRoleToUser_ThenThrowException() {
        //given
        Mockito.when(roleRepository.findByRolename(Mockito.anyString())).thenReturn(null);

        //when and then
        Assertions.assertThrows(BusinessException.class, () ->{
                    userRoleService.addRoleToUser("user1","role2");
                }
        );

    }

    @Test
    void givenInValidUsernameButValidRole_WhenAddRoleToUser_ThenThrowException() {
        //given
        Mockito.when(roleRepository.findByRolename(Mockito.anyString())).thenReturn(role);
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        //when and then
        Assertions.assertThrows(BusinessException.class, () ->{
                    userRoleService.addRoleToUser("user1","role2");
                }
        );

    }

    @Test
    void givenValidUser_WhenDeleteUser_ThenDeleteUser() {
        //given
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);

        //when
        userRoleService.deleteUser("user1");

        //then
        Mockito.verify(userRepository, Mockito.times(1)).delete(Mockito.any(User.class));
    }

    @Test
    void givenInValidUser_WhenDeleteUser_ThenThrowException() {
        //given
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        //when
        Assertions.assertThrows(BusinessException.class, () ->{
                    userRoleService.deleteUser("user1");
                }
        );

        //then
        Mockito.verify(userRepository, Mockito.never()).delete(Mockito.any(User.class));
    }

    @Test
    void givenValidRole_WhenDeleteRole_ThenDeleteRole() {
        //given
        Mockito.when(roleRepository.findByRolename(Mockito.anyString())).thenReturn(role);

        //when
        userRoleService.deleteRole("role1");

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).delete(Mockito.any(Role.class));
    }

    @Test
    void givenInValidRole_WhenDeleteRole_ThenThrowException() {
        //given
        Mockito.when(roleRepository.findByRolename(Mockito.anyString())).thenReturn(null);

        //when
        Assertions.assertThrows(BusinessException.class, () ->{
                    userRoleService.deleteRole("role1");
                }
        );

        //then
        Mockito.verify(roleRepository, Mockito.never()).delete(Mockito.any(Role.class));
    }

}