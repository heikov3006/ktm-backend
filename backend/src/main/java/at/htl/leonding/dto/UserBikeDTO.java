package at.htl.leonding.dto;

import at.htl.leonding.model.User;

import java.util.List;

public record UserBikeDTO(User user, List<BikeDTO> bikes) {
}
