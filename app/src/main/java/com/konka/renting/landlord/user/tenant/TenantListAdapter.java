package com.konka.renting.landlord.user.tenant;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.TenantListBean;
import com.konka.renting.utils.PhoneUtil;

import java.util.List;

public class TenantListAdapter extends RecyclerView.Adapter<TenantListAdapter.VH> {
    List<TenantListBean> listBeans;
    Context mContext;
    OnItemClickListent mClickListent;

    public TenantListAdapter(List<TenantListBean> listBeans, Context context) {
        this.listBeans = listBeans;
        this.mContext = context;
    }

    public void setmClickListent(OnItemClickListent mClickListent) {
        this.mClickListent = mClickListent;
    }

    @NonNull
    @Override
    public TenantListAdapter.VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.adapter_house_tenanter_list, null);
        VH vh = new VH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final TenantListAdapter.VH vh, final int i) {
        TextView address = vh.itemView.findViewById(R.id.adapter_tv_address);
        RecyclerView recyclerView = vh.itemView.findViewById(R.id.adapter_recyclerView);
        address.setText(listBeans.get(i).getRoom_name());
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(new RecyclerView.Adapter<VH>() {
            @NonNull
            @Override
            public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = View.inflate(mContext, R.layout.adapter_house_tenant_list_list, null);
                VH vh = new VH(view);
                return vh;
            }

            @Override
            public void onBindViewHolder(@NonNull VH vh, final int position) {
                    final TextView name=vh.itemView.findViewById(R.id.adapter_tv_name);
                    TextView number=vh.itemView.findViewById(R.id.adapter_tv_number);
                    TextView call=vh.itemView.findViewById(R.id.adapter_tv_call);
                    name.setText( listBeans.get(i).getRenter().get(position).getReal_name());
                number.setText( "("+ PhoneUtil.hindPhone(listBeans.get(i).getRenter().get(position).getPhone())+")");
                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mClickListent!=null)
                                mClickListent.clickCall( listBeans.get(i).getRenter().get(position).getPhone());
                        }
                    });
            }

            @Override
            public int getItemCount() {
                return listBeans.get(i).getRenter() == null ? 0 : listBeans.get(i).getRenter().size();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listBeans == null ? 0 : listBeans.size();
    }

    class VH extends RecyclerView.ViewHolder {

        public VH(@NonNull View itemView) {
            super(itemView);

        }
    }

    public interface OnItemClickListent{
        void clickCall(String phone);
    }
}
