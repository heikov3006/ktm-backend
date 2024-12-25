package at.htl.leonding.repository;

import at.htl.leonding.model.BikeUser;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserBikeRepository implements PanacheRepository<BikeUser> {

    public List<BikeUser> getBikeUserByEmail(String email) {
        return getEntityManager().createQuery("select bu from BikeUser bu where bu.user.email like :email").setParameter("email", email).getResultList();
    }

    public void deleteByEmail(Long id) {
        getEntityManager().createQuery("delete from BikeUser bu where bu.user.id = :id").setParameter("id", id);
    }

}
