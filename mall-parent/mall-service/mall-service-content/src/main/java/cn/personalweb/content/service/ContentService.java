package cn.personalweb.content.service;

import cn.personalweb.content.pojo.Content;

import java.util.List;

public interface ContentService {
    /***
     * 根据categoryId查询广告集合
     * @param id
     * @return
     */
    List<Content> findByCategory(Long id);
}
