package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.dto.request.PatientRegistrationRequest;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.dto.response.SignInResponse;
import com.ventionteams.medfast.dto.response.userinfo.ContactInfoResponse;
import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.UserStatus;
import com.ventionteams.medfast.exception.auth.AccountDeactivatedException;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;
import com.ventionteams.medfast.service.UserService;
import jakarta.mail.MessagingException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication service is responsible for signing up and signing in users. It can also send a
 * verification email to the user.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserService userService;
  private final EmailVerificationService emailVerificationService;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final RefreshTokenService refreshTokenService;
  private final AuthenticationManager authenticationManager;
  private final VerificationTokenService verificationTokenService;
  private final TokenConfig tokenConfig;

  /**
   * Sign up a user and send a verification email to the user's email.
   */
  @Transactional(rollbackFor = {MessagingException.class})
  public String signUp(PatientRegistrationRequest request)
      throws MessagingException {
    request.setPassword(passwordEncoder.encode(request.getPassword()));
    User user = userService.create(request);
    log.info("Accepted sign up request for user with email {}", user.getEmail());
    sendVerificationEmail(user.getEmail());
    return "Email verification link has been sent to your email";
  }

  /**
   * Changes user email, disables user and sends a verification email to the new email.
   */
  @Transactional(rollbackFor = {MessagingException.class, IOException.class})
  public ContactInfoResponse changeUserEmail(User user, String email)
      throws MessagingException {
    verificationTokenService.deleteVerificationToken(user.getEmail());
    user.setEmail(email);
    log.info("Attempt to update email for the user with id {}", user.getId());
    user.setEnabled(false);
    userService.save(user);
    sendVerificationEmail(user.getEmail());
    return userService.getContactInfo(user);

  }

  /**
   * Send a verification email to the user's email.
   */
  public void sendVerificationEmail(String email) throws MessagingException {
    User user = userService.findByEmail(email);
    if (user.isEnabled()) {
      throw new UserIsAlreadyVerifiedException(email);
    }
    verificationTokenService.addVerificationTokenForUser(user.getEmail());

    emailVerificationService.sendUserVerificationEmail(user);
  }

  /**
   * Sign in a user and return a JWT and a refresh token.
   */
  public SignInResponse signIn(SignInRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        ));
    User userDetails = (User) userService
        .getUserDetailsService()
        .loadUserByUsername(request.getEmail());

    if (userDetails.getUserStatus().equals(UserStatus.DEACTIVATED)
        || userDetails.getUserStatus().equals(UserStatus.DELETED)) {
      throw new AccountDeactivatedException();
    }
    log.info("Accepted sign in request for user with email {}", request.getEmail());

    String jwt = jwtService.generateToken(userDetails);
    RefreshToken refreshToken = refreshTokenService.generateToken(userDetails);

    return SignInResponse.builder()
        .accessToken(jwt)
        .refreshToken(refreshToken.getToken())
        .expiresIn(tokenConfig.timeout().access())
        .refreshExpiresIn(tokenConfig.timeout().refresh())
        .role(userDetails.getRole())
        .build();
  }
}
