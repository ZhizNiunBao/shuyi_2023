package cn.bywin.business.hetu;

import cn.bywin.business.bean.bydb.TBydbLogDo;
import cn.bywin.business.common.enums.JobStatus;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.service.bydb.BydbLogService;
import cn.bywin.tools.SpringContextUtil;
import cn.common.base.AuditLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.net.InetAddress;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

/**
 * HetuJdbcOperate 动态代理类，添加日志记录功能
 * @author zzm
 */
@Slf4j
public class HetuJdbcOperateLogProxy {

    private static final Enhancer enhancer;

    static {
        enhancer = new Enhancer();
        enhancer.setSuperclass(HetuJdbcOperate.class);
        enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> {
            boolean success = true;
            try {
                return methodProxy.invokeSuper(o, objects);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                AuditLog auditLog = method.getAnnotation(AuditLog.class);
                if (auditLog != null) {
                    try {
                        BydbLogService bydbLogService = SpringContextUtil.getBean(BydbLogService.class);
                        HetuJdbcOperate hetuJdbcOperate = (HetuJdbcOperate) o;
                        Connection connection = hetuJdbcOperate.getConnection();

                        String url = connection.getMetaData().getURL();
                        URI uri = new URI(url);
                        TBydbLogDo logInfo = new TBydbLogDo();
                        logInfo.setHostname(uri.getHost());
                        InetAddress localHost = InetAddress.getLocalHost();
                        logInfo.setUserIp(localHost.getHostAddress());

                        String currentDateStr = ComUtil.dateToLongStr(new Date());

                        logInfo.setQueryId(currentDateStr);

                        String sql = objects[auditLog.sqlParameterIndex()].toString();
                        logInfo.setMessage(sql);
                        logInfo.setMessageStmt(sql);
                        logInfo.setMessageTime(currentDateStr);
                        logInfo.setMessageStatus(success ? JobStatus.SUCCESS.name() : JobStatus.FAILD.name());

                        bydbLogService.insertBean(logInfo);
                    } catch (Exception e) {
                        log.warn("add audit log fail: " + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 获取 HetuJdbcOperate 增强类，添加日志记录功能，并进行初始化
     * @param url             hetu 连接地址
     * @param properties      hetu 配置信息
     * @return                hetu 增强类
     * @throws SQLException
     */
    public static HetuJdbcOperate getInstance(String url, Properties properties) throws SQLException {
        HetuJdbcOperate hetuJdbcOperate = (HetuJdbcOperate) enhancer.create();
        hetuJdbcOperate.init(url, properties);
        return hetuJdbcOperate;
    }
}
