package cn.bywin.business.bean.sqloperator;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * @author lhw
 */
@Data
@Entity
@Table(name = "t_olk_model_operator_element")
public class TOlkModelOperatorElementDo {

    @Id
    String elementId;

    String operatorId;

    String creatorId;

    String creatorName;

    Timestamp createTime;

    Timestamp updateTime;


}
