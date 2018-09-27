package life.oleg.vkoauth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class FriendsFragment extends android.support.v4.app.Fragment {
    public FriendsFragment() {
        super();
    }

    private LinearLayout linearLayout;
    private TextView nameText;
    private Handler mUIHandler = new Handler();
    private HandlerWorker handlerWorker;
    private ProgressBar progressBar;
    private ImageView imageOwner;

    static int length;
    static String name;
    static User[] users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.friends_layout, container, false);
        linearLayout = v.findViewById(R.id.linear_lay);
        progressBar = v.findViewById(R.id.progressBar);
        nameText = v.findViewById(R.id.name_owner);
        imageOwner = v.findViewById(R.id.image_owner);
        handlerWorker = new HandlerWorker("handlerWorker");
        handlerWorker.start();
        handlerWorker.prepareHandler();
        startRequest(v.getContext());
        return v;
    }


    public void startRequest(final Context context) {
        handlerWorker.postTask(new Runnable() {
            @Override
            public void run() {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (users == null) {
                            makeRequest(context);
                        } else {
                            makeList(context);
                            nameText.setText(name);
                        }
                    }
                });
            }
        });
    }

    private void makeRequest(final Context context) {
        VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "city,photo_200", VKApiConst.COUNT, 5, "order", "random"));
//			startApiCall(request)
        request.executeWithListener(new VKRequest.VKRequestListener() {

            @Override
            public void onComplete(VKResponse response) {
                if (context == null || !isAdded()) {
                    return;
                }
                try {
                    final JSONArray jsonArray = response.json.getJSONObject("response").getJSONArray("items");
                    length = jsonArray.length();
                    users = new User[length];
                    User parser = new User();
                    for (int i = 0; i < length; i++) {
                        users[i] = parser.parser(jsonArray.getJSONObject(i));
                    }
                    makeList(context);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        request = VKApi.users().get(VKParameters.from("name_case", "gen"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                if (context == null || !isAdded()) {
                    return;
                }
                try {
                    final JSONObject jsonObject = response.json.getJSONArray("response").getJSONObject(0);
                    name = new StringBuilder("Друзья "
                            + jsonObject.getString("first_name")
                            + " "
                            + jsonObject.getString("last_name")).toString();
                    nameText.setText(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void makeList(final Context context) {
        for (int i = 0; i < length; i++) {
            final int finalI = i;
            handlerWorker.postTask(new Runnable() {
                @Override
                public void run() {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (users[finalI] != null) {
                                createNewElement(users[finalI], context);
                            }
                        }
                    });
                }
            });
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("SetTextI18n")
    public synchronized void createNewElement(User user, Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        ImageView imageView = new ImageView(context);
        LinearLayout linearLayoutInner = new LinearLayout(context);
        TextView onlineText = new TextView(context);
        TextView nameText = new TextView(context);
        TextView cityText = new TextView(context);


        LinearLayout.LayoutParams layoutParamsInner = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParamsInner.gravity = Gravity.CENTER_VERTICAL;

        LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsText.gravity = Gravity.CENTER_VERTICAL;

        LinearLayout.LayoutParams layoutParamsImage = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsImage.bottomMargin = 16;
        layoutParamsImage.topMargin = 16;
        layoutParamsImage.leftMargin = 16;
        layoutParamsImage.rightMargin = 16;
        layoutParamsImage.gravity = Gravity.CENTER_VERTICAL;

        LinearLayout.LayoutParams linerLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linerLayoutParams.topMargin = 4;
        linerLayoutParams.bottomMargin = 4;


        linearLayout.setLayoutParams(linerLayoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundResource(R.drawable.background);
        linearLayout.setPadding(8, 8, 8, 8);


        imageView.setLayoutParams(layoutParamsImage);
//        imageView.setImageResource(R.mipmap.ic_launcher);
        new DownloadImageTask(imageView).execute(user.getPhotoUrl());


        linearLayoutInner.setOrientation(LinearLayout.VERTICAL);
        linearLayoutInner.setLayoutParams(layoutParamsInner);


        onlineText.setText(user.getOnline());
        onlineText.setLayoutParams(layoutParamsText);


        nameText.setText(user.getFirstName() + " " + user.getLastName());
        nameText.setLayoutParams(layoutParamsText);
        nameText.setTextSize(18);
        nameText.setTextColor(context.getResources().getColor(R.color.vk_black));


        cityText.setText(user.getCity());
        cityText.setLayoutParams(layoutParamsText);
        cityText.setTextSize(16);

        linearLayoutInner.addView(onlineText);
        linearLayoutInner.addView(nameText);
        linearLayoutInner.addView(cityText);
        linearLayout.addView(imageView);
        linearLayout.addView(linearLayoutInner);
        this.linearLayout.addView(linearLayout);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

