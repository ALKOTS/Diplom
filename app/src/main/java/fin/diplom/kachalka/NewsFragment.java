package fin.diplom.kachalka;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class PostOnClickListener implements View.OnClickListener
{

    Object post;
    ScrollView newsFeedContainer;
    LinearLayout newsFeed,postFeed;

    public PostOnClickListener(ScrollView news, Object post) {
        this.newsFeedContainer = news;
        this.post = post;
        this.newsFeed = (LinearLayout) news.getChildAt(0);
    }

    @Override
    public void onClick(View v)
    {
        postFeed = NewsFragment.draw_post(newsFeedContainer.getChildAt(0), post, false);
        postFeed.removeView(postFeed.getChildAt(postFeed.getChildCount()-1));

        postFeed.setOnClickListener(view -> {
            ((FrameLayout)newsFeedContainer.getParent()).removeView(((FrameLayout)newsFeedContainer.getParent()).getChildAt(1));
            newsFeedContainer.setVisibility(View.VISIBLE);
        });
        newsFeedContainer.setVisibility(View.GONE);
        ((FrameLayout)newsFeedContainer.getParent()).addView(postFeed);
    }

}


public class NewsFragment extends Fragment {

    NewsFragment nf;
    static ScrollView newsFeedContainer;

    static int news_drawn;

    public NewsFragment() {
    }

    public static LinearLayout draw_post(View view, Object post, Boolean forMenu){
        String text = ((String)((Map)post).get("text")).length()>50?((String)((Map)post).get("text")).substring(0,80)+"...":((String)((Map)post).get("text"));
        String date = ((String)((Map)post).get("date"));
        date = date.split("-")[2]+ " "+view.getResources().getStringArray(R.array.months_list)[Integer.parseInt(date.split("-")[1])-1]+" "+date.split("-")[0];
        if(!forMenu){
            text = (String)((Map)post).get("text");
        }
        LinearLayout postLayout = (LinearLayout) LayoutInflater.from(view.getContext()).inflate(R.layout.post_layout, null, false);

        ((TextView)postLayout.getChildAt(0)).setText(date);
        ((TextView)postLayout.getChildAt(1)).setText((String)((Map)post).get("title"));
        ((TextView)postLayout.getChildAt(2)).setText((String)((Map)post).get("sub_title"));
        ((TextView)postLayout.getChildAt(3)).setText((String)((Map)post).get(text));

        return postLayout;
    }

    public static void fill_news(View view, JSONObject response){
        ArrayList news = (ArrayList) new Gson().fromJson(String.valueOf(response), HashMap.class).get("news");

        for(Object post:news){
            LinearLayout drawn_post = draw_post(view, post, true);
            drawn_post.setOnClickListener(new PostOnClickListener(newsFeedContainer, post));
            ((LinearLayout)newsFeedContainer.getChildAt(0)).addView(drawn_post);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nf = this;
        news_drawn = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Method fill_news = null;
        AtomicInteger lastBottom = new AtomicInteger();

        try {
            fill_news = NewsFragment.class.getMethod("fill_news", View.class, JSONObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        newsFeedContainer = view.findViewById(R.id.feed);

        Method finalFill_news = fill_news;

        MainActivity.get_request(nf, "return_news/"+news_drawn, view, fill_news, null);

        newsFeedContainer.getViewTreeObserver()
                .addOnScrollChangedListener(() -> {
                    if ((newsFeedContainer.getChildAt(0).getBottom() <= (newsFeedContainer.getHeight() + newsFeedContainer.getScrollY())) && newsFeedContainer.getChildAt(0).getBottom()> lastBottom.get()) {
                        news_drawn+=10;
                        lastBottom.set(newsFeedContainer.getChildAt(0).getBottom());
                        MainActivity.get_request(nf, "return_news/" + news_drawn, view, finalFill_news, null);
                    }
                });
    }
}