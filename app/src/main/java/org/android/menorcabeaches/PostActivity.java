package org.android.menorcabeaches;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    Uri imatge;
    String url;
    StorageTask task;
    StorageReference sr;

    ImageView tancar, imatge_afagida;
    TextView publicacio;
    EditText descripcio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        tancar = findViewById(R.id.close);
        imatge_afagida = findViewById(R.id.image_added);
        publicacio = findViewById(R.id.post);
        descripcio = findViewById(R.id.description);

        sr = FirebaseStorage.getInstance().getReference("Publicacions");

        tancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        publicacio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pujarFoto();
            }
        });

        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void pujarFoto(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Penjant");
        progressDialog.show();

        if (imatge != null){
            final StorageReference referenciaArxiu = sr.child(System.currentTimeMillis()
                    + "." + getFileExtension(imatge));

            task = referenciaArxiu.putFile(imatge);
            task.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return referenciaArxiu.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri descarga = task.getResult();
                        url = descarga.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Publicacions");

                        String idpublicacio = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("idpublicacio", idpublicacio);
                        hashMap.put("imatge", url);
                        hashMap.put("descripcio", descripcio.getText().toString());
                        hashMap.put("usuari", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(idpublicacio).setValue(hashMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(PostActivity.this, "No s'ha pogut pujar",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(PostActivity.this, "No hi ha imatge",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imatge = result.getUri();

            imatge_afagida.setImageURI(imatge);
        } else {
            Toast.makeText(this, "Alguna cosa no ha anat bé", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }
}
