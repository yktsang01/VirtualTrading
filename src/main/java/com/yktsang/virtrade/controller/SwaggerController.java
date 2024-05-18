/*
 * SwaggerController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Swagger controller.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class SwaggerController {

    /**
     * Shows the Swagger UI.
     *
     * @return the Swagger UI
     */
    @GetMapping("/api")
    public ModelAndView apiIndex() {
        return new ModelAndView("redirect:/swagger-ui.html");
    }

}
