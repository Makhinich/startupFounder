package com.goit.startup.controller;

import com.goit.startup.entity.Image;
import com.goit.startup.entity.User;
import com.goit.startup.service.ImageService;
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

    private long defaultImageId = 1L;

    private ImageService imageService;

    private UserService userService;

    @Autowired
    public ImageController(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }


    @RequestMapping(value = "/{imageId}", method = RequestMethod.GET)
    public void getImage(@PathVariable(name = "imageId") long imageId,
                         HttpServletResponse response, HttpServletRequest request)
            throws ServletException, IOException {
        Image image = imageService.get(imageId);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(image.getData());
    }

    @RequestMapping(value = "/upload/{userId}/{imageId}", method = RequestMethod.POST)
    public String uploadImage(@RequestParam("file") MultipartFile file, @PathVariable("userId") long userId, @PathVariable("imageId") long imageId)
            throws ServletException, IOException {
        if (imageId != defaultImageId) {
            Image image = imageService.get(imageId);
            image.setData(file.getBytes());
            imageService.update(image);
        } else {
            Image newImage = new Image();
            newImage.setData(file.getBytes());
            User user = userService.get(userId);
            newImage = imageService.add(newImage);
            user.setImageId(newImage.getId());
            userService.update(user);
        }
        return "redirect:/";
    }
}