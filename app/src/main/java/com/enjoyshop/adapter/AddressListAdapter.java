package com.enjoyshop.adapter;

import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.enjoyshop.R;
import com.wang.bean.Address;

import java.util.List;

/**
 * Describe: 收货地址 适配器
 */

public class AddressListAdapter extends BaseQuickAdapter<Address, BaseViewHolder> {

    public AddressListAdapter(List<Address> datas) {
        super(R.layout.template_address, datas);
    }

    @Override
    protected void convert(BaseViewHolder holder, Address item) {
        holder.setText(R.id.txt_name, item.getAddress_name())
                .setText(R.id.txt_phone, item.getAddress_tel())
                .setText(R.id.txt_address, item.getAddress_user())
                .addOnClickListener(R.id.address)
                .addOnClickListener(R.id.addresss)
                .addOnClickListener(R.id.cb_is_defualt)
                .addOnClickListener(R.id.txt_edit)
                .addOnClickListener(R.id.txt_del);

        ((CheckBox)holder.getView(R.id.cb_is_defualt)).setChecked(item.getAddress_status()==1);

    }
}


