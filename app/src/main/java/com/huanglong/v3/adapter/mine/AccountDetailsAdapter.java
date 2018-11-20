package com.huanglong.v3.adapter.mine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.huanglong.v3.R;
import com.huanglong.v3.model.mine.AccountDetailsBean;

import java.util.List;

/**
 * Created by bin on 2018/1/31.
 * 账户明细的adapter
 */

public class AccountDetailsAdapter extends RecyclerView.Adapter<AccountDetailsAdapter.ViewHolder> {


    private List<AccountDetailsBean> accountDetailsBeans;

    public void setData(List<AccountDetailsBean> accountDetailsBeans) {
        this.accountDetailsBeans = accountDetailsBeans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(accountDetailsBeans.get(position));
    }

    @Override
    public int getItemCount() {
        if (accountDetailsBeans != null) {
            return accountDetailsBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title, tv_money, tv_date;


        public ViewHolder(View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.item_account_details_title);
            tv_money = itemView.findViewById(R.id.item_account_details_money);
            tv_date = itemView.findViewById(R.id.item_account_details_date);


        }

        public void setData(AccountDetailsBean accountDetailsBean) {

            tv_title.setText(accountDetailsBean.getTitle());
            tv_date.setText(accountDetailsBean.getTimestamp());

            String amount = accountDetailsBean.getAmount();
            int type = accountDetailsBean.getType();
            if (type == 1) {
                tv_money.setText("+" + amount);
            } else {
                tv_money.setText("-" + amount);
            }

        }
    }
}
