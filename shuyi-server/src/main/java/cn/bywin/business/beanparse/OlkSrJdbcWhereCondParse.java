package cn.bywin.business.beanparse;

import cn.bywin.business.bean.bydb.DatasetWhereCond;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.util.JdbcTypeToJavaTypeUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlkSrJdbcWhereCondParse {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    String errMsg;
    String debugMsg;
    String whereSql;

    public String getErrMsg() {
        return errMsg;
    }

    public String getDebugMsg() {
        return debugMsg;
    }

    public String getWhereSql() {
        return whereSql;
    }

//    public boolean paraseCond(String whereCond, List<TBydbDsEntityDo> entList, List<TBydbDsColumnDo> colList) {
//        errMsg = "";
//        debugMsg = "";
//        whereSql = "";
//        //relOnList = new ArrayList<>();
//        try {
//            if (StringUtils.isBlank(whereCond))
//                return true;
//            Map<String, TBydbDsColumnDo> colMap = colList.stream().collect(Collectors.toMap(x -> x.getId(), y -> y));
//            Map<String, TBydbDsEntityDo> entMap = entList.stream().collect(Collectors.toMap(x -> x.getId(), y -> y));
//            DatasetWhereCond cond = JsonUtil.deserialize(whereCond, DatasetWhereCond.class);
//            StringBuilder sb = new StringBuilder();
//            if (cond == null || cond.getConditions() == null || cond.getConditions().size() == 0) {
//                return true;
//            }
//            if (!dealCond(cond, cond.getConditions(), entMap, colMap, sb)) {
//                return false;
//            }
//            whereSql = sb.toString();
//            return true;
//        } catch (Exception e) {
//            logger.error("解析失败", e);
//            errMsg = "解析失败";
//        }
//        return false;
//
//    }

//    private boolean dealCond(DatasetWhereCond pc, List<DatasetWhereCond> subCond, Map<String, TBydbDsEntityDo> entMap, Map<String, TBydbDsColumnDo> colMap, StringBuilder sb) throws Exception {
//
//        if (subCond == null || subCond.size() == 0) {
//            setError("内容为空", "subCond is null or subCond.size()==0 ");
//            return false;
//        }
//
//        boolean bfirst = true;
//        sb.append(" ( ");
//        for (DatasetWhereCond cond : subCond) {
//            if (!bfirst) {
//                sb.append("\r\n").append(pc.getRelation());
//            }
//            bfirst = false;
//            if (cond.getConditions() != null && cond.getConditions().size() > 0) {
//                boolean bok = dealCond(cond, cond.getConditions(), entMap, colMap, sb);
//                if (!bok) {
//                    return false;
//                }
//                continue;
//            }
//            if (StringUtils.isBlank(cond.getFieldId1())) {
//                setError(String.format("%s对应字段Id为空", cond.getField1()), cond.impInfo(""));
//                return false;
//            }
//            TBydbDsColumnDo columnDo1 = colMap.get(cond.getFieldId1());
//            if (columnDo1 == null) {
//                setError(String.format("%s(%s)对应字段不存在", cond.getField1(), cond.getFieldId1()), cond.impInfo(""));
//                return false;
//            }
//
//            TBydbDsEntityDo entityDo = entMap.get(columnDo1.getEntityId());
//
//            if (entityDo == null) {
//                setError(String.format("%s(%s)对应表不存在", cond.getField1(), cond.getFieldId1()), cond.impInfo(""));
//                return false;
//            }
//            sb.append(" ").append(entityDo.getAliasName()).append(".").append(columnDo1.getColumnName()).append(" ");
//
//            String field1 = String.format("%s.%s", entityDo.getEntityName(), columnDo1.getColumnName());
//
//            if (StringUtils.isNotBlank(cond.getFieldId2())) {
//                TBydbDsColumnDo columnDo2 = colMap.get(cond.getFieldId1());
//                if (columnDo1 == null) {
//                    setError(String.format("%s(%s)对应字段不存在", cond.getField2(), cond.getFieldId2()), cond.impInfo(""));
//                    return false;
//                }
//
//                entityDo = entMap.get(columnDo2.getEntityId());
//
//                if (entityDo == null) {
//                    setError(String.format("%s(%s)对应表不存在", cond.getField2(), cond.getFieldId2()), cond.impInfo(""));
//                    return false;
//                }
//                String field2 = String.format("%s.%s", entityDo.getEntityName(), columnDo2.getColumnName());
//                if (!makeAttributeSql(sb, cond.getFunction(), String.format("%s.%s", entityDo.getAliasName(), columnDo2.getColumnName()), field1, field2, cond.impInfo(""))) {
//                    return false;
//                }
//                sb.append("\r\n");
//            } else {
//                if ("boolean".equalsIgnoreCase(columnDo1.getColumnType())) {
//                    if (!makeBooleanSql(sb, cond.getFunction(), cond.getParams(), field1, cond.impInfo("bool"))) {
//                        return false;
//                    }
//
//                }
//                if ("date".equalsIgnoreCase(columnDo1.getColumnType())) {
//                    if (!makeDateSql(sb, cond.getFunction(), "", null, null, cond.getParams(), field1, cond.impInfo("date"))) {
//                        return false;
//                    }
//                } else {
//                    String dateType = "STRING";
//                    if ("Integer".equalsIgnoreCase(columnDo1.getColumnType())
//                            || "Double".equalsIgnoreCase(columnDo1.getColumnType())
//                            || "Float".equalsIgnoreCase(columnDo1.getColumnType())
//                            || "Long".equalsIgnoreCase(columnDo1.getColumnType())) {
//                        dateType = "NUMBER";
//                    }
//                    if (!makeOtherTypeSql(sb, dateType, cond.getFunction(), cond.getParams(), field1, cond.impInfo(dateType))) {
//                        return false;
//                    }
//                }
//            }
//        }
//        sb.append(" )\r\n ");
//        return true;
//    }

    private void setError(String msg, String other) {
        this.errMsg = msg;
        this.debugMsg = other;

    }

    private boolean makeAttributeSql(StringBuilder sb, final String fun, String rightAttr, String leftEntAttr, String rightEntAttr, String impInfo) {

        switch (fun) {
            case "eq":
                sb.append(" = ").append(rightAttr).append(" ");
                break;
            case "noteq":
                sb.append(" != ").append(rightAttr).append(" ");
                break;
            case "empty":
            case "benull":
                sb.append(" IS NULL ");
                break;
            case "notempty":
            case "notnull":
                sb.append(" IS NOT NULL ");
                break;
            default:
                setError(String.format("%s 与 %s关系 %s 未设置或无效", leftEntAttr, rightEntAttr, "commen:" + fun), impInfo);
                return false;
            //sb.append(  " " + fun + " " );
        }
        return true;
    }


    public boolean paraseComCond(String whereCond, List<TOlkModelElementDo> entList, List<TOlkModelFieldDo> colList) {
        errMsg = "";
        debugMsg = "";
        whereSql = "";
        //relOnList = new ArrayList<>();
        try {
            if (StringUtils.isBlank(whereCond))
                return true;
            Map<String, TOlkModelFieldDo> colMap = colList.stream().collect(Collectors.toMap(x -> x.getId(), y -> y));
            Map<String, TOlkModelFieldDo> idFieldMap = colList.stream().collect(Collectors.toMap(x -> x.getFieldAlias(), y -> y));

            Map<String, TOlkModelElementDo> entMap = entList.stream().collect(Collectors.toMap(x -> x.getId(), y -> y));
            DatasetWhereCond cond = JsonUtil.deserialize(whereCond, DatasetWhereCond.class);
            StringBuilder sb = new StringBuilder();
            if (cond == null || cond.getConditions() == null || cond.getConditions().size() == 0) {
                return true;
            }
            if (!dealComCond(cond, cond.getConditions(), entMap, colMap,idFieldMap, sb)) {
                return false;
            }
            whereSql = sb.toString();
            return true;
        } catch (Exception e) {
            logger.error("解析失败", e);
            errMsg = "解析失败";
        }
        return false;

    }

    private boolean dealComCond(DatasetWhereCond pc, List<DatasetWhereCond> subCond, Map<String, TOlkModelElementDo> entMap,
                                Map<String, TOlkModelFieldDo> colMap,Map<String, TOlkModelFieldDo> idFieldMap, StringBuilder sb) throws Exception {

        if (subCond == null || subCond.size() == 0) {
            setError("内容为空", "subCond is null or subCond.size()==0 ");
            return false;
        }

        boolean bfirst = true;
        sb.append(" ( ");
        for (DatasetWhereCond cond : subCond) {
            if (!bfirst) {
                sb.append(" ").append(pc.getRelation());
            }
            bfirst = false;
            if (cond.getConditions() != null && cond.getConditions().size() > 0) {
                boolean bok = dealComCond(cond, cond.getConditions(), entMap, colMap,idFieldMap, sb);
                if (!bok) {
                    return false;
                }
                continue;
            }
            if (StringUtils.isBlank(cond.getFieldId1())) {
                setError(String.format("%s对应字段Id为空", cond.getField1()), cond.impInfo(""));
                return false;
            }
            TOlkModelFieldDo columnDo1 = colMap.get(cond.getFieldId1());
            if (columnDo1 == null) {
                setError(String.format("%s(%s)对应字段不存在", cond.getField1(), cond.getFieldId1()), cond.impInfo(""));
                return false;
            }
            TOlkModelElementDo entityDo = entMap.get(columnDo1.getElementId());

            if (entityDo == null) {
                setError(String.format("%s(%s)对应表不存在", cond.getField1(), cond.getFieldId1()), cond.impInfo(""));
                return false;
            }
            sb.append(" ")
                    .append(getDataFieldName( columnDo1, idFieldMap)).append(" ");

            String field1 = String.format("%s", getDataFieldName( columnDo1, idFieldMap));

            if (StringUtils.isNotBlank(cond.getFieldId2())) {
                TOlkModelFieldDo columnDo2 = colMap.get(cond.getFieldId1());
                if (columnDo1 == null) {
                    setError(String.format("%s(%s)对应字段不存在", cond.getField2(), cond.getFieldId2()), cond.impInfo(""));
                    return false;
                }
                entityDo = entMap.get(columnDo2.getElementId());
                if (entityDo == null) {
                    setError(String.format("%s(%s)对应表不存在", cond.getField2(), cond.getFieldId2()), cond.impInfo(""));
                    return false;
                }
                String field2 = String.format("%s", getDataFieldName( columnDo2, idFieldMap));
                if (!makeAttributeSql(sb, cond.getFunction(), String.format("%s", columnDo2.getFieldName()), field1, field2, cond.impInfo(""))) {
                    return false;
                }
                sb.append(" ");
            } else {
                if ("boolean".equalsIgnoreCase(JdbcTypeToJavaTypeUtil.chgType(columnDo1.getFieldType()))) {//JdbcTypeToJavaTypeUtil.chgType(dscol.getOrgType())
                    if (!makeBooleanSql(sb, cond.getFunction(), cond.getParams(), field1, cond.impInfo("bool"))) {
                        return false;
                    }

                }
                if ("date".equalsIgnoreCase(JdbcTypeToJavaTypeUtil.chgType(columnDo1.getFieldType()))) {
                    if (!makeDateSql(sb, cond.getFunction(), "", null, null, cond.getParams(), field1, cond.impInfo("date"))) {
                        return false;
                    }
                } else {
                    String dateType = "STRING";
                    if ("Integer".equalsIgnoreCase(JdbcTypeToJavaTypeUtil.chgType(columnDo1.getFieldType()))
                            || "Double".equalsIgnoreCase(JdbcTypeToJavaTypeUtil.chgType(columnDo1.getFieldType()))
                            || "Float".equalsIgnoreCase(JdbcTypeToJavaTypeUtil.chgType(columnDo1.getFieldType()))
                            || "Long".equalsIgnoreCase(JdbcTypeToJavaTypeUtil.chgType(columnDo1.getFieldType()))) {
                        dateType = "NUMBER";
                    }
                    if (!makeOtherTypeSql(sb, dateType, cond.getFunction(), cond.getParams(), field1, cond.impInfo(dateType))) {
                        return false;
                    }
                }
            }
        }
        sb.append(" ) ");
        return true;
    }

    //    public static String getDataFieldName(String element, String tableAlias, String fieldName, String fieldAlias) {
//        String pre = "";
//        if (element.equals(tableAlias)) {
//            pre = fieldName;
//        } else {
//            pre = fieldAlias;
//        }
//        return pre;
//    }
    public static String getDataFieldName( TOlkModelFieldDo fieldDo,Map<String, TOlkModelFieldDo> idFieldMap) {
        String fieldName = fieldDo.getFieldName();
        String outName =fieldDo.getTableAlias().concat(".").concat(fieldName);
//        if (isFlag && element.equals(fieldDo.getTableAlias())) {
//            fieldName = fieldDo.getFieldAlias();
//        } else {
//            fieldName = fieldDo.getFieldName();
//        }

        if (StringUtils.isNotBlank(fieldDo.getAggregation()) && !fieldDo.getAggregation().contains("{")) {

            String agg = fieldDo.getAggregation().toUpperCase();
            if (agg.startsWith("distinct")) {
                agg = "distinct count".toUpperCase();
            }
            outName = agg.concat(" ( ").concat(fieldDo.getTableAlias()).concat(".").
                    concat(fieldName).concat(" ) ");
//            if (StringUtils.isNotBlank(fieldDo.getOrderFunc())) {
//                outName = fieldDo.getOrderFunc().replace(fieldDo.getTableAlias().concat(".").concat(fieldName)
//                        , outName);
//            }
        }
        if (StringUtils.isNotBlank(fieldDo.getOrderFunc())) {
            String funcF =fieldDo.getOrderFunc().replaceAll(" ", "").replaceAll("\\\r|\\\n", "");;

            String quStr = funcF.substring(funcF.indexOf("(") + 1, funcF.indexOf(")"));
            String funcStr = funcF.substring(0, funcF.indexOf("("));
            List<String> funcField = new ArrayList<>();
            String[] strs = quStr.split(",");
            for (String str : strs) {
                TOlkModelFieldDo fieldDos = idFieldMap.get(str);
                if (fieldDos != null) {
                    String outNames = "";
                    if (StringUtils.isNotBlank(fieldDos.getAggregation()) && !fieldDos.getAggregation().contains("{")) {
                        String agg = fieldDos.getAggregation().toUpperCase();
                        if (agg.startsWith("distinct")) {
                            agg = "distinct count".toUpperCase();
                        }
                        outNames = agg.concat(" ( ").concat(fieldDos.getTableAlias()).concat(".").
                                concat(fieldDos.getFieldAlias()).concat(" ) ");
                        funcField.add(outNames);
                    } else {
                        funcField.add(fieldDos.getTableAlias().concat(".").concat(fieldDos.getFieldAlias()));
                    }
                } else {
                    funcField.add(str);
                }
            }
            outName=funcStr.concat("(").concat(String.join(",",funcField)).concat(")");
        }

        return outName;
    }

    public static String getDataFieldName( TOlkModelFieldDo fieldDo,String tableAliasPrex,
                                           Map<String, TOlkModelFieldDo> idFieldMap) {
        String fieldName = fieldDo.getFieldName();
        String outName =tableAliasPrex.concat(fieldName);


        if (StringUtils.isNotBlank(fieldDo.getAggregation()) && !fieldDo.getAggregation().contains("{")) {

            String agg = fieldDo.getAggregation().toUpperCase();
            if (agg.startsWith("distinct")) {
                agg = "distinct count".toUpperCase();
            }
            outName = agg.concat(" ( ").concat(tableAliasPrex).concat(fieldName).concat(" ) ");

        }
        if (StringUtils.isNotBlank(fieldDo.getOrderFunc())) {
            String funcF =fieldDo.getOrderFunc().replaceAll(" ", "").replaceAll("\\\r|\\\n", "");;

            String quStr = funcF.substring(funcF.indexOf("(") + 1, funcF.indexOf(")"));
            String funcStr = funcF.substring(0, funcF.indexOf("("));
            List<String> funcField = new ArrayList<>();
            String[] strs = quStr.split(",");
            for (String str : strs) {
                TOlkModelFieldDo fieldDos = idFieldMap.get(str);
                if (fieldDos != null) {
                    String outNames = "";
                    if (StringUtils.isNotBlank(fieldDos.getAggregation()) && !fieldDos.getAggregation().contains("{")) {
                        String agg = fieldDos.getAggregation().toUpperCase();
                        if (agg.startsWith("distinct")) {
                            agg = "distinct count".toUpperCase();
                        }
                        outNames = agg.concat(" ( ").concat(fieldDos.getTableAlias()).concat(".").
                                concat(fieldDos.getFieldAlias()).concat(" ) ");
                        funcField.add(outNames);
                    } else {
                        funcField.add(fieldDos.getTableAlias().concat(".").concat(fieldDos.getFieldAlias()));
                    }
                } else {
                    funcField.add(str);
                }
            }
            outName=funcStr.concat("(").concat(String.join(",",funcField)).concat(")");
        }

        return outName;
    }

    public boolean makeBooleanSql(StringBuilder sb, final String fun, List<String> paraList, String attrName, String impInfo) {
        List<String> valList = new ArrayList<>();
        switch (fun) {
            case "eq":
            case "noteq":

                if (paraList == null || paraList.size() != 1) {
                    setError(attrName + " 参数不能为空", impInfo);
                    return false;
                }
                for (String param : paraList) {
                    if (StringUtils.isBlank(param)) {
                        setError(attrName + " 参数有空值", impInfo);
                        return false;
                    }
                    if ("不是".equalsIgnoreCase(param) || "不".equalsIgnoreCase(param) || "否".equalsIgnoreCase(param)
                            || "0".equalsIgnoreCase(param) || "没有".equalsIgnoreCase(param)) {
                        valList.add("0");
                    } else {
                        valList.add("1");
                    }
                }
                break;
            //case "notvalue":
            case "benull":
                //case "hasvalue":
            case "notnull":
                break;
            default:
                setError(attrName + " 关系" + fun + "未设置或无效,", impInfo);
                return false;
        }

        switch (fun) {
            case "eq":
                if (valList.size() > 1) {
                    sb.append(" in ");
                    final String collect = valList.stream().collect(Collectors.joining(","));
                    sb.append(" (").append(collect).append(") ");
                } else {
                    sb.append(" = ");

                    sb.append(valList.get(0));
                }
                break;
            case "noteq":
                if (valList.size() > 1) {
                    sb.append(" not in ");
                    final String collect = valList.stream().collect(Collectors.joining(","));
                    sb.append(" (").append(collect).append(") ");
                } else {
                    sb.append(" != ");

                    sb.append(valList.get(0));
                }
                break;
//                    case "between":
//                        if (valList.size() != 2 ) {
//                            setError( eventAttrDo.getAttrName() + " 区间参数必须为两个," , fs.impInfo());
//                            return false;
//                        }
//                        sb.append(" between ");
//
//                        sb.append(" ").append(valList.get(0)).append(" and ").append(valList.get(1)).append(" ");
//                        break;

            //case "notvalue":
            case "benull":
                sb.append(" IS NULL ");
                break;
            //case "hasvalue":
            case "notnull":
                sb.append(" IS NOT NULL ");
                break;
            default:
                setError(attrName + " 关系" + fun + "未设置或无效,", impInfo);
                return false;
            //sb.append(  " " + fun + " " );
        }
        return true;
    }


    private boolean makeOtherTypeSql(StringBuilder sb, final String dataType, final String fun, List<String> paraList, String attrName, String impInfo) {

        switch (fun) {
            case "in":
            case "notin":
            case "eq":
            case "noteq":
                if (paraList == null || paraList.size() == 0) {
                    setError(attrName + " 参数不能为空", impInfo);
                    return false;
                }
                break;
            case "between":
                if (paraList == null || paraList.size() != 2) {
                    setError(attrName + " 参数必须为两个,", impInfo);
                    return false;
                }
                break;
            case "lt":
            case "let":
            case "gt":
            case "get":
            case "like":
            case "notlike":
                //case "reg":
                //case "notreg":
                if (paraList == null || paraList.size() != 1) {
                    setError(attrName + " 参数必须为1个,", dataType + "\r\n" + impInfo);
                    return false;
                }
                break;
            case "empty":
            case "benull":
            case "notempty":
            case "notnull":
                break;
            default:
                setError(attrName + " 关系" + fun + "未设置或无效,", impInfo);
                return false;
        }

        switch (fun) {
            case "in":
            case "eq":
                if (paraList.size() > 1) {
                    sb.append(" IN ");
                    if (!"NUMBER".equalsIgnoreCase(dataType)) {
                        final String collect = paraList.stream().map(x -> "'" + x + "'").collect(Collectors.joining(","));
                        sb.append(" (").append(collect).append(") ");
                    } else {
                        final String collect = paraList.stream().collect(Collectors.joining(","));
                        sb.append(" (").append(collect).append(") ");
                    }
                } else {
                    sb.append(" = ");
                    if (!"NUMBER".equalsIgnoreCase(dataType)) {
                        sb.append("'").append(paraList.get(0)).append("' ");
                    } else {
                        sb.append(paraList.get(0));
                    }
                }
                break;
            case "notin":
            case "noteq":
                if (paraList.size() > 1) {
                    sb.append(" NOT IN ");
                    if (!"NUMBER".equalsIgnoreCase(dataType)) {
                        final String collect = paraList.stream().map(x -> "'" + x + "'").collect(Collectors.joining(","));
                        sb.append(" (").append(collect).append(") ");
                    } else {
                        final String collect = paraList.stream().collect(Collectors.joining(","));
                        sb.append(" (").append(collect).append(") ");
                    }
                } else {
                    sb.append(" != ");
                    if (!"NUMBER".equalsIgnoreCase(dataType)) {
                        sb.append("'").append(paraList.get(0)).append("' ");
                    } else {
                        sb.append(paraList.get(0));
                    }
                }
                break;
            case "between":
                sb.append(" BETWEEN ");
                if (!"NUMBER".equalsIgnoreCase(dataType)) {
                    sb.append(" '").append(paraList.get(0)).append("' and '").append(paraList.get(1)).append("' ");
                } else {
                    final String collect = paraList.stream().collect(Collectors.joining(","));
                    sb.append(" ").append(paraList.get(0)).append(" and ").append(paraList.get(1)).append(" ");
                }

                break;
            //case "contain":
            //sb.append(" LIKE ");
            //sb.append("'%").append(paraList.get(0)).append("%' ");
            //   break;
            //case "notcontain":
            //sb.append(" NOT LIKE ");
            //sb.append("'%").append(paraList.get(0)).append("%' ");
            //    break;
//            case "reg":
//                sb.append("  REGEXP ");
//                sb.append("'").append(paraList.get(0)).append("' ");
//                break;
//            case "notreg":
//                sb.append(" NOT REGEXP ");
//                sb.append("'").append(paraList.get(0)).append("' ");
//                break;
            case "lt":
                sb.append(" < ");
                if (!"NUMBER".equalsIgnoreCase(dataType)) {
                    sb.append("'").append(paraList.get(0)).append("' ");
                } else {
                    sb.append(paraList.get(0));
                }
                break;
            case "let":
                sb.append(" <= ");
                if (!"NUMBER".equalsIgnoreCase(dataType)) {
                    sb.append("'").append(paraList.get(0)).append("' ");
                } else {
                    sb.append(paraList.get(0));
                }
                break;
            case "gt":
                sb.append(" > ");
                if (!"NUMBER".equalsIgnoreCase(dataType)) {
                    sb.append("'").append(paraList.get(0)).append("' ");
                } else {
                    sb.append(paraList.get(0));
                }
                break;
            case "get":
                sb.append(" >= ");
                if (!"NUMBER".equalsIgnoreCase(dataType)) {
                    sb.append("'").append(paraList.get(0)).append("' ");
                } else {
                    sb.append(paraList.get(0));
                }
                break;
            case "like":
                sb.append(" LIKE '%").append(paraList.get(0)).append("%' ");
                break;
            case "notlike":
                sb.append(" NOT LIKE '%").append(paraList.get(0)).append("%' ");
                break;
            case "notempty":
                sb.append(" != '' ");
                break;
            case "benull":
                sb.append(" IS NULL ");
                break;
            case "empty":
                sb.append(" = '' ");
                break;
            case "notnull":
                sb.append(" IS NOT NULL ");
                break;
            default:
                setError(attrName + " 关系" + fun + "未设置或无效,", impInfo);
                return false;
            //sb.append(  " " + fun + " " );
        }
        return true;
    }

    private boolean makeDateSql(StringBuilder sb, final String dateType, final String fun, String section, String space, List<String> paraList, String attrName, String impInfo) {

        if (StringUtils.isBlank(dateType)) {
            setError(attrName + " 时间条件为空，", impInfo);
            return false;
        }
        switch (dateType) {
            case "absolutetime":
                if (StringUtils.isBlank(fun)) {
                    setError(attrName + " 时间条件符为空，", impInfo);
                    return false;
                } else if ("between".equalsIgnoreCase(fun)) {
                    if (paraList == null || paraList.size() != 2) {
                        setError(attrName + " 时间条件值不正确，", impInfo);
                        return false;
                    }
                    sb.append(" between '").append(paraList.get(0)).append("' and '").append(paraList.get(1)).append("'");
                } else {
                    if (paraList == null || paraList.size() != 1) {
                        setError(attrName + " 时间条件值为空或不正确，", impInfo);
                        return false;
                    }
                    sb.append(" ").append(fun).append("  '").append(paraList.get(0)).append("'");
                }
                break;
            case "relativetimep":
                if ("future".equalsIgnoreCase(section)) {
                    if (paraList == null || paraList.size() != 1) {
                        setError(attrName + "  条件值为空或不正确，", impInfo);
                        return false;
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, 1); //从明天起算
                    final String d1 = ComUtil.dateToStr(cal.getTime());
                    cal.add(Calendar.DATE, Integer.parseInt(paraList.get(0)));
                    final String d2 = ComUtil.dateToStr(cal.getTime());
                    if ("inside".equalsIgnoreCase(space)) {
                        sb.append(" between '").append(d1).append("' and '").append(d2).append("'");
                    } else if ("before".equalsIgnoreCase(space)) {
                        sb.append(" <= '").append(d2).append("'");
                    } else {
                        setError(attrName + "  条件类别不正确，", impInfo);
                        return false;
                    }
                } else if ("former".equalsIgnoreCase(section)) { //过去
                    if (paraList == null || paraList.size() != 1) {
                        setError(attrName + "  条件值为空或不正确，", impInfo);
                        return false;
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, 1); //从明天起算
                    final String d1 = ComUtil.dateToStr(cal.getTime());
                    cal.add(Calendar.DATE, -Integer.parseInt(paraList.get(0)));
                    final String d2 = ComUtil.dateToStr(cal.getTime());
                    if ("inside".equalsIgnoreCase(space)) {
                        sb.append(" between '").append(d2).append("' and '").append(d1).append("'");
                    } else if ("before".equalsIgnoreCase(space)) {
                        sb.append(" <= '").append(d2).append("'");
                    } else {
                        setError(attrName + "  条件类别不正确，", impInfo);
                        return false;
                    }
                } else {
                    setError(attrName + "  时间类型不正确，", impInfo);
                    return false;
                }
                break;
            case "relativetimeb":
                if (paraList == null || paraList.size() != 2) {
                    setError(attrName + "  起止为空或不正确，", impInfo);
                    return false;
                }
                if (paraList != null) {
                    for (String param : paraList) {
                        if (StringUtils.isBlank(param)) {
                            setError(attrName + " 起止为有为空，", impInfo);
                            return false;
                        }
                    }
                }
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1); //从明天起算
                if ("future".equalsIgnoreCase(section)) {
                    cal.add(Calendar.DATE, Integer.parseInt(paraList.get(0)));
                } else if ("former".equalsIgnoreCase(section)) {
                    cal.add(Calendar.DATE, -Integer.parseInt(paraList.get(0)));
                } else {
                    setError(attrName + "  时间类型不正确，", impInfo);
                    return false;
                }
                final String d1 = ComUtil.dateToStr(cal.getTime());
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1); //从明天起算
                if ("future".equalsIgnoreCase(section)) {
                    cal.add(Calendar.DATE, Integer.parseInt(paraList.get(1)));
                    final String d2 = ComUtil.dateToStr(cal.getTime());
                    sb.append(" between '").append(d1).append("' and '").append(d2).append("'");
                } else if ("former".equalsIgnoreCase(section)) {
                    cal.add(Calendar.DATE, -Integer.parseInt(paraList.get(1)));
                    final String d2 = ComUtil.dateToStr(cal.getTime());
                    sb.append(" between '").append(d2).append("' and '").append(d1).append("'");
                }

                break;
            case "notnull":
            case "benotnull":
            case "hasvalue":
                sb.append(" IS NOT NULL ");
                break;
            case "isnull":
            case "benull":
            case "notvalue":
                sb.append(" IS NULL ");
                break;
            case "between":
                sb.append(" between timestamp '").append(paraList.get(0)).append("' and timestamp '").append(paraList.get(1)).append("'");
                break;
            case "eq":
                sb.append(" = timestamp '").append(paraList.get(0)).append("'");
                break;
            case "noteq":
                sb.append(" != timestamp '").append(paraList.get(0)).append("'");
                break;
            case "lt":
                sb.append(" < timestamp '").append(paraList.get(0)).append("'");
                break;
            case "let":
                sb.append(" <= timestamp '").append(paraList.get(0)).append("'");
                break;
            case "gt":
                sb.append(" > timestamp '").append(paraList.get(0)).append("'");
                break;
            case "get":
                sb.append(" >= timestamp '").append(paraList.get(0)).append("'");
                break;
        }
        return true;
    }

}
