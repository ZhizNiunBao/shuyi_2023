package cn.bywin.business.common.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注 javaBean 中和 exele 列对应的字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EJConvertorColumn {

    /**
     * javaBean 标注的字段对应的列标题
     */
    String columnTitle();

}
