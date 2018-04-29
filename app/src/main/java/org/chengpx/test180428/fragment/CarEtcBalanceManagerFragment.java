package org.chengpx.test180428.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.chengpx.mylib.http.HttpUtils;
import org.chengpx.mylib.http.RequestPool;
import org.chengpx.mylib.BaseFragment;
import org.chengpx.test180428.R;
import org.chengpx.test180428.dao.CarDao;
import org.chengpx.test180428.dao.UserDao;
import org.chengpx.test180428.domain.CarBean;
import org.chengpx.test180428.domain.UserBean;

import java.util.Date;
import java.util.Map;

/**
 * 第一题个人车辆ETC账户管理功能
 * <p>
 * create at 2018/4/28 20:27 by chengpx
 */
public class CarEtcBalanceManagerFragment extends BaseFragment implements View.OnClickListener {

    private static String sTag = "org.chengpx.test180428.fragment.CarEtcBalanceManagerFragment";

    private TextView caretcbalancemanager_tv_balance;
    private Spinner caretcbalancemanager_spinner_CarId;
    private TextView caretcbalancemanager_et_Money;
    private Button caretcbalancemanager_btn_search;
    private Button caretcbalancemanager_btn_recharge;

    private CarEtcBalanceManagerFragment mCarEtcBalanceManagerFragment;
    private Integer[] mCarIdArr = {
            1, 2, 3
    };

    @Override
    protected void initListener() {
        caretcbalancemanager_btn_search.setOnClickListener(this);
        caretcbalancemanager_btn_recharge.setOnClickListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCarEtcBalanceManagerFragment = this;
        View view = inflater.inflate(R.layout.fragment_caretcbalancemanager, container, false);
        caretcbalancemanager_tv_balance = (TextView) view.findViewById(R.id.caretcbalancemanager_tv_balance);
        caretcbalancemanager_spinner_CarId = (Spinner) view.findViewById(R.id.caretcbalancemanager_spinner_CarId);
        caretcbalancemanager_et_Money = (TextView) view.findViewById(R.id.caretcbalancemanager_et_Money);
        caretcbalancemanager_btn_search = (Button) view.findViewById(R.id.caretcbalancemanager_btn_search);
        caretcbalancemanager_btn_recharge = (Button) view.findViewById(R.id.caretcbalancemanager_btn_recharge);
        return view;
    }

    @Override
    protected void onDie() {
    }

    @Override
    protected void main() {
        caretcbalancemanager_spinner_CarId.setAdapter(new ArrayAdapter<Integer>(
                mFragmentActivity, android.R.layout.simple_list_item_1, mCarIdArr
        ));
        search(mCarEtcBalanceManagerFragment);
    }

    private static void search(CarEtcBalanceManagerFragment carEtcBalanceManagerFragment_para) {
        CarBean carBean = new CarBean();
        int selectedItemPosition = carEtcBalanceManagerFragment_para.getCaretcbalancemanager_spinner_CarId().getSelectedItemPosition();
        if (selectedItemPosition < 0) {
            selectedItemPosition = 0;
        }
        carBean.setCarId(carEtcBalanceManagerFragment_para.getCarIdArr()[selectedItemPosition]);
        RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetCarAccountBalance.do",
                carBean, new GetCarAccountBalanceCallBack(CarBean.class, carEtcBalanceManagerFragment_para));
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onDims() {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.caretcbalancemanager_btn_search:
                search(mCarEtcBalanceManagerFragment);
                break;
            case R.id.caretcbalancemanager_btn_recharge:
                recharge();
                break;
        }
    }

    private void recharge() {
        CarBean carBean = new CarBean();
        int selectedItemPosition = caretcbalancemanager_spinner_CarId.getSelectedItemPosition();
        if (selectedItemPosition < 0) {
            selectedItemPosition = 0;
        }
        carBean.setCarId(mCarIdArr[selectedItemPosition]);
        String strMoney = caretcbalancemanager_et_Money.getText().toString();
        if (!strMoney.matches("^[0-9]\\d{0,2}$")) {
            Toast.makeText(mFragmentActivity, "金额非法", Toast.LENGTH_SHORT).show();
            return;
        }
        int money = Integer.parseInt(strMoney);
        carBean.setMoney(money);
        RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/SetCarAccountRecharge.do",
                carBean, new SetCarAccountRechargeCallBack(Map.class, mCarEtcBalanceManagerFragment, carBean));
    }

    private static class GetCarAccountBalanceCallBack extends HttpUtils.Callback<CarBean> {

        private final CarEtcBalanceManagerFragment mCarEtcBalanceManagerFragment_inner;

        /**
         * @param carBeanClass                 结果数据封装体类型字节码
         * @param carEtcBalanceManagerFragment
         */
        GetCarAccountBalanceCallBack(Class<CarBean> carBeanClass, CarEtcBalanceManagerFragment carEtcBalanceManagerFragment) {
            super(carBeanClass);
            mCarEtcBalanceManagerFragment_inner = carEtcBalanceManagerFragment;
        }

        @Override
        protected void onSuccess(CarBean carBean) {
            mCarEtcBalanceManagerFragment_inner.getCaretcbalancemanager_tv_balance().setText(carBean.getBalance() + "");
        }

    }

    private static class SetCarAccountRechargeCallBack extends HttpUtils.Callback<Map> {

        private final CarEtcBalanceManagerFragment mCarEtcBalanceManagerFragment_inner;
        private final CarBean mCarBean_inner;

        /**
         * @param mapClass                     结果数据封装体类型字节码
         * @param carEtcBalanceManagerFragment
         * @param carBean
         */
        SetCarAccountRechargeCallBack(Class<Map> mapClass, CarEtcBalanceManagerFragment carEtcBalanceManagerFragment, CarBean carBean) {
            super(mapClass);
            mCarEtcBalanceManagerFragment_inner = carEtcBalanceManagerFragment;
            mCarBean_inner = carBean;
        }

        @Override
        protected void onSuccess(Map map) {
            Toast.makeText(mCarEtcBalanceManagerFragment_inner.getActivity(), (String) map.get("result"), Toast.LENGTH_SHORT).show();
            search(mCarEtcBalanceManagerFragment_inner);
            UserBean userBean_select = UserDao.getInstance(mCarEtcBalanceManagerFragment_inner.getActivity()).select("uname", "admin");
            if (userBean_select != null) {
                mCarBean_inner.setUser(userBean_select);
                Log.d(sTag, userBean_select.toString());
            }
            mCarBean_inner.setRechargeDate(new Date());
            int insert = CarDao.getInstance(mCarEtcBalanceManagerFragment_inner.getActivity()).insert(mCarBean_inner);
            Log.d(sTag, "CarDao insert: " + insert);
        }

    }

    public TextView getCaretcbalancemanager_tv_balance() {
        return caretcbalancemanager_tv_balance;
    }

    public Spinner getCaretcbalancemanager_spinner_CarId() {
        return caretcbalancemanager_spinner_CarId;
    }

    public TextView getCaretcbalancemanager_et_Money() {
        return caretcbalancemanager_et_Money;
    }

    public Button getCaretcbalancemanager_btn_search() {
        return caretcbalancemanager_btn_search;
    }

    public Button getCaretcbalancemanager_btn_recharge() {
        return caretcbalancemanager_btn_recharge;
    }

    public Integer[] getCarIdArr() {
        return mCarIdArr;
    }

}
