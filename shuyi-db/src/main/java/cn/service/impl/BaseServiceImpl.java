package cn.service.impl;

import cn.bywin.business.common.base.BaseEntityDo;
import cn.bywin.business.common.base.SidEntityDo;
import cn.service.BaseService;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

public abstract class BaseServiceImpl<T,ID> implements BaseService<T,ID> {

    public abstract Mapper<T> getMapper();

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值
     *
     * @param t
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class) //事务回滚
    public Integer insertBean(T t) {
        if (t instanceof SidEntityDo){
            if (StringUtils.isBlank(((SidEntityDo) t).getId())){
                String id=UUID.randomUUID().toString();
                ((SidEntityDo) t).setId(id.replace("-",""));
            }
        }
        if (t instanceof BaseEntityDo){
            ((BaseEntityDo) t).setCreatedTime(new Timestamp(System.currentTimeMillis()));
            ((BaseEntityDo) t).setModifiedTime(new Timestamp(System.currentTimeMillis()));
        }
        return getMapper().insertSelective(t); //封装单表操作方法
    }

    /**
     * 保存一个list实体，null的属性不会保存，会使用数据库默认值
     *
     * @param list
     * @return
     */
    @Override
    public Integer batchAdd(List<T> list){
        int cnt = 0;
        for( T t:list)
            cnt+= getMapper().insert(t); //封装单表操作方法
        return cnt;
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteById(ID id) {
        return getMapper().deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteByIds(List<ID> idList) {
        int size = 0;
        for (ID id : idList) {
            size = getMapper().deleteByPrimaryKey(id);
        }
        return size;
    }

    /**
     * 根据实体属性作为条件进行删除，查询条件使用等号
     *
     * @param t
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer delete(T t){
        return getMapper().deleteByPrimaryKey(t);
    }

    /**
     * 根据主键更新属性不为null的值
     *
     * @param t
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateBean(T t){
        if (t instanceof SidEntityDo){
            ((SidEntityDo) t).setModifiedTime(new Timestamp(System.currentTimeMillis()));
        }

        return getMapper().updateByPrimaryKey(t);
    }

    /**
     * 只是更新新的T中不为空的字段
     *
     * @param t
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateNoNull(T t){
        if (t instanceof SidEntityDo){
            ((SidEntityDo) t).setModifiedTime(new Timestamp(System.currentTimeMillis()));
        }
        return   getMapper().updateByPrimaryKeySelective(t);
    }





    /**
     * 根据主键更新属性不为null的值
     *
     * @param list
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchUpdateByPrimaryKey(List<T> list){
        int cnt = 0;
        for( T t: list){
            cnt += getMapper().updateByPrimaryKey(t);
        }
        return cnt;
    }

    /**
     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     *
     * @param t
     * @return
     */
    @Override
    public T findOne(T t){
       return getMapper().selectOne(t);
    }

    /**
     * 查询全部结果
     *
     * @return
     */
    @Override
    public List<T> findAll(){
        return getMapper().selectAll();
    }

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @Override
    public T findById(ID id){
        return getMapper().selectByPrimaryKey(id);
    }

    /**
     * 根据实体中的属性值进行查询，查询条件使用等号
     *
     * @param t
     * @return
     */
    @Override
    public List<T> find(T t){
        return getMapper().select(t);
    }

    /**
     * 根据Example条件更新实体record包含的不是null的属性值
     *
     * @return
     */
    //Integer updateByExampleSelective(QueryExample<T> queryExample);

    /**
     * 根据实体中的属性值进行分页查询，查询条件使用等号
     *
     * @param example
     * @return
     */
    //PageInfo<T> findPage(T t, Integer pageNum, Integer pageSize);

    @Override
    public List<T> findByExample(Example example){
        return getMapper().selectByExample(example);
    }

    /**
     * 根据query条件更新record数据
     *
     * @param record 要更新的数据
     * @param query  查询条件
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateByExampleSelective(T record, Example query){
        return getMapper().updateByExampleSelective(record,query);
    }

    /**
     * 根据query条件更新record数据
     *
     * @param record 要更新的数据
     * @param query  查询条件
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateByExampleSelective(T record, T query){
        return getMapper().updateByExampleSelective(record,query);
    }

    /**
     * 查询数量
     *
     * @param record
     * @return
     */
    @Override
    public Integer findCount(T record){
        return getMapper().selectCount(record);
    }

    /**
     * 查询数量
     *
     * @param query
     * @return
     */
    @Override
    public Integer findCountByExample(Example query){
        return getMapper().selectCountByExample(query);
    }

}
