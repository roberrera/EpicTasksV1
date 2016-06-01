package com.example.roberrera.epictasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ListView mListView;
    public static ArrayList<ArrayList<String>> mListofLists; // The main list is of type ArrayList because it has an array list of tasks within it.
    public static ArrayList<String> mList;
    private ArrayAdapter<String> mAdapter; // Because the ArrayList is type ArrayList, the adapter has to be as well.
    ImageView mFAB; // This is the floating activity button.
    EditText mEditText;
    TextView mBottomBarText; // Text view where user's XP info is shown.
    static int mLevel = 1;
    static int mXP = 0; // Completing tasks will give the user experience (XP), which will go toward raising their mLevel.
    static int mTasksCompleted = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Your Epic Lists");


        mListView = (ListView) findViewById(R.id.listViewMain);
        mBottomBarText = (TextView) findViewById(R.id.bottomText);
        mFAB = (ImageView) findViewById(R.id.fabButton);
        mEditText = (EditText) findViewById(R.id.addNewList);

        // TODO: Update the syntax of the following setText line to get rid of warning.
        // Bottom bar text, which shows user's level, xp, and tasks completed.
        mBottomBarText.setText("LV" + mLevel + "  " + mXP + "XP  " + mTasksCompleted
                + " tasks completed");

        // Task List: a nested array list.
        mListofLists = new ArrayList<ArrayList<String>>();
        mList = new ArrayList<String>();
        Log.d("MAINACTIVITY:mTaskLists", "mTaskLists size is " + (mListofLists.size()));
        Log.d("MAINACTIVITY:mList", "mList size is " + (mList.size()));

        // Setting array adapter
        mAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, mList);
        mListView.setAdapter(mAdapter);

        // When a row is pressed:
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("MAINACTIVITY", "onItemClick: List clicked at position " + position);

                Intent tasks = new Intent(MainActivity.this, TasksActivity.class);
                tasks.putExtra("POSITION", position);
                Log.d("MAINACTIVITY", "onItemClick / Sent 'position' to TasksActivity.");

                // TODO: Figure out why when you tap on a list for the first time, the name of the list is added as a task.
                tasks.putStringArrayListExtra("TASKS LIST", mListofLists.get(position));
                Log.d("MAINACTIVITY", "onItemClick / OnClick: Sent 'TASKS LIST' to TasksActivity");

                startActivityForResult(tasks, 0);
                Log.d("MAINACTIVITY", "onItemClick / startActivity: List clicked at position " + position);
            }
        });


        // Long-press on a row:
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //     Remove the item within array at its position.
                mList.remove(position);
                //     Refresh the adapter so the deleted item disappears.
                mAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "List deleted. :(", Toast.LENGTH_SHORT).show();
                Log.d("MAINACTIVITY", "List deleted by long-pressing on it.");
                // Return true consumes the long click event (marks it handled)
                return true;
            }
        });


        // When floating action button is pressed:
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = mEditText.getText().toString();
                // For adding a list on the same activity.
                if ( !(MainActivity.mList.isEmpty()) && MainActivity.mList.contains(userInput) ) {
                    mEditText.setError("You've already added that!");
                    Log.d("NEW TASK ACTIVITY", "User typed " + userInput + " but it already exists.");
                } else if ( userInput.isEmpty()) {
                    Log.d("NEW LIST ACTIVITY", "User input is null.");
                    mEditText.setError("Really? There's nothing you want to add?");

                } else {
                    mList.add(userInput);
                    mListofLists.add(mList);

                    mEditText.getText().clear();
                    mXP = mXP + 5; // The XP does not seem to update until you move to the next screen.
                    if (mXP % 75 == 0){
                        mLevel = mLevel + 1;
                        Toast.makeText(MainActivity.this, "YOU'VE REACHED LEVEL " + mLevel + "!", Toast.LENGTH_SHORT).show();
                    }
                    mBottomBarText.setText("LV" + mLevel + "  " + mXP + "XP  " + mTasksCompleted
                            + " tasks completed");
                    Log.d("TASKSACTIVITY", "5 XP added. Total XP = " + mXP);

                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "You've accepted a quest! +5XP (Long-press to delete.)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Checking that the request was successful

        if (resultCode == RESULT_OK) {

            // The user successfully submitted the name of a new list.
            Log.d("MAINACTIVITY:RSLT CODE", "Result OK.");

            ArrayList<String> getNewList = data.getStringArrayListExtra("TASKS LIST");
            Log.d("onActivityResult", "Received the following data: " + getNewList);
            mListofLists.set(data.getIntExtra("POSITION", -1), getNewList);

            mAdapter.notifyDataSetChanged();
            mBottomBarText.setText("LV" + mLevel + "  " + mXP + "XP  " + mTasksCompleted
                    + " tasks completed");

        } else {
            return;
        }
    }

}