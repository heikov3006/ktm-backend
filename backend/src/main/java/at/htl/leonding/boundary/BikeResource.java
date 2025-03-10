package at.htl.leonding.boundary;

import at.htl.leonding.dto.AddServiceHistoryDTO;
import at.htl.leonding.dto.BikeHistoryDTO;
import at.htl.leonding.dto.BikeUserAndServiceDTO;
import at.htl.leonding.dto.GetAllByFinDTO;
import at.htl.leonding.model.Bike;
import at.htl.leonding.model.BikeService;
import at.htl.leonding.model.BikeUser;
import at.htl.leonding.model.BikeserviceHistory;
import at.htl.leonding.repository.BikeRepository;
import at.htl.leonding.repository.BikeServiceRepository;
import at.htl.leonding.repository.BikeserviceHistoryRepository;
import at.htl.leonding.repository.UserBikeRepository;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("maintenance")
public class BikeResource {

    private final String MAILGUN_API_KEY = "9ac534cff650651e7c46731ecd838553-0920befd-d0f16de6";

    @Inject
    BikeserviceHistoryRepository bshrepo;

    @Inject
    BikeServiceRepository bsrepo;

    @Inject
    UserBikeRepository ubrepo;

    @Inject
    BikeRepository bikeRepository;

    @Path("getBikeserviceHistory/{email}/{fin}/{serviceId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBikeserviceHistory(@PathParam("email") String email, @PathParam("fin") String fin, @PathParam("serviceId") Long serviceId) {
        return Response.ok(
                 bshrepo.findByBikeUserAndService(email, fin, serviceId)
        ).build();
    }

    @Path("getBikeserviceHistory/last/{email}/{fin}/{serviceId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastBikeserviceHistory(@PathParam("email") String email, @PathParam("fin") String fin, @PathParam("serviceId") Long serviceId) {
        List<BikeUserAndServiceDTO> returnList = bshrepo.findByBikeUserAndService(email, fin, serviceId);
        if (returnList.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No history found").build();
        }
        return Response.ok(returnList.getLast()).build();
    }

    @Path("getBikeserviceHistory/fin/{fin}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBikeServiceHistoryByFin(@PathParam("fin") String fin) {
        GetAllByFinDTO getAllByFinDTO = bshrepo.getByFin(fin);
        if (getAllByFinDTO == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No history found").build();
        }
        return Response.ok(getAllByFinDTO).build();
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
        bikeserviceHistory.setBikeId(bikeUser.getBike().getId());
        Bike bike = bikeRepository.findById(bikeUser.getBike().getId());
        if (bike == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Bike not found").build();
        }

        BikeService service = bsrepo.findById(addServiceHistoryDTO.serviceId());
        System.out.println(addServiceHistoryDTO.serviceId());
        if (service == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Service not found").build();
        }

        int interval = service.getInterval();
        int kmAtService = addServiceHistoryDTO.km();
        Long actualKm = bikeUser.getKm();
        int restKm = interval - (actualKm.intValue()-kmAtService);

        // Erstelle den History-Eintrag
        bikeserviceHistory.setService(service); // Setze das Service
        bikeserviceHistory.setFin(bikeUser.getFin()); // Setze die FIN

        // Speichere die Historie
        bshrepo.persist(bikeserviceHistory);

        try {
            HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/sandboxe50a7d82c26c4fed88697d548556ced8.mailgun.org/messages")
                    .basicAuth("api", MAILGUN_API_KEY)
                    .queryString("from", "KTM MAINTENANCE <maintenance@ktm.com>")
                    .queryString("to", bikeUser.getUser().getEmail())
                    .queryString("subject", "Service " + service.getTitle() + " for your " + bike.getBrand() + ' ' + bike.getModel())
                    .queryString("text", "The service " + service.getTitle() + " for your " + bike.getBrand() + ' ' + bike.getModel() + " has been added to your history. You had " + bikeserviceHistory.getKilometersAtService() + " km at this time.")
                    .asJson();
            return Response.ok(restKm).build();
        } catch (UnirestException ex) {
            return Response.accepted("exception sending email").build();
        }
    }
}
