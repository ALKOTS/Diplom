package fin.diplom.kachalka;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import fin.diplom.kachalka.databinding.ActivityMainBinding;



public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    NewsFragment newsfragment;
    NotebookFragment notebookfragment;
    Fragment3 fragment3;
    Fragment4 fragment4;
    Fragment5 fragment5;



    public static void get_request(Object obj, String last_url, View view, Method fill_view, @Nullable ArrayList<Object> objects){
        String url = "http://10.0.2.2:1488/pract/"+last_url;


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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        queue.add(jor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
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

    private void changeScreen(Fragment screen){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.main_layout,screen,null).commit();

    }
}