package com.github.itroadlabs.oas.apicross.springmvc.params;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// See also https://github.com/spring-projects/spring-framework/issues/23618
public class SupportParamNameBindingProcessor extends ServletModelAttributeMethodProcessor {

    private final Map<Class<?>, Map<String, String>> renameCache = new ConcurrentHashMap<>();
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    public SupportParamNameBindingProcessor(boolean annotationNotRequired, RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        super(annotationNotRequired);
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameter().getType().getAnnotation(ParamNameBindingSupport.class) != null;
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest nativeWebRequest) {
        Object target = binder.getTarget();
        WebDataBinder targetBinder = binder;
        if (target != null) {
            Class<?> targetClass = target.getClass();
            if (targetClass.isAnnotationPresent(ParamNameBindingSupport.class)) {
                Map<String, String> mapping = renameCache.computeIfAbsent(targetClass, this::analyzeClass);
                targetBinder = new ParamNameDataBinder(target, binder.getObjectName(), mapping);
                WebBindingInitializer webBindingInitializer = requestMappingHandlerAdapter.getWebBindingInitializer();
                if (webBindingInitializer != null) {
                    webBindingInitializer.initBinder(targetBinder/*, nativeWebRequest*/);
                }
            }
        }
        super.bindRequestParameters(targetBinder, nativeWebRequest);
    }

    private Map<String, String> analyzeClass(Class<?> targetClass) {
        Field[] fields = targetClass.getDeclaredFields();
        Map<String, String> renameMap = new HashMap<>();
        for (Field field : fields) {
            ParamName paramNameAnnotation = field.getAnnotation(ParamName.class);
            if (paramNameAnnotation != null && !paramNameAnnotation.value().isEmpty()) {
                renameMap.put(paramNameAnnotation.value(), field.getName());
            }
        }
        if (renameMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return renameMap;
    }
}
