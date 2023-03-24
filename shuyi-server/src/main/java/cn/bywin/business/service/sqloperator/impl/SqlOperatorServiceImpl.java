package cn.bywin.business.service.sqloperator.impl;

import cn.bywin.business.bean.request.sqloperator.SqlOperatorAddRequest;
import cn.bywin.business.bean.response.sqloperator.SqlOperatorInVo;
import cn.bywin.business.bean.response.sqloperator.SqlOperatorInfoVo;
import cn.bywin.business.bean.response.sqloperator.SqlOperatorTableVo;
import cn.bywin.business.bean.response.sqloperator.SqlOperatorVo;
import cn.bywin.business.bean.response.sqloperator.SqlParseVo;
import cn.bywin.business.bean.sqloperator.TSqlOperatorDo;
import cn.bywin.business.bean.sqloperator.TSqlOperatorInDo;
import cn.bywin.business.bean.sqloperator.TSqlOperatorTableDo;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.except.ServerException;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.mapper.sqloperator.SqlOperatorInMapper;
import cn.bywin.business.mapper.sqloperator.SqlOperatorMapper;
import cn.bywin.business.mapper.sqloperator.SqlOperatorTableMapper;
import cn.bywin.business.service.sqloperator.SqlOperatorService;
import cn.bywin.business.util.sqloperator.SqlParseUtil;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 中台接口返回格式
 *
 * @author lhw
 */
@Service
public class SqlOperatorServiceImpl implements SqlOperatorService {

    @Autowired
    SqlOperatorMapper sqlOperatorMapper;

    @Autowired
    SqlOperatorInMapper sqlOperatorInMapper;

