package project.capstone.com.cabstone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyPage extends AppCompatActivity {

    Button add_Resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        add_Resume=(Button)findViewById(R.id.add_resume);

        add_Resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), WriteResume.class);
                startActivity(intent);
            }
        });
    }
}
