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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import coms.kxjsj.myapplication.viewpager.AlphaTransformer;
import coms.kxjsj.myapplication.viewpager.LoopFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MyCoordinatorLayout layout=findViewById(R.id.coor);
        final View viewById = findViewById(R.id.image);
        ViewPager viewPagerx=findViewById(R.id.viewpagerx);
        viewPagerx.setPageTransformer(false,new AlphaTransformer());
        viewPagerx.setAdapter(new LoopFragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getActualCount() {
                return 6;
            }

            @Override
            public Fragment getActualItem(int position) {
                return new coms.kxjsj.myapplication.viewpager.MyFragment();
            }

            @Override
            public CharSequence getActualPagerTitle(int position) {
                return null;
            }
        }.setAutoSwitch(true));
        viewById.post(new Runnable() {
            @Override
            public void run() {
                layout.setMiddle(viewById.getMeasuredHeight());
            }
        });
        findViewById(R.id.appbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"fffffffffff",Toast.LENGTH_SHORT).show();
            }
        });
        layout.setPullCallback(new MyCoordinatorLayout.PullCallback() {
            @Override
            public void pull(int dy, int scroll) {

//                viewById.setIndeterminate(false);
                viewById.setTranslationY(layout.getmAppbarBehavior().getTopAndBottomOffset()+scroll);

            }

            @Override
            public void middle() {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.RefreshComplete();
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                },2000);

            }
        });
//        final RecyclerView recyclerView = findViewById(R.id.bottomRecyclerview);
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

//        BottomSheetUtils.setBottomRecyclerView2Collapse(layout.getmBottomBehavior(), (ViewPager) layout.getmScrollngView());
//        /**
//         * 用户实现
//         */
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//        recyclerView.setAdapter(AdapterUtils.getAdapter(100, 0));

    }


}
