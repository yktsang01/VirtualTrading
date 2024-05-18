/*
 * AdminController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.admin.controller;

import com.yktsang.virtrade.api.controller.AccountService;
import com.yktsang.virtrade.api.controller.RefreshTokenService;
import com.yktsang.virtrade.request.GrantAdminAccessRequest;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.request.RevokeAdminAccessRequest;
import com.yktsang.virtrade.response.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.*;

/**
 * The controller for administrators.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class AdminController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);
    /**
     * The account service.
     */
    @Autowired
    private AccountService accountService;
    /**
     * The refresh token service.
     */
    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Shows the admin welcome page.
     * Checks whether the admin account has reset password.
     * The reset password page is loaded if account has not reset password,
     * as in first setup or first time use, the admin login page otherwise.
     *
     * @return the admin login page or the reset password page
     */
    @GetMapping("/admin")
    public ModelAndView adminIndex() {
        if (!accountService.checkAdminLastUpdated()) {
            return new ModelAndView("redirect:/resetPassword?from=admin");
        }
        return new ModelAndView("redirect:/login?from=admin");
    }

    /**
     * Executes the admin logout.
     * Go to the admin login page.
     *
     * @param session the HTTP session
     * @return the admin login page
     */
    @GetMapping("/admin/logout")
    public ModelAndView executeAdminLogout(HttpSession session) {
        if (Objects.nonNull(session.getAttribute("email"))
                && Objects.nonNull(session.getAttribute("admin"))) {
            session.invalidate();
            logger.info("admin session invalidated");
        }
        return new ModelAndView("redirect:/login?from=admin");
    }

    /**
     * Shows the admin profile page.
     * If failed, it will go to the admin login page.
     *
     * @param session the HTTP session
     * @return the admin profile page, the admin login page otherwise
     */
    @GetMapping("/admin/profile")
    public ModelAndView showAdminProfile(HttpSession session) {
        if (Objects.nonNull(session.getAttribute("email"))
                && Objects.nonNull(session.getAttribute("admin"))) {
            String email = (String) session.getAttribute("email");

            ModelAndView mv = new ModelAndView("admin/profile");
            mv.addObject("email", email);
            return mv;
        } else {
            return new ModelAndView("redirect:/login?from=admin");
        }
    }

    /**
     * Shows the admin requests page.
     * If failed, it will go to the admin login page.
     *
     * @param session  the HTTP session
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the admin requests page, the admin login page otherwise.
     */
    @GetMapping("/admin/adminRequests")
    public ModelAndView showAdminRequests(HttpSession session,
                                          @RequestParam("page") Optional<Integer> page,
                                          @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))
                && Objects.nonNull(session.getAttribute("admin"))) {
            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

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
            URI apiEndpoint = URI.create("/api/v1/admin/requests");

            RequestEntity<Void> req = new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> resp = accountService.adminRequests(req, defaultStartPage, defaultPageSize);
            logger.info("adminRequests={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=admin");
            }

            // forbidden (HTTP 403)
            if (resp.getStatusCode() == HttpStatus.FORBIDDEN) {
                return this.loadLoginPageWhenForbidden(resp);
            }

            ModelAndView mv = new ModelAndView("admin/adminRequests");
            mv.addObject("email", email);

            // success (HTTP 200) or no data returned (HTTP 204)
            if (resp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("adminRequests", new ArrayList<AdminAccessRequest>());
                mv.addObject("hasRecords", false);
            } else {
                Map<String, String> respHeaderMap = resp.getHeaders().toSingleValueMap();
                String first = respHeaderMap.get("first");
                String prev = respHeaderMap.get("prev");
                String next = respHeaderMap.get("next");
                String last = respHeaderMap.get("last");
                int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
                boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
                boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
                AdminRequestResponse actualResp = (AdminRequestResponse) resp.getBody();
                mv.addObject("adminRequests", Objects.requireNonNull(actualResp).adminRequests());
                mv.addObject("hasRecords", totalPageCount > 0);
                mv.addObject("first", first);
                mv.addObject("prev", prev);
                mv.addObject("next", next);
                mv.addObject("last", last);
                mv.addObject("hasPrev", hasPrev);
                mv.addObject("hasNext", hasNext);
            }

            return mv;
        } else {
            return new ModelAndView("redirect:/login?from=admin");
        }
    }

    /**
     * Grants the admin access.
     * If successful, it will go to the admin requests page, the admin login page otherwise.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the admin requests page, the admin login page otherwise
     */
    @PostMapping("/admin/adminRequests")
    public ModelAndView grantAdminAccess(HttpSession session, HttpServletRequest request) {
        if (Objects.nonNull(session.getAttribute("email"))
                && Objects.nonNull(session.getAttribute("admin"))) {
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

            String[] grants = Objects.requireNonNullElse(
                    request.getParameterValues("grant"), new String[]{}); // null if nothing is selected

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            GrantAdminAccessRequest actualReq = new GrantAdminAccessRequest(Arrays.stream(grants).toList());
            URI apiEndpoint = URI.create("/api/v1/admin/accesses/grant");

            RequestEntity<GrantAdminAccessRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = accountService.grantAdminAccesses(req);
            logger.info("grantAdminAccesses={}", resp.getStatusCode());

            // validation errors (HTTP 400) will not happen

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=admin");
            }

            // forbidden (HTTP 403)
            if (resp.getStatusCode() == HttpStatus.FORBIDDEN) {
                return this.loadLoginPageWhenForbidden(resp);
            }

            // success (HTTP 200) or no op (HTTP 304)
            return new ModelAndView("redirect:/admin/adminRequests");
        } else {
            return new ModelAndView("redirect:/login?from=admin");
        }
    }

    /**
     * Shows the admin access page.
     * If failed, it will go to the admin login page.
     *
     * @param session  the HTTP session
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the admin access page, the admin login page otherwise
     */
    @GetMapping("/admin/adminAccess")
    public ModelAndView showAdminAccess(HttpSession session,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))
                && Objects.nonNull(session.getAttribute("admin"))) {
            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

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
            URI apiEndpoint = URI.create("/api/v1/admin/accesses");

            RequestEntity<Void> req = new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> resp = accountService.adminAccesses(req, defaultStartPage, defaultPageSize);
            logger.info("adminAccesses={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=admin");
            }

            // forbidden (HTTP 403)
            if (resp.getStatusCode() == HttpStatus.FORBIDDEN) {
                return this.loadLoginPageWhenForbidden(resp);
            }

            ModelAndView mv = new ModelAndView("admin/adminAccess");
            mv.addObject("email", email);

            // success (HTTP 200) or no data returned (HTTP 204)
            if (resp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("adminAccesses", new ArrayList<AdminAccessRequest>());
                mv.addObject("hasRecords", false);
            } else {
                Map<String, String> respHeaderMap = resp.getHeaders().toSingleValueMap();
                String first = respHeaderMap.get("first");
                String prev = respHeaderMap.get("prev");
                String next = respHeaderMap.get("next");
                String last = respHeaderMap.get("last");
                int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
                boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
                boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
                AdminAccessResponse actualResp = (AdminAccessResponse) resp.getBody();
                mv.addObject("adminAccesses", Objects.requireNonNull(actualResp).adminAccesses());
                mv.addObject("hasRecords", totalPageCount > 0);
                mv.addObject("first", first);
                mv.addObject("prev", prev);
                mv.addObject("next", next);
                mv.addObject("last", last);
                mv.addObject("hasPrev", hasPrev);
                mv.addObject("hasNext", hasNext);
            }

            return mv;
        } else {
            return new ModelAndView("redirect:/login?from=admin");
        }
    }

    /**
     * Revokes the admin access.
     * If successful, it will go to the admin access page, the admin login page otherwise.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the admin access page, the admin login page otherwise
     */
    @PostMapping("/admin/adminAccess")
    public ModelAndView revokeAdminAccess(HttpSession session, HttpServletRequest request) {
        if (Objects.nonNull(session.getAttribute("email"))
                && Objects.nonNull(session.getAttribute("admin"))) {
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

            String[] revokes = Objects.requireNonNullElse(
                    request.getParameterValues("revoke"), new String[]{}); // null if nothing is selected

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            RevokeAdminAccessRequest actualReq = new RevokeAdminAccessRequest(Arrays.stream(revokes).toList());
            URI apiEndpoint = URI.create("/api/v1/admin/accesses/revoke");

            RequestEntity<RevokeAdminAccessRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = accountService.revokeAdminAccesses(req);
            logger.info("revokeAdminAccesses={}", resp.getStatusCode());

            // validation errors (HTTP 400) will not happen

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=admin");
            }

            // forbidden (HTTP 403)
            if (resp.getStatusCode() == HttpStatus.FORBIDDEN) {
                return this.loadLoginPageWhenForbidden(resp);
            }

            // success (HTTP 200) or no op (HTTP 304)
            return new ModelAndView("redirect:/admin/adminAccess");
        } else {
            return new ModelAndView("redirect:/login?from=admin");
        }
    }

    /**
     * Returns the login page when HTTP status code is 403 (forbidden).
     *
     * @param resp the response entity
     * @return the login page
     */
    private ModelAndView loadLoginPageWhenForbidden(ResponseEntity<?> resp) {
        ErrorResponse errResp = (ErrorResponse) resp.getBody();
        ModelAndView mv = new ModelAndView("login");
        mv.addObject("subfolder", true);
        mv.addObject("referrer", "admin");
        mv.addObject("errorMessage",
                Objects.requireNonNull(errResp).errorMessage());
        return mv;
    }

}
