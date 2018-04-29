package org.chengpx.test180428.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.chengpx.mylib.BaseFragment;
import org.chengpx.mylib.http.HttpUtils;
import org.chengpx.mylib.http.RequestPool;
import org.chengpx.test180428.R;
import org.chengpx.test180428.domain.CarBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 公司交通单双号管制功能
 * <p>
 * create at 2018/4/29 15:34 by chengpx
 */
public class TrafficControlFragment extends BaseFragment {

    private static String sTag = "org.chengpx.test180428.fragment.TrafficControlFragment";

    private TextView trafficcontrol_tv_date;
    private TextView trafficcontrol_tv_CarIds;
    private ListView trafficcontrol_lv_setting;
    private ImageView trafficcontrol_iv_redlight;
    private ImageView trafficcontrol_iv_yellowlight;
    private ImageView trafficcontrol_iv_greenlight;

    private DateFormat mDateFormat;
    private TrafficControlFragment mTrafficControlFragment;
    private int mGetCarMoveCarIdIndex;
    private List<CarBean> mCarBeanList;
    private int[] mCarIdArr = {
            1, 2, 3
    };

    @Override
    protected void initListener() {

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        mTrafficControlFragment = this;
        View view = inflater.inflate(R.layout.fragment_trafficcontrol, container, false);
        trafficcontrol_tv_date = (TextView) view.findViewById(R.id.trafficcontrol_tv_date);
        trafficcontrol_tv_CarIds = (TextView) view.findViewById(R.id.trafficcontrol_tv_CarIds);
        trafficcontrol_lv_setting = (ListView) view.findViewById(R.id.trafficcontrol_lv_setting);
        trafficcontrol_iv_redlight = (ImageView) view.findViewById(R.id.trafficcontrol_iv_redlight);
        trafficcontrol_iv_yellowlight = (ImageView) view.findViewById(R.id.trafficcontrol_iv_yellowlight);
        trafficcontrol_iv_greenlight = (ImageView) view.findViewById(R.id.trafficcontrol_iv_greenlight);
        return view;
    }

    @Override
    protected void onDie() {
        mDateFormat = null;
    }

    @Override
    protected void main() {

    }

    @Override
    protected void initData() {
        mCarBeanList = new ArrayList<>();
        mGetCarMoveCarIdIndex = 0;
        CarBean carBean = new CarBean();
        carBean.setCarId(mCarIdArr[mGetCarMoveCarIdIndex]);
        RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetCarMove.do",
                carBean, new GetCarMoveCallBack(CarBean.class, mTrafficControlFragment));
    }

    @Override
    protected void onDims() {
        mCarBeanList = null;
    }

    private static class GetCarMoveCallBack extends HttpUtils.Callback<CarBean> {

        private final TrafficControlFragment mTrafficControlFragment_inner;

        /**
         * @param carBeanClass           结果数据封装体类型字节码
         * @param trafficControlFragment
         */
        GetCarMoveCallBack(Class<CarBean> carBeanClass, TrafficControlFragment trafficControlFragment) {
            super(carBeanClass);
            mTrafficControlFragment_inner = trafficControlFragment;
            mTrafficControlFragment_inner.getCarBeanList().clear();
        }

        @Override
        protected void onSuccess(CarBean carBean) {
            carBean.setCarId(mTrafficControlFragment_inner.getCarIdArr()[mTrafficControlFragment_inner.getGetCarMoveCarIdIndex()]);
            mTrafficControlFragment_inner.setGetCarMoveCarIdIndex(mTrafficControlFragment_inner.getGetCarMoveCarIdIndex() + 1);
            mTrafficControlFragment_inner.getCarBeanList().add(carBean);
            if (mTrafficControlFragment_inner.getGetCarMoveCarIdIndex() < mTrafficControlFragment_inner.mCarIdArr.length) {
                CarBean reqCarBean = new CarBean();
                reqCarBean.setCarId(mTrafficControlFragment_inner.getCarIdArr()[mTrafficControlFragment_inner.getGetCarMoveCarIdIndex()]);
                RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetCarMove.do",
                        reqCarBean, this);
            } else {
                Log.d(sTag, mTrafficControlFragment_inner.mCarBeanList.toString());
            }
        }

    }

    public TextView getTrafficcontrol_tv_date() {
        return trafficcontrol_tv_date;
    }

    public TextView getTrafficcontrol_tv_CarIds() {
        return trafficcontrol_tv_CarIds;
    }

    public ListView getTrafficcontrol_lv_setting() {
        return trafficcontrol_lv_setting;
    }

    public ImageView getTrafficcontrol_iv_redlight() {
        return trafficcontrol_iv_redlight;
    }

    public ImageView getTrafficcontrol_iv_yellowlight() {
        return trafficcontrol_iv_yellowlight;
    }

    public ImageView getTrafficcontrol_iv_greenlight() {
        return trafficcontrol_iv_greenlight;
    }

    public DateFormat getDateFormat() {
        return mDateFormat;
    }

    public TrafficControlFragment getTrafficControlFragment() {
        return mTrafficControlFragment;
    }

    public int[] getCarIdArr() {
        return mCarIdArr;
    }

    public int getGetCarMoveCarIdIndex() {
        return mGetCarMoveCarIdIndex;
    }

    public void setGetCarMoveCarIdIndex(int getCarMoveCarIdIndex) {
        mGetCarMoveCarIdIndex = getCarMoveCarIdIndex;
    }

    public List<CarBean> getCarBeanList() {
        return mCarBeanList;
    }

}
