package com.alameen.wael.hp.chatapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class PutUserImage extends AppCompatActivity implements View.OnClickListener {

    private String phone_number;
    private final static int GALLERY_REQUEST = 2200;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_user_image);

        phone_number = getIntent().getExtras().getString("phone_number");
        ImageView pick_image = (ImageView) findViewById(R.id.pick_image);
        pick_image.setOnClickListener(this);
        Button skip = (Button) findViewById(R.id.skip);
        skip.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pick_image:
                GalleryPhoto galleryPhoto = new GalleryPhoto(getApplicationContext());
                Intent intent = galleryPhoto.openGalleryIntent();
                startActivityForResult(intent, GALLERY_REQUEST);
                break;
            case R.id.skip:
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user);
                uploadToServer(phone_number, bitmap);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST && data != null) {
                GalleryPhoto galleryPhoto = new GalleryPhoto(getApplicationContext());
                galleryPhoto.setPhotoUri(data.getData());
                String path = galleryPhoto.getPath();
                try {
                    Bitmap user_image = ImageLoader.init().from(path).requestSize(512, 512).getBitmap();
                    uploadToServer(phone_number, user_image);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadToServer(String phone_number, Bitmap user_image) {
        String encoded_image = BitMapToString(user_image);
        Map<String, Object> map = new HashMap<>();
        map.put(phone_number, encoded_image);
        root.updateChildren(map);
        startActivity(new Intent(this, MainActivity.class));
    }

    private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte [] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
