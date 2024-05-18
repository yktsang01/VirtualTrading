/*
 * IsoDataServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.IsoCurrency;
import com.yktsang.virtrade.entity.IsoData;
import com.yktsang.virtrade.entity.IsoDataRepository;
import com.yktsang.virtrade.request.ActivateIsoRequest;
import com.yktsang.virtrade.request.CreateIsoRequest;
import com.yktsang.virtrade.request.DeactivateIsoRequest;
import com.yktsang.virtrade.request.UpdateIsoRequest;
import com.yktsang.virtrade.response.*;
import com.yktsang.virtrade.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The API controller for implementing <code>IsoDataService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class IsoDataServiceController implements IsoDataService {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(IsoDataServiceController.class);
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;
    /**
     * The account service.
     */
    @Autowired
    private AccountService acctService;
    /**
     * The ISO data repository.
     */
    @Autowired
    private IsoDataRepository isoDataRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> activeCurrencies(RequestEntity<Void> req) {
        Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        if (activeCurrencies.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new IsoCurrencyResponse(activeCurrencies));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> isoData(RequestEntity<Void> req, int page, int pageSize) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (acctService.hasAdminRight(tokenUser)) {
            GenericHolder holder = this.getIsoDataResults(page, Math.max(pageSize, 1));
            List<IsoCode> isoCodes = (List<IsoCode>) holder.items();
            HttpHeaders respHeaderMap = holder.headers();

            if (isoCodes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                        .body(new IsoDataResponse(isoCodes));
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("No admin access for account"));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>IsoCode</code>>.
     *
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the GenericHolder
     */
    private GenericHolder getIsoDataResults(int page, int pageSize) {
        List<IsoData> isoCodes = isoDataRepo.findAll(Sort.by(Sort.Direction.ASC, "countryAlpha2Code"));
        List<IsoCode> respIsoData = isoCodes.stream()
                //map to response format
                .map(i -> new IsoCode(i.getCountryAlpha2Code(), i.getCountryName(),
                        i.getCurrencyAlphaCode(), i.getCurrencyName(), i.getCurrencyMinorUnits(),
                        i.isActive(),
                        i.getActivationDateTime(), i.getActivatedBy(),
                        i.getDeactivationDateTime(), i.getDeactivatedBy()))
                .toList();

        Page<IsoCode> respPage;
        if (page == 0) {
            respPage = (Page<IsoCode>)
                    PaginationUtil.convertListToPage(respIsoData, page, respIsoData.isEmpty() ? 1 : respIsoData.size());
        } else {
            respPage = (Page<IsoCode>)
                    PaginationUtil.convertListToPage(respIsoData, page, pageSize);
        }
        HttpHeaders respHeaderMap = PaginationUtil.populateResponseHeader(respPage.getTotalElements(),
                respPage.getTotalPages(), page, respPage.getPageable().getPageSize(),
                respPage.hasPrevious(), respPage.hasNext(), "");

        return new GenericHolder(respPage.getContent(), respHeaderMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> isoData(RequestEntity<Void> req, String countryCode) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (acctService.hasAdminRight(tokenUser)) {
            if (Objects.isNull(countryCode)
                    || countryCode.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            Optional<IsoData> isoDataOpt = isoDataRepo.findById(countryCode);
            if (isoDataOpt.isPresent()) {
                IsoData data = isoDataOpt.get();
                IsoCode isoCode = new IsoCode(data.getCountryAlpha2Code(), data.getCountryName(),
                        data.getCurrencyAlphaCode(), data.getCurrencyName(), data.getCurrencyMinorUnits(),
                        data.isActive(),
                        data.getActivationDateTime(), data.getActivatedBy(),
                        data.getDeactivationDateTime(), data.getDeactivatedBy());
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new IsoCodeResponse(isoCode));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("ISO data not found"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("No admin access for account"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> createIsoData(RequestEntity<CreateIsoRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (acctService.hasAdminRight(tokenUser)) {
            if (req.hasBody() && Objects.nonNull(req.getBody())) {
                CreateIsoRequest actualReq = req.getBody();

                if (
                        Objects.isNull(actualReq.countryCode())
                                || actualReq.countryCode().length() != 2 // countryCode not empty
                                || Objects.isNull(actualReq.countryName())
                                || actualReq.countryName().isEmpty()
                                || Objects.isNull(actualReq.currencyCode())
                                || actualReq.currencyCode().length() != 3 // currencyCode not empty
                                || Objects.isNull(actualReq.currencyName())
                                || actualReq.currencyName().isEmpty()
                                || Objects.isNull(actualReq.activate())

                                // if currencyMinorUnits is not null then check value=0,2,3,4
                                || (Objects.nonNull(actualReq.currencyMinorUnits())
                                && (actualReq.currencyMinorUnits() < 0
                                || actualReq.currencyMinorUnits() > 4
                                || actualReq.currencyMinorUnits() == 1))
                ) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponse("Validation failed"));
                }

                // insert ISO data
                IsoData isoData = new IsoData(actualReq.countryCode(), actualReq.countryName(),
                        actualReq.currencyCode(), actualReq.currencyName(), actualReq.currencyMinorUnits(),
                        actualReq.activate(), tokenUser);
                isoDataRepo.save(isoData);
                logger.info("ISO data created");

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new SuccessResponse("ISO data created"));

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid request"));

            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("No admin access for account"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> updateIsoData(RequestEntity<UpdateIsoRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (acctService.hasAdminRight(tokenUser)) {
            if (req.hasBody() && Objects.nonNull(req.getBody())) {
                UpdateIsoRequest actualReq = req.getBody();

                if (
                        Objects.isNull(actualReq.countryCode())
                                || actualReq.countryCode().length() != 2 // countryCode not empty
                                || Objects.isNull(actualReq.countryName())
                                || actualReq.countryName().isEmpty()
                                || Objects.isNull(actualReq.currencyCode())
                                || actualReq.currencyCode().length() != 3 // currencyCode not empty
                                || Objects.isNull(actualReq.currencyName())
                                || actualReq.currencyName().isEmpty()
                                || Objects.isNull(actualReq.deactivate())

                                // if currencyMinorUnits is not null then check value=0,2,3,4
                                || (Objects.nonNull(actualReq.currencyMinorUnits())
                                && (actualReq.currencyMinorUnits() < 0
                                || actualReq.currencyMinorUnits() > 4
                                || actualReq.currencyMinorUnits() == 1))
                ) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponse("Validation failed"));
                }

                String countryCode = actualReq.countryCode();
                String countryName = actualReq.countryName();
                String currencyCode = actualReq.currencyCode();
                String currencyName = actualReq.currencyName();
                Integer currencyMinorUnits = actualReq.currencyMinorUnits();
                boolean wantToDeactivate = actualReq.deactivate();

                Optional<IsoData> dataOpt = isoDataRepo.findById(countryCode);
                if (dataOpt.isPresent()) {
                    IsoData isoData = dataOpt.get();
                    boolean currentlyActive = isoData.isActive();
                    // update ISO data
                    isoData.setCountryName(countryName);
                    isoData.setCurrencyAlphaCode(currencyCode);
                    isoData.setCurrencyName(currencyName);
                    isoData.setCurrencyMinorUnits(currencyMinorUnits);
                    // only deactivate once
                    if (currentlyActive && wantToDeactivate) {
                        isoData.setActive(false);
                        isoData.setActivatedBy(null);
                        isoData.setActivationDateTime(null);
                        isoData.setDeactivatedBy(tokenUser);
                        isoData.setDeactivationDateTime(LocalDateTime.now());
                        logger.info("ISO data deactivated");
                    }
                    isoData.setLastUpdatedBy(tokenUser);
                    isoData.setLastUpdatedDateTime(LocalDateTime.now());
                    isoDataRepo.save(isoData);
                    logger.info("ISO data updated");

                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new SuccessResponse("ISO data updated"));

                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse("ISO data not found"));
                }

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid request"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("No admin access for account"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> activateIsoData(RequestEntity<ActivateIsoRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (acctService.hasAdminRight(tokenUser)) {
            if (req.hasBody() && Objects.nonNull(req.getBody())) {
                ActivateIsoRequest actualReq = req.getBody();

                if (Objects.isNull(actualReq.countryCodesToActivate())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponse("Validation failed"));
                }

                List<String> codesToActivate = actualReq.countryCodesToActivate();

                List<IsoData> isoCodes = isoDataRepo.findAll(Sort.by(Sort.Direction.ASC, "countryAlpha2Code"));

                if (!codesToActivate.isEmpty()) {
                    List<IsoData> isoDataToActivate = isoCodes.stream()
                            .filter(code -> codesToActivate.contains(code.getCountryAlpha2Code()))
                            .filter(code -> !code.isActive()) // activate only once
                            .toList();
                    logger.info("something to activate? {}", isoDataToActivate.size());

                    for (IsoData iso : isoDataToActivate) {
                        iso.setActive(true);
                        iso.setActivatedBy(tokenUser);
                        iso.setActivationDateTime(LocalDateTime.now());
                        iso.setDeactivatedBy(null);
                        iso.setDeactivationDateTime(null);
                        iso.setLastUpdatedBy(tokenUser);
                        iso.setLastUpdatedDateTime(LocalDateTime.now());
                        isoDataRepo.save(iso);
                        logger.info("activated {}", iso.getCountryAlpha2Code());
                    }

                    if (isoDataToActivate.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                    } else {
                        return ResponseEntity.status(HttpStatus.OK)
                                .body(new SuccessResponse("Successfully activated "
                                        + isoDataToActivate.size()));
                    }

                } else {
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                }

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid request"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("No admin access for account"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> deactivateIsoData(RequestEntity<DeactivateIsoRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (acctService.hasAdminRight(tokenUser)) {
            if (req.hasBody() && Objects.nonNull(req.getBody())) {
                DeactivateIsoRequest actualReq = req.getBody();

                if (Objects.isNull(actualReq.countryCodesToDeactivate())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponse("Validation failed"));
                }

                List<String> codesToDeactivate = actualReq.countryCodesToDeactivate();

                List<IsoData> isoCodes = isoDataRepo.findAll(Sort.by(Sort.Direction.ASC, "countryAlpha2Code"));

                if (!codesToDeactivate.isEmpty()) {
                    List<IsoData> isoDataToDeactivate = isoCodes.stream()
                            .filter(code -> codesToDeactivate.contains(code.getCountryAlpha2Code()))
                            .filter(IsoData::isActive) // deactivate only once
                            .toList();
                    logger.info("something to deactivate? {}", isoDataToDeactivate.size());

                    for (IsoData iso : isoDataToDeactivate) {
                        iso.setActive(false);
                        iso.setActivatedBy(null);
                        iso.setActivationDateTime(null);
                        iso.setDeactivatedBy(tokenUser);
                        iso.setDeactivationDateTime(LocalDateTime.now());
                        iso.setLastUpdatedBy(tokenUser);
                        iso.setLastUpdatedDateTime(LocalDateTime.now());
                        isoDataRepo.save(iso);
                        logger.info("deactivated {}", iso.getCountryAlpha2Code());
                    }

                    if (isoDataToDeactivate.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                    } else {
                        return ResponseEntity.status(HttpStatus.OK)
                                .body(new SuccessResponse("Successfully deactivated "
                                        + isoDataToDeactivate.size()));
                    }

                } else {
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid request"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("No admin access for account"));
        }
    }

}
