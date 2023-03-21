package cn.bywin.business.bean.request.analysis;

import io.swagger.annotations.ApiImplicitParam;
import lombok.Data;

/**
 * @author zzm
 */
@Data
public class QueryDataRequest {

    private String qryCond;

    private String catalogType;

    private Integer currentPage;

    private Integer pageSize;

    private String dbsourceId;

}
