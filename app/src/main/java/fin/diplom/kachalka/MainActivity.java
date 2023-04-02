package fin.diplom.kachalka;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fin.diplom.kachalka.databinding.ActivityMainBinding;



public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    NewsFragment newsfragment;
    NotebookFragment notebookfragment;
    Fragment3 fragment3;
    Fragment4 fragment4;
    Fragment5 fragment5;
    String authToken;
    static String basic_url = "http://10.0.2.2:1488/pract/";

    public void login_request(String login, String password) throws JSONException {
        String url = basic_url + "login/";

        JSONObject  credentials = new JSONObject (){{
            put("username",login);
            put("password",password);
        }};

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, credentials, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, error -> System.out.println(error));
        queue.add(jor);
    }

    public static void get_request(Object obj, String last_url, View view, Method fill_view, @Nullable ArrayList<Object> objects){
        String url = basic_url+last_url;

        RequestQueue queue = Volley.newRequestQueue(view.getContext());

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (objects != null) {
                        fill_view.invoke(obj, view, response, objects);
                    }else {
                        fill_view.invoke(obj, view, response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, error -> System.out.println(error));//{
//            @Override
//            public Map getHeaders() throws AuthFailureError {
//
//            }
//        };
        queue.add(jor);
    }

    public void startApp(){
        changeScreen(new NewsFragment());
        newsfragment = new NewsFragment();
        notebookfragment = new NotebookFragment();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
        fragment5 = new Fragment5();

        Bundle notebook_bundle = new Bundle();
        notebookfragment.setArguments(notebook_bundle);


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.news:
                    changeScreen(newsfragment);
                    break;
                case R.id.item2:
                    changeScreen(notebookfragment);
                    break;
                case R.id.item3:
                    changeScreen(fragment3);
                    break;
                case R.id.item4:
                    changeScreen(fragment4);
                    break;
                case R.id.item5:
                    changeScreen(fragment5);
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        findViewById(R.id.login_layout).setVisibility(View.GONE);

        SharedPreferences sharedPreferences = this.getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken", null);

        if(authToken == null) {
            findViewById(R.id.main_layout).setVisibility(View.GONE);
            findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
        }else{
            startApp();
        }

    }

    private void changeScreen(Fragment screen){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.screens_layout,screen,null).commit();

    }

    public void onLoginClick(View view) throws JSONException {
        login_request(((EditText)findViewById(R.id.loginField)).getText().toString(),((EditText)findViewById(R.id.passwordField)).getText().toString());
    }
}