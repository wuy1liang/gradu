package com.gradu.treehole.service.impl;

import com.gradu.treehole.dao.TreeholeDao;
import com.gradu.treehole.entity.TreeholeEntity;
import com.gradu.treehole.service.TreeholeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import java.util.Date;
import java.util.List;

@Service
public class TreeholeServiceImpl implements TreeholeService {

    @Autowired
    TreeholeDao treeholeDao;

    @Autowired
    IdWorker idWorker;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public List<TreeholeEntity> list() {
        return treeholeDao.findByParentidIsNullAndStateEquals("1");
    }

    @Cacheable(cacheNames = "treehole",key = "#id")
    @Override
    public TreeholeEntity selectById(String id) {
        TreeholeEntity treeholeEntity = treeholeDao.findById(id).get();
        return treeholeEntity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(TreeholeEntity entity) {
        entity.set_id(String.valueOf(idWorker.nextId()));
        entity.setThumbup(0);
        entity.setPublishtime(new Date());
        entity.setVisits(0);
        entity.setShare(0);
        entity.setState("1");
        entity.setComment(0);
        treeholeDao.save(entity);
    }

    @CacheEvict(cacheNames = "treehole",key = "#entity._id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(TreeholeEntity entity) {
        treeholeDao.save(entity);
    }

    @CacheEvict(cacheNames = "treehole",key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        treeholeDao.deleteById(id);
    }

    @Override
    public List<TreeholeEntity> findTreeholeEntityByParentid(String parentid) {
        return treeholeDao.findTreeholeEntitiesByParentid(parentid);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void thumbup(String userid,String treeholeid) {
        TreeholeEntity treeholeEntity = selectById(treeholeid);
        treeholeEntity.setThumbup(treeholeEntity.getThumbup()==null? 1 : treeholeEntity.getThumbup()+1);
        update(treeholeEntity);
        redisTemplate.opsForSet().add("treehole:thumbup:"+treeholeid,userid);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unthumbup(String userid,String treeholeid) {
        TreeholeEntity treeholeEntity = selectById(treeholeid);
        treeholeEntity.setThumbup(treeholeEntity.getThumbup()==null ? 0 : treeholeEntity.getThumbup()-1 );
        update(treeholeEntity);
        redisTemplate.opsForSet().remove("treehole:thumbup:"+treeholeid,userid);
    }

    @Override
    public List<TreeholeEntity> getMyTreehole(String user) {
        return treeholeDao.findTreeholeEntitiesByNickname(user);
    }

}
