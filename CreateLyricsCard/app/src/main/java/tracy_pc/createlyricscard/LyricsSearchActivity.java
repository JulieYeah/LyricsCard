package tracy_pc.createlyricscard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LyricsSearchActivity extends AppCompatActivity {
    private String get_original_url = "http://tingapi.ting.baidu.com/v1/restserver/ting?format=xml&calback=&from=webapp_music&method=baidu.ting.search.catalogSug&query=";
    private String get_id_url = "";
    EditText inputSearch;
    ListView lyric_list;
    Button btn_searchLyric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics_search);

        btn_searchLyric = (Button) findViewById(R.id.btn_searchLyric);
        inputSearch = (EditText) findViewById(R.id.search_input);
        lyric_list = (ListView) findViewById(R.id.search_list);
        final ImageView deletInput = (ImageView) findViewById(R.id.delete_input);
        btn_searchLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), DisplayLyricsActivity.class);
                intent.putExtra("search_by_name", inputSearch.getText());
                startActivity(intent);
            }
        });

        //onclicklistener to choose the song
        lyric_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getBaseContext(), DisplayLyricsActivity.class);
                TextView final_id = (TextView) view.findViewById(R.id.songid);
                TextView final_song = (TextView)view.findViewById(R.id.songs);
                intent.putExtra("songid", final_id.getText());
                intent.putExtra("songs",final_song.getText());
                System.out.println("The songid is "+final_id.getText());
                startActivity(intent);
            }
        });
        deletInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSearch.setText("");
            }
        });
        //the filter when inputting for search
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    deletInput.setVisibility(View.GONE);
                    lyric_list.setVisibility(View.INVISIBLE);
                } else if (s.length() > 2) {
                    get_id_url = get_original_url + s;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL new_url = new URL(get_id_url);
                                urlConnection(new_url);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    deletInput.setVisibility(View.VISIBLE);
                    lyric_list.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void urlConnection(URL url) {
        try {
            lyric_list.setVisibility(View.VISIBLE);
            HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
            if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                //Input Stream Reader
                BufferedReader in = new BufferedReader(new InputStreamReader(httpconn.getInputStream(), "UTF-8"));
                String line = in.readLine();

                JSONObject jsonObject = new JSONObject(line);
                JSONArray songArray = jsonObject.getJSONArray("song");
                int n = songArray.length();
                ArrayList<Map<String, Object>> list = new ArrayList<>();

                for (int i = 0; i < n; i++) {
                    JSONObject lyric = songArray.getJSONObject(i);
                    Map<String, Object> map = new HashMap<>();
                    map.put("songid", lyric.getInt("songid"));
                    map.put("Songs", lyric.getString("songname") + " --- " + lyric.getString("artistname"));
                    list.add(map);
                }
                Message msg = new Message();
                msg.what = 0;
                msg.obj = list;
                handler.sendMessage(msg);

                System.out.println(list);
               /* Looper.prepare();
                Toast.makeText(getApplicationContext(), "Connection Success!", Toast.LENGTH_SHORT).show();
                Looper.loop();*/
                in.close();
            }
            httpconn.disconnect();

        } catch (Exception e) {

            Message msg = new Message();
            msg.what = -1;
            handler.sendMessage(msg);
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "Fail to Connect", Toast.LENGTH_SHORT).show();
            Looper.loop();
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            ImageView not_found = (ImageView)findViewById(R.id.not_found);
            switch (msg.what) {
                case 0:
                    lyric_list.setVisibility(View.VISIBLE);
                    not_found.setVisibility(View.GONE);
                    SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), (ArrayList<Map<String, Object>>) msg.obj, R.layout.lyric_list, new String[]{"songid", "Songs"}, new int[]{R.id.songid, R.id.songs});
                    lyric_list.setAdapter(adapter);
                    break;
                case -1:
                    lyric_list.setVisibility(View.GONE);
                    not_found.setVisibility(View.VISIBLE);
            }
        }
    };
}
