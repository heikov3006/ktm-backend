package at.htl.leonding.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class BikeService extends PanacheEntityBase {

    @Id
    Long id;

    String title;
    int interval;
    //@ManyToMany(mappedBy = "services")
    //private List<Bike> bikes = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /*public List<Bike> getBike() {
        return bikes;
    }

    public void setBike(List<Bike> bike) {
        this.bikes = bike;
    }*/

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
