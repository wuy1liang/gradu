package com.gradu.base.controller;


import com.gradu.base.dto.LabelDTO;
import com.gradu.base.serice.LabelService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/label")
public class LabelController {

    @Autowired
    LabelService labelService;

    /**
     * 标签全部列表
     * @return
     */
    @GetMapping
    public Result getAllLabel(){
        return new Result(true, StatusCode.OK,"查询成功",labelService.getAllLabel());
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getLabelById(@PathVariable("id") String id){
        return new Result(true,StatusCode.OK,"查询成功",labelService.getLabelById(id));
    }

    /**
     * 添加标签
     * @param labelDTO
     * @return
     */
    @PostMapping
    public Result addLabel(@RequestBody LabelDTO labelDTO){
        labelService.addLabel(labelDTO);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /**
     * 修改标签
     * @param labelDTO
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    public Result updateLabel(@RequestBody LabelDTO labelDTO,@PathVariable("id") String id){
        labelDTO.setId(id);
        labelService.updateLabel(labelDTO);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delLabel(@PathVariable("id")String id){
        labelService.delLabelById(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 标签分页
     * @param dto
     * @return
     */
    @PostMapping("/search")
    public Result getSearch(@RequestBody LabelDTO dto){
        List<LabelDTO> labelDTOList = labelService.findSearch(dto);
        return new Result(true,StatusCode.OK,"查询成功",labelDTOList);
    }

    /**
     * 标签分页
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result getSearchPage(@PathVariable("page") int page,@PathVariable("size") int size,LabelDTO dto){
        Page<LabelDTO> pageData = labelService.getLabelPage(page,size,dto);
        PageResult<LabelDTO> pageResult = new PageResult<>();
        pageResult.setRows(pageData.getContent());
        pageResult.setTotal(pageData.getTotalElements());
        return new Result(true,StatusCode.OK,"查询成功");
    }



}
