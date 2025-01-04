package at.htl.leonding;

import at.htl.leonding.model.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
class BikeserviceHistoryTest {

    private static BikeUser testBikeUser;
    private static BikeService testBikeService;

    @BeforeEach
    void setup() {

        User user = new User();
        user.setEmail("search@example.com");
        user.setPassword("password456");
        user.setFirstname("Alice");
        user.setLastname("Smith");
        user.persist();

        Bike bike = new Bike();
        bike.setBrand("KTM");
        bike.setModel("Super Adventure");
        bike.setProductionNumber("1234567890");
        bike.setProductionYear("2020");
        bike.persist();

        // Erstelle und persistiere einen BikeUser
        testBikeUser = new BikeUser();
        testBikeUser.setUser(user);
        testBikeUser.setKm(10000L);
        testBikeUser.setFin("1234567890");
        testBikeUser.setBike(bike);
        testBikeUser.persist();

        // Erstelle und persistiere einen BikeService
        testBikeService = new BikeService();
        testBikeService.setTitle("Oil Change");
        testBikeService.setInterval(5000);
        testBikeService.persist();
    }

    @Test
    void testCreateBikeserviceHistory() {
        BikeserviceHistory history = new BikeserviceHistory();
        history.setBikeUser(testBikeUser);
        history.setService(testBikeService);
        history.setServiceDate(LocalDate.of(2024, 12, 25));
        history.setKilometersAtService(12000);

        history.persist();

        assertNotNull(history.id, "The BikeserviceHistory ID should be generated after persisting");
        assertEquals(testBikeUser, history.getBikeUser(), "The BikeUser should match the set value");
        assertEquals(testBikeService, history.getService(), "The BikeService should match the set value");
        assertEquals(LocalDate.of(2024, 12, 25), history.getServiceDate(), "The serviceDate should match the set value");
        assertEquals(12000, history.getKilometersAtService(), "The kilometersAtService should match the set value");
        assertNotNull(history.getCreatedAt(), "The createdAt timestamp should not be null");
        assertNotNull(history.getUpdatedAt(), "The updatedAt timestamp should not be null");
    }

    @Test
    void testUpdateBikeserviceHistory() {
        BikeserviceHistory history = new BikeserviceHistory();
        history.setBikeUser(testBikeUser);
        history.setService(testBikeService);
        history.setServiceDate(LocalDate.of(2024, 12, 25));
        history.setKilometersAtService(12000);

        history.persist();

        // Ändere die Werte und aktualisiere die Entität
        LocalDate newServiceDate = LocalDate.of(2025, 1, 1);
        int newKilometersAtService = 13000;
        history.setServiceDate(newServiceDate);
        history.setKilometersAtService(newKilometersAtService);
        history.persist();

        assertEquals(newServiceDate, history.getServiceDate(), "The serviceDate should reflect the updated value");
        assertEquals(newKilometersAtService, history.getKilometersAtService(), "The kilometersAtService should reflect the updated value");
        assertNotEquals(history.getCreatedAt(), history.getUpdatedAt(), "The updatedAt timestamp should change on update");
    }

    @Test
    void testPreUpdateHook() {
        BikeserviceHistory history = new BikeserviceHistory();
        history.setBikeUser(testBikeUser);
        history.setService(testBikeService);
        history.setServiceDate(LocalDate.of(2024, 12, 25));
        history.setKilometersAtService(12000);

        history.persist();
        LocalDate originalUpdatedAt = history.getUpdatedAt();

        // Simuliere eine Verzögerung, um eine tatsächliche Änderung im UpdatedAt-Timestamp zu überprüfen
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail("Thread sleep interrupted");
        }

        history.setKilometersAtService(12500);
        history.persist();

        assertNotEquals(originalUpdatedAt, history.getUpdatedAt(), "The updatedAt timestamp should be updated on modification");
    }

    @Test
    void testNullConstraints() {
        BikeserviceHistory history = new BikeserviceHistory();

        assertThrows(Exception.class, history::persist, "Persisting without required fields should throw an exception");

        history.setBikeUser(testBikeUser);
        assertThrows(Exception.class, history::persist, "Persisting without service should throw an exception");

        history.setService(testBikeService);
        assertThrows(Exception.class, history::persist, "Persisting without serviceDate should throw an exception");

        history.setServiceDate(LocalDate.of(2024, 12, 25));
        assertThrows(Exception.class, history::persist, "Persisting without kilometersAtService should throw an exception");
    }
}
