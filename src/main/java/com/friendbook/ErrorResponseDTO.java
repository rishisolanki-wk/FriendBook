package com.friendbook;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponseDTO {

	private int status;
	private String message;
	private String path;
	private LocalDateTime timestamp;
}
