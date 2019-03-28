package cn.ifhu.fragments.reviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.common.StringUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ifhu.R;
import cn.ifhu.activity.ReviewListActivity;
import cn.ifhu.adapter.AllReviewsAdapter;
import cn.ifhu.adapter.NewOrdersAdapter;
import cn.ifhu.base.BaseFragment;
import cn.ifhu.base.BaseObserver;
import cn.ifhu.bean.BaseEntity;
import cn.ifhu.bean.OrderBean;
import cn.ifhu.bean.ReviewBean;
import cn.ifhu.dialog.DialogListFragment;
import cn.ifhu.dialog.nicedialog.NiceDialog;
import cn.ifhu.dialog.nicedialog.ViewConvertListener;
import cn.ifhu.net.OperationService;
import cn.ifhu.net.OrderService;
import cn.ifhu.net.RetrofitAPIManager;
import cn.ifhu.net.SchedulerUtils;
import cn.ifhu.utils.StringUtils;
import cn.ifhu.utils.ToastHelper;
import cn.ifhu.utils.UserLogic;
import io.reactivex.Observer;

/**
 * @author fuhongliang
 */
public class AllReviewFragment extends BaseFragment {

    @BindView(R.id.recycler_list)
    RecyclerView recyclerList;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout layoutSwipeRefresh;
    Unbinder unbinder;

    AllReviewsAdapter allReviewsAdapter;

    public static AllReviewFragment newInstance() {
        return new AllReviewFragment();
    }


    public AllReviewFragment() {
    }


    public void getAllReviews() {
        layoutSwipeRefresh.setRefreshing(true);
        RetrofitAPIManager.create(OperationService.class).getStoreReviews(UserLogic.getUser().getStore_id())
                .compose(SchedulerUtils.ioMainScheduler()).subscribe(new BaseObserver<ReviewBean>(true) {
            @Override
            protected void onApiComplete() {
                layoutSwipeRefresh.setRefreshing(false);
            }

            @Override
            protected void onSuccees(BaseEntity<ReviewBean> t) throws Exception {
                allReviewsAdapter.updateData(t.getData());
                ((ReviewListActivity)getActivity()).setHeaderData(t.getData().getHaoping());
            }
        });
    }

    public void ReViewReply(String content,int reviewId) {
        layoutSwipeRefresh.setRefreshing(true);
        RetrofitAPIManager.create(OperationService.class).storeFeedback(UserLogic.getUser().getStore_id(),content,reviewId)
                .compose(SchedulerUtils.ioMainScheduler()).subscribe(new BaseObserver<Object>(true) {
            @Override
            protected void onApiComplete() {
                layoutSwipeRefresh.setRefreshing(false);
            }

            @Override
            protected void onSuccees(BaseEntity<Object> t) throws Exception {
                ToastHelper.makeText(t.getMessage(), Toast.LENGTH_SHORT, ToastHelper.NORMALTOAST).show();
            }
        });
    }

    public void showAddNoteDialog(int position) {

        final NiceDialog niceDialog = NiceDialog.init().setLayoutId(R.layout.add_notes_layout);
        niceDialog.setConvertListener((ViewConvertListener) (holder, dialog) -> {
            final EditText editText = holder.getView(R.id.edit_input);
            final TextView mTvAdd = holder.getView(R.id.tv_add);
            final TextView mTvCancel = holder.getView(R.id.tv_cancel);
            editText.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
            }, 500);

            mTvAdd.setOnClickListener(view -> {
                if (!StringUtils.isEmpty(editText.getText().toString().trim())){
                    ReViewReply(editText.getText().toString(),allReviewsAdapter.getReviewBean().getCom_list().get(position).getCom_id());
                    niceDialog.dismiss();
                }
            });

            mTvCancel.setOnClickListener(view -> niceDialog.dismiss());

        }).setShowBottom(true).show(getActivity().getSupportFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_all, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerList.setLayoutManager(new LinearLayoutManager(getActivity()));
        allReviewsAdapter = new AllReviewsAdapter(new ReviewBean(), getActivity(), new AllReviewsAdapter.OnclickButton() {
            @Override
            public void reply(int position) {
                showAddNoteDialog(position);
            }
        });
        recyclerList.setAdapter(allReviewsAdapter);
        setRefreshLayout();
        getAllReviews();
    }

    @SuppressLint("ResourceAsColor")
    public void setRefreshLayout() {
        layoutSwipeRefresh.setColorSchemeColors(R.color.colorPrimaryDark,
                R.color.colorPrimaryDark,
                R.color.colorPrimaryDark,
                R.color.colorPrimaryDark);

        layoutSwipeRefresh.setOnRefreshListener(() -> {
            getAllReviews();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
