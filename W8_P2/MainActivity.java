package com.example.w8_p2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    final String TAG = "KOBE";
    static ArrayList<String> facts;
    static ListIterator<String> iter;

    TextView txtFact;
    Button btnPrevious;
    Button btnNext;

    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain view objects
        txtFact = findViewById(R.id.txtFact);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        facts = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("trivia").document("facts");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        for (Object s : document.getData().values()) {
                            facts.add(String.valueOf(s));
                        }
                        index = 0;
                        txtFact.setText(facts.get(index));
                    } else {
                        Log.d(TAG, "No such document in Firestore");
                    }
                } else {
                    Log.d(TAG, "Failed to access Firestore", task.getException());
                }
            }
        });

        //iter = facts.listIterator();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, iter.nextIndex() + "");
//                if (iter.hasNext()) {
//                    Log.i(TAG, iter.next());
//                    txtFact.setText(iter.next());
//                }
//                else txtFact.setText(facts.get(0));
                displayNext();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPrevious();
            }
        });
    }

    // Encapsulating whole statements in try to catch out of bounds exceptions specifically
    private void displayNext() {
        index++;
        try {
            if (index < facts.size()) {
                txtFact.setText(facts.get(index));
            } else {
                txtFact.setText(facts.get(0));
                index = 0;
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void displayPrevious() {
        index--;
        try {
            if (index >= 0) txtFact.setText(facts.get(index));
            else {
                txtFact.setText(facts.get(facts.size() - 1));
                index = facts.size() - 1;
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, e.toString());
        }

    }
}
