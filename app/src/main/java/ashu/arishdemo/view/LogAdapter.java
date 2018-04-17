package ashu.arishdemo.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ashu.arishdemo.R;
import ashu.arishdemo.model.LogTimeStamp;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by apple on 16/04/18.
 */

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> implements RealmChangeListener{

    Context context;
    RealmResults<LogTimeStamp> logTimeStamps;


    public LogAdapter(Context context,RealmResults<LogTimeStamp> logTimeStamps){
        this.context = context;
        this.logTimeStamps = logTimeStamps;
        logTimeStamps.addChangeListener(this);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.log_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtLog.setText(logTimeStamps.get(position).getKeyword());
        holder.txtDate.setText(logTimeStamps.get(position).getDateTime());
    }

    @Override
    public int getItemCount() {
        return logTimeStamps.size();
    }

    @Override
    public void onChange() {
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtLog;
        TextView txtDate;

        ViewHolder(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txtTimeStamp);
            txtLog = (TextView) itemView.findViewById(R.id.txtQuery);
        }
    }
}
