package at.htl.leonding.dto;

import at.htl.leonding.model.Bike;

import java.util.List;

public record GetServiceByBikeDTO(Bike bike, List<ServiceByBikeDTO> services) {
}
