package at.htl.leonding.dto;

public record BikeUserAndServiceDTO(int bikeServiceHistoryId, int kmAtService, String createdAt, String serviceDate, String updatedAt, int serviceId, int serviceInterval, String serviceTitle) {
}
