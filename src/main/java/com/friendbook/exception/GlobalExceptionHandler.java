package com.friendbook.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.friendbook.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(PostNotFoundException.class)
	public ResponseEntity<ErrorResponseDTO> handlePostNotFound(PostNotFoundException ex, HttpServletRequest request) {

		ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
				request.getRequestURI(), LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {

		ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
				request.getRequestURI(), LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(InvalidException.class)
	public ResponseEntity<ErrorResponseDTO> handleInvalidException(InvalidException ex, HttpServletRequest request) {

		ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), ex.getMessage(),
				request.getRequestURI(), LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ErrorResponseDTO> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex,
			HttpServletRequest request) {

		ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), ex.getMessage(),
				request.getRequestURI(), LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponseDTO> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {

		ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), ex.getMessage(),
				request.getRequestURI(), LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}
}
