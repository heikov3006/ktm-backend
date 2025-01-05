package at.htl.leonding.dto;

public record UserCreationDTO(String firstName,
                              String lastName,
                              String email,
                              String password,
                              String passwordConfirm) {
}
