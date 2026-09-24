package com.friendbook;

import lombok.Data;

@Data
public class ApiResponseDTO<T> {

	private boolean success;
	private String message;
	private T data;
	private String timestamp;

}
