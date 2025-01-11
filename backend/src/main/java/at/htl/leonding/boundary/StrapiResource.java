package at.htl.leonding.boundary;

import at.htl.leonding.dto.*;
import at.htl.leonding.model.Bike;
import at.htl.leonding.model.BikeService;
import at.htl.leonding.repository.BikeRepository;
import at.htl.leonding.repository.BikeServiceRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/strapi")
public class StrapiResource {

    @Inject
    BikeServiceRepository bikeServiceRepository;

    @Inject
    BikeRepository bikeRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @POST
    @Transactional
    public void saveNewStrapiEntry(StrapiDTO strapiDTO) {

        System.out.println(strapiDTO);
        if (strapiDTO.model().equals("guidance")) {
            if (strapiDTO.event().equals("entry.create")) {
                StrapiGuidanceEntryDTO entry = objectMapper.convertValue(strapiDTO.entry(), StrapiGuidanceEntryDTO.class);
                BikeService bikeService = new BikeService();
                bikeService.setTitle(entry.title());
                bikeService.setInterval(entry.interval());
                bikeService.setId(entry.id());
                if (entry.bikes() == null) {
                    bikeServiceRepository.persist(bikeService);
                } else {
                    List<StrapiBikeDTO> dtoList = entry.bikes();
                    dtoList.forEach(strapiBikeDTO -> {
                        Bike bike = bikeRepository.findById(strapiBikeDTO.id());
                        bike.getServices().add(bikeService);
                    });
                    bikeServiceRepository.persist(bikeService);
                }
            } else if (strapiDTO.event().equals("entry.update")) {
                StrapiGuidanceEntryDTO entry = objectMapper.convertValue(strapiDTO.entry(), StrapiGuidanceEntryDTO.class);
                BikeService bikeService = bikeServiceRepository.findById(entry.id());
                bikeService.setTitle(entry.title());
                bikeService.setInterval(entry.interval());
                if (entry.bikes() == null) {
                    bikeServiceRepository.persist(bikeService);
                } else {
                    List<StrapiBikeDTO> dtoList = entry.bikes();
                    // Bestehende Bikes, die den Service haben
                    List<Bike> existingBikes = bikeRepository.listAll()
                            .stream()
                            .filter(bike -> bike.getServices().contains(bikeService))
                            .toList();

                    // IDs der Bikes aus dem DTO
                    Set<Long> newBikeIds = dtoList.stream()
                            .map(StrapiBikeDTO::id)
                            .collect(Collectors.toSet());

                    // Entferne den Service von Bikes, die nicht mehr im DTO sind
                    existingBikes.stream()
                            .filter(bike -> !newBikeIds.contains(bike.getId())) // Bikes, die entfernt wurden
                            .forEach(bike -> bike.getServices().remove(bikeService));

                    // Füge den Service zu neuen Bikes hinzu
                    dtoList.forEach(strapiBikeDTO -> {
                        Bike bike = bikeRepository.findById(strapiBikeDTO.id());
                        if (!bike.getServices().contains(bikeService)) {
                            bike.getServices().add(bikeService);
                        }
                    });
                }
            } else if (strapiDTO.event().equals("entry.delete")) {
                StrapiGuidanceEntryDTO entry = objectMapper.convertValue(strapiDTO.entry(), StrapiGuidanceEntryDTO.class);
                BikeService bikeService = bikeServiceRepository.findById(entry.id());
                List<StrapiBikeDTO> dtoList = entry.bikes();
                dtoList.forEach(strapiBikeDTO -> {
                    Bike bike = bikeRepository.findById(strapiBikeDTO.id());
                    bike.getServices().remove(bikeService);
                });
                bikeServiceRepository.delete(bikeService);
            }
        } else if (strapiDTO.model().equals("bike")) {
            System.out.println(strapiDTO);
            if (strapiDTO.event().equals("entry.create")) {
                Bike bike = new Bike();
                StrapiBikeEntryDTO strapiBikeEntryDTO = objectMapper.convertValue(strapiDTO.entry(), StrapiBikeEntryDTO.class);
                bike.setId(strapiBikeEntryDTO.id());
                bike.setBrand(strapiBikeEntryDTO.company());
                bike.setModel(strapiBikeEntryDTO.model());
                bike.setProductionYear(strapiBikeEntryDTO.productionYear());
                bike.setServices(bikeServiceRepository.getServicesByBikeId(strapiBikeEntryDTO.id()));
                bike.setProductionNumber(strapiBikeEntryDTO.modelId());
                List<StrapiGuidanceDTO> dtoList = strapiBikeEntryDTO.guidances();
                dtoList.forEach(strapiGuidanceDTO -> {
                    BikeService bikeService = bikeServiceRepository.findById(strapiGuidanceDTO.id());
                    bike.getServices().add(bikeService);
                });
                bike.persist();

            } else if (strapiDTO.event().equals("entry.update")) {
                StrapiBikeEntryDTO strapiBikeEntryDTO = objectMapper.convertValue(strapiDTO.entry(), StrapiBikeEntryDTO.class);
                System.out.println(strapiBikeEntryDTO.id());
                Bike bike = bikeRepository.findById(strapiBikeEntryDTO.id());
                bike.setBrand(strapiBikeEntryDTO.company());
                bike.setModel(strapiBikeEntryDTO.model());
                bike.setProductionYear(strapiBikeEntryDTO.productionYear());
                bike.setServices(bikeServiceRepository.getServicesByBikeId(strapiBikeEntryDTO.id()));
                bike.setProductionNumber(strapiBikeEntryDTO.modelId());
                // Aktuelle Services aus dem DTO
                List<StrapiGuidanceDTO> dtoList = strapiBikeEntryDTO.guidances();
                Set<Long> newServiceIds = dtoList.stream()
                        .map(StrapiGuidanceDTO::id)
                        .collect(Collectors.toSet());

                // Entferne Services, die nicht mehr im DTO sind
                bike.getServices().removeIf(service -> !newServiceIds.contains(service.getId()));

                // Füge neue Services hinzu
                dtoList.forEach(strapiGuidanceDTO -> {
                    BikeService bikeService = bikeServiceRepository.findById(strapiGuidanceDTO.id());
                    if (!bike.getServices().contains(bikeService)) {
                        bike.getServices().add(bikeService);
                    }
                });
                bike.persist();
            } else if (strapiDTO.event().equals("entry.delete")) {
                StrapiBikeEntryDTO strapiBikeEntryDTO = objectMapper.convertValue(strapiDTO.entry(), StrapiBikeEntryDTO.class);
                Bike bike = bikeRepository.findById(strapiBikeEntryDTO.id());
                bike.delete();
            }
        } else {
            System.out.println("Unknown model: " + strapiDTO.model());
        }

    }
}
