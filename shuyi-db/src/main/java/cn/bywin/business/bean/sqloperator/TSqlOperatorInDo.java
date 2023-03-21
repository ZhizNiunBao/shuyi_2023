package cn.bywin.business.bean.sqloperator;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "t_sql_operator_in")
public class TSqlOperatorInDo {
   @Id
   private String id;
   private String operatorId;
   private String inCode;
   private String inName;
   private String fieldCode;
   private String fieldName;
   private String fieldDesc;
   private String creatorId;
   private String creatorName;
   private Timestamp createdTime;
   private Timestamp updateTime;

}
