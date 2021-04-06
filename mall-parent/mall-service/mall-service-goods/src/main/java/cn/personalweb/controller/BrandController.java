package cn.personalweb.controller;

import cn.personalweb.goods.pojo.Brand;
import cn.personalweb.service.BrandService;
import com.github.pagehelper.PageInfo;
import entry.Result;
import entry.StatusCode;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.Inet4Address;
import java.sql.Struct;
import java.util.List;

@RestController
@RequestMapping(value = "/brand")
/**
 * 跨域
 */
@CrossOrigin
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    public Result<List<Brand>> findAll(){
        List<Brand> brands = brandService.findAll();
        return new Result<List<Brand>>(true, StatusCode.OK, "查询所有品牌集合成功", brands);
    }

    @GetMapping(value = "/{id}")
    public Result<Brand> findById(@PathVariable(value = "id") Integer id) {
        Brand brand = brandService.findById(id);
        return new Result<>(true, StatusCode.OK, "根据id查询品牌集合成功", brand);
    }

    @PostMapping
    public Result addBrand(@RequestBody Brand brand) {
        brandService.add(brand);
        return new Result(true, StatusCode.OK, "增加品牌成功");
    }

    @PutMapping(value = "/{id}")
    public Result update(@PathVariable(value = "id") Integer id, @RequestBody Brand brand) {
        brand.setId(id);
        brandService.update(brand);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    @DeleteMapping(value = "/{id}")
    public Result deleteById(@PathVariable(value = "id") Integer id) {
        brandService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    @PostMapping(value = "/search")
    public Result<List<Brand>> findList(@RequestBody Brand brand) {
        List<Brand> brands = brandService.findList(brand);
        return new Result<List<Brand>>(true, StatusCode.OK, "条件查询", brands);
    }

    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageInfo<Brand>> findPage(@PathVariable(value = "page") Integer page,
                                    @PathVariable(value = "size") Integer size) {
        PageInfo<Brand> pageInfo = brandService.findPage(page, size);
        return new Result<PageInfo<Brand>>(true, StatusCode.OK, "分页查询成功", pageInfo);
    }

    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo<Brand>> findPage(Brand brand,
                                            @PathVariable(value = "page") Integer page,
                                            @PathVariable(value = "size") Integer size) {
        PageInfo<Brand> pageInfo = brandService.findPage(brand, page, size);
        return new Result<PageInfo<Brand>>(true, StatusCode.OK, "分页查询成功", pageInfo);
    }

    @GetMapping(value = "/category/{id}")
    public Result<List<Brand>> findBrandByCategory(@PathVariable(value = "id") Integer categoryId) {
        List<Brand> brands = brandService.findByCategory(categoryId);
        return new Result<List<Brand>>(true, StatusCode.OK, "查询成功", brands);
    }
}
