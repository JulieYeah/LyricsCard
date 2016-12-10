package tracy_pc.createlyricscard;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomizeCardActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;
    private TextView textView_Info;
    //包含图片和文字的父组件
    private RelativeLayout containerView;
    //父组件的尺寸
    private float imageWidth, imageHeight, imagePositionX, imagePositionY;
    //底部按钮组
    private RelativeLayout btn_Group;
    private ImageButton btn_Images;
    private ImageButton btn_Fonts;
    private ImageButton btn_Save;
    //图片和进度条
    private LinearLayout bottomImages;
    private SeekBar seekBar;
    private LinearLayout img_Gallary;
    private int[] img_Group;
    //相册和拍照
    private static final int ALBUM_REQUEST_CODE = 1;
    private static final int CROP_REQUEST_CODE = 2;
    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
    Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);
    //文字
    private LinearLayout bottomFonts;
    private LinearLayout font_Gallary;
    private int[] font_Group;
    private Button btn_SizeMinus;
    private Button btn_SizePlus;
    private Button btn_AlignLeft;
    private Button btn_AlignMiddle;
    private Button btn_AlignRight;
    int font_size = 28;

    private ImageButton btn_Okay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.left_arrow);//设置导航栏图标
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CustomizeCardActivity.this).setTitle("Discard all changes ？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("Discard", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“Discard”返回歌词页
                                CustomizeCardActivity.this.finish();

                            }
                        })
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“Edit”后的操作,这里不设置没有任何操作,继续编辑
                            }
                        }).show();
            }
        });

        //自己设置的okay按钮
        btn_Okay = (ImageButton) findViewById(R.id.btn_okay);

        //绑定控件
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        textView_Info = (TextView) findViewById(R.id.textView_info);
        containerView = (RelativeLayout) findViewById(R.id.container);
        //按钮组
        btn_Group = (RelativeLayout) findViewById(R.id.btn_group);
        btn_Images = (ImageButton) findViewById(R.id.btn_images);
        btn_Fonts = (ImageButton) findViewById(R.id.btn_fonts);
        btn_Save = (ImageButton) findViewById(R.id.btn_save);
        //images
        bottomImages = (LinearLayout) findViewById(R.id.bottom_images);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        img_Gallary = (LinearLayout) findViewById(R.id.gallery_image);
        //fonts
        bottomFonts = (LinearLayout) findViewById(R.id.bottom_fonts);
        btn_SizeMinus = (Button) findViewById(R.id.size_minus);
        btn_SizePlus = (Button) findViewById(R.id.size_plus);
        btn_AlignLeft = (Button) findViewById(R.id.align_left);
        btn_AlignMiddle = (Button) findViewById(R.id.align_middle);
        btn_AlignRight = (Button) findViewById(R.id.align_right);
        font_Gallary = (LinearLayout) findViewById(R.id.gallery_font);

        //获得从上一个Activity传来的intent对象
        Intent intent=getIntent();
        String str_Value = intent.getStringExtra("extraLyrics");
        String str_Info = intent.getStringExtra("extraInfo");
        textView.setText(str_Value);
        textView_Info.setText(str_Info);

        //获取当前窗口长度，设置为imageView的宽度
        WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth,screenWidth));

        //设置textView最大高度为图片长度
        textView.setMaxHeight(screenWidth);

        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                imagePositionX = imageView.getX();
                imagePositionY = imageView.getY();
                imageWidth = imageView.getWidth();
                imageHeight = imageView.getHeight();
            }
        });
        final GestureDetector gestureDetector = new GestureDetector(this, new SimpleGestureListenerImpl());
        //移动
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        //okay按钮点击事件
        btn_Okay.getBackground().setAlpha(0);
        btn_Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_Okay.setVisibility(View.INVISIBLE);
                btn_Group.setVisibility(View.VISIBLE);
                bottomImages.setVisibility(View.GONE);
                bottomFonts.setVisibility(View.GONE);
            }
        });
        //images按钮点击事件
        btn_Images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置按钮组不可见，底部横向view可见
                btn_Okay.setVisibility(View.VISIBLE);
                btn_Group.setVisibility(View.INVISIBLE);
                bottomImages.setVisibility(View.VISIBLE);

                img_Group = new int[]{R.mipmap.bg_album,R.mipmap.bg_1,R.mipmap.bg_2,R.mipmap.bg_3,R.mipmap.bg_4,R.mipmap.bg_5,
                        R.mipmap.bg_6,R.mipmap.bg_7,R.mipmap.bg_8,R.mipmap.bg_9,R.mipmap.bg_10};
                for(int i = 0;i < img_Group.length;i++){
                    ImageButton img_bg= new ImageButton(getBaseContext());
                    img_bg.setLayoutParams(new HorizontalScrollView.LayoutParams(300,300));
                    img_bg.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    img_bg.setBackgroundColor(Color.WHITE);
                    img_bg.setImageResource(img_Group[i]);
                    img_bg.setTag(img_Group[i]);
                    img_Gallary.addView(img_bg);

                    img_bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int src = (int)v.getTag();
                            switch(src){
                                case R.mipmap.bg_album:
                                    //查看手机相册
                                    Intent intent_album = new Intent(Intent.ACTION_PICK);
                                    intent_album.setType("image/*");
                                    startActivityForResult(intent_album,ALBUM_REQUEST_CODE);
                                    break;
                                default:
                                    imageView.setImageResource(src);
                                    break;
                            }
                        }
                    });
                }
            }
        });
        //进度条拖动事件
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //progress 0-255，根据这个值调整亮度
                int brightness = progress - 127;
                ColorMatrix cMatrix = new ColorMatrix();
                cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness, // 改变亮度
                        0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });
                imageView.setColorFilter(new ColorMatrixColorFilter(cMatrix));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //fonts按钮点击事件
        btn_Fonts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_Okay.setVisibility(View.VISIBLE);
                btn_Group.setVisibility(View.INVISIBLE);
                bottomFonts.setVisibility(View.VISIBLE);


                font_Group = new int[]{R.mipmap.font_0,R.mipmap.font_1,R.mipmap.font_2,R.mipmap.font_3,R.mipmap.font_4,R.mipmap.font_5,
                        R.mipmap.font_6,R.mipmap.font_7,R.mipmap.font_8,R.mipmap.font_9,R.mipmap.font_10};
                for(int i = 0;i < font_Group.length;i++){
                    ImageButton img_font= new ImageButton(getBaseContext());
                    img_font.setLayoutParams(new HorizontalScrollView.LayoutParams(300,300));
                    img_font.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    img_font.setImageResource(font_Group[i]);
                    img_font.setTag(i);
                    font_Gallary.addView(img_font);

                    img_font.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //改变字体样式
                            int font_tag = (int)v.getTag();
                            if(font_tag == 0){
                                textView.setTypeface(Typeface.DEFAULT);
                            }else{
                                String filePath = "fonts/" + font_tag + ".ttf";
                                Typeface typeFace = Typeface.createFromAsset(getAssets(),filePath);
                                textView.setTypeface(typeFace);
                            }
                        }
                    });
                }
            }
        });
        //fonts里面5个按钮的点击事件
        btn_SizeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //字体变小
                font_size -=2;
                if(font_size<=10){
                    font_size = 10;
                }
                textView.setTextSize(font_size);
            }
        });
        btn_SizePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //字体变大
                font_size +=2;
                if(font_size>=50){
                    font_size = 50;
                }
                textView.setTextSize(font_size);
            }
        });
        btn_AlignLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //左边对齐
                textView.setGravity(Gravity.LEFT);
            }
        });
        btn_AlignMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //中间对齐
                textView.setGravity(Gravity.CENTER);
            }
        });
        btn_AlignRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //右边对齐
                textView.setGravity(Gravity.RIGHT);
            }
        });

        //save按钮点击事件
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap lyricsCard = loadBitmapFromView(containerView);
                String fileName = getFileName();
                //判断SD卡是否可用
                if (android.os.Environment.getExternalStorageState().equals(
                        android.os.Environment.MEDIA_MOUNTED)) {
                    //建立文件夹
                    String appHome = Environment.getExternalStorageDirectory().getAbsolutePath()+"/melyrics";
                    File file = new File(appHome);
                    if(!file.exists()){
                        file.mkdir();
                    }
                    String filePath = appHome + File.separator + fileName + ".jpg";
                    Uri uri = Uri.parse(filePath);
                    try {
                        lyricsCard.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
                        Toast.makeText(getBaseContext(), "Saved at : sdcard/melyrics/" + fileName + ".jpg",Toast.LENGTH_LONG).show();
                        showShareDialog(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.i("tag","error");
                    }
                } else {
                    Toast.makeText(getBaseContext(), "SD card is not usable !",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //以时间给文件命名
    private String getFileName() {
        String fileName = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        Date curDate = new Date(System.currentTimeMillis());
        fileName = formatter.format(curDate);
        return fileName;
    }

    //移动
    private int count = 0;
    //textView的x方向和y方向移动量
    private float mDx, mDy;
    private class SimpleGestureListenerImpl extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //向右移动时，distanceX为负；向左移动时，distanceX为正
            //向下移动时，distanceY为负；向上移动时，distanceY为正
            count++;
            mDx -= distanceX;
            mDy -= distanceY;

            //边界检查
            mDx = calPosition(imagePositionX - textView.getX(), imagePositionX + imageWidth - (textView.getX() + textView.getWidth()), mDx);
            mDy = calPosition(imagePositionY - textView.getY(), imagePositionY + imageHeight - (textView.getY() + textView.getHeight()), mDy);

            //控制刷新频率
            if (count % 5 == 0) {
                textView.setX(textView.getX() + mDx);
                textView.setY(textView.getY() + mDy);
            }
            return true;
        }
    }
    //计算正确的显示位置（不能超出边界）
    private float calPosition(float min, float max, float current) {
        if (current < min) {
            return min;
        }
        if (current > max) {
            return max;
        }
        return current;
    }
    //相机和相册的回调函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        ContentResolver resolver = getContentResolver();
        switch(requestCode){
            case ALBUM_REQUEST_CODE:
                if(data == null){
                    return;
                }
                cropPhoto(data.getData());
                break;
            case CROP_REQUEST_CODE:
                if(imageUri != null){
                    Bitmap bitmap = decodeUriAsBitmap(imageUri);//decode bitmap
                    imageView.setImageBitmap(bitmap);
                }
                break;
            default:
                break;
        }
    }
    //裁剪图片
    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);

        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }
    private Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    //以图片形式获取View显示的内容
    public static Bitmap loadBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //重写返回键
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(CustomizeCardActivity.this).setTitle("Discard all changes ？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“Discard”返回歌词页
                        CustomizeCardActivity.this.finish();

                    }
                })
                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“Edit”后的操作,这里不设置没有任何操作,继续编辑
                    }
                }).show();
    }

    //after save the file choose to share,create a new dialog style
    private void showShareDialog(final Uri uri) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.share_dialog,
                            (ViewGroup) findViewById(R.id.sharing_to));
            AlertDialog.Builder builder;
            final Dialog alertDialog;
            builder = new AlertDialog.Builder(this);
            builder.setView(layout);
            builder.setTitle("Click the picture to share");
            alertDialog = builder.create();
            Button cancle_share;
            ImageView image_share = (ImageView)layout.findViewById(R.id.img_finished);
            image_share.setImageBitmap(loadBitmapFromView(containerView));
            cancle_share = (Button)layout.findViewById(R.id.not_now_share);
            alertDialog.show();
            View.OnClickListener sharingListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            switch (v.getId()){
                                case R.id.img_finished:
                                    shareImage(uri);
                                    break;
                                case R.id.not_now_share:
                                    alertDialog.dismiss();
                                    layout.setVisibility(View.GONE);
                            }
                        }
                };
            cancle_share.setOnClickListener(sharingListener);
            image_share.setOnClickListener(sharingListener);
        }

        //调用系统自带的软件进行分享
        private void shareImage(Uri uri) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM,uri);
            startActivity(Intent.createChooser(share,"Share Image to"));
        }
}