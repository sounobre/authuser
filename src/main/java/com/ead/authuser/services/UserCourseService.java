package com.ead.authuser.services;

import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface UserCourseService {
    boolean existsByUserAndCourseId(UserModel userModel,  UUID uuid);

    UserCourseModel save(UserCourseModel userCourseModel);
}
