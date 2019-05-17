package com.miracle.gateway.controller;

import java.time.Instant;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.miracle.exception.APIExceptionResponse;
import com.miracle.exception.GatewayServiceException;


@ControllerAdvice(basePackages = {"com.miracle.gateway"})
public class GatewayControllerErrorHandler {
@Autowired
private APIExceptionResponse exceptionMessage;
	
	@ExceptionHandler({Exception.class})
	public ResponseEntity<APIExceptionResponse> handle(Exception exception,HttpServletRequest httpServletRequest){
		exceptionMessage.setErrorCode(((GatewayServiceException)exception).getErrorCode(exception, exceptionMessage));
		exceptionMessage.setErrorDescription(exception.getLocalizedMessage());
		exceptionMessage.setTimestamp(Instant.now());
		exceptionMessage.setStackTraceElement(exception.getStackTrace()[0]);
		exceptionMessage.setCompleteTrace(Arrays.toString(exception.getStackTrace()));
		exceptionMessage.setUri(httpServletRequest.getRequestURL());
		return new ResponseEntity<APIExceptionResponse>(exceptionMessage,exceptionMessage.getStatusCode());
	}
}
