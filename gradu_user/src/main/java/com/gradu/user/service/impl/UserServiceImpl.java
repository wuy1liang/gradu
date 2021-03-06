package com.gradu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.gradu.user.dao.UserDao;
import com.gradu.user.entity.UserEntity;
import com.gradu.user.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import service.impl.MPBaseServiceImpl;
import util.IdWorker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends MPBaseServiceImpl<UserDao, UserEntity> implements UserService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    IdWorker idWorker;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void add(UserEntity entity) {
        entity.setId(String.valueOf(idWorker.nextId()));
        entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));
        entity.setFollowcount(0);
        entity.setFanscount(0);
        entity.setOnline(0L);
        entity.setRegdate(new Date());
        entity.setLastdate(new Date());
        entity.setUpdatedate(new Date());
        save(entity);
    }

    @Override
    public UserEntity login(UserEntity userEntity) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(userEntity.getMobile()),"mobile",userEntity.getMobile());
        UserEntity one = getOne(wrapper);
        if (one!=null && bCryptPasswordEncoder.matches(userEntity.getPassword(),one.getPassword())){
            return one;
        }
        return null;
    }

    @Override
    public void incFans(String id, int num) {
        UserEntity entity = getById(id);
        entity.setFanscount(entity.getFanscount()+num);
    }

    @Override
    public void incFollow(String id, int num) {
        UserEntity entity = getById(id);
        entity.setFollowcount(entity.getFollowcount()+num);
    }

    @Override
    public void sendSms(String mobile) {
        String randomNumeric = RandomStringUtils.randomNumeric(6);

        redisTemplate.opsForValue().set("randomNumeric:"+mobile,randomNumeric,5, TimeUnit.MINUTES);

        Map<String,Object> params = new HashMap<>();
        params.put("mobile",mobile);
        params.put("randomNumeric",randomNumeric);
        rabbitTemplate.convertAndSend("sms",params);


    }


}
