package com.furious.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author furious 2024/4/29
 */
public class ProviderServiceRegistry {

    public static ProviderServiceRegistry INSTANCE = new ProviderServiceRegistry();

    private final Map<String, Object> map = new HashMap<>();

    public void register(Class<?> clazz, Object object) {
        map.put(clazz.getName(), object);
    }

    public <T> T getObject(String className) {
        return (T) map.get(className);
    }

}
