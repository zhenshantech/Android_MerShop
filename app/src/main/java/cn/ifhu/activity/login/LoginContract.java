package cn.ifhu.activity.login;


import cn.ifhu.base.BasePresenter;
import cn.ifhu.base.BaseView;

/**
 * @author fuhongliang
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {
        /**
         * 页面显示后初始化操作入口
         */
        void initData();

        /**
         * 显示最后一次登录的手机号码
         */
        void showLastTimePhoneNumber();

        /**
         * 提示信息
         * @param msg 内容
         */
        void showToast(String msg);

    }

    interface Presenter extends BasePresenter {
        boolean checkPhoneNumber(String phoneNo);
        void loginWithCode(String phone, String code);
    }
}
