package cn.bywin.business.bean.sqloperator;

import java.sql.Time;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "t_sql_operator")
public class TSqlOperatorDo {
    @Id
    private String id;
    private String name;
    private String operatorType;
    private String operatorTypeName;
    private String operatorDesc;
    private String scriptContent;
    private String optStatus;
    private String creatorId;
    private String creatorName;
    private Timestamp createdTime;
    private Timestamp updateTime;
}
