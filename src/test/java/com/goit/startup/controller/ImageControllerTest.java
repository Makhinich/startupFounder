package com.goit.startup.controller;

import com.goit.startup.entity.Image;
import com.goit.startup.entity.Startup;
import com.goit.startup.entity.User;
import com.goit.startup.enums.UserRole;
import com.goit.startup.service.ImageService;
import com.goit.startup.service.StartupService;
import com.goit.startup.service.UserService;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Вадим on 25.05.2017.
 */
public class ImageControllerTest {


    /**
     * An instance of implementation {@link ImageService} interface.
     */
    private ImageService imageService;

    /**
     * An instance of implementation {@link UserService} interface.
     */
    private UserService userService;

    /**
     * An instance of implementation {@link StartupService} interface.
     */
    private StartupService startupService;
    /**
     * An instance of implementation {@link ImageController} interface.
     */
    private ImageController imageController;

    /**
     * An instance of {@link User}.
     */
    private User user;

    /**
     * An instance of {@link Startup}.
     */
    private Startup startup;

    private byte[] data;
    private byte[] dataSize;
    private Image image;

    public ImageControllerTest() {
        this.user = new User("Test", "passtest", UserRole.USER);
        this.user.setId(1L);
        this.startup = new Startup();
        this.startup.setAuthor(user);
        this.startup.setId(1L);
        this.startup.setName("TestStartUp");
        this.image = new Image();
        data = new byte[]{0, 1, 2, 3};
        image.setData(data);
        image.setId(1L);
        dataSize = new byte[1025 * 1025];
        this.imageService = mock(ImageService.class);
        this.userService = mock(UserService.class);
        this.startupService = mock(StartupService.class);
        this.imageController = new ImageController(imageService, userService, startupService);
        when(userService.get(1L)).thenReturn(user);
        when(imageService.get(1L)).thenReturn(image);
        when(startupService.get(1L)).thenReturn(startup);
    }

    @Test
    public void getImageTest() throws Exception {

        HttpServletResponse response = mock(HttpServletResponse.class, CALLS_REAL_METHODS);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(response.getOutputStream()).thenReturn(getServletOutputStream());

        imageController.getImage(1L, response, request);
        verify(response).setContentType("image/jpeg, image/jpg, image/png, image/gif");
        verify(response).getOutputStream();
    }

    @Test(expected = IllegalStateException.class)
    public void uploadUserImageTypeTest() throws Exception {

        MultipartFile fileWrongNameType = new MockMultipartFile("mockfile", "file", "image/xml", data);
        imageController.uploadUserImage(fileWrongNameType, 1L, 2L);

    }

    @Test(expected = IllegalStateException.class)
    public void uploadUserImageWrongSizeTest() throws Exception {

        MultipartFile fileWrongSizeType = new MockMultipartFile("mockfile", "file", "image/jpg", dataSize);
        imageController.uploadUserImage(fileWrongSizeType, 1L, 2L);
    }

    @Test
    public void uploadUserImageTest() throws Exception {

        Image imageTest = new Image();
        imageTest.setData(data);

        when(imageService.add(imageTest)).thenReturn(imageTest);
        when(imageService.get(111L)).thenReturn(imageTest);
        when(imageService.add(imageTest)).thenReturn(imageTest);

        MultipartFile fileOk = new MockMultipartFile("mockfile", "file", "image/jpg", data);
        MultipartFile fileNameTypePng = new MockMultipartFile("mockfile", "file", "image/png", data);
        MultipartFile fileNameTypeGif = new MockMultipartFile("mockfile", "file", "image/gif", data);
        MultipartFile fileNameTypeJpeg = new MockMultipartFile("mockfile", "file", "image/jpeg", data);

        assertEquals(imageController.uploadUserImage(fileNameTypePng, 1L, 1L), "redirect:/user/" + user.getUsername() + "/true");
        assertEquals(imageController.uploadUserImage(fileNameTypeGif, 1L, 1L), "redirect:/user/" + user.getUsername() + "/true");
        assertEquals(imageController.uploadUserImage(fileNameTypeJpeg, 1L, 1L), "redirect:/user/" + user.getUsername() + "/true");
        verify(userService, times(3)).update(user);
        imageController.uploadUserImage(fileOk, 1L, 111L);
        verify(imageService).update(image);

    }


    @Test(expected = IllegalStateException.class)
    public void uploadStartUpImageTypeTest() throws Exception {

        MultipartFile fileWrongNameType = new MockMultipartFile("mockfile", "file", "image/xml", data);
        imageController.uploadStartupImage(fileWrongNameType, 1L, 2L);

    }

    @Test(expected = IllegalStateException.class)
    public void uploadStartUPImageWrongSizeTest() throws Exception {

        MultipartFile fileWrongSizeType = new MockMultipartFile("mockfile", "file", "image/jpg", dataSize);
        imageController.uploadStartupImage(fileWrongSizeType, 1L, 2L);
    }

    @Test
    public void uploadStartUpImageTest() throws Exception {

        Image imageTest = new Image();
        imageTest.setData(data);

        when(imageService.add(imageTest)).thenReturn(imageTest);
        when(imageService.get(111L)).thenReturn(imageTest);
        when(imageService.add(imageTest)).thenReturn(imageTest);

        MultipartFile fileOk = new MockMultipartFile("mockfile", "file", "image/jpg", data);
        MultipartFile fileNameTypePng = new MockMultipartFile("mockfile", "file", "image/png", data);
        MultipartFile fileNameTypeGif = new MockMultipartFile("mockfile", "file", "image/gif", data);

        assertEquals(imageController.uploadStartupImage(fileNameTypePng, 1L, 1L), "redirect:/startups/" + startup.getId());
        assertEquals(imageController.uploadStartupImage(fileNameTypeGif, 1L, 1L), "redirect:/startups/" + startup.getId());
        verify(startupService, times(2)).update(startup);
        imageController.uploadStartupImage(fileOk, 1L, 111L);
        verify(imageService).update(image);

    }

    private ServletOutputStream getServletOutputStream() {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                System.out.println(b);
            }
        };
    }

}