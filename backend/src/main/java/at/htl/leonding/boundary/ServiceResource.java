package at.htl.leonding.boundary;

import at.htl.leonding.dto.*;
import at.htl.leonding.model.*;
import at.htl.leonding.repository.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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
    @Path("getServicesByBike/{bikeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceByBike(@PathParam("bikeId") Long bikeId){
        List<BikeService> bikeServiceList = bsrepo.getServicesByBikeId(bikeId);
        List<ServiceByBikeDTO> dtoList = bikeServiceList.stream().map(bikeService -> {
            String title = bikeService.getTitle();
            int interval = bikeService.getInterval();
            Long serviceId = bikeService.getId();
            return new ServiceByBikeDTO(title, interval, serviceId);
        }).toList();
        Bike bike = bikeRepository.findById(bikeId);
        return Response.ok(new GetServiceByBikeDTO(bike, dtoList)).build();
    }

    @GET
    @Path("getServiceListForSpecificBike/{email}/{fin}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceListForSpecificBike(@PathParam("email") String email, @PathParam("fin") String fin){
        BikeUser bikeUser = userBikeRepository.getBikeUserByMailAndFin(email, fin);
        List<BikeService> bikeServiceList = bikeServiceRepository.getServicesByBikeId(bikeUser.getBike().getId());
        List<BikeserviceHistory> bikeserviceHistories = bikeserviceHistoryRepository.findByEmailAndFin(email, fin);
        if(!bikeserviceHistories.isEmpty()) {
            List<BikeServiceDTO> bikeServices = bikeserviceHistories.stream().map(bikeserviceHistory -> {
                String title = bikeserviceHistory.getService().getTitle();
                int interval = bikeserviceHistory.getService().getInterval();
                Long serviceId = bikeserviceHistory.getService().getId();
                int kilometersAtService = bikeserviceHistory.getKilometersAtService();
                Long actualKm = bikeserviceHistory.getBikeUser().getKm();
                return new BikeServiceDTO(title, interval, serviceId, kilometersAtService, actualKm);
            }).toList();
            return Response.ok(new GetServiceListForSpecificBikeDTO(bikeServiceList, bikeServices)).build();
        } else {
            return Response.ok().build();
        }
    }
}
