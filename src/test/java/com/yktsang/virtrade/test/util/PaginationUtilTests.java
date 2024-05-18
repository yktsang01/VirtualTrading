/*
 * PaginationUtilTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.util;

import com.yktsang.virtrade.entity.OutstandingTradingTransaction;
import com.yktsang.virtrade.util.PaginationUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Provides the test cases for <code>PaginationUtil</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class PaginationUtilTests {

    /**
     * Tests converting list to page and corresponding response header.
     */
    @Test
    public void listToPageThenResponseHeader() {
        // original data
        Set<OutstandingTradingTransaction> osTxnsSet = new HashSet<>();
        for (int i = 0; i < 33; i++) {
            osTxnsSet.add(new OutstandingTradingTransaction("SYM" + i, "NAME", "CCY"));
        }
        System.out.println("number of elements in original data=" + osTxnsSet.size());
        // convert to list
        List<OutstandingTradingTransaction> osTxnsList = osTxnsSet.stream().toList();
        System.out.println("number of elements in converted data=" + osTxnsList.size());

        // from user
        int inputPage = 1;
        int inputPageSize = 5;

        int totalPages = osTxnsList.size() / inputPageSize;
        int remainder = osTxnsList.size() % inputPageSize;
        if (remainder != 0) {
            totalPages++;
        }
        System.out.println("calculated totalPages=" + totalPages);
        System.out.println("calculated remainder=" + remainder);
        System.out.println();

        for (int i = inputPage; i <= totalPages; i++) {
            System.out.println("input page=" + i);
            Page<OutstandingTradingTransaction> desiredOutput =
                    (Page<OutstandingTradingTransaction>) PaginationUtil.convertListToPage(osTxnsList, i, Math.max(inputPageSize, 1));
            assertEquals(33L, desiredOutput.getTotalElements());
            assertEquals(7, desiredOutput.getTotalPages());
            assertEquals(i - 1, desiredOutput.getPageable().getPageNumber());
            assertEquals(5, desiredOutput.getPageable().getPageSize());
            if (i == 1) {
                assertFalse(desiredOutput.hasPrevious());
            } else {
                assertTrue(desiredOutput.hasPrevious());
            }
            if (i == totalPages) {
                assertFalse(desiredOutput.hasNext());
                assertEquals(3, desiredOutput.getNumberOfElements());
            } else {
                assertTrue(desiredOutput.hasNext());
                assertEquals(5, desiredOutput.getNumberOfElements());
            }

            HttpHeaders headers = PaginationUtil.populateResponseHeader(desiredOutput.getTotalElements(), desiredOutput.getTotalPages(),
                    i, desiredOutput.getPageable().getPageSize(), desiredOutput.hasPrevious(), desiredOutput.hasNext(), "");
            Map<String, String> respHeaderMap = headers.toSingleValueMap();
            String first = respHeaderMap.get("first");
            String prev = respHeaderMap.get("prev");
            String next = respHeaderMap.get("next");
            String last = respHeaderMap.get("last");
            long totalRecordCount = Objects.isNull(respHeaderMap.get("totalRecordCount")) ? 0L : Long.parseLong(respHeaderMap.get("totalRecordCount"));
            int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
            boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
            boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
            assertEquals(totalRecordCount, desiredOutput.getTotalElements());
            assertEquals(totalPageCount, desiredOutput.getTotalPages());
            assertEquals(hasPrev, desiredOutput.hasPrevious());
            assertEquals(hasNext, desiredOutput.hasNext());
            System.out.println("first=>" + first);
            System.out.println("prev=>" + prev);
            System.out.println("next=>" + next);
            System.out.println("last=>" + last);
            System.out.println();
        }
    }

}
