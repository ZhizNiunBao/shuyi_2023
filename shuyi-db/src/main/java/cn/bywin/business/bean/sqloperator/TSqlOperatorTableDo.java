package cn.bywin.business.bean.sqloperator;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "t_sql_operator_table")
public class TSqlOperatorTableDo {
    @Id
    private String id;
    private String operatorId;
    private String datasourceId;
    private String sourceTable;
    private String creatorId;
    private String creatorName;
    private Timestamp createdTime;
    private Timestamp updateTime;
}
