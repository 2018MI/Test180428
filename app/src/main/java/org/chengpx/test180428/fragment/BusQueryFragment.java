package org.chengpx.test180428.fragment;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import org.chengpx.mylib.AppException;
import org.chengpx.mylib.BaseFragment;
import org.chengpx.mylib.common.DataUtils;
import org.chengpx.mylib.http.HttpUtils;
import org.chengpx.mylib.http.RequestPool;
import org.chengpx.test180428.R;
import org.chengpx.test180428.domain.BusBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 公交查询模块
 * <p>
 * create at 2018/4/30 20:58 by chengpx
 */
public class BusQueryFragment extends BaseFragment implements Comparator<Map<String, Object>>, View.OnClickListener {

    private static String sTag = "org.chengpx.test180428.fragment.BusQueryFragment";

    private Integer[] mBusIdArr = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
    };
    private Integer[] mBusStationIdArr = {
            1, 2
    };
    private String[] mBusStationName = {
            "中医院站", "联想大厦站"
    };
    private List<List<Map<String, Object>>> mBusStationInfoList;
    private int mReqBusStationIdIndex;
    private Timer mTimer;
    private Map<Integer, BusBean> mBusBeanMap;
    private BusQueryFragment mBusQueryFragment;
    private List mBusStationIdList;
    private TextView busquery_tv_carryingcapacity;
    private Button busquery_btn_detail;
    private ExpandableListView busquery_expandablelistview_busstationinfo;
    private MyBaseExpandableListAdapter mMyBaseExpandableListAdapter;
    private Integer[] mShowBusIdArr = {
            1, 2
    };
    private ListView busquery_lv_carrayingcapacity;
    private AlertDialog mAlertDialog;

    @Override
    protected void initListener() {
        busquery_btn_detail.setOnClickListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBusQueryFragment = this;
        View view = inflater.inflate(R.layout.fragment_busquery, container, false);
        busquery_tv_carryingcapacity = (TextView) view.findViewById(R.id.busquery_tv_carryingcapacity);
        busquery_btn_detail = (Button) view.findViewById(R.id.busquery_btn_detail);
        busquery_expandablelistview_busstationinfo = (ExpandableListView) view.findViewById(R.id.busquery_expandablelistview_busstationinfo);
        return view;
    }

    @Override
    protected void onDie() {
    }

    @Override
    protected void main() {
        mMyBaseExpandableListAdapter = new MyBaseExpandableListAdapter();
        busquery_expandablelistview_busstationinfo.setAdapter(mMyBaseExpandableListAdapter);
    }

    @Override
    protected void initData() {
        mBusStationIdList = Arrays.asList(mBusStationIdArr);
        mBusBeanMap = new HashMap<>();
        mBusStationInfoList = new ArrayList<>();
        for (Integer aMBusStationIdArr : mBusStationIdArr) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (int busId : mShowBusIdArr) {
                mapList.add(new HashMap<String, Object>());
            }
            mBusStationInfoList.add(mapList);
        }
        mTimer = new Timer();
        GetBusstationInfoCallBack getBusstationInfoCallBack = new GetBusstationInfoCallBack(List.class, mBusQueryFragment);
        mTimer.schedule(new MyTimerTask(getBusstationInfoCallBack), 0, 3000);
    }

    @Override
    protected void onDims() {
        mReqBusStationIdIndex = 0;
        mMyBaseExpandableListAdapter = null;
        mBusBeanMap = null;
        mBusStationInfoList = null;
        mTimer.cancel();
        mTimer = null;
    }

    @Override
    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
        try {
            return DataUtils.obj2int(o1.get("Distance")) - DataUtils.obj2int(o2.get("Distance"));
        } catch (AppException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.busquery_btn_detail:
                showCarryingCapacityDialog();
                break;
            case R.id.busquery_btn_dialogback:
                mAlertDialog.dismiss();
                mAlertDialog = null;
                break;
        }
    }

    /**
     * 弹出公交车载客情况统计
     */
    private void showCarryingCapacityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentActivity);
        View dialogView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.dialog_showcarryingcapacity, null);
        builder.setView(dialogView);
        mAlertDialog = builder.create();
        mAlertDialog.show();
        busquery_lv_carrayingcapacity = dialogView.findViewById(R.id.busquery_lv_carrayingcapacity);
        busquery_lv_carrayingcapacity.setAdapter(new MyAdapter());
        TextView busquery_tv_dialogcarryingcapacity = dialogView.findViewById(R.id.busquery_tv_dialogcarryingcapacity);
        busquery_tv_dialogcarryingcapacity.setText("当前承载能力: " +  calcSumCarryingCapacity() + " 人");
        Button busquery_btn_dialogback = dialogView.findViewById(R.id.busquery_btn_dialogback);
        busquery_btn_dialogback.setOnClickListener(this);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBusBeanMap.size();
        }

        @Override
        public BusBean getItem(int position) {
            return mBusBeanMap.get(mBusIdArr[position]);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.lv_busquery_lv_carrayingcapacity,
                        busquery_lv_carrayingcapacity, false);
                viewHolder = ViewHolder.get(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BusBean busBean = getItem(position);
            viewHolder.getBusquery_tv_lvnum().setText(position + "");
            viewHolder.getBusquery_tv_lvBusId().setText(busBean.getBusId() + "");
            viewHolder.getBusquery_tv_lvcarryingcapacity().setText(busBean.getBusCapacity() + " 人");
            return convertView;
        }

    }

    private static class ViewHolder {

        private final TextView busquery_tv_lvnum;
        private final TextView busquery_tv_lvBusId;
        private final TextView busquery_tv_lvcarryingcapacity;

        private ViewHolder(View view) {
            busquery_tv_lvnum = view.findViewById(R.id.busquery_tv_lvnum);
            busquery_tv_lvBusId = view.findViewById(R.id.busquery_tv_lvBusId);
            busquery_tv_lvcarryingcapacity = view.findViewById(R.id.busquery_tv_lvcarryingcapacity);
        }

        public static ViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new ViewHolder(view);
                view.setTag(tag);
            }
            return (ViewHolder) tag;
        }

        public TextView getBusquery_tv_lvnum() {
            return busquery_tv_lvnum;
        }

        public TextView getBusquery_tv_lvBusId() {
            return busquery_tv_lvBusId;
        }

        public TextView getBusquery_tv_lvcarryingcapacity() {
            return busquery_tv_lvcarryingcapacity;
        }

    }

    private static class GroupViewHolder {

        private final TextView busquery_tv_bussationname;

        private GroupViewHolder(View view) {
            busquery_tv_bussationname = view.findViewById(R.id.busquery_tv_bussationname);
        }

        public static GroupViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new GroupViewHolder(view);
                view.setTag(tag);
            }
            return (GroupViewHolder) tag;
        }

        public TextView getBusquery_tv_bussationname() {
            return busquery_tv_bussationname;
        }

    }

    private static class ChildViewHolder {

        private final TextView busquery_tv_CarId;
        private final TextView busquery_tv_BusCapacity;
        private final TextView busquery_tv_arrivalsdate;
        private final TextView busquery_tv_distance;

        private ChildViewHolder(View view) {
            busquery_tv_CarId = view.findViewById(R.id.busquery_tv_CarId);
            busquery_tv_BusCapacity = view.findViewById(R.id.busquery_tv_BusCapacity);
            busquery_tv_arrivalsdate = view.findViewById(R.id.busquery_tv_arrivalsdate);
            busquery_tv_distance = view.findViewById(R.id.busquery_tv_distance);
        }

        public static ChildViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new ChildViewHolder(view);
                view.setTag(tag);
            }
            return (ChildViewHolder) tag;
        }

        public TextView getBusquery_tv_CarId() {
            return busquery_tv_CarId;
        }

        public TextView getBusquery_tv_BusCapacity() {
            return busquery_tv_BusCapacity;
        }

        public TextView getBusquery_tv_arrivalsdate() {
            return busquery_tv_arrivalsdate;
        }

        public TextView getBusquery_tv_distance() {
            return busquery_tv_distance;
        }

    }

    private class MyBaseExpandableListAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mBusStationInfoList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mShowBusIdArr.length;
        }

        @Override
        public List<Map<String, Object>> getGroup(int groupPosition) {
            return mBusStationInfoList.get(groupPosition);
        }

        @Override
        public Map<String, Object> getChild(int groupPosition, int childPosition) {
            return mBusStationInfoList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        /**
         * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们, 存在疑问
         *
         * @return
         */
        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder groupViewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity)
                        .inflate(R.layout.expandablelistview_busquery_expandablelistview_busstationinfo_group, parent, false);
                groupViewHolder = GroupViewHolder.get(convertView);
            } else {
                groupViewHolder = (GroupViewHolder) convertView.getTag();
            }
            groupViewHolder.getBusquery_tv_bussationname().setText(mBusStationName[groupPosition]);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder childViewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity)
                        .inflate(R.layout.expandablelistview_busquery_expandablelistview_busstationinfo_child, parent, false);
                childViewHolder = ChildViewHolder.get(convertView);
            } else {
                childViewHolder = (ChildViewHolder) convertView.getTag();
            }
            Map<String, Object> objMap = getChild(groupPosition, childPosition);
            Object busId = objMap.get("BusId");
            if (busId != null) {
                childViewHolder.getBusquery_tv_CarId().setText(busId + " 号");
            }
            Object objBusBean = objMap.get("busBean");
            if (objBusBean != null) {
                if (objBusBean instanceof BusBean) {
                    BusBean busBean = (BusBean) objBusBean;
                    childViewHolder.getBusquery_tv_BusCapacity().setText("(" + busBean.getBusCapacity() + " 人)");
                }
            }
            Object distance = objMap.get("Distance");
            if (distance != null) {
                childViewHolder.getBusquery_tv_distance().setText(distance + " 米");
            }
            return convertView;
        }

        /**
         * 指定位置的孩子是否可以选择
         *
         * @param groupPosition
         * @param childPosition
         * @return
         */
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

    }

    private class MyTimerTask extends TimerTask {

        private final GetBusstationInfoCallBack mGetBusstationInfoCallBack_inner;

        MyTimerTask(GetBusstationInfoCallBack getBusstationInfoCallBack) {
            mGetBusstationInfoCallBack_inner = getBusstationInfoCallBack;
        }

        @Override
        public void run() {
            mReqBusStationIdIndex = 0;
            HashMap<String, Integer> values = new HashMap<>();
            values.put("BusStationID", mBusStationIdArr[mReqBusStationIdIndex]);
            RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetBusstationInfo.do",
                    values, mGetBusstationInfoCallBack_inner);
            mFragmentActivity.runOnUiThread(new ShowCarryingCapacityRun());
        }

    }

    private class ShowCarryingCapacityRun implements Runnable {

        @Override
        public void run() {
            busquery_tv_carryingcapacity.setText("当前承载能力: " +  calcSumCarryingCapacity() + " 人");
        }

    }

    private int calcSumCarryingCapacity() {
        int sum = 0;
        for (Map.Entry<Integer, BusBean> busBeanEntry : mBusBeanMap.entrySet()) {
            sum += busBeanEntry.getValue().getBusCapacity();
        }
        return sum;
    }

    private static class GetBusCapacityCallBack extends HttpUtils.Callback<BusBean> {

        private final BusQueryFragment mBusQueryFragment_inner;
        private final Map<String, Object> mObjMap;

        /**
         * @param busBeanClass     结果数据封装体类型字节码
         * @param busQueryFragment
         * @param objMap
         */
        GetBusCapacityCallBack(Class<BusBean> busBeanClass, BusQueryFragment busQueryFragment, Map<String, Object> objMap) {
            super(busBeanClass);
            mBusQueryFragment_inner = busQueryFragment;
            mObjMap = objMap;
        }

        @Override
        protected void onSuccess(BusBean busBean) {
            mObjMap.put("busBean", busBean);
            try {
                int busId = DataUtils.obj2int(mObjMap.get("BusId"));
                busBean.setBusId(busId);
                mBusQueryFragment_inner.getBusBeanMap().put(busId, busBean);
            } catch (AppException e) {
                e.printStackTrace();
            }
            if (mBusQueryFragment_inner.getMyBaseExpandableListAdapter() != null) {
                mBusQueryFragment_inner.getMyBaseExpandableListAdapter().notifyDataSetChanged();
            }
        }

    }

    private static class GetBusstationInfoCallBack extends HttpUtils.Callback<List> {

        private final BusQueryFragment mBusQueryFragment_inner;

        /**
         * @param listClass        结果数据封装体类型字节码
         * @param busQueryFragment
         */
        GetBusstationInfoCallBack(Class<List> listClass, BusQueryFragment busQueryFragment) {
            super(listClass);
            mBusQueryFragment_inner = busQueryFragment;
        }

        @Override
        protected void onSuccess(List list) {
            int busStationId = mBusQueryFragment_inner.getBusStationIdArr()[mBusQueryFragment_inner.getReqBusStationIdIndex()];
            int busStationIdIndexOf = mBusQueryFragment_inner.getBusStationIdList().indexOf(busStationId);
            for (int i = 0; i < list.size(); i++) {
                Object obj = list.get(i);
                if (!(obj instanceof Map)) {
                    break;
                }
                Map map = (Map) obj;
                BusBean busBean = new BusBean();
                int busId = 0;
                try {
                    busId = DataUtils.obj2int(map.get("BusId"));
                    busBean.setBusId(busId);
                } catch (AppException e) {
                    e.printStackTrace();
                }
                Map<String, Object> objMap = new HashMap<>();
                int binarySearch = Arrays.binarySearch(mBusQueryFragment_inner.getShowBusIdArr(), busId);
                if (binarySearch >= 0) {
                    mBusQueryFragment_inner.getBusStationInfoList().get(busStationIdIndexOf).set(binarySearch, objMap);
                }
                objMap.put("BusId", busId);
                RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetBusCapacity.do",
                        busBean, new GetBusCapacityCallBack(BusBean.class, mBusQueryFragment_inner, objMap));
                objMap.put("Distance", map.get("Distance"));
            }
            mBusQueryFragment_inner.setReqBusStationIdIndex(mBusQueryFragment_inner.getReqBusStationIdIndex() + 1);
            if (mBusQueryFragment_inner.getReqBusStationIdIndex() < mBusQueryFragment_inner.getBusStationIdArr().length) {
                Map<String, Integer> values = new HashMap<>();
                values.put("BusStationID", mBusQueryFragment_inner.getBusStationIdArr()[mBusQueryFragment_inner.getReqBusStationIdIndex()]);
                RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetBusstationInfo.do",
                        values, this);
            } else {
                for (int i = 0; i < mBusQueryFragment_inner.getBusStationInfoList().size(); i++) {
                    List<Map<String, Object>> mapList = mBusQueryFragment_inner.getBusStationInfoList().get(i);
                    Collections.sort(mapList, mBusQueryFragment_inner);
                }
                Log.d(sTag, mBusQueryFragment_inner.getBusStationInfoList().toString());
                if (mBusQueryFragment_inner.getMyBaseExpandableListAdapter() != null) {
                    mBusQueryFragment_inner.getMyBaseExpandableListAdapter().notifyDataSetChanged();
                }
            }
        }

    }

    public Integer[] getBusIdArr() {
        return mBusIdArr;
    }

    public Integer[] getBusStationIdArr() {
        return mBusStationIdArr;
    }

    public List<List<Map<String, Object>>> getBusStationInfoList() {
        return mBusStationInfoList;
    }

    public int getReqBusStationIdIndex() {
        return mReqBusStationIdIndex;
    }

    public Timer getTimer() {
        return mTimer;
    }

    public Map<Integer, BusBean> getBusBeanMap() {
        return mBusBeanMap;
    }

    public void setReqBusStationIdIndex(int reqBusStationIdIndex) {
        mReqBusStationIdIndex = reqBusStationIdIndex;
    }

    public List getBusStationIdList() {
        return mBusStationIdList;
    }

    public MyBaseExpandableListAdapter getMyBaseExpandableListAdapter() {
        return mMyBaseExpandableListAdapter;
    }

    public Integer[] getShowBusIdArr() {
        return mShowBusIdArr;
    }

}
