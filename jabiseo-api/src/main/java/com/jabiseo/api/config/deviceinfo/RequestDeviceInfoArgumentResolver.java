package com.jabiseo.api.config.deviceinfo;

import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class RequestDeviceInfoArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String DEVICE_ID_HEADER = "X-Device-Id";
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isRequestDeviceInfoAnnotation = parameter.getParameterAnnotation(RequestDeviceInfo.class) != null;
        boolean isDeviceInfoClass = parameter.getParameterType().equals(DeviceInfo.class);
        return isRequestDeviceInfoAnnotation && isDeviceInfoClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String deviceId = webRequest.getHeader(DEVICE_ID_HEADER);

        if(deviceId == null) {
            log.warn("Request device id is empty");
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST_PARAMETER);
        }

        return new DeviceInfo(deviceId);
    }
}
