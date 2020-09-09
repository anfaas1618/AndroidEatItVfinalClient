package com.mibtech.nirmalbakeryclient.ui.menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mibtech.nirmalbakeryclient.Adapter.MyCategoriesAdapter;
import com.mibtech.nirmalbakeryclient.Common.Common;
import com.mibtech.nirmalbakeryclient.Common.SpacesItemDecoration;
import com.mibtech.nirmalbakeryclient.EventBus.MenuItemBack;
import com.mibtech.nirmalbakeryclient.Model.CategoryModel;
import com.mibtech.nirmalbakeryclient.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class MenuFragment extends Fragment {

    private MenuViewModel menuViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_menu)
    RecyclerView recycler_menu;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyCategoriesAdapter adapter;

    @SuppressLint("FragmentLiveDataObserve")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        menuViewModel =
                ViewModelProviders.of(this).get(MenuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        unbinder = ButterKnife.bind(this, root);
        initViews();

        menuViewModel.getMessageError().observe(this, s -> {
            Toast.makeText(getContext(), "" + s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        menuViewModel.getCategoryListMutable().observe(this, categoryModels -> {
            dialog.dismiss();
            adapter = new MyCategoriesAdapter(getContext(), categoryModels);
            recycler_menu.setAdapter(adapter);
            recycler_menu.setLayoutAnimation(layoutAnimationController);

        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        //Event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //Clear TExt when clikc toClear button on Search View
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ed = (EditText)searchView.findViewById(R.id.search_src_text);
                //Clear Text
                ed.setText("");
                //Clear Query
                searchView.setQuery("", false);
                //Collapse Action View
                searchView.onActionViewCollapsed();
                //Collapse Search Widget
                menuItem.collapseActionView();
                //Restore result to original
                menuViewModel.loadCategories();
            }
        });

    }

    private void startSearch(String query) {
        List<CategoryModel> resultList = new ArrayList<>();
        for(int i=0;i<adapter.getListCategory().size();i++) {

            CategoryModel categoryModel = adapter.getListCategory().get(i);

            if (categoryModel.getName().toLowerCase().contains(query.toLowerCase())) {
//                categoryModel.setPositionInList(i); //Save Index
                resultList.add(categoryModel);
            }
        }

        menuViewModel.getCategoryListMutable().setValue(resultList);
    }

    private void initViews() {
        setHasOptionsMenu(true);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        dialog.show();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter != null) {
                    switch (adapter.getItemViewType(position)) {
                        case Common
                                .DEFAULT_COLUMN_COUNT:
                            return 1;
                        case Common.FULL_WIDTH_COLUMN:
                            return 2;
                        default:
                            return -1;

                    }
                }
                return -1;
            }
        });

        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }
}