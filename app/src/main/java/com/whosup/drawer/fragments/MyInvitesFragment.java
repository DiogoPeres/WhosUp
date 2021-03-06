package com.whosup.drawer.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.whosup.android.whosup.MyInviteInformationActivity;
import com.whosup.android.whosup.R;
import com.whosup.android.whosup.utils.ConnectionDetector;
import com.whosup.android.whosup.utils.InviteAttend;
import com.whosup.android.whosup.utils.JSONParser;
import com.whosup.android.whosup.utils.SPreferences;
import com.whosup.android.whosup.utils.User;
import com.whosup.listview.Invite;
import com.whosup.listview.MyInviteAdapter;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyInvitesFragment extends Fragment {
    private ListView inviteListView;
    private ProgressDialog pDialog;
    private boolean refreshing = false;
    private final String TAG_USERNAME = "username";
    private final String GET_INVITES_URL = "http://whosup.host22.com/get_my_invites.php";
    ConnectionDetector cd;
    JSONParser jsonParser = new JSONParser();
    private static ArrayList<Invite> inviteList;
    JSONArray invitesList = null;



    public MyInvitesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_my_invites, null);
        /*FrameLayout fl = (FrameLayout) rootview.findViewById(R.id.my_invites_fragment);
        fl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        */
        inviteList = new ArrayList<>();
        inviteListView = (ListView) rootview.findViewById(R.id.list_invites);
        inviteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(rootview.getContext(), MyInviteInformationActivity.class);
                i.putExtra("invite", inviteList.get(position));
                startActivity(i);

            }
        });



        setHasOptionsMenu(true);

        return rootview;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(refreshing==false) {
            cd = new ConnectionDetector(getActivity());
            if (!cd.isConnectingToInternet()) {
                Toast.makeText(getActivity(), "Can't connect to the network.", Toast.LENGTH_SHORT).show();
            } else {
                new LoadMyInvites().execute();
            }
        }
        getActivity().setTitle(R.string.my_invites);
    }

    @Override
    public void onPause() {
        getActivity().setTitle(R.string.app_name);
        super.onPause();
    }

    /**
     * Background Async Task to Load all Albums by making http request
     * */

    class LoadMyInvites extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshing=true;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Listing My Invites ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Albums JSON
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(TAG_USERNAME, SPreferences.getInstance().getLoginUsername(getActivity())));

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(GET_INVITES_URL, "POST",
                    params);


            // Check your log cat for JSON reponse
            //Log.d("Invites JSON: ", "> " + json.toString());
            inviteList.clear();
            try {
                invitesList = json.getJSONArray("Invites");
                //Log.d("Invites JSONArray: ", "> " + invitesList);
                if (invitesList != null) {
                    // looping through All albums
                    for (int i = 0; i < invitesList.length(); i++) {
                        JSONObject c = invitesList.getJSONObject(i);
                        // Storing each json item values in variable
                        String id = c.getString("id");
                        String username = c.getString("username");
                        String firstName = c.getString("firstName");
                        String lastName = c.getString("lastName");
                        String gender = c.getString("gender");
                        String birthday = c.getString("birthday");
                        String postDay = c.getString("postDay");
                        String postHour = c.getString("postHour");
                        String meetDay = c.getString("meetDay");
                        String meetHour = c.getString("meetHour");
                        String category = c.getString("category");
                        String subcategory = c.getString("subcategory");
                        String description = c.getString("description");
                        String latitude = c.getString("latitude");
                        String longitude = c.getString("longitude");
                        String placeID = c.getString("placeID");
                        String address = c.getString("address");
                        String isOpen = c.getString("isOpen");

                        Invite newInvite = new Invite(id, username, firstName, lastName, gender, birthday, postDay, postHour, meetDay, meetHour, category, subcategory, description, latitude,
                                longitude, placeID, address, isOpen);
                        inviteList.add(newInvite);


                        JSONArray inviteAttendees = c.getJSONArray("InviteAttendees");
                        ArrayList<InviteAttend> inviteAttends = new ArrayList<>();
                        if (inviteAttendees != null) {
                            for (int j = 0; j < inviteAttendees.length(); j++) {
                                JSONObject ia = inviteAttendees.getJSONObject(j);
                                String idAttend = ia.getString("id");
                                String idInviteAttend = ia.getString("idInvite");
                                String hostUsernameAttend = ia.getString("hostUsername");
                                String invitedUsernameAttend = ia.getString("invitedUsername");
                                String stateAttend = ia.getString("state");

                                String usernameAttend = ia.getString("username");
                                String firstNameAttend = ia.getString("firstName");
                                String lastNameAttend = ia.getString("lastName");
                                String genderAttend = ia.getString("gender");
                                String birthdayAttend = ia.getString("birthday");
                                String cityAttend = ia.getString("city");
                                String countryAttend = ia.getString("country");
                                String photoLinkAttend = ia.getString("photoLink");
                                String aboutMeAttend = ia.getString("aboutMe");
                                String customPhraseAttend = ia.getString("customPhrase");
                                User userAttend = new User(false,usernameAttend, firstNameAttend, lastNameAttend, genderAttend,
                                        birthdayAttend, cityAttend, countryAttend, photoLinkAttend, aboutMeAttend, customPhraseAttend);

                                InviteAttend inviteAttend = new InviteAttend(idAttend, idInviteAttend, hostUsernameAttend, invitedUsernameAttend,
                                        stateAttend, userAttend);

                                inviteAttends.add(inviteAttend);
                                System.out.println("ADDED ATTEND");

                            }
                        }
                        newInvite.setInviteAttends(inviteAttends);
                    }
                    System.out.println("INVITE LIST: " + inviteList);
                } else {
                    Log.d("Invites: ", "null");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all albums
            refreshing=false;
            pDialog.dismiss();
            // updating UI from Background Thread
            /*runOnUiThread(new Runnable() {
                public void run() {*/
            /**
             * Updating parsed JSON data into ListView
             * */
            try{

                //System.out.println("INVITE LIST FIRST: " + inviteList.get(0).getDistanceFromMe());
                MyInviteAdapter adapter = new MyInviteAdapter(getActivity(), inviteList);
                inviteListView.setAdapter(adapter);
            }catch (Exception ex){
                Toast.makeText(getActivity(), "Can't load invites, try refreshing", Toast.LENGTH_SHORT).show();
            }





            // }
            // });

        }

        protected void onCancelled() {
            Toast.makeText(getActivity(), "Can't retrieve invites, try refreshing", Toast.LENGTH_SHORT).show();
        }
    }




}
