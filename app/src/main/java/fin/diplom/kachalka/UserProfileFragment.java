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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserProfileFragment extends Fragment {
    UserProfileFragment upf;
    LinearLayout appointmentsList;

    public void fill_form(View view, JSONObject response){
//        System.out.println(response);
        Map<String, String> client = (Map) ((ArrayList)new Gson().fromJson(String.valueOf(response), HashMap.class).get("client")).get(0);
        ((TextView)view.findViewById(R.id.nameView)).setText(client.get("username"));
        ((TextView)view.findViewById(R.id.lastNameView)).setText(client.get("last_name"));
        ((TextView)view.findViewById(R.id.emailView)).setText(client.get("email"));
    }

    public void fill_appointments(View view, JSONObject response){
        ArrayList appointments = (ArrayList) new Gson().fromJson(String.valueOf(response), HashMap.class).get("appointments");
        for(Object appointment:appointments){
            LinearLayout l = (LinearLayout) LayoutInflater.from(view.getContext()).inflate(R.layout.appointment_layout, null, false);
            ((TextView)l.findViewById(R.id.nameView)).setText((String)((Map)((Map)((Map)appointment).get("schedule_position")).get("activity")).get("name"));
            ((TextView)l.findViewById(R.id.dateView)).setText((String)((Map)((Map)appointment).get("schedule_position")).get("date"));
            ((TextView)l.findViewById(R.id.timeView)).setText((String)((Map)((Map)appointment).get("schedule_position")).get("startTime"));
            appointmentsList.addView(l);
        }
    }

    public UserProfileFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        upf = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile_fragment, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appointmentsList = view.findViewById(R.id.appointmentsList);
        Method fill_form = null;
        try {
            fill_form = UserProfileFragment.class.getMethod("fill_form", View.class, JSONObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Method fill_appointments = null;
        try {
            fill_appointments = UserProfileFragment.class.getMethod("fill_appointments", View.class, JSONObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainActivity.get_request(upf, "return_client", view, fill_form, null, null);
        MainActivity.get_request(upf, "return_appointments", view, fill_appointments, null, new JSONObject(){{
            try {
                put("date", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }});
    }
}