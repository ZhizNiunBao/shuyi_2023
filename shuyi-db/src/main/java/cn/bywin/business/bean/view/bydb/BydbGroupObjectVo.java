package cn.bywin.business.bean.view.bydb;


import cn.bywin.business.bean.bydb.TBydbObjectDo;
import lombok.Data;

@Data
public class BydbGroupObjectVo extends TBydbObjectDo {

	private String adminId;

	private String groupId;

	private String groupName;
	private String dbId;
	private String dbName;
	private String dbChnName;
	private String schemaId;
	private String schemaName;
	private String schemaChnName;
	private String objectId;

}
