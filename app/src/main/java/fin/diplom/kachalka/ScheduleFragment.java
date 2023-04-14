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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ScheduleFragment extends Fragment {
    ScheduleFragment sf;
    LinearLayout timeTable;
    public ScheduleFragment() {
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
//            ((TextView)drawn_pos.getChildAt(0)).setText((String) ((Map) ((Map)pos).get("activity")).get("name"));
            ((TextView)drawn_pos.findViewById(R.id.timeStamp)).setText(String.format("%s - %s", ((String)((Map) pos).get("timeStart")).substring(0,5), ((String)((Map) pos).get("timeFinish")).substring(0,5)));
            if((Boolean) ((Map) pos).get("is_free")){
                ((ImageView)drawn_pos.findViewById(R.id.isFree)).setImageResource(R.drawable.ffa);
            }
            ((TextView)drawn_pos.findViewById(R.id.availableSpots)).setText(String.valueOf (((Double) ((Double) (((Map) pos).get("people_limit")) - (Double) ((Map) pos).get("people_enlisted"))).intValue()));
            ((TextView)drawn_pos.findViewById(R.id.location)).setText((String) ((Map) pos).get("place"));
            ((TextView)drawn_pos.findViewById(R.id.leader)).setText((String) ((Map) ((Map) pos).get("leader")).get("name"));

            day.addView(drawn_pos);
        }
        return day;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fill_schedule(View view, JSONObject response){
        ArrayList schedule_response = (ArrayList) new Gson().fromJson(String.valueOf(response), HashMap.class).get("schedule");

        HashMap<String, ArrayList<Object>> schedule = new HashMap<>();

        LocalDate monday = LocalDate.parse((String) new Gson().fromJson(String.valueOf(response), HashMap.class).get("monday"));
        LocalDate next_monday = LocalDate.parse((String) new Gson().fromJson(String.valueOf(response), HashMap.class).get("next_monday"));

        for (LocalDate date = monday; date.isBefore(next_monday); date = date.plusDays(1)) {
            schedule.put(date.toString(),new ArrayList<>());
        }



        for(Object pos:schedule_response){
            schedule.get(((Map)pos).get("date")).add(pos);
        }
        System.out.println(schedule);

        for (LocalDate date = monday; date.isBefore(next_monday); date = date.plusDays(1)) {
            timeTable.addView(draw_schedule_position(view, schedule.get(date.toString()), date.toString()));
        }

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
        timeTable = view.findViewById(R.id.timeTableLayout);

        try {
            fill_schedule = ScheduleFragment.class.getMethod("fill_schedule", View.class, JSONObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayout timeTable = view.findViewById(R.id.timeTableLayout);

        Method finalFill_schedule = fill_schedule;

        MainActivity.get_request(sf, "return_schedule", view, fill_schedule, null, new JSONObject(){{
            try {
                put("now", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()));
                put("offset",offset);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }});
        ScrollView scheduleContainer = view.findViewById(R.id.scheduleContainer);

        Method finalFill_schedule1 = fill_schedule;
        scheduleContainer.getViewTreeObserver()
                .addOnScrollChangedListener(() -> {
                    if ((scheduleContainer.getChildAt(0).getBottom() <= (scheduleContainer.getHeight() + scheduleContainer.getScrollY())) && scheduleContainer.getChildAt(0).getBottom()> lastBottom.get()) {
                        offset.updateAndGet(v -> v + 7);
                        lastBottom.set(scheduleContainer.getChildAt(0).getBottom());
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




    }
}