package fin.diplom.kachalka;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

import fin.diplom.kachalka.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    NewsFragment newsfragment;
    NotebookFragment notebookfragment;
    Fragment3 fragment3;
    Fragment4 fragment4;
    Fragment5 fragment5;

    HashMap<Integer, HashMap<Integer, HashMap<Integer,ArrayList<HashMap<String, String>>>>> workouts; //year:{month:{day:[{name, length, personal_highscores_amount, exercise_id}]}}
    HashMap<String, ArrayList<ArrayList<HashMap<String, String>>>> exercises; //name, weight, reps


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

        //__________________________________________________________________________________

        workouts = new HashMap<Integer, HashMap<Integer, HashMap<Integer,ArrayList<HashMap<String, String>>>>>(){{
            put(2018, new HashMap<Integer, HashMap<Integer, ArrayList<HashMap<String,String>>>>(){{
                put(1, new HashMap<Integer, ArrayList<HashMap<String, String>>>() {{
                    put(1, new ArrayList<HashMap<String, String>>() {{
                        add(new HashMap<String, String>() {{
                            put("Name", "W1.1");
                            put("Length", "1:00:00");
                            put("Personal_highscores_amount", "TODO");
                            put("Exercise_id", "0");
                        }});
                        add(new HashMap<String, String>() {{
                            put("Name", "W1.2");
                            put("Length", "1:00:00");
                            put("Personal_highscores_amount", "TODO");
                            put("Exercise_id", "0");
                        }});
                    }});
                    put(2, new ArrayList<HashMap<String, String>>() {{
                        add(new HashMap<String, String>() {{
                            put("Name", "W2");
                            put("Length", "1:00:00");
                            put("Personal_highscores_amount", "TODO");
                            put("Exercise_id", "0");
                        }});
                    }});
                }});
                put(2, new HashMap<Integer, ArrayList<HashMap<String, String>>>() {{
                    put(1, new ArrayList<HashMap<String, String>>() {{
                        add(new HashMap<String, String>() {{
                            put("Name", "W3");
                            put("Length", "1:00:00");
                            put("Personal_highscores_amount", "TODO");
                            put("Exercise_id", "0");
                        }});
                    }});
                    put(2, new ArrayList<HashMap<String, String>>() {{
                        add(new HashMap<String, String>() {{
                            put("Name", "W4");
                            put("Length", "1:00:00");
                            put("Personal_highscores_amount", "TODO");
                            put("Exercise_id", "0");
                        }});
                    }});
                }});
            }});
        }};

//        for(int year:workouts.keySet()){
//            temp_calendar.put(year, new ArrayList<>());
//            for (int month:workouts.get(year).keySet()){
//                temp_calendar.get(year).add(month);
//            }
//        }

        exercises = new HashMap<String,ArrayList<ArrayList<HashMap<String, String>>>>(){{
            put("Exercises", new ArrayList<ArrayList<HashMap<String, String>>>(){{
                add(new ArrayList<HashMap<String, String>>(){{
                    add(new HashMap<String, String>(){{
                        put("Name","ss");
                        put("Weights","55");
                        put("Reps","2");
                    }});
                    add(new HashMap<String, String>(){{
                        put("Name","aa");
                        put("Weights","77");
                        put("Reps","3");
                    }});
                }});
                add(new ArrayList<HashMap<String, String>>(){{
                    add(new HashMap<String, String>(){{
                        put("Name","ff");
                        put("Weights","11");
                        put("Reps","9");
                    }});
                    add(new HashMap<String, String>(){{
                        put("Name","ww");
                        put("Weights","00");
                        put("Reps","1");
                    }});
                }});
            }});
        }};


        //____________________________________________________________________________________
        Bundle notebook_bundle = new Bundle();
        notebook_bundle.putSerializable("Exercises", exercises);
        notebook_bundle.putSerializable("Workouts", workouts);
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

//        ft.replace(((FrameLayout)findViewById(R.id.main_layout)).getChildAt(0).getId(),screen).setReorderingAllowed(true).commit();
    }
}