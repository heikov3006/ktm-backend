package at.htl.leonding.repository;

import at.htl.leonding.model.Bike;
import at.htl.leonding.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    @Inject
    UserBikeRepository userBikeRepository;

    public boolean userWithEmailAlreadyExisting(String email){
        if(getUserByEmail(email) != null) return true;
        return false;
    }

    public User getUserByEmail(String email) {
        List<User> user =  getEntityManager().createQuery("select u from User u where email like :email").setParameter("email", email).getResultList();
        if(!user.isEmpty()) return user.get(0);
        return null;
    }

    public User updateUser(String email, User user){
        User userToEdit = getUserByEmail(email);
        if(userToEdit != null) {
            userToEdit.setFirstname(user.getFirstname());
            userToEdit.setLastname(user.getLastname());
            userToEdit.setEmail(user.getEmail());
            userToEdit.setPassword(user.getPassword());
            return userToEdit;
        }
        return null;
    }

    public void deleteByEmail(String email) {
        getEntityManager().createQuery("delete from User u where u.email = :email").setParameter("email", email).executeUpdate();
    }
}
