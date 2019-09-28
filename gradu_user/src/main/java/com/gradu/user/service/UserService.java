package com.gradu.user.service;

import com.gradu.user.entity.UserEntity;
import service.MPBaseService;

public interface UserService extends MPBaseService<UserEntity> {
    void sendSms(String mobile);

    void add(UserEntity userEntity);

    UserEntity login(UserEntity userEntity);
}
