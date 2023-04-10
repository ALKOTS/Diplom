package fin.diplom.kachalka;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

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
    AddWorkoutFragment addWorkoutFragment;
    static ConstraintLayout mainLayout;
    static LinearLayout loginLayout;
    Fragment4 fragment4;
    Fragment5 fragment5;
    static String authToken = "";
    static String basic_url = "http://10.0.2.2:1488/pract/";
    LinearLayout registerLayout;
    static Response.ErrorListener errorListener;

    public static void handle_response(int error_code){
        String response_text;
        switch (error_code){
            case 400:
                response_text = "Invalid credentials";
                break;
            case 401:
                response_text = "Unauthorized request";
                forceLogin();
                break;
            default:
                response_text = "Error code: "+ error_code;
                break;
        }
        Toast.makeText(ctx, response_text, Toast.LENGTH_SHORT).show();
    }

    public void register_request(JSONObject credentials){
        String url = basic_url + "register/";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, credentials, response -> {
            try {
                if(response.getString("response").equals("Success")){
                    login_request(credentials.getString("login"), credentials.getString("password"));
                }else{
                    Map errors = ((Map)new Gson().fromJson(String.valueOf(response), HashMap.class).get("Errors"));
                    for (Object error: errors.keySet()){
                        Toast.makeText(ctx,errors.get(error).toString(), Toast.LENGTH_LONG).show();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }, errorListener);

        queue.add(jor);

    }

    public void login_request(String login, String password) throws JSONException {
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
                startApp();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }, errorListener);

        queue.add(jor);
    }

    public static void postWorkout_request(View view, JSONObject workoutData){
        String url = basic_url + "add_workout/";

        RequestQueue queue = Volley.newRequestQueue(view.getContext());

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, workoutData, response -> {
            try {
                System.out.println(response);
//                if(response.getString("response").equals("Success")){
//                    login_request(credentials.getString("login"), credentials.getString("password"));
//                }else{
//                    Map errors = ((Map)new Gson().fromJson(String.valueOf(response), HashMap.class).get("Errors"));
//                    for (Object error: errors.keySet()){
//                        Toast.makeText(ctx,errors.get(error).toString(), Toast.LENGTH_LONG).show();
//                    }
//
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }, errorListener){
            @Override
            public Map<String,String> getHeaders(){
                return new HashMap<String, String>(){{
                    put("Authorization", "Token "+authToken);
                }};
            }
        };

        queue.add(jor);
    }

    public static void get_request(Object obj, String last_url, View view, Method fill_view, @Nullable ArrayList<Object> objects){
        String url = basic_url+last_url;

        RequestQueue queue = Volley.newRequestQueue(view.getContext());

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                if (objects != null) {
                    fill_view.invoke(obj, view, response, objects);
                } else {
                    fill_view.invoke(obj, view, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, errorListener){
            @Override
            public Map<String,String> getHeaders(){
                return new HashMap<String, String>(){{
                    put("Authorization", "Token "+authToken);
                }};
            }
        };
        queue.add(jor);
    }

    public static void forceLogin(){
        mainLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
    }

    public void startApp(){
        loginLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);

        changeScreen(new NewsFragment());
        newsfragment = new NewsFragment();
        notebookfragment = new NotebookFragment();
        addWorkoutFragment = new AddWorkoutFragment();
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
                    changeScreen(addWorkoutFragment);
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

    @Override //removes focus on touch
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        loginLayout =findViewById(R.id.login_layout);
        mainLayout=findViewById(R.id.main_layout);
        registerLayout= findViewById(R.id.register_layout);
        registerLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.GONE);
        ctx = getApplicationContext();

        errorListener = error -> {
            if(error.networkResponse==null){
                Toast.makeText(ctx,"Servers may be down", Toast.LENGTH_SHORT).show();
                return;
            }
            handle_response(error.networkResponse.statusCode);

        };

        authToken = this.getSharedPreferences("AuthPrefs", MODE_PRIVATE).getString("authToken", null);

        if(authToken == null) {
            forceLogin();
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

    public void onRegisterClick(View view) throws JSONException {
        registerLayout.setVisibility(View.VISIBLE);

        boolean Verifier = true;
        for(int i = 0; i<registerLayout.getChildCount(); i++){
            if(String.valueOf(((EditText) registerLayout.getChildAt(i)).getText()).equals("")){
                Verifier = false;
                break;
            }
        }

        if(!String.valueOf(((EditText) findViewById(R.id.passwordField)).getText()).equals(String.valueOf(((EditText) findViewById(R.id.repeatPasswordField)).getText()))){
            Verifier = false;
            Toast.makeText(this, "Passwords are not equal", Toast.LENGTH_SHORT).show();
        }

        if(Verifier){
            JSONObject values = new JSONObject(){{
                put("login", ((EditText)findViewById(R.id.loginField)).getText());
                put("username", ((EditText)findViewById(R.id.nameField)).getText());
                put("last_name", ((EditText)findViewById(R.id.lastNameField)).getText());
                put("email", ((EditText)findViewById(R.id.emailField)).getText());
                put("password", ((EditText)findViewById(R.id.passwordField)).getText());
            }};
            register_request(values);
        }
    }


}