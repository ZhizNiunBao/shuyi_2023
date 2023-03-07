/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.bywin.business.common.util;

/**
 * web application constants
 */
public class Constants {

    //用户密码加密 des 密码 token
    public final static String DESPWD = "bykg20kforPPChn";
    public static final String AUTHORIZATION = "Authorization";
    public final static String DESTOKEN = "byfl22kforPPChn";
    public static final String CLIENT_PARA_NAME = "KgClientUrl";
    public final static String FLSYSTEM = "flsystem";
    public final static String ADMIN = "admin";

    //临时文件目录
    public final static String dataPath = "/data/";
    public final static String csvType = ".csv";
    public final static String csvPath = "csvfile";
    public final static String edaHtmlPath = "/edahtml/";

    /**
     * JG操作最大返回数量
     */
    public static final Integer MAX_RESULT_COUNT = 100;
    /**
     * 参数类型
     */
    public static final String MODEL_TYPE = "4";
    public static final String MODEL_TYPE_NAME = "模型工厂类型";
    public static final String IDATAFORMAT_TYPE = "1";
    public static final String IDATAFORMAT_TYPE_NAME = "数据格式类型";
    public static final String DATASOURCE_TYPE = "8";
    public static final String DATASOURCE_DRIVER = "81";
    public static final String ODPS_ENDPOINT = "82";
    public static final String JDBC_URL = "83";
    public static final String DATASOURCE_TYPE_NAME = "数据源";
    public static final String IELEMENT_TYPE = "5";
    public static final String IELEMENT_TYPE_NAME = "算子类型";
    public static final String MODELSUPERMARKET_TYPE = "3";
    public static final String MODELSUPERMARKET_TYPE_NAME = "模型超市类型";
    public static final String IDATA_TYPE = "2";
    public static final String IDATA_TYPE_NAME = "智能数据类型";
    public static final String RUNENVIRONMENT_TYPE = "6";
    public static final String RUNENVIRONMENT_TYPE_NAME = "运行环境";
    public static final String SCRIPT_TYPE = "7";
    public static final String SCRIPT_TYPE_NAME = "脚本类型";
    public static final String IELEMENT_PARAM_TYPE = "10";
    public static final String IELEMENT_PARAM_TYPE_NAME = "参数类型";
    public static final String IELEMENT_PARAM_SHOW_TYPE = "9";
    public static final String IELEMENT_PARAM_SHOW_TYPE_NAME = "参数展示方式";
    public static final String SQL_DATA_TYPE = "11";
    public static final String SQL_00DATA_TYPE_NAME = "sql数据类型";
    public static final String HOSTS_TYPE = "12";
    public static final String HOSTS_TYPE_NAME = "服务器类型";
    public static final String APPROVAL_TYPE = "13";
    public static final String APPROVAL_TYPE_NAME = "审批类型";
    public static final String ML_IELEMENT_TYPE = "14";
    public static final String ML_IELEMENT_TYPE_NAME = "机器学习算子类型";
    public static final String SYS_IELEMENT_TYPE = "15";
    public static final String SYS_IELEMENT_TYPE_NAME = "系统算子类型";
    public static final String IELESUPERMARKET_TYPE = "SZCS";  //算子超市
    /**
     * session user

     public static final String SESSION_USER = "session.user";
     public static final String SESSION_ID = "sessionId";
     public static final String PASSWORD_DEFAULT = "******";
     */

    /**
     * database type
     */

    public static final String DRIVERTYPE = "MYSQL,STARROCKS,CLICKHOUSE";
    public static final String MYSQL = "MYSQL";
    public static final String STARROCKS = "STARROCKS";
    public static final String HIVE = "HIVE";
    public static final String SPARK = "SPARK";
    public static final String ORACLE = "ORACLE";
    public static final String CLICKHOUSE = "CLICKHOUSE";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String KUNGRAPH = "KUNGRAPH";
    public static final String DM = "DM";
    public static final String TIDB = "TIDB";
    public static final String HBASE = "HBASE";
    public static final String MONGODB = "MONGODB";
    public static final String ODPS = "ODPS";


