package cn.personalweb.seckill.controller;

import cn.personalweb.seckill.pojo.SeckillGoods;
import cn.personalweb.seckill.service.SeckillGoodsService;
import entry.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/seckill/goods")
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /*****
     * 获取时间菜单
     * URLL:/seckill/goods/menus
     */
    @RequestMapping(value = "/menus")
    public List<Date> dateMenus(){
        return DateUtil.getDateMenus();
    }

    /****
     * URL:/seckill/goods/list
     * 对应时间段秒杀商品集合查询
     * 调用Service查询数据
     * @param time:2019050716
     */
    @RequestMapping(value = "/list")
    public List<SeckillGoods> list(String time){
        //调用Service查询数据
        return seckillGoodsService.list(time);
    }

    /****
     * URL:/seckill/goods/one
     * 根据ID查询商品详情
     * 调用Service查询商品详情
     * @param time
     * @param id
     */
    @RequestMapping(value = "/one")
    public SeckillGoods one(String time,Long id){
        //调用Service查询商品详情
        return seckillGoodsService.one(time,id);
    }
}
