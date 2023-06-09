package fin.diplom.kachalka;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NotebookFragment extends Fragment  {

    Spinner year_spinner;
    Spinner month_spinner;
    ArrayList<String> months;
    ArrayList<String> years;
    LinearLayout workouts_layout;
    NotebookFragment nf;



    public void create_spinner(View view, Spinner s, ArrayList<String> al){

        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, al);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    public void fill_year_spinner(View view, JSONObject response, ArrayList<Object> objects){
        years = new ArrayList<>();

        for(Double el:(ArrayList<Double>) new Gson().fromJson(String.valueOf(response), HashMap.class).get("years")){
            years.add(String.valueOf(el).substring(0,4));
        }

        create_spinner(view, (Spinner) objects.get(0), years);
    }

    public void fill_month_spinner(View view, JSONObject response, ArrayList<Object> objects){
        months = new ArrayList<>();

        for(Double el:(ArrayList<Double>) new Gson().fromJson(String.valueOf(response), HashMap.class).get("months")){
            months.add(getResources().getStringArray(R.array.months_list)[(int) Math.round(el)-1]);
        }

        create_spinner(view, (Spinner) objects.get(0), months);
    }

    public void fill_workouts(View view, JSONObject response){

        ArrayList workouts = (ArrayList) new Gson().fromJson(String.valueOf(response), HashMap.class).get("workouts");


        HashMap<Integer,ArrayList<Map>> days = new HashMap<>();

        for(Object workout:workouts){
            int day =  Integer.parseInt ((String) ((Map)workout).get("day"));
            days.computeIfAbsent(day, k -> new ArrayList<>());
            days.get(day).add((Map) workout);
        }

        for(Integer day:days.keySet()){
            workouts_layout.addView(new LinearLayout(view.getContext()){{
                setOrientation(LinearLayout.VERTICAL);
                setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.border));

                setLayoutParams(new MarginLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)){{
                    setMargins(5,5,5,5);
                }});
            }});
            draw_workouts(view, days.get(day), workouts_layout);
        }
    }

    public void draw_workouts(View view, ArrayList<Map> workout_day, LinearLayout workouts_layout){
        for(Map workout:workout_day){
            LinearLayout l = (LinearLayout) LayoutInflater.from(view.getContext()).inflate(R.layout.workout_day_layout, null, false);
            ((TextView)l.findViewById(R.id.workoutNameView)).setText((String)workout.get("name"));
            ((TextView)l.findViewById(R.id.workoutDateView)).setText(MessageFormat.format("{0}, {1}", workout.get("day"), getResources().getStringArray(R.array.months_list)[((Double) workout.get("month")).intValue() - 1]));

//            LinearLayout miscInfo = (LinearLayout) l.getChildAt(2);
            ((TextView)l.findViewById(R.id.workoutLengthView)).setText(String.format("%s - %s", ((String)workout.get("startTime")).substring(0,5), ((String)workout.get("endTime")).substring(0,5)));//new DecimalFormat("#.00").format(Float.parseFloat(String.valueOf(workout.get("length"))) / 60 / 60));

//            LinearLayout exercisesInfo = (LinearLayout) l.getChildAt(3);

            for(Object ex:(ArrayList)workout.get("exercises")){
                Map x = (Map)ex;
                ((LinearLayout)l.findViewById(R.id.workoutSetsView)).addView(new LinearLayout(view.getContext()){{
                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    setOrientation(LinearLayout.HORIZONTAL);

                    addView(new AppCompatTextView(view.getContext()){{
                        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT){{
                            weight = 1.0f;
                        }});
                        setText((String) ((Map)x.get("activity")).get("name"));
                    }});

                    addView(new AppCompatTextView(view.getContext()){{
                        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT){{
                            weight = 1.0f;
                        }});
                        setText(String.format("%skg x %s", x.get("weight"), x.get("reps")));
                    }});
                }});
            }
            l.findViewById(R.id.deleteButton).setOnClickListener(view1 -> new AlertDialog.Builder(view1.getContext())
                    .setTitle("Delete workout")
                    .setMessage("Are you sure you want to delete this workout?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        System.out.println(workout.get("id"));
//                                    ((LinearLayout)workouts_layout.getChildAt(workouts_layout.getChildCount()-1)).removeView(l);
                        l.setVisibility(View.GONE);
                        try {
                            MainActivity.post_request(view1, new JSONObject().put("id",workout.get("id")), "remove_workout/", "Success", null, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show());
            ((LinearLayout)workouts_layout.getChildAt(workouts_layout.getChildCount()-1)).addView(l);

        }
    }

    public NotebookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nf = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notebook_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Method fill_year_spinner = null;
        try {
            fill_year_spinner = NotebookFragment.class.getMethod("fill_year_spinner", View.class, JSONObject.class, ArrayList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


        year_spinner = view.findViewById(R.id.year_spinner);
        month_spinner = view.findViewById(R.id.month_spinner);
        workouts_layout = view.findViewById(R.id.workouts_layout);

        MainActivity.get_request(nf, "return_workout_years", view, fill_year_spinner, new ArrayList<Object>(){{add(year_spinner);}},null);

        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int pos, long id) {
                Method fill_month_spinner = null;
                try {
                    fill_month_spinner = NotebookFragment.class.getMethod("fill_month_spinner", View.class, JSONObject.class, ArrayList.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MainActivity.get_request(nf, "return_workout_months/"+years.get(pos), view, fill_month_spinner, new ArrayList<Object>(){{add(month_spinner);}},null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int pos, long l) {
                workouts_layout.removeAllViews();
                int year = Integer.parseInt ((String) year_spinner.getSelectedItem());
                int month = Arrays.asList(getResources().getStringArray(R.array.months_list)).indexOf(months.get(pos))+1;

                Method fill_workouts = null;
                try {
                    fill_workouts = NotebookFragment.class.getMethod("fill_workouts", View.class, JSONObject.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MainActivity.get_request(nf, "return_workouts/"+year+"/"+month, view, fill_workouts, null, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}