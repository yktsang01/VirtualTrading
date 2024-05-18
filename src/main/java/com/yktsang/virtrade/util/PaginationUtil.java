/*
 * PaginationUtil.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * Provides the pagination utility functions.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class PaginationUtil {

    /**
     * Returns the response headers with pagination support information.
     *
     * @param totalRecordCount the total record count
     * @param totalPageCount   the total page count
     * @param page             the current page
     * @param pageSize         the page size
     * @param hasPrev          the has previous indicator
     * @param hasNext          the has next indicator
     * @param currency         the currency
     * @return the response headers
     */
    public static HttpHeaders populateResponseHeader(long totalRecordCount, int totalPageCount, int page,
                                                     int pageSize, boolean hasPrev, boolean hasNext, String currency) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("totalRecordCount", Long.toString(totalRecordCount));
        headers.add("totalPageCount", Integer.toString(totalPageCount));
        headers.add("hasPrev", Boolean.toString(hasPrev));
        headers.add("hasNext", Boolean.toString(hasNext));
        String ccyStr = !currency.isEmpty() ? "ccy=" + currency + "&" : "";
        headers.add("first", ccyStr + "page=1&pageSize=" + pageSize);
        headers.add("prev", ccyStr + "page=" + (Math.max(page - 1, 1)) + "&pageSize=" + pageSize);
        headers.add("next", ccyStr + "page=" + (Math.min(page + 1, totalPageCount)) + "&pageSize=" + pageSize);
        headers.add("last", ccyStr + "page=" + totalPageCount + "&pageSize=" + pageSize);
        return headers;
    }

    /**
     * Returns the response headers specifically for watch list with pagination support information.
     *
     * @param totalRecordCount the total record count
     * @param totalPageCount   the total page count
     * @param page             the current page
     * @param pageSize         the page size
     * @param hasPrev          the has previous indicator
     * @param hasNext          the has next indicator
     * @param currency         the currency
     * @return the response headers
     */
    public static HttpHeaders populateWatchListResponseHeader(long totalRecordCount, int totalPageCount, int page,
                                                              int pageSize, boolean hasPrev, boolean hasNext, String currency) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("totalRecordCount", Long.toString(totalRecordCount));
        headers.add("totalPageCount", Integer.toString(totalPageCount));
        headers.add("hasPrev", Boolean.toString(hasPrev));
        headers.add("hasNext", Boolean.toString(hasNext));
        String ccyStr = !currency.isEmpty() ? "ccy=" + currency + "&" : "";
        headers.add("first", ccyStr + "wlPage=1&wlPageSize=" + pageSize);
        headers.add("prev", ccyStr + "wlPage=" + (Math.max(page - 1, 1)) + "&wlPageSize=" + pageSize);
        headers.add("next", ccyStr + "wlPage=" + (Math.min(page + 1, totalPageCount)) + "&wlPageSize=" + pageSize);
        headers.add("last", ccyStr + "wlPage=" + totalPageCount + "&wlPageSize=" + pageSize);
        headers.add("current", ccyStr + "wlPage=" + page + "&wlPageSize=" + pageSize);
        return headers;
    }

    /**
     * Returns the response headers specifically for search result with pagination support information.
     *
     * @param totalRecordCount the total record count
     * @param totalPageCount   the total page count
     * @param page             the current page
     * @param pageSize         the page size
     * @param hasPrev          the has previous indicator
     * @param hasNext          the has next indicator
     * @return the response headers
     */
    public static HttpHeaders populateSearchResultResponseHeader(long totalRecordCount, int totalPageCount, int page,
                                                                 int pageSize, boolean hasPrev, boolean hasNext) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("totalRecordCount", Long.toString(totalRecordCount));
        headers.add("totalPageCount", Integer.toString(totalPageCount));
        headers.add("hasPrev", Boolean.toString(hasPrev));
        headers.add("hasNext", Boolean.toString(hasNext));
        headers.add("first", "srPage=1&srPageSize=" + pageSize);
        headers.add("prev", "srPage=" + (Math.max(page - 1, 1)) + "&srPageSize=" + pageSize);
        headers.add("next", "srPage=" + (Math.min(page + 1, totalPageCount)) + "&srPageSize=" + pageSize);
        headers.add("last", "srPage=" + totalPageCount + "&srPageSize=" + pageSize);
        headers.add("current", "srPage=" + page + "&srPageSize=" + pageSize);
        return headers;
    }

    /**
     * Converts the list of data to the page of data.
     *
     * @param originalData the original list of data
     * @param currentPage  the current page
     * @param pageSize     the page size
     * @return the page of data
     */
    public static Page<?> convertListToPage(List<?> originalData, int currentPage, int pageSize) {
        // currentPage = 0 means all data in single page
        // pageable page starts at 0, pageSize cannot be 0
        Pageable pageable = PageRequest.of(currentPage == 0 ? 0 : currentPage - 1, pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), originalData.size());
        return new PageImpl<>(originalData.subList(start, end), pageable, originalData.size());
    }

}
