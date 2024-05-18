/*
 * JsonUtil.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.List;

/**
 * Converts Java objects and lists from/to JSON string.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class JsonUtil {

    /**
     * Converts Java object to JSON string.
     *
     * @param obj the Java object to convert
     * @return the JSON string of Java object
     * @throws IOException when problems writing data
     */
    public static String toJson(Object obj)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); //support for Java 8 date and time API
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsString(obj);
    }

    /**
     * Converts JSON string to Java object.
     *
     * @param <T>        the type
     * @param cls        the Java object class
     * @param jsonString the JSON string to convert
     * @return the Java object of the JSON string
     * @throws IOException when problems reading data
     */
    public static <T> T toObject(Class<T> cls, String jsonString)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); //support for Java 8 date and time API
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.readValue(jsonString, cls);
    }

    /**
     * Converts JSON string to Java list.
     *
     * @param cls        the Java object class
     * @param jsonString the JSON string to convert
     * @return the Java List of the JSON string
     * @throws IOException when problems reading data
     */
    public static List<?> toList(Class<?> cls, String jsonString)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); //support for Java 8 date and time API
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, cls);
        return mapper.readValue(jsonString, type);
    }

}
