package com.nbclient.androideatitv2client.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.nbclient.androideatitv2client.Adapter.MyBestDealsAdapter;
import com.nbclient.androideatitv2client.Adapter.MyPopularCategoriesAdapter;
import com.nbclient.androideatitv2client.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    Unbinder unbinder;

    @BindView(R.id.recycler_popular)
    RecyclerView recycler_popular;
    @BindView(R.id.viewPager)
    LoopingViewPager viewPager;

    LayoutAnimationController layoutAnimationController;

    @SuppressLint("FragmentLiveDataObserve")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        unbinder = ButterKnife.bind(this, root);

        init();

        homeViewModel.getPopularList().observe(this, popularCategoryModels -> {
            MyPopularCategoriesAdapter adapter = new MyPopularCategoriesAdapter(getContext(), popularCategoryModels);
            recycler_popular.setAdapter(adapter);
        });

        homeViewModel.getBestDealList().observe(this, bestDealModels -> {
            MyBestDealsAdapter adapter = new MyBestDealsAdapter(getContext(), bestDealModels, true);
            viewPager.setAdapter(adapter);
        });

        return root;
    }

    private void init() {
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        recycler_popular.setHasFixedSize(true);
        recycler_popular.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recycler_popular.setLayoutAnimation(layoutAnimationController);

    }

    @Override
    public void onPause() {
        viewPager.pauseAutoScroll();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.resumeAutoScroll();
    }
}