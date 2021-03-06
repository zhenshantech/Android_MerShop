package cn.ifhu.mershop.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ifhu.mershop.R;
import cn.ifhu.mershop.base.BaseActivity;
import cn.ifhu.mershop.base.BaseObserver;
import cn.ifhu.mershop.bean.BaseEntity;
import cn.ifhu.mershop.net.MeService;
import cn.ifhu.mershop.net.RetrofitAPIManager;
import cn.ifhu.mershop.net.SchedulerUtils;
import cn.ifhu.mershop.utils.StringUtils;
import cn.ifhu.mershop.utils.ToastHelper;
import cn.ifhu.mershop.utils.UserLogic;

/**
 * @author fuhongliang
 */
public class FeedBackActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_feedback)
    EditText etFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        tvTitle.setText("意见反馈");
    }

    public void feedBack() {
        setLoadingMessageIndicator(true);
        RetrofitAPIManager.create(MeService.class).storeMsgFeedback(UserLogic.getUser().getStore_id(),etFeedback.getText().toString().trim(),1)
                .compose(SchedulerUtils.ioMainScheduler()).subscribe(new BaseObserver<Object>(true) {

            @Override
            protected void onApiComplete() {
                setLoadingMessageIndicator(false);
            }

            @Override
            protected void onSuccees(BaseEntity<Object> t) throws Exception {
                ToastHelper.makeText(t.getMessage()+"", Toast.LENGTH_SHORT,ToastHelper.NORMALTOAST).show();
                clearEdit();
            }
        });
    }

    public void clearEdit(){
        etFeedback.setText("");
    }

    @OnClick(R.id.iv_back)
    public void onIvBackClicked() {
        finish();
    }

    @OnClick(R.id.btn_feedback)
    public void onBtnFeedbackClicked() {
        if (checkContent()){
            feedBack();
        }
    }

    public boolean checkContent(){
        if(StringUtils.isEmpty(etFeedback.getText().toString().trim())){
            return false;
        }
        return true;
    }
}
