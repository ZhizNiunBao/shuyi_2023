package cn.bywin.business.common.util;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;


public class PageBeanWrapper {
	private static final Logger logger = LoggerFactory.getLogger(PageBeanWrapper.class);

	private Map<String,String> paramap = new HashMap<String,String>(); //查询条件对应值

	//private Map<String,Object> datamap = new HashMap<String,Object>();//查询条件对应名称

	private List<HttpReq> paraList = new ArrayList<HttpReq>();
	//private HttpRequest request ;

	public PageBeanWrapper() {
	}

	/**
	 * 构造函数
	 * @param bean
	 * @param hru
	 * @param prePara
	 */
	public PageBeanWrapper(Object bean , HttpRequestUtil hru, String prePara) {
		BeanWrapper bw = new BeanWrapperImpl(bean);
		this.setPageparameter(bw, hru.getReq(),prePara,hru.getJson());
	}

	public PageBeanWrapper(Object bean , HttpRequestUtil hru) {
		BeanWrapper bw = new BeanWrapperImpl(bean);
		this.setPageparameter(bw, hru.getReq(),"",hru.getJson());
	}

	private PageBeanWrapper(Object bean , HttpServletRequest request) {
		BeanWrapper bw = new BeanWrapperImpl(bean);
		this.setPageparameter(bw, request,"",null);
	}

	private PageBeanWrapper(Object bean, HttpServletRequest request, String prePara, JsonObject jsonObject)  {
		BeanWrapper bw = new BeanWrapperImpl(bean);
		this.setPageparameter(bw, request,prePara,jsonObject);
	}


	protected void paraParameter( HttpServletRequest request ,JsonObject jsonObject)
	 {
	   //String   paraName ;
	   //String value ;
	   paraList.clear();
	   String code ="utf-8";
	   if( request != null ) {
		   Enumeration<String> paraSet = request.getParameterNames();
		   request.getCharacterEncoding();
		   while( paraSet.hasMoreElements() )
		   {
			   String paraName = (String) paraSet.nextElement() ;
			   String value = request.getParameter(paraName);
		       HttpReq req = new HttpReq();
		       req.setPara(paraName);
		       this.paraList.add(req);
	
		       try{
		    	   if( logger.isDebugEnabled() )
		    		   logger.debug("paraName="+ paraName + ":"+value );
		    	if( "utf-8".equalsIgnoreCase( code )){
		    		req.setValue( value );
		    	}
		    	else if( "gb3212".equalsIgnoreCase( code )){
		    		req.setValue( value );
		    	}
		    	else if( "gbk".equalsIgnoreCase( code )){
		    		req.setValue( value );
		    	}
		    	else{
		    		req.setValue( value );
		    		//req.setValue( new String( value.getBytes("8859_1") , "utf-8" ) );
		    	}
		       }catch(Exception e){e.printStackTrace();}
	     }
	   }
	   if( jsonObject != null && !jsonObject.keySet().isEmpty() ) {
		   Set<String> set =  jsonObject.keySet();
		   for( String paraName:set ) {
			   
		       HttpReq req = new HttpReq();
		       req.setPara(paraName);
		       this.paraList.add(req);
		       if( jsonObject.get(paraName) == null || jsonObject.get(paraName).isJsonNull()
					   || jsonObject.get(paraName).isJsonObject() || jsonObject.get(paraName).isJsonArray()) {
		    	   continue;
		       }
	    	   String value = jsonObject.get(paraName).getAsString();

		       try{
		    	   if( logger.isDebugEnabled() )
		    		   logger.debug("paraName="+ paraName + ":"+value );
		    	if( "utf-8".equalsIgnoreCase( code )){
		    		req.setValue( value );
		    	}
		    	else if( "gb3212".equalsIgnoreCase( code )){
		    		req.setValue( value );
		    	}
		    	else if( "gbk".equalsIgnoreCase( code )){
		    		req.setValue( value );
		    	}
		    	else{
		    		req.setValue( value );
		    		//req.setValue( new String( value.getBytes("8859_1") , "utf-8" ) );
		    	}
		       }catch(Exception e){e.printStackTrace();}
		   }
	   }
	  }

