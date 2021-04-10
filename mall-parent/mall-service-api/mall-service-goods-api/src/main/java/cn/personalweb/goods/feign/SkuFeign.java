package cn.personalweb.goods.feign;

import cn.personalweb.goods.pojo.Sku;
import entry.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "goods")
@RequestMapping(value = "/sku")
public interface SkuFeign {
    /***
     * 根据审核状态查询Sku
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable String status);

    /**
     * 根据条件搜索
     * @param sku
     * @return
     */
    @PostMapping(value = "/search")
    public Result<List<Sku>> findList(@RequestBody(required = false) Sku sku);

    /***
     * 根据ID查询Sku数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable Long id);

    /***
     * 库存递减
     * @param username
     * @return
     */
    @PostMapping(value = "/decr/count")
    Result decrCount(@RequestParam(value = "username") String username);
}
