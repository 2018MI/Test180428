package org.chengpx.test180428.dao;

import android.content.Context;

import org.chengpx.mylib.db.BaseDao;
import org.chengpx.test180428.domain.UserBean;

/**
 * create at 2018/4/29 9:24 by chengpx
 */
public class UserDao extends BaseDao<UserBean> {

    private static UserDao sUserDao;

    public UserDao(Context context) {
        super(context);
    }

    public static UserDao getInstance(Context context) {
        if (sUserDao == null) {
            synchronized (UserDao.class) {
                if (sUserDao == null) {
                    sUserDao = new UserDao(context);
                }
            }
        }
        return sUserDao;
    }

}
