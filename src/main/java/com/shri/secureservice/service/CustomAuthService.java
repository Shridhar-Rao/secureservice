package com.shri.secureservice.service;

import com.shri.secureservice.entity.Role;

import java.util.Set;

public interface CustomAuthService {
    //return a special "secret" auth token or error, if not found. The token is only valid for pre-configured time (2h)
    public String authenticate(String username, String password);

    // returns nothing, the token is no longer valid after the call. Handles correctly the case of invalid token given as input
    public void invalidate(String authToken);

    //returns true if the user, identified by the token, belongs to the role, false otherwise; error if token is invalid expired etc
    public boolean checkRole(String authToken, String rolename);

    //returns all roles for the user, error if token is invalid
    public Set<String> allRoles(String authToken);

}
