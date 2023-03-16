package cn.bywin.business.controller.system;

import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin(value = {"*"},
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
		maxAge = 3600)
@RestController
@RequestMapping("/system/logbackview")
public class LogbackViewController  extends BaseController {

	protected final Logger logger = LoggerFactory.getLogger(LogbackViewController.class);
	
	private String LOGBACK="ch.qos.logback.classic.util.ContextSelectorStaticBinder";
	private String LOG4J="org.slf4j.impl.Log4jLoggerFactory";
	  
	@RequestMapping(value = {"/idx"},method= {RequestMethod.POST,RequestMethod.GET}) // url
	public ModelAndView idx(HttpServletRequest request, HttpServletResponse response) {
		String pg = request.getContextPath();
	    String url = request.getRequestURL().toString();
	    url = url.substring(0,url.indexOf(pg));
	    url = url.substring(url.indexOf("://")+3);
	    //System.out.println(url);
	    String urlpage=url+pg;
		ModelAndView mv = new ModelAndView("/page/system/logview");
		mv.addObject("pg", pg);
		mv.addObject("urlpage", urlpage);
        return mv; 
	}
	
	@RequestMapping(value="/list",method= {RequestMethod.POST,RequestMethod.GET})
	public Object list(String clsName, HttpServletRequest request, HttpServletResponse response) {
		ResponeMap resMap = this.genResponeMap();
		try {
			
			//JSONObject json =	HttpRequestUtil.httpBody(request);
			//String clsName = HttpRequestUtil.getNvlPara("clsName", request, json).trim().toLowerCase();
			clsName = clsName.toLowerCase();
			logger.info("clsName:"+ clsName);
			resMap.put("clsName", clsName);
			    List<Object> list = new ArrayList<>();
			    if(!clsName.equals("")) {
				String type = StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr();
//				System.out.println(type);
//				if ( LOG4J.equals( type ) )
//				{
//				    //logFrameworkType = LogFrameworkType.LOG4J;
//				    Enumeration enumeration = org.apache.log4j.LogManager.getCurrentLoggers();
//				    while ( enumeration.hasMoreElements() )
//				    {
//				        org.apache.log4j.Logger log = (org.apache.log4j.Logger)enumeration.nextElement();
//				        if ( log.getName().toLowerCase().indexOf(clsName)>=0 )
//				        {
//				        	HashMap<String,Object> logdata = new HashMap<String,Object>();
//				        	logdata.put("name", log.getName());
//				        	logdata.put("level",log.getLevel());
//				        	list.add(logdata);
//				        }
//				    }
//				    
//				} 
//				else 
				if ( LOGBACK.equals( type ) )
				{
				    ch.qos.logback.classic.LoggerContext loggerContext = (ch.qos.logback.classic.LoggerContext)LoggerFactory.getILoggerFactory();
				    for ( ch.qos.logback.classic.Logger log : loggerContext.getLoggerList() )
				    {
				        if ( log.getName().toLowerCase().indexOf(clsName)>=0 )
				        {
				        	HashMap<String,Object> logdata = new HashMap<String,Object>();
				        	logdata.put("name", log.getName());
				        	if( log.getLevel() != null )
				        		logdata.put("level",log.getLevel().levelStr);
				        	else
				        		logdata.put("level", null);
				        	list.add(logdata);
				        }
				    }
				   // ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger( Logger.ROOT_LOGGER_NAME );
				    //loggerMap.put( rootLogger.getName(), rootLogger );
				}
				/*else if ( LogConstant.LOG4J2_LOGGER_FACTORY.equals( type ) )
				{
				    logFrameworkType = LogFrameworkType.LOG4J2;
				    org.apache.logging.log4j.core.LoggerContext         loggerContext   = (org.apache.logging.log4j.core.LoggerContext)org.apache.logging.log4j.LogManager.getContext( false );
				    Map<String, org.apache.logging.log4j.core.config.LoggerConfig>  map     = loggerContext.getConfiguration().getLoggers();
				    for ( org.apache.logging.log4j.core.config.LoggerConfig loggerConfig : map.values() )
				    {
				        String key = loggerConfig.getName();
				        if ( StringUtils.isBlank( key ) )
				        {
				            key = "root";
				        }
				        loggerMap.put( key, loggerConfig );
				    }
				} else {
				    logFrameworkType = LogFrameworkType.UNKNOWN;
				    LOG.error( "Log框架无法识别: type={}", type );
				}*/
		    }
		
			resMap.setOk(list.size(), list);
			resMap.put("rows",list);
		} catch (Exception e) {
			logger.error("发送kafka失败", e);
			resMap.setErr(e.getMessage());
		}
		return resMap.getResultMap();
	}
	
	 
	 
	
	@RequestMapping(value="/resetlevel",method= {RequestMethod.POST,RequestMethod.GET})
	public Object resetLevel(String clsName,String level, HttpServletRequest request, HttpServletResponse response) {
		ResponeMap resMap = this.genResponeMap();
		try {
			//JSONObject json =	HttpRequestUtil.httpBody(request);
			//String clsName = HttpRequestUtil.getNvlPara("clsName", request, json).trim();
			//String level = HttpRequestUtil.getNvlPara("level", request, json).trim().toLowerCase();
			level = level.toLowerCase();
			logger.info("clsName:"+ clsName +" level:" +level);
			resMap.put("set", "clsName:"+ clsName +" level:" +level);
			     
		    if(clsName.equals("") ||level.equals("") ) {
		    	resMap.setErr("clsName level 不能为空");
		    	return resMap.getResultMap();
		    }
			
		    List<Object> list = new ArrayList<>();
			String type = StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr();
//			if ( LOG4J.equals( type ) )
//			{
//			    //logFrameworkType = LogFrameworkType.LOG4J;
//			    Enumeration enumeration = org.apache.log4j.LogManager.getCurrentLoggers();
//			    while ( enumeration.hasMoreElements() )
//			    {
//			        org.apache.log4j.Logger log = (org.apache.log4j.Logger)enumeration.nextElement();
//			        if ( log.getName().equals(clsName) )
//			        {
//			        	log.setLevel(org.apache.log4j.Level.toLevel(level));
//			        }
//			    }
//			    
//			} else 
			if ( LOGBACK.equals( type ) )
			{
			    ch.qos.logback.classic.LoggerContext loggerContext = (ch.qos.logback.classic.LoggerContext)LoggerFactory.getILoggerFactory();
			    for ( ch.qos.logback.classic.Logger log : loggerContext.getLoggerList() )
			    {
			    	if ( log.getName().equals(clsName) )
			        {
			        	log.setLevel(ch.qos.logback.classic.Level.toLevel(level));
			        	logger.info("change " + log.getName() +" to " + level );
			        }
			    }
			   // ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger( Logger.ROOT_LOGGER_NAME );
			    //loggerMap.put( rootLogger.getName(), rootLogger );
			}
			/*else if ( LogConstant.LOG4J2_LOGGER_FACTORY.equals( type ) )
			{
			    logFrameworkType = LogFrameworkType.LOG4J2;
			    org.apache.logging.log4j.core.LoggerContext         loggerContext   = (org.apache.logging.log4j.core.LoggerContext)org.apache.logging.log4j.LogManager.getContext( false );
			    Map<String, org.apache.logging.log4j.core.config.LoggerConfig>  map     = loggerContext.getConfiguration().getLoggers();
			    for ( org.apache.logging.log4j.core.config.LoggerConfig loggerConfig : map.values() )
			    {
			        String key = loggerConfig.getName();
			        if ( StringUtils.isBlank( key ) )
			        {
			            key = "root";
			        }
			        loggerMap.put( key, loggerConfig );
			    }
			} else {
			    logFrameworkType = LogFrameworkType.UNKNOWN;
			    LOG.error( "Log框架无法识别: type={}", type );
			}*/
		
			resMap.setOk(list.size(), list);
		} catch (Exception e) {
			logger.error("发送kafka失败", e);
			resMap.setErr(e.getMessage());
		}
		return resMap.getResultMap();
	}
	 
	 
}
 
 
