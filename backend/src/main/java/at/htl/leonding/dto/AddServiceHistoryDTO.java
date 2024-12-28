package at.htl.leonding.dto;

import at.htl.leonding.model.User;

public record AddServiceHistoryDTO(String email, Long bikeId, Long serviceId, int km) {
}
