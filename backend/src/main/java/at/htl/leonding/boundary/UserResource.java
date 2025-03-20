package at.htl.leonding.boundary;

import at.htl.leonding.dto.*;
import at.htl.leonding.model.Bike;
import at.htl.leonding.model.BikeUser;
import at.htl.leonding.model.User;
import at.htl.leonding.repository.BikeRepository;
import at.htl.leonding.repository.BikeserviceHistoryRepository;
import at.htl.leonding.repository.UserBikeRepository;
import at.htl.leonding.repository.UserRepository;
import at.htl.leonding.sec.Encryption;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;

@Path("maintenance")
public class UserResource {

    @Inject
    Mailer mailer;

    @Inject
    UserRepository userRepository;
    @Inject
    BikeRepository bikeRepository;
    @Inject
    UserBikeRepository userBikeRepository;
    @Inject
    BikeserviceHistoryRepository bikeserviceHistoryRepository;

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
                return Response.status(450, "Passwords do not match").build();
            } else {
                try {
                    String hashedPassword = Encryption.generateSaltedHash(userCreationDTO.password().toCharArray());
                    User user = new User(userCreationDTO.firstname(), userCreationDTO.lastname(), userCreationDTO.email(), hashedPassword);
                    userRepository.persist(user);
                    sendMail(user.getEmail(), "Registration successful", "You have successfully registered to KTM Maintenance!");
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
                sendMail(user.getEmail(), "Profile updated", "Your profile has been updated successfully!");
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
            List<BikeUser> bikeUsers = userBikeRepository.getBikeUserByEmail(userToDelete.getEmail());
            bikeUsers.forEach(bikeUser -> {
                System.out.println(bikeUser.toString());
                bikeserviceHistoryRepository.deleteEmail(bikeUser.getUser().getEmail());
            });
            userBikeRepository.deleteByEmail(userToDelete.getEmail());
            userRepository.deleteByEmail(userToDelete.getEmail());
            sendMail(userToDelete.getEmail(), "Account deleted", "Your account has been deleted successfully!");
            return Response.ok("Email sent").build();
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
        String fin = addBikeDTO.fin();
        if((fin.length() != 17 || fin.isBlank() || fin.contains("O") || fin.contains("I") || fin.contains("Q")) || !fin.matches("[0-9A-Z]+")) {
            return Response.accepted("FIN is invalid").build();
        }
        if (user != null && bike != null) {
            try {
                BikeUser bikeUser = new BikeUser(addBikeDTO.fin(), user, bike, addBikeDTO.km(), addBikeDTO.imgUrl());
                userBikeRepository.persist(bikeUser);
                sendMail(user.getEmail(), "Bike added", "Bike " + bike.getBrand() + ' ' + bike.getModel() + " with FIN " + addBikeDTO.fin() + " has been added to your account!");
                return Response.ok(getUserBikeDTO(user)).build();
            } catch (Exception ex) {
                return Response.accepted("Bike with this FIN is already used").build();
            }
        } else {
            return Response.accepted("No such user or bike").build();
        }
    }

    @POST
    @Path("deleteBikeFromUser")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteBikeFromUser(AddBikeDTO addBikeDTO) {
        User user = userRepository.getUserByEmail(addBikeDTO.email());
        Bike bike = bikeRepository.findById(addBikeDTO.bikeId());
        if (user != null && bike != null) {
            bikeserviceHistoryRepository.deleteEmail(addBikeDTO.email());
            userBikeRepository.deleteByEmailAndFin(addBikeDTO.email(), addBikeDTO.fin());
            sendMail(user.getEmail(), "Bike deleted", "Bike " + bike.getBrand() + ' ' + bike.getModel() + " with FIN " + addBikeDTO.fin() + " has been deleted from your account!");
            return Response.ok(getUserBikeDTO(user)).build();
        }
        return Response.accepted("No such user or bike").build();
    }

    private UserBikeDTO getUserBikeDTO(User user){
        List<BikeUser> bikeUserList = userBikeRepository.getBikeUserByEmail(user.getEmail());
        Stream<BikeDTO> bikeDTOList = bikeUserList.stream().map(bikeUserTemp -> {return new BikeDTO(bikeUserTemp.getBike(), bikeUserTemp.getKm(), bikeUserTemp.getFin(), bikeUserTemp.getImgUrl());});
        return new UserBikeDTO(user, bikeDTOList.toList());
    }

    private void sendMail(String email, String subject, String text) {
        try {
            mailer.send(new Mail().setText(text).setSubject(subject).setTo(List.of(email)).setFrom("KTM MAINTENANCE <maintenance@ktm.com>"));
        } catch (Exception ex) {
            System.out.println("exception sending email");
        }
    }
}
