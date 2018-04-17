package ashu.arishdemo.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ashu.arishdemo.R;

/**
 * Created by apple on 16/04/18.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder>{

    Context context;
    List<String> url;

    public SearchResultAdapter(Context context, List<String> url){
        this.context = context;
        this.url = url;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_items, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtUrl.setText("Url: " +url.get(position).toString());
        Glide.with(context)
                .load(url.get(position))
                .into(holder.imgSearch);
    }

    @Override
    public int getItemCount() {
        return url.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUrl;
        ImageView imgSearch;

        ViewHolder(View itemView) {
            super(itemView);
            imgSearch = (ImageView)itemView.findViewById(R.id.imgSearch);
            txtUrl = (TextView) itemView.findViewById(R.id.txtUrl);
        }
    }
}
