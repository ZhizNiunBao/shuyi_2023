package cn.bywin.cache.cache;


import cn.bywin.business.common.base.UserDo;

public interface IUserRedisCache {


    public UserDo getUser(String tokenId );
    public void setUser( UserDo user);

    public void removeUser(String tokenId);

    public static void clear(){
    }
}
