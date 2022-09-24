package com.shri.secureservice.controller;

import com.shri.secureservice.dto.*;
import com.shri.secureservice.dto.request.AllRolesRequest;
import com.shri.secureservice.dto.request.AuthenticateRequest;
import com.shri.secureservice.dto.request.CheckRoleRequest;
import com.shri.secureservice.dto.request.InvalidateRequest;
import com.shri.secureservice.dto.response.AllRolesResponse;
import com.shri.secureservice.dto.response.AuthenticateResponse;
import com.shri.secureservice.dto.response.CheckRoleResponse;
import com.shri.secureservice.service.CustomAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class CustomAuthController {
    @Autowired
    private CustomAuthService customAuthService;

    @PutMapping("/authenticate")
    public AuthenticateResponse authenticate(@RequestBody AuthenticateRequest authenticateRequest){
        String authToken = customAuthService.authenticate(
                authenticateRequest.getUsername(), authenticateRequest.getPassword());

        AuthenticateResponse authenticateResponse = new AuthenticateResponse();
        authenticateResponse.setAuthToken(authToken);

        return authenticateResponse;
    }

    @PutMapping("/invalidate")
    public OperationStatusModel authenticate(@RequestBody InvalidateRequest invalidateRequest){
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.INVALIDATE.name());

        customAuthService.invalidate(invalidateRequest.getAuthToken());

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    //returns true if the user, identified by the token, belongs to the role, false otherwise; error if token is invalid expired etc
    @PutMapping("/checkrole")
    public CheckRoleResponse authenticate(@RequestBody CheckRoleRequest checkRoleRequest){
        boolean doesRoleExistInUserRoles = customAuthService.checkRole(
                checkRoleRequest.getAuthToken(), checkRoleRequest.getRolename());

        CheckRoleResponse checkRoleResponse = new CheckRoleResponse();
        checkRoleResponse.setCheckRole(doesRoleExistInUserRoles);

        return checkRoleResponse;
    }

    //returns all roles for the user, error if token is invalid
    @PutMapping("/allroles")
    public AllRolesResponse authenticate(@RequestBody AllRolesRequest allRolesRequest){
        Set<String> allRoleNamesBelongingToToken = customAuthService.allRoles(allRolesRequest.getAuthToken());

        AllRolesResponse allRolesResponse = new AllRolesResponse();

        allRolesResponse.setRolenames(allRoleNamesBelongingToToken);

        return allRolesResponse;
    }
}

