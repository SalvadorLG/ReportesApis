package com.example.reportesapis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.reportesapis.entities.PostResponse;
import com.example.reportesapis.network.ApiService;
import com.example.reportesapis.network.RetrofitBuilder;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";

    @BindView(R.id.post_title)
    TextView title;
    @BindView(R.id.post_body)
    TextView body;


    ApiService service;
    TokenManager tokenManager;
    Call<PostResponse> call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(PostActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
    }

    @OnClick(R.id.btn_posts)
    void getPosts(){

        call = service.posts();
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    title.setText(response.body().getData().get(0).getTitle());
                    body.setText(response.body().getData().get(0).getBody());
                    for(int i = 0; i < response.body().getData().size(); i++){
                        System.out.println(response.body().getData().get(i).getTitle()+
                                "-"+response.body().getData().get(i).getBody());
                    }
                    Log.w(TAG,"lista"+ response.body().getData().size());
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(PostActivity.this, LoginActivity.class));
                    finish();

                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
    }
}
