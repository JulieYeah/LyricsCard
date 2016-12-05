package tracy_pc.createlyricscard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button btn_start = (Button)findViewById(R.id.btn_start);
        Button btn_history = (Button)findViewById(R.id.btn_history);

        View.OnClickListener listener = new View.OnClickListener(){
            public void onClick(View view){
                switch (view.getId()){
                    case R.id.btn_start:
                        Intent intent = new Intent(getBaseContext(),LyricsSearchActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btn_history:
                        Intent intent2 = new Intent(getBaseContext(),HistoryImageActivity.class);
                        startActivity(intent2);
                        break;
                }
            }
        };
        btn_start.setOnClickListener(listener);
        btn_history.setOnClickListener(listener);
    }
}
