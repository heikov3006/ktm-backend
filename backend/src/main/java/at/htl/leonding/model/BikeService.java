package at.htl.leonding.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
public class BikeService extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bikeService_seq")
    @SequenceGenerator(
            name = "bikeService_seq",
            sequenceName = "bikeService_sequence",
            initialValue = 1000, // Startwert
            allocationSize = 1   // Inkrementgröße
    )
    Long id;

    String title;
    int interval;
    @ManyToOne
    Bike bike;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Long getId() {
        return id;
    }
}
