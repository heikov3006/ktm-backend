package at.htl.leonding.boundary;

import at.htl.leonding.dto.BikeDTO;
import at.htl.leonding.dto.BikeServiceDTO;
import at.htl.leonding.dto.GetServiceByBikeDTO;
import at.htl.leonding.dto.ServiceForBikeDTO;
import at.htl.leonding.model.BikeService;
import at.htl.leonding.model.BikeUser;
import at.htl.leonding.model.BikeserviceHistory;
import at.htl.leonding.model.User;
import at.htl.leonding.repository.BikeServiceRepository;
import at.htl.leonding.repository.BikeserviceHistoryRepository;
import at.htl.leonding.repository.UserBikeRepository;
import at.htl.leonding.repository.UserRepository;
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
    BikeServiceRepository bikeServiceRepository;

    @Inject
    BikeserviceHistoryRepository bikeserviceHistoryRepository;

    @GET
    @Path("getServicesByBike")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceByBike(GetServiceByBikeDTO getServiceByBikeDTO){
        return Response.ok(bsrepo.getServicesByBikeId(getServiceByBikeDTO.bikeId())).build();
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
            return Response.ok(bikeServices).build();
        } else {
            return Response.ok().build();
        }
    }
}
