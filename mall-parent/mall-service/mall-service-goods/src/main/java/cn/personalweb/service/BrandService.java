package cn.personalweb.service;

import cn.personalweb.goods.pojo.Brand;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;

import java.util.List;

public interface BrandService {

    /**
     * 查询所有品牌
     * @return
     */
    List<Brand> findAll();

    Brand findById(Integer id);

    void add(Brand brand);

    void update(Brand brand);

    void deleteById(Integer id);

    /**
     * 根据品牌信息多条件搜索
     * @param brand
     * @return
     */
    List<Brand> findList(Brand brand);

    /**
     * 分页查询
     * @param page 页数
     * @param size 每一页大小
     * @return
     */
    PageInfo<Brand> findPage(Integer page, Integer size);

    /**
     * 条件搜索+分页
     * @param brand
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> findPage(Brand brand, Integer page, Integer size);

    /**
     * 根据分类id查询品牌集合
     * @param cateforyId
     * @return
     */
    List<Brand> findByCategory(Integer cateforyId);
}
