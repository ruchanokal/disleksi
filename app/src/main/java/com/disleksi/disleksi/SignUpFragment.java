package com.disleksi.disleksi;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Any;

import java.util.HashMap;


public class SignUpFragment extends Fragment {


    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListenerRegistration reference;
    private final String TAG = "UserSignUpFr";
    String userUid = "";
    private String email = "";
    private String kullaniciAdi = "";
    private String sifre = "";
    private String sifre2 = "";
    private EditText editTextKullaniciAdiKayit,editTextEmailKayit,editTextSifreKayit,editTextSifreKayit2;
    private AppCompatButton kayitOlButton;
    private ProgressBar progressBarSignUp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmailKayit = view.findViewById(R.id.editTextEmailKayit);
        editTextKullaniciAdiKayit = view.findViewById(R.id.editTextKullaniciAdiKayit);
        editTextSifreKayit = view.findViewById(R.id.editTextSifreKayit);
        editTextSifreKayit2 = view.findViewById(R.id.editTextSifreKayit2);
        kayitOlButton = view.findViewById(R.id.kayitOlButton);
        progressBarSignUp = view.findViewById(R.id.progressBarSignUp);


        kayitOlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBarSignUp.setVisibility(View.VISIBLE);

                email = editTextEmailKayit.getText().toString();
                kullaniciAdi = editTextKullaniciAdiKayit.getText().toString();
                sifre = editTextSifreKayit.getText().toString();
                sifre2 = editTextSifreKayit2.getText().toString();

                databaseCollection();
            }
        });





    }

    private void databaseCollection() {

        reference = db.collection("User").whereEqualTo("kullaniciAdi",kullaniciAdi).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                Log.i(TAG, "kullaniciAdi: " + kullaniciAdi);

                if (error != null) {
                    Log.i(TAG, "error: " + error);
                } else {

                    if (value != null) {

                        Log.i(TAG, "null değil");

                        if (!value.isEmpty()) {
                            Log.i(TAG, "empty değil");

                            Toast.makeText(
                                    getActivity(), "Lütfen başka bir kullanıcı adı deneyin!",
                                    Toast.LENGTH_LONG
                            ).show();

                            progressBarSignUp.setVisibility(View.GONE);

                            reference.remove();

                        } else {
                            Log.i(TAG, "empty");
                            kontroller();
                        }

                    } else {
                        Log.i(TAG, "null");
                        kontroller();
                    }

                }
            }
        });

    }

    private void kontroller() {

        if (email.equals("")
                || kullaniciAdi.equals("")
                || sifre.equals("")
        ) {

            reference.remove();

            Toast.makeText(getActivity(), "Lütfen gerekli alanları doldurunuz", Toast.LENGTH_LONG).show();

            progressBarSignUp.setVisibility(View.GONE);

        } else if (!sifre.equals(sifre2)) {

            reference.remove();

            Toast.makeText(getActivity(), "Şifreler aynı olmalıdır!", Toast.LENGTH_LONG).show();

            progressBarSignUp.setVisibility(View.GONE);

        } else {


            mAuth.createUserWithEmailAndPassword(email,sifre)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        reference.remove();

                        userUid = mAuth.getCurrentUser().getUid();
                        progressBarSignUp.setVisibility(View.GONE);


                        HashMap<String, String> hashMap = new HashMap();

                        String email = mAuth.getCurrentUser().getEmail();
                        hashMap.put("email",email);
                        hashMap.put("kullaniciAdi",kullaniciAdi);

                        db.collection("User").document(userUid).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Log.i(TAG, "user bir kullanıcı eklendi");

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.putExtra("kullaniciAdi", kullaniciAdi);
                                intent.putExtra("definite", 1);
                                startActivity(intent);
                                requireActivity().finish();

                                Toast.makeText(getActivity(), "Hoşgeldin " + kullaniciAdi, Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.e(TAG, "kullanıcı oluşturulamadı yeniden deneyin!");


                            }
                        });

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    try {
                        throw e;
                    } catch (FirebaseAuthUserCollisionException error) {

                        reference.remove();

                        Toast.makeText(
                                getActivity(),
                                "Bu e-posta adresi zaten başka bir hesap tarafından kullanılıyor",
                                Toast.LENGTH_LONG
                        ).show();
                        progressBarSignUp.setVisibility(View.GONE);

                    } catch (FirebaseAuthWeakPasswordException error) {

                        reference.remove();

                        Toast.makeText(
                                getActivity(),
                                "Lütfen en az 6 haneli bir şifre giriniz",
                                Toast.LENGTH_LONG
                        ).show();
                        progressBarSignUp.setVisibility(View.GONE);

                    } catch (FirebaseNetworkException error) {

                        reference.remove();

                        Toast.makeText(
                                getActivity(),
                                "Lütfen internet bağlantınızı kontrol edin",
                                Toast.LENGTH_LONG
                        ).show();
                        progressBarSignUp.setVisibility(View.GONE);

                    } catch (Exception error) {

                        reference.remove();

                        Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        progressBarSignUp.setVisibility(View.GONE);

                    }

                }
            });


        }
    }
}