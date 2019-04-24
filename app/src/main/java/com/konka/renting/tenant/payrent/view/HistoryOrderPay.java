package com.konka.renting.tenant.payrent.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;

/**
 * Created by ATI on 2018/4/1.
 */

public class HistoryOrderPay extends Activity implements View.OnClickListener {
    RecyclerView recyclerView;
    static String no;
    public static void toActivity(Context context,String ino){
        no=ino;
        Intent intent = new Intent(context,HistoryOrderPay.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_payrent_hisactivity);
        new HisPayProxy().init(this.getWindow().getDecorView(),no);
    }

    @Override
    public void onClick(View view) {
        this.finish();
    }
}
