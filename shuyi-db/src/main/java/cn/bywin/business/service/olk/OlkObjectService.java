package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.*;
import cn.bywin.business.bean.view.olk.OlkObjectWithFieldsVo;
import cn.bywin.business.bean.view.olk.VOlkObjectVo;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.mapper.olk.*;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.ArrayList;
import java.util.List;


@Service
public class OlkObjectService extends BaseServiceImpl<TOlkObjectDo, String> {

    @Autowired
    private OlkObjectMapper commMapper;

    @Autowired
    private OlkDatabaseMapper databaseMapper;

    @Autowired
    private OlkSchemaMapper schemaMapper;

    @Autowired
    private OlkFieldMapper fieldMapper;

    @Autowired
    private OlkDataNodeMapper dataNodeMapper;

//	@Autowired
//	private OlkGroupObjectMapper groupObjectMapper;
//
//	@Autowired
//	private OlkItemObjectMapper itemObjectMapper;

    @Override
    public Mapper<TOlkObjectDo> getMapper() {
        return commMapper;
    }

    public List<TOlkObjectDo> findBeanList( TOlkObjectDo bean ) {
        return commMapper.findBeanList( bean );
    }

    public long findBeanCnt( TOlkObjectDo bean ) {
        return commMapper.findBeanCnt( bean );
    }

    public List<VOlkObjectVo> findNodeBeanList(VOlkObjectVo bean ) {
        return commMapper.findNodeBeanList( bean );
    }

    public long findNodeBeanCnt( VOlkObjectVo bean ) {
        return commMapper.findNodeBeanCnt( bean );
    }

    public List<TOlkObjectDo> findLikeName( String name ) {
        return commMapper.findLikeName( name );
    }

    public long findSameNameCount( TOlkObjectDo bean ) {
        return commMapper.findSameNameCount( bean );
    }

    public List<OlkObjectWithFieldsVo> findUserTable( OlkObjectWithFieldsVo bean ) {
        return commMapper.findUserTable( bean );
    }

//    public List<VOlkObjectVo> findUserObjectList( TOlkObjectDo bean ) {
//        return commMapper.findUserObjectList( bean );
//    }
//
//    public VOlkObjectVo findViewObjectById( String id ) {
//        return commMapper.findViewObjectById( id );
//    }

    public TOlkObjectDo findByFullName( String fullName ) {
        return commMapper.findByFullName( fullName );
    }

    /**
     * 找到资源所属的节点信息
     * @param fullName  资源全名称
     * @return          节点信息
     */
    public TOlkDcServerDo findBelongDcServer(String fullName) {
        return commMapper.findBelongDcCode(fullName);
    }

    public long deleteWhithOthers( List<TOlkObjectDo> delList ) {

        if ( delList != null ) {
            for ( TOlkObjectDo obj : delList ) {
                fieldMapper.deleteByObjectId( obj.getId() );
                dataNodeMapper.delByDataId( obj.getId() );
                commMapper.deleteByPrimaryKey( obj.getId() );
            }
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public long updateBeanWithFlag( List<TOlkObjectDo> updateList, List<TOlkDatabaseDo> dbList,
                                    List<TOlkSchemaDo> schemaList ) {
        if ( dbList != null ) {
            for ( TOlkDatabaseDo obj : dbList ) {
                databaseMapper.updateEnable( obj );
            }
        }

        if ( schemaList != null ) {
            for ( TOlkSchemaDo obj : schemaList ) {
                schemaMapper.updateEnable( obj );
            }
        }
        if ( updateList != null ) {
            for ( TOlkObjectDo obj : updateList ) {
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
    public long saveWithFields( List<OlkObjectWithFieldsVo> list ) throws Exception {
        for ( OlkObjectWithFieldsVo bean : list ) {
            TOlkObjectDo objectDo = commMapper.selectByPrimaryKey( bean.getId() );
            if ( objectDo == null ) {
                objectDo = new TOlkObjectDo();
                MyBeanUtils.copyBeanNotNull2Bean( bean, objectDo );
                commMapper.insert( objectDo );
                if ( bean.getFieldList() != null && bean.getFieldList().size() > 0 ) {
                    for ( TOlkFieldDo fieldDo : bean.getFieldList() ) {
                        fieldMapper.insert( fieldDo );
                    }
                }
                if ( bean.getDataNodeList() != null && bean.getDataNodeList().size() > 0 ) {
                    for ( TOlkDataNodeDo dataNodeDo : bean.getDataNodeList() ) {
                        dataNodeMapper.insert( dataNodeDo );
                    }
                }
            }
            else {
                MyBeanUtils.copyBeanNotNull2Bean( bean, objectDo );
                commMapper.updateByPrimaryKey( objectDo );

                List<TOlkFieldDo> tmpList = new ArrayList<>();
                if ( bean.getFieldList() != null ) {
                    tmpList.addAll( bean.getFieldList() );
                }
                List<TOlkFieldDo> fieldList = fieldMapper.selectByObjectId( bean.getId() );
                if ( fieldList != null ) {
                    for ( TOlkFieldDo fieldDo : fieldList ) {
                        boolean bfound = false;
                        for ( TOlkFieldDo tf : tmpList ) {
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
                for ( TOlkFieldDo tOlkFieldDo : tmpList ) {
                    fieldMapper.insert( tOlkFieldDo );
                }

                List<TOlkDataNodeDo> ndList = new ArrayList<>();
                if ( bean.getDataNodeList() != null ) {
                    ndList.addAll( bean.getDataNodeList() );
                }
                List<TOlkDataNodeDo> deldnList = dataNodeMapper.findByDataId( bean.getId() );
                if ( deldnList != null ) {

                    for ( TOlkDataNodeDo dn : deldnList ) {
                        boolean bfound = false;
                        for ( TOlkDataNodeDo tf : ndList ) {
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
                for ( TOlkDataNodeDo dataNodeDo : ndList ) {
                    dataNodeMapper.insert( dataNodeDo );
                }
            }
        }
        return list.size();
    }

    @Transactional(rollbackFor = Exception.class)
    public long updateWithNodes( TOlkObjectDo bean, List<TOlkDataNodeDo> addList, List<TOlkDataNodeDo> modList, List<TOlkDataNodeDo> delList ) throws Exception {
        commMapper.updateByPrimaryKeySelective( bean );

        if ( delList != null ) {
            for ( TOlkDataNodeDo dn : delList ) {
                dataNodeMapper.deleteByPrimaryKey( dn.getId() );
            }
        }
        if ( addList != null ) {
            for ( TOlkDataNodeDo dn : addList ) {
                dataNodeMapper.insert( dn );
            }
        }
        if ( modList != null ) {
            for ( TOlkDataNodeDo dn : modList ) {
                dataNodeMapper.updateByPrimaryKey( dn );
            }
        }
        return 1;
    }

}
