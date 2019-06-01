package com.piotics.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class TokenException extends RuntimeException{

	
	public TokenException(String msg) {
        super(msg);
    }
}
