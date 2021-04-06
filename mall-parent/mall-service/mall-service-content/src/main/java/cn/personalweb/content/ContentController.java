package cn.personalweb.content;


import cn.personalweb.content.service.ContentService;
import cn.personalweb.content.pojo.Content;
import entry.Result;
import entry.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/content")
@CrossOrigin
public class ContentController {

    @Autowired
    private ContentService contentService;

    /***
     * 根据categoryId查询广告集合
     */
    @GetMapping(value = "/list/category/{id}")
    public Result<List<Content>> findByCategory(@PathVariable Long id){
        //根据分类ID查询广告集合
        List<Content> contents = contentService.findByCategory(id);
        return new Result<List<Content>>(true, StatusCode.OK,"查询成功！",contents);
    }
}
