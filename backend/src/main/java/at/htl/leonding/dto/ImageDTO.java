package at.htl.leonding.dto;

public record ImageDTO(Long id, String name, String alternativeText, String caption, String width, String height, Object formats, String hash, String ext, String mime, String size, String url, String previewUrl, String provider, String provider_metadata, String createdAt, String updatedAt) {
}
