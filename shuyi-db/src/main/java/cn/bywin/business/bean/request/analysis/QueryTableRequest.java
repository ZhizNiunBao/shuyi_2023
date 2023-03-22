package cn.bywin.business.bean.request.analysis;

import lombok.Data;

/**
 * @author zzm
 */
@Data
public class QueryTableRequest {

    /**
     * 模糊条件
     */
    private String qryCond;

    /**
     * 数据类型, 全部pub, 收藏 favorite
     */
    private String dataType;

    /**
     * 时间条件 day week month year
     */
    private String ssj;

    /**
     * 页数
     */
    private Integer currentPage;

    /**
     * 分页大小
     */
    private Integer pageSize;

}
