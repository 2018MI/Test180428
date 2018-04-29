package org.chengpx.test180428.dao;

import android.content.Context;

import org.chengpx.mylib.db.BaseDao;
import org.chengpx.test180428.domain.CarBean;

/**
 * 小车 dao
 * <p>
 * create at 2018/4/29 8:46 by chengpx
 */
public class CarDao extends BaseDao<CarBean> {

    private static CarDao sCarDao;

    private CarDao(Context context) {
        super(context);
    }

    public static CarDao getInstance(Context context) {
        if (sCarDao == null) {
            synchronized (CarDao.class) {
                if (sCarDao == null) {
                    sCarDao = new CarDao(context);
                }
            }
        }
        return sCarDao;
    }

}
