package com.hci.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.Settings.Secure;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelArrayAdapter;
import org.threeten.bp.Instant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private AmazonS3Client s3Client;
    private TransferUtility transferUtility;
    private ViewFlipper viewFlipper;
    private TextView movieTitle;
    private TextView movieInfo;
    private TextView movieDesc;
    private Button watch_trailer;
    private static final int ITEM_COUNT = 10;
    private float previousAngle = 0;
    private File temp = null;
    private MovieList movieList = new MovieList();
    private String id;

    private Movie currMovie;
    private int clicks = 0;
    private int shown_interest = 0;
    private long time_on_last_item = Instant.now().getEpochSecond();;
    private Movie firstMovie = null;
    private Movie lastMovie = null;

    private boolean file_saved = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id = Secure.ANDROID_ID;
        AndroidThreeTen.init(this);

        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

        try {
            temp = File.createTempFile(""+ id, ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Delete temp file when program exits.
        temp.deleteOnExit();
        BasicAWSCredentials credentials = new BasicAWSCredentials("", "");
        s3Client = new AmazonS3Client(credentials);
        transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        final WheelView wheelView = (WheelView) findViewById(R.id.wheelview);

        movieList.addDefault(this.getBaseContext());

        viewFlipper = (ViewFlipper)findViewById(R.id.image_view_flipper);
        movieTitle = findViewById(R.id.movie_title);
        movieDesc = findViewById(R.id.movie_desc);
        movieInfo = findViewById(R.id.movie_info);
        watch_trailer = findViewById(R.id.watch_trailer);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        viewFlipper.setAutoStart(true);
        //create data for the adapter
//        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(ITEM_COUNT);
//        for (int i = 0; i < ITEM_COUNT; i++) {
//            Map.Entry<String, Integer> entry = MaterialColor.random(this, "\\D*_500$");
//            entries.add(entry);
//        }

        //populate the adapter, that knows how to draw each item (as you would do with a ListAdapter)
//        wheelView.setAdapter(new MaterialColorAdapter(entries));

        wheelView.setAdapter(new MovieAdapter(movieList.getMovie_list()));
        //a listener for receiving a callback for when the item closest to the selection angle changes
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectListener() {
            @Override
            public void onWheelItemSelected(WheelView parent, Drawable itemDrawable, int position) {
                //get the item at this position
//                Map.Entry<String, Integer> selectedEntry = ((MaterialColorAdapter) parent.getAdapter()).getItem(position);
//                parent.setSelectionColor(getContrastColor(selectedEntry));

                if (Instant.now().getEpochSecond() - time_on_last_item > 2) {
                    shown_interest++;
                }
                time_on_last_item = Instant.now().getEpochSecond();
                currMovie = (( MovieAdapter) parent.getAdapter()).getItem(position);
                if (currMovie.name.length() > 25 && currMovie.name.lastIndexOf("\n") == -1) {
                    int split_loc = currMovie.name.substring(0,25).lastIndexOf(" ");
                    currMovie.setName(currMovie.name.substring(0,split_loc) + "\n" + currMovie.name.substring(split_loc+1,currMovie.name.length()));
                }
                SpannableString ss1=  new SpannableString(currMovie.name + " (" + currMovie.year + ")\n");
                ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, currMovie.name.length(), 0);// set color
                ss1.setSpan(new RelativeSizeSpan(1.5f), 0,currMovie.name.length(), 0); // set size

                String myRating = getStars(currMovie.rating);

                SpannableString ss12 =  new SpannableString(myRating);
                ss12.setSpan(new ForegroundColorSpan(Color.YELLOW), 13, myRating.length(), 0);// set color
                ss12.setSpan(new RelativeSizeSpan(0.85f), 0,myRating.length(), 0);

                SpannableString genre = new SpannableString("Genre: " + currMovie.genre);
                genre.setSpan(new RelativeSizeSpan(0.85f), 0,genre.length(), 0);

                SpannableString ss3=  new SpannableString("Synopsis:\n" + currMovie.synopsis);
                ss3.setSpan(new RelativeSizeSpan(1.5f), 0,10, 0); // set size



                movieTitle.setText(ss1);
                movieInfo.setText(TextUtils.concat(ss12,genre));
                movieDesc.setText(ss3);
                viewFlipper.removeAllViews();
                for (String img: currMovie.screenshots) {
                    try
                    {
                        // get input stream
                        InputStream ims = getAssets().open(img);
                        // load image as Drawable
                        Drawable d = Drawable.createFromStream(ims, null);
                        // set image to ImageView
                        ImageView mImage = new ImageView(getApplicationContext());
                        mImage.setImageDrawable(d);
                        viewFlipper.addView(mImage);
                        ims .close();
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                        return;
                    }
                }


            }
        });

        watch_trailer.setOnClickListener(v -> {
            if (currMovie != null ) {
                watchYoutubeVideo(this,currMovie.trailer);
            }
        });



        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {
                currMovie = (( MovieAdapter) parent.getAdapter()).getItem(position);
                String msg = String.format("You selected to watch %s!", currMovie.name);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();


                if (clicks == 0) {
                    firstMovie = currMovie;
                }
                lastMovie = currMovie;
                clicks++;





            }
        });

        wheelView.setSelected(0);
        wheelView.getOnWheelItemSelectListener().onWheelItemSelected(wheelView,wheelView.getSelectionDrawable(),0);
        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //wheelView.setSelectionAngle(-wheelView.getAngleForPosition(5));
                wheelView.setMidSelected();
            }
        }, 3000); */
    }

    //for saving metrics info
    @Override
    public void onPause() {
        super.onPause();
        file_saved = true;
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(temp));
            if (firstMovie != null) {
                out.write("first click; " + firstMovie.name + "\nnumber of clicks; " + clicks + "\nnumber of movies shown interest in; " + shown_interest
                        + "\nlast click; " + lastMovie.name);

            }

            else {
                out.write("first click; None" +  "\nnumber of clicks; " + clicks + "\nnumber of movies shown interest in; " + shown_interest
                        + "\nlast click; None");
            }
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        TransferObserver uploadObserver = transferUtility.upload(
                "appcon-storage",
                temp.getName()
                ,temp);

        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                                /*CharSequence text = "Upload Finished! This will last one day in our servers.";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                                toast.show();
                                */
                    Regions clientRegion = Regions.DEFAULT_REGION;
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
            }
        } );
    }

    public void onResume() {
        super.onResume();
        file_saved = false;
    }

    public void onDestroy() {
        super.onDestroy();
        if (!file_saved) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(temp));
            out.write("first click; "+firstMovie.name + "\nnumber of clicks; "+clicks + "\nnumber of movies shown interest in; " + shown_interest
                    + "\nlast click; " + lastMovie.name);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        TransferObserver uploadObserver = transferUtility.upload(
                "appcon-storage",
                temp.getName()
                ,temp);

        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                                /*CharSequence text = "Upload Finished! This will last one day in our servers.";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                                toast.show();
                                */
                    Regions clientRegion = Regions.DEFAULT_REGION;
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
            }
        } );
        }
    }

    //get the materials darker contrast
    private int getContrastColor(Map.Entry<String, Integer> entry) {
        String colorName = MaterialColor.getColorName(entry);
        return MaterialColor.getContrastColor(colorName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    static class MovieAdapter extends WheelArrayAdapter<Movie> {
        MovieAdapter(List<Movie> entries) {
            super(entries);
        }
        @Override
        public Drawable getDrawable(int position) {
            Drawable[] drawable = new Drawable[] {
                getItem(position).drawable
            };
            return new LayerDrawable(drawable);
        }

    }

    static class MaterialColorAdapter extends WheelArrayAdapter<Map.Entry<String, Integer>> {
        MaterialColorAdapter(List<Map.Entry<String, Integer>> entries) {
            super(entries);
        }

        @Override
        public Drawable getDrawable(int position) {

            Drawable[] drawable = new Drawable[] {
                    createOvalDrawable(getItem(position).getValue()),
                    new TextDrawable(String.valueOf(position))
            };
            return new LayerDrawable(drawable);
        }

        private Drawable createOvalDrawable(int color) {
            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            shapeDrawable.getPaint().setColor(color);
            return shapeDrawable;
        }
    }

    private void setFlipperImage(int res) {
        Log.i("Set Filpper Called", res+"");
        ImageView image = new ImageView(getApplicationContext());
        image.setBackgroundResource(res);
        viewFlipper.addView(image);
    }

    public static String getTextSize(String text,int size) {
        return "<span style=\"size:"+size+"\" >"+text+"</span>";

    }

    public static String getStars(double rating) {
        rating /= 2;
        String myStars = "IMDB Rating: ";
        for (int i = 0; i < (int) rating; i++) {
            myStars += "\u2605";
        }
        myStars += " ("+ rating*2 +"/10)\n";

        return myStars;
    }

    public static void watchYoutubeVideo(Activity activity, String id){

        Log.d("YOUTUBE",id);
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(id));
        activity.startActivity(webIntent);

    }
}
