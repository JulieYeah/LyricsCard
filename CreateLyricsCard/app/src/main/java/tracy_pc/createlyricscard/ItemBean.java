package tracy_pc.createlyricscard;

/**
 * Created by HP-PC on 2016/12/2.
 */
public class ItemBean {
    private String msg;
    private boolean isShow; // 是否显示CheckBox
    private boolean isChecked; // 是否选中CheckBox

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public boolean isShow() {
        return isShow;
    }
    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
    public ItemBean(String msg, boolean isShow, boolean isChecked) {
        super();
        this.msg = msg;
        this.isShow = isShow;
        this.isChecked = isChecked;
    }
}
