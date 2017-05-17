package com.goit.startup.controller;

import com.goit.startup.entity.Investment;
import com.goit.startup.entity.Startup;
import com.goit.startup.entity.User;
import com.goit.startup.service.StartupService;
import com.goit.startup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.Contended;

/**
 * The class is responsible of processing {@link com.goit.startup.entity.Startup} entity related requests
 *
 * @author Perevoznyk Pavlo
 *         created on 14 may 2017
 * @version 1.0
 */
@Controller
@RequestMapping(value = "/startups")
public class StartUpController {

    private StartupService startupService;

    private UserService userService;

    @Autowired
    public StartUpController(StartupService startupService, UserService userService) {
        this.startupService = startupService;
        this.userService = userService;
    }


    @RequestMapping(value = "/add/{userId}", method = RequestMethod.GET)
    public ModelAndView addStartupPage(@PathVariable(name = "userId") long userId){
        ModelAndView modelAndView = new ModelAndView();
        Startup startup = new Startup();
        startup.setAuthor(userService.get(userId));
        modelAndView.addObject("startup", startup);
        modelAndView.addObject("is_admin", this.userService.isAuthenticatedAdmin());
        modelAndView.setViewName("addStartUp");
        return modelAndView;
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addStartup(Startup startup){
        long userId = startup.getAuthor().getId();
        startup.setAuthor(userService.get(userId));
        startupService.add(startup);
        return "redirect:/";
    }

    @RequestMapping(value = "/{startupId}", method = RequestMethod.GET)
    public ModelAndView startUpDetails(@PathVariable(name = "startupId") long startupId){
        ModelAndView modelAndView = new ModelAndView();
        Startup startup = startupService.get(startupId);
        modelAndView.addObject("startup", startup);
        modelAndView.addObject("is_admin", this.userService.isAuthenticatedAdmin());
        modelAndView.setViewName("startUpDetails");
        return modelAndView;
    }

    @RequestMapping(value = "edit/{startupId}", method = RequestMethod.GET)
    public ModelAndView editStartupPage(@PathVariable(name = "startupId") long startupId) throws IllegalAccessException {
        Startup startup = startupService.get(startupId);
        if (!startup.getAuthor().equals(userService.getAuthenticatedUser()) || userService.isAuthenticatedAdmin()) {
            throw new IllegalAccessException("Only startup's author can edit the startup");
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("startup", startup);
        modelAndView.addObject("is_admin", this.userService.isAuthenticatedAdmin());
        modelAndView.setViewName("editStartUp");
        return modelAndView;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editStartup(Startup startup){
        long userId = startup.getAuthor().getId();
        startup.setAuthor(userService.get(userId));
        startupService.update(startup);
        return "redirect:/";
    }

    @RequestMapping(value = "delete/{startupId}", method = RequestMethod.GET)
    public String deleteStartup(@PathVariable(name = "startupId") long startupId) throws IllegalAccessException {
        Startup startup = startupService.get(startupId);
        if (!startup.getAuthor().equals(userService.getAuthenticatedUser()) || userService.isAuthenticatedAdmin()) {
            throw new IllegalAccessException("Only startup's author can edit the startup");
        }
        //        startupService.remove(startup);
        User user = userService.get(startup.getAuthor().getId());
        user.getStartups().remove(startup);
        userService.update(user);
        return "redirect:/";
    }
}
