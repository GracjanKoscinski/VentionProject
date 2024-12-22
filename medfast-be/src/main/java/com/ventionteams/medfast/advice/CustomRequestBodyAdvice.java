package com.ventionteams.medfast.advice;

import com.ventionteams.medfast.dto.request.EmailRequest;
import com.ventionteams.medfast.dto.request.PatientRegistrationRequest;
import com.ventionteams.medfast.dto.request.SetPermanentPasswordRequest;
import com.ventionteams.medfast.dto.request.SignUpRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

/**
 * Intercepts requests to set an data in contextHolder.
 */
@ControllerAdvice
public class CustomRequestBodyAdvice implements RequestBodyAdvice {

  @Override
  public boolean supports(MethodParameter methodParameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return SignUpRequest.class.isAssignableFrom(methodParameter.getParameterType())
        || PatientRegistrationRequest.class.isAssignableFrom(methodParameter.getParameterType())
        || SetPermanentPasswordRequest.class.isAssignableFrom(methodParameter.getParameterType());
  }

  @Override
  public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
      Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
    return inputMessage;
  }

  @Override
  public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
      Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
    if (body instanceof EmailRequest emailRequest) {
      RequestContextHolder.getRequestAttributes().setAttribute(
          "email", emailRequest.getEmail(), RequestAttributes.SCOPE_REQUEST);
    }
    return body;
  }

  @Override
  public Object handleEmptyBody(Object body, HttpInputMessage inputMessage,
      MethodParameter parameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }
}
