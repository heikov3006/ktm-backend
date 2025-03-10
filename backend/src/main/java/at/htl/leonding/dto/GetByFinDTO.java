package at.htl.leonding.dto;

import at.htl.leonding.model.Bike;
import at.htl.leonding.model.BikeService;
import at.htl.leonding.model.BikeUser;

import java.time.LocalDate;

public record GetByFinDTO(BikeService bikeService, LocalDate serviceDate, int kilometersAtService, LocalDate createdAt) {
}
