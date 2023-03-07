package cn.bywin.business.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.beanutils.PropertyUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MyDbObjectFieldAndValue
{
    private String taleName ;
    private String idValue;
    private String idName;
    private List<Map> resultList;

    public List<Map> parseObj( Object bean ) throws Exception{
        resultList =  new LinkedList<>();
        final Table table = bean.getClass().getAnnotation(Table.class);
        this.taleName = table.name();
        Class<?> clazz = bean.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {//向上循环  遍历父类
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                final String name = field.getName();

                final Id fieldId = field.getAnnotation(Id.class);
                if( fieldId != null ){
                    idValue = PropertyUtils.getSimpleProperty(bean, name).toString();
                    idName = name;
                }

                final Transient tran = field.getAnnotation(Transient.class);
                final Column colField = field.getAnnotation(Column.class);
                String col ;
                if( colField != null ){
                    col = colField.name();
                }
                else{
                    final char[] chars = name.toCharArray();
                    StringBuilder sb = new StringBuilder();
                    for (char aChar : chars) {
                        if( aChar>='A' && aChar<='Z'){
                            sb.append( ( "_" + aChar).toLowerCase());
                        }
                        else
                        {
                            sb.append( aChar );
                        }
                    }
                    col = sb.toString();
                }
                if( tran == null ) {
                    Object value = PropertyUtils.getSimpleProperty(bean, name);
                    if (value != null) {
                        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                        data.put("f",col);
                        data.put("v",value);
                        resultList.add(data);
                        //map.put( field.getName() +"("+col+")", value );
                        //System.out.println(field.getName() +"("+col+")" +":" + value);
                    }
                }
            }
        }
        return resultList;
    }

    public List compareObject(Object oldBean, Object newBean) throws Exception{

        resultList = new LinkedList<>();
        final Table table = oldBean.getClass().getAnnotation(Table.class);
        this.taleName = table.name() ;

        Class<?> clazz = oldBean.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {//向上循环  遍历父类
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                final String name = field.getName();

                final Id fieldId = field.getAnnotation(Id.class);
                if( fieldId != null ){
                    idValue = PropertyUtils.getSimpleProperty(oldBean, name).toString();
                    idName = name;
                }

                final Transient tran = field.getAnnotation(Transient.class);
                final Column colField = field.getAnnotation(Column.class);
                String col ;
                if( colField != null ){
                    col = colField.name();
                }
                else{
                    final char[] chars = name.toCharArray();
                    StringBuilder sb = new StringBuilder();
                    for (char aChar : chars) {
                        if( aChar>='A' && aChar<='Z'){
                            sb.append( ( "_" + aChar).toLowerCase());
                        }
                        else
                        {
                            sb.append( aChar );
                        }
                    }
                    col = sb.toString();
                }
                if( tran == null ) {
                    Object value1 = PropertyUtils.getSimpleProperty(oldBean, name);
                    Object value2 = PropertyUtils.getSimpleProperty(newBean, name);
                    if( value1 == null && value2 == null ){ //same null

                    }

                    else if( value1 != null && value2 != null ){ //same null
                        if( !value1.equals( value2 )){
                            LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                            data.put("f", col);
                            data.put("v1", value1);
                            data.put("v2", value2);
                            resultList.add(data);
                        }
                    }
                    else {
                        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                        data.put("f", col);
                        data.put("v1", value1);
                        data.put("v2", value2);
                        resultList.add(data);
                        //map.put( field.getName() +"("+col+")", value );
                        //System.out.println(field.getName() +"("+col+")" +":" + value);
                    }
                }
            }
        }
        return resultList;
    }

    public String getTaleName() {
        return taleName;
    }

    public void setTaleName(String taleName) {
        this.taleName = taleName;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public List<Map> getResultList() {
        return resultList;
    }

    public void setResultList(List<Map> resultList) {
        this.resultList = resultList;
    }
    public String getResultListAsJson(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
        return gson.toJson( resultList );
    }
}
