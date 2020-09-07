package com.fastjavaframework.listener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author wangshuli
 */
public class SystemSetting {

    private static Map<String, Set<String>> authority;
    private static Map<String, String> authorityByUrl;
    private static Map<String, Set<String>> authorityByRole;

    public static Map<String, Set<String>> authority() {
        if (null == authority) {
            authority = new HashMap<>(0);
        }
        return Collections.unmodifiableMap(authority);
    }

    public static Map<String, String> authorityByUrl() {
        if (null == authorityByUrl) {
            authorityByUrl = new HashMap<>(0);
        }
        return Collections.unmodifiableMap(authorityByUrl);
    }

    public static Map<String, Set<String>> authorityByRole() {
        if (null == authorityByRole) {
            authorityByRole = new HashMap<>(0);
        }
        return Collections.unmodifiableMap(authorityByRole);
    }

    static void putAuthority(String key, Set<String> value) {
        if (null == authority) {
            authority = new HashMap<>();
        }

        Set<String> exist = authority.putIfAbsent(key, value);
        if (null != exist) {
            exist.addAll(value);
            authority.put(key, exist);
        }
    }

    static void putAuthorityByUrl(String key, String value) {
        if (null == authorityByUrl) {
            authorityByUrl = new HashMap<>();
        }

        authorityByUrl.put(key, value);
    }

    static void putAuthorityByRole(String key, Set<String> value) {
        if (null == authorityByRole) {
            authorityByRole = new HashMap<>();
        }

        Set<String> exist = authorityByRole.putIfAbsent(key, value);
        if (null != exist) {
            exist.addAll(value);
            authorityByRole.put(key, exist);
        }
    }
}
