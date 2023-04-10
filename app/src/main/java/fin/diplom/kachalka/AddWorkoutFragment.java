package fin.diplom.kachalka;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class StatsOnClickListener implements View.OnClickListener
{

    int exerciseIndex;
    String who;

    public StatsOnClickListener(int exerciseIndex, String who) {
        this.exerciseIndex=exerciseIndex;
        this.who=who;
    }

    @Override
    public void onClick(View view12)
    {
        LinearLayout parent = (LinearLayout) view12.getParent();
        view12.setVisibility(View.GONE);
        parent.addView(new androidx.appcompat.widget.AppCompatEditText(view12.getContext()){{
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            setInputType(InputType.TYPE_CLASS_NUMBER);
            requestFocus();
            setOnFocusChangeListener((v1, hasFocus) -> {
                if(!hasFocus){
//                    if(AddWorkoutFragment.exercisesStats.get(exerciseIndex)==null){
//                        AddWorkoutFragment.exercisesStats.add(new HashMap<>());
//                    }
                    if(!((EditText) v1).getText().toString().equals("")){
                        ((TextView) view12).setText(((EditText) v1).getText());

                        ((HashMap)AddWorkoutFragment.exercisesStats.get(exerciseIndex)).put(who, ((EditText) v1).getText());

                    }else{
                        AddWorkoutFragment.exercisesStats.add(new HashMap(){{
                            ((HashMap)AddWorkoutFragment.exercisesStats.get(exerciseIndex)).put(who, 0);
                        }});
                    }

                    view12.setVisibility(VISIBLE);
                    parent.removeViewAt(2);
                }
            });
        }});
    }
}

public class AddWorkoutFragment extends Fragment {
    CalendarView calendar;
    AddWorkoutFragment adf;
    LinearLayout allExercisesView;
    LinearLayout exercisesView;
    ArrayList receivedExercises;
    ArrayList<Object> exercisesToAdd;
    ArrayList<Object> exercisesObjects;
    HashMap<String,LinearLayout> exerciseSuggestionsViews;
//    static HashMap<Object, Object> exerciseStats;
    static ArrayList<HashMap<String, String>> exercisesStats;

    public AddWorkoutFragment() {
    }

    public void setPicker(TextView picker, View view){
        TimePicker tp = new TimePicker(view.getContext(), null, R.style.Temka){{
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {{
                weight = 1.0f;
            }});
            setIs24HourView(true);
            setHour(Integer.parseInt(((String)(picker.getText())).split(":")[0]));
            setMinute(Integer.parseInt(((String)(picker.getText())).split(":")[1]));

        }};


        picker.setOnClickListener(view12 -> {
            if(((LinearLayout) picker.getParent()).getChildAt(1)!=null){
                ((LinearLayout) picker.getParent()).removeView(((LinearLayout) picker.getParent()).getChildAt(1));
            }else {
                ((LinearLayout) picker.getParent()).addView(tp);
            }
        });

