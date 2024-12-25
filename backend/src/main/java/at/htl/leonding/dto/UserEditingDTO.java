package at.htl.leonding.dto;

import at.htl.leonding.model.User;

public record UserEditingDTO(String email, String password, User user) {
}
