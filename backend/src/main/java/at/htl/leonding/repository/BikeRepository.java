package at.htl.leonding.repository;

import at.htl.leonding.model.Bike;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BikeRepository implements PanacheRepository<Bike> {
}
