package com.example.midterm;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
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
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
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
        if (b == null) getSupportFragmentManager().beginTransaction().add(new FirstFragment(), "table").commit();
    }

    static void generateMultiplicationTable(int x){
        currTable.clear();

        for (int i = 1; i <= 10; i++){
            currTable.add(x + " x " + i + " = " + (x * i));
        }
        history.add(x);
        tableAdapter.notifyDataSetChanged();
        historyAdapter.clear();
        historyAdapter.addAll(history);
        historyAdapter.notifyDataSetChanged();
    }

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
                } try {
                    generateMultiplicationTable(Integer.parseInt(s));
                } catch(Exception e) {
                    Toast.makeText(requireContext(),"Invalid number",Toast.LENGTH_SHORT).show();
                }
            });

            btnHistory.setOnClickListener(v->
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .add(new SecondFragment(),"history")
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

    public static class SecondFragment extends Fragment {
        @Override public void onStart() {
            super.onStart();
            groupTable.setVisibility(android.view.View.GONE);
            groupHistory.setVisibility(android.view.View.VISIBLE);

            lvHistory.setOnItemClickListener((p,view,pos,id)->{
                Integer n = historyAdapter.getItem(pos);
                if (n!=null){
                    generateMultiplicationTable(n);
                    Toast.makeText(requireContext(),"Generated table for "+n,Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });

            btnBack.setOnClickListener(v->
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .add(new FirstFragment(), "Times tables")
                            .addToBackStack("Times tables").commit()
            );
        }
    }
}
