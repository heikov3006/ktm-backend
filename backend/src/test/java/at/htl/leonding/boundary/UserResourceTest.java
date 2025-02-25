package at.htl.leonding.boundary;

import at.htl.leonding.dto.AddBikeDTO;
import at.htl.leonding.model.Bike;
import at.htl.leonding.model.User;
import at.htl.leonding.repository.BikeRepository;
import at.htl.leonding.repository.UserBikeRepository;
import at.htl.leonding.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@Transactional
public class UserResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    BikeRepository bikeRepository;

    @Inject
    UserBikeRepository userBikeRepository;

    @Inject
    UserResource userResource;

    @Test
    public void testAddBike() {
        // Setup: Erstelle ein Motorrad (KTM)
        Bike bike = new Bike();
        bike.setBrand("KTM");
        bike.setModel("Duke 390");
        bike.setProductionNumber("D390-2023");
        bike.setProductionYear("2023");
        bike.setId(1000L);
        bikeRepository.persist(bike);

        User user = new User("John", "Doe", "john.doe@example.com", "hashedpassword");
        userRepository.persist(user);

        AddBikeDTO addBikeDTO = new AddBikeDTO("123ABC456", "john.doe@example.com", bike.getId(), 500L, "imageUrl");

        Response response = userResource.addBike(addBikeDTO);

        assertThat(response.getStatus(), is(200));

        assertThat(userBikeRepository.listAll().size(), is(1));  // Es sollte 1 Fahrrad für den Benutzer geben

        assertThat(userBikeRepository.getBikeUserByEmail("john.doe@example.com").get(0).getBike().getId(), is(bike.getId()));
    }

    @Test
    public void testAddBike_userNotFound() {
        AddBikeDTO addBikeDTO = new AddBikeDTO("123ABC456", "nonexistent.user@example.com", 1L, 500L, "imageUrl");

        Response response = userResource.addBike(addBikeDTO);

        assertThat(response.getStatus(), is(202));  // Antwort sollte immer noch 200 OK sein
        assertThat(response.getEntity().toString(), containsString("No such user or bike"));
    }

    @Test
    public void testAddBike_bikeNotFound() {
        AddBikeDTO addBikeDTO = new AddBikeDTO("123ABC456", "john.doe@example.com", 999L, 500L, "imageUrl");

        Response response = userResource.addBike(addBikeDTO);

        assertThat(response.getStatus(), is(202));  // Antwort sollte 200 OK sein
        assertThat(response.getEntity().toString(), containsString("No such user or bike"));
    }

    @Test
    public void testAddBike_existingBike() {
        Bike bike = new Bike();
        bike.setBrand("KTM");
        bike.setModel("Duke 390");
        bike.setProductionNumber("D390-2023");
        bike.setProductionYear("2023");
        bike.setId(2000L);
        bikeRepository.persist(bike);

        User user = new User("John", "Doe", "johnny.doe@example.com", "hashedpassword");
        userRepository.persist(user);

        AddBikeDTO addBikeDTO = new AddBikeDTO("12345ABC", "johnny.doe@example.com", bike.getId(), 500L, "imageUrl");
        Response response = userResource.addBike(addBikeDTO);
        assertThat(response.getStatus(), is(200));

        Response secondResponse = userResource.addBike(addBikeDTO);
        assertThat(secondResponse.getStatus(), is(202)); // Custom-Status für "Fahrrad bereits verwendet"
        assertThat(secondResponse.getEntity().toString(), containsString("Bike with this FIN is already used"));
    }
}
