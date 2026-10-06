package br.com.mobhillmotos.api.adapter.in;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import br.com.mobhillmotos.api.domain.exception.ImageUploadException;
import br.com.mobhillmotos.api.domain.exception.InvalidCredentialsException;
import br.com.mobhillmotos.api.domain.exception.InvalidImageException;
import br.com.mobhillmotos.api.domain.exception.LoginAlreadyExistsException;
import br.com.mobhillmotos.api.domain.exception.MotoNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MotoNotFoundException.class)
	public ProblemDetail handleNotFound(MotoNotFoundException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleValidation(MethodArgumentNotValidException e) {
		Map<String, String> errors = new LinkedHashMap<>();
		e.getBindingResult().getFieldErrors()
				.forEach(f -> errors.putIfAbsent(f.getField(), f.getDefaultMessage()));

		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Dados inválidos");
		problem.setProperty("errors", errors);
		return problem;
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ProblemDetail handleInvalidCredentials(InvalidCredentialsException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
	}

	@ExceptionHandler(LoginAlreadyExistsException.class)
	public ProblemDetail handleLoginExists(LoginAlreadyExistsException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
	}

	@ExceptionHandler(InvalidImageException.class)
	public ProblemDetail handleInvalidImage(InvalidImageException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ProblemDetail handleUploadTooLarge(MaxUploadSizeExceededException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.CONTENT_TOO_LARGE,
				"Imagem grande demais. O limite é de 10 MB por arquivo.");
	}

	@ExceptionHandler(ImageUploadException.class)
	public ProblemDetail handleUpload(ImageUploadException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, e.getMessage());
	}
}
