package com.darragh.musicalnotepad.Pagers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.darragh.musicalnotepad.Modules.WebViewController;
import com.darragh.musicalnotepad.Pitch_Detector.Song;
import com.darragh.musicalnotepad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongRequestListAdapter extends ArrayAdapter{
    private final Context context;
    private final ArrayList<Song> songList;
    private TextView songName,key,time;
    private ImageView profilePicture;
    private Button previewSong,acceptSong,declineSong;
    private View mainView;

    public SongRequestListAdapter(Context _context, ArrayList<Song> _songList, View _mainView) {
        super(_context, R.layout.potentialsongrequestrow, _songList);
        this.context = _context;
        this.songList = _songList;
        this.mainView = _mainView;
    }

    private void addSongToSongList(final String timeStamp){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Map<String,Object> map = new HashMap<>();
                //Move song from Song Request to SongList
                Song sentSong = JSONToSongConverter.songFromJSON(dataSnapshot.child(getContext().getResources().getString(R.string.users)
                        +FirebaseAuth.getInstance().getCurrentUser().getUid()+"/SongRequest/"+timeStamp+"/").getChildren());

                map.put(getContext().getResources().getString(R.string.users)+ FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()+"/songId/"+timeStamp+"/",sentSong);
                databaseReference.updateChildren(map);
                RemoveSongRequest(timeStamp, databaseReference, sentSong.getUID());
//                getContext().startActivity(new Intent(getContext().getApplicationContext(), FriendRequest.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError){}
        });
    }

    private void RemoveSongRequest(String timeStamp,DatabaseReference databaseReference, String UID){
        databaseReference.child(getContext().getResources().getString(R.string.users)+UID+"/PendingSong/"+timeStamp)
                .setValue(null);
        databaseReference.child(getContext().getResources().getString(R.string.users)+FirebaseAuth.getInstance()
                .getCurrentUser().getUid()+"/SongRequest/"+timeStamp).setValue(null);
    }

    private void previewSong(View rowView, final String timeStamp){
        Intent intent = new Intent(getContext(),songDisplay.class);
        intent.putExtra("Timestamp",timeStamp);
        intent.putExtra("Directory","/SongRequest/");
        mainView.getContext().startActivity(intent);
    }

    private void setButtons(final View rowView, final int position){
        previewSong = (Button) rowView.findViewById(R.id.preview);
        previewSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewSong(rowView, songList.get(position).getTimestamp());
            }
        });
        acceptSong = (Button) rowView.findViewById(R.id.accept);
        acceptSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSongToSongList(songList.get(position).getTimestamp());
            }
        });
        declineSong = (Button) rowView.findViewById(R.id.decline);
        declineSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveSongRequest(songList.get(position).getTimestamp()
                        ,FirebaseDatabase.getInstance().getReference()
                        ,songList.get(position).getUID());
            }
        });

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.potentialsongrequestrow, parent, false);
        songName = (TextView) rowView.findViewById(R.id.songName);
        key = (TextView) rowView.findViewById(R.id.key);
        time = (TextView) rowView.findViewById(R.id.time);
        profilePicture = (ImageView) rowView.findViewById(R.id.userProfilePicture);

        setButtons(rowView,position);
        setValues(position);

        return rowView;
    }

    private void setValues(int position){
        songName.setText("Song name: " + songList.get(position).getName());
        key.setText("Key: " + songList.get(position).getKeySignature());
        time.setText("Time: " + songList.get(position).getTimeSignature());
        previewSong.setText(getContext().getResources().getString(R.string.previewSong));
//        acceptSong.setText(getContext().getResources().getString(R.string.Accept));
//        declineSong.setText(getContext().getResources().getString(R.string.Decline));
        if(!songList.get(position).getProfilePhoto().equals(null)){
            Picasso.with(getContext()).load(songList.get(position).getProfilePhoto()+"?sz=350").into(profilePicture);
        }
    }
}