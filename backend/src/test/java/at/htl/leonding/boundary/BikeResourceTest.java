package at.htl.leonding.boundary;

import at.htl.leonding.dto.AddServiceHistoryDTO;
import at.htl.leonding.dto.BikeHistoryDTO;
import at.htl.leonding.dto.BikeUserAndServiceDTO;
import at.htl.leonding.model.*;
import at.htl.leonding.repository.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class BikeResourceTest {

    @InjectMock
    BikeserviceHistoryRepository bshrepo;

    @InjectMock
    BikeServiceRepository bsrepo;

    @InjectMock
    UserBikeRepository ubrepo;

    @InjectMock
    BikeRepository bikeRepository;

    @Test
    void testGetBikeserviceHistory() {
        BikeHistoryDTO bikeHistoryDTO = new BikeHistoryDTO("user@example.com", "FIN123", 1L);
        List<BikeUserAndServiceDTO> mockHistory = List.of(
                new BikeUserAndServiceDTO(1, 1000, "2024-01-01", "2024-01-10", "2024-01-15", 1, 5000, "Service1")
        );

        when(bshrepo.findByBikeUserAndService("user@example.com", "FIN123", 1L)).thenReturn(mockHistory);

        given()
                .contentType("application/json")
                .body(bikeHistoryDTO)
                .when()
                .get("/maintenance/getBikeserviceHistory")
                .then()
                .statusCode(200)
                .body("$.size()", is(1));
    }

    @Test
    void testGetLastBikeserviceHistory_Success() {
        BikeHistoryDTO bikeHistoryDTO = new BikeHistoryDTO("user@example.com", "FIN123", 1L);
        List<BikeUserAndServiceDTO> mockHistory = List.of(
                new BikeUserAndServiceDTO(1, 1000, "2024-01-01", "2024-01-10", "2024-01-15", 1, 5000, "Service1"),
                new BikeUserAndServiceDTO(2, 1200, "2024-02-01", "2024-02-10", "2024-02-15", 2, 7000, "Service2")
        );

        when(bshrepo.findByBikeUserAndService("user@example.com", "FIN123", 1L)).thenReturn(mockHistory);

        given()
                .contentType("application/json")
                .body(bikeHistoryDTO)
                .when()
                .get("/maintenance/getBikeserviceHistory/last")
                .then()
                .statusCode(200)
                .body("serviceTitle", equalTo("Service2"));
    }

    @Test
    void testGetLastBikeserviceHistory_NotFound() {
        BikeHistoryDTO bikeHistoryDTO = new BikeHistoryDTO("user@example.com", "FIN123", 1L);
        when(bshrepo.findByBikeUserAndService("user@example.com", "FIN123", 1L)).thenReturn(Collections.emptyList());

        given()
                .contentType("application/json")
                .body(bikeHistoryDTO)
                .when()
                .get("/maintenance/getBikeserviceHistory/last")
                .then()
                .statusCode(404)
                .body(equalTo("No history found"));
    }

    @Test
    void testAddServiceHistory_Success() {
        AddServiceHistoryDTO dto = new AddServiceHistoryDTO("user@example.com", "FIN123", 1L, 15000);
        BikeUser mockBikeUser = new BikeUser();
        User mockUser = new User();
        mockUser.setEmail("user@example.com");
        mockBikeUser.setUser(mockUser);
        Bike mockBike = new Bike();
        mockBikeUser.setBike(mockBike);
        mockBikeUser.setKm(15000L);
        BikeService mockService = new BikeService();

        when(ubrepo.getBikeUserByMailAndFin("user@example.com", "FIN123")).thenReturn(mockBikeUser);
        when(bikeRepository.findById(any())).thenReturn(mockBike);
        when(bsrepo.findById(1L)).thenReturn(mockService);
        doNothing().when(bshrepo).persist(Mockito.any(BikeserviceHistory.class));

        given()
                .contentType("application/json")
                .body(dto)
                .when()
                .post("/maintenance/addServiceHistory")
                .then()
                .statusCode(200)
                .body(equalTo("History added"));
    }
}
