package com.example.imageupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {
    RequestQueue requestQueue;
    Button upload,choose;
    ImageView imageView,imageView2;
    private  final int IMG_REQUEST=1;
    private Bitmap bitmap;

    String URL="http://10.0.2.2:5467/PROJECT2020/aayesha.asmx/ProfilePic";
    String img_path="http://10.0.2.2:5467/PROJECT2020/App_Themes/Theme1/assets/images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      requestQueue= Volley.newRequestQueue(getApplicationContext());
        imageView=findViewById(R.id.img_id);
        upload=findViewById(R.id.upload_id);
        choose=findViewById(R.id.choose_id);
        imageView2=findViewById(R.id.img_id2);
        choose.setOnClickListener(this);
        upload.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.choose_id:
                  selectImage();
                break;

            case R.id.upload_id:
                 uploadImage();
                break;
                default:

                    break;
        }
    }

    private void selectImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);

    }
    private  void uploadImage()
    { final String img=ImageToString(bitmap);
    final  TextView tv=findViewById(R.id.tv);

           Log.d("IMG",img);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String path= jsonObject.getString("key");
                          Toast.makeText(MainActivity.this, "Upload Image Succes", Toast.LENGTH_LONG).show();
                 //         imageView.setVisibility(imageView.GONE);

                      new DownloadImageTask(imageView2).execute(img_path+path);

                    tv.setText(path);
            }
            catch(Exception e) {
                Toast.makeText(MainActivity.this, "Upload Image Not excption"+e.getMessage(), Toast.LENGTH_LONG).show();

            }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Upload Image not error "+error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

              Map<String,String>  params=new HashMap<>();
              params.put("image_key",img);
              params.put("username_key","Anis");
              return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    private String ImageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgeByte=byteArrayOutputStream.toByteArray();

         Toast.makeText(MainActivity.this," String ="+imgeByte,Toast.LENGTH_LONG).show();

         return Base64.encodeToString(imgeByte,Base64.DEFAULT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null)
       {
           try {

                 Uri path = data.getData();
                 bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                 imageView.setImageBitmap(bitmap);
                 Toast.makeText(MainActivity.this,"Path ="+path+" Bitmap  ="+bitmap,Toast.LENGTH_LONG).show();

             }catch(IOException e)
             {
                 Toast.makeText(MainActivity.this,"Error "+e.getMessage(),Toast.LENGTH_LONG).show();
             }
       }
    }

}
