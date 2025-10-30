package com.example.midterm;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static int ROOT_ID;
    public static String sharedText = "";
    static LinearLayout groupTable;
    static LinearLayout groupHistory;
    static EditText etNum;
    static Button btnGenerate;
    static Button btnHistory;
    static Button btnBack;
    static ListView lvTable;
    static ListView lvHistory;

    static ArrayAdapter<String> tableAdapter;
    static ArrayAdapter<Integer> historyAdapter;

    public static final ArrayList<String> currTable = new ArrayList<>();
    public static final Set<Integer> history = new LinkedHashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout root = new FrameLayout(this);
        ROOT_ID = ViewCompat.generateViewId();
        root.setId(ROOT_ID);
        setContentView(root);

        groupTable = findViewById(R.id.groupTable);
        groupHistory = findViewById(R.id.groupHistory);
        etNum = findViewById(R.id.etNum);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnHistory = findViewById(R.id.btnHistory);
        btnBack = findViewById(R.id.btnBack);
        lvTable = findViewById(R.id.lvTable);
        lvHistory = findViewById(R.id.lvHistory);

        tableAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currTable);
        lvTable.setAdapter(tableAdapter);
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(history));
        lvHistory.setAdapter(historyAdapter);


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
        @Override public void onStart() {
            super.onStart();
            groupTable.setVisibility(android.view.View.VISIBLE);
            groupHistory.setVisibility(android.view.View.GONE);

            btnGenerate.setOnClickListener(v->{
                String s=etNum.getText().toString().trim();
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(requireContext(),"Enter a number",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    generateMultiplicationTable(Integer.parseInt(s));
                } catch(Exception e){
                    Toast.makeText(requireContext(),"Invalid number",Toast.LENGTH_SHORT).show();
                }
            });

            btnHistory.setOnClickListener(v->
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .add(new SecondFragment(), "history")
                            .addToBackStack("history").commit()
            );

            lvTable.setOnItemClickListener((p,view,pos,id)->{
                String row = tableAdapter.getItem(pos);
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete row?")
                        .setMessage("Delete: "+row+" ?")
                        .setPositiveButton("Delete",(d,w)->{
                            if (pos>=0 && pos<currTable.size()){
                                currTable.remove(pos);
                                tableAdapter.notifyDataSetChanged();
                                Toast.makeText(requireContext(),"Deleted: "+row,Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();
            });
        }
    }

    // Fragment 2
    public static class SecondFragment extends Fragment {
        private SharedViewModel vm;
        private ArrayAdapter<Integer> adapter;
        private ArrayList<Integer> items;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true); // optional Clear All for history
            vm = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            items = new ArrayList<>();
            adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1, items);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_history, container, false);
            ListView listView = v.findViewById(R.id.lvHistory);
            listView.setAdapter(adapter);

            vm.getHistory().observe(getViewLifecycleOwner(), list -> {
                items.clear();
                items.addAll(list);
                adapter.notifyDataSetChanged();
            });

            return v;
        }

    }
}
