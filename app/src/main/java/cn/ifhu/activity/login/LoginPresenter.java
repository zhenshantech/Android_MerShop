package cn.ifhu.activity.login;


import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;

import cn.ifhu.base.BaseObserver;
import cn.ifhu.bean.BaseEntity;
import cn.ifhu.bean.UserServiceBean;
import cn.ifhu.net.RetrofitAPIManager;
import cn.ifhu.net.SchedulerUtils;
import cn.ifhu.net.UserService;
import cn.ifhu.utils.IrReference;
import cn.ifhu.utils.StringUtils;
import cn.ifhu.utils.UserLogic;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * @author fuhongliang
 */
public class LoginPresenter implements LoginContract.Presenter {
    private final LoginContract.View loginView;

    @SuppressLint("RestrictedApi")
    public LoginPresenter(LoginContract.View loginView) {
        this.loginView = checkNotNull(loginView, "loginView cannot be null!");
        this.loginView.setPresenter(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void start() {
        loginView.initData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean checkPhoneNumber(String phoneNo, String passWord) {
        if (StringUtils.isEmpty(phoneNo)) {
            loginView.showToast("请输入手机号码！");
            return false;
        }
        if (StringUtils.isEmpty(passWord)) {
            loginView.showToast("请输入密码！");
            return false;
        }
        return true;
    }

    @Override
    public void loginWithCode(String phone, String code) {
        UserServiceBean.LoginForm loginForm = new UserServiceBean.LoginForm();
        loginForm.setMember_name(phone);
        loginForm.setMember_passwd(code);
        RetrofitAPIManager.create(UserService.class).login(loginForm)
                .compose(SchedulerUtils.ioMainScheduler()).subscribe(new BaseObserver<UserServiceBean.LoginResponse>(true) {

            @Override
            protected void onApiComplete() {
                loginView.setLoadingMessageIndicator(false);
            }

            @Override
            protected void onSuccees(BaseEntity<UserServiceBean.LoginResponse> t) throws Exception {
                loginView.showToast("登录成功！");
                UserLogic.saveUser(t.getData());
                loginView.loginSuccess();
            }

            @Override
            protected void onCodeError(BaseEntity<UserServiceBean.LoginResponse> t) throws Exception {
                super.onCodeError(t);
            }
        });
    }

}
