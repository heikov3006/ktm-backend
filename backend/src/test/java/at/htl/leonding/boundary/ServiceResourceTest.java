package at.htl.leonding.boundary;

import at.htl.leonding.dto.*;
import at.htl.leonding.model.*;
import at.htl.leonding.repository.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ServiceResourceTest {

    @InjectMock
    BikeServiceRepository bsrepo;

    @InjectMock
    UserBikeRepository userBikeRepository;

    @InjectMock
    BikeRepository bikeRepository;

    @InjectMock
    BikeServiceRepository bikeServiceRepository;

    @InjectMock
    BikeserviceHistoryRepository bikeserviceHistoryRepository;

    @Test
    void testGetServiceByBike_Success() {
        Long bikeId = 1L;
        GetServiceByBikeIdDTO requestDTO = new GetServiceByBikeIdDTO(bikeId);

        Bike bike = new Bike();
        bike.setId(bikeId);
        when(bikeRepository.findById(bikeId)).thenReturn(bike);

        BikeService service1 = new BikeService();
        service1.setId(1L);
        service1.setTitle("Service 1");
        service1.setInterval(5000);

        BikeService service2 = new BikeService();
        service2.setId(2L);
        service2.setTitle("Service 2");
        service2.setInterval(10000);

        List<BikeService> mockServices = List.of(service1, service2);
        when(bsrepo.getServicesByBikeId(bikeId)).thenReturn(mockServices);

        given()
                .contentType("application/json")
                .body(requestDTO)
                .when()
                .get("/maintenance/getServicesByBike")
                .then()
                .statusCode(200)
                .body("bike.id", equalTo(bikeId.intValue()))
                .body("services.size()", equalTo(2));
    }

    @Test
    void testGetServiceListForSpecificBike_Success() {
        ServiceForBikeDTO requestDTO = new ServiceForBikeDTO("user@example.com", "FIN123");

        Bike bike = new Bike();
        bike.setId(1L);
        BikeUser bikeUser = new BikeUser();
        bikeUser.setBike(bike);
        bikeUser.setKm(15000L);

        when(userBikeRepository.getBikeUserByMailAndFin("user@example.com", "FIN123")).thenReturn(bikeUser);

        BikeService service1 = new BikeService();
        service1.setId(1L);
        service1.setTitle("Service 1");
        service1.setInterval(5000);

        BikeService service2 = new BikeService();
        service2.setId(2L);
        service2.setTitle("Service 2");
        service2.setInterval(10000);

        List<BikeService> mockServices = List.of(service1, service2);
        when(bikeServiceRepository.getServicesByBikeId(1L)).thenReturn(mockServices);

        BikeserviceHistory history1 = new BikeserviceHistory();
        history1.setBikeUser(bikeUser);
        history1.setService(service1);
        history1.setKilometersAtService(10000);

        BikeserviceHistory history2 = new BikeserviceHistory();
        history2.setBikeUser(bikeUser);
        history2.setService(service2);
        history2.setKilometersAtService(15000);

        List<BikeserviceHistory> mockHistories = List.of(history1, history2);
        when(bikeserviceHistoryRepository.findByEmailAndFin("user@example.com", "FIN123")).thenReturn(mockHistories);

        given()
                .contentType("application/json")
                .body(requestDTO)
                .when()
                .get("/maintenance/getServiceListForSpecificBike")
                .then()
                .statusCode(200)
                .body("bikeServices.size()", equalTo(2));
    }
}
