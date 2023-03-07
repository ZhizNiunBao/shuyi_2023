package cn.bywin.business.bean.view.bydb;


import cn.bywin.business.bean.bydb.TBydbDataNodeDo;
import cn.bywin.business.bean.bydb.TBydbFieldDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.federal.FDataApproveDo;
import lombok.Data;

import java.util.List;

@Data
public class BydbObjectFieldsVo extends TBydbObjectDo{
	 List<TBydbFieldDo> fieldList;
	 List<TBydbDataNodeDo> dataNodeList;
	 List<FDataApproveDo> approveList;
}
