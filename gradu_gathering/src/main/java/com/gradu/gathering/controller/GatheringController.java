package com.gradu.gathering.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.gradu.gathering.dto.GatheringDTO;
import com.gradu.gathering.entity.DynamicEntity;
import com.gradu.gathering.entity.GatheringEntity;
import com.gradu.gathering.entity.UserGathEntity;
import com.gradu.gathering.service.GatheringService;
import com.gradu.gathering.service.UserGathService;
import entity.PageData;
import entity.Result;
import entity.StatusCode;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import util.ConvertUtil;
import util.IdWorker;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/gathering")
public class GatheringController {

    @Autowired
    GatheringService gatheringService;

    @Autowired
    UserGathService userGathService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    IdWorker idWorker;

    @GetMapping("/page")
    public Result page(@RequestParam Map<String, Object> params){
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims != null) {
            params.put("userId", claims.getId());
        }
        List<GatheringDTO> dtoList = gatheringService.dtoList(params);
        return new Result(true, StatusCode.OK,"查询成功", dtoList);
    }

    @GetMapping("/{id}")
    public Result get(@PathVariable("id") String id){
        Claims claims = (Claims) request.getAttribute("claims");
        GatheringEntity gatheringEntity = gatheringService.selectById(id);
        GatheringDTO gatheringDTO = ConvertUtil.sourceToTarget(gatheringEntity, GatheringDTO.class);
        if (claims != null) {
            Map<String, Object> params = new HashMap<>(4);
            params.put("userid", claims.getId());
            params.put("gathid", gatheringDTO.getId());
            List<UserGathEntity> userGathEntityList = userGathService.list(params);
            if (CollectionUtil.isNotEmpty(userGathEntityList)) {
                gatheringDTO.setJoin(true);
            } else {
                gatheringDTO.setJoin(false);
            }
        }
        return new Result(true, StatusCode.OK,"查询成功", gatheringDTO);
    }

    @PostMapping
    public Result save (@RequestBody GatheringEntity entity) {
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims == null) {
            return new Result(false, StatusCode.ACCESS_ERROR, "未登录");
        }
        entity.setCreator(claims.getId());
        gatheringService.add(entity);
        return new Result(true, StatusCode.OK, "发布成功");
    }

    @GetMapping("usergath/{gatheringId}")
    public Result join(@PathVariable("gatheringId") String gatheringId) {
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims == null) {
            return new Result(false, StatusCode.ACCESS_ERROR, "未登录");
        }
        GatheringEntity gatheringEntity = gatheringService.selectById(gatheringId);
        if(gatheringEntity == null) {
            return new Result(false, StatusCode.FAIL, "活动已失效");
        }
        UserGathEntity userGathEntity = new UserGathEntity();
        userGathEntity.setGathid(gatheringId);
        userGathEntity.setUserid(claims.getId());
        userGathEntity.setExetime(new Date());
        userGathEntity.setId(String.valueOf(idWorker.nextId()));
        userGathService.insert(userGathEntity);

        DynamicEntity dynamicEntity = new DynamicEntity();
        dynamicEntity.setUserid(claims.getId());
        dynamicEntity.setCreateDate(new Date());
        dynamicEntity.setAction("报名参加了活动");
        dynamicEntity.setContent(gatheringEntity.getName());
        dynamicEntity.setExternalUrl("/gathering/" + gatheringId);

        return new Result(true, StatusCode.OK, "报名成功");
    }
}

