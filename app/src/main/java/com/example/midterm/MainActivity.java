package com.example.midterm;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static int ROOT_ID;
    public static String sharedText = "";
    private LinearLayout groupTable;
    private LinearLayout groupHistory;
    private EditText etNum;
    private Button btnGenerate;
    private Button btnHistory;
    private Button btnBack;
    private ListView lvTable;
    private ListView lvHistory;

    private ArrayAdapter<String> tableAdapter;
    private ArrayAdapter<Integer> historyAdapter;

    public static final ArrayList<String> currTable = new ArrayList<>();
    public static final Set<Integer> history = new LinkedHashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout root = new FrameLayout(this);
        ROOT_ID = ViewCompat.generateViewId();
        root.setId(ROOT_ID);
        setContentView(root);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(ROOT_ID, new FirstFragment())
                    .commit();
        }
    }

    static void generateMultiplicationTable(int x){
        currTable.clear();
        for (int i = 1; i <= 10; i++){
            currTable.add(x + " x " + i + " = " + (x * i));
        }
        history.add(x);
    }

    // Fragment 1
    public static class FirstFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull android.view.LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
            LinearLayout col = new LinearLayout(requireContext());
            col.setOrientation(LinearLayout.VERTICAL);

            EditText input = new EditText(requireContext());
            Button next = new Button(requireContext());
            next.setText("Next");

            col.addView(input, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            col.addView(next, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            next.setOnClickListener(v -> {
                MainActivity.sharedText = input.getText().toString();
                getParentFragmentManager().beginTransaction()
                        .replace(MainActivity.ROOT_ID, new SecondFragment())
                        .commit();
            });

            return col;
        }
    }

    // Screen 2
    public static class SecondFragment extends Fragment {
        int topId;
        int bottomId;

        @Nullable
        @Override
        public View onCreateView(@NonNull android.view.LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
            LinearLayout col = new LinearLayout(requireContext());
            col.setOrientation(LinearLayout.VERTICAL);

            FrameLayout top = new FrameLayout(requireContext());
            FrameLayout bottom = new FrameLayout(requireContext());
            topId = ViewCompat.generateViewId();
            bottomId = ViewCompat.generateViewId();
            top.setId(topId);
            bottom.setId(bottomId);


            col.addView(top, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
            col.addView(bottom, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

            FragmentManager fm = getChildFragmentManager();
            if (fm.findFragmentById(topId) == null) fm.beginTransaction().replace(topId, new ChildAFragment()).commit();
            if (fm.findFragmentById(bottomId) == null) fm.beginTransaction().replace(bottomId, new ChildBFragment()).commit();

            return col;
        }

        void updateChildB(String s) {
            Fragment f = getChildFragmentManager().findFragmentById(bottomId);
            if (f instanceof ChildBFragment) ((ChildBFragment) f).setTextNow(s);
        }
    }

    // Child Fragment A
    public static class ChildAFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull android.view.LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
            LinearLayout col = new LinearLayout(requireContext());
            col.setOrientation(LinearLayout.VERTICAL);

            EditText input = new EditText(requireContext());
            Button send = new Button(requireContext());
            send.setText("Send");


            col.addView(input, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            col.addView(send, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            send.setOnClickListener(v -> {
                String s = input.getText().toString();
                MainActivity.sharedText = s;
                Fragment parent = getParentFragment();
                if (parent instanceof SecondFragment) {
                    ((SecondFragment) parent).updateChildB(s);
                }
            });

            return col;
        }
    }

    // Child Fragment B
    public static class ChildBFragment extends Fragment {
        TextView tv;


        @Nullable
        @Override
        public View onCreateView(@NonNull android.view.LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
            tv = new TextView(requireContext());
            tv.setText(MainActivity.sharedText);
            return tv;
        }

        void setTextNow(String s) {
            if (tv != null) tv.setText(s);
        }
    }
}
