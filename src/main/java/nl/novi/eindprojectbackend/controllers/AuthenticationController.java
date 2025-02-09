package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.exceptions.InvalidCredentialsException;
import nl.novi.eindprojectbackend.models.AuthenticationRequest;
import nl.novi.eindprojectbackend.models.AuthenticationResponse;
import nl.novi.eindprojectbackend.services.CustomUserDetailsService;
import nl.novi.eindprojectbackend.utils.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    CustomUserDetailsService customUserDetailsService,
                                    JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());
            String jwt = jwtTokenUtil.generateToken(userDetails);
            return new AuthenticationResponse(jwt);

        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}", request.getUsername(), e);
            throw new InvalidCredentialsException();
        }
    }
}
