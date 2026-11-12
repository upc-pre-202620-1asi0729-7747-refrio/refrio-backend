package com.refrio.platform.operations.infrastructure.external;
public class ExternalWeatherUnavailableException extends RuntimeException {
    public ExternalWeatherUnavailableException(String message){super(message);}
    public ExternalWeatherUnavailableException(String message, Throwable cause){super(message,cause);}
}
