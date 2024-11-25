package com.customerorder.response;

import lombok.Data;

@Data
public class AuthResponse {
	
	private String jwt;
	private String message;
	
}
