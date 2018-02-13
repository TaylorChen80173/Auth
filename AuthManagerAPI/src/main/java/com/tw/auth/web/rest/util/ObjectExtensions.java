package com.tw.auth.web.rest.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ObjectExtensions {

    public static List<Map<String, Object>> mapping(List<Object[]> results, Object[][] params) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Object[] result : results) {
            list.add(new HashMap<String, Object>() {{
                for (Object[] param : params) {
                    put((String) param[0], result[(Integer) param[1]]);
                }
            }});
        }
        return list;
    }

    public static List<Map<String, Object>> mapping(List<Object[]> results, Object[] params) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Object[] result : results) {
            list.add(new HashMap<String, Object>() {{
                IntStream.range(0, params.length).forEach(index ->
                    put((String) params[index], result[index])
                );
            }});
        }
        return list;
    }

    public static Map<String, Object> mapping(Object[] results, Object[] params) {
        return new HashMap<String, Object>() {{
            IntStream.range(0, params.length).forEach(index -> put((String) params[index], results[index]));
        }};
    }

    public static Page<Map<String, Object>> mapping(Page<Object[]> results, Object[] params) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Object[] result : results.getContent()) {
            list.add(new HashMap<String, Object>() {{
                IntStream.range(0, params.length).forEach(index ->
                    put((String) params[index], result[index])
                );
            }});
        }
        return new PageImpl(list, new PageRequest(results.getNumber(), results.getSize(), results.getSort()), results.getTotalElements());
    }

    public static List<Map<String, Object>> labelValuePair(List<Object[]> results) {
        return mapping(results, new Object[][]{{"label", 0}, {"value", 1}});
    }

    public static String sqlLike(String source) {
        return source == null ? null : ("%" + source + "%");
    }

    public static String sqlStartsWith(String source) {
        return source == null ? null : (source + "%");
    }

    public static String sqlEndsWith(String source) {
        return source == null ? null : ("%" + source);
    }

    public static <T> T nonNull(@Nullable T source, @Nullable T notNull) {
        if (source == null) {
            if (notNull == null) {
                throw new NullPointerException();
            }
            return notNull;
        }
        return source;
    }
}
