package nl.novi.eindprojectbackend.models;

import jakarta.validation.constraints.NotEmpty;

public class AuthenticationRequest {

    @NotEmpty(message = "Username may not be empty")
    private String username;

    @NotEmpty(message = "Password may not be empty")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
