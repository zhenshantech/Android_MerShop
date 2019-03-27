package cn.ifhu.fragments.reviews;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ifhu.R;
import cn.ifhu.adapter.NewOrdersAdapter;
import cn.ifhu.base.BaseFragment;
import cn.ifhu.base.BaseObserver;
import cn.ifhu.bean.BaseEntity;
import cn.ifhu.bean.OrderBean;
import cn.ifhu.dialog.DialogListFragment;
import cn.ifhu.net.OrderService;
import cn.ifhu.net.RetrofitAPIManager;
import cn.ifhu.net.SchedulerUtils;
import cn.ifhu.utils.ToastHelper;
import cn.ifhu.utils.UserLogic;

/**
 * @author fuhongliang
 */
public class ReviewFragment extends BaseFragment {

    @BindView(R.id.recycler_list)
    RecyclerView recyclerList;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout layoutSwipeRefresh;
    Unbinder unbinder;

    NewOrdersAdapter newOrdersAdapter;
    private List<OrderBean> mDatas;
    private ArrayList<String> reasonList;
    public static ReviewFragment newInstance() {
        return new ReviewFragment();
    }


    public ReviewFragment() {
        // Required empty public constructor
    }

    protected void initData() {
        mDatas = new ArrayList<>();
        reasonList = new ArrayList<>();
        reasonList.add("商品已售罄");
        reasonList.add("商家已打样");
        reasonList.add("其他");
    }

    public void getNewOrders(){
        Logger.d("getNewOrders");
        layoutSwipeRefresh.setRefreshing(true);
        RetrofitAPIManager.create(OrderService.class).getNewOrder(UserLogic.getUser().getStore_id())
                .compose(SchedulerUtils.ioMainScheduler()).subscribe(new BaseObserver<ArrayList<OrderBean>>(true) {

            @Override
            protected void onApiComplete() {
                Logger.d("onApiComplete");
                layoutSwipeRefresh.setRefreshing(false);
            }

            @Override
            protected void onSuccees(BaseEntity<ArrayList<OrderBean>> t) throws Exception {
                Logger.d("onSuccees"+t);
                if (t.getData().isEmpty()){
//                    recyclerList
                }else {
                    mDatas.clear();
                    mDatas.addAll(t.getData());
                    newOrdersAdapter.updateData(mDatas);
                }

                ToastHelper.makeText("刷新成功！", Toast.LENGTH_SHORT,ToastHelper.NORMALTOAST).show();
            }
        });
    }

    public void receiveOrder(String orderId,int position){

        RetrofitAPIManager.create(OrderService.class).receiveOrder(orderId)
                .compose(SchedulerUtils.ioMainScheduler()).subscribe(new BaseObserver<Object>(true) {

            @Override
            protected void onApiComplete() {

            }

            @Override
            protected void onSuccees(BaseEntity<Object> t) throws Exception {
                mDatas.remove(position);
                newOrdersAdapter.updateData(mDatas);
                ToastHelper.makeText("接单成功",Toast.LENGTH_SHORT,ToastHelper.NORMALTOAST).show();
            }
        });
    }

    public void refuseOrder(int orderId,int position,String reason){

        RetrofitAPIManager.create(OrderService.class).refuseOrder(orderId,reason)
                .compose(SchedulerUtils.ioMainScheduler()).subscribe(new BaseObserver<Object>(true) {

            @Override
            protected void onApiComplete() {

            }

            @Override
            protected void onSuccees(BaseEntity<Object> t) throws Exception {
                mDatas.remove(position);
                newOrdersAdapter.updateData(mDatas);
                ToastHelper.makeText("已拒绝！",Toast.LENGTH_SHORT,ToastHelper.NORMALTOAST).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_order, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.d("onViewCreated");
        initData();
        recyclerList.setLayoutManager(new LinearLayoutManager(getActivity()));
        newOrdersAdapter = new NewOrdersAdapter(mDatas, getContext(), new NewOrdersAdapter.OnclickButton() {
            @Override
            public void refuse(int position) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "请选择拒单理由");
                bundle.putStringArrayList("stringList", reasonList);
                showOptionDialog(bundle,position);
            }

            @Override
            public void accept(int position) {
                receiveOrder(mDatas.get(position).getOrder_id()+"",position);
            }
        });
        recyclerList.setAdapter(newOrdersAdapter);
        setRefreshLayout();
    }

    @SuppressLint("ResourceAsColor")
    public void setRefreshLayout() {
        layoutSwipeRefresh.setColorSchemeColors(R.color.colorPrimaryDark,
                R.color.colorPrimaryDark,
                R.color.colorPrimaryDark,
                R.color.colorPrimaryDark);

        layoutSwipeRefresh.setOnRefreshListener(() -> {
            getNewOrders();
        });
    }

    /**
     * @param bundle 携带参数
     */
    public void showOptionDialog(Bundle bundle,int position) {
        DialogListFragment.showOperateDialog(getActivity().getSupportFragmentManager(), bundle, string -> {
            refuseOrder(mDatas.get(position).getOrder_id(),position,string);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}