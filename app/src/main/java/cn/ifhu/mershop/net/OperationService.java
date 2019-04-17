package cn.ifhu.mershop.net;

import java.util.List;

import cn.ifhu.mershop.bean.AddGoodsBean;
import cn.ifhu.mershop.bean.BaseEntity;
import cn.ifhu.mershop.bean.DiscountBean;
import cn.ifhu.mershop.bean.EditGoodsBean;
import cn.ifhu.mershop.bean.OperationBean;
import cn.ifhu.mershop.bean.ProductManageBean;
import cn.ifhu.mershop.bean.ReviewBean;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * @author fuhongliang
 */
public interface OperationService {

    @FormUrlEncoded
    @POST("goods_list")
    public Observable<BaseEntity<ProductManageBean>> goodsList(@Field("store_id") int store_id, @Field("class_id") int class_id);


    @FormUrlEncoded
    @POST("add_goods_class")
    public Observable<BaseEntity<List<ProductManageBean.ClassListBean>>> addGoodsClass(@Field("store_id") int store_id, @Field("class_name") String class_name);

    @FormUrlEncoded
    @POST("add_goods_class")
    public Observable<BaseEntity<List<ProductManageBean.ClassListBean>>> updateGoodsClass(@Field("store_id") int store_id, @Field("class_id") int class_id, @Field("class_name") String class_name);

    @POST("add_goods")
    public Observable<BaseEntity<Object>> addGoods(@Body AddGoodsBean addGoodsBean);

    @POST("edit_goods")
    public Observable<BaseEntity<Object>> editGoods(@Body EditGoodsBean editGoodsBean);

    @FormUrlEncoded
    @POST("sort_goods_class")
    public Observable<BaseEntity<List<ProductManageBean.ClassListBean>>> sortGoodsClass(@Field("class_ids") String sortCategoryBean, @Field("store_id") int store_id);

    @FormUrlEncoded
    @POST("get_store_com")
    public Observable<BaseEntity<ReviewBean>> getStoreReviews(@Field("store_id") int store_id);


    @FormUrlEncoded
    @POST("get_store_com")
    public Observable<BaseEntity<ReviewBean>> getNoReplyReviews(@Field("store_id") int store_id, @Field("no_com") int no_com);


    @FormUrlEncoded
    @POST("store_feedback")
    public Observable<BaseEntity<Object>> storeFeedback(@Field("store_id") int store_id, @Field("content") String content, @Field("parent_id") int parent_id);

    @FormUrlEncoded
    @POST("store_yunying")
    public Observable<BaseEntity<OperationBean>> storeYunying(@Field("store_id") int store_id);


    @FormUrlEncoded
    @POST("del_goods_class")
    public Observable<BaseEntity<List<Object>>> delGoodsClass(@Field("class_id") int class_id, @Field("store_id") int store_id);

    @FormUrlEncoded
    @POST("chgoods_state")
    public Observable<BaseEntity<Object>> chGoodsState(@Field("goods_id") int goods_id, @Field("store_id") int store_id);

    @FormUrlEncoded
    @POST("del_goods")
    public Observable<BaseEntity<Object>> delGoods(@Field("goods_id") int goods_id, @Field("store_id") int store_id);

    @FormUrlEncoded
    @POST("xianshi_list")
    public Observable<BaseEntity<List<DiscountBean>>> getDiscountList(@Field("store_id") int store_id);


}
