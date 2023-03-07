package cn.bywin.business.common.util;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyBeanUtils
        extends PropertyUtilsBean {

    private static void convert( Object dest, Object orig ) throws
            IllegalAccessException, InvocationTargetException {

        // Validate existence of the specified beans
        if ( dest == null ) {
            throw new IllegalArgumentException
                    ( "No destination bean specified" );
        }
        if ( orig == null ) {
            throw new IllegalArgumentException( "No origin bean specified" );
        }

        // Copy the properties, converting as necessary
        if ( orig instanceof DynaBean ) {
            DynaProperty origDescriptors[] =
                    ((DynaBean) orig).getDynaClass().getDynaProperties();
            for ( int i = 0; i < origDescriptors.length; i++ ) {
                String name = origDescriptors[i].getName();
                if ( PropertyUtils.isWriteable( dest, name ) ) {
                    Object value = ((DynaBean) orig).get( name );
                    try {
                        getInstance().setSimpleProperty( dest, name, value );
                    }
                    catch ( Exception e ) {
                        ; // Should not happen
                    }

                }
            }
        }
        else if ( orig instanceof Map ) {
            Iterator names = ((Map) orig).keySet().iterator();
            while ( names.hasNext() ) {
                String name = (String) names.next();
                if ( PropertyUtils.isWriteable( dest, name ) ) {
                    Object value = ((Map) orig).get( name );
                    try {
                        getInstance().setSimpleProperty( dest, name, value );
                    }
                    catch ( Exception e ) {
                        ; // Should not happen
                    }

                }
            }
        }
        else
            /* if (orig is a standard JavaBean) */ {
            PropertyDescriptor origDescriptors[] =
                    PropertyUtils.getPropertyDescriptors( orig );
            for ( int i = 0; i < origDescriptors.length; i++ ) {
                String name = origDescriptors[i].getName();
//              String type = origDescriptors[i].getPropertyType().toString();
                if ( "class".equals( name ) ) {
                    continue; // No point in trying to set an object's class
                }
                if ( PropertyUtils.isReadable( orig, name ) &&
                        PropertyUtils.isWriteable( dest, name ) ) {
                    try {
                        Object value = PropertyUtils.getSimpleProperty( orig, name );
                        getInstance().setSimpleProperty( dest, name, value );
                    }
                    catch ( IllegalArgumentException ie ) {
                        ; // Should not happen
                    }
                    catch ( Exception e ) {
                        ; // Should not happen
                    }

                }
            }
        }

    }

    /**
     * 对象拷贝
     * 数据对象空值不拷贝到目标对象
     *
     * @param databean
     * @param tobean
     * @throws NoSuchMethodException copy
     */
    public static void copyBeanNotNull2Bean( Object databean, Object tobean ) throws Exception {
        PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors( databean );
        PropertyDescriptor prop2[] = PropertyUtils.getPropertyDescriptors( tobean );
        HashMap<String, String> nameMap = new HashMap<>();
        for ( int i = 0; i < prop2.length; i++ ) {
            String name = prop2[i].getName();
            nameMap.put( name, name );
        }
        for ( int i = 0; i < origDescriptors.length; i++ ) {
            String name = origDescriptors[i].getName();
//          String type = origDescriptors[i].getPropertyType().toString();
            if ( "class".equals( name ) ) {
                continue; // No point in trying to set an object's class
            }
            if ( !nameMap.containsKey( name ) ) continue;
            if ( PropertyUtils.isReadable( databean, name ) && PropertyUtils.isWriteable( tobean, name ) ) {
                try {
                    Object value = PropertyUtils.getSimpleProperty( databean, name );
                    if ( value != null ) {
                        getInstance().setSimpleProperty( tobean, name, value );
                    }
                }
                catch ( IllegalArgumentException ie ) {
                    ; // Should not happen
                }
                catch ( Exception e ) {
                    ; // Should not happen
                }

            }
        }
    }

    /**
     * 设置对象属性值 属性忽略大小写
     *
     * @param bean
     * @param prop
     * @param dataVal
     * @throws Exception
     */
    public static void setBeanIgnoreCaseProperty( Object bean, String prop, Object dataVal ) throws Exception {
        PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors( bean );

        for ( int i = 0; i < origDescriptors.length; i++ ) {
            String name = origDescriptors[i].getName();
            if ( "class".equals( name ) ) {
                continue; // No point in trying to set an object's class
            }
            if ( !name.equalsIgnoreCase( prop ) ) continue;
            if ( PropertyUtils.isWriteable( bean, name ) ) {
                getInstance().setSimpleProperty( bean, name, dataVal );
            }
        }
    }

    /**
     * 把orig和dest相同属性的value复制到dest中
     *
     * @param dest
     * @param orig
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void copyBean2Bean( Object dest, Object orig ) throws Exception {
        convert( dest, orig );
    }

    public static void copyBean2Map( Map map, Object bean ) {
        copyBean2Map( map, bean, null );
        /*PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors( bean );
        for ( int i = 0; i < pds.length; i++ ) {
            PropertyDescriptor pd = pds[i];
            String propname = pd.getName();
            try {
                Object propvalue = PropertyUtils.getSimpleProperty( bean, propname );
                map.put( propname, propvalue );
            }
            catch ( IllegalAccessException e ) {
                //e.printStackTrace();
            }
            catch ( InvocationTargetException e ) {
                //e.printStackTrace();
            }
            catch ( NoSuchMethodException e ) {
                //e.printStackTrace();
            }
        }*/
    }

    public static void copyBean2Map( Map map, Object bean, String... item ) {
        List<String> itemList = null;
        if ( item != null ) {
            itemList = Arrays.stream( item ).map( x -> x.toLowerCase() ).collect( Collectors.toList() );
        }
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors( bean );
        for ( int i = 0; i < pds.length; i++ ) {
            PropertyDescriptor pd = pds[i];
            String propname = pd.getName();
            if ( itemList != null && itemList.indexOf( propname.toLowerCase() ) < 0 ) continue;
            try {
                Object propvalue = PropertyUtils.getSimpleProperty( bean, propname );
                map.put( propname, propvalue );
            }
            catch ( IllegalAccessException e ) {
                //e.printStackTrace();
            }
            catch ( InvocationTargetException e ) {
                //e.printStackTrace();
            }
            catch ( NoSuchMethodException e ) {
                //e.printStackTrace();
            }
        }
    }

    public static void copyBeanNotNull2Map( Object bean, Map map ) {
        copyBeanNotNull2Map( bean, map, null );
        /*PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors( bean );
        for ( int i = 0; i < pds.length; i++ ) {
            PropertyDescriptor pd = pds[i];
            String propname = pd.getName();
            try {
                if( !  Class.class.equals( pd.getPropertyType() ) ) {
                    Object propvalue = PropertyUtils.getSimpleProperty( bean, propname );
                    if ( propvalue != null ) {
                        map.put( propname, propvalue );
                    }
                }
            }
            catch ( IllegalAccessException e ) {
                //e.printStackTrace();
            }
            catch ( InvocationTargetException e ) {
                //e.printStackTrace();
            }
            catch ( NoSuchMethodException e ) {
                //e.printStackTrace();
            }
        }*/
    }

    public static void copyBeanNotNull2Map( Object bean, Map map, String... item ) {
        List<String> itemList = null;
        if ( item != null ) {
            itemList = Arrays.stream( item ).map( x -> x.toLowerCase() ).collect( Collectors.toList() );
        }
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors( bean );
        for ( int i = 0; i < pds.length; i++ ) {
            PropertyDescriptor pd = pds[i];
            String propname = pd.getName();
            if ( itemList != null && itemList.indexOf( propname.toLowerCase() ) < 0 ) continue;
            try {
                if ( !Class.class.equals( pd.getPropertyType() ) ) {
                    Object propvalue = PropertyUtils.getSimpleProperty( bean, propname );
                    if ( propvalue != null ) {
                        map.put( propname, propvalue );
                    }
                }
            }
            catch ( IllegalAccessException e ) {
                //e.printStackTrace();
            }
            catch ( InvocationTargetException e ) {
                //e.printStackTrace();
            }
            catch ( NoSuchMethodException e ) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * 将Map内的key与Bean中属性相同的内容复制到BEAN中
     *
     * @param bean       Object
     * @param properties Map
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void copyMap2Bean( Object bean, Map properties ) throws
            IllegalAccessException, InvocationTargetException {
        // Do nothing unless both arguments have been specified
        if ( (bean == null) || (properties == null) ) {
            return;
        }
        // Loop through the property name/value pairs to be set
        Iterator names = properties.keySet().iterator();
        while ( names.hasNext() ) {
            String name = (String) names.next();
            // Identify the property name and value(s) to be assigned
            if ( name == null ) {
                continue;
            }


            Object value = null;

            try {
                Class clazz = PropertyUtils.getPropertyType( bean, name );
                if ( null == clazz ) {
                    continue;
                }
                String className = clazz.getName();
                if ( className.equalsIgnoreCase( "java.lang.Integer" ) ) {
                    value = Integer.parseInt( properties.get( name ).toString() );
                }
                else {
                    value = properties.get( name );
                }
                if ( className.equalsIgnoreCase( "java.sql.Timestamp" ) ) {
                    if ( value == null || value.equals( "" ) ) {
                        continue;
                    }
                }
                getInstance().setSimpleProperty( bean, name, value );
            }
            catch ( NoSuchMethodException e ) {
                continue;
            }
        }
    }

    /**
     * 自动转Map key值大写
     * 将Map内的key与Bean中属性相同的内容复制到BEAN中
     *
     * @param bean       Object
     * @param properties Map
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void copyMap2BeanNobig( Object bean, Map<String, Object> properties ) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // Do nothing unless both arguments have been specified
        if ( (bean == null) || (properties == null) ) {
            return;
        }

        Map<String, String> nameMap = properties.keySet().stream().collect( Collectors.toMap( x -> x.toLowerCase(), x -> x ) );

        PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors( bean );
        //HashMap<String, String> nameMap = new HashMap<>();
//        for (int i = 0; i < prop.length; i++) {
//            String name = prop[i].toLowerCase();
//            nameMap.put(name, name);
//        }
        for ( int i = 0; i < origDescriptors.length; i++ ) {
            String name = origDescriptors[i].getName();
//          String type = origDescriptors[i].getPropertyType().toString();
            if ( "class".equals( name ) ) {
                continue; // No point in trying to set an object's class
            }
            if ( !nameMap.containsKey( name.toLowerCase() ) ) continue;
            String proName = nameMap.get( name.toLowerCase() );
            Object proVal = properties.get( proName );
            if ( PropertyUtils.isWriteable( bean, name ) ) {
                if ( proVal != null ) {
                    getInstance().setSimpleProperty( bean, name, proVal );
                }
                else {
                    getInstance().setSimpleProperty( bean, name, null );
                }
            }
        }

        // Loop through the property name/value pairs to be set
        /*Iterator names = properties.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            // Identify the property name and value(s) to be assigned
            if (name == null) {
                continue;
            }
            Object value = properties.get(name);
            // 命名应该大小写应该敏感(否则取不到对象的属性)
            //name = name.toLowerCase();
            try {
                if (value == null) {    // 不光Date类型，好多类型在null时会出错
                    continue;    // 如果为null不用设 (对象如果有特殊初始值也可以保留？)
                }
                Class clazz = PropertyUtils.getPropertyType(bean, name);
                if (null == clazz) {    // 在bean中这个属性不存在
                    continue;
                }
                String className = clazz.getName();
                // 临时对策（如果不处理默认的类型转换时会出错）
                if (className.equalsIgnoreCase("java.util.Date")) {
                    value = new java.util.Date(((java.sql.Timestamp) value).getTime());// wait to do：貌似有时区问题, 待进一步确认
                }
//              if (className.equalsIgnoreCase("java.sql.Timestamp")) {
//                  if (value == null || value.equals("")) {
//                      continue;
//                  }
//              }
                getInstance().setSimpleProperty(bean, name, value);
            } catch (NoSuchMethodException e) {
                continue;
            }
        }*/
    }

    /**
     * Map内的key与Bean中属性相同的内容复制到BEAN中
     * 对于存在空值的取默认值
     *
     * @param bean         Object
     * @param properties   Map
     * @param defaultValue String
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void copyMap2Bean( Object bean, Map properties, String defaultValue ) throws
            IllegalAccessException, InvocationTargetException {
        // Do nothing unless both arguments have been specified
        if ( (bean == null) || (properties == null) ) {
            return;
        }
        // Loop through the property name/value pairs to be set
        Iterator names = properties.keySet().iterator();
        while ( names.hasNext() ) {
            String name = (String) names.next();
            // Identify the property name and value(s) to be assigned
            if ( name == null ) {
                continue;
            }
            Object value = properties.get( name );
            try {
                Class clazz = PropertyUtils.getPropertyType( bean, name );
                if ( null == clazz ) {
                    continue;
                }
                String className = clazz.getName();
                if ( className.equalsIgnoreCase( "java.sql.Timestamp" ) ) {
                    if ( value == null || value.equals( "" ) ) {
                        continue;
                    }
                }
                if ( className.equalsIgnoreCase( "java.lang.String" ) ) {
                    if ( value == null ) {
                        value = defaultValue;
                    }
                }
                getInstance().setSimpleProperty( bean, name, value );
            }
            catch ( NoSuchMethodException e ) {
                continue;
            }
        }
    }

    /**
     * 重置对象的属性值为 like 查询条件
     *
     * @param databean
     * @param prop
     * @throws Exception
     */
    public static void chgBeanLikeProperties( Object databean, String... prop ) throws Exception {
        if ( databean == null || prop == null || prop.length == 0 ) {
            return;
        }
        PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors( databean );
        HashMap<String, String> nameMap = new HashMap<>();
        for ( int i = 0; i < prop.length; i++ ) {
            String name = prop[i].toLowerCase();
            nameMap.put( name, name );
        }
        for ( int i = 0; i < origDescriptors.length; i++ ) {
            String name = origDescriptors[i].getName();
//          String type = origDescriptors[i].getPropertyType().toString();
            if ( "class".equals( name ) ) {
                continue; // No point in trying to set an object's class
            }
            if ( !nameMap.containsKey( name.toLowerCase() ) ) continue;
            if ( PropertyUtils.isReadable( databean, name ) && PropertyUtils.isWriteable( databean, name ) ) {
                try {
                    Object dataval = PropertyUtils.getSimpleProperty( databean, name );
                    if ( dataval != null && StringUtils.isNotBlank( dataval.toString() ) ) {
                        String strVal = "%" + dataval.toString().trim().replaceAll( "\\\\", "\\\\\\\\" ).replaceAll( "%", "\\%" )
                                .replaceAll( "\\_", "\\\\_" ).replaceAll( "\\%", "\\\\%" ) + "%";
                        getInstance().setSimpleProperty( databean, name, strVal );
                    }
                    else {
                        getInstance().setSimpleProperty( databean, name, null );
                    }
                }
                catch ( IllegalArgumentException ie ) {
                    ; // Should not happen
                }
                catch ( Exception e ) {
                    ; // Should not happen
                }
            }
        }
    }

    public static void chgBeanLikeAndOtherStringEmptyToNull( Object databean, String... prop ) throws Exception {
        if ( databean == null || prop == null || prop.length == 0 ) {
            return;
        }
        PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors( databean );
        HashMap<String, String> nameMap = new HashMap<>();
        for ( int i = 0; i < prop.length; i++ ) {
            String name = prop[i].toLowerCase();
            nameMap.put( name, name );
        }
        for ( int i = 0; i < origDescriptors.length; i++ ) {
            String name = origDescriptors[i].getName();
            String type = origDescriptors[i].getPropertyType().toString();
//            if ("class".equals(name)) {
//                continue; // No point in trying to set an object's class
//            }
            if ( type.equals( "class java.lang.String" ) ) {

                if ( PropertyUtils.isReadable( databean, name ) && PropertyUtils.isWriteable( databean, name ) ) {
                    try {
                        Object dataval = PropertyUtils.getSimpleProperty( databean, name );
                        if ( dataval != null && StringUtils.isNotBlank( dataval.toString() ) ) {
                            if ( nameMap.containsKey( name.toLowerCase() ) ) {
                                String strVal = "%" + dataval.toString().trim().replaceAll( "\\\\", "\\\\\\\\" ).replaceAll( "%", "\\%" )
                                        .replaceAll( "\\_", "\\\\_" ).replaceAll( "\\%", "\\\\%" ) + "%";
                                getInstance().setSimpleProperty( databean, name, strVal );
                            }
                        }
                        else {
                            getInstance().setSimpleProperty( databean, name, null );
                        }
                    }
                    catch ( IllegalArgumentException ie ) {
                        ; // Should not happen
                    }
                    catch ( Exception e ) {
                        ; // Should not happen
                    }
                }
            }
        }
    }


    /**
     * @param bean1
     * @param bean2
     * @param bCaseInsensitive 不区分大小写
     * @param fieldNames       字段列表
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public static boolean checkSameProperty( Object bean1, Object bean2, boolean bCaseInsensitive, String... fieldNames ) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // Do nothing unless both arguments have been specified
        if ( (bean1 == null) && (bean2 == null) ) {
            return true;
        }
        if ( fieldNames == null || fieldNames.length == 0 ) {
            return false;
        }
        if ( (bean1 == null) || (bean2 == null) ) {
            return false;
        }

        PropertyDescriptor origDescriptors1[] = PropertyUtils.getPropertyDescriptors( bean1 );
        PropertyDescriptor origDescriptors2[] = PropertyUtils.getPropertyDescriptors( bean2 );
        List<String> fnList = new ArrayList<>();
        for ( String name : fieldNames ) {
            if ( bCaseInsensitive ) { //不区分大小写
                fnList.add( name.toLowerCase() );
            }
            else {
                fnList.add( name );
            }
        }
        Map<String, String> nameMap1 = new HashMap<>();
        Map<String, String> nameMap2 = new HashMap<>();
        for ( int i = 0; i < origDescriptors1.length; i++ ) {
            String name = origDescriptors1[i].getName();
            if ( bCaseInsensitive ) {
                nameMap1.put( name.toLowerCase(), name );
            }
            else {
                nameMap1.put( name, name );
            }
        }
        for ( String s : fnList ) {
            if ( !nameMap1.containsKey( s.toLowerCase() ) ) {
                return false;
            }
        }

        for ( int i = 0; i < origDescriptors2.length; i++ ) {
            String name = origDescriptors2[i].getName();
            if ( bCaseInsensitive ) {
                nameMap2.put( name.toLowerCase(), name );
            }
            else {
                nameMap2.put( name, name );
            }
        }
        for ( String s : fnList ) {
            if ( !nameMap2.containsKey( s ) ) {
                return false;
            }
        }

        for ( String name : fnList ) {
            String name1 = nameMap1.get( name );
            String name2 = nameMap2.get( name );
            if ( !PropertyUtils.isReadable( bean1, name1 ) || !PropertyUtils.isReadable( bean2, name2 ) ) {
                return false;
            }
            Object dataval1 = PropertyUtils.getSimpleProperty( bean1, name1 );
            Object dataval2 = PropertyUtils.getSimpleProperty( bean2, name2 );

            if ( dataval1 == null && dataval2 == null ) {
                continue;
            }
            if ( dataval1 == null || dataval2 == null ) {
                return false;
            }
            if ( !dataval1.equals( dataval2 ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * 复制指定属性值
     *
     * @param source
     * @param target
     * @param bCaseInsensitive 不区分大小写
     * @param fieldNames
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public static boolean copyBeanProp( Object source, Object target, boolean bCaseInsensitive, String... fieldNames ) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if ( (source == null) || (target == null) ) {
            return false;
        }

        PropertyDescriptor origDescriptors1[] = PropertyUtils.getPropertyDescriptors( source );
        PropertyDescriptor origDescriptors2[] = PropertyUtils.getPropertyDescriptors( target );
        List<String> fnList = new ArrayList<>();
        for ( String name : fieldNames ) {
            if ( bCaseInsensitive ) { //不区分大小写
                fnList.add( name.toLowerCase() );
            }
            else {
                fnList.add( name );
            }
        }
        Map<String, String> nameMap1 = new HashMap<>();
        Map<String, String> nameMap2 = new HashMap<>();
        for ( int i = 0; i < origDescriptors1.length; i++ ) {
            String name = origDescriptors1[i].getName();
            if ( bCaseInsensitive ) {
                nameMap1.put( name.toLowerCase(), name );
            }
            else {
                nameMap1.put( name, name );
            }
        }

        for ( int i = 0; i < origDescriptors2.length; i++ ) {
            String name = origDescriptors2[i].getName();
            if ( bCaseInsensitive ) {
                nameMap2.put( name.toLowerCase(), name );
            }
            else {
                nameMap2.put( name, name );
            }
        }

        for ( String name : fnList ) {
            String name1 = nameMap1.get( name );
            String name2 = nameMap2.get( name );
            if ( name1 == null || name2 == null ) continue;
            if ( !PropertyUtils.isReadable( source, name1 ) || !PropertyUtils.isWriteable( target, name2 ) ) {
                continue;
            }
            Object dataval = PropertyUtils.getSimpleProperty( source, name1 );

            if ( dataval == null ) {
                getInstance().setSimpleProperty( target, name2, null );
            }
            else {
                getInstance().setSimpleProperty( target, name2, dataval );
            }
        }
        return true;
    }

    public MyBeanUtils() {
        super();
    }
}
