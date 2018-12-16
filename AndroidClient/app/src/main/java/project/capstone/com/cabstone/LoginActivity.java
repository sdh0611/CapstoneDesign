package project.capstone.com.cabstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView personRegist,
            businessRegist;
    EditText loginIDText,
            loginPWText;
    Button loginBtn;
    RadioButton loginPersonal,
                loginBusiness;
    RadioGroup loginTypeGroup;
    String loginType = "";
    String url  = "http://192.168.10.106:3000/post";
    Toast toast;
    boolean isChangeActivity = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        toast.setGravity(Gravity.TOP, 0, 10);

        personRegist=(TextView) findViewById(R.id.personRegist);
        businessRegist=(TextView)findViewById(R.id.businessRegist);

        loginIDText = (EditText)findViewById(R.id.loginID);
        loginPWText = (EditText)findViewById(R.id.loginPW);

        loginPersonal = (RadioButton)findViewById(R.id.loginPersonal);
        loginBusiness = (RadioButton)findViewById(R.id.loginBusiness);
        loginTypeGroup = (RadioGroup)findViewById(R.id.loginTypeRadioGroup);
        //라디오 그룹 내 Default설정 라디오 버튼을 개인 회원 라디오 버튼으로 설정
//        loginTypeGroup.check(R.id.loginPersonal);
        //라디오 그룹 리스너 등록
        loginTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.v("MyLog", "RadioGroup");
                switch(checkedId)
                {
                    case R.id.loginPersonal:
                        Log.v("MyLog", "Personal");
                        loginType = "personal";
                        break;
                    case R.id.loginBusiness:
                        Log.v("MyLog", "Business");
                        loginType = "business";
                        break;
                }
            }
        });

        loginBtn=(Button)findViewById(R.id.loginBtn);
        //로그인 콜백
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginType.isEmpty()) {
                    toast.makeText(LoginActivity.this, "회원 유형을 선택해주십시오.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String id = loginIDText.getText().toString();
                String pw = loginPWText.getText().toString();
                if(id.isEmpty() || pw.isEmpty())
                {
                    toast.makeText(LoginActivity.this, "ID와 PW를 입력하십시오.", Toast.LENGTH_SHORT).show();
                    return;
                }


                new JSONTask().execute(url, loginType, id, pw);
                Log.v("MyLog", id);
                Log.v("MyLog", pw);
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

    public class JSONTask extends AsyncTask<String, String, String> {

        //excute로 호출되면 doInBackground 메소드를 실행함.
        @Override
        protected String doInBackground(String... args) {
            try {
                //JSON Object를 만들고 key, value 형식으로 값을 저장
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("type", "identify");
                jsonObject.accumulate("loginType", args[1]);
                jsonObject.accumulate("userID", args[2]);
                jsonObject.accumulate("userPW", args[3]);
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
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
            else
            {
                toast.makeText(LoginActivity.this, "ID 혹은 PW를 다시 확인하여 주십시오.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}