package at.htl.leonding.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Bike extends PanacheEntityBase {

    @Id
    Long id;

    String brand;
    String model;
    String productionNumber;
    String productionYear;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "bike_service",
            joinColumns = @JoinColumn(name = "bike_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    @JsonIgnore
    private List<BikeService> services = new ArrayList<>();

    public List<BikeService> getServices() {
        return services;
    }

    public void setServices(List<BikeService> services) {
        this.services = services;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProductionNumber() {
        return productionNumber;
    }

    public void setProductionNumber(String productionNumber) {
        this.productionNumber = productionNumber;
    }

    public String getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(String productionYear) {
        this.productionYear = productionYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
