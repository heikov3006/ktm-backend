package at.htl.leonding.repository;

import at.htl.leonding.dto.BikeUserAndServiceDTO;
import at.htl.leonding.dto.GetAllByFinDTO;
import at.htl.leonding.dto.GetByFinDTO;
import at.htl.leonding.model.Bike;
import at.htl.leonding.model.BikeserviceHistory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class BikeserviceHistoryRepository implements PanacheRepository<BikeserviceHistory> {

    private final BikeRepository bikeRepository;

    @jakarta.inject.Inject
    public BikeserviceHistoryRepository(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    public List<BikeUserAndServiceDTO> findByBikeUserAndService(String email, String fin, Long serviceId) {
        if (!email.isEmpty() && !fin.isEmpty() && serviceId != null) {
            try {
                return getEntityManager().createQuery("select bsh from BikeserviceHistory bsh where bikeUser.user.email like :email and bikeUser.fin = :fin and service.id = :serviceId order by kilometersAtService asc").setParameter("email", email).setParameter("fin", fin).setParameter("serviceId", serviceId).getResultList();
            } catch (Exception exception) {
                return null;
            }
        }
        return null;
    }

    public List<BikeserviceHistory> findByEmailAndFin(String email, String fin){
        if (!email.isEmpty() && !fin.isEmpty()) {
            try {
                return getEntityManager().createQuery("select bsh from BikeserviceHistory bsh where bikeUser.user.email like :email and bikeUser.fin = :fin").setParameter("email", email).setParameter("fin", fin).getResultList();
            } catch (Exception exception) {
                return null;
            }
        }
        return null;
    }

    public GetAllByFinDTO getByFin(String fin) {
        List<BikeserviceHistory> bikeserviceHistoryList = getEntityManager().createQuery("select bsh from BikeserviceHistory bsh where bsh.fin = :fin").setParameter("fin", fin).getResultList();
        List<GetByFinDTO> getByFinDTOList = new ArrayList<>();
        bikeserviceHistoryList.forEach(bikeserviceHistory -> {
            GetByFinDTO getByFinDTO = new GetByFinDTO(bikeserviceHistory.getService(), bikeserviceHistory.getServiceDate(), bikeserviceHistory.getKilometersAtService(), bikeserviceHistory.getCreatedAt(), bikeserviceHistory.getServiceType());
            getByFinDTOList.add(getByFinDTO);
        });
        Bike bike = bikeRepository.findById(bikeserviceHistoryList.getFirst().getBikeId());
        return new GetAllByFinDTO(fin, bike, getByFinDTOList);
    }

    public void deleteEmail(String email) {
        getEntityManager().createQuery("update BikeserviceHistory bsh set bikeUser = null where bikeUser.user.email = :email").setParameter("email", email).executeUpdate();
    }
}
