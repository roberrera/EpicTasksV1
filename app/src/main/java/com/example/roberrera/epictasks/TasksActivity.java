package com.example.roberrera.epictasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Rob on 1/21/16.
 */
public class TasksActivity extends AppCompatActivity {

    ListView mListView;
    private ArrayAdapter<String> mTaskAdapter;
    public static ArrayList<String> mTaskList;
    ImageView mAddTask;
    ImageView mCastle;
    private EditText mEditText;
    Button mSaveButton;
    TextView mTextView;
    TextView mBottomBarText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        setTitle("Your Epic Tasks");

        mListView = (ListView) findViewById(R.id.listViewTasks);
        mCastle = (ImageView)findViewById(R.id.castle);
        mTextView = (TextView)findViewById(R.id.textView);
        mAddTask = (ImageView) findViewById(R.id.fabButton4);
        mBottomBarText = (TextView) findViewById(R.id.bottomText2);
        mSaveButton = (Button) findViewById(R.id.save_button);
        mEditText = (EditText) findViewById(R.id.addNewTask);
        mTaskList = new ArrayList<String>();

        // Receive the data from the intent.
        mTaskList = getIntent().getStringArrayListExtra("TASKS LIST");
        Log.d("TASKSACTIVITY", "Intent received: " + mTaskList);
        mTaskAdapter = new ArrayAdapter<String>(TasksActivity.this, android.R.layout.simple_list_item_1, mTaskList);
        mListView.setAdapter(mTaskAdapter);

        // If no tasks exist, show an image and message
        if (mTaskList.isEmpty()){
            mCastle.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mCastle.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);
        }

        // Refresh the list view.
        mTaskAdapter.notifyDataSetChanged();


        // Long-press on a row:
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //     Remove the item within array at its position.
                mTaskList.remove(position);
                MainActivity.mXP = MainActivity.mXP + 10;

                //      Increase the number of tasks completed in the bottom bar.
                MainActivity.mTasksCompleted = MainActivity.mTasksCompleted + 1;
                Toast.makeText(TasksActivity.this, "Task completed! XP +10!", Toast.LENGTH_SHORT).show();

                if (MainActivity.mXP % 75 == 0){
                    MainActivity.mLevel = MainActivity.mLevel + 1;
                    Toast.makeText(TasksActivity.this, "YOU'VE REACHED LEVEL " + MainActivity.mLevel + "!", Toast.LENGTH_SHORT).show();
                }

                // If all tasks are completed, show an image and message
                if (mTaskList.isEmpty()){
                    mCastle.setVisibility(View.VISIBLE);
                    mTextView.setVisibility(View.VISIBLE);
                } else {
                    mCastle.setVisibility(View.GONE);
                    mTextView.setVisibility(View.GONE);
                }

                //     Refresh the adapter so the deleted item disappears and the image shows (if the image should show)
                mTaskAdapter.notifyDataSetChanged();
                mBottomBarText.setText("LV" + MainActivity.mLevel + "  " + MainActivity.mXP + "XP  " + MainActivity.mTasksCompleted
                        + " tasks completed");

                Toast.makeText(TasksActivity.this, "List deleted. :(", Toast.LENGTH_SHORT).show();
                Log.d("TASKSACTIVITY", "Task deleted by long-pressing on it.");

                // Return true consumes the long click event (marks it handled)
                return true;
            }
        });


        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = mEditText.getText().toString();

                // For adding a task
                // Check if the user inputted task is already in the array list
                if ( !(mTaskList.isEmpty()) && mTaskList.contains(userInput) ) {
                    mEditText.setError("You've already added that!");
                    Log.d("TASKSACTIVITY", "User typed " + userInput + " but it already exists.");

                    // Check if the user has typed anything
                } else if ( userInput.isEmpty()) {
                    Log.d("TASKSACTIVITY", "User input is null.");
                    mEditText.setError("Really? There's nothing you want to add?");

                    // Add user input to array list of tasks as a new entry
                } else {
                    mTaskList.add(userInput);
                    Log.d("TASKSACTIVITY", "User typed " + userInput + " and it was added to mTaskList.");

                    // Clear the text from the EditText area
                    mEditText.getText().clear();

                    // Award XP to the user for adding a task. If the user's XP is then divisible by 75XP, a user level is gained.
                    MainActivity.mXP = MainActivity.mXP + 5; // The XP does not seem to update until you move to the next screen.
                    if (MainActivity.mXP % 75 == 0){
                        MainActivity.mLevel = MainActivity.mLevel + 1;
                        Toast.makeText(TasksActivity.this, "YOU'VE REACHED LEVEL " + MainActivity.mLevel + "!", Toast.LENGTH_SHORT).show();
                    }

                    // Update the values in the bottom bar
                    mBottomBarText.setText("LV" + MainActivity.mLevel + "  " + MainActivity.mXP + "XP  " + MainActivity.mTasksCompleted
                            + " tasks completed");
                    Log.d("TASKSACTIVITY", "5 XP added. Total XP = " + MainActivity.mXP);

                    mCastle.setVisibility(View.GONE);
                    mTextView.setVisibility(View.GONE);
                    mTaskAdapter.notifyDataSetChanged();
                    Toast.makeText(TasksActivity.this, "Task added! +5XP", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // When save button is pressed, tasks created are not lost. Same effect as device's back button.
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saveButton = new Intent(TasksActivity.this, MainActivity.class);
                saveButton.putExtra("POSITION", getIntent().getIntExtra("POSITION", -1));
                saveButton.putStringArrayListExtra("TASKS LIST", mTaskList);
                setResult(RESULT_OK, saveButton);
                Log.d("TASKSACTIVITY", "Tasks saved. Going back to MainActivity.");
                finish();
            }
        });

        // Bottom bar text, which shows user's level, xp, and tasks completed.
        mBottomBarText.setText("LV" + MainActivity.mLevel + "  " + MainActivity.mXP +
                "XP  " + MainActivity.mTasksCompleted + " tasks completed");
    }


    // When device's back button is pressed, tasks created are not lost.
    @Override
    public void onBackPressed() {
        Intent backPressed = new Intent();
        backPressed.putExtra("POSITION", getIntent().getIntExtra("POSITION", -1));
        backPressed.putStringArrayListExtra("TASKS LIST", mTaskList);
        setResult(RESULT_OK, backPressed);
        Log.d("TASKSACTIVITY", "Back pressed.");
        mBottomBarText.setText("LV" + MainActivity.mLevel + "  " + MainActivity.mXP + "XP  " + MainActivity.mTasksCompleted
                + " tasks completed");
        finish();
    }

}