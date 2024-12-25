package at.htl.leonding.repository;

import at.htl.leonding.model.BikeService;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BikeServiceRepository implements PanacheRepository<BikeService> {
}
