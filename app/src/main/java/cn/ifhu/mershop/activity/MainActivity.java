package cn.ifhu.mershop.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.ifhu.mershop.R;
import cn.ifhu.mershop.activity.login.LoginActivity;
import cn.ifhu.mershop.adapter.FragmentAdapter;
import cn.ifhu.mershop.base.BaseActivity;
import cn.ifhu.mershop.base.BaseFragment;
import cn.ifhu.mershop.base.ViewManager;
import cn.ifhu.mershop.bean.MessageEvent;
import cn.ifhu.mershop.fragments.me.MeFragment;
import cn.ifhu.mershop.fragments.neworder.NewOrderFragment;
import cn.ifhu.mershop.fragments.operation.OperationFragment;
import cn.ifhu.mershop.fragments.orders.OrdersFragment;
import cn.ifhu.mershop.utils.UserLogic;

import static cn.ifhu.mershop.utils.Constants.LOGOUT;

/**
 * @author fuhongliang
 */
public class MainActivity extends BaseActivity {

    private ViewPager mPager;
    private List<BaseFragment> mFragments;
    private FragmentAdapter mAdapter;
    BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        resetToDefaultIcon();
        return setCurrentItemIcon(item);
    };

    public boolean setCurrentItemIcon(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.navigation_home) {
            item.setIcon(R.drawable.status_ic_dclm);
            mPager.setCurrentItem(0);
            return true;
        } else if (i == R.id.navigation_orders) {
            item.setIcon(R.drawable.status_ic_ddglx);
            mPager.setCurrentItem(1);
            return true;
        } else if (i == R.id.navigation_operation) {
            item.setIcon(R.drawable.status_ic_yym);
            mPager.setCurrentItem(2);
            ((OperationFragment)mFragments.get(2)).getOperationData();
            return true;
        } else if (i == R.id.navigation_me) {
            item.setIcon(R.drawable.status_ic_wdm);
            mPager.setCurrentItem(3);
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        initViewPager();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        navigation.setItemIconTintList(null);
        navigation.setSelectedItemId(R.id.navigation_home);
        Resources resource = getBaseContext().getResources();
        ColorStateList csl = resource.getColorStateList(R.color.bottom_navigation_color);
        navigation.setItemTextColor(csl);
    }


    private void resetToDefaultIcon() {
        MenuItem home = navigation.getMenu().findItem(R.id.navigation_home);
        MenuItem orders = navigation.getMenu().findItem(R.id.navigation_orders);
        MenuItem operation = navigation.getMenu().findItem(R.id.navigation_operation);
        MenuItem me = navigation.getMenu().findItem(R.id.navigation_me);
        home.setIcon(R.drawable.status_ic_dclx);
        orders.setIcon(R.drawable.status_ic_ddglm);
        operation.setIcon(R.drawable.status_ic_yyx);
        me.setIcon(R.drawable.status_ic_wdx);
    }

    private void initViewPager() {
        ViewManager.getInstance().addFragment(0, NewOrderFragment.newInstance());
        ViewManager.getInstance().addFragment(1, OrdersFragment.newInstance());
        ViewManager.getInstance().addFragment(2, OperationFragment.newInstance());
        ViewManager.getInstance().addFragment(3, MeFragment.newInstance());
        mFragments = ViewManager.getInstance().getAllFragment();
        mPager = findViewById(R.id.container_pager);
        mPager.setOffscreenPageLimit(8);
        mAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragments);
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                resetToDefaultIcon();
                setCurrentItemIcon(navigation.getMenu().getItem(i));
                navigation.getMenu().getItem(i).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void logout() {
        UserLogic.loginOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMessage()) {
            case LOGOUT:
                logout();
                break;
            default:
        }
    }
}
