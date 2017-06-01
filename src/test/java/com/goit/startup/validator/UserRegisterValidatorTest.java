package com.goit.startup.validator;

import com.goit.startup.entity.User;
import com.goit.startup.enums.UserRole;
import com.goit.startup.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Вадим on 31.05.2017.
 */
public class UserRegisterValidatorTest {

    /**
     * An instance of implementation {@link UserService} interface.
     */
    private UserService userService;

    /**
     * An instance of class {@link UserRegisterValidator}
     */
    private UserRegisterValidator userRegisterValidator;

    /**
     * An instance of  {@link Errors} class.
     */
    private Errors errors;

    /**
     * An instance of  {@link User} class.
     */
    private User user;

    public UserRegisterValidatorTest() {
        this.user = new User();
        this.user.setConfirmPassword("Password");
        this.user.setId(1L);
        this.errors = mock(Errors.class);
        this.userService = mock(UserService.class);
        this.userRegisterValidator = new UserRegisterValidator(userService);
    }

    @Before
    public void initUser() throws Exception {
        this.user.setUsername("TestUser");
        this.user.setPassword("Password");
        this.user.setConfirmPassword("Password");
        this.user.setId(1L);

    }

    @Test
    public void supports() throws Exception {
        assertTrue(userRegisterValidator.supports(User.class));
    }

    @Test
    public void validateEmptyOrWhitespaceName() throws Exception {
        user.setUsername(" ");
        when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);
        when(userService.getByUsername(user.getUsername())).thenReturn(user);

        userRegisterValidator.validate(user, errors);
        verify(errors).rejectValue("username", "Required", null, null);
        user.setUsername("Test  ");
        userRegisterValidator.validate(user, errors);
        verify(errors).rejectValue("username", "Blanks.user.username");
        verify(errors, times(2)).rejectValue("password", "Required", null, null);
    }

    @Test
    public void validateUserNameLength() throws Exception {
        setFieldsByReflection();
        userRegisterValidator.validate(user, errors);
        verify(errors, never()).rejectValue("username", "Size.user.username");
        user.setUsername("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        userRegisterValidator.validate(user, errors);
        user.setUsername("e");
        userRegisterValidator.validate(user, errors);
        verify(errors, times(2)).rejectValue("username", "Size.user.username");
    }

    @Test
    public void validateDifferentIdAndException() throws Exception {
        User user2 = new User("TestUser", "Password", UserRole.USER);
        user2.setConfirmPassword("Password");
        user2.setId(2L);

        when(userService.getByUsername("TestUser")).thenReturn(user).thenThrow(new UsernameNotFoundException(""));
        when(userService.loadUserByUsername("TestUser")).thenReturn(user);
        userRegisterValidator.validate(user2, errors);
        userRegisterValidator.validate(user2, errors);
        verify(errors, times(2)).rejectValue("username", "Required", null, null);
        verify(errors, times(1)).rejectValue("username", "Duplicate.user.username");
    }

    @Test
    public void validateEmptyOrWhitespacePassword() throws Exception {
        user.setPassword("  ");
        userRegisterValidator.validate(user, errors);
        verify(errors).rejectValue("password", "Required", null, null);
        verify(errors).rejectValue("password", "Blanks.user.password");
    }

    @Test
    public void validatePasswordLength() throws Exception {
        setFieldsByReflection();
        userRegisterValidator.validate(user, errors);
        verify(errors, never()).rejectValue("password", "Size.user.password");
        user.setPassword("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        userRegisterValidator.validate(user, errors);
        user.setPassword("e");
        userRegisterValidator.validate(user, errors);
        verify(errors, times(2)).rejectValue("password", "Size.user.password");
    }

    @Test
    public void validateEqualsPasswordAndConfirm() throws Exception {
        user.setConfirmPassword("Password1");
        userRegisterValidator.validate(user, errors);
        verify(errors).rejectValue("password", "Required", null, null);
        verify(errors).rejectValue("confirmPassword", "Different.user.password");
    }

    private void setFieldsByReflection() throws Exception {
        Class refValidator = userRegisterValidator.getClass();
        Field minUsernameLength = refValidator.getDeclaredField("minUsernameLength");
        Field maxUsernameLength = refValidator.getDeclaredField("maxUsernameLength");
        Field minPasswordLength = refValidator.getDeclaredField("minPasswordLength");
        Field maxPasswordLength = refValidator.getDeclaredField("maxPasswordLength");
        minUsernameLength.setAccessible(true);
        maxUsernameLength.setAccessible(true);
        minPasswordLength.setAccessible(true);
        maxPasswordLength.setAccessible(true);
        minUsernameLength.setInt(userRegisterValidator, 4);
        maxUsernameLength.setInt(userRegisterValidator, 16);
        minPasswordLength.setInt(userRegisterValidator, 4);
        maxPasswordLength.setInt(userRegisterValidator, 32);
    }

}