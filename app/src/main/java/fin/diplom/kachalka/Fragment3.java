package fin.diplom.kachalka;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;

public class Fragment3 extends Fragment {
    CalendarView calendar;


    public Fragment3() {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView date_picker = view.findViewById(R.id.dateView);
        TextView startTimePicker = view.findViewById(R.id.startTime);
        TextView endTimePicker = view.findViewById(R.id.endTime);

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