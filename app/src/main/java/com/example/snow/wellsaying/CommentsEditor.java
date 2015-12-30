package com.example.snow.wellsaying;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import jp.wasabeef.richeditor.RichEditor;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;

/**
 * Created by zhouyong on 12/29/15.
 */
public class CommentsEditor extends Activity {

    private static final String TAG = "CommentsEditor";

    private RichEditor mEditor;
    private TextView mIndexColor;
    private DBHelper dbHelper;
    private int mContentId;
    private int mCurrentColor = 0xff000000;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_editor);
        Intent intent = getIntent();
        mContext = this;
        mContentId = intent.getIntExtra("content_id",0);
        mIndexColor = (TextView)findViewById(R.id.index_color);
        mEditor = (RichEditor)findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(20);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        mEditor.setPadding(10, 10, 10, 10);
        // mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Write Your Comments Here...");
        initEditor();
        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void initEditor() {
        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(mContext)
                        .setTitle("Choose color")
                        .initialColor(mCurrentColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(8)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //do something when color selected.
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                mCurrentColor = selectedColor;
                                mIndexColor.setBackgroundColor(mCurrentColor);
                                mEditor.setTextColor(mCurrentColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });


        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent pickPicIntent = new Intent();
                pickPicIntent.setType("image/*");
                pickPicIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pickPicIntent, "Select Picture"), 1);
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                insertLink();
                //mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });
        findViewById(R.id.action_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEditor.getHtml();
                insertContent2DB(content);
                Log.d(TAG, "content = " + content);
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1) {
            Uri uri = data.getData();
            String path = getPath(uri);
            Log.d(TAG,"uri = "+uri+" path = "+path+" scheme="+uri.getScheme());
            mEditor.insertImage("file://"+path,"image");
        }
    }
    private void insertContent2DB(String content) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("content_id",mContentId);
        values.put("comment",content);
        values.put("author","zhouyongxyz");
        db.insert("comments", null, values);
    }
    private String getPath(Uri uri) {
        if("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = mContext.getContentResolver().query(uri,projection,null,null,null);
                int index = cursor.getColumnIndexOrThrow("_data");
                if(cursor.moveToFirst()) {
                    return cursor.getString(index);
                }
            } catch (Exception e) {
                return null;
            }
        } else if("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    private void insertLink() {
        final EditText input = new EditText(mContext);
        input.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Insert Link")
                .setView(input)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Ok",
                   new DialogInterface.OnClickListener(){
                       public void onClick(DialogInterface dialog,int which) {
                           mEditor.insertLink(input.getText().toString(),"");
                       }
                });
        builder.show();
    }
}
