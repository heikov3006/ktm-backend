package at.htl.leonding.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class BikeserviceHistory extends PanacheEntity {

    @ManyToOne()
    @JoinColumn(name = "userBikeId")
    private BikeUser bikeUser;  // Verknüpfung zur BikeUser-Tabelle

    private String fin;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "serviceId", nullable = false)
    private BikeService service; // Verknüpfung zur BikeService-Tabelle

    @Column(nullable = false)
    private LocalDate serviceDate;  // Datum der Durchführung

    @Column(nullable = false)
    private int kilometersAtService; // Kilometerstand beim Service

    @Column(nullable = false, updatable = false)
    private Long bikeId;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now(); // Zeitstempel der Erstellung

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    @Column(nullable = false)
    private LocalDate updatedAt = LocalDate.now(); // Zeitstempel der letzten Änderung

    public Long getBikeId() {
        return bikeId;
    }

    public void setBikeId(Long bikeId) {
        this.bikeId = bikeId;
    }

    public BikeUser getBikeUser() {
        return bikeUser;
    }

    public void setBikeUser(BikeUser bikeUser) {
        this.bikeUser = bikeUser;
    }

    public BikeService getService() {
        return service;
    }

    public void setService(BikeService service) {
        this.service = service;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public int getKilometersAtService() {
        return kilometersAtService;
    }

    public void setKilometersAtService(int kilometersAtService) {
        this.kilometersAtService = kilometersAtService;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }
}
