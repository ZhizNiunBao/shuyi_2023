package cn.bywin.business.bean.view.olk;


import cn.bywin.business.bean.olk.TOlkDataNodeDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.view.federal.FDataApproveVo;
import lombok.Data;

import java.util.List;

@Data
public class OlkObjectWithFieldsVo extends TOlkObjectDo {
	private String grantUserId;
	private String grantNodeId;
	 List<TOlkFieldDo> fieldList;
	 List<TOlkDataNodeDo> dataNodeList;
	List<FDataApproveVo> approveList;
}
