/*
 * ProfileController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import com.yktsang.virtrade.api.controller.AccountService;
import com.yktsang.virtrade.api.controller.ProfileService;
import com.yktsang.virtrade.api.controller.RefreshTokenService;
import com.yktsang.virtrade.request.*;
import com.yktsang.virtrade.response.CompleteProfileResponse;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.JwtResponse;
import com.yktsang.virtrade.response.ProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.Objects;

/**
 * The controller for <code>Trader</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class ProfileController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    /**
     * The profile service.
     */
    @Autowired
    private ProfileService profileService;
    /**
     * The account service.
     */
    @Autowired
    private AccountService acctService;
    /**
     * The refresh token service.
     */
    @Autowired
    private RefreshTokenService refreshTokenService;
    /**
     * The admin email.
     */
    @Value("${admin.email}")
    private String adminEmail; // from application.properties

    /**
     * Shows the member registration form.
     *
     * @return the member registration page
     */
    @GetMapping("/register")
    public ModelAndView showMemberRegistration() {
        return new ModelAndView("registration");
    }

    /**
     * Registers the member.
     * If there are any validation errors, it will return back to the member registration page.
     * If successful, it will go to the member profile page.
     *
     * @param request the HTTP request
     * @param session the HTTP session
     * @return the member profile page, the member registration page otherwise
     */
    @PostMapping("/register")
    public ModelAndView executeMemberRegistration(HttpServletRequest request, HttpSession session) {
        String email = request.getParameter("email");
        String rawPassword = request.getParameter("password1");
        String confirmPassword = request.getParameter("password2");
        String fullName = request.getParameter("fullName");
        int birthYear = Integer.parseInt(request.getParameter("dateOfBirthYear"));
        int birthMonth = Integer.parseInt(request.getParameter("dateOfBirthMonth"));
        int birthDay = Integer.parseInt(request.getParameter("dateOfBirthDay"));
        boolean hideDateOfBirth = Objects.nonNull(request.getParameter("hideDateOfBirth"))
                && request.getParameter("hideDateOfBirth").equalsIgnoreCase("Y");
        String riskTolerance = request.getParameter("riskTolerance");
        boolean autoTransferToBank = Objects.nonNull(request.getParameter("autoTransferToBank"))
                && request.getParameter("autoTransferToBank").equalsIgnoreCase("Y");
        boolean allowReset = Objects.nonNull(request.getParameter("allowReset"))
                && request.getParameter("allowReset").equalsIgnoreCase("Y");

        RegistrationRequest actualReq =
                new RegistrationRequest(email, rawPassword, confirmPassword, fullName,
                        birthYear, birthMonth, birthDay, hideDateOfBirth,
                        riskTolerance, autoTransferToBank, allowReset);
        URI apiEndpoint = URI.create("/api/v1/registration");

        RequestEntity<RegistrationRequest> req =
                new RequestEntity<>(actualReq, HttpMethod.POST, apiEndpoint);
        ResponseEntity<?> resp = profileService.register(req);
        logger.info("register={}", resp.getStatusCode());

        // validation errors (HTTP 400)
        if (resp.getStatusCode() == HttpStatus.BAD_REQUEST) {
            ErrorResponse errResp = (ErrorResponse) resp.getBody();
            ModelAndView mv = new ModelAndView("registration");
            mv.addObject("errorMessage",
                    Objects.requireNonNull(errResp).errorMessage());
            return mv;
        }

        // created (HTTP 201)
        JwtResponse actualResp = (JwtResponse) resp.getBody();
        String token = Objects.requireNonNull(actualResp).token();
        session.setAttribute("jwt", token);
        session.setMaxInactiveInterval(1800); // 1800 sec (30 min)
        session.setAttribute("email", email);
        return new ModelAndView("redirect:/member/profile");
    }

    /**
     * Shows the member public profile page.
     *
     * @param session the HTTP session
     * @return the member public profile page, the member login page otherwise
     */
    @GetMapping("/member/publicProfile")
    public ModelAndView showPublicProfile(HttpSession session) {
        if (Objects.nonNull(session.getAttribute("email"))) {
            String email = (String) session.getAttribute("email");
            String jwt = (String) session.getAttribute("jwt");

            RefreshTokenRequest refreshActualReq = new RefreshTokenRequest(email, jwt);
            RequestEntity<RefreshTokenRequest> refreshReq =
                    new RequestEntity<>(refreshActualReq, HttpMethod.POST, URI.create("/api/v1/refreshToken"));
            ResponseEntity<?> refreshResp = refreshTokenService.refreshToken(refreshReq);
            if (refreshResp.getStatusCode() == HttpStatus.OK) {
                JwtResponse actualResp = (JwtResponse) refreshResp.getBody();
                String newToken = Objects.requireNonNull(actualResp).token();
                session.setAttribute("jwt", newToken);
                jwt = newToken;
            }

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            URI apiEndpoint = URI.create("/api/v1/member/profile/public");

            RequestEntity<Void> req =
                    new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> resp = profileService.publicProfile(req);
            logger.info("publicProfile={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // account not found (HTTP 404)
            if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return this.loadLoginPageWhenAccountNotFound(resp);
            }

            // success (HTTP 200)
            ProfileResponse actualResp = (ProfileResponse) resp.getBody();
            ModelAndView mv = new ModelAndView("member/publicProfile");
            mv.addObject("email", email);
            mv.addObject("trader", Objects.requireNonNull(actualResp).getTrader());
            return mv;

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Shows the member profile page.
     *
     * @param session the HTTP session
     * @return the member profile page, the member login page otherwise
     */
    @GetMapping("/member/profile")
    public ModelAndView showMemberProfile(HttpSession session) {
        if (Objects.nonNull(session.getAttribute("email"))) {
            String email = (String) session.getAttribute("email");
            String jwt = (String) session.getAttribute("jwt");

            RefreshTokenRequest refreshActualReq = new RefreshTokenRequest(email, jwt);
            RequestEntity<RefreshTokenRequest> refreshReq =
                    new RequestEntity<>(refreshActualReq, HttpMethod.POST, URI.create("/api/v1/refreshToken"));
            ResponseEntity<?> refreshResp = refreshTokenService.refreshToken(refreshReq);
            if (refreshResp.getStatusCode() == HttpStatus.OK) {
                JwtResponse actualResp = (JwtResponse) refreshResp.getBody();
                String newToken = Objects.requireNonNull(actualResp).token();
                session.setAttribute("jwt", newToken);
                jwt = newToken;
            }

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);

            return this.loadPrivateProfilePage(email, headerMap, null, null);

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Returns the member profile page.
     *
     * @param email        the email address
     * @param headerMap    the HTTP headers
     * @param errorKey     the error key
     * @param errorMessage the error message
     * @return the member profile page
     */
    private ModelAndView loadPrivateProfilePage(String email, HttpHeaders headerMap,
                                                String errorKey, String errorMessage) {
        URI apiEndpoint = URI.create("/api/v1/member/profile/private");

        RequestEntity<Void> req =
                new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
        ResponseEntity<?> resp = profileService.privateProfile(req);
        logger.info("privateProfile={}", resp.getStatusCode());

        // not authorized (HTTP 401)
        if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=member");
        }

        // account not found (HTTP 404)
        if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
            return this.loadLoginPageWhenAccountNotFound(resp);
        }

        // success (HTTP 200)
        CompleteProfileResponse actualResp = (CompleteProfileResponse) resp.getBody();
        ModelAndView mv = new ModelAndView("member/profile");
        mv.addObject("email", email);
        mv.addObject("trader", Objects.requireNonNull(actualResp).getTrader());
        mv.addObject("account", actualResp.getAccount());
        mv.addObject("adminAccount", email.equals(adminEmail));

        if (Objects.nonNull(errorMessage)) {
            mv.addObject(errorKey, errorMessage);
        }
        return mv;
    }

    /**
     * Updates the member profile.
     * If there are any validation errors, it will return back to the member profile page.
     * If successful, it will go to the member profile page, the member login page otherwise.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the member profile page, the member login page otherwise
     */
    @PostMapping("/member/updateProfile")
    public ModelAndView updateMemberProfile(HttpSession session, HttpServletRequest request) {
        if (Objects.nonNull(session.getAttribute("email"))) {
            String email = (String) session.getAttribute("email");
            String jwt = (String) session.getAttribute("jwt");

            RefreshTokenRequest refreshActualReq = new RefreshTokenRequest(email, jwt);
            RequestEntity<RefreshTokenRequest> refreshReq =
                    new RequestEntity<>(refreshActualReq, HttpMethod.POST, URI.create("/api/v1/refreshToken"));
            ResponseEntity<?> refreshResp = refreshTokenService.refreshToken(refreshReq);
            if (refreshResp.getStatusCode() == HttpStatus.OK) {
                JwtResponse actualResp = (JwtResponse) refreshResp.getBody();
                String newToken = Objects.requireNonNull(actualResp).token();
                session.setAttribute("jwt", newToken);
                jwt = newToken;
            }

            String fullName = request.getParameter("fullName");
            int birthYear = Integer.parseInt(request.getParameter("dateOfBirthYear"));
            int birthMonth = Integer.parseInt(request.getParameter("dateOfBirthMonth"));
            int birthDay = Integer.parseInt(request.getParameter("dateOfBirthDay"));
            boolean hideDateOfBirth = Objects.nonNull(request.getParameter("hideDateOfBirth"))
                    && request.getParameter("hideDateOfBirth").equalsIgnoreCase("Y");
            String riskTolerance = request.getParameter("riskTolerance");
            boolean autoTransferToBank = Objects.nonNull(request.getParameter("autoTransferToBank"))
                    && request.getParameter("autoTransferToBank").equalsIgnoreCase("Y");
            boolean allowReset = Objects.nonNull(request.getParameter("allowReset"))
                    && request.getParameter("allowReset").equalsIgnoreCase("Y");

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            ProfileRequest actualReq = new ProfileRequest(email, fullName,
                    birthYear, birthMonth, birthDay, hideDateOfBirth,
                    riskTolerance, autoTransferToBank, allowReset);
            URI apiEndpoint = URI.create("/api/v1/member/profile/update");

            RequestEntity<ProfileRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = profileService.updateProfile(req);
            logger.info("updateProfile={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            //validation errors (HTTP 400)
            if (resp.getStatusCode() == HttpStatus.BAD_REQUEST) {
                ErrorResponse errResp = (ErrorResponse) resp.getBody();
                return this.loadPrivateProfilePage(email, headerMap,
                        "updateProfileErrorMessage", Objects.requireNonNull(errResp).errorMessage());
            }

            // account not found (HTTP 404)
            if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return this.loadLoginPageWhenAccountNotFound(resp);
            }

            // success (HTTP 200)
            return new ModelAndView("redirect:/member/profile");

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Updates the account.
     * If there are any validation errors, it will return back to the member profile page.
     * If successful, it will go to the member profile page, the member login page otherwise.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the member profile page, the member login page otherwise
     */
    @PostMapping("/member/updateAccount")
    public ModelAndView updateMemberAccount(HttpSession session, HttpServletRequest request) {
        if (Objects.nonNull(session.getAttribute("email"))) {
            String email = (String) session.getAttribute("email");
            String jwt = (String) session.getAttribute("jwt");

            RefreshTokenRequest refreshActualReq = new RefreshTokenRequest(email, jwt);
            RequestEntity<RefreshTokenRequest> refreshReq =
                    new RequestEntity<>(refreshActualReq, HttpMethod.POST, URI.create("/api/v1/refreshToken"));
            ResponseEntity<?> refreshResp = refreshTokenService.refreshToken(refreshReq);
            if (refreshResp.getStatusCode() == HttpStatus.OK) {
                JwtResponse actualResp = (JwtResponse) refreshResp.getBody();
                String newToken = Objects.requireNonNull(actualResp).token();
                session.setAttribute("jwt", newToken);
                jwt = newToken;
            }

            String rawNewPassword = request.getParameter("password1");
            String rawConfirmPassword = request.getParameter("password2");

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            ResetPasswordRequest actualReq =
                    new ResetPasswordRequest(email, rawNewPassword, rawConfirmPassword);
            URI apiEndpoint = URI.create("/api/v1/member/account/update");

            RequestEntity<ResetPasswordRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = acctService.updateAccount(req);
            logger.info("updateAccount={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400)
            if (resp.getStatusCode() == HttpStatus.BAD_REQUEST) {
                ErrorResponse errResp = (ErrorResponse) resp.getBody();
                return this.loadPrivateProfilePage(email, headerMap,
                        "updateAccountErrorMessage", Objects.requireNonNull(errResp).errorMessage());
            }

            // account not found (HTTP 404)
            if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return this.loadLoginPageWhenAccountNotFound(resp);
            }

            // success (HTTP 200)
            return new ModelAndView("redirect:/member/profile");

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Requests admin access.
     * If there is no action, it will return back to the member profile page.
     * If successful, it will go to the member profile page.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the member profile page if successful or no action, the member login page otherwise
     */
    @PostMapping("/member/requestAdmin")
    public ModelAndView requestAdmin(HttpSession session, HttpServletRequest request) {
        if (Objects.nonNull(session.getAttribute("email"))) {
            String email = (String) session.getAttribute("email");
            String jwt = (String) session.getAttribute("jwt");

            RefreshTokenRequest refreshActualReq = new RefreshTokenRequest(email, jwt);
            RequestEntity<RefreshTokenRequest> refreshReq =
                    new RequestEntity<>(refreshActualReq, HttpMethod.POST, URI.create("/api/v1/refreshToken"));
            ResponseEntity<?> refreshResp = refreshTokenService.refreshToken(refreshReq);
            if (refreshResp.getStatusCode() == HttpStatus.OK) {
                JwtResponse actualResp = (JwtResponse) refreshResp.getBody();
                String newToken = Objects.requireNonNull(actualResp).token();
                session.setAttribute("jwt", newToken);
                jwt = newToken;
            }

            boolean requestAdmin = Objects.nonNull(request.getParameter("admin"))
                    && request.getParameter("admin").equalsIgnoreCase("Y");

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            RequestAdminAccessRequest actualReq = new RequestAdminAccessRequest(requestAdmin);
            URI apiEndpoint = URI.create("/api/v1/member/admin/request");

            RequestEntity<RequestAdminAccessRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = acctService.requestAdminAccess(req);
            logger.info("requestAdminAccess={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // account not found (HTTP 404)
            if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return this.loadLoginPageWhenAccountNotFound(resp);
            }

            // success (HTTP 200) or no op (HTTP 304)
            return new ModelAndView("redirect:/member/profile");

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Deactivates the profile.
     * If there are any validation errors, it will return back to the member profile page.
     * If there is no action, it will return back to the member profile page.
     * If successful, it will log out user then go to the member login page.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the member profile page if validation errors or no action, the member login page otherwise
     */
    @PostMapping("/member/deactivateProfile")
    public ModelAndView deactivateProfile(HttpSession session, HttpServletRequest request) {
        if (Objects.nonNull(session.getAttribute("email"))) {
            String email = (String) session.getAttribute("email");
            String jwt = (String) session.getAttribute("jwt");

            RefreshTokenRequest refreshActualReq = new RefreshTokenRequest(email, jwt);
            RequestEntity<RefreshTokenRequest> refreshReq =
                    new RequestEntity<>(refreshActualReq, HttpMethod.POST, URI.create("/api/v1/refreshToken"));
            ResponseEntity<?> refreshResp = refreshTokenService.refreshToken(refreshReq);
            if (refreshResp.getStatusCode() == HttpStatus.OK) {
                JwtResponse actualResp = (JwtResponse) refreshResp.getBody();
                String newToken = Objects.requireNonNull(actualResp).token();
                session.setAttribute("jwt", newToken);
                jwt = newToken;
            }

            boolean deactivate = Objects.nonNull(request.getParameter("deactivate"))
                    && request.getParameter("deactivate").equalsIgnoreCase("Y");
            String deactivationReason = request.getParameter("reason");

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            DeactivateAccountRequest actualReq = new DeactivateAccountRequest(deactivate, deactivationReason);
            URI apiEndpoint = URI.create("/api/v1/member/profile/deactivate");

            RequestEntity<DeactivateAccountRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = profileService.deactivateProfile(req);
            logger.info("deactivateProfile={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400)
            if (resp.getStatusCode() == HttpStatus.BAD_REQUEST) {
                ErrorResponse errResp = (ErrorResponse) resp.getBody();
                return this.loadPrivateProfilePage(email, headerMap,
                        "deactivateAccountErrorMessage", Objects.requireNonNull(errResp).errorMessage());
            }

            // account not found (HTTP 404)
            if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return this.loadLoginPageWhenAccountNotFound(resp);
            }

            // no op (HTTP 304)
            if (resp.getStatusCode() == HttpStatus.NOT_MODIFIED) {
                return new ModelAndView("redirect:/member/profile");
            }

            // success (HTTP 200)
            return new ModelAndView("redirect:/member/logout");

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Returns the login page when account not found (HTTP 404).
     *
     * @param resp the response entity
     * @return the login page
     */
    private ModelAndView loadLoginPageWhenAccountNotFound(ResponseEntity<?> resp) {
        ErrorResponse errResp = (ErrorResponse) resp.getBody();
        ModelAndView mv = new ModelAndView("login");
        mv.addObject("subfolder", true);
        mv.addObject("referrer", "member");
        mv.addObject("errorMessage",
                Objects.requireNonNull(errResp).errorMessage());
        return mv;
    }

}
