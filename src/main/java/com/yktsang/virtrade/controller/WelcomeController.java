/*
 * WelcomeController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/**
 * The controller for the welcome page.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class WelcomeController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

    /**
     * Shows the main welcome page.
     *
     * @param session the HTTP session
     * @return the main welcome page
     */
    @GetMapping("/")
    public ModelAndView mainIndex(HttpSession session) {
        String email = (String) session.getAttribute("email");
        ModelAndView mv = new ModelAndView("welcome");
        mv.addObject("hasValidSession", Objects.nonNull(email));
        mv.addObject("email", email);
        return mv;
    }

}
