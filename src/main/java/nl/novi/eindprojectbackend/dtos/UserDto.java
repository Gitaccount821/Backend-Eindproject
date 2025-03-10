package nl.novi.eindprojectbackend.dtos;

import java.util.Set;

public class UserDto {
    private String username;

    private String password;

    private final String email;

    private final boolean enabled;
    private final String apikey;
    private final Set<String> roles;

    public UserDto(String username, String password, String email, boolean enabled, String apikey, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.apikey = apikey;
        this.roles = roles;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public boolean isEnabled() { return enabled; }
    public String getApikey() { return apikey; }
    public Set<String> getRoles() { return roles; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}
