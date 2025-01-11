package at.htl.leonding.dto;

import java.util.List;

public record StrapiGuidanceEntryDTO(Long id, String title, String tools, String description, String createdAt, String updatedAt, String publishedAt, String conclusion, Object video, Object headerImg, List<Object> Steps, int interval, List<StrapiBikeDTO> bikes) {
}
