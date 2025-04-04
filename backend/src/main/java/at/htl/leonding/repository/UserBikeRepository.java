package at.htl.leonding.repository;

import at.htl.leonding.model.Bike;
import at.htl.leonding.model.BikeUser;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserBikeRepository implements PanacheRepository<BikeUser> {

    public List<BikeUser> getBikeUserByEmail(String email) {
        return getEntityManager().createQuery("select bu from BikeUser bu where bu.user.email like :email").setParameter("email", email).getResultList();
    }

    public BikeUser getBikeUserByMailAndFin(String email, String fin){
        return (BikeUser) getEntityManager().createQuery("select bu from BikeUser bu where bu.user.email like :email and bu.fin = :fin").setParameter("email", email).setParameter("fin", fin).getSingleResult();
    }

    public void deleteByEmail(String email) {
        getEntityManager().createQuery("delete from BikeUser bu where bu.user.email = :email").setParameter("email", email).executeUpdate();
    }

    public void deleteByEmailAndFin(String email, String fin) {
        getEntityManager().createQuery("delete from BikeUser bu where bu.user.email = :email and bu.fin like :fin").setParameter("email", email).setParameter("fin", fin).executeUpdate();

    }

    public BikeUser getBikeUserByFin(String fin) {
        return (BikeUser) getEntityManager().createQuery("select bu from BikeUser bu where bu.fin like :fin").setParameter("fin", fin).getSingleResult();
    }
}

