package com.shri.secureservice.service.impl;

import com.shri.secureservice.dto.RoleDto;
import com.shri.secureservice.dto.UserDto;
import com.shri.secureservice.entity.Role;
import com.shri.secureservice.entity.Token;
import com.shri.secureservice.entity.User;
import com.shri.secureservice.exception.BusinessException;
import com.shri.secureservice.repository.RoleRepository;
import com.shri.secureservice.repository.TokenRepository;
import com.shri.secureservice.repository.UserRepository;
import com.shri.secureservice.util.RandomUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class CustomAuthServiceImplTest {

    public static final String USERNAME_VALUE = "user1";
    public static final String PASSWORD_VALUE = "password1";
    public static final String EXPIRED_RANDOM_TOKEN_VALUE = "expiredrandomtoken1";
    public static final String VALID_RANDOM_TOKEN_VALUE = "validrandomtoken1";
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RandomUtil randomUtil;

    @InjectMocks
    private CustomAuthServiceImpl customAuthService;

    private UserDto userDto;

    private RoleDto roleDto;

    private User user;

    private Role role;

    private Token expiredToken;

    private Token validToken;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUsername(USERNAME_VALUE);
        userDto.setPassword("password1");

        roleDto = new RoleDto();
        roleDto.setRolename("role1");

        role = new Role();
        role.setId(1l);
        role.setRolename("role1");

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        user = new User();
        user.setId(1);
        user.setPassword(PASSWORD_VALUE);
        user.setUsername(USERNAME_VALUE);
        user.setRoles(roles);

        expiredToken = new Token();
        expiredToken.setId(1);
        expiredToken.setAuthtoken(EXPIRED_RANDOM_TOKEN_VALUE);
        expiredToken.setStartdatetime(LocalDateTime.now().minusHours(3));

        validToken = new Token();
        validToken.setId(1);
        validToken.setAuthtoken(VALID_RANDOM_TOKEN_VALUE);
        validToken.setStartdatetime(LocalDateTime.now().minusHours(1));
        validToken.setUser(user);
    }

    @Test
    void givenUserCredentialsAreValidAndTokenDoesNotExist_WhenAuthenticate_ThenReturnToken() {
        //given
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(passwordEncoder.matches(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        Mockito.when(tokenRepository.findByUser(Mockito.anyInt())).thenReturn(null);
        String expectedAuthToken = "randomtoken1";
        Mockito.when(randomUtil.generateRandomString(Mockito.anyInt())).thenReturn(expectedAuthToken);

        //when
        String authToken = customAuthService.authenticate(USERNAME_VALUE,"password1");

        //then
        Assertions.assertEquals(expectedAuthToken, authToken);
        Mockito.verify(tokenRepository, Mockito.times(1)).save(Mockito.any(Token.class));

    }

    @Test
    void givenUserCredentialsAreInValid_WhenAuthenticate_ThenThrowException() {
        //given
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        //when
        Assertions.assertThrows(BusinessException.class, () ->{
                    customAuthService.authenticate(USERNAME_VALUE,"password1");
                }
        );

        //then
        Mockito.verify(tokenRepository, Mockito.never()).save(Mockito.any(Token.class));
    }

    @Test
    void givenUserCredentialsAreValidAndTokenExistsButInvalid_WhenAuthenticate_ThenUpdateToken() {
        //given
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(passwordEncoder.matches(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        Mockito.when(tokenRepository.findByUser(Mockito.anyInt())).thenReturn(expiredToken);
        String expectedAuthToken = "updatedrandomtoken1";
        Mockito.when(randomUtil.generateRandomString(Mockito.anyInt())).thenReturn(expectedAuthToken);

        //when
        String authToken = customAuthService.authenticate(USERNAME_VALUE,"password1");

        //then
        Assertions.assertEquals(expectedAuthToken, authToken);
        Mockito.verify(tokenRepository, Mockito.times(1)).save(Mockito.any(Token.class));
    }

    @Test
    void givenUserCredentialsAreValidAndTokenExistsAndIsValid_WhenAuthenticate_ThenReturnExistingTokenString() {
        //given
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(passwordEncoder.matches(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        Mockito.when(tokenRepository.findByUser(Mockito.anyInt())).thenReturn(validToken);
        String expectedAuthToken = VALID_RANDOM_TOKEN_VALUE;

        //when
        String authToken = customAuthService.authenticate(USERNAME_VALUE,"password1");

        //then
        Assertions.assertEquals(expectedAuthToken, authToken);
        Mockito.verify(tokenRepository, Mockito.never()).save(Mockito.any(Token.class));
        Mockito.verify(randomUtil, Mockito.never()).generateRandomString(Mockito.anyInt());
    }

    @Test
    void givenValidToken_WhenInvalidate_ThenInvalidateTokenDeleteToken() {
        //given
        Mockito.when(tokenRepository.findByAuthtoken(Mockito.anyString())).thenReturn(validToken);

        //when
        customAuthService.invalidate(VALID_RANDOM_TOKEN_VALUE);

        //then
        Mockito.verify(tokenRepository, Mockito.times(1)).delete(Mockito.any(Token.class));
    }

    @Test
    void givenInValidExpiredToken_WhenInvalidate_ThenThrowException() {
        //given
        Mockito.when(tokenRepository.findByAuthtoken(Mockito.anyString())).thenReturn(expiredToken);

        //when
        Assertions.assertThrows(BusinessException.class, () ->{
                    customAuthService.invalidate("expiredrandomtoken1");
                }
        );

        //then
        Mockito.verify(tokenRepository, Mockito.never()).delete(Mockito.any(Token.class));
    }

    @Test
    void givenInValidNotExistingToken_WhenInvalidate_ThenThrowException() {
        //given
        Mockito.when(tokenRepository.findByAuthtoken(Mockito.anyString())).thenReturn(null);

        //when
        Assertions.assertThrows(BusinessException.class, () ->{
                    customAuthService.invalidate("randomtoken1");
                }
        );

        //then
        Mockito.verify(tokenRepository, Mockito.never()).delete(Mockito.any(Token.class));
    }

    @Test
    void givenValidToken_WhenAllRoles_ThenReturnAllRoles() {
        //given
        Mockito.when(tokenRepository.findByAuthtoken(Mockito.anyString())).thenReturn(validToken);
        Set<String> expectedRolenames = new HashSet<>();
        expectedRolenames.add("role1");

        //when
        Set<String> roleNames = customAuthService.allRoles(VALID_RANDOM_TOKEN_VALUE);

        //then
        assertEqualsForOrderAgnosticEquality_ShouldBeTrue(expectedRolenames, roleNames);
    }

    @Test
    void givenExpiredInValidToken_WhenAllRoles_ThenThrowException() {
        //given
        Mockito.when(tokenRepository.findByAuthtoken(Mockito.anyString())).thenReturn(expiredToken);

        //when and then
        Assertions.assertThrows(BusinessException.class, () ->{
                    customAuthService.allRoles("expiredrandomtoken1");
                }
        );
    }

    @Test
    void givenNotExistingInValidToken_WhenAllRoles_ThenThrowException() {
        //given
        Mockito.when(tokenRepository.findByAuthtoken(Mockito.anyString())).thenReturn(null);

        //when and then
        Assertions.assertThrows(BusinessException.class, () ->{
                    customAuthService.allRoles("notexistingrandomtoken1");
                }
        );
    }

    @Test
    void givenValidTokenAndRoleBelongsToUser_WhenCheckRole_ThenReturnTrue() {
        //given
        Mockito.when(tokenRepository.findByAuthtoken(Mockito.anyString())).thenReturn(validToken);
        Mockito.when(roleRepository.findByRolename(Mockito.anyString())).thenReturn(role); //role belongs to user

        //when
        boolean checkIfRoleBelongsToUserOfToken = customAuthService.checkRole(VALID_RANDOM_TOKEN_VALUE, "role1");

        //then
        Assertions.assertTrue(checkIfRoleBelongsToUserOfToken);

    }

    @Test
    void givenValidTokenAndRoleDoesNotBelongsToUser_WhenCheckRole_ThenReturnFalse() {
        //given
        //role that does not belong to user
        Role doesNotBelongToUserRole = new Role();
        doesNotBelongToUserRole.setId(2l);
        doesNotBelongToUserRole.setRolename("doesnotbelongrole2");

        Mockito.when(tokenRepository.findByAuthtoken(Mockito.anyString())).thenReturn(validToken);
        Mockito.when(roleRepository.findByRolename(Mockito.anyString())).thenReturn(doesNotBelongToUserRole); //role does not belong to user

        //when
        boolean checkIfRoleBelongsToUserOfToken = customAuthService.checkRole(VALID_RANDOM_TOKEN_VALUE, "doesnotbelongrole2");

        //then
        Assertions.assertFalse(checkIfRoleBelongsToUserOfToken);

    }

    @Test
    void givenExpiredInValidToken_WhenCheckRole_ThenThrowException() {
        //given
        Mockito.when(tokenRepository.findByAuthtoken(Mockito.anyString())).thenReturn(expiredToken);

        //when and then
        Assertions.assertThrows(BusinessException.class, () ->{
                    boolean checkIfRoleBelongsToUserOfToken = customAuthService.checkRole("expiredrandomtoken1", "role1");
                    Assertions.assertFalse(checkIfRoleBelongsToUserOfToken);
                }
        );
    }

    @Test
    void givenNotExistingInValidToken_WhenCheckRole_ThenThrowException() {
        //given
        Mockito.when(tokenRepository.findByAuthtoken(Mockito.anyString())).thenReturn(null);

        //when and then
        Assertions.assertThrows(BusinessException.class, () ->{
                    boolean checkIfRoleBelongsToUserOfToken = customAuthService.checkRole("notexistingrandomtoken1", "role1");
                    Assertions.assertFalse(checkIfRoleBelongsToUserOfToken);
                }
        );
    }

    public void assertEqualsForOrderAgnosticEquality_ShouldBeTrue(Set first, Set second) {
        Assertions.assertTrue(first.size() == second.size() && first.containsAll(second) && second.containsAll(first));
    }
}