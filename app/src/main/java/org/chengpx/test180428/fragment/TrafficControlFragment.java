package org.chengpx.test180428.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.chengpx.mylib.BaseFragment;
import org.chengpx.mylib.http.HttpUtils;
import org.chengpx.mylib.http.RequestPool;
import org.chengpx.test180428.R;
import org.chengpx.test180428.domain.CarBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 公司交通单双号管制功能
 * <p>
 * create at 2018/4/29 15:34 by chengpx
 */
public class TrafficControlFragment extends BaseFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

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
    /**
     * 出行车辆 CarId 集合
     */
    private List<Integer> mTravelCarIdList;
    private MyAdapter mMyAdapter;
    private StringBuilder mTravelCarIdStrBuilder;
    private Calendar mCalendar;
    private int[] mCarIdArr = {
            1, 2, 3
    };
    private Timer mTimer;

    @Override
    protected void initListener() {
        trafficcontrol_tv_date.setOnClickListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTrafficControlFragment = this;
        mDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        mCalendar = Calendar.getInstance();
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
        mCalendar = null;
        mDateFormat = null;
        mTrafficControlFragment = null;
    }

    @Override
    protected void main() {
        mMyAdapter = new MyAdapter();
        mTimer = new Timer();

        trafficcontrol_tv_date.setText(mDateFormat.format(mCalendar.getTime()));
        trafficcontrol_tv_CarIds.setText(mTravelCarIdStrBuilder.toString());
        trafficcontrol_lv_setting.setAdapter(mMyAdapter);
        mMyAdapter.notifyDataSetChanged();

        GetTrafficLightNowStatusCallBack getTrafficLightNowStatusCallBack
                = new GetTrafficLightNowStatusCallBack(Map.class, mTrafficControlFragment);
        mTimer.schedule(new GetTrafficLightNowStatusTimerTask(getTrafficLightNowStatusCallBack), 0, 1000);
    }

    @Override
    protected void initData() {
        mTravelCarIdList = new ArrayList<Integer>();
        mCarBeanList = new ArrayList<>();
        mTravelCarIdStrBuilder = new StringBuilder();

        int dayForMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        int oddeven = dayForMonth % 2;
        if (oddeven == 0) {
            mTravelCarIdStrBuilder.append("双号出行车辆: ");
        } else {
            mTravelCarIdStrBuilder.append("单号出行车辆: ");
        }
        for (int carId : mCarIdArr) {
            if (carId % 2 == oddeven) {
                mTravelCarIdList.add(carId);
                mTravelCarIdStrBuilder.append(carId).append(",");
            }
        }
        mTravelCarIdStrBuilder.delete(mTravelCarIdStrBuilder.length() - 1, mTravelCarIdStrBuilder.length());

        CarBean carBean = new CarBean();
        carBean.setCarId(mCarIdArr[mGetCarMoveCarIdIndex]);
        RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetCarMove.do",
                carBean, new GetCarMoveCallBack(CarBean.class, mTrafficControlFragment));
    }

    @Override
    protected void onDims() {
        mTravelCarIdList = null;
        mTravelCarIdStrBuilder = null;
        mCarBeanList = null;
        mMyAdapter = null;
        mGetCarMoveCarIdIndex = 0;
        mTimer.cancel();
        mTimer = null;
    }

    @Override
    public void onClick(View v) {
        if (R.id.trafficcontrol_tv_date == v.getId()) {
            showDateDialog();
        } else if (v instanceof Switch) {
            Switch aSwitch = (Switch) v;
            CarBean carBean = new CarBean();
            carBean.setCarId((Integer) aSwitch.getTag());
            if (aSwitch.isChecked()) {
                carBean.setCarAction("Start");
            } else {
                carBean.setCarAction("Stop");
            }
            RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/SetCarMove.do",
                    carBean, new SetCarMoveCallBack(Map.class, mTrafficControlFragment));
        }
    }

    /**
     * 弹出日期对话框
     */
    private void showDateDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(mFragmentActivity, this, mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (view.isShown()) {
            Log.d(sTag, "onDateSet");
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            onDims();
            initData();
            main();
        }
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
                MyAdapter myAdapter = mTrafficControlFragment_inner.getMyAdapter();
                if (myAdapter != null) {
                    myAdapter.notifyDataSetChanged();
                }
                Log.d(sTag, mTrafficControlFragment_inner.mCarBeanList.toString());
            }
        }

    }

    private static class SetCarMoveCallBack extends HttpUtils.Callback<Map> {

        private final TrafficControlFragment mTrafficControlFragment_inner;

        /**
         * @param mapClass               结果数据封装体类型字节码
         * @param trafficControlFragment
         */
        public SetCarMoveCallBack(Class<Map> mapClass, TrafficControlFragment trafficControlFragment) {
            super(mapClass);
            mTrafficControlFragment_inner = trafficControlFragment;
        }

        @Override
        protected void onSuccess(Map map) {
            Toast.makeText(mTrafficControlFragment_inner.getContext(), (String) map.get("result"), Toast.LENGTH_SHORT).show();
        }

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCarBeanList.size();
        }

        @Override
        public CarBean getItem(int position) {
            return mCarBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.lv_trafficcontrol_lv_setting,
                        trafficcontrol_lv_setting, false);
                viewHolder = ViewHolder.get(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CarBean carBean = getItem(position);
            viewHolder.getTrafficcontrol_tv_CarId().setText(carBean.getCarId() + " 号");
            Switch trafficcontrol_switch_carActionControl = viewHolder.getTrafficcontrol_switch_CarActionControl();
            if ("Start".equals(carBean.getCarAction())) {
                trafficcontrol_switch_carActionControl.setChecked(true);
            } else if ("Stop".equals(carBean.getCarAction())) {
                trafficcontrol_switch_carActionControl.setChecked(false);
            }
            if (mTravelCarIdList.contains(carBean.getCarId())) {
                trafficcontrol_switch_carActionControl.setEnabled(true);
            } else {
                trafficcontrol_switch_carActionControl.setEnabled(false);
            }
            trafficcontrol_switch_carActionControl.setTag(carBean.getCarId());
            trafficcontrol_switch_carActionControl.setOnClickListener(TrafficControlFragment.this);
            return convertView;
        }
    }

    private static class ViewHolder {

        private final TextView trafficcontrol_tv_CarId;
        private final Switch trafficcontrol_switch_CarActionControl;

        ViewHolder(View view) {
            trafficcontrol_tv_CarId = (TextView) view.findViewById(R.id.trafficcontrol_tv_CarId);
            trafficcontrol_switch_CarActionControl = (Switch) view.findViewById(R.id.trafficcontrol_switch_CarActionControl);
        }

        public static ViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new ViewHolder(view);
                view.setTag(tag);
            }
            return (ViewHolder) tag;
        }

        public TextView getTrafficcontrol_tv_CarId() {
            return trafficcontrol_tv_CarId;
        }

        public Switch getTrafficcontrol_switch_CarActionControl() {
            return trafficcontrol_switch_CarActionControl;
        }

    }

    private class GetTrafficLightNowStatusTimerTask extends TimerTask {

        private final Map<String, String> mMap;
        private final GetTrafficLightNowStatusCallBack mGetTrafficLightNowStatusCallBack_inner;

        GetTrafficLightNowStatusTimerTask(GetTrafficLightNowStatusCallBack getTrafficLightNowStatusCallBack) {
            super();
            mGetTrafficLightNowStatusCallBack_inner = getTrafficLightNowStatusCallBack;
            mMap = new HashMap<>();
            mMap.put("TrafficLightId", "1");
        }

        @Override
        public void run() {
            RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetTrafficLightNowStatus.do",
                    mMap, mGetTrafficLightNowStatusCallBack_inner);
        }

    }

    private static class GetTrafficLightNowStatusCallBack extends HttpUtils.Callback<Map> {

        private final TrafficControlFragment mTrafficControlFragment_inner;

        /**
         * @param mapClass               结果数据封装体类型字节码
         * @param trafficControlFragment
         */
        GetTrafficLightNowStatusCallBack(Class<Map> mapClass, TrafficControlFragment trafficControlFragment) {
            super(mapClass);
            mTrafficControlFragment_inner = trafficControlFragment;
        }

        @Override
        protected void onSuccess(Map map) {
            String status = (String) map.get("Status");
            switch (status) {
                case "Red":
                    mTrafficControlFragment_inner.getTrafficcontrol_iv_redlight().setImageResource(R.drawable.shape_oval_red);
                    mTrafficControlFragment_inner.getTrafficcontrol_iv_yellowlight().setImageResource(R.drawable.shape_oval_gray);
                    mTrafficControlFragment_inner.getTrafficcontrol_iv_greenlight().setImageResource(R.drawable.shape_oval_gray);
                    break;
                case "Yellow":
                    mTrafficControlFragment_inner.getTrafficcontrol_iv_redlight().setImageResource(R.drawable.shape_oval_gray);
                    mTrafficControlFragment_inner.getTrafficcontrol_iv_yellowlight().setImageResource(R.drawable.shape_oval_yellow);
                    mTrafficControlFragment_inner.getTrafficcontrol_iv_greenlight().setImageResource(R.drawable.shape_oval_gray);
                    break;
                case "Green":
                    mTrafficControlFragment_inner.getTrafficcontrol_iv_redlight().setImageResource(R.drawable.shape_oval_gray);
                    mTrafficControlFragment_inner.getTrafficcontrol_iv_yellowlight().setImageResource(R.drawable.shape_oval_gray);
                    mTrafficControlFragment_inner.getTrafficcontrol_iv_greenlight().setImageResource(R.drawable.shape_oval_green);
                    break;
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

    public MyAdapter getMyAdapter() {
        return mMyAdapter;
    }

    public List<Integer> getTravelCarIdList() {
        return mTravelCarIdList;
    }

    public StringBuilder getTravelCarIdStrBuilder() {
        return mTravelCarIdStrBuilder;
    }

}
