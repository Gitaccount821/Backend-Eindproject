package nl.novi.eindprojectbackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class UserDto {
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Email(message = "Invalid email format")
    private String email;

    private boolean enabled;
    private String apikey;
    private Set<String> roles;

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
    public void setEmail(String email) { this.email = email; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setApikey(String apikey) { this.apikey = apikey; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
