package org.chengpx.test180428;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import org.chengpx.test180428.fragment.MainSlidingMenuFragment;

public class MainActivity extends SlidingFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
    }

    private void initFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fl_sldingmenu, new MainSlidingMenuFragment(), "");
        fragmentTransaction.commit();
    }

    private void initView() {
        setBehindContentView(R.layout.slidingmenu_main);
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        assert windowManager != null;
        int width = windowManager.getDefaultDisplay().getWidth();
        slidingMenu.setBehindOffset((int) (width * (1.0f * 400 / 500) * 1.0f));
    }

}
