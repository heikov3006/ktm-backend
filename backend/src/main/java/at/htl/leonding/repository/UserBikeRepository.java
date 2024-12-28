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

    public BikeUser getBikeUserByMailAndBikeId(String email, Long bikeId){
        return (BikeUser) getEntityManager().createQuery("select bu from BikeUser bu where bu.user.email like :email and bu.bike.id = :bikeId").setParameter("email", email).setParameter("bikeId", bikeId).getSingleResult();
    }

    public void deleteByEmail(String email) {
        getEntityManager().createQuery("delete from BikeUser bu where bu.user.email = :email").setParameter("email", email).executeUpdate();
    }
}
