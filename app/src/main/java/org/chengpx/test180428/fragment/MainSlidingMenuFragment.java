package org.chengpx.test180428.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.chengpx.mylib.BaseFragment;
import org.chengpx.test180428.R;

/**
 * 侧滑菜单
 * <p>
 * create at 2018/4/28 20:20 by chengpx
 */
public class MainSlidingMenuFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mainsldingmenu_lv_itemlist;
    private FragmentActivity mFragmentActivity;

    private String[] mItemStrArr = {
            "第一题个人车辆ETC账户管理功能", "公司交通单双号管制功能", "公交查询模块"
    };
    private BaseFragment[] mBaseFragmentArr = {
            new CarEtcBalanceManagerFragment(), new TrafficControlFragment(), new BusQueryFragment()
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mainslidingmenu, container, false);
        mFragmentActivity = getActivity();
        initView(view);
        initListener();
        return view;
    }

    private void initListener() {
        mainsldingmenu_lv_itemlist.setOnItemClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        main();
    }

    private void main() {
        mainsldingmenu_lv_itemlist.setAdapter(new ArrayAdapter<String>(
                mFragmentActivity, android.R.layout.simple_list_item_1, mItemStrArr
        ));
    }

    private void initData() {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initView(View view) {
        mainsldingmenu_lv_itemlist = (ListView) view.findViewById(R.id.mainsldingmenu_lv_itemlist);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        assert getFragmentManager() != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fl_content, mBaseFragmentArr[position], "");
        fragmentTransaction.commit();
    }

}
