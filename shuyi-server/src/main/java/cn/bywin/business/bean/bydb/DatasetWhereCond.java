package cn.bywin.business.bean.bydb;

import cn.bywin.business.common.util.JsonUtil;
import java.util.List;
import lombok.Data;

@Data
public class DatasetWhereCond {


    private String id;

    private String pid;

    private String fieldId1;

    private String fieldId2;

    private String field1;

    private String field2;

    private String function;

    private String relation;

    private List<String> params;

    List<DatasetWhereCond> conditions;

    public String impInfo( String type ){
        return "datatype:"+type+"\r\n"+ JsonUtil.toJson( this );
    }

}
