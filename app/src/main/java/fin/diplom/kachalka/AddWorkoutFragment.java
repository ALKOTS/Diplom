package fin.diplom.kachalka;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddWorkoutFragment extends Fragment {
    CalendarView calendar;
    AddWorkoutFragment adf;
    LinearLayout exerciseList;

    public AddWorkoutFragment() {
    }

    public void setPicker(TextView picker, View view){
        TimePicker tp = new TimePicker(view.getContext(), null, R.style.Temka){{
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {{
                weight = 1.0f;
            }});
            setIs24HourView(true);
        }};


        picker.setOnClickListener(view12 -> {
            if(((LinearLayout) picker.getParent()).getChildAt(1)!=null){
                ((LinearLayout) picker.getParent()).removeView(((LinearLayout) picker.getParent()).getChildAt(1));
            }else {
                ((LinearLayout) picker.getParent()).addView(tp);
            }
        });

        tp.setOnTimeChangedListener((timePicker, i, i1) -> picker.setText(i+":"+i1));
    }

    public void drawExercises(View view, JSONObject response){
        ArrayList exercises = (ArrayList) new Gson().fromJson(String.valueOf(response), HashMap.class).get("exercises");
        for(Object exercise:exercises){
//            System.out.println(exercise);
            ArrayList<String> tags = new ArrayList<>();

            for(Object tag:((Map)exercise).keySet()){
                if(
                        !String.valueOf(tag).equals("is_group")
                        && !String.valueOf(tag).equals("is_competition")
                        && !String.valueOf(tag).equals("is_exercise")
                        && Objects.equals(((Map) exercise).get(String.valueOf(tag)),true)
                ){
                    tags.add(String.valueOf(tag));
                }
            }
            System.out.println(tags);

            exerciseList.addView(new LinearLayout(view.getContext()){{
                setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                    weight = 1.0f;
                }});
                setPadding(10,10,10,0);
                setOrientation(VERTICAL);
                addView(new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                        weight = 1.0f;
                    }});
                    setPadding(0,0,0,10);
                    setText(String.valueOf(((Map)exercise).get("name")));
                    setTextSize(20);
                }});
                addView(new LinearLayout(view.getContext()){{
                    setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT) {{
                        weight = 1.0f;
                    }});
                    setOrientation(HORIZONTAL);
                    for(String tag:tags){
                        addView(new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                                weight = 1.0f;
                            }});
                            setPadding(0,0,5,0);
                            setText(tag);
                            setTextSize(16);
                        }});
                    }
                }});
            }});
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adf = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_workout_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView date_picker = view.findViewById(R.id.dateView);
        TextView startTimePicker = view.findViewById(R.id.startTime);
        TextView endTimePicker = view.findViewById(R.id.endTime);
        FrameLayout exerciseListLayout = view.findViewById(R.id.exerciseListLayout);
        exerciseList = view.findViewById(R.id.exerciseList);

        exerciseListLayout.setVisibility(View.GONE);

        Method drawExercises = null;
        try {
            drawExercises = AddWorkoutFragment.class.getMethod("drawExercises", View.class, JSONObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Method finalDrawExercises = drawExercises;
        view.findViewById(R.id.addExerciseBtn).setOnClickListener(view1 -> {
            exerciseListLayout.setVisibility(View.VISIBLE);
            MainActivity.get_request(adf, "return_exercise_activities", view, finalDrawExercises, null);
        });
        setPicker(startTimePicker, view);
        setPicker(endTimePicker, view);

        calendar = new CalendarView(view.getContext()){{
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {{
                weight = 1.0f;
            }});
            setOnDateChangeListener(
                    (vv, year, month, day) -> {
                        String Date
                                = day + " "
                                + getResources().getStringArray(R.array.months_list)[month] + " " + year;

                        date_picker.setText(Date);
                        ((LinearLayout)vv.getParent()).removeView(vv);
                    });


        }};
        date_picker.setOnClickListener(v -> {
            if(((LinearLayout) date_picker.getParent()).getChildAt(1)!=null){
                ((LinearLayout) date_picker.getParent()).removeView(((LinearLayout) date_picker.getParent()).getChildAt(1));
            }else {
                ((LinearLayout) date_picker.getParent()).addView(calendar);
            }
        });



    }
}