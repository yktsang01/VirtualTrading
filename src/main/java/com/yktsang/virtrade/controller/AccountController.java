/*
 * AccountController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import com.yktsang.virtrade.api.controller.AccountService;
import com.yktsang.virtrade.request.AuthRequest;
import com.yktsang.virtrade.request.ResetPasswordRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.JwtResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * The controller for <code>Account</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class AccountController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);
    /**
     * The account service.
     */
    @Autowired
    private AccountService accountService;

    /**
     * Shows the login page.
     *
     * @param from the Optional containing the referrer
     * @return the login page
     */
    @GetMapping("/login")
    public ModelAndView showLogin(@RequestParam Optional<String> from) {
        if (from.isEmpty() || from.get().isEmpty()) {
            return new ModelAndView("redirect:/login?from=member");
        }

        ModelAndView mv = new ModelAndView("login");
        mv.addObject("subfolder", false);
        mv.addObject("referrer", from.get());
        return mv;
    }

    /**
     * Performs the login.
     * If there are any validation errors, it will return back to the login page.
     * If account is not found, it will go to the member registration page for members, admin login page for admins.
     * If successful, it will go to the member dashboard page for members, admin profile page for admins.
     *
     * @param request the HTTP request
     * @param session the HTTP session
     * @return the appropriate page if successful, the login page if validation errors,
     * the appropriate page if account not found
     */
    @PostMapping("/login")
    public ModelAndView executeLogin(HttpServletRequest request, HttpSession session) {
        String referrer = request.getParameter("referrer");
        String email = request.getParameter("email");
        String rawPassword = request.getParameter("password");

        AuthRequest actualReq = new AuthRequest(email, rawPassword);
        actualReq.setAdminLogin(referrer.equalsIgnoreCase("admin"));
        URI apiEndpoint = URI.create("/api/v1/login");

        RequestEntity<AuthRequest> req =
                new RequestEntity<>(actualReq, HttpMethod.POST, apiEndpoint);
        ResponseEntity<?> resp = accountService.login(req);
        logger.info("login={}", resp.getStatusCode());

        // validation errors (HTTP 400) or authentication errors (HTTP 403)
        if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                || resp.getStatusCode() == HttpStatus.FORBIDDEN) {
            return this.loadPageAgainWhenValidationOrAuthFailed(resp, "login", referrer);
        }

        // account not found (HTTP 404)
        if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
            return this.loadPageWhenAccountNotFound(resp, referrer);
        }

        // success (HTTP 200)
        JwtResponse actualResp = (JwtResponse) resp.getBody();
        String token = Objects.requireNonNull(actualResp).token();
        session.setAttribute("jwt", token);
        session.setMaxInactiveInterval(1800); // 1800 sec (30 min)
        session.setAttribute("email", email);

        if (referrer.equalsIgnoreCase("admin")) {
            session.setAttribute("admin", true);
            return new ModelAndView("redirect:/admin/profile");
        } else {
            return new ModelAndView("redirect:/member/dashboard");
        }
    }

    /**
     * Executes the member logout.
     * Go to the member login page.
     *
     * @param session the HTTP session
     * @return the member login page
     */
    @GetMapping("/member/logout")
    public ModelAndView executeMemberLogout(HttpSession session) {
        if (Objects.nonNull(session.getAttribute("email"))) {
            session.invalidate();
            logger.info("member session invalidated");
        }
        return new ModelAndView("redirect:/login?from=member");
    }

    /**
     * Shows the reset password page.
     *
     * @param from the Optional containing the referrer
     * @return the reset password page
     */
    @GetMapping("/resetPassword")
    public ModelAndView showResetPassword(@RequestParam Optional<String> from) {
        if (from.isEmpty() || from.get().isEmpty()) {
            return new ModelAndView("redirect:/resetPassword?from=member");
        }

        ModelAndView mv = new ModelAndView("resetPassword");
        mv.addObject("referrer", from.get());
        return mv;
    }

    /**
     * Executes the reset password.
     * If there are any validation errors, it will return back to the reset password page.
     * If account is not found, it will go to the member registration page.
     * If successful, it will go to the member login page.
     *
     * @param request the HTTP request
     * @return the member login page if successful, the reset password page if validation errors,
     * the member registration page if account not found
     */
    @PostMapping("/resetPassword")
    public ModelAndView executeResetPassword(HttpServletRequest request) {
        String referrer = request.getParameter("referrer");
        String email = request.getParameter("email");
        String rawNewPassword = request.getParameter("password1");
        String rawConfirmPassword = request.getParameter("password2");

        ResetPasswordRequest actualReq =
                new ResetPasswordRequest(email, rawNewPassword, rawConfirmPassword);
        URI apiEndpoint = URI.create("/api/v1/password/reset");

        RequestEntity<ResetPasswordRequest> req =
                new RequestEntity<>(actualReq, HttpMethod.POST, apiEndpoint);
        ResponseEntity<?> resp = accountService.resetPassword(req);
        logger.info("resetPassword={}", resp.getStatusCode());

        // validation errors (HTTP 400)
        if (resp.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return this.loadPageAgainWhenValidationOrAuthFailed(resp, "resetPassword", referrer);
        }

        // account not found (HTTP 404)
        if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
            return this.loadPageWhenAccountNotFound(resp, referrer);
        }

        // success (HTTP 200)
        return new ModelAndView("redirect:/login?from=" + referrer);
    }

    /**
     * Return the same page when validation or authentication failed.
     *
     * @param resp     the entity response
     * @param viewName the view name
     * @param referrer the referrer
     * @return the same page when validation or authentication failed
     */
    private ModelAndView loadPageAgainWhenValidationOrAuthFailed(ResponseEntity<?> resp,
                                                                 String viewName, String referrer) {
        ErrorResponse errResp = (ErrorResponse) resp.getBody();
        ModelAndView mv = new ModelAndView(viewName);
        mv.addObject("subfolder", false);
        mv.addObject("referrer", referrer);
        mv.addObject("errorMessage",
                Objects.requireNonNull(errResp).errorMessage());
        return mv;
    }

    /**
     * Returns the appropriate page when account not found (HTTP 404).
     *
     * @param resp     the response entity
     * @param referrer the referrer
     * @return the appropriate page when account not found
     */
    private ModelAndView loadPageWhenAccountNotFound(ResponseEntity<?> resp, String referrer) {
        ErrorResponse errResp = (ErrorResponse) resp.getBody();
        ModelAndView mv = new ModelAndView("registration");
        if (referrer.equalsIgnoreCase("admin")) {
            mv = new ModelAndView("login");
            mv.addObject("subfolder", false);
            mv.addObject("referrer", referrer);
        }
        mv.addObject("errorMessage",
                Objects.requireNonNull(errResp).errorMessage());
        return mv;
    }

}
