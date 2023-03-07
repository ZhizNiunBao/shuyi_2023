package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TBydbDataNodeDo;
import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.bydb.TBydbFieldDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.bydb.TBydbSchemaDo;
import cn.bywin.business.bean.view.bydb.BydbObjectFieldsVo;
import cn.bywin.business.bean.view.VBydbObjectVo;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.mapper.bydb.BydbDataNodeMapper;
import cn.bywin.business.mapper.bydb.BydbDatabaseMapper;
import cn.bywin.business.mapper.bydb.BydbFieldMapper;
import cn.bywin.business.mapper.bydb.BydbObjectMapper;
import cn.bywin.business.mapper.bydb.BydbSchemaMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.ArrayList;
import java.util.List;


@Service
public class BydbObjectService extends BaseServiceImpl<TBydbObjectDo, String> {

    @Autowired
    private BydbObjectMapper commMapper;

    @Autowired
    private BydbDatabaseMapper databaseMapper;

    @Autowired
    private BydbSchemaMapper schemaMapper;

    @Autowired
    private BydbFieldMapper fieldMapper;

    @Autowired
    private BydbDataNodeMapper dataNodeMapper;

//	@Autowired
//	private BydbGroupObjectMapper groupObjectMapper;
//
//	@Autowired
//	private BydbItemObjectMapper itemObjectMapper;

    @Override
    public Mapper<TBydbObjectDo> getMapper() {
        return commMapper;
    }

    public List<TBydbObjectDo> findBeanList( TBydbObjectDo bean ) {
        return commMapper.findBeanList( bean );
    }

    public long findBeanCnt( TBydbObjectDo bean ) {
        return commMapper.findBeanCnt( bean );
    }

    public List<VBydbObjectVo> findNodeBeanList( VBydbObjectVo bean ) {
        return commMapper.findNodeBeanList( bean );
    }

    public long findNodeBeanCnt( VBydbObjectVo bean ) {
        return commMapper.findNodeBeanCnt( bean );
    }

    public List<TBydbObjectDo> findLikeName( String name ) {
        return commMapper.findLikeName( name );
    }

    public long findSameNameCount( TBydbObjectDo bean ) {
        return commMapper.findSameNameCount( bean );
    }


//    public List<VBydbObjectVo> findUserObjectList( TBydbObjectDo bean ) {
//        return commMapper.findUserObjectList( bean );
//    }
//
//    public VBydbObjectVo findViewObjectById( String id ) {
//        return commMapper.findViewObjectById( id );
//    }

    public List<TBydbObjectDo> findByFullName( String fullName ) {
        return commMapper.findByFullName( fullName );
    }


