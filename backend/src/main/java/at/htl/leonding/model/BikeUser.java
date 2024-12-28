package at.htl.leonding.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "user_bike")
public class BikeUser extends PanacheEntityBase {

    @Id
    String fin;

    @ManyToOne()
    @JoinColumn(name = "userid", nullable = false)
    private User user;


    @ManyToOne()
    @JoinColumn(name = "bikeid", nullable = false)
    private Bike bike;

    Long km;

    public BikeUser(String fin, User user, Bike bike, Long km) {
        setUser(user);
        setBike(bike);
        setKm(km);
        setFin(fin);
    }

    public BikeUser() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public Long getKm() {
        return km;
    }

    public void setKm(Long km) {
        this.km = km;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }
}
