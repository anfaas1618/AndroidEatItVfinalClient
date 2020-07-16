package com.nbfinal.androideatitv2client.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nbfinal.androideatitv2client.Callback.IRecyclerClickListener;
import com.nbfinal.androideatitv2client.EventBus.PopularCategoryClick;
import com.nbfinal.androideatitv2client.Model.PopularCategoryModel;
import com.nbfinal.androideatitv2client.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyPopularCategoriesAdapter extends RecyclerView.Adapter<MyPopularCategoriesAdapter.MyViewHOlder> {

    Context context;
    List<PopularCategoryModel> popularCategoryModelList;

    public MyPopularCategoriesAdapter(Context context, List<PopularCategoryModel> popularCategoryModelList) {
        this.context = context;
        this.popularCategoryModelList = popularCategoryModelList;
    }

    @NonNull
    @Override
    public MyViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHOlder(LayoutInflater.from(context)
        .inflate(R.layout.layout_popular_categories_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHOlder holder, int position) {
        Glide.with(context).load(popularCategoryModelList.get(position).getImage())
                .into(holder.category_image);

        holder.txt_category_name.setText(popularCategoryModelList.get(position).getName());
        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int pos) {
//                Toast.makeText(context, ""+popularCategoryModelList.get(pos).getName(), Toast.LENGTH_SHORT).show();
                EventBus.getDefault().postSticky(new PopularCategoryClick(popularCategoryModelList.get(pos)));
            }

        });
    }

    @Override
    public int getItemCount() {
        return popularCategoryModelList.size();
    }

    public class MyViewHOlder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;

        @BindView(R.id.txt_category_name)
        TextView txt_category_name;
        @BindView(R.id.category_image)
        CircleImageView category_image;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHOlder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view, getAdapterPosition());
        }
    }
}
