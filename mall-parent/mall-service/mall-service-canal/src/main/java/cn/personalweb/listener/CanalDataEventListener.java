package cn.personalweb.listener;


import cn.personalweb.item.feign.PageFeign;
import cn.personalweb.content.feign.ContentFeign;
import cn.personalweb.content.pojo.Content;
import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;
import entry.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@CanalEventListener
public class CanalDataEventListener {

    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private PageFeign pageFeign;

    //字符串
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //自定义数据库的 操作来监听
    //destination = "example"
    @ListenPoint(destination = "example",
            schema = "changgou_goods",
            table = {"tb_content", "tb_content_category"},
            eventType = {
                    CanalEntry.EventType.UPDATE,
                    CanalEntry.EventType.DELETE,
                    CanalEntry.EventType.INSERT})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //1.获取列名 为category_id的值
        String categoryId = getColumnValue(eventType, rowData);
        //2.调用feign 获取该分类下的所有的广告集合
        Result<List<Content>> categoryresut = contentFeign.findByCategory(Long.valueOf(categoryId));
        List<Content> data = categoryresut.getData();
        //3.使用redisTemplate存储到redis中
        System.out.println("调用了：--"+data);
        stringRedisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(data));
    }

    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String categoryId = "";
        //判断 如果是删除  则获取beforlist
        if (eventType == CanalEntry.EventType.DELETE) {
            for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                if (column.getName().equalsIgnoreCase("id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        } else {
            //判断 如果是添加 或者是更新 获取afterlist
            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                if (column.getName().equalsIgnoreCase("id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        }
        return categoryId;
    }


    /**
     * 监听静态页面的变化，如果数据库发生了变化，就改变静态页面
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example",
            schema = "changgou_goods",
            table = {"tb_spu"},
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT, CanalEntry.EventType.DELETE})
    public void onEventCustomSpu(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        //判断操作类型
        if (eventType == CanalEntry.EventType.DELETE) {
            String spuId = "";
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                if (column.getName().equals("id")) {
                    spuId = column.getValue();//spuid
                    break;
                }
            }
            //todo 删除静态页

        }else{
            //新增 或者 更新
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            String spuId = "";
            for (CanalEntry.Column column : afterColumnsList) {
                if (column.getName().equals("id")) {
                    spuId = column.getValue();
                    break;
                }
            }
            //更新 生成静态页
            pageFeign.createHtml(Long.valueOf(spuId));
        }
    }

    /***
     * 增加数据监听
     * @param eventType
     * @param rowData
     */
//    @InsertListenPoint
//    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
//        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
//    }

    /***
     * 修改数据监听
     * @param rowData
     */
//    @UpdateListenPoint
//    public void onEventUpdate(CanalEntry.RowData rowData) {
//        System.out.println("UpdateListenPoint");
//        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
//    }

    /***
     * 删除数据监听
     * @param eventType
     */
//    @DeleteListenPoint
//    public void onEventDelete(CanalEntry.EventType eventType) {
//        System.out.println("DeleteListenPoint");
//    }

    /***
     * 自定义数据修改监听
     * @param eventType
     * @param rowData
     */
//    @ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content_category", "tb_content"}, eventType = CanalEntry.EventType.UPDATE)
//    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
//        System.err.println("DeleteListenPoint");
//        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
//    }
}
