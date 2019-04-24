package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

//import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
//import com.mylhyl.zxing.scanner.ScannerView;

public class ScanQrCodeActivity extends BaseActivity {

//    @BindView(R.id.scanner_view)
//    ScannerView mScannerView;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, ScanQrCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_scan_qr_code;
    }

    @Override
    public void init() {
//        mScannerView.toggleLight(false);//关
//
//        mScannerView.setOnScannerCompletionListener(new OnScannerCompletionListener() {
//            @Override
//            public void onScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
//                ParsedResultType type = parsedResult.getType();
//                switch (type) {
//                    case ADDRESSBOOK:
////                        AddressBookParsedResult addressBook = (AddressBookParsedResult) parsedResult;
////                        bundle.putSerializable(Intents.Scan.RESULT, new AddressBookResult(addressBook));
//                        break;
//                    case URI:
////                        URIParsedResult uriParsedResult = (URIParsedResult) parsedResult;
////                        Log.e(TAG, "onScannerCompletion: "+uriParsedResult.getURI().toString() );
//                        break;
//                    case TEXT:
////                        bundle.putString(Intents.Scan.RESULT, rawResult.getText());
//                        Log.e(TAG, "onScannerCompletion: "+rawResult.getText());
//                        RxBus.getDefault().post(new ScanEvent(rawResult.getText()));
//                        finish();
//                        break;
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mScannerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mScannerView.onPause();
    }


}
