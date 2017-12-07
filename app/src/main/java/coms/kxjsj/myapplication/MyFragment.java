package coms.kxjsj.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vange on 2017/12/7.
 */

public class MyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.recyclerview, container, false);
        RecyclerView recyclerView=inflate.findViewById(R.id.childrecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView.setAdapter(AdapterUtils.getAdapter(100,0xff353535));
        return inflate;
    }
}
