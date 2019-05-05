package cn.ifhu.mershop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ifhu.mershop.R;
import cn.ifhu.mershop.bean.JSBean;
import cn.ifhu.mershop.bean.WithDrawBean;
import cn.ifhu.mershop.utils.DateUtil;

/**
 * @author fuhongliang
 */
public class SettledAdapter extends BaseAdapter {
    public List<JSBean.ListBean> mDataList;
    public Context mContext;
    public int mCurPosition = 0;

    public SettledAdapter(List<JSBean.ListBean> mDataList, Context mContext) {
        this.mDataList = mDataList;
        this.mContext = mContext;
    }

    public void setmDataList(List<JSBean.ListBean> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_settled, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvMonth.setText(mDataList.get(position).getOs_month() + "月份");
        viewHolder.tvCardNo.setText("结算尾号：" + mDataList.get(position).getOb_no());
        viewHolder.tvMoney.setText("￥" + mDataList.get(position).getAmount());
        viewHolder.tvState.setText(getState(mDataList.get(position).getState()));
        return convertView;
    }

    public String getState(String state) {
        String stateString;
        switch (state) {
            case "1":
                stateString = "已出账";
                break;
            case "2":
                stateString = "已确认";
            break;
            case "3":
                stateString = "已审核";
            break;
            case "4":
                stateString = "已完成";
            break;
            default:
                stateString = "已出账";
            break;
        }
        return stateString;
    }

    static class ViewHolder {

        @BindView(R.id.tv_month)
        TextView tvMonth;
        @BindView(R.id.tv_money)
        TextView tvMoney;
        @BindView(R.id.tv_card_no)
        TextView tvCardNo;
        @BindView(R.id.tv_state)
        TextView tvState;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
