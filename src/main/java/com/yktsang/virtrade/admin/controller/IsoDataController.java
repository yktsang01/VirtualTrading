/*
 * IsoDataController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.admin.controller;

import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.controller.RefreshTokenService;
import com.yktsang.virtrade.request.ActivateIsoRequest;
import com.yktsang.virtrade.request.CreateIsoRequest;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.request.UpdateIsoRequest;
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
 * The controller for <code>IsoData</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class IsoDataController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(IsoDataController.class);
    /**
     * The ISO data service.
     */
    @Autowired
    private IsoDataService isoDataService;
    /**
     * The refresh token service.
     */
    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Shows the ISO data page.
     * If failed, it will go to the admin login page.
     *
     * @param session  the HTTP session
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the ISO data page, the admin login page otherwise
     */
    @GetMapping("/admin/isoData")
    public ModelAndView showIsoData(HttpSession session,
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
            URI apiEndpoint = URI.create("/api/v1/admin/iso");

            RequestEntity<Void> req = new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> resp = isoDataService.isoData(req, defaultStartPage, defaultPageSize);
            logger.info("isoData={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=admin");
            }

            // forbidden (HTTP 403)
            if (resp.getStatusCode() == HttpStatus.FORBIDDEN) {
                return this.loadLoginPageWhenForbidden(resp);
            }

            ModelAndView mv = new ModelAndView("admin/isoData");
            mv.addObject("email", email);

            // success (HTTP 200) or no data returned (HTTP 204)
            if (resp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("isoCodes", new ArrayList<IsoCode>());
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
                IsoDataResponse actualResp = (IsoDataResponse) resp.getBody();
                mv.addObject("isoCodes", Objects.requireNonNull(actualResp).isoData());
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
     * Shows the ISO data creation page.
     * If failed, it will go to the admin login page.
     *
     * @param session the HTTP session
     * @return the ISO data creation page, the admin login page otherwise
     */
    @GetMapping("/admin/isoCreation")
    public ModelAndView showIsoDataCreation(HttpSession session) {
        if (Objects.nonNull(session.getAttribute("email"))
                && Objects.nonNull(session.getAttribute("admin"))) {
            String email = (String) session.getAttribute("email");

            return this.loadCreateIsoPage(email, null);

        } else {
            return new ModelAndView("redirect:/login?from=admin");
        }
    }

    /**
     * Returns the ISO data creation page.
     *
     * @param email        the email address
     * @param errorMessage the error message
     * @return the ISO data creation page
     */
    private ModelAndView loadCreateIsoPage(String email, String errorMessage) {
        ModelAndView mv = new ModelAndView("admin/addIsoData");
        mv.addObject("email", email);

        if (Objects.nonNull(errorMessage)) {
            mv.addObject("errorMessage", errorMessage);
        }

        return mv;
    }

    /**
     * Creates the ISO data.
     * If successful, it will go to the ISO data page, the admin login page otherwise.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the ISO data page, the admin login page otherwise
     */
    @PostMapping("/admin/createIsoData")
    public ModelAndView createIsoData(HttpSession session, HttpServletRequest request) {
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

            String countryCode = request.getParameter("countryCode");
            String countryName = request.getParameter("countryName");
            String currencyCode = request.getParameter("currencyCode");
            String currencyName = request.getParameter("currencyName");
            Integer currencyMinorUnits = request.getParameter("currencyMinorUnits")
                    .equalsIgnoreCase("UNKNOWN") ? null
                    : Integer.valueOf(request.getParameter("currencyMinorUnits"));
            boolean wantToActivate = Objects.nonNull(request.getParameter("activate"))
                    && request.getParameter("activate").equalsIgnoreCase("Y");

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            CreateIsoRequest actualReq = new CreateIsoRequest(countryCode, countryName,
                    currencyCode, currencyName, currencyMinorUnits, wantToActivate);
            URI apiEndpoint = URI.create("/api/v1/admin/iso/create");

            RequestEntity<CreateIsoRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = isoDataService.createIsoData(req);
            logger.info("createIsoData={}", resp.getStatusCode());

            // validation errors (HTTP 400)
            if (resp.getStatusCode() == HttpStatus.BAD_REQUEST) {
                ErrorResponse errResp = (ErrorResponse) resp.getBody();
                return this.loadCreateIsoPage(email, Objects.requireNonNull(errResp).errorMessage());
            }

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=admin");
            }

            // forbidden (HTTP 403)
            if (resp.getStatusCode() == HttpStatus.FORBIDDEN) {
                return this.loadLoginPageWhenForbidden(resp);
            }

            // success (HTTP 201)
            return new ModelAndView("redirect:/admin/isoData");
        } else {
            return new ModelAndView("redirect:/login?from=admin");
        }
    }

    /**
     * Shows the ISO data update page.
     * If failed, it will go to the admin login page.
     *
     * @param session the HTTP session
     * @param code    the Optional containing the country code
     * @return the ISO data update page, the admin login page otherwise
     */
    @GetMapping("/admin/isoUpdate")
    public ModelAndView showIsoDataUpdate(HttpSession session, @RequestParam Optional<String> code) {
        if (Objects.nonNull(session.getAttribute("email"))
                && Objects.nonNull(session.getAttribute("admin"))) {

            if (code.isEmpty() || code.get().isEmpty()) {
                return new ModelAndView("redirect:/admin/isoData");
            }

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

            return this.loadAmendIsoPage(email, headerMap, code.get(), null);

        } else {
            return new ModelAndView("redirect:/login?from=admin");
        }
    }

    /**
     * Returns the ISO data update page.
     *
     * @param email        the email address
     * @param headerMap    the HTTP headers
     * @param countryCode  the country code
     * @param errorMessage the error message
     * @return the ISO data update page
     */
    private ModelAndView loadAmendIsoPage(String email, HttpHeaders headerMap,
                                          String countryCode, String errorMessage) {
        URI apiEndpoint = URI.create("/api/v1/admin/iso/" + countryCode);

        RequestEntity<Void> req = new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
        ResponseEntity<?> resp = isoDataService.isoData(req, countryCode);
        logger.info("isoData={}", resp.getStatusCode());

        // not authorized (HTTP 401)
        if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=admin");
        }

        // forbidden (HTTP 403)
        if (resp.getStatusCode() == HttpStatus.FORBIDDEN) {
            return this.loadLoginPageWhenForbidden(resp);
        }

        // validation errors (HTTP 400) or ISO data not found (HTTP 404)
        if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                || resp.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ModelAndView("redirect:/admin/isoData");
        }

        // success (HTTP 200)
        IsoCodeResponse actualResp = (IsoCodeResponse) resp.getBody();
        IsoCode isoCode = Objects.requireNonNull(actualResp).isoCode();

        ModelAndView mv = new ModelAndView("admin/amendIsoData");
        mv.addObject("email", email);
        mv.addObject("data", isoCode);

        if (Objects.nonNull(errorMessage)) {
            mv.addObject("errorMessage", errorMessage);
        }

        return mv;
    }

    /**
     * Updates the ISO data. Also allow to deactivate the ISO data if currently active.
     * If successful, it will go to the ISO data page, the admin login page otherwise.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the ISO data page, the admin login page otherwise
     */
    @PostMapping("/admin/updateIsoData")
    public ModelAndView updateIsoData(HttpSession session, HttpServletRequest request) {
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

            String countryCode = request.getParameter("hiddenCountryCode");
            String countryName = request.getParameter("countryName");
            String currencyCode = request.getParameter("currencyCode");
            String currencyName = request.getParameter("currencyName");
            Integer currencyMinorUnits = request.getParameter("currencyMinorUnits")
                    .equalsIgnoreCase("UNKNOWN") ? null
                    : Integer.valueOf(request.getParameter("currencyMinorUnits"));
            boolean wantToDeactivate = Objects.nonNull(request.getParameter("deactivate"))
                    && request.getParameter("deactivate").equalsIgnoreCase("Y");

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            UpdateIsoRequest actualReq = new UpdateIsoRequest(countryCode, countryName,
                    currencyCode, currencyName, currencyMinorUnits, wantToDeactivate);
            URI apiEndpoint = URI.create("/api/v1/admin/iso/update");

            RequestEntity<UpdateIsoRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = isoDataService.updateIsoData(req);
            logger.info("updateIsoData={}", resp.getStatusCode());

            // validation errors (HTTP 400)
            if (resp.getStatusCode() == HttpStatus.BAD_REQUEST) {
                ErrorResponse errResp = (ErrorResponse) resp.getBody();
                return this.loadAmendIsoPage(email, headerMap, countryCode,
                        Objects.requireNonNull(errResp).errorMessage());
            }

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=admin");
            }

            // forbidden (HTTP 403)
            if (resp.getStatusCode() == HttpStatus.FORBIDDEN) {
                return this.loadLoginPageWhenForbidden(resp);
            }

            // ISO data not found (HTTP 404)
            if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ModelAndView("redirect:/admin/isoData");
            }

            // success (HTTP 200)
            return new ModelAndView("redirect:/admin/isoData");
        } else {
            return new ModelAndView("redirect:/login?from=admin");
        }
    }

    /**
     * Activates the ISO data.
     * If successful, it will go to the ISO data page, the admin login page otherwise.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the ISO data page, the admin login page otherwise
     */
    @PostMapping("/admin/activateIsoData")
    public ModelAndView activateIsoData(HttpSession session, HttpServletRequest request) {
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

            String[] activates = Objects.requireNonNullElse(
                    request.getParameterValues("activate"), new String[]{}); // null if nothing is selected

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            ActivateIsoRequest actualReq = new ActivateIsoRequest(Arrays.stream(activates).toList());
            URI apiEndpoint = URI.create("/api/v1/admin/iso/activate");

            RequestEntity<ActivateIsoRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = isoDataService.activateIsoData(req);
            logger.info("activateIsoData={}", resp.getStatusCode());

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
            return new ModelAndView("redirect:/admin/isoData");
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
