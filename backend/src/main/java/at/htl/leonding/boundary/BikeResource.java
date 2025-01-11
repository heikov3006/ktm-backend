package at.htl.leonding.boundary;

import at.htl.leonding.dto.AddServiceHistoryDTO;
import at.htl.leonding.dto.BikeHistoryDTO;
import at.htl.leonding.dto.BikeUserAndServiceDTO;
import at.htl.leonding.model.Bike;
import at.htl.leonding.model.BikeService;
import at.htl.leonding.model.BikeUser;
import at.htl.leonding.model.BikeserviceHistory;
import at.htl.leonding.repository.BikeRepository;
import at.htl.leonding.repository.BikeServiceRepository;
import at.htl.leonding.repository.BikeserviceHistoryRepository;
import at.htl.leonding.repository.UserBikeRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("maintenance")
public class BikeResource {

    @Inject
    BikeserviceHistoryRepository bshrepo;

    @Inject
    BikeServiceRepository bsrepo;

    @Inject
    UserBikeRepository ubrepo;

    @Inject
    BikeRepository bikeRepository;

    @Path("getBikeserviceHistory")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBikeserviceHistory(BikeHistoryDTO bikeHistoryDTO) {
        return Response.ok(
                 bshrepo.findByBikeUserAndService(bikeHistoryDTO.email(), bikeHistoryDTO.fin(), bikeHistoryDTO.serviceId())
        ).build();
    }

    @Path("getBikeserviceHistory/last")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastBikeserviceHistory(BikeHistoryDTO bikeHistoryDTO) {
        List<BikeUserAndServiceDTO> returnList = bshrepo.findByBikeUserAndService(bikeHistoryDTO.email(), bikeHistoryDTO.fin(), bikeHistoryDTO.serviceId());
        if (returnList.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No history found").build();
        }
        return Response.ok(returnList.getLast()).build();
    }

    @Path("addServiceHistory")
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addServiceHistory(AddServiceHistoryDTO addServiceHistoryDTO) {
        BikeserviceHistory bikeserviceHistory = new BikeserviceHistory();
        BikeUser bikeUser = ubrepo.getBikeUserByMailAndFin(addServiceHistoryDTO.email(), addServiceHistoryDTO.fin());
        bikeserviceHistory.setBikeUser(bikeUser);
        bikeserviceHistory.setServiceDate(LocalDate.now());
        bikeserviceHistory.setKilometersAtService(addServiceHistoryDTO.km());
        Bike bike = bikeRepository.findById(bikeUser.getBike().getId());
        if (bike == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Bike not found").build();
        }

        BikeService service = bsrepo.findById(addServiceHistoryDTO.serviceId());
        System.out.println(addServiceHistoryDTO.serviceId());
        if (service == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Service not found").build();
        }

        // Erstelle den History-Eintrag
        bikeserviceHistory.setService(service); // Setze das Service

        // Speichere die Historie
        bshrepo.persist(bikeserviceHistory);

        return Response.ok("History added").build();
    }
}
