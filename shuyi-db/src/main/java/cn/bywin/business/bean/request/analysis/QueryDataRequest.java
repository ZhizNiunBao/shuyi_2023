package cn.bywin.business.bean.request.analysis;

import lombok.Data;

/**
 * @author zzm
 */
@Data
public class QueryDataRequest {

    /**
     * 查询条件
     */
    private String qryCond;

    /**
     * 文件夹Id
     */
    private String catalogType;

    /**
     * 页数
     */
    private Integer currentPage;

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 根节点标识
     */
    private String dbsourceId;

}
