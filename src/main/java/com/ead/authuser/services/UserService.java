package com.ead.authuser.services;

import com.ead.authuser.dtos.UserRecordDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.specifications.SpecificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<UserModel> findAll( Pageable pageable);
    Optional<UserModel> findById(UUID userID);
    void delete(UserModel userModel);
    UserModel registerUser(UserRecordDto userRecordDto);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    UserModel updateUser(UserRecordDto userRecordDto, UserModel userModel);
    UserModel updatePassword(UserRecordDto userRecordDto, UserModel userModel);
    UserModel updateImage(UserRecordDto userRecordDto, UserModel userModel);
    Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable);
}
