package com.goit.startup.controller;

import com.goit.startup.entity.Image;
import com.goit.startup.entity.Startup;
import com.goit.startup.entity.User;
import com.goit.startup.service.ImageService;
import com.goit.startup.service.StartupService;
import com.goit.startup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The class is responsible of processing image related requests
 *
 * @author Perevoznyk Pavlo
 *         created on 17 may 2017
 * @version 1.0
 */
@Controller
@RequestMapping("/images")
public class ImageController {

    /**
     * Id of image which will be used by default
     */
    private long defaultImageId = 1L;

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
     * Constructor
     *
     * @param imageService   startupService an instance of implementation {@link ImageService} interface
     * @param userService    startupService an instance of implementation {@link UserService} interface
     * @param startupService startupService an instance of implementation {@link StartupService} interface
     */
    @Autowired
    public ImageController(ImageService imageService, UserService userService, StartupService startupService) {
        this.imageService = imageService;
        this.userService = userService;
        this.startupService = startupService;
    }

    /**
     * Method for getting image from DB.
     *
     * @param imageId  an instance of implementation {@link ImageService} interface.
     * @param response an instance of {@link HttpServletResponse}.
     * @param request  an instance of {@link HttpServletResponse}.
     * @throws ServletException in case of problems with servlet.
     * @throws IOException      in case of problems with input/output.
     */
    @RequestMapping(value = "/{imageId}", method = RequestMethod.GET)
    public void getImage(@PathVariable(name = "imageId") long imageId,
                         HttpServletResponse response, HttpServletRequest request)
            throws ServletException, IOException {
        Image image = imageService.get(imageId);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(image.getData());
    }

    /**
     * Method for uploading user's image.
     *
     * @param file    an image that user uploads.
     * @param userId  user's id.
     * @param imageId id of user's image from DB.
     * @return an address of user's page.
     * @throws ServletException in case of problems with servlet.
     * @throws IOException      in case of problems with input/output.
     */
    @RequestMapping(value = "/uploadUserImage/{userId}/{imageId}", method = RequestMethod.POST)
    public String uploadUserImage(@RequestParam("file") MultipartFile file,
                                  @PathVariable("userId") long userId,
                                  @PathVariable("imageId") long imageId)
            throws ServletException, IOException {
        User user = userService.get(userId);
        System.out.println(file.getContentType());
        if (!file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/gif") && !file.getContentType().equals("image/png")) {
            throw new IllegalStateException(
                    "The file you selected is of incorrect type. An image should be .jpg, .gif or .png");
        }
        if (file.getSize() > 1024 * 100) {
            throw new IllegalStateException("The file you selected is too big. A file must be less than 100 kB.");
        }
        if (imageId != defaultImageId) {
            Image image = imageService.get(imageId);
            image.setData(file.getBytes());
            imageService.update(image);
        } else {
            Image newImage = new Image();
            newImage.setData(file.getBytes());
            newImage = imageService.add(newImage);
            user.setImageId(newImage.getId());
            userService.update(user);
        }
        return "redirect:/user/" + user.getUsername() + "/true";
    }

    /**
     * Method for uploading startup's image.
     *
     * @param file      an image that user uploads.
     * @param startupId startup's id.
     * @param imageId   id of startup's image from DB.
     * @return an address of startup's page.
     * @throws ServletException in case of problems with servlet.
     * @throws IOException      in case of problems with input/output.
     */
    @RequestMapping(value = "/uploadStartupImage/{startupId}/{imageId}", method = RequestMethod.POST)
    public String uploadStartupImage(@RequestParam("file") MultipartFile file,
                                     @PathVariable("startupId") long startupId,
                                     @PathVariable("imageId") long imageId)
            throws ServletException, IOException {
        Startup startup = startupService.get(startupId);
        if (!file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/gif") && !file.getContentType().equals("image/png")) {
            throw new IllegalStateException(
                    "The file you selected is of incorrect type. An image should be .jpg, .gif or .png");
        }
        if (file.getSize() > 1024 * 1024) {
            throw new IllegalStateException("The file you selected is too big. A file must be less than 1 MB.");
        }
        if (imageId != defaultImageId) {
            Image image = imageService.get(imageId);
            image.setData(file.getBytes());
            imageService.update(image);
        } else {
            Image newImage = new Image();
            newImage.setData(file.getBytes());
            newImage = imageService.add(newImage);
            startup.setImageId(newImage.getId());
            startupService.update(startup);
        }
        return "redirect:/startups/" + startup.getId();
    }
}
