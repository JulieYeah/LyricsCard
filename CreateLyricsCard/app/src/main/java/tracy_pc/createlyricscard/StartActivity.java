package tracy_pc.createlyricscard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
        RelativeLayout.LayoutParams par = (RelativeLayout.LayoutParams)img_Welcome.getLayoutParams();
        System.out.println(par.leftMargin);

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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(StartActivity.this).setTitle("Exit ？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“Yes”退出应用
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“Cancel”后的操作,这里不设置没有任何操作,继续编辑
                    }
                }).show();
    }
}
