package cn.personalweb.order.service.impl;

import cn.personalweb.goods.feign.SkuFeign;
import cn.personalweb.order.dao.OrderItemMapper;
import cn.personalweb.order.dao.OrderMapper;
import cn.personalweb.order.pojo.Order;
import cn.personalweb.order.pojo.OrderItem;
import cn.personalweb.order.service.CartService;
import cn.personalweb.order.service.OrderService;
import cn.personalweb.user.feign.UserFeign;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entry.IdWorker;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private UserFeign userFeign;

    /***
     * 添加订单
     * @param order
     * @return
     */
    @Override
    public int add(Order order) {
        //查询出用户的所有购物车
        List<OrderItem> orderItems = cartService.list(order.getUsername());

        //统计计算
        int totalMoney = 0;
        int totalPayMoney=0;
        int num = 0;
        for (OrderItem orderItem : orderItems) {
            //总金额
            totalMoney+=orderItem.getMoney();

            //实际支付金额
            totalPayMoney+=orderItem.getPayMoney();
            //总数量
            num+=orderItem.getNum();
        }
        order.setTotalNum(num);
        order.setTotalMoney(totalMoney);
        order.setPayMoney(totalPayMoney);
        order.setPreMoney(totalMoney-totalPayMoney);

        //其他数据完善
        order.setCreateTime(new Date());
        order.setUpdateTime(order.getCreateTime());
        order.setBuyerRate("0");        //0:未评价，1：已评价
        order.setSourceType("1");       //来源，1：WEB
        order.setOrderStatus("0");      //0:未完成,1:已完成，2：已退货
        order.setPayStatus("0");        //0:未支付，1：已支付，2：支付失败
        order.setConsignStatus("0");    //0:未发货，1：已发货，2：已收货
        order.setId("NO."+idWorker.nextId());
        int count = orderMapper.insertSelective(order);

        //添加订单明细
        for (OrderItem orderItem : orderItems) {
            orderItem.setId("NO."+idWorker.nextId());
            orderItem.setIsReturn("0");
            orderItem.setOrderId(order.getId());
            orderItemMapper.insertSelective(orderItem);
        }
        // 减少库存里面的数据
        skuFeign.decrCount(order.getUsername());
        // 增加用户积分
        userFeign.addPoints(10);
        //清除Redis缓存购物车数据
        redisTemplate.delete("Cart_"+order.getUsername());

        // 成功创建订单后发送到死信队列，设置30分钟后过期，过期后删除订单
        rabbitTemplate.convertAndSend("orderDelayQueue", (Object) order.getId(), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 设置30分钟后过期
                message.getMessageProperties().setExpiration("1000*60*30");
                return message;
            }
        });
        return count;
    }

    /**
     * Order条件+分页查询
     * @param order 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Order> findPage(Order order, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(order);
        //执行搜索
        return new PageInfo<Order>(orderMapper.selectByExample(example));
    }

    /**
     * Order分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Order> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Order>(orderMapper.selectAll());
    }

    /**
     * Order条件查询
     * @param order
     * @return
     */
    @Override
    public List<Order> findList(Order order){
        //构建查询条件
        Example example = createExample(order);
        //根据构建的条件查询数据
        return orderMapper.selectByExample(example);
    }


    /**
     * Order构建查询对象
     * @param order
     * @return
     */
    public Example createExample(Order order){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(order!=null){
            // 订单id
            if(!StringUtils.isEmpty(order.getId())){
                    criteria.andEqualTo("id",order.getId());
            }
            // 数量合计
            if(!StringUtils.isEmpty(order.getTotalNum())){
                    criteria.andEqualTo("totalNum",order.getTotalNum());
            }
            // 金额合计
            if(!StringUtils.isEmpty(order.getTotalMoney())){
                    criteria.andEqualTo("totalMoney",order.getTotalMoney());
            }
            // 优惠金额
            if(!StringUtils.isEmpty(order.getPreMoney())){
                    criteria.andEqualTo("preMoney",order.getPreMoney());
            }
            // 邮费
            if(!StringUtils.isEmpty(order.getPostFee())){
                    criteria.andEqualTo("postFee",order.getPostFee());
            }
            // 实付金额
            if(!StringUtils.isEmpty(order.getPayMoney())){
                    criteria.andEqualTo("payMoney",order.getPayMoney());
            }
            // 支付类型，1、在线支付、0 货到付款
            if(!StringUtils.isEmpty(order.getPayType())){
                    criteria.andEqualTo("payType",order.getPayType());
            }
            // 订单创建时间
            if(!StringUtils.isEmpty(order.getCreateTime())){
                    criteria.andEqualTo("createTime",order.getCreateTime());
            }
            // 订单更新时间
            if(!StringUtils.isEmpty(order.getUpdateTime())){
                    criteria.andEqualTo("updateTime",order.getUpdateTime());
            }
            // 付款时间
            if(!StringUtils.isEmpty(order.getPayTime())){
                    criteria.andEqualTo("payTime",order.getPayTime());
            }
            // 发货时间
            if(!StringUtils.isEmpty(order.getConsignTime())){
                    criteria.andEqualTo("consignTime",order.getConsignTime());
            }
            // 交易完成时间
            if(!StringUtils.isEmpty(order.getEndTime())){
                    criteria.andEqualTo("endTime",order.getEndTime());
            }
            // 交易关闭时间
            if(!StringUtils.isEmpty(order.getCloseTime())){
                    criteria.andEqualTo("closeTime",order.getCloseTime());
            }
            // 物流名称
            if(!StringUtils.isEmpty(order.getShippingName())){
                    criteria.andEqualTo("shippingName",order.getShippingName());
            }
            // 物流单号
            if(!StringUtils.isEmpty(order.getShippingCode())){
                    criteria.andEqualTo("shippingCode",order.getShippingCode());
            }
            // 用户名称
            if(!StringUtils.isEmpty(order.getUsername())){
                    criteria.andLike("username","%"+order.getUsername()+"%");
            }
            // 买家留言
            if(!StringUtils.isEmpty(order.getBuyerMessage())){
                    criteria.andEqualTo("buyerMessage",order.getBuyerMessage());
            }
            // 是否评价
            if(!StringUtils.isEmpty(order.getBuyerRate())){
                    criteria.andEqualTo("buyerRate",order.getBuyerRate());
            }
            // 收货人
            if(!StringUtils.isEmpty(order.getReceiverContact())){
                    criteria.andEqualTo("receiverContact",order.getReceiverContact());
            }
            // 收货人手机
            if(!StringUtils.isEmpty(order.getReceiverMobile())){
                    criteria.andEqualTo("receiverMobile",order.getReceiverMobile());
            }
            // 收货人地址
            if(!StringUtils.isEmpty(order.getReceiverAddress())){
                    criteria.andEqualTo("receiverAddress",order.getReceiverAddress());
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(!StringUtils.isEmpty(order.getSourceType())){
                    criteria.andEqualTo("sourceType",order.getSourceType());
            }
            // 交易流水号
            if(!StringUtils.isEmpty(order.getTransactionId())){
                    criteria.andEqualTo("transactionId",order.getTransactionId());
            }
            // 订单状态,0:未完成,1:已完成，2：已退货
            if(!StringUtils.isEmpty(order.getOrderStatus())){
                    criteria.andEqualTo("orderStatus",order.getOrderStatus());
            }
            // 支付状态,0:未支付，1：已支付，2：支付失败
            if(!StringUtils.isEmpty(order.getPayStatus())){
                    criteria.andEqualTo("payStatus",order.getPayStatus());
            }
            // 发货状态,0:未发货，1：已发货，2：已收货
            if(!StringUtils.isEmpty(order.getConsignStatus())){
                    criteria.andEqualTo("consignStatus",order.getConsignStatus());
            }
            // 是否删除
            if(!StringUtils.isEmpty(order.getIsDelete())){
                    criteria.andEqualTo("isDelete",order.getIsDelete());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        orderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Order
     * @param order
     */
    @Override
    public void update(Order order){
        orderMapper.updateByPrimaryKey(order);
    }


    /**
     * 根据ID查询Order
     * @param id
     * @return
     */
    @Override
    public Order findById(String id){
        return  orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Order全部数据
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    @Override
    public void deleteOrder(String outtradeno) {
        Order order = orderMapper.selectByPrimaryKey(outtradeno);
        order.setUpdateTime(new Date());
        order.setPayStatus("2");
        orderMapper.updateByPrimaryKey(order);

        // TODO  回滚库存
        // 还没写 调用goods微服务
    }

     /**
     * 修改订单
     * @param outtradeno 订单号
     * @param paytime 支付时间
     * @param transactionid 交易流水号
     */
    @Override
    public void updateStatus(String outtradeno, String paytime, String transactionid) throws ParseException {
        // 时间转换
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date payTime = simpleDateFormat.parse(paytime);
        Order order = orderMapper.selectByPrimaryKey(outtradeno);
        order.setPayTime(payTime);
        order.setPayStatus("1");
        order.setTransactionId(transactionid);
        // 修改到数据库
        orderMapper.updateByPrimaryKey(order);
    }
}
