package cn.personalweb.service.impl;

import cn.personalweb.dao.BrandMapper;
import cn.personalweb.goods.pojo.Brand;
import cn.personalweb.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询所有品牌
     * @return
     */
    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    /**
     * 根据主键id查询
     * @param id
     * @return
     */
    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Brand brand) {
        /**
         * 方法中有Selective会忽略空值
         */
        brandMapper.insertSelective(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void deleteById(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    /**
     * 条件构建, 抽出公共方法
     * @param brand
     * @return
     */
    public Example createExample(Brand brand) {
        // 自定义条件搜索对象 Example
        Example example = new Example(brand.getClass());
        // 条件构造器
        Example.Criteria criteria = example.createCriteria();

        if (brand != null) {
            // brand != null 根据名字模糊搜索 where name like %米%
            if (!StringUtils.isEmpty(brand.getName())) {
                /**
                 * property:数据库的属性名
                 * value:需要条件查询的值
                 */
                criteria.andLike("name","%"+brand.getName()+"%");
            }
            if (!StringUtils.isEmpty(brand.getLetter())) {
                criteria.andEqualTo("letter", brand.getLetter());
            }
        }
        return example;
    }


    @Override
    public List<Brand> findList(Brand brand) {
        Example example = createExample(brand);
        return brandMapper.selectByExample(example);
    }

    @Override
    public PageInfo<Brand> findPage(Integer page, Integer size) {
        /**
         * 分页实现：PageHelper.startPage(page, size)：分页实现，后面的查询紧跟集合查询
         */
        PageHelper.startPage(page, size);
        List<Brand> brands = brandMapper.selectAll();
        return new PageInfo<Brand>(brands);
    }

    @Override
    public PageInfo<Brand> findPage(Brand brand, Integer page, Integer size) {
        PageHelper.startPage(page,size);
        List<Brand> brands = findList(brand);
        return new PageInfo<Brand>(brands);
    }

}
