package com.rackspace.salus.telemetry.api.web;

import com.rackspace.salus.common.errors.ResponseMessages;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice(basePackages = "com.rackspace.salus.telemetry.api.web")
@ResponseBody
public class RestExceptionHandler extends com.rackspace.salus.common.web.AbstractRestExceptionHandler {
  @Autowired
  public RestExceptionHandler(ErrorAttributes errorAttributes) {
      super(errorAttributes);
  }

  @ExceptionHandler({ResourceAccessException.class})
  public ResponseEntity<?> handleBadRequest(
      HttpServletRequest request) {
    return respondWith(request, HttpStatus.BAD_GATEWAY, ResponseMessages.apiResourceAccessException);
  }

}
