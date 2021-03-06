package cn.ifhu.mershop.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ifhu.mershop.R;
import cn.ifhu.mershop.base.BaseActivity;
import cn.ifhu.mershop.base.BaseObserver;
import cn.ifhu.mershop.bean.BaseEntity;
import cn.ifhu.mershop.bean.UserServiceBean;
import cn.ifhu.mershop.net.MeService;
import cn.ifhu.mershop.net.RetrofitAPIManager;
import cn.ifhu.mershop.net.SchedulerUtils;
import cn.ifhu.mershop.utils.StringUtils;
import cn.ifhu.mershop.utils.ToastHelper;
import cn.ifhu.mershop.utils.UserLogic;

/**
 * @author fuhongliang
 */
public class ChangePassWordActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.et_password_again)
    EditText etPasswordAgain;
    @BindView(R.id.btn_ok)
    Button btnOk;
    private Timer mTimer;
    private int mOriSecond = 5;
    private int mCurSecond;
    private int mCodeLength = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        tvTitle.setText("修改密码");
        tvPhone.setText(UserLogic.getUser().getMember_mobile());
    }

    @OnClick(R.id.iv_back)
    public void onIvBackClicked() {
        finish();
    }

    @OnClick(R.id.tv_get_code)
    public void onTvGetCodeClicked() {
        setLoadingMessageIndicator(true);
        UserServiceBean.LoginResponse loginResponse = UserLogic.getUser();
        RetrofitAPIManager.create(MeService.class).getSms(loginResponse.getMember_mobile()+"")
                .compose(SchedulerUtils.ioMainScheduler()).subscribe(new BaseObserver<Object>(true) {
            @Override
            protected void onApiComplete() {
                setLoadingMessageIndicator(false);
            }

            @Override
            protected void onSuccees(BaseEntity t) throws Exception {
                ToastHelper.makeText(t.getMessage() + "", Toast.LENGTH_SHORT, ToastHelper.NORMALTOAST).show();
                showCountDown();
            }
        });
    }

    private void showCountDown() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mCurSecond--;
                if (mCurSecond < 1) {
                    this.cancel();
                    runOnUiThread(() -> {
                        tvGetCode.setText("获取验证码");
                    });
                    mCurSecond = mOriSecond;
                } else {
                    String sencond = mCurSecond + "重新获取";
                    runOnUiThread(() -> {
                        tvGetCode.setText(sencond);
                    });
                }
            }
        }, 0, 1000);
    }

    @OnClick(R.id.btn_ok)
    public void onBtnOkClicked() {
        if (checkPassWord()) {
            setLoadingMessageIndicator(true);
            UserServiceBean.LoginResponse loginResponse = UserLogic.getUser();
            RetrofitAPIManager.create(MeService.class).editPasswd(loginResponse.getMember_id()+"", loginResponse.getMember_mobile(), code, newPassWord, newPassWordAgain)
                    .compose(SchedulerUtils.ioMainScheduler()).subscribe(new BaseObserver<Object>(true) {
                @Override
                protected void onApiComplete() {
                    setLoadingMessageIndicator(false);
                }

                @Override
                protected void onSuccees(BaseEntity t) throws Exception {
                    ToastHelper.makeText(t.getMessage() + "", Toast.LENGTH_SHORT, ToastHelper.NORMALTOAST).show();
                }
            });
        }
    }

    String newPassWord = "";
    String newPassWordAgain = "";
    String code = "";

    public boolean checkPassWord() {
        newPassWord = etNewPassword.getText().toString().trim();
        newPassWordAgain = etPasswordAgain.getText().toString().trim();
        code = etCode.getText().toString().trim();

        if (StringUtils.isEmpty(code)) {
            return false;
        }

        if (StringUtils.isEmpty(newPassWord) || newPassWord.length() < mCodeLength) {
            return false;
        }

        if (!newPassWord.equals(newPassWordAgain)) {
            return false;
        }
        return true;
    }

}
