package at.htl.leonding.dto;

import at.htl.leonding.model.BikeService;

import java.util.List;

public record StrapiBikeEntryDTO(Long id, String createdAt, String updatedAt, String publishedAt, String model, String company, String modelId, ImageDTO image, String productionYear, List<StrapiGuidanceDTO> guidances) {
}
