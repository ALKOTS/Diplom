package fin.diplom.kachalka;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewsFragment extends Fragment {

    NewsFragment nf;


    public NewsFragment() {
    }

    public static void fill_news(View view, JSONObject response){
        ArrayList news = (ArrayList) new Gson().fromJson(String.valueOf(response), HashMap.class).get("news");
        ScrollView newsFeed = view.findViewById(R.id.feed);
        for(Object post:news){
            LinearLayout drawn_post = new LinearLayout(view.getContext()){{
                setOrientation(LinearLayout.VERTICAL);
                setPadding(20,10,20,10);
                setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                addView(new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                    setPadding(0,0,0,10);
                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    setText((String)((Map)post).get("date"));
                    setTextSize(15);
                }});
                addView(new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                    setPadding(0,0,0,10);
                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    setText((String)((Map)post).get("title"));
                    setTextSize(40);
                }});
                addView(new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                    setPadding(0,0,0,10);
                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    setText((String)((Map)post).get("sub_title"));
                    setTextSize(25);
                }});
                addView(new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                    setPadding(0,0,0,10);
                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    setText(         ((String)((Map)post).get("text")).length()>50?((String)((Map)post).get("text")).substring(0,80)+"...":((String)((Map)post).get("text"))                );
                    setTextSize(17);
                }});
                addView(new androidx.appcompat.widget.AppCompatImageView(view.getContext()){{
                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    setGravity(Gravity.END);
                    setImageResource(R.drawable.arrow);
                }});
            }};
            ((LinearLayout)newsFeed.getChildAt(0)).addView(drawn_post);
        }
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

        ScrollView newsFeed = view.findViewById(R.id.feed);

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.months_list));

//        newsFeed.setAdapter(adapter);

        newsFeed.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (newsFeed.getChildAt(0).getBottom()
                                <= (newsFeed.getHeight() + newsFeed.getScrollY())) {
                            System.out.println("Bottom");
                        } else {
                            System.out.println("Suck shit");
                        }
                    }
                });
        MainActivity.get_request(nf, "return_news", view, fill_news, null);
    }
}