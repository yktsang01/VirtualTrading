/*
 * JsonUtilTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.util;

import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * Provides the test cases for <code>JsonUtil</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class JsonUtilTests {

    /**
     * Tests JSON conversion using <code>ErrorResponse</code>.
     *
     * @throws IOException when it is unable to process
     */
    @Test
    public void errorResponseToJson() throws IOException {
        ErrorResponse originalErrResp = new ErrorResponse("Some error");
        String json = JsonUtil.toJson(originalErrResp);
        assertThat(json, containsString("Some error"));
    }

}
