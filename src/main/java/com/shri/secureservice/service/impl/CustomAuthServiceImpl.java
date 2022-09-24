package com.shri.secureservice.service.impl;

import com.shri.secureservice.entity.Role;
import com.shri.secureservice.entity.Token;
import com.shri.secureservice.entity.User;
import com.shri.secureservice.exception.BusinessException;
import com.shri.secureservice.repository.RoleRepository;
import com.shri.secureservice.repository.TokenRepository;
import com.shri.secureservice.repository.UserRepository;
import com.shri.secureservice.service.CustomAuthService;
import com.shri.secureservice.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CustomAuthServiceImpl implements CustomAuthService {
    public static final int TOKEN_EXPIRE_DURATION_HOURS = 2;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RandomUtil randomUtil;

    private static final int AUTH_TOKEN_LENGTH = 10;

    @Override
    public String authenticate(String username, String password) {
        User existingUser = userRepository.findByUsername(username);

        if(existingUser == null) {
            throw new BusinessException("Cannot authenticate as user does not exist");
        }

        //user credentials is valid. match the encoded password
        if(passwordEncoder.matches(password, existingUser.getPassword())){
            log.info("password unencoded is matching...");
            //check if token already exists,
            Token token = tokenRepository.findByUser(existingUser.getId());

            // then check if it is valid, if not valid then generate it
            if(token != null){
                if(isTokenValid(token)){
                    return token.getAuthtoken();
                }
                else{//token exists, but has expired, so update it- with now() time and new authtoken
                    String authToken = randomUtil.generateRandomString(AUTH_TOKEN_LENGTH);
                    token.setAuthtoken(authToken);
                    token.setStartdatetime(LocalDateTime.now());
                    token.setUser(existingUser);
                    tokenRepository.save(token);
                    return authToken;
                }
            }
            else{ //token does not exist in table. so create a new row represending the token
                String authToken = randomUtil.generateRandomString(AUTH_TOKEN_LENGTH);
                Token newToken = new Token(authToken, LocalDateTime.now(), existingUser);
                tokenRepository.save(newToken);
                return newToken.getAuthtoken();
            }

        }
        //user is invalid
        else{
            throw new BusinessException("User credentials is invalid");
        }
    }

    @Override
    public void invalidate(String authToken) {
        Token token = tokenRepository.findByAuthtoken(authToken);

        // then check if it is valid, if yes, then invalidate/delete it
        if(token != null && isTokenValid(token)){
            tokenRepository.delete(token);
        }
        else{ //token is invalid
            throw new BusinessException("Invalid Token");
        }
    }

    //returns true if the user, identified by the token, belongs to the role, false otherwise; error if token is invalid expired etc
    @Override
    public boolean checkRole(String authToken, String rolename) {
        Token existingToken = tokenRepository.findByAuthtoken(authToken);

        if(existingToken == null){
            throw new BusinessException("Cannot check role as token does not exist");
        }
        else if(!isTokenValid(existingToken)){
            throw new BusinessException("Cannot check role as token is invalid");
        }

        Role roleToBeChecked = roleRepository.findByRolename(rolename);

        if(roleToBeChecked == null){
            throw new BusinessException("Cannot check role as role does not exist");
        }

        User existingUser = existingToken.getUser();
        Set<Role> existingRoles = existingUser.getRoles();

        if(existingRoles.contains(roleToBeChecked)){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public Set<String> allRoles(String authToken) {
        Token existingToken = tokenRepository.findByAuthtoken(authToken);

        if(existingToken == null){
            throw new BusinessException("Cannot check role as token does not exist");
        }
        else if(!isTokenValid(existingToken)){
            throw new BusinessException("Cannot check role as token is invalid");
        }

        User existingUserBelongingToToken = existingToken.getUser();
        Set<Role> allRolesBelongingToToken = existingUserBelongingToToken.getRoles();

        Set<String> rolenames = allRolesBelongingToToken.stream().map(
                role -> role.getRolename()).collect(Collectors.toSet());

        return rolenames;
    }

    //additional method to check if token is valid
    private boolean isTokenValid(Token token){

        //if token does not exist, return false
        if(token ==  null){
            return false;
        }

        //if token exists, check for validity (if >2 hrs then return false)
        if(ChronoUnit.HOURS.between(token.getStartdatetime(), LocalDateTime.now()) > TOKEN_EXPIRE_DURATION_HOURS)
        {
            return false;
        }

        return true;
    }
}
