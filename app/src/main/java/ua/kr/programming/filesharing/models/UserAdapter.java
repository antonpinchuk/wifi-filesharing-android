package ua.kr.programming.filesharing.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import ua.kr.programming.filesharing.R;

public class UserAdapter extends BaseAdapter {

    private List<User> users;

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        User user = users.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_user, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder)convertView.getTag();

        holder.tvName.setText(user.name);
        holder.tvIp.setText(user.name);

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.tvIp)
        TextView tvIp;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public void setUsers(List<User> users) {
        this.users = users;
        this.notifyDataSetChanged();
    }
}
