package at.htl.leonding.boundary;

import at.htl.leonding.dto.AddServiceHistoryDTO;
import at.htl.leonding.dto.BikeHistoryDTO;
import at.htl.leonding.model.BikeserviceHistory;
import at.htl.leonding.repository.BikeServiceRepository;
import at.htl.leonding.repository.BikeserviceHistoryRepository;
import at.htl.leonding.repository.UserBikeRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
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

    @Path("getBikeserviceHistory")
    @GET
    public Response getBikeserviceHistory(BikeHistoryDTO bikeHistoryDTO) {
        return Response.ok(
                bshrepo.findByBikeUserAndService(bikeHistoryDTO.email(), bikeHistoryDTO.bikeId(), bikeHistoryDTO.serviceId())
        ).build();
    }

    @Path("getBikeserviceHistory/last")
    @GET
    public Response getLastBikeserviceHistory(BikeHistoryDTO bikeHistoryDTO) {
        List<BikeserviceHistory> returnList = bshrepo.findByBikeUserAndService(bikeHistoryDTO.email(), bikeHistoryDTO.bikeId(), bikeHistoryDTO.serviceId());
        return Response.ok(returnList.getLast()).build();
    }

    @Path("addServiceHistory")
    @POST
    @Transactional
    public Response addServiceHistory(AddServiceHistoryDTO addServiceHistoryDTO) {
        BikeserviceHistory bikeserviceHistory = new BikeserviceHistory();
        bikeserviceHistory.setBikeUser(ubrepo.getBikeUserByMailAndBikeId(addServiceHistoryDTO.email(), addServiceHistoryDTO.bikeId()));
        bikeserviceHistory.setService(bsrepo.findById(addServiceHistoryDTO.serviceId()));
        bikeserviceHistory.setServiceDate(LocalDate.now());
        bikeserviceHistory.setKilometersAtService(addServiceHistoryDTO.km());
        bshrepo.persist(bikeserviceHistory);
        return Response.ok("history added").build();
    }
}
