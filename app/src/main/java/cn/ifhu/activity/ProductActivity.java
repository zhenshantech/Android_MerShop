package cn.ifhu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ifhu.R;
import cn.ifhu.adapter.CategoryAdapter;
import cn.ifhu.adapter.ProductAdapter;
import cn.ifhu.base.BaseActivity;
import cn.ifhu.base.BaseObserver;
import cn.ifhu.bean.BaseEntity;
import cn.ifhu.bean.ProductManageBean;
import cn.ifhu.dialog.nicedialog.ConfirmDialog;
import cn.ifhu.net.OperationService;
import cn.ifhu.net.RetrofitAPIManager;
import cn.ifhu.net.SchedulerUtils;
import cn.ifhu.utils.DialogUtils;
import cn.ifhu.utils.ToastHelper;
import cn.ifhu.utils.UserLogic;

/**
 * @author fuhongliang
 */
public class ProductActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lv_category)
    ListView lvCategory;
    @BindView(R.id.lv_product)
    ListView lvProduct;
    ArrayList<ProductManageBean.ClassListBean> mDataArray = new ArrayList<>();
    ArrayList<ProductManageBean.GoodsListBean> mProductArray = new ArrayList<>();
    CategoryAdapter mCategoryAdapter;
    ProductAdapter mProductAdapter;
    @BindView(R.id.rl_manage_category)
    RelativeLayout rlManageCategory;
    @BindView(R.id.rl_add_product)
    RelativeLayout rlAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        View emptyView = LayoutInflater.from(this).inflate(R.layout.neworder_empty, null);
        lvProduct.setEmptyView(emptyView);
        mCategoryAdapter = new CategoryAdapter(mDataArray, this, new CategoryAdapter.ItemOnclick() {
            @Override
            public void onClickItem(int position) {
                mProductData(mDataArray.get(position).getStc_id());
                mCategoryAdapter.notifyDataSetChanged();
            }
        });
        lvCategory.setAdapter(mCategoryAdapter);

        mProductAdapter = new ProductAdapter(mProductArray, this);
        lvProduct.setAdapter(mProductAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData(0);
    }

    public void getData(int class_id){
        setLoadingMessageIndicator(true);
        RetrofitAPIManager.create(OperationService.class).goodsList(UserLogic.getUser().getStore_id(),class_id)
                .compose(SchedulerUtils.ioMainScheduler()).subscribe(new BaseObserver<ProductManageBean>(true) {

            @Override
            protected void onApiComplete() {
                setLoadingMessageIndicator(false);
            }

            @Override
            protected void onSuccees(BaseEntity<ProductManageBean> t) throws Exception {
                if (t.getData().getClass_list().isEmpty()){
                    DialogUtils.showConfirmDialog("温馨提示", "您的店铺还没有商品分类 \n 是否新建商品分类？", "否", "是",getSupportFragmentManager(),  new ConfirmDialog.ButtonOnclick() {
                        @Override
                        public void cancel() {
                            finish();
                        }

                        @Override
                        public void ok() {
                            startActivity(new Intent(ProductActivity.this,AddOrEditProductActivity.class));
                        }
                    });
                }else {
                    mCategoryAdapter.setmDataList(t.getData().getClass_list());
                    mProductAdapter.setmDataList(t.getData().getGoods_list());
                }
            }
        });
    }


    public void mProductData(int class_id) {
        mProductArray.clear();
        getData(class_id);
    }


    @OnClick(R.id.iv_back)
    public void onIvBackClicked() {
        finish();
    }

    @OnClick(R.id.rl_manage_category)
    public void onRlManageCategoryClicked() {
        startActivity(new Intent(ProductActivity.this,ManageProductCategoryActivity.class));
    }

    @OnClick(R.id.rl_add_product)
    public void onRlAddProductClicked() {
        startActivity(new Intent(ProductActivity.this,AddOrEditProductActivity.class));
    }
}
