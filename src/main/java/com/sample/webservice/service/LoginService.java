package com.sample.webservice.service;

import com.sample.webservice.model.LoginRequest;
import com.sample.webservice.model.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    public LoginResponse getResult(LoginRequest loginRequest) {

        if ( loginRequest.getUsername().equals( "ozan" ) && loginRequest.getPassword().equals( "123456" ) ) {

            return new LoginResponse( "Login Successfully" );

        } else if ( !loginRequest.getUsername().equals( "ozan" ) ) {

            return new LoginResponse( "User could not been found!" );

        } else if ( loginRequest.getUsername().equals( "ozan" ) && !loginRequest.getPassword().equals( "123456" ) ) {

            return new LoginResponse( "Username and password do not match" );

        }

        return new LoginResponse();

    }

}
