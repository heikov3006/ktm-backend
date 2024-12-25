package at.htl.leonding.dto;

public record UserCreationDTO(String firstname,
                              String lastname,
                              String email,
                              String password,
                              String passwordConfirm) {
}
