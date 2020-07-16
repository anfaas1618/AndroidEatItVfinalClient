package com.NirmalBakeryClient.androideatitv2client.ui.view_orders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NirmalBakeryClient.androideatitv2client.Adapter.MyOrdersAdapter;
import com.NirmalBakeryClient.androideatitv2client.Callback.ILoadOrderCallbackListener;
import com.NirmalBakeryClient.androideatitv2client.Common.Common;
import com.NirmalBakeryClient.androideatitv2client.Common.MySwipeHelper;
import com.NirmalBakeryClient.androideatitv2client.Database.CartDataSource;
import com.NirmalBakeryClient.androideatitv2client.Database.CartDatabase;
import com.NirmalBakeryClient.androideatitv2client.Database.CartItem;
import com.NirmalBakeryClient.androideatitv2client.Database.LocalCartDataSource;
import com.NirmalBakeryClient.androideatitv2client.EventBus.CounterCartEvent;
import com.NirmalBakeryClient.androideatitv2client.EventBus.MenuItemBack;
import com.NirmalBakeryClient.androideatitv2client.Model.OrderModel;
import com.NirmalBakeryClient.androideatitv2client.Model.ShippingOrderModel;
import com.NirmalBakeryClient.androideatitv2client.R;
import com.NirmalBakeryClient.androideatitv2client.TrackingOrderActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ViewOrdersFragment extends Fragment implements ILoadOrderCallbackListener {

    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;

    AlertDialog dialog;

    private Unbinder unbinder;


    private ViewOrdersViewModel viewOrdersViewModel;

    private ILoadOrderCallbackListener listener;

    @SuppressLint("FragmentLiveDataObserve")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewOrdersViewModel =
                ViewModelProviders.of(this).get(ViewOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_view_order, container, false);
        unbinder = ButterKnife.bind(this, root);
        initView(root);
        loadOrdersFromFirebase();

        viewOrdersViewModel.getMutableLiveDataOrderList().observe(this, orderModelList -> {
            MyOrdersAdapter adapter = new MyOrdersAdapter(getContext(), orderModelList);
            recycler_orders.setAdapter(adapter);
        });

        return root;
    }

    private void loadOrdersFromFirebase() {
        List<OrderModel> orderList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .orderByChild("userId")
                .equalTo(Common.currentUser.getUid())
                .limitToLast(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            OrderModel order = orderSnapshot.getValue(OrderModel.class);
                            order.setOrderNumber(orderSnapshot.getKey());
                            orderList.add(order);
                        }

                        listener.onLoadOrderSuccess(orderList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onLoadOrderFailed(databaseError.getMessage());
                    }
                });
    }

    private void initView(View root) {

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        listener = this;

        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();

        recycler_orders.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_orders.setLayoutManager(layoutManager);
        recycler_orders.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recycler_orders, 250) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Cancel Order", 30, 0, Color.parseColor("#FF3C30"),
                        pos -> {
                            OrderModel orderModel = ((MyOrdersAdapter) recycler_orders.getAdapter())
                                    .getItemAtPosition(pos);
                            if (orderModel.getOrderStatus() == 0) {
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                                builder.setTitle("Cancel Order")
                                        .setMessage("Do you really want to cancel this order?")
                                        .setNegativeButton("NO", (dialogInterface, which) -> {
                                            dialogInterface.dismiss();
                                        })
                                        .setPositiveButton("YES", (dialog, which) -> {
                                            Map<String, Object> update_data = new HashMap<>();
                                            update_data.put("orderStatus", -1); //Cancel Order
                                            FirebaseDatabase.getInstance()
                                                    .getReference(Common.ORDER_REF)
                                                    .child(orderModel.getOrderNumber())
                                                    .updateChildren(update_data)
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnSuccessListener(aVoid -> {
                                                        orderModel.setOrderStatus(-1); //Local Update
                                                        ((MyOrdersAdapter) recycler_orders.getAdapter()).setItemAtPosition(pos, orderModel);
                                                        recycler_orders.getAdapter().notifyItemChanged(pos);
                                                        Toast.makeText(getContext(), "Cancel order successfully!", Toast.LENGTH_SHORT).show();
                                                    });

                                        });
                                androidx.appcompat.app.AlertDialog dialog = builder.create();
                                dialog.show();

                            } else {
                                Toast.makeText(getContext(), new StringBuilder("Your order was changed to ")
                                                .append(Common.convertStatusToText(orderModel.getOrderStatus()))
                                                .append(" , so you can't CANCEL it!")
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }

                ));

                buf.add(new MyButton(getContext(), "Tracking Order", 30, 0, Color.parseColor("#001970"),
                        pos -> {
                            OrderModel orderModel = ((MyOrdersAdapter) recycler_orders.getAdapter())
                                    .getItemAtPosition(pos);

                            //Fetch from Firebase
                            FirebaseDatabase.getInstance()
                                    .getReference(Common.SHIPPING_ORDER_REF) //Copy from Shipper App
                                    .child(orderModel.getOrderNumber())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Common.currentShippingOrder = dataSnapshot.getValue(ShippingOrderModel.class);
                                                Common.currentShippingOrder.setKey(dataSnapshot.getKey());
                                                if (Common.currentShippingOrder.getCurrentLat() != -1 &&
                                                        Common.currentShippingOrder.getCurrentLng() != -1)
                                                {
                                                    startActivity(new Intent(getContext(), TrackingOrderActivity.class));
                                                }
                                                else
                                                {
                                                    Toast.makeText(getContext(), "Shipepr not start ship your order, just wait!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getContext(), "Your order is just placed, must be wait for Shipping!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(getContext(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }

                ));
                buf.add(new MyButton(getContext(), "Repeat Order", 30, 0, Color.parseColor("#5d4037"),
                        pos -> {
                            OrderModel orderModel = ((MyOrdersAdapter) recycler_orders.getAdapter())
                                    .getItemAtPosition(pos);
                            dialog.show();

                            cartDataSource.cleanCart(Common.currentUser.getUid())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
//                                            Toast.makeText(getContext(), "Clear Cart Success!", Toast.LENGTH_SHORT).show();
                                            //After Clean Cart, Just add new
                                            CartItem[] cartItems = orderModel.getCartItemList()
                                                    .toArray(new CartItem[orderModel.getCartItemList().size()]);

                                            //Insert New
                                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItems)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(()->{
                                                        dialog.dismiss();
                                                        Toast.makeText(getContext(), "Add all item to Cart Success!", Toast.LENGTH_SHORT).show();
                                                        EventBus.getDefault().postSticky(new CounterCartEvent(true)); //Counter Fab
                                                    }, throwable -> {
                                                        dialog.dismiss();
                                                        Toast.makeText(getContext(), "[Repeat Cart]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }));

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            dialog.dismiss();
                                            Toast.makeText(getContext(), "[ERROR]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }

                ));
            }
        };


    }

    @Override
    public void onLoadOrderSuccess(List<OrderModel> orderList) {
        dialog.dismiss();
        viewOrdersViewModel.setMutableLiveDataOrderList(orderList);
    }

    @Override
    public void onLoadOrderFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}