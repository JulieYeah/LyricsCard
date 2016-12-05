package tracy_pc.createlyricscard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HP-PC on 2016/12/2.
 */
public class Adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ItemBean> list;
    private OnShowItemClickListener onShowItemClickListener;

    public Adapter(List<ItemBean> list, Context context) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_listview, null);
            holder.msg = (TextView) convertView.findViewById(R.id.item_textview);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ItemBean bean = list.get(position);
        // 是否是多选状态
        if (bean.isShow()) {
            holder.cb.setVisibility(View.VISIBLE);
        } else {
            holder.cb.setVisibility(View.GONE);
        }

        holder.msg.setText(bean.getMsg());
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bean.setChecked(true);
                } else {
                    bean.setChecked(false);
                }
                // 回调方法，将Item加入已选
                onShowItemClickListener.onShowItemClick(bean);
            }
        });
        // 必须放在监听后面
        holder.cb.setChecked(bean.isChecked());
        return convertView;
    }

    static class ViewHolder {
        TextView msg;
        CheckBox cb;
    }

    public interface OnShowItemClickListener {
        public void onShowItemClick(ItemBean bean);
    }

    public void setOnShowItemClickListener(OnShowItemClickListener onShowItemClickListener) {
        this.onShowItemClickListener = onShowItemClickListener;
    }
}