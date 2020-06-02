package com.example.firebasefeatures;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

   private Activity mActivity ;
   private DatabaseReference mDatabaseReference ;
   private String mDisplayName ;
   private ArrayList<DataSnapshot> mSnapshotList ;

   private ChildEventListener mChildEventListener =new ChildEventListener() {
       @Override
       public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
           mSnapshotList.add(dataSnapshot) ; //pass the child reference to the datasnapshat as soon as changes is done in firebase database or we can say when a  msg is send
           notifyDataSetChanged();           // refereshing the list View as soon as new msg is added to listview
       }

       @Override
       public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

       }

       @Override
       public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

       }

       @Override
       public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

       }

       @Override
       public void onCancelled(@NonNull DatabaseError databaseError) {

       }
   };

    public ChatListAdapter(Activity activity, DatabaseReference ref, String name) {
        mActivity = activity;
        mDatabaseReference = ref.child("Messages");
        mDatabaseReference.addChildEventListener(mChildEventListener) ;   // adding the database Reference to the listenr so that it listens to the event
        mDisplayName = name;
        mSnapshotList = new ArrayList<>();
    }

    static class ViewHolder{
        TextView authorName ;
        TextView body ;
        LinearLayout.LayoutParams params ;
    }

    @Override
    public int getCount() {
        return mSnapshotList.size();
    }

    @Override
    public InstantMessage getItem(int position)
    {
        DataSnapshot snapshot = mSnapshotList.get(position) ;  // get the value return by the list
        return snapshot.getValue(InstantMessage.class);        // and returning in form of class ( as snapshot list conntains the value in form of json so we need to convert in object and return it
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // todo : returnig the view or list of items and also updating the list of items as new messages are added to firebase database
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // here convertView represents the listView if convertView is not null return the convert view
        // else create the the new row to contain the list view item
        if(convertView ==null) {
            LayoutInflater inflater  = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            convertView = inflater.inflate(R.layout.item_message ,parent,false);                            // added the inflater to listview

            final ViewHolder holder = new ViewHolder();                                         // creating the new row in list view and passing the valuew
            holder.authorName = (TextView) convertView.findViewById(R.id.nameTextView);
            holder.body       = (TextView) convertView.findViewById(R.id.messageTextView );
            holder.params     = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();

            convertView.setTag(holder);  // temporarily storing the holder to view so that their  is no need to call the findViewByID method again n again
        }

        final InstantMessage message = getItem(position);
        final ViewHolder holder = (ViewHolder ) convertView.getTag();

        boolean isMe = message.getAuthor().equals(mDisplayName);  // checking if the msg is send by user or by friend
        setChatRowAppearance(isMe,holder);

        String name = message.getAuthor();
        holder.authorName.setText(name);

        String messageText = message.getMessage();
        holder.body.setText(messageText);

        return convertView;
    }

    public void setChatRowAppearance(boolean isMe , ViewHolder holder) {
        if(isMe) {
           holder.params.gravity = Gravity.END ;
           holder.authorName.setTextColor(Color.GREEN);
           holder.body.setBackgroundResource(R.drawable.bubble2);
        } else {
            holder.params.gravity = Gravity.START ;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }

        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);
    }

    // todo: removing the childEventListener after the activity is stopped
    public void cleanUp(){
        mDatabaseReference.removeEventListener(mChildEventListener);
    }
}
