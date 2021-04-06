package cn.personalweb.content.service.impl;

import cn.personalweb.content.dao.ContentMapper;
import cn.personalweb.content.pojo.Content;
import cn.personalweb.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentMapper contentMapper;

    @Override
    public List<Content> findByCategory(Long id) {
        Content content = new Content();
        content.setCategoryId(id);
        content.setStatus("1");
        return contentMapper.select(content);
    }
}
