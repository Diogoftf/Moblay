package com.example.moblay;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.moblay.Adapter.VideoAdapter;
import com.example.moblay.Model.VideoModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView _recyclerView;
    private RecyclerView.LayoutManager recycleviewLayoutManager;
    private ArrayList<VideoModel> arrayListVideos;
    private ArrayList<VideoModel> arrayListVideosToCompare;
    private VideoAdapter videoAdapter;

    private static final int MY_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayListVideos = new ArrayList<>();
        arrayListVideosToCompare = new ArrayList<>();
        videoAdapter = new VideoAdapter(getApplicationContext(), arrayListVideos, this);

        _recyclerView = (RecyclerView) findViewById(R.id.recyclerViewVideo);
        recycleviewLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        _recyclerView.setLayoutManager(recycleviewLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMain();

        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false) == false){
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
            Intent intent = new Intent(this, TutorialOne.class);
            startActivity(intent);
        }
    }

    private void startMain(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            init(); //initialize all of the controls
        }
    }

    private void init() {

        fetchVideosFromGallery();

        if (compareArrays() == false){
            _recyclerView = (RecyclerView) findViewById(R.id.recyclerViewVideo);
            recycleviewLayoutManager = new GridLayoutManager(getApplicationContext(),3);
            _recyclerView.setLayoutManager(recycleviewLayoutManager);
            arrayListVideosToCompare.clear();
            fetchVideosFromGallery();
            setAdapter();
        }

    }

    private boolean compareArrays(){
        boolean value = true;

        if(arrayListVideos.isEmpty() || arrayListVideos == null || arrayListVideos == null || arrayListVideos.isEmpty()){
            value = false;
        }
        else if(arrayListVideos.size() != arrayListVideosToCompare.size()) {
            value = false;
        }
        else{
            for (int i = 0; i < arrayListVideos.size(); i ++){
                if(!(arrayListVideos.get(i).equals(arrayListVideosToCompare.get(i)))){
                    value = false;
                }
            }
        }

        return value;

    }

    private void fetchVideosFromGallery() {
        Uri uri;
        Cursor cursor;
        int column_index_data, thum; //index data and thumbnail

        String absolutePathImage = null;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI; //first, we initialize the URI

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Thumbnails.DATA};

        String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        arrayListVideos.clear();

        while(cursor.moveToNext()) {
            absolutePathImage = cursor.getString(column_index_data);
            VideoModel videoModel = new VideoModel();
            videoModel.setBoolean_selected(false);
            videoModel.setStr_path(absolutePathImage);
            videoModel.setStr_thumb(cursor.getString(thum));

            arrayListVideos.add(videoModel);
        }

    }

    public void setAdapter(){
        //call the adapter class and set it to recyclerview
        videoAdapter = new VideoAdapter(getApplicationContext(), arrayListVideos, this);
        _recyclerView.setAdapter(videoAdapter);

        for (int i = 0; i < arrayListVideos.size(); i++){
            arrayListVideosToCompare.add(arrayListVideos.get(i));
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                        init();
                    }
                } else {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

}