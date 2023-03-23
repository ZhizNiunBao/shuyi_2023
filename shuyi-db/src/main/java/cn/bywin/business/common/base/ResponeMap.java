package cn.bywin.business.common.base;

import cn.bywin.business.common.enums.ErrorCodeConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponeMap implements Serializable {
	public final String resultFlag = "success";
	public final String msgFlag = "msg";
	private final String defMsg = "操作失败！";
	private final String codeFlag = "code";
	private final Map<String, Object> resultMap = new HashMap<>();

	private long startSpend = System.currentTimeMillis();

	public ResponeMap() {
		resultMap.clear();
		resultMap.put(resultFlag, false);
		resultMap.put(msgFlag, defMsg);
		resultMap.put(codeFlag, ErrorCodeConstants.SUCCESS.getCode());
		resultMap.put("data", new ArrayList<>());//
		resultMap.put("total", 0);// 总记录数
	}

	public ResponeMap( String msg ) {
		resultMap.clear();
		resultMap.put(resultFlag, false);
		resultMap.put(msgFlag, msg);
		resultMap.put(codeFlag, ErrorCodeConstants.SUCCESS.getCode());
		resultMap.put("data", new ArrayList<>());//
		resultMap.put("total", 0);// 总记录数
	}

	public ResponeMap initSingleObject(  ) {
		resultMap.put("data", null);
		return this;
	}
	 

	public ResponeMap setOk(String msg) {
		resultMap.put(resultFlag, true);
		resultMap.put(msgFlag, msg);
		return this;
	}

	public ResponeMap setOk() {
		return setOk(null);
	}

	public ResponeMap setOk(long cnt, List<?> list) {
		return setOk(cnt, list, null);
	}

 	public ResponeMap setPageInfo(Integer pageSize,Integer page) {
		resultMap.put("pageSize", pageSize);//
		resultMap.put("page", page);//
		resultMap.put("isPage", true);//
		return this;
	}

	public ResponeMap setOk(long cnt, List<?> list, String msg) {
		setOk(msg);
		resultMap.put("data", list);//
		resultMap.put("total", cnt);// 总记录数
		return this;
	}

	public ResponeMap setErr(String msg) {
		resultMap.put(resultFlag, false);
		resultMap.put(msgFlag, msg);
		resultMap.put(codeFlag, ErrorCodeConstants.ERROR.getCode());
		return this;
	}

	public ResponeMap setDebugeInfo(String msg) {
		resultMap.put("debugInfo", msg);
		return this;
	}


	public ResponeMap  setSingleOk(Object  data, String msg) {
		resultMap.put("data", data);
		setOk(msg);
		return this;
	}

	public ResponeMap put(String key,Object value) {
		resultMap.put( key, value);
		return this;
	}

	public String getResultFlag() {
		return resultFlag;
	}

	public String getMsgFlag() {
		return msgFlag;
	}

	public String getDefMsg() {
		return defMsg;
	}

	public Map<String, Object> getResultMap() {
		long spendSec = System.currentTimeMillis() - startSpend ;
		resultMap.put("a_spend_time_a", "耗时："+(spendSec/1000.0)+"秒");
		return resultMap;
	}

}
