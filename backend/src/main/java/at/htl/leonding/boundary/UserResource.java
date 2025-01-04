package at.htl.leonding.boundary;

import at.htl.leonding.dto.*;
import at.htl.leonding.model.Bike;
import at.htl.leonding.model.BikeUser;
import at.htl.leonding.model.User;
import at.htl.leonding.repository.BikeRepository;
import at.htl.leonding.repository.UserBikeRepository;
import at.htl.leonding.repository.UserRepository;
import at.htl.leonding.sec.Encryption;
import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("maintenance")
public class UserResource {

    private final String MAILGUN_API_KEY = "9ac534cff650651e7c46731ecd838553-0920befd-d0f16de6";

    @Inject
    UserRepository userRepository;
    @Inject
    BikeRepository bikeRepository;
    @Inject
    UserBikeRepository userBikeRepository;

    @GET
    public List<User> all() {
        return userRepository.findAll().list();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response register(UserCreationDTO userCreationDTO) {
        if (!userRepository.userWithEmailAlreadyExisting(userCreationDTO.email())) {
            if (!userCreationDTO.password().equals(userCreationDTO.passwordConfirm())) {
                User user;
                return Response.status(450, "Passwords do not match").build();
            } else {
                try {
                    String hashedPassword = Encryption.generateSaltedHash(userCreationDTO.password().toCharArray());
                    User user = new User(userCreationDTO.firstName(), userCreationDTO.lastName(), userCreationDTO.email(), hashedPassword);
                    userRepository.persist(user);
                    return Response.ok(getUserBikeDTO(user)).build();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return Response.accepted("Exception at persisting").build();
            }
        } else {
            return Response.accepted("User already existing! Please log in").build();
        }
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(UserLoginDTO userLoginDTO) {
        User user = userRepository.getUserByEmail(userLoginDTO.email());
        if (user != null) {
            String actualPW = user.getPassword();
            boolean isVerified = false;
            try {
                isVerified = Encryption.verifyPassword(userLoginDTO.password().toCharArray(), actualPW);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                return Response.status(497, "encryption exception").build();
            }
            if (isVerified) {
                return Response.ok(getUserBikeDTO(user)).build();
            } else {
                return Response.status(498, "Wrong password").build();
            }
        } else {
            return Response.ok("No such user! Please register now.").status(499).build();
        }
    }

    @POST
    @Path("editProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response editProfile(UserEditingDTO userEditingDTO) {
        User user = userRepository.getUserByEmail(userEditingDTO.email());
        if (user != null) {
            String actualPW = user.getPassword();
            boolean isVerified = false;
            try {
                isVerified = Encryption.verifyPassword(userEditingDTO.password().toCharArray(), actualPW);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                return Response.status(497, "encryption exception").build();
            }
            if (isVerified) {
                try {
                    String hashedPassword = Encryption.generateSaltedHash(userEditingDTO.user().getPassword().toCharArray());
                    userEditingDTO.user().setPassword(hashedPassword);
                } catch (Exception ex) {
                    return Response.ok("failed generating salted hash password").status(300).build();
                }
                User updatedUser = userRepository.updateUser(userEditingDTO.email(), userEditingDTO.user());
                return Response.ok(updatedUser).build();
            } else {
                return Response.status(498, "Wrong password").build();
            }
        }
        return Response.ok("No such User").status(300).build();
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    //https://app.mailgun.com/mg/sending/sandboxe50a7d82c26c4fed88697d548556ced8.mailgun.org/logs?dateRange=%7B%22endDate%22%3A%222024-12-22T22%3A59%3A59.999Z%22%2C%22startDate%22%3A%222024-12-14T23%3A00%3A00.000Z%22%7D&fields=Event%2CSender%2CRecipient%2CDelivery+Status+Message&page=1&search=
    public Response deleteUser(UserDeleteDTO userDeleteDTO) {
        User userToDelete = userRepository.getUserByEmail(userDeleteDTO.email());
        String actualPW = userToDelete.getPassword();
        boolean isVerified = false;
        try {
            isVerified = Encryption.verifyPassword(userDeleteDTO.password().toCharArray(), actualPW);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return Response.accepted("exception during verifying user").build();
        }
        if (isVerified) {
            userBikeRepository.deleteByEmail(userToDelete.getEmail());
            userRepository.delete(userToDelete);
            try {
                HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/sandboxe50a7d82c26c4fed88697d548556ced8.mailgun.org/messages")
                        .basicAuth("api", MAILGUN_API_KEY)
                        .queryString("from", "KTM MAINTENANCE <maintenance@ktm.com>")
                        .queryString("to", userDeleteDTO.email())
                        .queryString("subject", "Deleting your account")
                        .queryString("text", "Your account has been deleted successfully!")
                        .asJson();
                return Response.ok("Email sent").build();
            } catch (UnirestException ex) {
                return Response.accepted("exception sending email").build();
            }
        }
        return Response.accepted("Wrong password").build();
    }

    @POST
    @Path("addBike")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addBike(AddBikeDTO addBikeDTO) {
        User user = userRepository.getUserByEmail(addBikeDTO.email());
        Bike bike = bikeRepository.findById(addBikeDTO.bikeId());
        if (user != null && bike != null) {
            BikeUser bikeUser = new BikeUser(addBikeDTO.bikeId().toString(), user, bike, addBikeDTO.km());
            userBikeRepository.persist(bikeUser);
            return Response.ok(getUserBikeDTO(user)).build();
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("getUserBikes")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserBikes(@QueryParam("email") String email) {
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
        UserBikeDTO userBikeDTO = getUserBikeDTO(user);
        return Response.ok(userBikeDTO).build();
    }


    @POST
    @Path("deleteBikeFromUser")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public User deleteBikeFromUser(AddBikeDTO addBikeDTO) {
        User user = userRepository.getUserByEmail(addBikeDTO.email());
        Bike bike = bikeRepository.findById(addBikeDTO.bikeId());
        if (user != null && bike != null) {
            userBikeRepository.deleteByEmail(addBikeDTO.email());
        }
        return user;
    }

    private UserBikeDTO getUserBikeDTO(User user){
        List<BikeUser> bikeUserList = userBikeRepository.getBikeUserByEmail(user.getEmail());
        Stream<BikeDTO> bikeDTOList = bikeUserList.stream().map(bikeUserTemp -> {return new BikeDTO(bikeUserTemp.getBike(), bikeUserTemp.getKm());});
        return new UserBikeDTO(user, bikeDTOList.toList());
    }
}
