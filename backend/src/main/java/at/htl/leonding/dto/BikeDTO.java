package at.htl.leonding.dto;

import at.htl.leonding.model.Bike;

public record BikeDTO(Bike bike, Long km, String fin, String imgUrl) {
}
