package at.htl.leonding.repository;

import at.htl.leonding.model.BikeService;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class BikeServiceRepository implements PanacheRepository<BikeService> {
    public BikeService findServiceById(Long serviceId) {
        return find("id", serviceId).firstResult();
    }

    public List<BikeService> getServicesByBikeId(Long bikeId) {
        return getEntityManager().createQuery("select b.services from Bike b where b.id = :bikeId").setParameter("bikeId", bikeId).getResultList();
    }
}
