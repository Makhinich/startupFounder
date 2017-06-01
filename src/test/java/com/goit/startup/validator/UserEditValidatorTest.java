package com.goit.startup.validator;

import com.goit.startup.entity.User;
import com.goit.startup.service.UserService;
import com.goit.startup.service.UserServiceImpl;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Вадим on 30.05.2017.
 */
public class UserEditValidatorTest {

    /**
     * An instance of implementation {@link StartupValidator} interface.
     */
    private UserEditValidator userEditValidator;

    /**
     * An instance of implementation {@link UserService} interface.
     */
    private UserService userService;

    /**
     * An instance of  {@link Errors} class.
     */
    private Errors errors;

    /**
     * An instance of  {@link User} class.
     */
    private User user;

    public UserEditValidatorTest() {
        this.user = new User();
        this.userService = mock(UserServiceImpl.class);
        this.errors = mock(Errors.class);
        this.userEditValidator = new UserEditValidator(userService);
    }

    @Test
    public void supports() throws Exception {
        assertTrue(userEditValidator.supports(User.class));
    }

    @Test
    public void validateEmptyOrWhitespace() throws Exception {

        when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);
        when(userService.getByUsername(user.getUsername())).thenReturn(user);

        userEditValidator.validate(user, errors);
        verify(errors).rejectValue("username", "Required", null, null);
        user.setUsername("Test  ");
        userEditValidator.validate(user, errors);
        verify(errors).rejectValue("username", "Blanks.user.username");

    }

    @Test
    public void validateUserNameLength() throws Exception {
        setFieldsByReflection();
        user.setUsername("Tester");
        userEditValidator.validate(user, errors);
        verify(errors, never()).rejectValue("username", "Size.user.username");
        user.setUsername("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        userEditValidator.validate(user, errors);
        user.setUsername("e");
        userEditValidator.validate(user, errors);
        verify(errors, times(2)).rejectValue("username", "Size.user.username");

    }

    @Test
    public void validateDifferentIdAndExeption() throws Exception {

        user.setUsername("Tester");
        user.setId(1L);
        User user2 = new User();
        user2.setUsername("Tester");
        user2.setId(2L);

        when(userService.getByUsername("Tester")).thenReturn(user2).thenThrow(new UsernameNotFoundException(""));
        when(userService.loadUserByUsername("Tester")).thenReturn(user2);
        userEditValidator.validate(user, errors);
        userEditValidator.validate(user, errors);
        verify(errors, times(2)).rejectValue("username", "Required", null, null);
        verify(errors, times(1)).rejectValue("username", "Duplicate.user.username");
    }


    private void setFieldsByReflection() throws Exception {
        Class refValidator = userEditValidator.getClass();
        Field minUsernameLength = refValidator.getDeclaredField("minUsernameLength");
        Field maxUsernameLength = refValidator.getDeclaredField("maxUsernameLength");
        minUsernameLength.setAccessible(true);
        maxUsernameLength.setAccessible(true);
        minUsernameLength.setInt(userEditValidator, 4);
        maxUsernameLength.setInt(userEditValidator, 16);
    }

}