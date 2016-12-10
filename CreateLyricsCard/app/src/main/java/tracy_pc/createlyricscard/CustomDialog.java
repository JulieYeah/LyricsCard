package tracy_pc.createlyricscard;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by yeyaya on 12/8/16.
 */

public class CustomDialog extends Dialog {
    int layoutRes;
    Context context;
    public CustomDialog(Context context) {
        super(context);
    }
    public CustomDialog(Context context,int resLayout){
        super(context);
        this.context = context;
        this.layoutRes=resLayout;
    }
    public CustomDialog(Context context,int theme,int resLayout){
        super(context,theme);
        this.context=context;
        this.layoutRes=resLayout;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        this.setCancelable(true);
    }

}
