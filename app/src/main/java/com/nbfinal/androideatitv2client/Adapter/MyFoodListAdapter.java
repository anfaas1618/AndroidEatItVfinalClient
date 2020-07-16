package com.nbfinal.androideatitv2client.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nbfinal.androideatitv2client.Callback.IRecyclerClickListener;
import com.nbfinal.androideatitv2client.Common.Common;
import com.nbfinal.androideatitv2client.Database.CartDataSource;
import com.nbfinal.androideatitv2client.Database.CartDatabase;
import com.nbfinal.androideatitv2client.Database.CartItem;
import com.nbfinal.androideatitv2client.Database.LocalCartDataSource;
import com.nbfinal.androideatitv2client.EventBus.CounterCartEvent;
import com.nbfinal.androideatitv2client.EventBus.FoodItemClick;
import com.nbfinal.androideatitv2client.Model.FoodModel;
import com.nbfinal.androideatitv2client.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyFoodListAdapter extends RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder> {

    private Context context;
    private List<FoodModel> foodModelList;
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;

    public MyFoodListAdapter(Context context, List<FoodModel> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;
        this.compositeDisposable = new CompositeDisposable();
        this.cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_food_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(foodModelList.get(position).getImage()).into((holder.img_food_image));
        holder.txt_food_price.setText(new StringBuilder("₹").append(foodModelList.get(position).getPrice()));
        holder.txt_food_name.setText(new StringBuilder("")
                .append(foodModelList.get(position).getName()));

        //Event
        holder.setListener((view, pos) -> {
            Common.selectedFood = foodModelList.get(pos);
            Common.selectedFood.setKey(String.valueOf(pos));
            EventBus.getDefault().postSticky(new FoodItemClick(true, foodModelList.get(pos)));
        });

        holder.img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartItem = new CartItem();
                cartItem.setUid(Common.currentUser.getUid());
                cartItem.setUserPhone(Common.currentUser.getPhone());
                cartItem.setCategoryId(Common.categorySelected.getMenu_id());
                cartItem.setFoodId(foodModelList.get(position).getId());
                cartItem.setFoodName(foodModelList.get(position).getName());
                cartItem.setFoodImage(foodModelList.get(position).getImage());
                cartItem.setFoodPrice(Double.valueOf(String.valueOf(foodModelList.get(position).getPrice())));
                cartItem.setFoodQuantity(1);
                cartItem.setFoodExtraPrice(0.0); //Because Default we do not choose Size + Addon
                cartItem.setFoodAddOn("Default");
                cartItem.setFoodSize("Default");

                cartDataSource.getItemWithAllOptionsInCart(Common.currentUser.getUid(),
                        Common.categorySelected.getMenu_id(),
                        cartItem.getFoodId(),
                        cartItem.getFoodSize(),
                        cartItem.getFoodAddOn())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<CartItem>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                
                            }

                            @Override
                            public void onSuccess(CartItem cartItemFromDB) {
                                if(cartItemFromDB.equals(cartItem))
                                {
                                    //Already in Database, Just Update
                                    cartItemFromDB.setFoodExtraPrice(cartItem.getFoodExtraPrice());
                                    cartItemFromDB.setFoodAddOn(cartItem.getFoodAddOn());
                                    cartItemFromDB.setFoodSize(cartItem.getFoodSize());
                                    cartItemFromDB.setFoodQuantity(cartItemFromDB.getFoodQuantity() + cartItem.getFoodQuantity());

                                    cartDataSource.updateCartItem(cartItemFromDB)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new SingleObserver<Integer>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {
                                                    
                                                }

                                                @Override
                                                public void onSuccess(Integer integer) {
                                                    Toast.makeText(context, "Update Cart Success!", Toast.LENGTH_SHORT).show();
                                                    EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Toast.makeText(context, "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                else
                                {
                                    //Item not available in Cart before, Insert new
                                    compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(()->{
                                                Toast.makeText(context, "Add to Cart Success!", Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                            }, throwable -> {
                                                Toast.makeText(context, "[Cart Error]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if(e.getMessage().contains("empty"))
                                {
                                    //Default this code will fired
                                    compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(()->{
                                                Toast.makeText(context, "Add to Cart Success!", Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                            }, throwable -> {
                                                Toast.makeText(context, "[Cart Error]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }));
                                }

                                Toast.makeText(context, "[GET CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


//                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
//                    .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(()->{
//                            Toast.makeText(context, "Add to Cart Success!", Toast.LENGTH_SHORT).show();
//
//                            //Here, We will send notification to HomeActivity to update counter in Cart
//                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
//                        }, throwable -> {
//                            Toast.makeText(context, "[Cart Error]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                        })
//                );

            }
        });
    }

    @Override
    public int getItemCount() {
        return foodModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Unbinder unbinder;
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.img_food_image)
        ImageView img_food_image;
        @BindView(R.id.img_fav)
        ImageView img_fav;
        @BindView(R.id.img_quick_cart)
        ImageView img_cart;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
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