        tp.setOnTimeChangedListener((timePicker, i, i1) ->{
            if(i1<10){
                picker.setText(MessageFormat.format("{0}:0{1}", i, i1));
                return;
            }
            picker.setText(i+":"+i1);
        });
    }

    public LinearLayout drawExercises(View view, Map exercise){
        return new LinearLayout(view.getContext()){{
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                weight = 1.0f;
            }});
            setPadding(10,10,10,0);
            setOrientation(HORIZONTAL);
            addView(new LinearLayout(view.getContext()){{
                setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT){{
                    weight = 1.0f;
                }});
                setOrientation(VERTICAL);
                addView(new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT){{
                        weight = 1.0f;
                    }});
                    setText(String.valueOf(exercise.get("name")));
                    setTextSize(20);
                }});
                addView(new LinearLayout(view.getContext()){{
                    setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {{
                        weight = 1.0f;
                    }});
                    setOrientation(HORIZONTAL);
                }});
            }});
            addView(new androidx.appcompat.widget.AppCompatCheckBox(view.getContext()){{
                setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                    weight = 1.0f;
                }});
                setText("");
                setGravity(Gravity.END);
                setLayoutDirection(LAYOUT_DIRECTION_RTL);
            }});
            setOnClickListener(view1 -> {
                if(((CheckBox)getChildAt(1)).isChecked()) {
                    ((CheckBox) getChildAt(1)).setChecked(false);
                    exercisesToAdd.remove(exercise);
                    return;
                }
                ((CheckBox)getChildAt(1)).setChecked(true);
                exercisesToAdd.add(exercise);

            });
        }};
    }

    public void fillExercises(View view, JSONObject response){
        receivedExercises = (ArrayList) new Gson().fromJson(String.valueOf(response), HashMap.class).get("exercises");

        exercisesToAdd = new ArrayList<>();

        exerciseSuggestionsViews = new HashMap<>();
        for(Object exercise: receivedExercises){
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
            LinearLayout exerciseToDraw = drawExercises(view, (Map)exercise);
            LinearLayout tagsLayout = (LinearLayout) ((LinearLayout) exerciseToDraw.getChildAt(0)).getChildAt(1);

            for(String tag:tags){
                tagsLayout.addView(new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                    setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                        weight = 1.0f;
                    }});
                    setPadding(0,0,5,0);
                    setText(tag);
                    setTextSize(16);
                }});
            }

            allExercisesView.addView(exerciseToDraw);
            exerciseSuggestionsViews.put(String.valueOf(((Map)exercise).get("name")), exerciseToDraw);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)  {
        System.out.println("ss");
        TextView date_picker = view.findViewById(R.id.dateView);
        TextView startTimePicker = view.findViewById(R.id.startTime);
        TextView endTimePicker = view.findViewById(R.id.endTime);
        FrameLayout exerciseListLayout = view.findViewById(R.id.exerciseListLayout);
        allExercisesView = view.findViewById(R.id.exerciseList);
        exercisesView = view.findViewById(R.id.selectedExercisesView);
        exercisesStats = new ArrayList<>();

        ((EditText) view.findViewById(R.id.workoutName)).setText("");
        exerciseListLayout.setVisibility(View.GONE);
        ArrayList<String> current_date = new ArrayList<>(Arrays.asList(new SimpleDateFormat("dd/MM/yyyy/HH/mm").format(new Date()).split("/")));
        date_picker.setText(String.format("%s %s %s", current_date.get(0), getResources().getStringArray(R.array.months_list)[Integer.parseInt(current_date.get(1))-1], current_date.get(2)));
        startTimePicker.setText(String.format("%s:%s", current_date.get(3), current_date.get(4)));
        endTimePicker.setText(String.format("%s:%s", Integer.parseInt(current_date.get(3))+1, current_date.get(4)));

        AtomicReference<String> dateData = new AtomicReference<>(String.format("%s %s %s", current_date.get(2), current_date.get(1), current_date.get(0)));

        Method drawExercises = null;
        try {
            drawExercises = AddWorkoutFragment.class.getMethod("fillExercises", View.class, JSONObject.class);
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
                        dateData.set(String.format("%s %s %s", year, month+1, day));
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

        view.findViewById(R.id.exerciseListBackBtn).setOnClickListener(v -> {
            exerciseListLayout.setVisibility(View.GONE);
            allExercisesView.removeAllViews();
            exercisesToAdd.clear();
        });

        view.findViewById(R.id.acceptExercisesBtn).setOnClickListener(v -> {
            exerciseListLayout.setVisibility(View.GONE);
            allExercisesView.removeAllViews();
            exercisesView.removeAllViews();

            if(exercisesObjects ==null){
                exercisesObjects = new ArrayList<>(exercisesToAdd);
            }else{
                exercisesObjects.addAll(exercisesToAdd);
            }


//            View.OnClickListener onEditViewClick = view12 -> {
//                LinearLayout parent = (LinearLayout) view12.getParent();
//                view12.setVisibility(View.GONE);
//                parent.addView(new androidx.appcompat.widget.AppCompatEditText(view12.getContext()){{
//                    setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                    setInputType(InputType.TYPE_CLASS_NUMBER);
//                    requestFocus();
//                    setOnFocusChangeListener((v1, hasFocus) -> {
//                        if(!hasFocus){
//                            if(!((EditText) v1).getText().toString().equals("")){
//                                ((TextView) view12).setText(((EditText) v1).getText());
//                            }
//
//                            view12.setVisibility(VISIBLE);
//                            parent.removeViewAt(2);
//                        }
//                    });
//                }});
//            };

//            for(Object exercise: exercisesObjects){
            for(int i=0; i<exercisesObjects.size(); i++){
                Object exercise = exercisesObjects.get(i);
                if(exercisesStats.size()<=i){
                    exercisesStats.add(new HashMap(){{
                        put("w",0);
                        put("r",0);
                    }});
                }

                System.out.println(exercisesStats);

                int finalI = i;
                TextView weightView = new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                    setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                        weight = 1.0f;
                    }});
                    setText(((Map)exercisesStats.get(finalI)).get("w").toString());

                    setOnClickListener(new StatsOnClickListener(finalI, "w"));
                }};

                TextView repsView = new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                    setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                        weight = 1.0f;
                    }});
                    setText(((Map)exercisesStats.get(finalI)).get("r").toString());
                    setOnClickListener(new StatsOnClickListener(finalI, "r"));
                }};

                LinearLayout drawnExercise = drawExercises(view, (Map)exercise);
                drawnExercise.removeViewAt(1);
                drawnExercise.setOnClickListener(null);

                exercisesView.addView(drawnExercise);

                LinearLayout controlsLayout = (LinearLayout) ((LinearLayout) drawnExercise.getChildAt(0)).getChildAt(1);
                controlsLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                controlsLayout.addView(new LinearLayout(view.getContext()){{
                    setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                        weight = 1.0f;
                    }});
                    setOrientation(HORIZONTAL);
                    addView(new LinearLayout(view.getContext()){{
                        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                            weight = 1.0f;
                        }});
                        setOrientation(VERTICAL);

                        addView(new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                                weight = 1.0f;
                            }});
                            setText("Weight");
                        }});

                        addView(weightView);
                    }});
                    addView(new LinearLayout(view.getContext()){{
                        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                            weight = 1.0f;
                        }});
                        setOrientation(VERTICAL);

                        addView(new androidx.appcompat.widget.AppCompatTextView(view.getContext()){{
                            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                                weight = 1.0f;
                            }});
                            setText("Reps");
                        }});

                        addView(repsView);
                    }});
                    addView(new androidx.appcompat.widget.AppCompatButton(view.getContext()){{
                        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT){{
                            weight = 1.0f;
                        }});
                        setText("Delete");

                        setOnClickListener(view13 -> {

                            exercisesObjects.remove(exercisesView.indexOfChild(drawnExercise));
                            exercisesStats.remove(exercisesView.indexOfChild(drawnExercise));
                            exercisesView.removeView(drawnExercise);
                        });
                    }});
                }});
            }
        });

        ((EditText)view.findViewById(R.id.exerciseSearchInput)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(exerciseSuggestionsViews ==null){
                    return;
                }
                for(String exName: exerciseSuggestionsViews.keySet()){
                    if(!exName.toLowerCase(Locale.ROOT).contains(charSequence.toString().toLowerCase(Locale.ROOT))){
                        exerciseSuggestionsViews.get(exName).setVisibility(View.GONE);
                    }else {
                        exerciseSuggestionsViews.get(exName).setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        view.findViewById(R.id.commitBtn).setOnClickListener(v -> {
            ArrayList<String> endTime = new ArrayList<>(Arrays.asList(((String) endTimePicker.getText()).split(":")));
            ArrayList<String> startTime = new ArrayList<>(Arrays.asList(((String) startTimePicker.getText()).split(":")));
            if(((EditText) view.findViewById(R.id.workoutName)).getText().toString().equals("")){
                ((EditText) view.findViewById(R.id.workoutName)).setText("Workout");
            }

            int length = 0;
            if(Integer.parseInt(startTime.get(1))*60+Integer.parseInt(startTime.get(0))*3600>Integer.parseInt(endTime.get(1))*60+Integer.parseInt(endTime.get(0))*3600){
                length = Integer.parseInt(endTime.get(1))*60+Integer.parseInt(endTime.get(0))*3600 - Integer.parseInt(startTime.get(1))*60-Integer.parseInt(startTime.get(0))*3600+24*3600;
            }else{
                length = Integer.parseInt(endTime.get(1))*60+Integer.parseInt(endTime.get(0))*3600 - Integer.parseInt(startTime.get(1))*60-Integer.parseInt(startTime.get(0))*3600;
            }

            int finalLength = length;
            HashMap workoutData = new HashMap(){{
                try {
                    put("name",((EditText)view.findViewById(R.id.workoutName)).getText().toString());
                    put("date", dateData);
                    put("length", String.valueOf(finalLength));
                    put("exercises", new ArrayList(){{
                        for(int i = 0; i < exercisesObjects.size(); i++){
                            int finalI = i;
                            add(new HashMap(){{
                                put("activity",((Map) exercisesObjects.get(finalI)).get("id"));
                                put("weight",String.valueOf(exercisesStats.get(finalI).get("w")));
                                put("reps",String.valueOf(exercisesStats.get(finalI).get("r")));
                            }});
                        }
                    }});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }};
            exercisesObjects.clear();

            JSONObject requestData = new JSONObject(workoutData);
//            System.out.println(workoutData);
//            System.out.println(requestData);
            MainActivity.postWorkout_request(view, requestData);
        });
    }
}