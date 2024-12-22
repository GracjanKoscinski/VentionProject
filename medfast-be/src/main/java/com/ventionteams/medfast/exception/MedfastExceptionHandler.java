package com.ventionteams.medfast.exception;

import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.exception.appointment.AppointmentAlreadyCancelledException;
import com.ventionteams.medfast.exception.appointment.CompletedOrMissedAppointmentException;
import com.ventionteams.medfast.exception.appointment.InvalidAppointmentTimeException;
import com.ventionteams.medfast.exception.appointment.NegativeAppointmentsAmountException;
import com.ventionteams.medfast.exception.appointment.TimeOccupiedException;
import com.ventionteams.medfast.exception.auth.AccountDeactivatedException;
import com.ventionteams.medfast.exception.auth.InvalidVerificationTokenException;
import com.ventionteams.medfast.exception.auth.TermsAndConditionsNotAcceptedException;
import com.ventionteams.medfast.exception.auth.TokenExpiredException;
import com.ventionteams.medfast.exception.auth.UserAlreadyExistsException;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;
import com.ventionteams.medfast.exception.auth.password.InvalidCurrentPasswordException;
import com.ventionteams.medfast.exception.auth.password.PasswordDoesNotMeetRepetitionConstraint;
import com.ventionteams.medfast.exception.auth.password.PermanentPasswordAlreadySetException;
import com.ventionteams.medfast.exception.doctor.OutsideWorkingHoursException;
import com.ventionteams.medfast.exception.location.DoctorLocationMismatchException;
import com.ventionteams.medfast.exception.medicaltestappointment.MissingPdfForMedicalTestException;
import com.ventionteams.medfast.exception.medicaltestappointment.PdfForMedicalTestAlreadyExistsException;
import com.ventionteams.medfast.exception.patient.PatientMismatchException;
import com.ventionteams.medfast.exception.service.ServiceNotProvidedException;
import com.ventionteams.medfast.exception.specialization.SpecializationsNotFoundException;
import com.ventionteams.medfast.exception.userdetails.InvalidExtensionException;
import com.ventionteams.medfast.exception.userdetails.UpdatingNotExistingFieldsException;
import com.ventionteams.medfast.utils.ContextUtils;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Global exception handler.
 */
