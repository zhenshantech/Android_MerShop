package cn.ifhu.mershop.net;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import cn.ifhu.mershop.utils.UserLogic;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author fuhongliang
 */
public class RetrofitAPIManager {

    private static final long TIMEOUT = 1000;
    private static Retrofit retrofit;
//    public static String baseDevURL = "http://47.92.244.60:88/v1/";
//    public static String baseDevURL = "http://47.111.27.189:2000/v1/";//开发环境
    public static String baseDevURL = " http://47.92.244.60/api/public/index.php/v1/";//正式环境
    private RetrofitAPIManager() {

    }

    public static Retrofit getClientApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseDevURL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(genericClient())
                    .build();
        }

        return retrofit;
    }


    public static <T> T create(final Class<T> service) {
        return getClientApi().create(service);
    }

    private static OkHttpClient genericClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(chain -> {
                    String token = "";
                    if (UserLogic.isLogin()) {
                        token = UserLogic.getUser().getToken();
                    }
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("token", token)
                            .build();
                    return chain.proceed(request);
                })
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Logger.d(message);
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BODY));

        OkHttpClient httpClient = builder.build();
        return httpClient;
    }
}
