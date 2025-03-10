package at.htl.leonding.dto;

import at.htl.leonding.model.Bike;

import java.util.List;

public record GetAllByFinDTO(String fin, Bike bike, List<GetByFinDTO> getByFinDTOList) {
}
