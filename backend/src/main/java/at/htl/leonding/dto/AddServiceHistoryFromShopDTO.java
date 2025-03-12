package at.htl.leonding.dto;

import java.time.LocalDate;

public record AddServiceHistoryFromShopDTO(Long bike, Long service, String fin, int km) {
}
