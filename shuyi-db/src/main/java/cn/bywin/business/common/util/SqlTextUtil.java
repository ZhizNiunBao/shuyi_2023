package cn.bywin.business.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlTextUtil {

    /**
     * 该类提供格式化JSON字符串的方法。
     * 该类的方法formatJson将JSON字符串格式化，方便查看JSON数据。
     * <p>例如：
     * <p>JSON字符串：["yht","xzj","zwy"]
     * <p>格式化为：
     * <p>[
     * <p>     "yht",
     * <p>     "xzj",
     * <p>     "zwy"
     * <p>]
     *
     * <p>使用算法如下：
     * <p>对输入字符串，追个字符的遍历
     * <p>1、获取当前字符。
     * <p>2、如果当前字符是前方括号、前花括号做如下处理：
     * <p>（1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
     * <p>（2）打印：当前字符。
     * <p>（3）前方括号、前花括号，的后面必须换行。打印：换行。
     * <p>（4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
     * <p>（5）进行下一次循环。
     * <p>3、如果当前字符是后方括号、后花括号做如下处理：
     * <p>（1）后方括号、后花括号，的前面必须换行。打印：换行。
     * <p>（2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
     * <p>（3）打印：当前字符。
     * <p>（4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
     * <p>（5）继续下一次循环。
     * <p>4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
     * <p>5、打印：当前字符。
     *

     */

        /**
         * 单位缩进字符串。
         */
        private static String SPACE = "    ";

        /**
         * 返回格式化JSON字符串。
         *
         * @param json 未格式化的JSON字符串。
         * @return 格式化的JSON字符串。
         */
        public static String formatJson(String json)
        {
            StringBuffer result = new StringBuffer();

            int length = json.length();
            int number = 0;
            char key = 0;

            //遍历输入字符串。
            for (int i = 0; i < length; i++)
            {
                //1、获取当前字符。
                key = json.charAt(i);

                //2、如果当前字符是前方括号、前花括号做如下处理：
                if((key == '[') || (key == '{') )
                {
                    //（1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
                    if((i - 1 > 0) && (json.charAt(i - 1) == ':'))
                    {
                        result.append('\n');
                        result.append(indent(number));
                    }

                    //（2）打印：当前字符。
                    result.append(key);

                    //（3）前方括号、前花括号，的后面必须换行。打印：换行。
                    result.append('\n');

                    //（4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
                    number++;
                    result.append(indent(number));

                    //（5）进行下一次循环。
                    continue;
                }

                //3、如果当前字符是后方括号、后花括号做如下处理：
                if((key == ']') || (key == '}') )
                {
                    //（1）后方括号、后花括号，的前面必须换行。打印：换行。
                    result.append('\n');

                    //（2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
                    number--;
                    result.append(indent(number));

                    //（3）打印：当前字符。
                    result.append(key);

                    //（4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
                    if(((i + 1) < length) && (json.charAt(i + 1) != ','))
                    {
                        result.append('\n');
                    }

                    //（5）继续下一次循环。
                    continue;
                }

                //4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
                if((key == ','))
                {
                    result.append(key);
                    result.append('\n');
                    result.append(indent(number));
                    continue;
                }

                //5、打印：当前字符。
                result.append(key);
            }

            return result.toString();
        }

        /**
         * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
         *
         * @param number 缩进次数。
         * @return 指定缩进次数的字符串。
         */
        private static String indent(int number)
        {
            StringBuffer result = new StringBuffer();
            for(int i = 0; i < number; i++)
            {
                result.append(SPACE);
            }
            return result.toString();
        }


    /**
     * 清除语句中注释
     * @param sqlContent
     * @return
     * @throws RuntimeException
     */
    public static String removeRemark(String sqlContent) throws RuntimeException {

        if(StringUtils.isBlank( sqlContent ) ){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String temp = sqlContent;

        int idx1 = temp.indexOf("/*");
        while (idx1 >= 0) {
            sb.append(temp.substring(0, idx1));
            temp = temp.substring(idx1 + 2);
            int idx2 = temp.indexOf("*/");
            if (idx2 < 0) {
                //System.out.println("块注释不完整");
                throw new RuntimeException("块注释不完整");
            }
            temp = temp.substring(idx2 + 2);
            idx1 = temp.indexOf("/*");
        }
        sb.append(temp);
        temp = sb.toString();

        if (temp.indexOf("*/") >= 0) {
            throw new RuntimeException("块注释不完整");
        }
        sb = new StringBuilder();
        idx1 = temp.indexOf("--");
        while (idx1 >= 0) {
            sb.append(temp.substring(0, idx1));
            temp = temp.substring(idx1 + 2);
            int idx2 = temp.indexOf("\n");
            if (idx2 < 0) {
                temp = "";
            } else {
                temp = temp.substring(idx2 + 1);
            }
            idx1 = temp.indexOf("--");
        }
        sb.append(temp);
        return sb.toString().trim();
    }

    public static List<String> splitToSql(String sqlContent) throws RuntimeException {
        List<String> retList = new ArrayList<>();
        if( sqlContent == null )
            return retList;
        StringBuilder sb = new StringBuilder();
        String temp = sqlContent;

        int idx1 = temp.indexOf("/*");
        while (idx1 >= 0) {
            sb.append(temp.substring(0, idx1));
            temp = temp.substring(idx1 + 2);
            int idx2 = temp.indexOf("*/");
            if (idx2 < 0) {
                //System.out.println("块注释不完整");
                throw new RuntimeException("块注释不完整");
            }
            temp = temp.substring(idx2 + 2);
            idx1 = temp.indexOf("/*");
        }
        sb.append(temp);
        temp = sb.toString();

        if (temp.indexOf("*/") >= 0) {
            //System.out.println("块注释不完整");
            throw new RuntimeException("块注释不完整");
        }
//        if( true ){
//            System.out.println(temp);
//            return ;
//        }

        sb = new StringBuilder();
        idx1 = temp.indexOf("--");
        while (idx1 >= 0) {
            sb.append(temp.substring(0, idx1));
            temp = temp.substring(idx1 + 2);
            int idx2 = temp.indexOf("\n");
            if (idx2 < 0) {
                temp = "";
            } else {
                temp = temp.substring(idx2 + 1);
            }
            idx1 = temp.indexOf("--");
        }
        sb.append(temp);
        temp = sb.toString();

        temp = temp.replaceAll("(\\r\\n)+", "\r\n").replaceAll("(\\n)+", "\n");
        final String[] split = temp.split(";");

        StringBuilder sb1 = new StringBuilder();
        for (String data : split) {
            if( data.trim().length()>0) {
                if( sb1.length() ==0  ){
                    sb1.append(data);
                }
                else {
                    if (sb1.charAt(sb1.length() - 1) == '\'' && data.charAt(0) == '\'') {
                        sb1.append(";").append(data);
                    } else {
                        retList.add(sb1.toString());
                        sb1 = new StringBuilder();
                        sb1.append(data);
                    }
                }
            }
        }

        if( sb1.length()>0 ){
            retList.add(sb1.toString() );
        }
        return retList;
    }

    public static List<String> splitToWord(String sqlContent) throws RuntimeException {
        List<String> retList = new ArrayList<>();
        if( StringUtils.isBlank( sqlContent ) )
            return retList;
        StringBuilder sb = new StringBuilder();
        String temp = sqlContent.trim();

        List<String> split1 = new ArrayList<>();

        while( true ){ //  '块'注释
            int idx = temp.indexOf("/*");
            if( idx >=0 ) {
                String dat1 = temp.substring(0, idx).trim();
                if( dat1.length()>0)
                    split1.add( dat1 );
                temp = temp.substring( idx ) ;
                int idxE = temp.indexOf("*/");
                idx = 0;

                if (idxE <= 0) {
                    //System.out.println(temp);
                    throw new RuntimeException("块注释不完整");
                } else {
                    dat1 = temp.substring(idx, idxE+2);
                    split1.add(dat1);
                    temp = temp.substring(idxE + 2 ).trim();
                    if (temp.length() == 0)
                        break;
                }
            }
            else{
                split1.add(temp);
                break;
            }
        }

        for (String s : split1) {
            temp = s.trim();
            if( temp.startsWith("/*"))
                retList.add(temp);
            else
            {//  '行'注释
                final String[] split2 = temp.split("\\n+");
                for (String s1 : split2) {
                    int idx = s1.indexOf("--");
                    if( idx >=0 ){
                        String s2 = s1.substring(0,idx).trim();
                        if( s2.length()> 0 )
                            Collections.addAll(retList, addSpaceAndSplit(s2));
                        retList.add(s1.substring(idx).trim());
                    }
                    else{
                        Collections.addAll(retList, addSpaceAndSplit(s1));
                    }
                }
            }
        }
        int size = retList.size()-1;
        for( int i = size;i >=0; i--){
            if( retList.get(i).trim().length()==0){
                retList.remove(i);
            }
        }
        return retList;
    }

    /**
     * 将语句中符号前后加空格
     * @param ss
     * @return
     */
    private static String[] addSpaceAndSplit( String ss ){
        return ss.replaceAll("="," = ").replaceAll("\\("," ( ").replaceAll("\\)"," ) ")
                .replaceAll(","," , ").replaceAll(";"," ; ").trim().split("\\s+");
    }



    /**
     * 判断注释块是否有分号";"
     * @param sqlContent
     * @return
     */
    public static boolean checkRemarkSemicolon(String sqlContent) {
        if( sqlContent == null )
            return false;
        String reg ="--.*?;";
        Pattern pat = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pat.matcher(sqlContent);
        if( matcher.find() ) {
            return true;
        }
        reg ="(\\/\\*)(.|\\s)*?(\\*\\/)";
        pat = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
        matcher = pat.matcher(sqlContent);
        while( matcher.find() ) {
            final String group = matcher.group();
            if( group.indexOf(";")>=0)
                return true;
        }
        return false;
    }


    public static String chgSql(String strsql){
        String sql = strsql;
        sql = sql.replaceAll("--.*\\s?"," ");//单行注释
        sql = sql.replaceAll("\\s+"," " );//匹配任意的空白符
        sql = sql.replaceAll("\\/\\*(\\s|.)*?\\*\\/"," ");//多行注释

        while( true) {
            int len = sql.length();
            // sql = sql.replaceAll("\\(([^\\(\\)]*)\\)", " aaa ");
            sql = sql.replaceAll("\\(([^\\(\\)])*?\\)"," ddd ");//一对()
            if( sql.length() == len )
                break;
        }
        return sql;
    }

    public static  boolean isSelectSql(String sql) {
        if (sql == null || StringUtils.isBlank(sql))
            return false;
        String tmp = chgSql(sql).toLowerCase().trim();
        //String ret ="^select.*from";
        String reg ="^((with.*)\\s+)?(select\\s+).*";
        return tmp.matches( reg );
    }

    public static  boolean isShowSql(String sql){
        if (sql == null || StringUtils.isBlank(sql))
            return false;
        String tmp = chgSql(sql).toLowerCase().trim();
        //String ret ="^select.*from";
        String reg ="^(show\\s+).*";
        return tmp.matches( reg );
    }

    public static  boolean isExplainSql(String sql){
        if (sql == null || StringUtils.isBlank(sql))
            return false;
        String tmp = chgSql(sql).toLowerCase().trim();
        //String ret ="^select.*from";
        String reg ="^(explain\\s+).*";
        return tmp.matches( reg );
    }

    /**
     * 限制查询条数
     * @param sql
     * @return
     */
    public static String limitSelet( String sql , int size ){
        String not1 = "(\\s)+LIMIT(\\s)+(\\d)+(\\s)*$";
        Pattern pat = Pattern.compile(not1, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pat.matcher(sql);
        if (matcher.find()){
            return matcher.replaceFirst( " LIMIT "+size);
        }
        return sql +" LIMIT "+ size;
        /*final int limit = sql.toLowerCase().lastIndexOf(" limit ");
        if( limit >0 ) {
            String temp = sql.substring(limit + 6 );
            temp = temp.replaceAll(" ", "").replaceAll("\\\r|\\\n", "");
            if( !temp.matches("\\d+")){
                return sql +" limit 100 ";
            }
            else{
                long cnt = Long.parseLong(temp);
                if( cnt >100 ){
                    temp = sql.substring(0,limit) +" limit 100 ";
                }
            }
            return sql;
        }
        else
            return sql +" limit 100 ";*/
    }

    /**
     * 判断是否危险语句 drop delete truncate
     * @param sql
     * @return
     */
    public static  boolean isDanger1Sql( String sql ){
        if (sql == null || StringUtils.isBlank(sql))
            return false;
        String reg = "([^\\w|^]|^)+(drop|delete|truncate)\\s+";
        Pattern pat = Pattern.compile( reg,Pattern.CASE_INSENSITIVE );
        return  pat.matcher( sql ).find() ;
    }

//    public static void main(String[] args) {
//        String sql ="drop table dds";
//        System.out.println(isDanger1Sql(sql) );
//        sql ="dropdd table dds";
//        System.out.println(isDanger1Sql(sql) );
//        sql ="sdrop table dds";
//        System.out.println(isDanger1Sql(sql) );
//        sql ="\tdrop table dds";
//        System.out.println(isDanger1Sql(sql) );
//        sql ="_drop table dds";
//        System.out.println(isDanger1Sql(sql) );
//    }
}
