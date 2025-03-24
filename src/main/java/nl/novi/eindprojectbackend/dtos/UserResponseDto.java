package nl.novi.eindprojectbackend.dtos;

import java.util.Set;

public class UserResponseDto {

    private String username;
    private String email;
    private boolean enabled;
    private String apikey;
    private Set<String> roles;

    public UserResponseDto() {
    }

    public UserResponseDto(String username, String email, boolean enabled, String apikey, Set<String> roles) {
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.apikey = apikey;
        this.roles = roles;
    }

    public String getUsername() { return username; }

    public String getEmail() { return email; }

    public boolean isEnabled() { return enabled; }

    public String getApikey() { return apikey; }

    public Set<String> getRoles() { return roles; }
}
