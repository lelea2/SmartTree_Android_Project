package com.kdao.cmpe235_project;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.provider.MediaStore;
import java.io.*;
import android.os.Environment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.PreferenceData;

public class CameraActivity extends AppCompatActivity {

    //Pre-select option for photo taking
    static CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

    static String TAG = "CameraActivity";
    static int TAKE_PHOTO = 1;
    static int CHOOSE_PHOTO = 2;

    Button cameraButton;
    Button videoButton;
    Button audioButton;
    Button shareButton;
    Button uploadButton;
    ImageView viewImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean userLoggedIn = PreferenceData.getUserLoggedInStatus(getApplicationContext());
        if (userLoggedIn == false) { //navigate to signin page if user is not signin yet*/
            Intent signinIntent = new Intent(CameraActivity.this, SigninActivity.class);
            signinIntent.putExtra(Config.SIGN_IN_REQUIRED, Config.REQUIRE_SIGNIN);
            startActivity(signinIntent);
            return;
        }
        setContentView(R.layout.activity_camera);
        getElements();
        handleCameraButton();
        handleVideoButton();
        handleAudioButton();
        handleSharePhoto();
        handleUpload();
    }

    /**
     * Private function to handle Video Button
     */
    private void handleVideoButton() {
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Video action");
                Intent videoView = new Intent(CameraActivity.this, VideoActivity.class);
                startActivity(videoView);
            }
        });
    }

    /**
     * Private function to handle Audio Button
     */
    private void handleAudioButton() {
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Audio action");
                Intent audioView = new Intent(CameraActivity.this, AudioActivity.class);
                startActivity(audioView);
            }
        });
    }

    /**
     * Private function to handle Upload Button
     */
    private void handleUpload() {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Upload action");
                Intent uploadView = new Intent(CameraActivity.this, UploadActivity.class);
                startActivity(uploadView);
            }
        });
    }

    /**
     * Private helper identify all the element
     */
    private void getElements() {
        cameraButton = (Button)findViewById(R.id.camera_btn);
        videoButton = (Button)findViewById(R.id.video_btn);
        audioButton = (Button)findViewById(R.id.audio_btn);
        shareButton = (Button) findViewById(R.id.image_share_btn);
        uploadButton = (Button) findViewById(R.id.upload_btn);
        viewImage = (ImageView) findViewById(R.id.viewImage);
    }

    /**
     * Handle click on camera button
     */
    private void handleCameraButton() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    /**
     * Private function to handle share photo
     */
    private void handleSharePhoto() {
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable)viewImage.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                // Save this bitmap to a file.
                File cache = getApplicationContext().getExternalCacheDir();
                File sharefile = new File(cache, "toshare.png");
                try {
                    FileOutputStream out = new FileOutputStream(sharefile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                // Now send it out to share
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + sharefile));
                try {
                    startActivity(Intent.createChooser(share, "Share photo"));
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    /**
     * Function handle image selecting (either taken photo or chosen from gallery)
     */
    private void selectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, TAKE_PHOTO);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, CHOOSE_PHOTO);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Function handle image result after image is taken
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { //Image comes straight from take photo
            if (requestCode == TAKE_PHOTO) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    viewImage.setImageBitmap(bitmap);
                    String path = android.os.Environment.getExternalStorageDirectory() + File.separator + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, file.getName() , file.getName());
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CHOOSE_PHOTO) { //Image comes from gallery
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                viewImage.setImageBitmap(thumbnail);
            }
        }
    }
}
