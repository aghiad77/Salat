package com.example.salat.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.salat.R;
import com.example.salat.models.Database;
import com.example.salat.models.Pray;
import com.example.salat.models.lastLocation;
import com.example.salat.services.LocationService;
import com.example.salat.utils.PrayTime;
import com.example.salat.utils.Utils;
import com.example.salat.viewModels.FirebaseViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public TextView dayName , monthHijri , dayHijri , yearHijri , monthGreo , dayGreo , yearGreo , Duhurtime ,
            Sunrisetime , Fajrtime , Ishatime , Maghribtime , Asrtime , duration;
    public ImageView calenderImage , Duhuradd , Fajradd , Asradd , Maghribadd , Ishaaadd ,
                      Duhurbackground , Fajrbackground , Asrbackground , Maghribbackground , Ishaabackground ;
    DatePickerDialog datePickerDialog;
    public static String currentDate;
    Database db;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;
    private FirebaseViewModel firebaseViewModel;
    FirebaseDatabase firebaseDatabase;
    public static boolean PrayFirsttime = false;
    public static Location currentlocation;
    public static List<Pray> prayList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;

    private BroadcastReceiver getLocation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            lastLocation loc = db.getLocation();
            if(loc!=null){
                db.deleteLocation(loc.getId());
                db.saveLocation(LocationService.latitude, LocationService.longitude);
            }
            if(!PrayFirsttime) {
                if(db.getLocation()==null) {
                    getPrayTimes(LocationService.latitude, LocationService.longitude, new Date());
                    db.saveLocation(LocationService.latitude,LocationService.longitude);
                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        initial();
        getPrayerTimes();
        calenderImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                dayName.setText(new SimpleDateFormat("EEEE").format(calendar.getTime()));
                                setHijriDate(year,month,day);
                                setGreoDate();
                                currentDate = Utils.getDateFromDatePicker(calendar.getTime());
                                if (LocationService.latitude != 0 && LocationService.longitude !=0 ) {
                                    getPrayTimes(LocationService.latitude, LocationService.longitude, calendar.getTime());
                                }else if (currentlocation != null){
                                    getPrayTimes(currentlocation.getLatitude(), currentlocation.getLongitude() , calendar.getTime());
                                }else if (db.getLocation()!=null){
                                    getPrayTimes(db.getLocation().getLat(), db.getLocation().getLongt(), calendar.getTime());
                                }
                                setallBackgroudColorWhite();
                                if(prayList!=null) {
                                    prayList.clear();
                                    prayList = db.getPray(currentDate);
                                    for (Pray pray : prayList) {
                                        setBackgroundColor(pray.getPray(), "gold");
                                    }
                                }
                            }
                        }, year, month, dayOfMonth);
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        Fajradd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                  //showcodeDialog("Did you perform the Fajr prayer?","Fajr");
                  showDialog(MainActivity.this,"Did you perform the Fajr prayer?","Fajr");
            }
        });

        Duhuradd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                //showcodeDialog("Did you perform the Duhur prayer?","Duhur");
                showDialog(MainActivity.this,"Did you perform the Duhur prayer?","Duhur");
            }
        });

        Asradd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                //showcodeDialog("Did you perform the Asr prayer?","Asr");
                showDialog(MainActivity.this,"Did you perform the Asr prayer?","Asr");
            }
        });

        Maghribadd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                //showcodeDialog("Did you perform the Maghrib prayer?","Maghrib");
                showDialog(MainActivity.this,"Did you perform the Maghrib prayer?","Maghrib");
            }
        });

        Ishaaadd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                //showcodeDialog("Did you perform the Ishaa prayer?","Ishaa");
                showDialog(MainActivity.this,"Did you perform the Ishaa prayer?","Ishaa");
            }
        });
    }

    @SuppressLint({"NewApi", "MissingPermission"})
    private void initial() {

        Typeface face1 = ResourcesCompat.getFont(this, R.font.gothic);
        Typeface face2 = ResourcesCompat.getFont(this, R.font.geniso);

        db = new Database(this);
        db.onUpgrade(db.getWritableDatabase(),0,1);
        currentDate = Utils.getGregorianDate();
        prayList = db.getPray(currentDate);

        calenderImage=(ImageView) findViewById(R.id.calender);
        Fajradd=(ImageView) findViewById(R.id.fajr_add);
        Duhuradd=(ImageView) findViewById(R.id.duhur_add);
        Asradd=(ImageView) findViewById(R.id.asr_add);
        Maghribadd=(ImageView) findViewById(R.id.maghrib_add);
        Ishaaadd=(ImageView) findViewById(R.id.isha_add);
        Duhurbackground=(ImageView) findViewById(R.id.rectangle1);
        Fajrbackground=(ImageView) findViewById(R.id.rectangle3);
        Ishaabackground=(ImageView) findViewById(R.id.rectangle4);
        Maghribbackground=(ImageView) findViewById(R.id.rectangle5);
        Asrbackground=(ImageView) findViewById(R.id.rectangle6);
        dayName=(TextView)findViewById(R.id.day_name);
        duration=(TextView)findViewById(R.id.duration);
        dayName.setTypeface(face1);
        duration.setTypeface(face1);

        monthHijri=(TextView)findViewById(R.id.month_hijri_name);
        monthGreo=(TextView)findViewById(R.id.month_greo_name);
        monthHijri.setTypeface(face1);
        monthGreo.setTypeface(face1);

        dayHijri=(TextView)findViewById(R.id.day_hijri);
        dayGreo=(TextView)findViewById(R.id.greo_day);
        dayHijri.setTypeface(face1);
        dayGreo.setTypeface(face1);

        yearHijri=(TextView)findViewById(R.id.hijri_year);
        yearGreo=(TextView)findViewById(R.id.greo_year);

        Fajrtime=(TextView)findViewById(R.id.Fajrtime);
        Sunrisetime=(TextView)findViewById(R.id.Sunrisetime);
        Duhurtime=(TextView)findViewById(R.id.Dhuhrtime);
        Asrtime=(TextView)findViewById(R.id.Asrtime);
        Maghribtime=(TextView)findViewById(R.id.Maghribtime);
        Ishatime=(TextView)findViewById(R.id.Ishatime);
        Fajrtime.setTypeface(face1);
        Sunrisetime.setTypeface(face1);
        Duhurtime.setTypeface(face1);
        Asrtime.setTypeface(face1);
        Maghribtime.setTypeface(face1);
        Ishatime.setTypeface(face1);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        setHijriDate(year,month,dayOfMonth);
        setGreoDate();
        dayName.setText(new SimpleDateFormat("EEEE").format(calendar.getTime()));
        if(prayList!=null) {
            for (Pray pray : prayList) {
                setBackgroundColor(pray.getPray(), "gold");
            }
        }
    }

    @SuppressLint("ResourceType")
    private void setBackgroundColor(String pray, String color) {
        if(color.equals("gold")){
            switch (pray) {

                case "Fajr":
                    //Fajrbackground.setBackgroundResource(R.raw.rectangle_2);
                    Fajrbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_2));
                    break;

                case "Duhur":
                    Duhurbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_2));
                    break;

                case "Asr":
                    Asrbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_2));
                    break;

                case "Maghrib":
                    Maghribbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_2));
                    break;

                case "Ishaa":
                    Ishaabackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_2));
                    break;
            }
        }else{
            switch (pray) {

                case "Fajr":
                    Fajrbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_1));
                    break;

                case "Duhur":
                    Duhurbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_1));
                    break;

                case "Asr":
                    Asrbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_1));
                    break;

                case "Maghrib":
                    Maghribbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_1));
                    break;

                case "Ishaa":
                    Ishaabackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_1));
                    break;
            }
        }
    }

    @SuppressLint("ResourceType")
    private void setallBackgroudColorWhite() {
        Fajrbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_1));
        Duhurbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_1));
        Asrbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_1));
        Maghribbackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_1));
        Ishaabackground.setImageDrawable(ContextCompat.getDrawable(this, R.raw.rectangle_1));
    }

    @SuppressLint("NewApi")
    private void setHijriDate(int year, int month, int day) {
        dayHijri.setText(Utils.getHijriDay(Utils.convertGregorianToHijri(year,month+1,day)));
        monthHijri.setText( Utils.getHijriMonthName(Utils.convertGregorianToHijri(year,month+1,day)));
        yearHijri.setText(Utils.getHijriYear(Utils.convertGregorianToHijri(year,month+1,day)));
    }

    @SuppressLint("NewApi")
    private void setGreoDate() {
        dayGreo.setText(Utils.getGregorianDay(Utils.getDateFromDatePicker(calendar.getTime())));
        monthGreo.setText(Utils.getGregorianMonthName(Utils.getDateFromDatePicker(calendar.getTime())));
        yearGreo.setText(Utils.getGregorianYear(Utils.getDateFromDatePicker(calendar.getTime())));

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLocationPermission();
        getLastLocation();
        LocalBroadcastManager.getInstance(this).registerReceiver(getLocation, new IntentFilter("ACTION_GET_LOCATION"));
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(LocationService.ACTION_START_FOREGROUND_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            currentlocation = location;
                            lastLocation loc = db.getLocation();
                            if(loc!=null){
                                db.deleteLocation(loc.getId());
                                db.saveLocation(location.getLatitude(), location.getLongitude());
                            }
                            if(!PrayFirsttime) {
                                if(db.getLocation()==null) {
                                    getPrayTimes(location.getLatitude(), location.getLongitude() ,  new Date());
                                    db.saveLocation(location.getLatitude(), location.getLongitude());
                                }
                            }
                        }
                    }
                });
    }

    private void getPrayerTimes() {
        lastLocation loc = db.getLocation();
        if (loc!=null){
            PrayFirsttime = true;
            getPrayTimes(loc.getLat(),loc.getLongt(),  new Date());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(getLocation);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(LocationService.ACTION_STOP_FOREGROUND_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void getPrayTimes(double latitude , double longitude , Date now){
        double timezone = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            timezone = Utils.getTimeZoneOffset();
        }
        // Test Prayer times here
        PrayTime prayers = new PrayTime();

        prayers.setTimeFormat(prayers.Time12);
        prayers.setCalcMethod(prayers.Makkah);
        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal,
                latitude, longitude, timezone);
        ArrayList<String> prayerNames = prayers.getTimeNames();

        if(!prayerTimes.isEmpty() && !prayerNames.isEmpty()) {
            PrayFirsttime = true;
            Fajrtime.setText(prayerTimes.get(0).split(" ")[0]);
            Sunrisetime.setText(prayerTimes.get(1).split(" ")[0]);
            Duhurtime.setText(prayerTimes.get(2).split(" ")[0]);
            Asrtime.setText(prayerTimes.get(3).split(" ")[0]);
            Maghribtime.setText(prayerTimes.get(5).split(" ")[0]);
            Ishatime.setText(prayerTimes.get(6).split(" ")[0]);
            calculateDuration(prayerTimes);
        }
    }

    private void calculateDuration(ArrayList<String> times) {
        //1
        List<String> names = Arrays.asList("FAJR", "SUNRISE", "DUHUR", "ASR", "MAGHRIB","ISHAA");
        Calendar nowcal = Calendar.getInstance();
        Calendar timecal = Calendar.getInstance();
        for (int i=0 ; i < times.size() ; i++){
            timecal.set(Calendar.HOUR , Integer.valueOf(times.get(i).split(" ")[0].split(":")[0]));
            timecal.set(Calendar.MINUTE , Integer.valueOf(times.get(i).split(" ")[0].split(":")[1]));
            if(times.get(i).split(" ")[1].equals("am"))
                timecal.set(Calendar.AM_PM , Calendar.AM);
            else
                timecal.set(Calendar.AM_PM , Calendar.PM);
            long dura = timecal.getTimeInMillis() - nowcal.getTimeInMillis();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(dura);
            if(minutes > 0){
                int Hour = (int) (minutes / 60);
                int minute = (int) (minutes % 60);
                if( Hour > 0 && minute > 0){
                    duration.setText(Hour+" HOUR "+ minute +" MINUTES TILL "+names.get(i));
                }else if ( Hour > 0 && minute == 0){
                    duration.setText(Hour+" HOUR TILL "+names.get(i));
                }else if ( Hour == 0 && minute > 0){
                    duration.setText(minute +" MINUTES TILL "+names.get(i));
                }
                break;
            }
        }
    }

    public static void getAddressFromLocation(final double latitude, final double longitude, final Context context) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List < Address > addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        String cityName = addressList.get(0).getAddressLine(0);
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)); //.append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                        Toast.makeText(context,cityName,Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    Log.e("Location Address Loader", "Unable connect to Geocoder", e);
                }
            }
        };
        thread.start();
    }

    public void showDialog(Activity activity , String title , String prayValue) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.newcustom_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title1 = dialog.findViewById(R.id.title);
        title1.setText(title);
        FrameLayout mDialogNo = dialog.findViewById(R.id.frmNo);
        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean exist = false;
                int id = 0;
                if(prayList!=null) {
                    for (Pray pray : prayList) {
                        if (pray.getPray().equals(prayValue)) {
                            exist = true;
                            id = pray.getId();
                            prayList.remove(pray);
                            break;
                        }
                    }
                    if (exist) {
                        db.deletePray(id);
                        setBackgroundColor(prayValue, "white");
                    }
                }
                dialog.dismiss();
            }
        });

        FrameLayout mDialogOk = dialog.findViewById(R.id.frmOk);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean exist = false;
                if(prayList!=null) {
                    for (Pray pray : prayList) {
                        if (pray.getPray().equals(prayValue)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        db.addPray(prayValue, currentDate);
                        setBackgroundColor(prayValue, "gold");
                        prayList.clear();
                        prayList = db.getPray(currentDate);
                    }
                }else{
                    db.addPray(prayValue, currentDate);
                    setBackgroundColor(prayValue, "gold");
                    prayList = db.getPray(currentDate);
                }
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO re-request
                }
                break;
            }
        }
    }

}