package fin.diplom.kachalka;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class NewsFragment extends Fragment {

    NewsFragment nf;

    public NewsFragment() {
    }

    public static void fill_news(View view, JSONObject response){
        TextView tv = view.findViewById(R.id.textView);
        tv.setText( response.toString());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nf = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Method fill_news = null;

        try {
            fill_news = NewsFragment.class.getMethod("fill_news", View.class, JSONObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MainActivity.get_request(nf, "return_news", view, fill_news, null);
    }
}