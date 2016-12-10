package tracy_pc.createlyricscard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DisplayLyricsActivity extends AppCompatActivity implements Adapter.OnShowItemClickListener {
    private ListView listView;
    private List<ItemBean> dataList;
    private List<ItemBean> selectList;
    private Adapter adapter;
    static Toolbar toolbar;
    // 是否显示CheckBox标识
    private static boolean isShow;
    //记录选择的歌词行数
    private int countRow = 0;
    //选中的歌词,歌名+歌手
    private String choosen_Lyrics;
    private String choosen_songInfo = "";
    //下方的linearLayout
    private LinearLayout bottom_Menu;

    private String[] str_Lyrics;
    private ArrayAdapter<String> lyrics_adapter;

    private String songInfo;
    private String songName;
    private String singer;

    private String songid = "";
    private String get_lrc_url = "http://tingapi.ting.baidu.com/v1/restserver/ting?format=xml&calback=&from=webapp_music&method=baidu.ting.song.lry&songid=";
    ProgressBar progressBar = null;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_lyrics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get the song name data from the onItemClick function from Lyric_SearchActivity
        Intent intent = getIntent();
        songid = intent.getStringExtra("songid");
        songInfo = intent.getStringExtra("songs");
        //截取歌名和歌手，歌名-0，歌手-1
        String[] songInfoSplit = songInfo.split(" --- ");
        if(songInfoSplit.length >= 1) {
            songName = songInfoSplit[0];
            singer = songInfoSplit[1];
            choosen_songInfo = songName + " ♪ " +singer;

            toolbar.setTitle(songName);
            toolbar.setSubtitle(singer);
        }
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);
        dataList = new ArrayList<ItemBean>();
        selectList = new ArrayList<ItemBean>();

        //下方菜单
        bottom_Menu = (LinearLayout)findViewById(R.id.bottom_menu);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    showProgressBar();
                    urlConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_lyrics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_aboutUs) {
            //弹出新界面介绍app和developer
            Intent intent = new Intent(getBaseContext(),AboutUsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //item点击事件
    public void onShowItemClick(ItemBean bean) {
        if (bean.isChecked() && !selectList.contains(bean)) {
            selectList.add(bean);
        } else if (!bean.isChecked() && selectList.contains(bean)) {
            selectList.remove(bean);
        }
    }
    //显示下方操作菜单
    private void showOperate() {
        bottom_Menu.setVisibility(View.VISIBLE);
        // 返回和创建卡片按钮初始化及点击监听
        ImageView img_Back = (ImageView) findViewById(R.id.operate_back);
        //style 1最多4句，2不限，3最多两句
        ImageView img_Style1 = (ImageView) findViewById(R.id.operate_style1);
        ImageView img_Style2 = (ImageView) findViewById(R.id.operate_style2);
        ImageView img_Style3 = (ImageView) findViewById(R.id.operate_style3);

        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    selectList.clear();
                    for (ItemBean bean : dataList) {
                        bean.setChecked(false);
                        bean.setShow(false);
                    }
                    adapter.notifyDataSetChanged();
                    isShow = false;
                    listView.setLongClickable(true);
                    bottom_Menu.setVisibility(View.GONE);
                }
            }
        });
        img_Style1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //musixmatch音乐风格创建歌词卡片
                choosen_Lyrics = "";
                countRow = selectList.size();
                if(countRow == 0){
                    Toast.makeText(getBaseContext(),"Please select the lyrics !", Toast.LENGTH_SHORT).show();
                }
                else if(countRow > 4){
                    Toast.makeText(getBaseContext(),"Rows must be less than 5 !", Toast.LENGTH_SHORT).show();
                }else{
                    for(int i = 0;i < countRow;i++){
                        String s = selectList.get(i).getMsg() + "\n";
                        choosen_Lyrics += s;
                    }
                    Intent intent = new Intent(getBaseContext(),CustomizeCardActivity.class);
                    //传入选中的歌词
                    intent.putExtra("extraLyrics",choosen_Lyrics);
                    intent.putExtra("extraInfo",choosen_songInfo);
                    startActivity(intent);
                }
            }
        });
        img_Style2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //网易云音乐风格创建卡片
                choosen_Lyrics = "";
                countRow = selectList.size();
                if(countRow == 0){
                    Toast.makeText(getBaseContext(),"Please select the lyrics !", Toast.LENGTH_SHORT).show();
                }else {
                    for (int i = 0; i < countRow; i++) {
                        String s = selectList.get(i).getMsg() + "\n";
                        choosen_Lyrics += s;
                    }
                }
                Intent intent = new Intent(getBaseContext(),CustomizeTwoActivity.class);
                //传入选中的歌词
                intent.putExtra("extraLyrics",choosen_Lyrics);
                intent.putExtra("extraInfo",choosen_songInfo);
                startActivity(intent);
            }
        });
        img_Style3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosen_Lyrics = "";
                countRow = selectList.size();
                if(countRow == 0){
                    Toast.makeText(getBaseContext(),"Please select the lyrics !", Toast.LENGTH_SHORT).show();
                }
                else if(countRow > 2){
                    Toast.makeText(getBaseContext(),"Rows must be less than 3 !", Toast.LENGTH_SHORT).show();
                }else{
                    for(int i = 0;i < countRow;i++){
                        String s = selectList.get(i).getMsg() + "\n";
                        choosen_Lyrics += s;
                    }
                    Intent intent = new Intent(getBaseContext(),CustomizeThreeActivity.class);
                    //传入选中的歌词
                    intent.putExtra("extraLyrics",choosen_Lyrics);
                    intent.putExtra("extraInfo",choosen_songInfo);
                    startActivity(intent);
                }
            }
        });
    }

    //丫丫的代码
    // handle
    Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dismissProgressbar();
                    //得到xml字符串
                    String str_xml = msg.obj.toString();
                    //SAXReader sax = new SAXReader();
                    try {
                        Document document = DocumentHelper.parseText(str_xml);
                        Element root = document.getRootElement();
                        List nodes = root.elements("song_lry_response_elt");
                        String lrc_Content = "";
                        //有两个值，第一个为歌名，第二个为歌词
                        for (Iterator it = nodes.iterator(); it.hasNext(); ) {
                            Element elm = (Element) it.next();
                            lrc_Content = elm.getText();
                        }
                        //对返回的string进行处理，截取[后的内容，一行行填充进arrayList
                        str_Lyrics = lrc_Content.split("\\[");
                        for (int i = 0; i < str_Lyrics.length; i++) {
                            //00:14.55]从9开始截取
                            if(str_Lyrics[i].length()>9) {
                                str_Lyrics[i] = str_Lyrics[i].substring(9);
                            }else{
                                str_Lyrics[i] = null;
                            }
                        }
                        //用新数组去掉null
                        List<String> tmp = new ArrayList<String>();
                        for(String str:str_Lyrics){
                            if(str!=null && str.length()!=0){
                                tmp.add(str);
                            }
                        }
                        str_Lyrics = tmp.toArray(new String[0]);

                        //填充listView
                        for (int i = 0; i < str_Lyrics.length; i++) {
                            dataList.add(new ItemBean(str_Lyrics[i], false, false));
                        }
                        adapter = new Adapter(dataList, DisplayLyricsActivity.this);
                        listView.setAdapter(adapter);
                        adapter.setOnShowItemClickListener(DisplayLyricsActivity.this);
                        //设置长按监听器，缺一个如果textview为空不显示checkbox
                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                if (isShow) {
                                    return false;
                                } else {
                                    isShow = true;
                                    for (ItemBean bean : dataList) {
                                        bean.setShow(true);
                                    }
                                    adapter.notifyDataSetChanged();
                                    showOperate();
                                    listView.setLongClickable(false);
                                }
                                return true;
                            }
                        });
                        //设置短按监听器
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (isShow) {
                                    ItemBean bean = dataList.get(position);
                                    boolean isChecked = bean.isChecked();
                                    if (isChecked) {
                                        bean.setChecked(false);
                                    } else {
                                        bean.setChecked(true);
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getBaseContext(), dataList.get(position).getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    str_Lyrics = new String[1];
                    str_Lyrics[0] = "Fail to display lyrics !";
                    //填充listView
                    lyrics_adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,str_Lyrics);
                    listView.setAdapter(lyrics_adapter);
                    break;
            }
        }
    };


    private void urlConnection() {
        try {
            get_lrc_url = get_lrc_url + songid;
            URL url = new URL(get_lrc_url);
            HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
            if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //Input Stream Reader
                BufferedReader in = new BufferedReader(new InputStreamReader(httpconn.getInputStream(), "UTF-8"));
                String line;
                StringBuilder response_lrc = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response_lrc.append(line);
                }
                Message msg = new Message();
                msg.what = 1;
                msg.obj = response_lrc;
                handler2.sendMessage(msg);

               /* Looper.prepare();
                Toast.makeText(getApplicationContext(), "Connection Success!", Toast.LENGTH_SHORT).show();
                Looper.loop();*/
                in.close();
            }
            httpconn.disconnect();

        } catch (Exception e) {
            Message msg = new Message();
            msg.what = 2;
            handler2.sendMessage(msg);
           /* Looper.prepare();
            Toast.makeText(getApplicationContext(), "Fail to Connect", Toast.LENGTH_SHORT).show();
            Looper.loop();*/
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        progressBar.setVisibility(View.VISIBLE);
        viewGroup = (ViewGroup) findViewById(R.id.parent_view);
        viewGroup.addView(progressBar, params);
    }

    private void dismissProgressbar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
            viewGroup.removeView(progressBar);
            progressBar = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (isShow) {
            selectList.clear();
            for (ItemBean bean : dataList) {
                bean.setChecked(false);
                bean.setShow(false);
            }
            adapter.notifyDataSetChanged();
            isShow = false;
            listView.setLongClickable(true);
            bottom_Menu.setVisibility(View.GONE);
        }
        DisplayLyricsActivity.this.finish();
    }
}
