package com.leo.coordi.controller

import com.leo.coordi.service.ErrorResponse
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.ConstraintViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class ControllerExceptionHandler {
    private val log: Logger = LoggerFactory.getLogger(ControllerExceptionHandler::class.java)

    // ----------------------------------------------------------------------------
    // --- 4xx Client Error (로그를 남기지 않는다)  
    // ----------------------------------------------------------------------------

    // 요청 argument 적절하지 않아 예외가 발생한 경우
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ErrorResponse {
        return ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.message ?: HttpStatus.BAD_REQUEST.reasonPhrase)
    }

    // 요청 DTO 가 @Valid 유효성 체크 실패했을 경우
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ErrorResponse {
        // 오류 메시지 생성
        val errorMessages =
            ex.bindingResult.allErrors.map { "[${(it as FieldError).field}=${it.rejectedValue}] ${it.defaultMessage}" }
        val errorMessage = errorMessages.joinToString(separator = " ")
        return ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage)
    }

    @ExceptionHandler(
        value = [
            ConstraintViolationException::class, // 쿼리파라미터의 유효성 체크 실패 
            EntityNotFoundException::class,      // 엔티티 조회 실패
            NoResourceFoundException::class,     // 존재하지 않는 API 호출 
        ]
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle4xxException(ex: Exception): ErrorResponse {
        return ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "${ex.javaClass.simpleName}: ${ex.message ?: "Client Error"}"
        )
    }

    // ----------------------------------------------------------------------------
    // --- 5xx Server Error ---
    // ----------------------------------------------------------------------------

    // check 등으로 인한 내부 상태 오류 
    @ExceptionHandler(IllegalStateException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleIllegalStateException(ex: IllegalStateException): ErrorResponse {
        // 에러로그 남김
        log.error(ex.message ?: HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, ex)
        return ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.message ?: HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase
        )
    }

    // 명시적으로 예외 핸들러 설정을 하지 않은 예외는 500 에러코드와, 예외클래스명 + 에러 메세지를 API 결과로 전달한다.  
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleUnknownException(ex: Exception): ErrorResponse {
        // 에러로그 남김
        log.error(ex.message ?: "Unknown Internal Server Error", ex)
        return ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "${ex.javaClass.simpleName}: ${ex.message ?: "Unknown Internal Server Error"}"
        )
    }
}