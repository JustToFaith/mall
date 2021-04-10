package cn.personalweb.order.service;

import cn.personalweb.order.pojo.Order;
import com.github.pagehelper.PageInfo;

import java.text.ParseException;
import java.util.List;

public interface OrderService {

    /***
     * Order多条件分页查询
     * @param order
     * @param page
     * @param size
     * @return
     */
    PageInfo<Order> findPage(Order order, int page, int size);

    /***
     * Order分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Order> findPage(int page, int size);

    /***
     * Order多条件搜索方法
     * @param order
     * @return
     */
    List<Order> findList(Order order);

    /***
     * 删除Order
     * @param id
     */
    void delete(String id);

    /***
     * 修改Order数据
     * @param order
     */
    void update(Order order);

    /***
     * 新增Order
     * @param order
     */
    int add(Order order);

    /**
     * 根据ID查询Order
     * @param id
     * @return
     */
     Order findById(String id);

    /***
     * 查询所有Order
     * @return
     */
    List<Order> findAll();

    /**
     * 删除订单
     */
    public void deleteOrder(String outtradeno);

    /**
     * 修改订单
     * @param outtradeno 订单号
     * @param paytime 支付时间
     * @param transactionid 交易流水号
     */
    public void updateStatus(String outtradeno, String paytime, String transactionid) throws ParseException;

}
