package cn.personalweb.seckill.controller;

import cn.personalweb.seckill.pojo.SeckillStatus;
import cn.personalweb.seckill.service.SeckillOrderService;
import entry.Result;
import entry.StatusCode;
import entry.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(value = "/seckill/order")
public class SeckillOrderController {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private TokenDecode tokenDecode;

    /****
     * URL:/seckill/order/add
     * 添加订单
     * 调用Service增加订单
     * 匿名访问：anonymousUser
     * @param time
     * @param id
     */
    @RequestMapping(value = "/add")
    public Result add(String time, Long id){
        try {
            //用户登录名
            String username = tokenDecode.getUserInfo().get("username");

            //调用Service增加订单
            Boolean bo = seckillOrderService.add(id, time, username);

            if(bo){
                //抢单成功
                return new Result(true, StatusCode.OK,"抢单成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(true,StatusCode.ERROR,"服务器繁忙，请稍后再试");
    }

    /****
     * 查询抢购
     * @return
     */
    @RequestMapping(value = "/query")
    public Result queryStatus(){
        //获取用户名
        String username = tokenDecode.getUserInfo().get("username");

        //根据用户名查询用户抢购状态
        SeckillStatus seckillStatus = seckillOrderService.queryStatus(username);

        if(seckillStatus!=null){
            return new Result(true,seckillStatus.getStatus(),"抢购状态", seckillStatus);
        }
        //NOTFOUNDERROR =20006,没有对应的抢购数据
        return new Result(false,StatusCode.NOTFOUNDERROR,"没有抢购信息");
    }

}
