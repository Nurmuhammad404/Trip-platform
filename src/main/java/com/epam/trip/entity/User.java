package com.epam.trip.entity;

import com.epam.trip.auth.Role;

import java.util.Objects;

public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private Role role;

    public User() {
        super();
        this.role = Role.USER;
    }

    public User(Long id, String username, String email, String password,
            String fullName, String phoneNumber, Role role) {
        super(id);
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = (role != null) ? role : Role.USER;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = (role != null) ? role : Role.USER; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, email);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', email='" + email +
                "', fullName='" + fullName + "', role=" + role + '}';
    }
}
