package com.disleksi.disleksi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {

    private FirebaseAuth mAuth ;
    private final String TAG = "ForgetPasswordFr";
    private EditText sifremiUnuttumEditText;
    private AppCompatButton sifremiUnuttumTamamButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        sifremiUnuttumEditText = view.findViewById(R.id.sifremiUnuttumEditText);
        sifremiUnuttumTamamButton = view.findViewById(R.id.sifremiUnuttumTamamButton);

        sifremiUnuttumTamamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!sifremiUnuttumEditText.getText().equals("")) {

                    String email = sifremiUnuttumEditText.getText().toString();

                    mAuth.sendPasswordResetEmail(email).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getActivity(),"E-posta gönderilemedi, tekrar deneyin!",
                                    Toast.LENGTH_LONG).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(),"E-posta başarıyla gönderildi",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                } else {

                    Toast.makeText(getActivity(),"Lütfen kayıtlı e-posta adresinizi girin!",
                            Toast.LENGTH_LONG).show();

                }


            }
        });



    }
}