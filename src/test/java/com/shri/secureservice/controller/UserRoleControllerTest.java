package com.shri.secureservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shri.secureservice.dto.OperationStatusModel;
import com.shri.secureservice.dto.RequestOperationStatus;
import com.shri.secureservice.dto.RoleDto;
import com.shri.secureservice.dto.UserDto;
import com.shri.secureservice.dto.request.AddRoleToUserRequest;
import com.shri.secureservice.service.UserRoleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserRoleController.class})
@AutoConfigureMockMvc(addFilters = false)
class UserRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRoleService userRoleService;

    @Test
    public void givenUserObject_whenUserDoesNotExist_thenCreateUser() throws Exception{
        //given
        UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("password1");

        Mockito.when(userRoleService.createUser(Mockito.any(UserDto.class))).thenReturn(userDto);

        //when
        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        //then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.username",
                is(userDto.getUsername())));

    }

    @Test
    public void givenRoleObject_whenRoleDoesNotExist_thenCreateRole() throws Exception{
        //given
        RoleDto roleDto = new RoleDto();
        roleDto.setRolename("role1");

        Mockito.when(userRoleService.createRole(Mockito.any(RoleDto.class))).thenReturn(roleDto);

        //when
        ResultActions response = mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)));

        //then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.rolename",
                        is(roleDto.getRolename())));

    }

    @Test
    public void givenUsername_whenUserExists_thenDeleteUser() throws Exception{
        //given
        String username = "user1";
        OperationStatusModel expectedReturnValue = new OperationStatusModel();
        expectedReturnValue.setOperationName(RequestOperationName.DELETE_USER.name());
        expectedReturnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        Mockito.doNothing().when(userRoleService).deleteUser(Mockito.anyString());

        //when
        ResultActions response = mockMvc.perform(delete("/users/{username}", username));

        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.operationName",
                        is(expectedReturnValue.getOperationName())))
                .andExpect(jsonPath("$.operationResult",
                        is(expectedReturnValue.getOperationResult())));

    }

    @Test
    public void givenRolename_whenRoleExists_thenDeleteRole() throws Exception{
        //given
        String rolename = "role1";
        OperationStatusModel expectedReturnValue = new OperationStatusModel();
        expectedReturnValue.setOperationName(RequestOperationName.DELETE_ROLE.name());
        expectedReturnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        Mockito.doNothing().when(userRoleService).deleteRole(Mockito.anyString());

        //when
        ResultActions response = mockMvc.perform(delete("/roles/{rolename}", rolename));

        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.operationName",
                        is(expectedReturnValue.getOperationName())))
                .andExpect(jsonPath("$.operationResult",
                        is(expectedReturnValue.getOperationResult())));

    }

    @Test
    void givenValidRoleAndUser_WhenAddRoleToUser_ThenAddRoleToUser() throws Exception{
        //given
        AddRoleToUserRequest addRoleToUserRequest = new AddRoleToUserRequest();
        addRoleToUserRequest.setRolename("role1");
        addRoleToUserRequest.setUsername("user1");

        OperationStatusModel expectedReturnValue = new OperationStatusModel();
        expectedReturnValue.setOperationName(RequestOperationName.ADD_ROLE_TO_USER.name());
        expectedReturnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        Mockito.doNothing().when(userRoleService).addRoleToUser(Mockito.anyString(), Mockito.anyString());

        //when
        ResultActions response = mockMvc.perform(post("/addroletouser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRoleToUserRequest)));

        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.operationName",
                        is(expectedReturnValue.getOperationName())))
                .andExpect(jsonPath("$.operationResult",
                        is(expectedReturnValue.getOperationResult())));

    }
}