    public long deleteWhithOthers( List<TBydbObjectDo> delList ) {

        if ( delList != null ) {
            for ( TBydbObjectDo obj : delList ) {
                fieldMapper.deleteByObjectId( obj.getId() );
                dataNodeMapper.delByDataId( obj.getId() );
                commMapper.deleteByPrimaryKey( obj.getId() );
            }
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public long updateBeanWithFlag( List<TBydbObjectDo> updateList, List<TBydbDatabaseDo> dbList,
                                    List<TBydbSchemaDo> schemaList ) {
        if ( dbList != null ) {
            for ( TBydbDatabaseDo obj : dbList ) {
                databaseMapper.updateEnable( obj );
            }
        }

        if ( schemaList != null ) {
            for ( TBydbSchemaDo obj : schemaList ) {
                schemaMapper.updateEnable( obj );
            }
        }
        if ( updateList != null ) {
            for ( TBydbObjectDo obj : updateList ) {
                commMapper.updateByPrimaryKey( obj );
                if(obj.getEnable() != null && obj.getEnable() == 0) {
                    fieldMapper.updateEnableByObjectId( obj.getId() );
                }
            }
            return updateList.size();
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public long saveWithFields( List<BydbObjectFieldsVo> list ) throws Exception {
        for ( BydbObjectFieldsVo bean : list ) {
            TBydbObjectDo objectDo = commMapper.selectByPrimaryKey( bean.getId() );
            if ( objectDo == null ) {
                objectDo = new TBydbObjectDo();
                MyBeanUtils.copyBeanNotNull2Bean( bean, objectDo );
                commMapper.insert( objectDo );
                if ( bean.getFieldList() != null && bean.getFieldList().size() > 0 ) {
                    for ( TBydbFieldDo fieldDo : bean.getFieldList() ) {
                        fieldMapper.insert( fieldDo );
                    }
                }
                if ( bean.getDataNodeList() != null && bean.getDataNodeList().size() > 0 ) {
                    for ( TBydbDataNodeDo dataNodeDo : bean.getDataNodeList() ) {
                        dataNodeMapper.insert( dataNodeDo );
                    }
                }
            }
            else {
                MyBeanUtils.copyBeanNotNull2Bean( bean, objectDo );
                commMapper.updateByPrimaryKey( objectDo );

                List<TBydbFieldDo> tmpList = new ArrayList<>();
                if ( bean.getFieldList() != null ) {
                    tmpList.addAll( bean.getFieldList() );
                }
                List<TBydbFieldDo> fieldList = fieldMapper.selectByObjectId( bean.getId() );
                if ( fieldList != null ) {
                    for ( TBydbFieldDo fieldDo : fieldList ) {
                        boolean bfound = false;
                        for ( TBydbFieldDo tf : tmpList ) {
                            if ( fieldDo.getId().equals( tf.getId() ) ) {
                                bfound = true;
                                fieldMapper.updateByPrimaryKey( tf );
                                tmpList.remove( tf );
                                break;
                            }
                        }
                        if ( !bfound ) {
                            fieldMapper.deleteByPrimaryKey( fieldDo.getId() );
                        }
                    }
                }
                for ( TBydbFieldDo tBydbFieldDo : tmpList ) {
                    fieldMapper.insert( tBydbFieldDo );
                }

                List<TBydbDataNodeDo> ndList = new ArrayList<>();
                if ( bean.getDataNodeList() != null ) {
                    ndList.addAll( bean.getDataNodeList() );
                }
                List<TBydbDataNodeDo> deldnList = dataNodeMapper.findByDataId( bean.getId() );
                if ( deldnList != null ) {

                    for ( TBydbDataNodeDo dn : deldnList ) {
                        boolean bfound = false;
                        for ( TBydbDataNodeDo tf : ndList ) {
                            if ( dn.getId().equals( tf.getId() ) ) {
                                bfound = true;
                                dataNodeMapper.updateByPrimaryKey( tf );
                                ndList.remove( tf );
                                break;
                            }
                        }
                        if ( !bfound ) {
                            dataNodeMapper.deleteByPrimaryKey( dn.getId() );
                        }
                    }
                }
                for ( TBydbDataNodeDo dataNodeDo : ndList ) {
                    dataNodeMapper.insert( dataNodeDo );
                }
            }
        }
        return list.size();
    }

    @Transactional(rollbackFor = Exception.class)
    public long updateWithNodes( TBydbObjectDo bean, List<TBydbDataNodeDo> addList, List<TBydbDataNodeDo> modList, List<TBydbDataNodeDo> delList ) throws Exception {
        commMapper.updateByPrimaryKeySelective( bean );

        if ( delList != null ) {
            for ( TBydbDataNodeDo dn : delList ) {
                dataNodeMapper.deleteByPrimaryKey( dn.getId() );
            }
        }
        if ( addList != null ) {
            for ( TBydbDataNodeDo dn : addList ) {
                dataNodeMapper.insert( dn );
            }
        }
        if ( modList != null ) {
            for ( TBydbDataNodeDo dn : modList ) {
                dataNodeMapper.updateByPrimaryKey( dn );
            }
        }
        return 1;
    }

}