@Log4j2
@RestControllerAdvice
public class MedfastExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected StandardizedResponse<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex, HandlerMethod handlerMethod) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach((error) -> {
      String fieldName = error.getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    String controllerName = handlerMethod.getMethod().getDeclaringClass()
        .toString().substring(6);
    String methodName = handlerMethod.getMethod().getName();
    log.error("{} occurred in controller {} in method {}(), details: {}",
        ex.getClass().getName(), controllerName, methodName, ex.getMessage());

    return StandardizedResponse.error(
        errors,
        HttpStatus.BAD_REQUEST.value(),
        "Validation failed",
        ex.getClass().getName(),
        "It seems like some of the information you provided is not valid. "
            + "Please check the details you've entered and try again. If you're unsure "
            + "what's wrong, make sure all required fields are filled out correctly."
    );
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  protected StandardizedResponse<Map<String, String>> handleConstraintViolation(
      ConstraintViolationException ex, HandlerMethod handlerMethod) {
    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations().forEach((error) -> {
      String propertyPath = error.getPropertyPath().toString();
      String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
      String errorMessage = error.getMessage();
      errors.put(fieldName, errorMessage);
    });

    String controllerName = handlerMethod.getMethod().getDeclaringClass()
        .toString().substring(6);
    String methodName = handlerMethod.getMethod().getName();
    log.error("{} occurred in controller {} in method {}(), details: {}",
        ex.getClass().getName(), controllerName, methodName, ex.getMessage());

    return StandardizedResponse.error(
        errors,
        HttpStatus.BAD_REQUEST.value(),
        "Validation failed",
        ex.getClass().getName(),
        "It looks like some of the information you provided "
            + "doesn't meet our requirements. Please review your input and ensure "
            + "it follows the necessary guidelines. If you're unsure "
            + "what needs to be fixed, double-check for any missing or incorrect details."
    );
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected StandardizedResponse<?> handleTypeMismatchException(
      MethodArgumentTypeMismatchException ex, HandlerMethod handlerMethod) {
    StandardizedResponse<?> response;

    String controllerName = handlerMethod.getMethod().getDeclaringClass()
        .toString().substring(6);
    String methodName = handlerMethod.getMethod().getName();
    log.error("{} occurred in controller {} in method {}(), details: {}",
        ex.getClass().getName(), controllerName, methodName, ex.getMessage());

    if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
      String validValues = String.join(", ", getEnumValues(ex.getRequiredType()));
      response = StandardizedResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          "Invalid '" + ex.getName() + "' parameter value",
          ex.getClass().getName(),
          String.format("It seems like the information you provided doesn't match "
              + "the expected format. Expected one of: %s", validValues)
      );
    } else {
      response = StandardizedResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          "Invalid parameter",
          ex.getClass().getName(),
          "It seems like the information you provided doesn't match the expected format. "
              + "Invalid value provided for parameter '" + ex.getName() + "'"
      );
    }

    return response;
  }

  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  @ExceptionHandler({InterruptedException.class})
  protected StandardizedResponse<?> handleAsyncExceptions(
      InterruptedException ex, HandlerMethod handlerMethod) {

    return handleException(
        ex,
        handlerMethod,
        HttpStatus.SERVICE_UNAVAILABLE,
        "Something went wrong while processing your request, "
            + "and the operation was unexpectedly interrupted. Please try again. "
            + "If the problem continues, feel free to reach out to our support team for help."
    );
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler({
      PermanentPasswordAlreadySetException.class,
  })
  protected StandardizedResponse<?> handlePermanentPasswordAlreadySetException(
      PermanentPasswordAlreadySetException ex, HandlerMethod handlerMethod) {

    return handleException(
        ex,
        handlerMethod,
        HttpStatus.CONFLICT,
        "Your password has already been permanently set."
    );
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({
      SpecializationsNotFoundException.class,
      MissingPdfForMedicalTestException.class,
      UsernameNotFoundException.class,
  })
  protected StandardizedResponse<?> handleNotFoundException(
      RuntimeException ex, HandlerMethod handlerMethod) {

    return handleException(
        ex,
        handlerMethod,
        HttpStatus.NOT_FOUND,
        "The requested resource could not be found. Please verify your request and try again."
    );
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
      NegativeAppointmentsAmountException.class,
      InvalidCurrentPasswordException.class,
      PasswordDoesNotMeetRepetitionConstraint.class,
      InvalidVerificationTokenException.class,
      IllegalArgumentException.class,
      MissingServletRequestParameterException.class,
      TermsAndConditionsNotAcceptedException.class,
      TokenExpiredException.class,
      UpdatingNotExistingFieldsException.class,
      MaxUploadSizeExceededException.class,
      InvalidExtensionException.class,
      ServiceNotProvidedException.class,
      OutsideWorkingHoursException.class,
      AppointmentAlreadyCancelledException.class,
      DoctorLocationMismatchException.class,
      CompletedOrMissedAppointmentException.class,
      InvalidAppointmentTimeException.class
  })
  protected StandardizedResponse<?> handleBadRequestException(
      RuntimeException ex, HandlerMethod handlerMethod) {

    return handleException(
        ex,
        handlerMethod,
        HttpStatus.BAD_REQUEST,
        "Your request could not be processed due to invalid input. "
            + "Please verify the information and try again."
    );
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler({
      UserAlreadyExistsException.class,
      PdfForMedicalTestAlreadyExistsException.class,
      TimeOccupiedException.class
  })
  protected StandardizedResponse<?> handleConflictException(
      RuntimeException ex, HandlerMethod handlerMethod) {

    return handleException(
        ex,
        handlerMethod,
        HttpStatus.CONFLICT,
        "It seems there is a conflict with the provided information. "
            + "Please verify the details and try again."
    );
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler({BadCredentialsException.class})
  protected StandardizedResponse<?> handleBadCredentialsException(
      BadCredentialsException ex, HandlerMethod handlerMethod) {

    return handleException(
        ex,
        handlerMethod,
        HttpStatus.UNAUTHORIZED,
        "Incorrect email or password. Please double-check your credentials and try again."
    );
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler({
      DisabledException.class,
      CredentialsExpiredException.class
  })
  protected StandardizedResponse<?> handleDisabledException(
      RuntimeException ex, HandlerMethod handlerMethod) {

    return handleException(
        ex,
        handlerMethod,
        HttpStatus.UNAUTHORIZED,
        "Your account is currently disabled or expired. Please check your email."
    );
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler({UserIsAlreadyVerifiedException.class})
  protected StandardizedResponse<?> handleUserIsAlreadyVerifiedException(
      UserIsAlreadyVerifiedException ex, HandlerMethod handlerMethod) {

    return handleException(
        ex,
        handlerMethod,
        HttpStatus.CONFLICT,
        "Your account has already been verified. You can proceed to log in."
    );
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler({
      AccessDeniedException.class,
      PatientMismatchException.class
  })
  protected StandardizedResponse<?> handleAccessDeniedException(
      RuntimeException ex, HandlerMethod handlerMethod) throws IOException {

    return handleException(
        ex,
        handlerMethod,
        HttpStatus.FORBIDDEN,
        "The specified user does not match the required role for this operation. "
            + "Please verify the email and its associated role or contact support for assistance."
    );
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({Exception.class})
  protected StandardizedResponse<?> handleInternalServerError(
      Exception ex, HandlerMethod handlerMethod) {

    return handleException(
        ex,
        handlerMethod,
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Something went wrong on our end. We're working to fix the issue. "
            + "Please try again later, and if the problem persists, contact support for help."
    );
  }

  @ResponseStatus(HttpStatus.LOCKED)
  @ExceptionHandler({AccountDeactivatedException.class})
  protected StandardizedResponse<?> handleAccountDeactivatedException(
      AccountDeactivatedException ex, HandlerMethod handlerMethod) {

    return handleException(
        ex,
        handlerMethod,
        HttpStatus.LOCKED,
        "Your account has been deactivated. "
            + "Please contact the administrator for further information."
    );
  }

  private List<String> getEnumValues(Class<?> enumType) {
    return Arrays.stream(enumType.getEnumConstants())
        .map(Object::toString)
        .toList();
  }

  private StandardizedResponse<?> handleException(
      Exception ex,
      HandlerMethod handlerMethod,
      HttpStatus status,
      String errorMessage) {

    String controllerName = handlerMethod.getMethod().getDeclaringClass()
        .toString().substring(6);
    String methodName = handlerMethod.getMethod().getName();

    log.error("{} occurred in controller {} in method {}() "
            + "for the user with credential {}, details: {}",
        ex.getClass().getName(), controllerName, methodName,
        ContextUtils.getEmailFromContext(), ex.getMessage());

    return StandardizedResponse.error(
        status.value(),
        null,
        ex.getClass().getName(),
        errorMessage
    );
  }
}
