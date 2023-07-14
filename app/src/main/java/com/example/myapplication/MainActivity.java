package com.example.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "CustomContentProvider";
    private EditText title, content, delete_id;
    private Button add, update, delete, showNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        delete_id = (EditText) findViewById(R.id.delete_id);

        // add Click Listners
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);
        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(this);
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        showNotes = (Button) findViewById(R.id.show_notes);
        showNotes.setOnClickListener(this);

        getNotes();

    }

    void addNote() {
        if (title.getText().toString().length() > 0
                && content.getText().toString().length() > 0) {
            ContentValues values = new ContentValues();
            values.put(NotesMetaData.NotesTable.TITLE, title.getText().toString());
            values.put(NotesMetaData.NotesTable.CONTENT, content.getText().toString());
            getContentResolver().insert(NotesMetaData.CONTENT_URI, values);
            Log.d(TAG, "Inserted");
            makeToast("Note Added");
        } else {
            makeToast("Empty Field");
        }
    }

    void deleteNote(String str_id) {
        try {
            int id = Integer.parseInt(str_id);
            Log.i(TAG, "Deleting with id = " + id);
            getContentResolver().delete(NotesMetaData.CONTENT_URI,
                    NotesMetaData.NotesTable.ID + " = " + id, null);
            Log.i(TAG, "Deleted");
            makeToast("Note Deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateNote(String str_id) {
        try {
            int id = Integer.parseInt(str_id);
            Log.i(TAG, "Updating with id = " + id);
            ContentValues values = new ContentValues();
            values.put(NotesMetaData.NotesTable.TITLE, title.getText().toString());
            values.put(NotesMetaData.NotesTable.CONTENT, content.getText().toString());
            getContentResolver().update(NotesMetaData.CONTENT_URI, values,
                    NotesMetaData.NotesTable.ID + " = " + id, null);
            makeToast("Note Updated");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getNotes() {

        Cursor cur = getContentResolver().query(NotesMetaData.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {

            Log.i(TAG, "Showing values.....");
            while (cur.moveToNext()) {
                String Id = cur.getString(cur.getColumnIndexOrThrow(NotesMetaData.NotesTable.ID));
                String title = cur.getString(cur
                        .getColumnIndexOrThrow(NotesMetaData.NotesTable.TITLE));
                System.out.println("Id = " + Id + ", Note Title : " + title);
            }
            makeToast("Check the LogCat for Notes");
        } else {
            Log.i(TAG, "No Notes added");
            makeToast("No Notes added");
        }
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == add) {
            addNote();
        }
        if (arg0 == update) {
            // update note with Id
            updateNote(delete_id.getText().toString());
        }
        if (arg0 == delete) {
            // delete note with Id
            deleteNote(delete_id.getText().toString());
        }
        if (arg0 == showNotes) {
            // show all
            getNotes();
        }
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

}