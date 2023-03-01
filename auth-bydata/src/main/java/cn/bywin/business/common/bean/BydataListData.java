package cn.bywin.business.common.bean;

import lombok.Data;

import java.util.List;

/**
 * 中台接口列表数据格式
 * @author zzm
 */
@Data
public class BydataListData<T> {

    private Integer currentPage;

    private Integer totalPage;

    private List<T> dataList;

}
