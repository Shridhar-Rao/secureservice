package com.shri.secureservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shri.secureservice.dto.OperationStatusModel;
import com.shri.secureservice.dto.RequestOperationStatus;
import com.shri.secureservice.dto.request.AllRolesRequest;
import com.shri.secureservice.dto.request.AuthenticateRequest;
import com.shri.secureservice.dto.request.CheckRoleRequest;
import com.shri.secureservice.dto.request.InvalidateRequest;
import com.shri.secureservice.entity.Role;
import com.shri.secureservice.service.CustomAuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CustomAuthController.class})
@AutoConfigureMockMvc(addFilters = false)
class CustomAuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomAuthService customAuthService;

    @Test
    void givenValidCredentials_WhenAuthenticate_ThenReturnAuthTokenInResponse() throws Exception{
        //given
        AuthenticateRequest authenticateRequest = new AuthenticateRequest();
        authenticateRequest.setUsername("user1");
        authenticateRequest.setPassword("password1");
        String authToken = "randomtoken1";

        Mockito.when(customAuthService.authenticate(Mockito.anyString(),Mockito.anyString())).thenReturn(authToken);

        //when
        ResultActions response = mockMvc.perform(put("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticateRequest)));

        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.authToken",
                        is(authToken)));
    }

    @Test
    void givenValidCredentials_WhenInvalidate_ThenInvalidateToken() throws Exception{
        //given
        InvalidateRequest invalidateRequest = new InvalidateRequest();
        invalidateRequest.setAuthToken("randomtoken1");

        OperationStatusModel expectedReturnValue = new OperationStatusModel();
        expectedReturnValue.setOperationName(RequestOperationName.INVALIDATE.name());
        expectedReturnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        Mockito.doNothing().when(customAuthService).invalidate(Mockito.anyString());

        //when
        ResultActions response = mockMvc.perform(put("/invalidate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidateRequest)));

        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.operationName",
                        is(expectedReturnValue.getOperationName())))
                .andExpect(jsonPath("$.operationResult",
                        is(expectedReturnValue.getOperationResult())));
    }

    @Test
    void givenValidTokenAndRoleName_WhenCheckRole_ThenReturnTrueResponse() throws Exception{
        //given
        CheckRoleRequest checkRoleRequest = new CheckRoleRequest();
        checkRoleRequest.setAuthToken("randomtoken1");
        checkRoleRequest.setRolename("role1");

        Mockito.when(customAuthService.checkRole(Mockito.anyString(),Mockito.anyString())).thenReturn(true);

        //when
        ResultActions response = mockMvc.perform(put("/checkrole")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkRoleRequest)));

        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.checkRole",
                        is(true)));
    }

    @Test
    void givenValidToken_WhenAllRoles_ThenReturnSetOfRoles() throws Exception{
        //given
        AllRolesRequest allRolesRequest = new AllRolesRequest();
        allRolesRequest.setAuthToken("randomtoken1");

        Set<String> rolenames = new HashSet<>();
        rolenames.add("role1");
        rolenames.add("role2");

        Mockito.when(customAuthService.allRoles(Mockito.anyString())).thenReturn(rolenames);

        //when
        ResultActions response = mockMvc.perform(put("/allroles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(allRolesRequest)));

        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.rolenames",
                        hasSize(2)))
                .andExpect(jsonPath("$.rolenames",
                        hasItem("role1")))
                .andExpect(jsonPath("$.rolenames",
                        hasItem("role2")));
    }

}