import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText messageEditText;
    private Button sendMessageButton;
    private RecyclerView chatMessagesRecyclerView;
    private List<ChatMessage> messages;
    private ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("chats");

        // Initialize UI elements
        messageEditText = findViewById(R.id.chat_message_editText);
        sendMessageButton = findViewById(R.id.send_message_button);
        chatMessagesRecyclerView = findViewById(R.id.chat_messages_recycler_view);

        // Initialize chat messages list and adapter
        messages = new ArrayList<>();
        adapter = new ChatMessageAdapter(messages);
        chatMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatMessagesRecyclerView.setAdapter(adapter);

        // Send message button click listener
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageEditText.setText(""); // Clear message EditText after sending
                }
            }
        });

        // Listen for new messages in the database
        mDatabase.addChildEventListener(new ChildEventListener() {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
        messages.add(chatMessage);
        adapter.notifyDataSetChanged();
        chatMessagesRecyclerView.smoothScrollToPosition(messages.size() - 1);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        // Implement logic to update existing messages if needed
        ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
        int index = messages.indexOf(chatMessage);
        if (index != -1) {
            messages.set(index, chatMessage);
            adapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
        int index = messages.indexOf(chatMessage);
        if (index != -1) {
            messages.remove(index);
            adapter.notifyItemRemoved(index);
        }
    }

    @Override
public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
    int oldIndex = messages.indexOf(chatMessage);
    int newIndex = messages.indexOf(dataSnapshot.getKey());
    if (oldIndex != -1 && newIndex != -1) {
        messages.remove(oldIndex);
        messages.add(newIndex, chatMessage);
        adapter.notifyItemMoved(oldIndex, newIndex);
    }
}

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e("Firebase", "Error: " + databaseError.getMessage());
    }
});
    }

    private void sendMessage(String message, Uri fileUri) {
    String userId = mAuth.getCurrentUser().getUid();
    String timestamp = String.valueOf(System.currentTimeMillis());

    HashMap<String, Object> messageData = new HashMap<>();
    messageData.put("message", message);
    messageData.put("senderId", userId);
    messageData.put("timestamp", timestamp);

    if (fileUri != null) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageRef.child("chat_files/" + UUID.randomUUID().toString());
        UploadTask uploadTask = fileRef.putFile(fileUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {
                messageData.put("fileUrl", uri.toString());
                mDatabase.push().setValue(messageData);
            });
        }).addOnFailureListener(exception -> {
            Log.e("Firebase", "Error uploading file: " + exception.getMessage());
        });
    } else {
        mDatabase.push().setValue(messageData);
    }
}
}
