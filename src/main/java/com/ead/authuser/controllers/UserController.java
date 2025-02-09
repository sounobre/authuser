package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserRecordDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@RestController
@RequestMapping("/users")
//@CrossOrigin(origins = "http://exemplo.com", maxAge = 3600)
public class UserController {

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(SpecificationTemplate.UserSpec spec,
                                                       Pageable pageable,
                                                       @RequestParam(required = false) UUID courseId) {


        Page<UserModel> userModelPage = (courseId != null) ?
                userService.findAll(SpecificationTemplate.userCourseID(courseId).and(spec), pageable) :
                userService.findAll(spec, pageable);


        if (!userModelPage.isEmpty()){
            for (UserModel user : userModelPage.toList()){
                user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    @GetMapping("/{userID}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userID") UUID userID) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(userID).get());
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") UUID userID) {
        log.debug("DELETE deleteUser received {}", userID);
        userService.delete(userService.findById(userID).get());

        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }

    @PutMapping("/{userID}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userID") UUID userID,
                                             @RequestBody @Validated(UserRecordDto.UserView.UserPut.class)
                                             @JsonView(UserRecordDto.UserView.UserPut.class) UserRecordDto userRecordDto) {

        log.debug("PUT updateUser received {}", userRecordDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                userService.updateUser(userRecordDto, userService.findById(userID).get()
                ));
    }

    @PutMapping("/{userID}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userID") UUID userID,
                                                 @RequestBody @Validated(UserRecordDto.UserView.PasswordPut.class)
                                                 @JsonView(UserRecordDto.UserView.PasswordPut.class) UserRecordDto userRecordDto) {

        log.debug("PUT updatePassword received {}", userID);

        Optional<UserModel> userModelOptional = userService.findById(userID);
        if (!userModelOptional.get().getPassword().equals(userRecordDto.oldPassword())) {
            log.warn("Mismatched old password! userId: {}", userID);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password.");
        }
        userService.updatePassword(userRecordDto, userModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully.");
    }

    @PutMapping("/{userID}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userID") UUID userID,
                                               @RequestBody @Validated(UserRecordDto.UserView.ImagePut.class)
                                               @JsonView(UserRecordDto.UserView.ImagePut.class) UserRecordDto userRecordDto) {
        log.debug("PUT updateImage UserRecordDto received {}", userRecordDto);
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateImage(userRecordDto, userService.findById(userID).get()));
    }
}
