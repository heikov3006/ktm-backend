package at.htl.leonding.repository;

import at.htl.leonding.model.BikeserviceHistory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BikeserviceHistoryRepository implements PanacheRepository<BikeserviceHistory> {

    public List<BikeserviceHistory> findByBikeUserAndService(String email, Long bikeId, Long serviceId) {
        if (!email.isEmpty() && bikeId != null && serviceId != null) {
            try {
                return getEntityManager().createQuery("select bsh from BikeserviceHistory bsh where bikeUser.user.email like :email and bikeUser.bike.id = :bikeId and service.id = :serviceId").setParameter("email", email).setParameter("bikeId", bikeId).setParameter("serviceId", serviceId).getResultList();
            } catch (Exception exception) {
                return null;
            }
        }
        return null;
    }
}
