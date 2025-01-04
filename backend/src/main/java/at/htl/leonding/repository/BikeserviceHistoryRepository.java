package at.htl.leonding.repository;

import at.htl.leonding.model.BikeserviceHistory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BikeserviceHistoryRepository implements PanacheRepository<BikeserviceHistory> {

    public List<BikeserviceHistory> findByBikeUserAndService(String email, String fin, Long serviceId) {
        if (!email.isEmpty() && !fin.isEmpty() && serviceId != null) {
            try {
                return getEntityManager().createQuery("select bsh from BikeserviceHistory bsh where bikeUser.user.email like :email and bikeUser.fin = :fin and service.id = :serviceId").setParameter("email", email).setParameter("fin", fin).setParameter("serviceId", serviceId).getResultList();
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
}
