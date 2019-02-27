package com.project.myapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.myapp.pref.SettingsPrefHandler;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FullScreenImageAdapter extends RecyclerView.Adapter<FullScreenImageAdapter.ViewHolder> {

    private static final String PREF_NAME = "settings_pref";
    public static ViewHolder holder;
    private static LayoutInflater inflater;
    static {
        inflater = null;
    }
    List<Bitmap> f14b;
    ListModel tempValues;
    String countryName = "";
    private Context _activity;
    @SuppressWarnings("unused")
    private ArrayList<String> _imagePaths;
    private ArrayList<ListModel> data;
    FlagItemListener flagItemListener = new FlagItemListener() {
        @Override
        public void onFlagClicked(View view, int position) {

            String countryName = data.get(position).getNationality();
            // Toast.makeText(view.getContext(), countryName, Toast.LENGTH_LONG).show();

            Toast toast=new Toast(view.getContext());
            toast.setGravity(Gravity.BOTTOM,0,100);


            TextView tv=new TextView(view.getContext());
            Resources res = view.getContext().getResources();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Drawable imgDrawable=res.getDrawable(R.drawable.roundedcorner);
                tv.setBackground(imgDrawable);
            }
            //tv.setBackgroundColor(Color.GRAY);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(15);

            Typeface t=Typeface.create("Sans-serif",Typeface.NORMAL);
            tv.setTypeface(t);
            tv.setPadding(30,25,30,25);



            tv.setText("countryName");
            toast.setView(tv);
            toast.show();



        }
    };
    private String nationText;
    private DatabaseReference gpsStatusDatabaseReference;
    private DatabaseReference visibilityStatusDatabaseReference;
    private DatabaseReference connectionStatusDatabaseReference;
    private DatabaseReference appStatusDatabaseReference;
    private DatabaseReference locationStatusDatabaseReference;
    private ValueEventListener locationStatusVel;
    private ValueEventListener appStatusVel;
    private ValueEventListener gpsStatusVEL;
    private ValueEventListener visibilityStatusVEL;
    private ValueEventListener connectionStatusVEL;
    private DatabaseReference positionStatusDatabaseReference;
    private ValueEventListener positionVEL;
    private SharedPreferences pref;
    private SettingsPrefHandler settingsPrefHandler;
    private String gpsStatus;
    private Boolean appStatus;
    private boolean visibilityStatus;
    private String internetStatus;
    private Boolean positionStatus;
    private boolean isUserOutOfArea = false;
    private SharedPreferences sp;
    private String myLocation;
    private boolean firstTime;
    private int pos;
    public LocationModel locationModel;


    public FullScreenImageAdapter(Context context, ArrayList<ListModel> d, int pos) {
        this.tempValues = null;
        this.f14b = new ArrayList<Bitmap>();
        this._activity = context;
        this.data = d;
        this.pos = pos;
        firstTime = true;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.full_screen, parent, false);
        locationModel = new LocationModel();
        return new FullScreenImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        pref = _activity.getSharedPreferences(PREF_NAME, 0);
        settingsPrefHandler = new SettingsPrefHandler(_activity);
        holder.btnConnect.setVisibility(View.VISIBLE);
        if (firstTime) {
            holder.btnConnect.setOnClickListener(view -> {
                holder.btnConnect.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(_activity, Transparent.class);
                _activity.startActivity(intent);
                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(500);
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);

                sendNotificationToOtherUser();

            });

            Log.e("TAG", "MAC Address: " + settingsPrefHandler.getMAC());
            if (this.data.size() <= 0) {
                holder.layout.setVisibility(View.GONE);
            } else {
                this.tempValues = this.data.get(pos);
                //   Log.d("tempValuescheck", this.tempValues.getMacID());
                this.nationText = this.tempValues.getNationality();
                holder.nickName.setText(this.tempValues.getNickName());
                Log.d("task1", "getNickName==" + this.tempValues.isOutOfRange());
                holder.nationality.setText(this.tempValues.getNationality());
                Glide.with(_activity).load(data.get(pos).getProfilePicUrl()).asBitmap().dontAnimate().into(holder.profilePic);
                NationalityAvtivity act1 = new NationalityAvtivity();
                gpsStatusVEL = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (FullScreenActivity.backPressed) {
                            gpsStatusDatabaseReference.removeEventListener(gpsStatusVEL);
                        } else {
                            gpsStatus = dataSnapshot.getValue(String.class);
                            Log.e("TAG", "Sanchit: GPS " + dataSnapshot.getValue(String.class));
                            if (gpsStatus != null && !gpsStatus.equals("true")) {
                                holder.btnConnect.setEnabled(false);
                                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                                holder.user_status.setText("User set position off");
                                holder.user_status.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };


                visibilityStatusVEL = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (FullScreenActivity.backPressed) {
                            visibilityStatusDatabaseReference.removeEventListener(visibilityStatusVEL);
                        } else {
                            visibilityStatus = dataSnapshot.getValue() != null && dataSnapshot.getValue().equals(true);
                            if (!visibilityStatus) {
                                holder.btnConnect.setEnabled(true);
                                holder.notAvailableLayout.setVisibility(View.GONE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                                holder.user_status.setText("User set visibility off");
                                holder.user_status.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };






                appStatusVel = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (FullScreenActivity.backPressed) {
                            appStatusDatabaseReference.removeEventListener(appStatusVel);
                        } else {
                            try {
                                Log.e("TAG", "Sanchit: Visibility " + dataSnapshot.getValue(Boolean.class));
                                appStatus = dataSnapshot.getValue(Boolean.class);
                            } catch (Exception e) {}

                            if (appStatus != null && !appStatus.equals("true")) {
                                holder.btnConnect.setEnabled(true);
                                holder.notAvailableLayout.setVisibility(View.GONE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                                holder.user_status.setText("User device off");
                                holder.user_status.setVisibility(View.GONE);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.user_status.setVisibility(View.GONE);
                                    }
                                }, 3000);

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                };

                connectionStatusVEL = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (FullScreenActivity.backPressed) {
                            connectionStatusDatabaseReference.removeEventListener(connectionStatusVEL);
                        } else {
                            internetStatus = dataSnapshot.getValue(String.class);
                            Log.e("TAG", "Sanchit: Internet " + dataSnapshot.getValue(String.class));

                            if (internetStatus != null && !internetStatus.equals("true")) {
                                holder.btnConnect.setEnabled(false);
                                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                                holder.user_status.setText("Internet connection missing");
                                holder.user_status.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                positionVEL = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (FullScreenActivity.backPressed) {
                            positionStatusDatabaseReference.removeEventListener(positionVEL);
                        } else {
//                            positionStatus = dataSnapshot.getValue(Boolean.class);
                          //  Log.e("TAG", "Sanchit: Position " + dataSnapshot.getValue(String.class));
                            if (positionStatus != null && !positionStatus.equals("true")) {
                                holder.btnConnect.setEnabled(false);
                                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                                holder.user_status.setText("User set position off");
                                holder.user_status.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                sp = PreferenceManager.getDefaultSharedPreferences(_activity);
                myLocation = sp.getString("location", "");

                locationStatusVel = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (FullScreenActivity.backPressed) {
                            locationStatusDatabaseReference.removeEventListener(locationStatusVel);
                        } else {
                            String locationString1[] = myLocation.split(":");//own location
                            Location startPoint = new Location("YourLocation");
                            startPoint.setLatitude(Double.valueOf(locationString1[0]));
                            startPoint.setLongitude(Double.valueOf(locationString1[1]));
                            String location = dataSnapshot.getValue(String.class);
                            String locationString2[] = location.split(":");//other user location
                            Location endPoint = new Location("othersLocation");
                            endPoint.setLatitude(Double.valueOf(locationString2[0]));
                            endPoint.setLongitude(Double.valueOf(locationString2[1]));

                            locationModel.setLat1(Double.valueOf(locationString1[0]));
                            locationModel.setLon1(Double.valueOf(locationString1[1]));
                            locationModel.setLat2(Double.valueOf(locationString2[0]));
                            locationModel.setLon2(Double.valueOf(locationString2[1]));
                            double distance = locationModel.get_distance_in_meter();

                            if (distance > 30) {
                                isUserOutOfArea = true;
                                holder.user_status.setText("User is out of range now");
                                holder.user_status.setVisibility(View.VISIBLE);
                                //Toast.makeText(_activity, "User is out of range now", Toast.LENGTH_SHORT).show();
                                holder.btnConnect.setEnabled(false);
                                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                            } else {
                                isUserOutOfArea = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                locationStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("location");

                gpsStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("permission");

                visibilityStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("visibility");

                connectionStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("connection");

                appStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("app");


                positionStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("position");

                //a-comments

//                gpsStatusDatabaseReference.addValueEventListener(gpsStatusVEL);
//                visibilityStatusDatabaseReference.addValueEventListener(visibilityStatusVEL);
//                connectionStatusDatabaseReference.addValueEventListener(connectionStatusVEL);
//                positionStatusDatabaseReference.addValueEventListener(positionVEL);
//                appStatusDatabaseReference.addValueEventListener(appStatusVel);

              //  Log.d("MaciDsa", this.data.get(pos).getMacID());
//                FirebaseDatabase.getInstance().getReference("users").child(settingsPrefHandler.getMAC()).addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                        try {
//                            if (positionStatus.equals("true") && internetStatus.equals("true") && gpsStatus.equals("true") && visibilityStatus && !isUserOutOfArea && appStatus.equals("true")) {
//                                holder.btnConnect.setEnabled(true);
//                                holder.notAvailableLayout.setVisibility(View.GONE);
//                                holder.user_status.setVisibility(View.GONE);
//                            }
//
//                        }catch (Exception e) {}
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

                act1.setModelDat();
                Iterator<SpinnerModel> i = act1.CustomListViewValuesArr.iterator();
                while (i.hasNext()) {
                    SpinnerModel mymodel = i.next();
                    if (mymodel.getNationality().contains(this.nationText)) {
                        holder.nationPic.setImageBitmap(BitmapFactory.decodeResource(_activity.getResources(), mymodel.getFlag()));
                        countryName = mymodel.getNationality();

                        break;
                    }
                }

                holder.nationPic.setOnClickListener(new OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        // Toast.makeText(v.getContext(), data.get(position).getNationality(), Toast.LENGTH_LONG).show();

                        Toast toast=new Toast(_activity);
                        toast.setGravity(Gravity.BOTTOM,0,100);


                        System.out.println("ddd"+data.get(position).getNationality());

                        TextView tv=new TextView(_activity);
                        Resources res = _activity.getResources();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            Drawable imgDrawable=res.getDrawable(R.drawable.roundedcorner);
                            tv.setBackground(imgDrawable);
                        }
                        tv.setTextColor(Color.WHITE);
                        tv.setTextSize(15);

                        Typeface t=Typeface.create("Sans-serif",Typeface.NORMAL);
                        tv.setTypeface(t);
                        tv.setPadding(30,25,30,25);



                        tv.setText( data.get(position).getNationality());
                        toast.setView(tv);
                        toast.show();

                    }
                });


            }




            firstTime = false;
        } else {
            holder.btnConnect.setOnClickListener(view -> {
                Intent intent = new Intent(_activity, Transparent.class);
                _activity.startActivity(intent);
                holder.btnConnect.setVisibility(View.GONE);
                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(500);
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);

                sendNotificationToOtherUser();
            });


            Log.e("TAG", "MAC Address: " + settingsPrefHandler.getMAC());
            if (this.data.size() <= 0) {
                holder.layout.setVisibility(View.GONE);
            } else {


                //this.tempValues = null;
                this.tempValues = this.data.get(position);
             //   Log.d("tempValuescheck", this.tempValues.getMacID());
                this.nationText = this.tempValues.getNationality();
                holder.nickName.setText(this.tempValues.getNickName());
                Log.d("task1", "getNickName==" + this.tempValues.isOutOfRange());
                holder.nationality.setText(this.tempValues.getNationality());
                //holder.profilePic.setImageBitmap(this.tempValues.getProfilePic());
                Glide
                        .with(_activity)
                        .load(data.get(position).getProfilePicUrl()).asBitmap().dontAnimate()
                        .into(holder.profilePic);
                NationalityAvtivity act1 = new NationalityAvtivity();

                gpsStatusVEL = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (FullScreenActivity.backPressed) {
                            gpsStatusDatabaseReference.removeEventListener(gpsStatusVEL);
                        } else {
                            gpsStatus = dataSnapshot.getValue(String.class);
                            Log.e("TAG", "Sanchit: GPS " + dataSnapshot.getValue(String.class));
                            if (gpsStatus != null && !gpsStatus.equals("true")) {
                                holder.btnConnect.setEnabled(false);
                                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                                holder.user_status.setText("User set position off");
                                holder.user_status.setVisibility(View.VISIBLE);

                                final Handler handler = new Handler();
                                handler.postDelayed(() -> holder.user_status.setVisibility(View.GONE), 3000);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };


                visibilityStatusVEL = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (FullScreenActivity.backPressed) {
                            visibilityStatusDatabaseReference.removeEventListener(visibilityStatusVEL);
                        } else {
                            try {
                                Log.e("TAG", "Sanchit: Visibility " + dataSnapshot.getValue(Boolean.class));
                                visibilityStatus = dataSnapshot.getValue(Boolean.class);
                            } catch (Exception e) {}

                            if (FullScreenActivity.backPressed) {

                            }

                            if (!visibilityStatus) {
                                holder.btnConnect.setEnabled(false);
                                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                                holder.user_status.setText("User set visibility off");
                                holder.user_status.setVisibility(View.VISIBLE);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.user_status.setVisibility(View.GONE);
                                    }
                                }, 3000);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };


                //

                appStatusVel = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (FullScreenActivity.backPressed) {
                            appStatusDatabaseReference.removeEventListener(appStatusVel);
                        } else {
                           // Log.e("TAG", "Rao: APP STAUTS " + dataSnapshot.getValue(String.class));
                            try {
                                Log.e("TAG", "Sanchit: Visibility " + dataSnapshot.getValue(Boolean.class));
                                appStatus = dataSnapshot.getValue(Boolean.class);
                            } catch (Exception e) {}

                            if (appStatus != null && !appStatus.equals("true")) {
                                holder.btnConnect.setEnabled(false);
                                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                                holder.user_status.setText("User device off");
                                holder.user_status.setVisibility(View.VISIBLE);


                                final Handler handler = new Handler();
                                handler.postDelayed(() -> holder.user_status.setVisibility(View.GONE), 3000);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                };

                connectionStatusVEL = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (FullScreenActivity.backPressed) {
                            connectionStatusDatabaseReference.removeEventListener(connectionStatusVEL);
                        } else {
                            internetStatus = dataSnapshot.getValue(String.class);
                            Log.e("TAG", "Sanchit: Internet " + dataSnapshot.getValue(String.class));

                            if (internetStatus != null && !internetStatus.equals("true")) {
                                holder.btnConnect.setEnabled(false);
                                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                                holder.user_status.setText("Internet connection missing");
                                holder.user_status.setVisibility(View.VISIBLE);


                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.user_status.setVisibility(View.GONE);
                                    }
                                }, 3000);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                positionVEL = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (FullScreenActivity.backPressed) {
                            positionStatusDatabaseReference.removeEventListener(positionVEL);
                        } else {
                            try {
                                Log.e("TAG", "Sanchit: Visibility " + dataSnapshot.getValue(Boolean.class));
                                positionStatus  = dataSnapshot.getValue(Boolean.class);
                            } catch (Exception e) {}

                            if (positionStatus != null && !positionStatus.equals("true")) {
                                holder.btnConnect.setEnabled(false);
                                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                                holder.user_status.setText("User set position off");
                                holder.user_status.setVisibility(View.VISIBLE);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.user_status.setVisibility(View.GONE);
                                    }
                                }, 3000);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                sp = PreferenceManager.getDefaultSharedPreferences(_activity);
                myLocation = sp.getString("location", "");

                locationStatusVel = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (FullScreenActivity.backPressed) {
                            locationStatusDatabaseReference.removeEventListener(locationStatusVel);
                        } else {
                            String locationString1[] = myLocation.split(":");//own location
                            Location startPoint = new Location("YourLocation");
                            startPoint.setLatitude(Double.valueOf(locationString1[0]));
                            startPoint.setLongitude(Double.valueOf(locationString1[1]));
                            String location = dataSnapshot.getValue(String.class);
                            String locationString2[] = location.split(":");//other user location
                            Location endPoint = new Location("othersLocation");
                            endPoint.setLatitude(Double.valueOf(locationString2[0]));
                            endPoint.setLongitude(Double.valueOf(locationString2[1]));

                            locationModel.setLat1(Double.valueOf(locationString1[0]));
                            locationModel.setLon1(Double.valueOf(locationString1[1]));
                            locationModel.setLat2(Double.valueOf(locationString2[0]));
                            locationModel.setLon2(Double.valueOf(locationString2[1]));
                            double distance = locationModel.get_distance_in_meter();

                            if (distance > 30) {
                                isUserOutOfArea = true;
                                holder.user_status.setText("User is out of range now");
                                holder.user_status.setVisibility(View.VISIBLE);
                                //Toast.makeText(_activity, "User is out of range now", Toast.LENGTH_SHORT).show();
                                holder.btnConnect.setEnabled(false);
                                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                            } else {
                                isUserOutOfArea = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };


                locationStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("location");

                gpsStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("permission");

                visibilityStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("visibility");

                connectionStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("connection");

                appStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("app");


                positionStatusDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(settingsPrefHandler.getMAC()).child("position");

                //a=comments
//                gpsStatusDatabaseReference.addValueEventListener(gpsStatusVEL);
//                visibilityStatusDatabaseReference.addValueEventListener(visibilityStatusVEL);
//                connectionStatusDatabaseReference.addValueEventListener(connectionStatusVEL);
//                positionStatusDatabaseReference.addValueEventListener(positionVEL);
//                appStatusDatabaseReference.addValueEventListener(appStatusVel);

              //  Log.d("MaciDsa", this.data.get(position).getMacID());
                FirebaseDatabase.getInstance().getReference("users").child(settingsPrefHandler.getMAC()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        try{

                        if (positionStatus.equals("true") && internetStatus.equals("true") && gpsStatus.equals("true") && visibilityStatus && !isUserOutOfArea && appStatus.equals("true")) {
                            holder.btnConnect.setEnabled(true);
                            holder.notAvailableLayout.setVisibility(View.GONE);
                            holder.user_status.setVisibility(View.GONE);
                        }
                        } catch (Exception e) {Log.e("","");}

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




                act1.setModelDat();
                Iterator<SpinnerModel> i = act1.CustomListViewValuesArr.iterator();
                while (i.hasNext()) {
                    SpinnerModel mymodel = i.next();
                    if (mymodel.getNationality().contains(this.nationText)) {
                        holder.nationPic.setImageBitmap(BitmapFactory.decodeResource(_activity.getResources(), mymodel.getFlag()));
                        countryName = mymodel.getNationality();
                        break;
                    }
                }
                holder.nationPic.setOnClickListener(v -> flagItemListener.onFlagClicked(v, position));
                holder.nationPic.setOnClickListener(new OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        // Toast.makeText(v.getContext(), data.get(position).getNationality(), Toast.LENGTH_LONG).show();

                        Toast toast=new Toast(_activity);
                        toast.setGravity(Gravity.BOTTOM,0,100);


                        System.out.println("ddd"+data.get(position).getNationality());

                        TextView tv=new TextView(_activity);
                        Resources res = _activity.getResources();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            Drawable imgDrawable=res.getDrawable(R.drawable.roundedcorner);
                            tv.setBackground(imgDrawable);
                        }
                        tv.setTextColor(Color.WHITE);
                        tv.setTextSize(15);

                        Typeface t=Typeface.create("Sans-serif",Typeface.NORMAL);
                        tv.setTypeface(t);
                        tv.setPadding(30,25,30,25);

                        tv.setText( data.get(position).getNationality());
                        toast.setView(tv);
                        toast.show();

                    }
                });
            }
        }
    }

