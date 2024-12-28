package at.htl.leonding.boundary;

import at.htl.leonding.dto.GetServiceByBikeDTO;
import at.htl.leonding.repository.BikeServiceRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("maintenance")
public class ServiceResource {

    @Inject
    BikeServiceRepository bsrepo;

    @GET
    @Path("getServicesByBike")
    public Response getServiceByBike(GetServiceByBikeDTO getServiceByBikeDTO){
        return Response.ok(bsrepo.getServicesByBikeId(getServiceByBikeDTO.bikeId())).build();
    }
}