	/**
	 * 获取页面的参数
	 * @param bw
	 * @param request
	 * @param prePara
	 * @param jsonObject
	 */
	private void setPageparameter(BeanWrapper bw, HttpServletRequest request,String prePara ,JsonObject jsonObject)  {
		//try {
		paramap = new HashMap<String,String>();
		//datamap = new HashMap<String,Object>();

		HashMap<String, String> map = new HashMap<String, String>();
		if( bw != null ){
			PropertyDescriptor[] pro = bw.getPropertyDescriptors();
			for (int i = 0; i < pro.length; i++) {
				if( pro[i].getWriteMethod()!= null ) {
					String name = pro[i].getName();
					map.put( name.toLowerCase(), name );
				}
			}
		}
		logger.debug("prePara:" + prePara);
		if( prePara== null )
			prePara = "";
		this.paraParameter(request,jsonObject);
		for( int i=0; i < paraList.size(); i ++){
			String paraName = paraList.get(i).getPara();
			String paraValue = paraList.get(i).getValue();
			if (paraName != null) {
				if( logger.isDebugEnabled() )
					logger.debug("paraname: " + paraName + " = " + paraList.get(i).getValue());
				if ( prePara.equals("") || paraName.indexOf(prePara)==0 ) { //保存对象

					if (paraName.indexOf("_like") >= 0) {//
						paraName = paraName.replaceAll("_like", "");
						if( logger.isDebugEnabled() )
							logger.debug( "set paraName="+paraName);


							if( paraValue!= null && paraValue.trim().length()>0)
								paraValue = "%" + paraValue + "%";

					}
					if(!prePara.equals("")){
						if(paraName.indexOf(prePara)==0){
							paraName = paraName.substring(prePara.length());
						}
					}

					String subname = "";
					int pos = paraName.indexOf(".");
					if (pos >= 0) {
						subname = paraName.substring(0, pos);
						paraName = paraName.substring(pos+1);
					}
					else {
						subname = paraName;
						paraName = "";
					}
					subname = subname.toLowerCase();

					if (map.containsKey(subname)) {
						subname = map.get(subname);
						if (pos < 0) {
							if( paraValue != null && paraValue.trim().length() > 0 ){
								this.paramap.put(subname.toLowerCase() , subname);
							}
							setValue(bw, subname, paraValue);
						}
						else {
							if( bw.getPropertyValue(subname)!= null ){
								BeanWrapper subbw = new BeanWrapperImpl(bw.getPropertyValue(subname));
								setSubPageparameter(subbw, paraName, paraValue);
							}
						}
					}
				}
			}
		}
	}

