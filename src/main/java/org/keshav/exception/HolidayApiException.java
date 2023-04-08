package org.keshav.exception;

public class HolidayApiException extends RuntimeException {

    public HolidayApiException(String message, Throwable cause) {
        super(message, cause);
    }
    public HolidayApiException(String message) {
    	super(message);
	}
}