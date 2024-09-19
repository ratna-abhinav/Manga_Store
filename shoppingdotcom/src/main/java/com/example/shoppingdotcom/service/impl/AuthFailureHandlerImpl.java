package com.example.shoppingdotcom.service.impl;

import com.example.shoppingdotcom.model.Users;
import com.example.shoppingdotcom.repository.UserRepository;
import com.example.shoppingdotcom.service.UserService;
import com.example.shoppingdotcom.util.AppConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");
        Users user = userRepository.findByEmail(email);

        if (user.getIsEnable()) {
            if (user.getAccountNonLocked()) {
                if (user.getFailedAttempt() < AppConstants.ATTEMPT_TIME) {
                    userService.increaseFailedAttempt(user);
                } else {
                    userService.userAccountLock(user);
                    exception = new LockedException("Your account is locked !! No of failed attempts = 3");
                }
            } else {
                if (userService.unlockAccountTimeExpired(user)) {
                    exception = new LockedException("Your account is unlocked !! Please try to login again");
                } else {
                    exception = new LockedException("Your account is locked !! Please try after sometime");
                }
            }
        } else {
            exception = new LockedException("Your account is Inactive!");
        }
        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
