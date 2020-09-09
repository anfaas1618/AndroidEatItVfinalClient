package com.mibtech.nirmalbakeryclient.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.mibtech.nirmalbakeryclient.Model.AddonModel;
import com.mibtech.nirmalbakeryclient.Model.CategoryModel;
import com.mibtech.nirmalbakeryclient.Model.FoodModel;
import com.mibtech.nirmalbakeryclient.Model.ShippingOrderModel;
import com.mibtech.nirmalbakeryclient.Model.SizeModel;
import com.mibtech.nirmalbakeryclient.Model.TokenModel;
import com.mibtech.nirmalbakeryclient.Model.UserModel;
import com.mibtech.nirmalbakeryclient.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {
    public static final String USER_REFERENCES = "Users";
    public static final String POPULAR_CATEGORY_REF = "MostPopular";
    public static final String BEST_DEALS_REF = "BestDeals";
    public static final String SHIPPING_ORDER_REF = "ShippingOrder";
    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1 ;
    public static final String CATEGORY_REF = "Category";
    public static final String COMMENT_REF = "Comments" ;
    public static final String ORDER_REF = "Order" ;
    public static final String NOTI_TITLE = "title";
    public static final String NOTI_CONTENT = "content";
    public static final String IS_SUBSCRIBE_NEWS = "IS_SUBSCRIBE_NEWS" ;
    public static final String NEWS_TOPIC = "news";
    public static final String IS_SEND_IMAGE = "IS_SEND_IMAGE";
    public static final String IMAGE_URL = "IMAGE_URL";
    private static final String TOKEN_REF = "Tokens";
    public static UserModel currentUser;
    public static CategoryModel categorySelected;
    public static FoodModel selectedFood;
    public static ShippingOrderModel currentShippingOrder;


    public static String formatPrice(double price) {
        if(price!=0)
        {
            DecimalFormat df = new DecimalFormat("0.##");
            df.setRoundingMode(RoundingMode.UP);

            String finalPrice = new StringBuilder(df.format(price)).toString();
            return finalPrice.replace(".", ",");
        } else
        {
            return "0,00";
        }

    }

    public static Double calculateExtraPrice(SizeModel userSelectedSize, List<AddonModel> userSelectedAddon) {
        Double result = 0.0;
        if(userSelectedSize == null && userSelectedAddon == null)
            return 0.0;
        else if(userSelectedSize == null)
        {
            //If User Selected Add on !=null, sum price
            for(AddonModel addonModel: userSelectedAddon)
            {
                result+= addonModel.getPrice();
            }
            return result;
        }
        else if (userSelectedAddon == null)
        {
            return userSelectedSize.getPrice()*1.0;
        }
        else
        {
            //If both Size and Addon Selected
            result = userSelectedSize.getPrice()*1.0;
            for(AddonModel addonModel: userSelectedAddon)
            {
                result+= addonModel.getPrice();
            }

            return result;
        }
    }

    public static void setSpanString(String welcome, String name, TextView textView) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(welcome);
        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan,0,name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        textView.setText(builder, TextView.BufferType.SPANNABLE);

    }

    public static String createOrderNumber()
    {
        return new StringBuilder()
                .append(System.currentTimeMillis()) //Get Current time in Millis
                .append(Math.abs(new Random().nextInt())) //Add Random Number to block same number order at same time
                .toString();
    }

    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude-end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if(begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float)(Math.toDegrees(Math.atan(lng/lat)));
        else if(begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float)((90 - Math.toDegrees(Math.atan(lng/lat))) + 90);
        else if(begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float)(Math.toDegrees(Math.atan(lng/lat)) + 180);
        else if(begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float)(Math.toDegrees(Math.atan(lng/lat)) + 270);

        return -1;
    }

    public static String convertStatusToText(int orderStatus) {
        switch (orderStatus)
        {
            case 0:
                return "Placed";
            case 1:
                return "Shipping";
            case 2:
                return "Shipped";
            case -1:
                return "Cancelled";
            default:
                return "Unk";
        }
    }


    public static String getDateOfWeek(int i) {
        switch (i)
        {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "Unk";
        }
    }



    public static void showNotification(Context context, int id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if(intent!=null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,pendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "com_example_eat_it_v2";
        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Eat It V2", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Eat It V2");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_restaurant_menu_black_24dp));

        if(pendingIntent!=null)
            builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notificationManager.notify(id, notification);

    }

    public static void updateNewToken(Context context, String newToken) {
        if(currentUser!=null)
        {
            FirebaseDatabase.getInstance()
                    .getReference(Common.TOKEN_REF)
                    .child(Common.currentUser.getUid())
                    .setValue(new TokenModel(Common.currentUser.getPhone(), newToken))
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public static String createTopicOrder() {
        return new StringBuilder("/topics/new_order_darshil").toString();
    }

    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index=0,len=encoded.length();
        int lat=0, lng=0;

        while(index < len)
        {
            int b, shift =0, result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |= (b & 0xff) << shift;
                shift+=5;
            }while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift =0;
            result = 0;
            do{
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while (b >= 0x20);

            int dlng =  ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double)lat/1E5)),
                    (((double)lng/1E5)));
            poly.add(p);
        }

        return poly;

    }

    public static String getListAddon(List<AddonModel> addonModels) {

        StringBuilder result = new StringBuilder();

        for(AddonModel addonModel : addonModels)
        {
            result.append(addonModel.getName()).append(",");
        }

        return result.substring(0, result.length()-1); //Remove Last ","

    }

    public static FoodModel findFoodInListById(CategoryModel categoryModel, String foodId) {
        if(categoryModel.getFoods()!=null && categoryModel.getFoods().size()>0)
        {
            for(FoodModel foodModel : categoryModel.getFoods())
                if(foodModel.getId().equals(foodId))
                    return foodModel;

                return  null;
        }
        else
            return null;
    }

    public static void showNotificationBigStyle(Context context, int id, String title, String content, Bitmap bitmap, Intent intent) {
        PendingIntent pendingIntent = null;
        if(intent!=null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,pendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "com_example_eat_it_v2";
        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Eat It V2", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Eat It V2");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));

        if(pendingIntent!=null)
            builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notificationManager.notify(id, notification);

    }

}
