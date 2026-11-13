package com.refrio.platform.shared.interfaces;

import com.refrio.platform.operations.infrastructure.external.ExternalWeatherUnavailableException;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ResourceNotFoundException;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.Instant;
import java.util.*;

/** Consistent, locale-aware API errors. Angular-compatible `message` is preserved. */
@RestControllerAdvice
public class ApiErrorHandler {
    private final MessageSource messages;
    public ApiErrorHandler(MessageSource messages){this.messages=messages;}

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,Object>> notFound(ResourceNotFoundException e,HttpServletRequest request,Locale locale){
        return response(HttpStatus.NOT_FOUND,"RESOURCE_NOT_FOUND","error.notfound",e.getMessage(),request,locale);
    }
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<Map<String,Object>> denied(ForbiddenOperationException e,HttpServletRequest request,Locale locale){
        return response(HttpStatus.FORBIDDEN,"FORBIDDEN","error.forbidden",e.getMessage(),request,locale);
    }
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String,Object>> domain(DomainException e,HttpServletRequest request,Locale locale){
        return response(HttpStatus.BAD_REQUEST,"DOMAIN_VALIDATION","error.domain",e.getMessage(),request,locale);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException e,HttpServletRequest request,Locale locale){
        String detail=e.getBindingResult().getFieldErrors().stream()
             .map(x->x.getField()+": "+x.getDefaultMessage()).findFirst().orElse("Invalid input");
        return response(HttpStatus.BAD_REQUEST,"REQUEST_VALIDATION","error.validation",detail,request,locale);
    }
    @ExceptionHandler({IllegalArgumentException.class,MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String,Object>> illegal(Exception e,HttpServletRequest request,Locale locale){
        return response(HttpStatus.BAD_REQUEST,"INVALID_PARAMETER","error.validation",e.getMessage(),request,locale);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> forbidden(AccessDeniedException e,HttpServletRequest request,Locale locale){
        return response(HttpStatus.FORBIDDEN,"FORBIDDEN","error.forbidden",null,request,locale);
    }
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,Object>> malformed(Exception e,HttpServletRequest request,Locale locale){
        return response(HttpStatus.BAD_REQUEST,"INVALID_JSON","error.json",null,request,locale);
    }
    @ExceptionHandler(ExternalWeatherUnavailableException.class)
    public ResponseEntity<Map<String,Object>> weather(ExternalWeatherUnavailableException e,HttpServletRequest request,Locale locale){
        return response(HttpStatus.SERVICE_UNAVAILABLE,"EXTERNAL_SERVICE_UNAVAILABLE","error.external",null,request,locale);
    }
    private ResponseEntity<Map<String,Object>> response(HttpStatus status,String code,String key,
              String detail,HttpServletRequest request,Locale locale){
        Map<String,Object> data=new LinkedHashMap<>();
        data.put("timestamp",Instant.now().toString());
        data.put("status",status.value());
        data.put("code",code);
        data.put("message",messages.getMessage(key,null,locale));
        if(detail!=null && !detail.isBlank())data.put("detail",detail);
        data.put("path",request.getRequestURI());
        return ResponseEntity.status(status).body(data);
    }
}
