package project.capstone.com.cabstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BusinessRegist extends AppCompatActivity {
    Button cancelBtn,
            registBtn;
    EditText businessIDText,
            businessPWText,
            businessNameText,
            businessPartText,
            businessMailAddrText;
    boolean isChangeActivity = false;
    String url = "http://192.168.10.106:3000/post";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_regist);

        cancelBtn=(Button)findViewById(R.id.business_regist_Cancel);
        registBtn=(Button)findViewById(R.id.business_regist_OK);

        businessIDText = (EditText)findViewById(R.id.business_regist_ID);
        businessPWText = (EditText)findViewById(R.id.business_regist_PW);
        businessNameText = (EditText)findViewById(R.id.business_regist_Name);
        businessPartText = (EditText)findViewById(R.id.business_regist_Part);
        businessMailAddrText = (EditText)findViewById(R.id.business_regist_Email);

        //확인버튼
        registBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("MyLog", "Regist");
                String id = businessIDText.getText().toString();
                String pw = businessPWText.getText().toString();
                String name = businessNameText.getText().toString();
                String part = businessPartText.getText().toString();
                String mailAddr = businessMailAddrText.getText().toString();

                Log.v("MyLog", "RegistJson");
                String json = "{" + "\"type\": \"registBusiness\"" + ", "+"\"userID\":" + id + ", "+ "\"userPW\":" + pw + ", "
                        + "\"userName\":" + name + ", " + "\"userPart\":" + part + ", " + "\"userMailAddr\":" + mailAddr + "}";

                new BusinessRegist.JSONTask().execute(url, json);
                Log.v("MyLog", id);
                Log.v("MyLog", pw);

            }
        });

        //취소버튼 로그인 화면으로 이동
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        //excute로 호출되면 doInBackground 메소드를 실행함.
        @Override
        protected String doInBackground(String... args) {
            try {
                //JSON Object를 만들고 key, value 형식으로 값을 저장
                JSONObject jsonObject = new JSONObject(args[1]);
                Log.v("MyLog", jsonObject.toString());

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    Log.v("MyLog", "Post Start");
                    URL url = new URL(args[0]);

                    Log.v("MyLog", args[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application.json");
                    con.setRequestProperty("Accept", "test/html");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    Log.v("MyLog", "Connect");
                    con.connect();
                    Log.v("MyLog", "Post Connect");

                    Log.v("MyLog", "OutputStream");
                    //서버로 보내기 위한 OutputStream
                    OutputStream outStream = con.getOutputStream();
                    //버퍼 생성 후 데이터를 넣음
                    Log.v("MyLog", "BufferedWriter");
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    Log.v("MyLog", "server");
                    //서버로부터 데이터를 받음
                    InputStream inputStream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    Log.v("MyLog", "Init StringBuffer");
                    StringBuffer buffer = new StringBuffer();

                    Log.v("MyLog", "Line Attach");
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String result = buffer.toString();
                    Log.v("MyLog",result);
                    if(result.compareTo("sucsess") == 0)
                    {
                        isChangeActivity = true;
                    }

                    Log.v("MyLog", "Return result");
                    return result;
                } catch (MalformedURLException e) {
                    Log.v("MyLog", "Error");
                    e.printStackTrace();
                } catch (IOException e){
                    Log.v("MyLog", "Error : " + con.getErrorStream());
                    Log.v("MyLog", "Error : " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if(con != null)
                    {
                        con.disconnect();
                    }
                    try{
                        if(reader != null)
                        {
                            reader.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v("MyLog", "End");

            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            Log.v("MyLog", "Compare");
            if(res.compareTo("success") == 0)
            {

                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        }
    }

}
