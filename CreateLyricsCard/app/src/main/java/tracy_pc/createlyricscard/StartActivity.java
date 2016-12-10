package tracy_pc.createlyricscard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class StartActivity extends AppCompatActivity {

    private ImageView img_Welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //获取当前窗口长度，设置为imageView的宽度
        img_Welcome = (ImageView) findViewById(R.id.img_welcome);
        WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int screenHeight = screenWidth * 75 / 100;
        img_Welcome.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth,screenHeight));

        Button btn_start = (Button)findViewById(R.id.btn_start);
        Button btn_about_us = (Button)findViewById(R.id.btn_about_us);

        View.OnClickListener listener = new View.OnClickListener(){
            public void onClick(View view){
                switch (view.getId()){
                    case R.id.btn_start:
                        Intent intent = new Intent(getBaseContext(),LyricsSearchActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btn_about_us:
                        Intent intent2 = new Intent(getBaseContext(),AboutUsActivity.class);
                        startActivity(intent2);
                        break;
                }
            }
        };
        btn_start.setOnClickListener(listener);
        btn_about_us.setOnClickListener(listener);
    }
}
