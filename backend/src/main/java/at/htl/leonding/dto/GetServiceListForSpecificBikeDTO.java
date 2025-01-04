package at.htl.leonding.dto;

import at.htl.leonding.model.BikeService;

import java.util.List;

public record GetServiceListForSpecificBikeDTO(List<BikeService> services, List<BikeServiceDTO> bikeServices) {
}
