package cn.bywin.business.util;//package cn.bywin.business.util;
//
//import cn.bywin.business.bean.system.SysLogDo;
//import cn.bywin.business.common.util.JsonUtil;
//import cn.bywin.business.service.system.SysLogService;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//@Aspect
//public class LogAspect {
//
//    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
//
//    @Autowired
//    private SysLogService sysLogService;
//
//    @Pointcut("execution(public * cn.bywin.business.controller.federal.LoginController.registe*(..))")
//    public void msgLog() {
//    }
//
//    //环绕通知,环绕增强，相当于MethodInterceptor
//    @Around("msgLog()")
//    public Object msgLogAround(ProceedingJoinPoint pjp) {
//        try {
//            Object o = pjp.proceed();
//            return o;
//        } catch (Throwable e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @After("msgLog()")
//    public void msgLogAfter(JoinPoint point) {
//    }
//    @AfterReturning(pointcut = "msgLog()")
//    public void msgLogAfterReturning(JoinPoint point) {
//        Object json = JsonUtil.toJson(point.getArgs());
//        SysLogDo modes = JsonUtil.deserialize(json.toString(), SysLogDo.class);
//    }
//
//
//}
