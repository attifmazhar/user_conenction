package com.project.myapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.myapp.pref.SettingsPrefHandler;

import java.util.ArrayList;
import java.util.Iterator;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
@SuppressLint("InflateParams")
public class CustomAdapter extends BaseAdapter {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<ListModel> data;
    private static LayoutInflater inflater = null;
    public Resources res;
    ListModel tempValues = null;
    String countryName = "";
    int i = 0;
    int select = 0;
    String nationText;
    public static boolean isUserOutOfRange;
    private ListModel model;
    private SettingsPrefHandler prefHandler;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/");

    /*************  CustomAdapter Constructor *****************/
    public CustomAdapter(Activity a, ArrayList<ListModel> d) {

        ListDevice.finalList = d;
        /********** Take passed values **********/
        activity = a;
        data = d;
        // res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    @Override
    public int getCount() {
        // Toast.makeText(activity, "ListSize : "+data.size(), Toast.LENGTH_SHORT).show();

        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder {

        public TextView nickName;
        //  public TextView nationality;
        public ImageView nationPic;
        public ImageView profilePic;
        public FrameLayout notAvailableLayout;
        public View v;
        private VisiblityListner mListner;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;


        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.mylist, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.nickName = (TextView) vi.findViewById(R.id.nickname2);
            // holder.nationality=(TextView)vi.findViewById(R.id.tv_nationality2);
            holder.profilePic = (ImageView) vi.findViewById(R.id.pic2);
            holder.nationPic = (ImageView) vi.findViewById(R.id.img_nationality2);
            holder.notAvailableLayout = (FrameLayout) vi.findViewById(R.id.no_more_available);
            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
            prefHandler = new SettingsPrefHandler(vi.getContext());
            holder.v = vi;
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.nickName.setText("No user found");
            //	 holder.nationality.setText("Try searching again!");
            vi.setVisibility(View.GONE);
        } else {

            /***** Get each Model object from Arraylist ********/

            tempValues = data.get(position);
            /************  Set Model values in Holder elements ***********/
            nationText = tempValues.getNationality();

            holder.nickName.setText(tempValues.getNickName());
            //holder.nationality.setText( nationText );

            if (tempValues.isOutOfRange()) {
                isUserOutOfRange = true;
                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
            } else {
                isUserOutOfRange = false;
                holder.notAvailableLayout.setVisibility(View.GONE);
                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#00000000"));
            }

            if (!tempValues.getVisibility()) {
                Log.d("CustomAdapter", "1>>> " + tempValues.getVisibility());
                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
            }

            if (tempValues.getConnectionState().equalsIgnoreCase("false")) {
                Log.d("CustomAdapter", "2>>> " + tempValues.getConnectionState());
                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
            }

            if (tempValues.getPermissionState().equalsIgnoreCase("false")) {
                Log.d("CustomAdapter", "3>>> " + tempValues.getPermissionState());
                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
            }

            if (tempValues.getAppState().equalsIgnoreCase("false")) {
                Log.d("CustomAdapter", "4>>> " + tempValues.getAppState());
                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
            }

            if (tempValues.getPositionState().equalsIgnoreCase("false")) {
                Log.d("CustomAdapter", "5>>> " + tempValues.getPositionState());
                holder.notAvailableLayout.setVisibility(View.VISIBLE);
                holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
            }

            enableSoundEffect(holder, holder.notAvailableLayout.getVisibility() == View.GONE);

            Glide
                    .with(activity.getApplication())
                    .load(data.get(position).getProfilePicUrl()).asBitmap().dontAnimate()
                    .into(holder.profilePic);

            holder.nationPic.setImageBitmap(tempValues.getNationPic());
            NationalityAvtivity act1 = new NationalityAvtivity();

            act1.setModelDat();
            Iterator<SpinnerModel> i = act1.CustomListViewValuesArr.iterator();
            while (i.hasNext()) {
                SpinnerModel mymodel = i.next();
                if (mymodel.getNationality().contains(nationText)) {
                    Bitmap bitmap = BitmapFactory.decodeResource(vi.getContext().getResources(), mymodel.getFlag());
                    holder.nationPic.setImageBitmap(bitmap);
                    countryName = mymodel.getNationality();
                    break;
                }
            }

            if (data.get(position).getDeviceAddress() != null) {
                databaseReference.child(data.get(position).getDeviceAddress()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        model = dataSnapshot.getValue(ListModel.class);

                        if (model == null) return;

                        if (model.isUserVisibility()) {
                            Log.d("CustomAdapter", "Mac Address :" + data.get(position).getDeviceAddress());
                            holder.notAvailableLayout.setVisibility(View.VISIBLE);
                            holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                        } else {
                            holder.notAvailableLayout.setVisibility(View.GONE);
                            holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#00000000"));
                        }
                        if (model.getConnectionState().equalsIgnoreCase("false")) {
                            Log.d("CustomAdapter", "Mac Address :" + data.get(position).getDeviceAddress());
                            holder.notAvailableLayout.setVisibility(View.VISIBLE);
                            holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                        } else {
                            holder.notAvailableLayout.setVisibility(View.GONE);
                            holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#00000000"));
                        }
                        if (model.getPermissionState().equalsIgnoreCase("false")) {
                            Log.d("CustomAdapter", "Mac Address :" + data.get(position).getDeviceAddress());
                            holder.notAvailableLayout.setVisibility(View.VISIBLE);
                            holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                        } else {
                            holder.notAvailableLayout.setVisibility(View.GONE);
                            holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#00000000"));
                        }
                        if (model.getPositionState().equalsIgnoreCase("false")) {
                            Log.d("CustomAdapter", "Mac Address :" + data.get(position).getDeviceAddress());
                            holder.notAvailableLayout.setVisibility(View.VISIBLE);
                            holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                        } else {
                            holder.notAvailableLayout.setVisibility(View.GONE);
                            holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#00000000"));
                        }
                        if (model.getAppState().equalsIgnoreCase("false")) {
                            Log.d("CustomAdapter", "Mac Address :" + data.get(position).getDeviceAddress());
                            holder.notAvailableLayout.setVisibility(View.VISIBLE);
                            holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                        } else {
                            holder.notAvailableLayout.setVisibility(View.GONE);
                            holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#00000000"));
                        }

                        enableSoundEffect(holder, holder.notAvailableLayout.getVisibility()== View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
                  /* h           older.image.setImageResource(
                               res.getIdentifier(
                               "com.androidexample.customlistview:drawable/"+tempValues.getImage()
                               ,null,null));*/

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            //vi.setOnClickListener(new OnItemClickListener( position ));

            //holder.profilePic.setTag(tempValues.getMacID());
            holder.profilePic.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempValues = data.get(position);
                    select++;
                    Log.d("SelectTime-------------", Integer.toString(select));
                    if (select>6) {
                        prefHandler.setStartuptime(true);
                    }
                    else
                        prefHandler.setStartuptime(false);
                    //a-comment
                    if(!tempValues.isOutOfRange() && tempValues.getVisibility() &&
                            !tempValues.getConnectionState().equalsIgnoreCase("false") &&
                            !tempValues.getPermissionState().equalsIgnoreCase("false") &&
                            !tempValues.getAppState().equalsIgnoreCase("false") &&
                            !tempValues.getPositionState().equalsIgnoreCase("false")) {
                        if (!prefHandler.getStartuptime()) {
                            Intent i2 = new Intent(activity.getBaseContext(), FullScreenActivity.class);
                            i2.putExtra("Position", position);
                            activity.startActivity(i2);
                        }
                        else
                            Toast.makeText(activity.getBaseContext(), "NO MORE USERS AVAILABLE", Toast.LENGTH_SHORT).show();
                    }
                   // else
                      //  Toast.makeText(activity.getBaseContext(), "User not available!", Toast.LENGTH_SHORT).show();
                }
            });

            holder.nationPic.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Toast.makeText(v.getContext(), data.get(position).getNationality(), Toast.LENGTH_LONG).show();
                }
            });
        }
        return vi;
    }

    private void enableSoundEffect(ViewHolder holder,boolean enable) {
       // holder.nationPic.setSoundEffectsEnabled(enable);
        holder.profilePic.setSoundEffectsEnabled(enable);
      //  holder.notAvailableLayout.setSoundEffectsEnabled(enable);
    }
}

interface VisiblityListner {
    boolean userVisiblity();
}