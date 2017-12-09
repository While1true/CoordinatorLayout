package coms.kxjsj.myapplication;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       final MyCoordinatorLayout layout=findViewById(R.id.coor);
        layout.setTransYView(layout);
       final ProgressBar viewById = findViewById(R.id.image);
        layout.setPullCallback(new MyCoordinatorLayout.PullCallback() {
            @Override
            public void pull(int dy, int scroll) {

//                viewById.setIndeterminate(false);
//                viewById.setTranslationY(layout.getmAppbarBehavior().getTopAndBottomOffset()+scroll);

            }

            @Override
            public void middle() {
//                viewById.setIndeterminate(true);
//                System.out.println("middle"+Thread.currentThread().getName());
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.RefreshComplete();
                    }
                },2000);

            }
        });
        final RecyclerView recyclerView = findViewById(R.id.bottomRecyclerview);
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return new MyFragment();
            }

            @Override
            public int getCount() {
                return 5;
            }
        });

        BottomSheetUtils.setBottomRecyclerView2Collapse(layout.getmBottomBehavior(), (ViewPager) layout.getmScrollngView());
        /**
         * 用户实现
         */
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(AdapterUtils.getAdapter(100, 0));

    }


}
