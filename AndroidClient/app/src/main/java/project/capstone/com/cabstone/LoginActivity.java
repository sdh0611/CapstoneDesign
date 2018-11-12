package project.capstone.com.cabstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//Import for Communicate with Node.js server
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


public class LoginActivity extends AppCompatActivity {
    TextView personRegist, businessRegist;
    EditText loginIDText, loginPWText;
    Button loginBtn;
    boolean isChangeActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        personRegist=(TextView) findViewById(R.id.personRegist);
        businessRegist=(TextView)findViewById(R.id.businessRegist);

        loginIDText = (EditText)findViewById(R.id.loginID);
        loginPWText = (EditText)findViewById(R.id.loginPW);

        loginBtn=(Button)findViewById(R.id.loginBtn);

        //로그인 콜백
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = loginIDText.getText().toString();
                String pw = loginPWText.getText().toString();

                new JSONTask().execute("http://192.168.10.106:3000/post", id, pw);
                Log.v("MyLog : ", id);
                Log.v("MyLog : ", pw);
            }
        });

        //개인 회원가입 콜백
        personRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), PersonRegist.class);
                startActivity(intent);
            }
        });

        //기업 회원가입 콜백
        businessRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), BusinessRegist.class);
                startActivity(intent);
            }
        });


    }

    public class JSONTask extends AsyncTask<String, String, String>{

        //excute로 호출되면 doInBackground 메소드를 실행함.
        @Override
        protected String doInBackground(String... args) {
            try {
                //JSON Object를 만들고 key, value 형식으로 값을 저장
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("userID", args[1]);
                jsonObject.accumulate("userPW", args[2]);
                Log.v("Server", jsonObject.toString());

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(args[0]);
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application.json");
                    con.setRequestProperty("Accept", "test/html");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.connect();

                    //서버로 보내기 위한 OutputStream
                    OutputStream outStream = con.getOutputStream();
                    //버퍼 생성 후 데이터를 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    Log.v("Server", "server");
                    //서버로부터 데이터를 받음
                    InputStream inputStream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    Log.v("Server", "Init StringBuffer");
                    StringBuffer buffer = new StringBuffer();

                    Log.v("Server : ", "Line Attach");
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String result = buffer.toString();
                    Log.v("Server : ",result);
                    if(result.compareTo("sucsess") == 0)
                    {
                        isChangeActivity = true;
                    }
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e){
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

            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            Log.v("Server", "Compare");
            if(res.compareTo("success") == 0)
            {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        }
    }


}
