package com.example.firebasefeatures;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener ;
    DatabaseReference databaseReference ;

    private final int RC_SIGN_IN =123;
    String customerId ;

    EditText messageText;
    Button sendButton ;
    ListView chatListView ;

    ChatListAdapter mChatListAdapter ;          // creating the chatlistadapter object
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageText =(EditText ) findViewById(R.id.messagEditText) ;
        sendButton  =(Button) findViewById(R.id.sendButton ) ;
        chatListView = (ListView) findViewById(R.id.messageListView);

        firebaseAuth = FirebaseAuth.getInstance() ;


        messageText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage() ;
                return false;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user =  firebaseAuth.getCurrentUser();
                if(user!=null) {
                    //user in sign in
                    Toast.makeText(getApplicationContext(),"user is sign-IN ",Toast.LENGTH_SHORT).show();
                } else {
                    //user is sign out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                           new AuthUI.IdpConfig.GoogleBuilder().build()
                                           )
                                    ).build(),
                            RC_SIGN_IN);
                }
            }
        };

        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    public void sendMessage() {

        String current_message = messageText.getText().toString();

        if(!current_message.equals("")) {
            InstantMessage instantMessage= new InstantMessage(customerId,current_message);
            databaseReference.child("Messages").push().setValue(instantMessage);
            messageText.setText("");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(getApplicationContext() ,"sign in successful",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext() ,"sign in canceled" ,Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater() ;
        menuInflater.inflate(R.menu.menu_main ,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.signout :
                //signout
                AuthUI.getInstance().signOut(this) ;
                Toast.makeText(getApplicationContext() ,"user signed out",Toast.LENGTH_SHORT).show();
                return true ;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(mAuthStateListener) ;
    }

    //todo : creating the chatListAdapter object (Adapter for listview) and adding that adapter to the list View
    @Override
    protected void onStart() {
        super.onStart();
        customerId = firebaseAuth.getCurrentUser().getUid();
        mChatListAdapter = new ChatListAdapter(this ,databaseReference,customerId);
        chatListView.setAdapter(mChatListAdapter);        // added the adapter to the listview
    }

    // removing the listener from the adapter
    @Override
    protected void onStop() {
        super.onStop();
        mChatListAdapter.cleanUp();
    }
}
