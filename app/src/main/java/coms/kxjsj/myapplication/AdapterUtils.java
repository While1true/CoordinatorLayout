package coms.kxjsj.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by vange on 2017/12/7.
 */

public class AdapterUtils {

    public static RecyclerView.Adapter getAdapter(final int count, final int color){
        return new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tt,parent,false)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                TextView tv=holder.itemView.findViewById(R.id.tv);
                if(color!=0){
                    holder.itemView.setBackgroundColor(color);
                }
                tv.setText("Item:"+position);
            }

            @Override
            public int getItemCount() {
                return count;
            }
        };
    }
}
