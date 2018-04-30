package org.chengpx.test180428.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chengpx.mylib.BaseFragment;
import org.chengpx.mylib.http.HttpUtils;
import org.chengpx.mylib.http.RequestPool;
import org.chengpx.test180428.domain.BusBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 公交查询模块
 * <p>
 * create at 2018/4/30 20:58 by chengpx
 */
public class BusQueryFragment extends BaseFragment {

    private int[] mBusIdArr = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
    };
    private int[] mBusStationIdArr = {
            1, 2
    };
    private List<List<Map<String, Object>>> mBusStationInfoList;
    private List<BusBean> mBusBeanList;
    private int mReqBusIdIndex;
    private int mReqBusStationIdIndex;
    private Timer mTimer;

    @Override
    protected void initListener() {

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {

    }

    @Override
    protected void initData() {
        mBusStationInfoList = new ArrayList<>();
        mBusBeanList = new ArrayList<>();
        mTimer = new Timer();
        mTimer.schedule(new MyTimerTask(), 0, 3000);
    }

    @Override
    protected void onDims() {
        mBusStationInfoList = null;
        mBusBeanList = null;
        mTimer.cancel();
        mTimer = null;
        mReqBusIdIndex = 0;
        mReqBusStationIdIndex = 0;
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetBusCapacity.do",
                    null, new GetBusCapacityCallBack(BusBean.class));
            RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetBusstationInfo.do",
                    null, new GetBusstationInfoCallBack(List.class));
        }

    }

    private static class GetBusCapacityCallBack extends HttpUtils.Callback<BusBean> {

        /**
         * @param busBeanClass 结果数据封装体类型字节码
         */
        GetBusCapacityCallBack(Class<BusBean> busBeanClass) {
            super(busBeanClass);
        }

        @Override
        protected void onSuccess(BusBean busBean) {

        }

    }

    private static class GetBusstationInfoCallBack extends HttpUtils.Callback<List> {

        /**
         * @param listClass 结果数据封装体类型字节码
         */
        public GetBusstationInfoCallBack(Class<List> listClass) {
            super(listClass);
        }

        @Override
        protected void onSuccess(List list) {

        }

    }

}
