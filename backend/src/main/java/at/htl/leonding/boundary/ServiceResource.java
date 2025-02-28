package at.htl.leonding.boundary;

import at.htl.leonding.dto.*;
import at.htl.leonding.model.*;
import at.htl.leonding.repository.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("maintenance")
public class ServiceResource {

    @Inject
    BikeServiceRepository bsrepo;

    @Inject
    UserRepository userRepository;

    @Inject
    UserBikeRepository userBikeRepository;

    @Inject
    BikeRepository bikeRepository;

    @Inject
    BikeServiceRepository bikeServiceRepository;

    @Inject
    BikeserviceHistoryRepository bikeserviceHistoryRepository;

    @GET
    @Path("getServicesByBike")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceByBike(GetServiceByBikeIdDTO getServiceByBikeDTO){
        List<BikeService> bikeServiceList = bsrepo.getServicesByBikeId(getServiceByBikeDTO.bikeId());
        List<ServiceByBikeDTO> dtoList = bikeServiceList.stream().map(bikeService -> {
            String title = bikeService.getTitle();
            int interval = bikeService.getInterval();
            Long serviceId = bikeService.getId();
            return new ServiceByBikeDTO(title, interval, serviceId);
        }).toList();
        Bike bike = bikeRepository.findById(getServiceByBikeDTO.bikeId());
        return Response.ok(new GetServiceByBikeDTO(bike, dtoList)).build();
    }

    @GET
    @Path("getServiceListForSpecificBike")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceListForSpecificBike(ServiceForBikeDTO serviceForBikeDTO){
        BikeUser bikeUser = userBikeRepository.getBikeUserByMailAndFin(serviceForBikeDTO.email(), serviceForBikeDTO.fin());
        List<BikeService> bikeServiceList = bikeServiceRepository.getServicesByBikeId(bikeUser.getBike().getId());
        List<BikeserviceHistory> bikeserviceHistories = bikeserviceHistoryRepository.findByEmailAndFin(serviceForBikeDTO.email(), serviceForBikeDTO.fin());
        if(!bikeserviceHistories.isEmpty()) {
            List<BikeServiceDTO> bikeServices = bikeserviceHistories.stream().map(bikeserviceHistory -> {
                String title = bikeserviceHistory.service.getTitle();
                int interval = bikeserviceHistory.service.getInterval();
                Long serviceId = bikeserviceHistory.service.getId();
                int kilometersAtService = bikeserviceHistory.kilometersAtService;
                Long actualKm = bikeserviceHistory.bikeUser.getKm();
                return new BikeServiceDTO(title, interval, serviceId, kilometersAtService, actualKm);
            }).toList();
            return Response.ok(new GetServiceListForSpecificBikeDTO(bikeServiceList, bikeServices)).build();
        } else {
            return Response.ok().build();
        }
    }
}
