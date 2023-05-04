package fin.diplom.kachalka;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
//class SchPosOnClickListener implements View.OnClickListener
//{
//
//    Object pos;
////    ScrollView newsFeedContainer;
////    LinearLayout newsFeed,postFeed;
//
//    public SchPosOnClickListener(Object pos){//ScrollView news, Object post) {
////        this.newsFeedContainer = news;
//        this.pos = pos;
////        this.newsFeed = (LinearLayout) news.getChildAt(0);
//    }
//
//    @Override
//    public void onClick(View v)
//    {
////        postFeed = NewsFragment.draw_post(newsFeedContainer.getChildAt(0), post, false);
////        postFeed.removeView(postFeed.getChildAt(postFeed.getChildCount()-1));
////
////        postFeed.setOnClickListener(view -> {
////            ((FrameLayout)newsFeedContainer.getParent()).removeView(((FrameLayout)newsFeedContainer.getParent()).getChildAt(1));
////            newsFeedContainer.setVisibility(View.VISIBLE);
////        });
////        newsFeedContainer.setVisibility(View.GONE);
////        ((FrameLayout)newsFeedContainer.getParent()).addView(postFeed);
//    }
//
//}

public class ScheduleFragment extends Fragment {
    ScheduleFragment sf;
    LinearLayout timeTable;
    FrameLayout singlePosView;
    ScrollView scheduleContainer;
    public ScheduleFragment() {
    }

    public void draw_single_activity(JSONObject response, Object pos){
            singlePosView.setVisibility(View.VISIBLE);
            ((TextView)singlePosView.findViewById(R.id.nameView)).setText((String) ((Map) ((Map)pos).get("activity")).get("name"));
            ((TextView)singlePosView.findViewById(R.id.timeView)).setText(String.format("%s - %s", ((String)((Map) pos).get("startTime")).substring(0,5), ((String)((Map) pos).get("endTime")).substring(0,5)));
            ((TextView)singlePosView.findViewById(R.id.placeView)).setText((String) ((Map) pos).get("place"));
            ((TextView)singlePosView.findViewById(R.id.leaderView)).setText((String) ((Map) ((Map) pos).get("leader")).get("name"));
            ((TextView)singlePosView.findViewById(R.id.spotsView)).setText(String.format("Available spots: %s", ((Double) ((Double) (((Map) pos).get("people_limit")) - (Double) ((Map) pos).get("people_enlisted"))).intValue()));
            ((TextView)singlePosView.findViewById(R.id.descriptionView)).setText((String) ((Map) pos).get("description"));

            Button enrollBtn = singlePosView.findViewById(R.id.enrollBtn);

            if((Double) new Gson().fromJson(String.valueOf(response), HashMap.class).get("enrolled")==0){
                enrollBtn.setText("Enroll");
            }
            else {
                enrollBtn.setText("Cancel");
            }
    }

