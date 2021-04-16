package com.github.itroadlabs.oas.apicross.utils;

import com.github.itroadlabs.oas.apicross.CodeGeneratorException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class PluginsHelper {
    public static  <T> T instantiatePlugin(String className, Supplier<T> defaultInstance) {
        if (className != null) {
            try {
                return (T) Class.forName(className).newInstance();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new CodeGeneratorException(e);
            }
        } else {
            return defaultInstance.get();
        }
    }
}
