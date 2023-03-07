package cn.bywin.business.bean.view.olk;



import cn.bywin.business.bean.olk.TOlkObjectDo;
import lombok.Data;

@Data
public class VOlkObjectVo extends TOlkObjectDo {
	//private String adminId;
	private Integer userObjPriv;
	private Integer userPrivGrant;
	private String itemId;
	private String catalogType;
	private String pItemId;
	private String itemName;
	private String dbId;
	private String dbChnName;
	private String dbName;
	private String schemaId;
	private String schemaChnName;
	private String schemaName;
//	private String objectId;
	private String nodePartyName;
	private String ownerUserId;
	private String ownerNodeId;
	private String scatalog;
	private Integer usedCnt;
}
