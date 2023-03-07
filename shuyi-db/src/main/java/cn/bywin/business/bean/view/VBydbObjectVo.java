package cn.bywin.business.bean.view;


import cn.bywin.business.bean.bydb.TBydbObjectDo;
import lombok.Data;

import javax.persistence.Column;

@Data
public class VBydbObjectVo extends TBydbObjectDo {
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