    @Autowired
    SqlOperatorTableMapper sqlOperatorTableMapper;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public SqlParseVo parseSql(String sql) {
        return SqlParseUtil.parseSql(sql);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOperator(SqlOperatorAddRequest sqlOperatorAdd, UserDo userDo) {
        TSqlOperatorDo sqlOperatorDo = new TSqlOperatorDo();
        MyBeanUtils.copyBean2Bean(sqlOperatorDo, sqlOperatorAdd.getSqlOperatorVo());
        sqlOperatorDo.setCreatorId(userDo.getUserId());
        sqlOperatorDo.setCreatorName(userDo.getUserName());

        Preconditions.checkArgument(sqlOperatorDo.getId() == null
            && sqlOperatorMapper.selectByName(sqlOperatorDo.getName()).size() == 0, "算子名称已使用");
        try {
            sqlOperatorDo.setId(ComUtil.genId());

            List<SqlOperatorInVo> sqlOperatorInVoList = sqlOperatorAdd.getSqlOperatorInVoList();
            List<SqlOperatorTableVo> sqlOperatorTableVoList = sqlOperatorAdd
                .getSqlOperatorTableVoList();

            sqlOperatorMapper.insertSelective(sqlOperatorDo);
            for (SqlOperatorInVo sqlOperatorInVo : sqlOperatorInVoList) {
                TSqlOperatorInDo sqlOperatorInDo = new TSqlOperatorInDo();
                MyBeanUtils.copyBean2Bean(sqlOperatorInDo, sqlOperatorInVo);
                sqlOperatorInDo.setId(ComUtil.genId());
                sqlOperatorInDo.setOperatorId(sqlOperatorDo.getId());
                sqlOperatorInDo.setCreatorId(userDo.getUserId());
                sqlOperatorInDo.setCreatorName(userDo.getUserName());
                sqlOperatorInMapper.insertSelective(sqlOperatorInDo);
            }

            for (SqlOperatorTableVo sqlOperatorTableVo : sqlOperatorTableVoList) {
                TSqlOperatorTableDo sqlOperatorTableDo = new TSqlOperatorTableDo();
                MyBeanUtils.copyBean2Bean(sqlOperatorTableDo, sqlOperatorTableVo);
                sqlOperatorTableDo.setId(ComUtil.genId());
                sqlOperatorTableDo.setOperatorId(sqlOperatorDo.getId());
                sqlOperatorTableDo.setCreatorName(userDo.getUserName());
                sqlOperatorTableDo.setCreatorId(userDo.getUserId());
                sqlOperatorTableMapper.insertSelective(sqlOperatorTableDo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException("500", "新增失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOperator(SqlOperatorAddRequest sqlOperatorAdd, UserDo userDo)
        throws Exception {
        TSqlOperatorDo sqlOperatorDo = new TSqlOperatorDo();
        MyBeanUtils.copyBean2Bean(sqlOperatorDo, sqlOperatorAdd.getSqlOperatorVo());
        sqlOperatorDo.setCreatorId(userDo.getUserId());
        sqlOperatorDo.setCreatorName(userDo.getUserName());

        sqlOperatorMapper.selectByName(sqlOperatorDo.getName()).forEach(
            t -> Preconditions.checkArgument(t.getId().equals(sqlOperatorDo.getId()), "算子名称已使用")
        );

        try {
            sqlOperatorMapper.updateByPrimaryKeySelective(sqlOperatorDo);
            TSqlOperatorTableDo sqlOperatorTableDo = new TSqlOperatorTableDo();
            sqlOperatorTableDo.setOperatorId(sqlOperatorDo.getId());
            sqlOperatorTableDo.setCreatorId(userDo.getUserId());
            sqlOperatorTableDo.setCreatorName(userDo.getUserName());
            sqlOperatorTableMapper.delete(sqlOperatorTableDo);

            TSqlOperatorInDo sqlOperatorInDo = new TSqlOperatorInDo();
            sqlOperatorInDo.setOperatorId(sqlOperatorDo.getId());
            sqlOperatorInDo.setCreatorId(userDo.getUserId());
            sqlOperatorInDo.setCreatorName(userDo.getUserName());
            sqlOperatorInMapper.delete(sqlOperatorInDo);

            List<SqlOperatorInVo> sqlOperatorInVoList = sqlOperatorAdd.getSqlOperatorInVoList();
            List<SqlOperatorTableVo> sqlOperatorTableVoList = sqlOperatorAdd
                .getSqlOperatorTableVoList();

            for (SqlOperatorInVo sqlOperatorInVo : sqlOperatorInVoList) {
                sqlOperatorInDo = new TSqlOperatorInDo();
                MyBeanUtils.copyBean2Bean(sqlOperatorInDo, sqlOperatorInVo);
                sqlOperatorInDo.setId(ComUtil.genId());
                sqlOperatorInDo.setOperatorId(sqlOperatorDo.getId());
                sqlOperatorInDo.setCreatorId(userDo.getUserId());
                sqlOperatorInDo.setCreatorName(userDo.getUserName());
                sqlOperatorInMapper.insertSelective(sqlOperatorInDo);
            }

            for (SqlOperatorTableVo sqlOperatorTableVo : sqlOperatorTableVoList) {
                sqlOperatorTableDo = new TSqlOperatorTableDo();
                MyBeanUtils.copyBean2Bean(sqlOperatorTableDo, sqlOperatorTableVo);
                sqlOperatorTableDo.setId(ComUtil.genId());
                sqlOperatorTableDo.setOperatorId(sqlOperatorDo.getId());
                sqlOperatorTableDo.setCreatorId(userDo.getUserId());
                sqlOperatorTableDo.setCreatorName(userDo.getUserName());
                sqlOperatorTableMapper.insertSelective(sqlOperatorTableDo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException("500", "更新失败");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOperator(String operatorId, UserDo userDo) {
        try {
            TSqlOperatorDo sqlOperatorDo = new TSqlOperatorDo();
            sqlOperatorDo.setId(operatorId);
            sqlOperatorDo.setCreatorId(userDo.getUserId());
            sqlOperatorDo.setCreatorName(userDo.getUserName());

            TSqlOperatorInDo sqlOperatorInDo = new TSqlOperatorInDo();
            sqlOperatorInDo.setOperatorId(operatorId);
            sqlOperatorInDo.setCreatorId(userDo.getUserId());
            sqlOperatorInDo.setCreatorName(userDo.getUserName());
            sqlOperatorInMapper.delete(sqlOperatorInDo);

            TSqlOperatorTableDo sqlOperatorTableDo = new TSqlOperatorTableDo();
            sqlOperatorTableDo.setOperatorId(operatorId);
            sqlOperatorTableDo.setCreatorId(userDo.getUserId());
            sqlOperatorTableDo.setCreatorName(userDo.getUserName());
            sqlOperatorTableMapper.delete(sqlOperatorTableDo);

            sqlOperatorMapper.delete(sqlOperatorDo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException("500","删除失败");
        }

    }

    @Override
    public List<SqlOperatorVo> queryPage(Integer currentPage, Integer pageSize, String name,
        String type, UserDo userDo) {

        PageHelper.startPage(currentPage, pageSize);
        return sqlOperatorMapper.queryPage(name, type, userDo.getUserId())
            .stream().map( i -> {
                SqlOperatorVo sqlOperatorVo = new SqlOperatorVo();
                MyBeanUtils.copyBean2Bean(sqlOperatorVo, i);
                return sqlOperatorVo;
            }).collect(Collectors.toList());
    }

    @Override
    public SqlOperatorInfoVo getOperatorInfo(String operatorId, UserDo userDo) {
        SqlOperatorInfoVo sqlOperatorInfoVo = new SqlOperatorInfoVo();
        TSqlOperatorDo sqlOperatorDo = sqlOperatorMapper.selectByPrimaryKey(operatorId);

        TSqlOperatorInDo sqlOperatorInDo = new TSqlOperatorInDo();
        sqlOperatorInDo.setOperatorId(sqlOperatorDo.getId());
        sqlOperatorInfoVo.setSqlOperatorInVoList(sqlOperatorInMapper.select(sqlOperatorInDo).stream().map( indo -> {
            SqlOperatorInVo sqlOperatorInVo = new SqlOperatorInVo();
            MyBeanUtils.copyBean2Bean(sqlOperatorInVo, indo);
            return sqlOperatorInVo;
        }).collect(Collectors.toList()));

        TSqlOperatorTableDo sqlOperatorTableDo = new TSqlOperatorTableDo();
        sqlOperatorTableDo.setOperatorId(sqlOperatorDo.getId());
        sqlOperatorInfoVo.setSqlOperatorTableVoList(sqlOperatorTableMapper.select(sqlOperatorTableDo).stream().map( indo -> {
            SqlOperatorTableVo sqlOperatorTableVo = new SqlOperatorTableVo();
            MyBeanUtils.copyBean2Bean(sqlOperatorTableVo, indo);
            return sqlOperatorTableVo;
        }).collect(Collectors.toList()));

        SqlOperatorVo sqlOperatorVo = new SqlOperatorVo();
        MyBeanUtils.copyBean2Bean(sqlOperatorVo, sqlOperatorDo);
        sqlOperatorInfoVo.setSqlOperatorVo(sqlOperatorVo);
        return sqlOperatorInfoVo;
    }



}
