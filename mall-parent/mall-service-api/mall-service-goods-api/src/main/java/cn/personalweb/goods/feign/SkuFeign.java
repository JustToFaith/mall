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
}
