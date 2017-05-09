package com.example.koki1.barcode_test;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class present extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // キーボード表示を制御するためのオブジェクト
    InputMethodManager inputMethodManager;
    // 背景のレイアウト
    private LinearLayout mainLayout;
    //public int btn_flag;
    //public String year="",month="",day="",result_date="";
    public String dasi,uke,tmp;
    public  String now_date;
    private GoogleApiClient mGoogleApiClient;
    public String txt_result,title_result;
    //public String folder_pass = "";
    //public String mother_pass = "0ByFJAPrSNSRKMm5KRHlPLWl0WFE",dab_pass = "0ByFJAPrSNSRKVHlEQ0hIa1phY0E",other_pass = "0ByFJAPrSNSRKQmJOR25fN0pKMEk";
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 3;
    public Bitmap image;
    public Uri imageUri;
//    public String[] test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present);
//        test[0] = "1";
//        test[2] = "2";
//        Spinner dasi_spin = (Spinner)findViewById(R.id.out_deck_spin);
//        setSpinner(dasi_spin,test);

        //ここからdriveの初期認証処理
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        Bundle bundle =getIntent().getExtras();
        //btn_flag = (int)bundle.get("btn_flag");
        dasi = (String)bundle.get("dasi");
        uke = (String)bundle.get("uke");
        imageUri = (Uri)bundle.get("URL");

        EditText OCRTextView = (EditText) findViewById(R.id.edit_out);
        OCRTextView.setText(dasi);

        EditText OCRTextView2 = (EditText) findViewById(R.id.edit_in);
        OCRTextView2.setText(uke);


    }


    // 画面タップ時の処理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
// キーボードを隠す
        inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
// 背景にフォーカスを移す
        mainLayout.requestFocus();

        return true;
    }

    //入力されているディスクNo.を交換
    public void exChange(View view){

        EditText edit_out = (EditText)findViewById(R.id.edit_out);
        SpannableStringBuilder sp_out = (SpannableStringBuilder)edit_out.getText();

        EditText edit_in = (EditText)findViewById(R.id.edit_in);
        SpannableStringBuilder sp_in = (SpannableStringBuilder)edit_in.getText();

        dasi = sp_out.toString();
        uke = sp_in.toString();

        tmp = dasi;
        dasi = uke;
        uke = tmp;

        edit_out.setText(dasi);
        edit_in.setText(uke);
    }

    /*

    public void callCalendar(View view){
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), getPackageName()+".calendar");
        startActivityForResult(intent,500);
    }

    public void clearDate(View view){
        TextView oa_date = (TextView) findViewById(R.id.oa_date);
        oa_date.setText("放送日を選択……");
        year = "";
        month = "";
        day = "";
    }

    */

    public void makeResult(){
        Calendar date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmmss");
        now_date = sdf.format(date.getTime());
        //Spinner title_spin = (Spinner)findViewById(R.id.title_spin);
        Spinner dasi_spin = (Spinner)findViewById(R.id.out_deck_spin);
        Spinner uke_spin = (Spinner)findViewById(R.id.in_deck_spin);
        //TextView oa_date = (TextView) findViewById(R.id.oa_date);

        /*
        if(year == "" || month == "" || day == ""){
            result_date = "";
        }else{
            result_date = year +"/" + month +"/" + day;
        }
        */

        /*
        if(title_spin.getSelectedItemPosition() == 0){
            title_result = "";
        }else{
            title_result = title_spin.getSelectedItem().toString();
        }
        */

        /*
        txt_result = title_result + "," + result_date + ","
                + dasi_spin.getSelectedItem() + "," + dasi +"," + uke_spin.getSelectedItem() + "," + uke;
        */
        txt_result = dasi_spin.getSelectedItem() + "," + dasi +"," + uke_spin.getSelectedItem() + "," + uke;

    }


    public void onSendButtonClick(View view) {
        System.out.println("ボタン押された！");
        makeResult();
        // Start by creating a new contents, and setting a callback.
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            System.out.println("The operation was not successful");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();


                        //画像版
                        try {
                            image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        //Bitmap test = BitmapFactory.decodeResource(getResources(), R.drawable.test);
                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 50, bitmapStream);
                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            System.out.println("failed");
                        }

                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle(now_date +".jpeg").setDescription(txt_result).build();


                        //ちょい退避、txt版
//                        try {
//                            outputStream.write(txt_result.getBytes());
//                        } catch (IOException e1) {
//                            System.out.println("Unable to write file contents.");
//                        }
//
//
//                        // Create the initial metadata - MIME type and title.
//                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
//                                .setMimeType("text/plain").setTitle(now_date +".csv").build();

                        //ここまで

                        // Create an intent for the file chooser, and start it.

                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                //.setActivityStartFolder(DriveId.zzdD(folder_pass))
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);
                        try {
                            startIntentSenderForResult(
                                    intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            System.out.println("Failed to launch file chooser.");
                        }
                    }
                });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        /*
        if(requestCode == 500 && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            year = String.valueOf((int)bundle.get("year"));
            month = String.valueOf((int)bundle.get("month")+1);
            day = String.valueOf((int)bundle.get("day"));


            TextView oa_date = (TextView)findViewById(R.id.oa_date);
            oa_date.setText(year+"/"+month+"/"+day);
        }
        */
        if(requestCode == REQUEST_CODE_CREATOR && resultCode == RESULT_OK){
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }


    private void setSpinner(Spinner spinner,String[] arr){
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

}