    /**
     * driver
     */
    public static final String POSTGRESQLDRIVER = "org.postgresql.Driver";
    public static final String MYSQLDRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String HIVEDRIVER = "org.apache.hive.jdbc.HiveDriver";
    public static final String ORACLEDRIVER = "oracle.jdbc.driver.OracleDriver";
    public static final String CLICKHOUSELDRIVER = "ru.yandex.clickhouse.ClickHouseDriver";
    public static final String SQLSERVERDRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String DMDRIVER = "dm.jdbc.driver.DmDriver";
    public static final String ODPSDRIVER = "com.aliyun.odps.jdbc.OdpsDriver";


    public static final String SPLIT_AT = "@";
    public static final String SPLIT_SCOLON = ":";
    public static final String AND = "AND";
    public static final String SPLIT_COMMA = ",";
    public static final String SPLIT_COLON = ";";
    public static final String SPLIT_POINT = ".";
    public static final String EQUAL = "=";
    public static final String SPLIT_AMPERSAND = "&";
    public static final String SPACE = " ";
    public static final String STRING_BLANK = "";
    public static final String SPLIT_HYPHEN = "-";
    public static final String SPLIT_DIVIDE = "/";
    public static final String SPLIT_STAR = "*";
    public static final String SPLIT_QUESTION = "?";
    public static final String MONGO_URL_PREFIX = "mongodb://";


    public static final String MYSQL_DATABASE = "Unknown database";
    public static final String MYSQL_CONNEXP = "Communications link failure";
    public static final String MYSQL_ACCDENIED = "Access denied";
    public static final String MYSQL_TABLE_NAME_ERR1 = "Table";
    public static final String MYSQL_TABLE_NAME_ERR2 = "doesn't exist";
    public static final String MYSQL_SELECT_PRI = "SELECT command denied to user";
    public static final String MYSQL_COLUMN1 = "Unknown column";
    public static final String MYSQL_COLUMN2 = "field list";
    public static final String MYSQL_WHERE = "where clause";

    public static final String ORACLE_DATABASE = "ORA-12505";
    public static final String ORACLE_CONNEXP = "The Network Adapter could not establish the connection";
    public static final String ORACLE_ACCDENIED = "ORA-01017";
    public static final String ORACLE_TABLE_NAME = "table or view does not exist";
    public static final String ORACLE_SELECT_PRI = "insufficient privileges";
    public static final String ORACLE_SQL = "invalid identifier";

    /*数据血缘分析用*/
    public static final String TABLE_COLUMN_SEPARATOR = ".";
    public static final String TRANSFERRED_TABLE_COLUMN_SEPARATOR = "\\.";
    public static final String MULTY_COLUMN_SEPARATOR = ",";
    public static final String TABLE_ALIAS_SEPARATOR = "#";
    /**
     * 默认的catalog以及database
     */
    public static final String DEFAULT_CATALOG = "default_catalog";
    public static final String DEFAULT_DATABASE = "default_database";
    public static final String SQL_BACK_QUOTE = "`";
    /**
     * 忽略上报字段
     */
    public static final String[] IGNORE_FIELDS = {"proctime", "rowtime"};


    public static final String syspara_SystemCode = "SystemCode";

    //任务类型常量
    public static final String job_DataCollisionModel = "datacollisionmodel"; //数据碰撞
    public static final String job_DataOperatorModel = "dataoperatormodel";//数据模型
    public static final String job_DataDevelopMentmodel = "datadevelopmentmodel";//数据开发
    public static final String job_UserProfileLabelGroupGen = "userprofilelabelgroupgen";//用户标签分群生成

    //olk 数据源标志
    public static final String dchetu = "dchetu";
}