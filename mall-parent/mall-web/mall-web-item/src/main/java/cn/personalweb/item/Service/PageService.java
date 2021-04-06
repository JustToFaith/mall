package cn.personalweb.item.Service;

public interface PageService {
    /**
     * 根据商品的ID 生成静态页
     * @param spuId
     */
    public void createPageHtml(long spuId);
}
