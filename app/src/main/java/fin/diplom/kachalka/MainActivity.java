package fin.diplom.kachalka;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

    static Context ctx;
    ActivityMainBinding binding;
    NewsFragment newsfragment;
    NotebookFragment notebookfragment;
    Fragment3 fragment3;
    Fragment4 fragment4;
    Fragment5 fragment5;
    static String authToken = "b4219c9acf9784f39842497a4c3ae8463252cc02";
    static String basic_url = "http://10.0.2.2:1488/pract/";

//    public static Context getAppContext() {
//        return ctx;
//    }

    public static void handle_response(int error_code){
        String response_text;
        switch (error_code){
            case 400:
                response_text = "Invalid credentials";
                break;
            case 401:
                response_text = "Unauthorized request";
                break;
            default:
                response_text = "Error code: "+ error_code;
                break;
        }
        Toast.makeText(ctx,
                response_text, Toast.LENGTH_SHORT).show();
    }

    public  void login_request(String login, String password) throws JSONException {
        String url = basic_url + "login/";

        JSONObject  credentials = new JSONObject (){{
            put("username",login);
            put("password",password);
        }};

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, credentials, response -> {
            try {
                authToken = response.getString("token");
                ctx.getSharedPreferences("AuthPrefs", MODE_PRIVATE).edit().putString("authToken", authToken).apply();
//                System.out.println(authToken);
                startApp();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }, error -> handle_response(error.networkResponse.statusCode));
//            @Override
//            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                int mStatusCode = response.statusCode;
//                System.out.println("3 "+mStatusCode);
//                return super.parseNetworkResponse(response);
//            }


        queue.add(jor);
    }

    public static void get_request(Object obj, String last_url, View view, Method fill_view, @Nullable ArrayList<Object> objects){
        String url = basic_url+last_url;

        RequestQueue queue = Volley.newRequestQueue(view.getContext());

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                if (objects != null) {
                    fill_view.invoke(obj, view, response, objects);
                }else {
                    fill_view.invoke(obj, view, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> handle_response(error.networkResponse.statusCode)){
            @Override
            public Map<String,String> getHeaders(){
                return new HashMap<String, String>(){{
                    put("Authorization", "Token "+authToken);
                }};
            }
        };
        queue.add(jor);
    }

    public void startApp(){
        findViewById(R.id.login_layout).setVisibility(View.GONE);
        findViewById(R.id.main_layout).setVisibility(View.VISIBLE);

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
        ctx = getApplicationContext();

        authToken = this.getSharedPreferences("AuthPrefs", MODE_PRIVATE).getString("authToken", null);

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