	private void setSubPageparameter(BeanWrapper bw, String paraname, String paravalue) {
		try {
			PropertyDescriptor[] pro = bw.getPropertyDescriptors();
			HashMap<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < pro.length; i++) {
				String name = pro[i].getName();
				map.put(name.toLowerCase(), name);
			}
			if (map.containsKey(paraname.toLowerCase())) {
				String subname = map.get(paraname.toLowerCase());
				setValue(bw, subname, paravalue);
			}
		}
		catch (Exception e) {
			logger.error(e.toString());
		}
	}


	/**
	 * 设置对象的属性值

	 *
	 * @param paraName
	 * @param paraValue
	 * @throws Exception
	 */
	private void setValue(BeanWrapper bw, String paraName, String paraValue)  {
		//try {
			//当标签中含有'_like'时，表示作为like查询条件 对应值加上'%',并将其'_like'去掉
			if (bw.getPropertyType(paraName) == null)
				return;
			logger.debug(paraName+ " old value "+ bw.getPropertyValue(paraName));
			String ParaType = bw.getPropertyType(paraName).toString();
			if (paraValue == null || paraValue.trim().length() == 0) {
				if (ParaType.trim().equals("int") || ParaType.trim().equals("double") || ParaType.trim().equals("float")) {
					// Integer类型
					Integer k = 0;
					bw.setPropertyValue(paraName, k);
					return;
				}
				if (bw.getPropertyDescriptor(paraName) != null && bw.isWritableProperty( paraName ))
					bw.setPropertyValue(paraName, null);
				return;
			}
			// 设置查询条件字段

			//System.out.println("ParaType=" + ParaType);
			if (ParaType.trim().equals("class java.lang.String")) {
				// String(字符串)类型
				bw.setPropertyValue(paraName, paraValue);

			}
			else if (ParaType.trim().equals("class java.lang.Integer")) {
				// Integer类型
				//Integer k = strtext.Str2Integer(I_Value);
				Integer k = Integer.parseInt( paraValue );
				//if (k != null) {
				bw.setPropertyValue(paraName, k);
				//}

			}
			else if (ParaType.trim().equals("int")) {
				// Integer类型
				//Integer k = strtext.Str2Integer(I_Value);
				Integer k = Integer.parseInt( paraValue );
				//if (k != null) {
				bw.setPropertyValue(paraName, k);
				//}
			}
			else if (ParaType.trim().equals("class java.lang.Short")) {
				// Integer类型
				//Short k = strtext.Str2Short(I_Value);
				Short k = Short.parseShort( paraValue );
				//if (k != null) {
				bw.setPropertyValue(paraName, k);
				//}
			}
			else if (ParaType.trim().equals("class java.lang.Long")) {
				//Integer类型
				//Long k = strtext.Str2Long(I_Value);
				long k = Long.parseLong(paraValue);
				//if( k != null ){
				bw.setPropertyValue(paraName, k);
				//}
			}
			else if (ParaType.trim().equals("class java.util.Date")) {
				// Date(日期)类型
				Date date = null;
				if (paraValue != null && paraValue.trim().equalsIgnoreCase("sysdate")) {
					date = new Date();
				}
				else {
					//date = strtext.Str2Date(I_Value, "yyyy-MM-dd HH:mm:ss");
					date = ComUtil.strToDate(paraValue, "yyyy-MM-dd HH:mm:ss");
				}

				//if (date != null) {
				bw.setPropertyValue(paraName, date);
				//}

			}
			else if (ParaType.trim().equals("class java.math.BigDecimal")) {
				// BigDecimal(浮点数据)类型
				//BigDecimal bigdec = strtext.Str2BigDecimal(I_Value);
				BigDecimal bigdec = new BigDecimal(paraValue);
				//if (bigdec != null) {
				bw.setPropertyValue(paraName, bigdec);
				//}
			}
			else if (ParaType.trim().equals("class java.lang.Double")) {
				// BigDecimal(浮点数据)类型
				//bw.setPropertyValue(I_Name, strtext.Str2Double(I_Value));
				bw.setPropertyValue(paraName, Double.parseDouble(paraValue));
			}
			else if (ParaType.trim().equals("class java.sql.Timestamp")) {
				// 时间
				Date date = null;
				if (paraValue != null && paraValue.trim().equals("sysdate")) {
					date = new Date();
				}
				else {
					//date = strtext.Str2Date(I_Value, "yyyy-MM-dd HH:mm:ss");
					date = ComUtil.strToDate(paraValue, "yyyy-MM-dd HH:mm:ss");
				}
				if( date == null )
					bw.setPropertyValue(paraName,null);
				else
					bw.setPropertyValue(paraName, new Timestamp(date.getTime()));
			}
			else if (ParaType.length() > 0) {
				logger.error("参数" + paraName + "因类型" + ParaType + "未知，无法设置值!");
				//throw new Exception("参数" + I_Name + "因类型" + ParaType + "未知，无法设置值!");
			}
			logger.debug(paraName+ " new value "+ bw.getPropertyValue(paraName));
		//}
		//catch (Exception e) {
		//
		//	e.printStackTrace();
		//	throw e;
		//}
	}
	private class HttpReq{
		private String para;
		private String value;

		public HttpReq() {
			super();
		}
//		public HttpReq(String para, String value) {
//			super();
//			this.para = para;
//			this.value = value;
//		}
		public String getPara() {
			return para;
		}
		public void setPara(String para) {
			this.para = para;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}

	}
}

