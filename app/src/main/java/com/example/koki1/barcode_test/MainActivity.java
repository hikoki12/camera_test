package com.example.koki1.barcode_test;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;


public class MainActivity extends AppCompatActivity{
    //public int btn_flag;//0ならマザー、1ならダビング
    public String rtn_cnt;
    public String dasi_num = "dasi", uke_num = "uke";
    public int REQUEST_CODE_CAPTURE_IMAGE = 1;
    public Uri imageUri;
    public  Bitmap image2;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main); //最初の画面表示
        rtn_cnt = "dasi";



        //System.out.println("rootディレクトリは次：" + Environment.getExternalStorageDirectory());
        onCameraImageClick();

    }


/*
    protected void onStart(){
        super.onStart();
    }
*/



    public void onCameraImageClick() {

//        if (view.getId() == R.id.btn_dab) {
//            //btn_flag = 1;
//            dasi_num = "";
//            uke_num = "";
//            Intent intent = new Intent();
//            intent.setClassName(getPackageName(), getPackageName() + ".present");
//            //intent.putExtra("btn_flag", btn_flag);
//            intent.putExtra("dasi", dasi_num);
//            intent.putExtra("uke", uke_num);
//            startActivity(intent);
//        } else {
//            //btn_flag = 0;



            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.setPrompt("出しNo.を読み込んでください");
            integrator.setResultDisplayDuration(0);
            integrator.setWide();
            integrator.setCameraId(0);
            integrator.initiateScan();
  //      }


    }

    //バーコードスキャン後、送信画面に移動する
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        System.out.println(result + "帰ってきた" + rtn_cnt);
         if(result != null && result.getContents() != null) {
        if (rtn_cnt == "dasi") {
            rtn_cnt = "uke";
            dasi_num = result.getContents();
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.setPrompt("受けNo.を読み込んでください");
            integrator.setResultDisplayDuration(0);
            integrator.setWide();
            integrator.setCameraId(0);
            integrator.initiateScan();
        } else if (rtn_cnt == "uke") {
                uke_num = result.getContents();

                //ストレージへのアクセス許可
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(this, permissions, 2000);
                    return;
                }

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyMMdd_HHmmss");
                Date now = new Date(System.currentTimeMillis());  // （1）
                String nowStr = sdf.format(now);  // （1）
                String fileName = "UseCameraActivityPhoto_" + nowStr + ".jpg";  // （1）

                ContentValues values = new ContentValues();  // （2）
                values.put(MediaStore.Images.Media.TITLE, fileName);  // （3）
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");  // （4）

                ContentResolver resolver = getContentResolver();  // （5）
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  // （6）

                //ここからカメラ起動
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  // （7）
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);  // （8）
                //System.out.println("imageUri:"+ _imageUri);
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);  // （9）
        }

        } else if(result != null && result.getContents() == null) {
             if(rtn_cnt == "dasi"){
                 rtn_cnt = "uke";
                 dasi_num = "";
                 IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                 integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                 //integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                 integrator.setPrompt("受けNo.を読み込んでください");
                 integrator.setResultDisplayDuration(0);
                 integrator.setWide();
                 integrator.setCameraId(0);
                 integrator.initiateScan();

             }else if(rtn_cnt == "uke"){
                 uke_num = "";


                 //ストレージへのアクセス許可
                 if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                     String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                     ActivityCompat.requestPermissions(this, permissions, 2000);
                     return;
                 }

                 java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyMMdd_HHmmss");
                 Date now = new Date(System.currentTimeMillis());  // （1）
                 String nowStr = sdf.format(now);  // （1）
                 String fileName = "UseCameraActivityPhoto_" + nowStr + ".jpg";  // （1）

                 ContentValues values = new ContentValues();  // （2）
                 values.put(MediaStore.Images.Media.TITLE, fileName);  // （3）
                 values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");  // （4）

                 ContentResolver resolver = getContentResolver();  // （5）
                 imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  // （6）

                 //ここからカメラ起動
                 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  // （7）
                 intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);  // （8）
                 //System.out.println("imageUri:"+ _imageUri);
                 startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);  // （9）
             }

         }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

        if ((requestCode == REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK)) {

            System.out.println("遷移処理入った");

            //画像からバーコード取得のテスト
            /*
                //image2 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                image2 = BitmapFactory.decodeResource(getResources(), R.drawable.test4);
                int[] intArray = new int[image2.getWidth()*image2.getHeight()];
                LuminanceSource source = new RGBLuminanceSource(image2.getWidth(), image2.getHeight(), intArray);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Reader reader = new MultiFormatReader();
                GenericMultipleBarcodeReader test = new GenericMultipleBarcodeReader(reader);
                //Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
                //decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                System.out.println("ここまでは来てる");
                //Result[] rlt = test.decodeMultiple(bitmap,decodeHints);
            try {
                Result[] rlt = test.decodeMultiple(bitmap);
                System.out.println("結果は次の通り:"+rlt.toString());
 //           }catch (IOException e) {
   //             e.printStackTrace();
                }catch (NotFoundException e) {
                    e.printStackTrace();
                }
            */

            Intent intent = new Intent();
            intent.setClassName(getPackageName(), getPackageName() + ".present");
            //intent.putExtra("btn_flag", btn_flag);
            intent.putExtra("dasi", dasi_num);
            intent.putExtra("uke", uke_num);
            intent.putExtra("URL", imageUri);
            startActivity(intent);
        }

    }


    protected void onResume() {
        super.onResume();
    }

}

