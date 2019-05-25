package com.example.moblay;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.moblay.Adapter.VideoAdapter;
import com.example.moblay.Model.VideoModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView _recyclerView;
    RecyclerView.LayoutManager recycleviewLayoutManager;

    ArrayList<VideoModel> arrayListVideos;

    private static final int MY_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        _recyclerView = (RecyclerView) findViewById(R.id.recyclerViewVideo);
        recycleviewLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        _recyclerView.setLayoutManager(recycleviewLayoutManager);
        arrayListVideos = new ArrayList<>();
        fetchVideosFromGallery();
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

        while(cursor.moveToNext()) {
            absolutePathImage = cursor.getString(column_index_data);
            VideoModel videoModel = new VideoModel();
            videoModel.setBoolean_selected(false);
            videoModel.setStr_path(absolutePathImage);
            videoModel.setStr_thumb(cursor.getString(thum));

            arrayListVideos.add(videoModel);
        }

        //call the adapter class and set it to recyclerview
        VideoAdapter videoAdapter = new VideoAdapter(getApplicationContext(), arrayListVideos, this);
        _recyclerView.setAdapter(videoAdapter);

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
