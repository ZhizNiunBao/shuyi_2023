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
@Table(name = "t_olk_model_operator_element_rel")
public class TOlkModelOperatorElementRelDo {

    @Id
    String id;

    String elementId;

    String inCode;

    String inElementId;

    String creatorId;

    String creatorName;

    Timestamp createTime;
}
