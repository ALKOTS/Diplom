package fin.diplom.kachalka;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public class NotebookFragment extends Fragment  {

    Spinner year_spinner;
    Spinner month_spinner;
    ArrayList<String> months;
    ArrayList<String> years;
    LinearLayout workouts_layout;
    //    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private String mParam1;
//    private String mParam2;
    private HashMap<Integer, ArrayList<Integer>> calendar;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer,ArrayList<HashMap<String, String>>>>> workouts;
    private HashMap<String, ArrayList<ArrayList<HashMap<String, String>>>> exercises;


    public void create_spinner(View view, Spinner s, ArrayList<String> al){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, al);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    public void fill_workouts(Integer day, String month, View view, ArrayList<HashMap<String, String>> workout_day, ArrayList<ArrayList<HashMap<String, String>>> exercises, LinearLayout workouts_layout){
        System.out.println(workout_day);
        for(HashMap<String, String> workout:workout_day){
            LinearLayout l = new LinearLayout(view.getContext()){{
                setOrientation(LinearLayout.VERTICAL);
                setPadding(10,10,10,10);
                setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                setDividerDrawable(getResources().getDrawable(R.style.Divider_Horizontal) );

                addView(new AppCompatTextView(view.getContext()){{
                    setPadding(0,0,0,15);

                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    setText(workout.get("Name"));
                }});

                addView(new AppCompatTextView(view.getContext()){{
                    setPadding(0,0,0,10);
                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    setText(day+ ", "+month);
                }});

                addView(new LinearLayout(view.getContext()){{

                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    setOrientation(LinearLayout.HORIZONTAL);
                    setPadding(0,0,0,10);

                    addView(new AppCompatTextView(view.getContext()){{
                        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                            weight = 1.0f;
                        }});
                        setText(workout.get("Length"));
                    }});

                    addView(new AppCompatTextView(view.getContext()){{
                        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                            weight = 1.0f;
                        }});
                        setText("TODO total weight");
                    }});

                    addView(new AppCompatTextView(view.getContext()){{
                        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT){{
                            weight = 1.0f;
                        }});
                        setText(workout.get("Personal_highscores_amount"));
                    }});
                }});

                addView(new LinearLayout(view.getContext()){{
                    setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    setOrientation(LinearLayout.VERTICAL);

                    addView(new LinearLayout(view.getContext()){{
                        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        setOrientation(LinearLayout.HORIZONTAL);

                        addView(new AppCompatTextView(view.getContext()){{
                            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            setText("Exercise");
                        }});
                        addView(new AppCompatTextView(view.getContext()){{
                            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            setText("Sets");
                        }});
                    }});

                    for(HashMap<String, String> x:exercises.get(Integer.parseInt(workout.get("Exercise_id")))){
                        addView(new LinearLayout(view.getContext()){{
                            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            setOrientation(LinearLayout.HORIZONTAL);

                            addView(new AppCompatTextView(view.getContext()){{
                                setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT){{
                                    weight = 1.0f;
                                }});
                                setText(x.get("Name"));
                            }});

                            addView(new AppCompatTextView(view.getContext()){{
                                setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT){{
                                    weight = 1.0f;
                                }});
                                setText(x.get("Weights")+"kg x "+x.get("Reps"));
                            }});
                        }});
                    }

                }});
            }};

            ((LinearLayout)workouts_layout.getChildAt(workouts_layout.getChildCount()-1)).addView(l);

        }
    }

    public NotebookFragment() {
    }

//    public static NotebookFragment newInstance(String param1, String param2) {
//        NotebookFragment fragment = new NotebookFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        //Save the fragment's state here
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (savedInstanceState != null) {
//            //Restore the fragment's state here
//        }

        if (getArguments() != null) {
            workouts = (HashMap<Integer, HashMap<Integer, HashMap<Integer,ArrayList<HashMap<String, String>>>>>)(getArguments().getSerializable("Workouts"));
            exercises = (HashMap<String, ArrayList<ArrayList<HashMap<String, String>>>>)(getArguments().getSerializable("Exercises"));
        }

        calendar = new HashMap<>();
        for(int year:workouts.keySet()){
            calendar.put(year, new ArrayList<>());
            for (int month:workouts.get(year).keySet()){
                calendar.get(year).add(month);
            }
        }



        years = new ArrayList<>();
        months = new ArrayList<>();

        for ( Integer key : calendar.keySet() ) {
            years.add(key.toString());
        }
        Collections.sort(years);
        Collections.reverse(years);

        for (Integer month : calendar.get(Integer.parseInt(years.get(0)))){
            months.add(getResources().getStringArray(R.array.months_list)[month-1]);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notebook_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        year_spinner = view.findViewById(R.id.year_spinner);
        month_spinner = view.findViewById(R.id.month_spinner);
        workouts_layout = view.findViewById(R.id.workouts_layout);

        create_spinner(view, year_spinner, years);

        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int pos, long id) {
                months.clear();

                Collections.sort(calendar.get(Integer.parseInt(years.get(pos))));
                Collections.reverse(calendar.get(Integer.parseInt(years.get(pos))));

                for (Integer month : calendar.get(Integer.parseInt(years.get(pos)))){
                    months.add(getResources().getStringArray(R.array.months_list)[month-1]);
                }

                create_spinner(view, month_spinner, months);
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
                int month = calendar.get(year).get(pos);

//                System.out.println(workouts+"\n________________________________________________");
                if(workouts.keySet().contains(year) && workouts.get(year).containsKey(month)){
                    for(Integer day : workouts.get(year).get(month).keySet()){
                        workouts_layout.addView(new LinearLayout(view.getContext()){{
                           setOrientation(LinearLayout.VERTICAL);
                            setBackground(getResources().getDrawable(R.drawable.border));


                            setLayoutParams(new MarginLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)){{
                                setMargins(5,5,5,5);
                            }});
                        }});


//                        System.out.println(workouts.get(year).get(month).get(day)+"\n______________________________________________");
                        fill_workouts(day, getResources().getStringArray(R.array.months_list)[month-1], view, workouts.get(year).get(month).get(day), exercises.get("Exercises"), workouts_layout);
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
}