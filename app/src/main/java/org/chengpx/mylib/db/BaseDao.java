package org.chengpx.mylib.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

/**
 * create at 2018/4/27 14:08 by chengpx
 */
public abstract class BaseDao<T> {

    protected Dao mDao;

    public BaseDao(Context context) {
        Class<? extends BaseDao> aClass = getClass();// 得到运行时 class
        Type genericSuperclass = aClass.getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length == 0) {
            return;
        }
        Type actualTypeArgument = actualTypeArguments[0];
        if (!(actualTypeArgument instanceof Class)) {
            return;
        }
        Class actualClassArgument = (Class) actualTypeArgument;
        OrmLiteSqliteOpenHelper ormLiteSqliteOpenHelper = DbHelper.getInstance(context);
        try {
            mDao = ormLiteSqliteOpenHelper.getDao(actualClassArgument);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        mDao = null;
    }

    public int insert(T t) {
        try {
            return mDao.create(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public T select(String field, String val) {
        try {
            List list = mDao.queryForEq(field, val);
            if (list != null && list.size() > 0) {
                return (T) list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