    public LinearLayout draw_schedule_position(View view, ArrayList<Object> day_objects, String date){
        LinearLayout day = (LinearLayout) LayoutInflater.from(view.getContext()).inflate(R.layout.schedule_day_layout, null, false);
        date = date.split("-")[2]+ " "+view.getResources().getStringArray(R.array.months_list)[Integer.parseInt(date.split("-")[1])-1]+" "+date.split("-")[0];
        ((TextView)day.getChildAt(0)).setText(date);
        if(day_objects.size()==0){
            day.addView(new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                setText("Nothing today");
            }});
            return day;
        }
        for (Object pos:day_objects){
            LinearLayout drawn_pos = (LinearLayout) LayoutInflater.from(view.getContext()).inflate(R.layout.schedule_position_layout, null, false);
            ((TextView)drawn_pos.findViewById(R.id.name)).setText((String) ((Map) ((Map)pos).get("activity")).get("name"));
            ((TextView)drawn_pos.findViewById(R.id.timeStamp)).setText(String.format("%s - %s", ((String)((Map) pos).get("startTime")).substring(0,5), ((String)((Map) pos).get("endTime")).substring(0,5)));
            if((Double) ((Map) pos).get("price")==0){
                ((ImageView)drawn_pos.findViewById(R.id.isFree)).setImageResource(R.drawable.ffa);
            }
            ((TextView)drawn_pos.findViewById(R.id.availableSpots)).setText(String.valueOf (((Double) ((Double) (((Map) pos).get("people_limit")) - (Double) ((Map) pos).get("people_enlisted"))).intValue()));
            ((TextView)drawn_pos.findViewById(R.id.location)).setText((String) ((Map) pos).get("place"));
            ((TextView)drawn_pos.findViewById(R.id.leader)).setText((String) ((Map) ((Map) pos).get("leader")).get("name"));

            drawn_pos.setOnClickListener(view1 -> {
                try {
                    Method draw_single_activity = ScheduleFragment.class.getMethod("draw_single_activity", JSONObject.class, Object.class);
                    MainActivity.post_request(view1, new JSONObject(){{put("id",((Map)pos).get("id"));}}, "check_for_appointment",null, draw_single_activity,sf, pos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            day.addView(drawn_pos);
        }
        return day;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fill_schedule(View view, JSONObject response){
        ArrayList schedule_response = (ArrayList) new Gson().fromJson(String.valueOf(response), HashMap.class).get("schedule");

        HashMap<String, ArrayList<Object>> schedule = new HashMap<>();

        LocalDate weekday = LocalDate.parse((String) new Gson().fromJson(String.valueOf(response), HashMap.class).get("weekday"));
        LocalDate next_weekday = LocalDate.parse((String) new Gson().fromJson(String.valueOf(response), HashMap.class).get("next_weekday"));

        for (LocalDate date = weekday; date.isBefore(next_weekday); date = date.plusDays(1)) {
            schedule.put(date.toString(),new ArrayList<>());
        }

        for(Object pos:schedule_response){
            schedule.get(((Map)pos).get("date")).add(pos);
        }
        System.out.println(schedule);

        for (LocalDate date = weekday; date.isBefore(next_weekday); date = date.plusDays(1)) {
            timeTable.addView(draw_schedule_position(view, schedule.get(date.toString()), date.toString()));
        }

//        System.out.println(scheduleContainer.getMeasuredHeight()+" "+timeTable.getMeasuredHeight());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sf = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_fragment, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)  {
        AtomicInteger offset = new AtomicInteger(0);
        AtomicInteger lastBottom = new AtomicInteger();
        Method fill_schedule = null;
        scheduleContainer = view.findViewById(R.id.scheduleContainer);
        timeTable = view.findViewById(R.id.timeTableLayout);
        singlePosView = view.findViewById(R.id.singlePos);
        singlePosView.setVisibility(View.GONE);

        try {
            fill_schedule = ScheduleFragment.class.getMethod("fill_schedule", View.class, JSONObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayout timeTable = view.findViewById(R.id.timeTableLayout);


        MainActivity.get_request(sf, "return_schedule", view, fill_schedule, null, new JSONObject(){{
            try {
                put("now", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()));
                put("offset",offset);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }});

//        offset.updateAndGet(v->v+1);
//        MainActivity.get_request(sf, "return_schedule", view, fill_schedule, null, new JSONObject(){{
//            try {
//                put("now", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()));
//                put("offset",offset);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }});


        Method finalFill_schedule1 = fill_schedule;
        scheduleContainer.getViewTreeObserver()
                .addOnScrollChangedListener(() -> {
                    if ((timeTable.getBottom() <= (scheduleContainer.getHeight() + scheduleContainer.getScrollY())) && timeTable.getBottom()> lastBottom.get()) {
                        offset.updateAndGet(v -> v + 2);
                        lastBottom.set(timeTable.getBottom());
                        MainActivity.get_request(sf, "return_schedule", view, finalFill_schedule1,null, new JSONObject(){{
                            try {
                                put("now", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()));
                                put("offset",offset);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }});
                    }
                });

        singlePosView.findViewById(R.id.closeBtn).setOnClickListener(view1 -> {
            singlePosView.setVisibility(View.GONE);
        });

//        System.out.println(scheduleContainer.getHeight()+" "+timeTable.getHeight());
    }
}