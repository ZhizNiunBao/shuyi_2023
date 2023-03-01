package cn.bywin.business.common.base;

public class BaseController {
	protected final String resultFlag = "success";
	protected final String msgFlag = "msg";
	protected final String defMsg = "操作失败！";
	
	/*protected Map<String, Object> resultMap = new HashMap<>();

	protected void resetResult() {
		resultMap.clear();
		resultMap.put(resultFlag, false);
		resultMap.put(msgFlag, defMsg);
		resultMap.put("rows", new ArrayList<Object>());//
		resultMap.put("total", 0);// 总记录数

	}

	protected void reset(String msg) {
		resultMap.clear();
		resultMap.put(resultFlag, false);
		resultMap.put(msgFlag, defMsg);
		resultMap.put("rows", new ArrayList<Object>());//
		resultMap.put("total", 0);// 总记录数
	}

	protected void setOk(String msg) {
		resultMap.put(resultFlag, true);
		resultMap.put(msgFlag, msg);
	}

	protected void setOk() {
		setOk(null);
	}

	public void setOk(long cnt, List<?> list) {
		setOk(cnt, list, null);
	}

	public void setOk(long cnt, List<?> list, String msg) {
		setOk(msg);
		resultMap.put("rows", list);//
		resultMap.put("total", cnt);// 总记录数
	}

	protected void setErr(String msg) {
		resultMap.put(resultFlag, false);
		resultMap.put(msgFlag, msg);
	}*/
	protected ResponeMap genResponeMap() {
		return new ResponeMap();
	}
	protected ResponeMap genResponeMap(String msg) {
		return new ResponeMap( msg );
	}

}