//    private void updateConnectedStatusFcmDB() {
//
//    }
//
//    private void gotoGalleryScreen() {
//        holder.btnConnect.postDelayed(() -> {
//
//            sendNotificationToOtherUser();
//
//            }, 2000);
//    }

    private void sendNotificationToOtherUser() {
        ConnectionService.updateOtherUserConnectedStatus(settingsPrefHandler.getMAC(), data.get(0));
    }

    @Override
    public int getItemCount() {
        if (this.data == null || this.data.size() <= 0) {
            return 1;
        }
        return this.data.size();
    }

    public interface FlagItemListener {

        void onFlagClicked(View view, int position);


    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        LinearLayout layoutBase;
        ImageView nationPic;
        TextView nationality;
        TextView nickName;
        ImageView profilePic;
        Button btnConnect;
        FrameLayout notAvailableLayout;
        TextView user_status;

        ViewHolder(View itemView) {
            super(itemView);

            this.layoutBase = itemView.findViewById(R.id.linearLayoutBase);
            this.layout = itemView.findViewById(R.id.linearLayout1);
            this.nickName = itemView.findViewById(R.id.nickname2);
            this.nationality = itemView.findViewById(R.id.tv_nationality2);
            this.profilePic = itemView.findViewById(R.id.pic2);
            this.nationPic = itemView.findViewById(R.id.img_nationality2);
            this.notAvailableLayout = itemView.findViewById(R.id.no_more_available_full);
            this.btnConnect = itemView.findViewById(R.id.btnConnect);
            this.user_status = itemView.findViewById(R.id.user_status_textview);
        }
    }

    /* renamed from: FullScreenImageAdapter.1 */
    class C02371 implements OnClickListener {
        C02371() {
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @SuppressLint("ShowToast")
        public void onClick(View v) {

            //  Toast.makeText(v.getContext(), FullScreenImageAdapter.this.countryName, Toast.LENGTH_LONG).show();

           /* Toast toast=new Toast(v.getContext());
            toast.setGravity(Gravity.BOTTOM,0,100);


            TextView tv=new TextView(v.getContext());
            tv.setBackground(Drawable.createFromPath(String.valueOf(R.drawable.roundedcorner)));
            //tv.setBackgroundColor(Color.GRAY);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(15);

            Typeface t=Typeface.create("Sans-serif",Typeface.NORMAL);
            tv.setTypeface(t);
            tv.setPadding(30,25,30,25);



            tv.setText("Check internet connection");
            toast.setView(tv);
            toast.show();*/

            holder.user_status.setVisibility(View.VISIBLE);
            holder.user_status.setText("User set position off");




        }
    }


}
