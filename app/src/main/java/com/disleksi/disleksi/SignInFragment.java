package com.disleksi.disleksi;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SignInFragment extends Fragment {

    private final String TAG = "SignInFr";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration reference;
    private ArrayList<String> emailList;
    private AppCompatButton girisYapButton;
    private TextView kayitOlText,sifremiUnuttumText;
    private ProgressBar progressBarSignIn;
    private EditText editTextEmail, editTextSifre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        girisYapButton = view.findViewById(R.id.girisYapButton);
        kayitOlText = view.findViewById(R.id.kayitOlText);
        sifremiUnuttumText = view.findViewById(R.id.sifremiUnuttumText);
        progressBarSignIn = view.findViewById(R.id.progressBarSignIn);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextSifre = view.findViewById(R.id.editTextSifre);
        emailList  = new ArrayList<>();

        if (mAuth.getCurrentUser() != null) {

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("definite", 2);
            startActivity(intent);
            getActivity().finish();

        }

        girisYapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();

            }
        });

        kayitOlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavDirections action =
                        SignInFragmentDirections
                                .actionSignInFragmentToSignUpFragment();
                Navigation.findNavController(view).navigate(action);

            }
        });

        sifremiUnuttumText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavDirections action =
                        SignInFragmentDirections
                                .actionSignInFragmentToForgotPasswordFragment();
                Navigation.findNavController(view).navigate(action);

            }
        });

    }

    private void signIn() {


        progressBarSignIn.setVisibility(View.VISIBLE);
        progressBarSignIn.setTranslationZ(2F);
        progressBarSignIn.setElevation(10F);


        String email = editTextEmail.getText().toString();
        String password = editTextSifre.getText().toString();

        if (email.equals("") && password.equals("")) {

            Toast.makeText(getActivity(), "Lütfen gerekli alanları doldurunuz!", Toast.LENGTH_LONG).show();
            progressBarSignIn.setVisibility(View.GONE);

        } else if (password.equals("")) {

            Toast.makeText(getActivity(), "Lütfen şifrenizi giriniz!", Toast.LENGTH_LONG).show();
            progressBarSignIn.setVisibility(View.GONE);

        } else if (email.equals("")) {

            Toast.makeText(getActivity(), "Lütfen kayıtlı e-posta adresinizi giriniz!", Toast.LENGTH_LONG).show();
            progressBarSignIn.setVisibility(View.GONE);

        } else {

            Query query = db.collection("User");

            reference = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (error != null) {

                        Toast.makeText(requireContext(), "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }else {

                        if (value != null) {

                            if (!value.isEmpty()) {

                                Log.i(TAG, "user snapshotListener");

                                List<DocumentSnapshot> documents = value.getDocuments();

                                for (DocumentSnapshot document : documents) {

                                    String testEmail = (String) document.get("email") ;
                                    emailList.add(testEmail);

                                    if (testEmail.equals(email)) {

                                        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (task.isSuccessful()){

                                                    reference.remove();

                                                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                                                    intent.putExtra("definite", 2);
                                                    startActivity(intent);
                                                    requireActivity().finish();
                                                    progressBarSignIn.setVisibility(View.GONE);

                                                } else {

                                                    try {
                                                        throw task.getException();
                                                    } catch (FirebaseAuthUserCollisionException error) {

                                                        reference.remove();

                                                        Toast.makeText(
                                                                getActivity(),
                                                                error.getLocalizedMessage(),
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                        progressBarSignIn.setVisibility(View.GONE);


                                                    } catch (FirebaseAuthEmailException error) {

                                                        reference.remove();

                                                        Toast.makeText(
                                                                getActivity(),
                                                                error.getLocalizedMessage(),
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                        progressBarSignIn.setVisibility(View.GONE);


                                                    } catch (FirebaseAuthInvalidUserException error) {

                                                        reference.remove();

                                                        Toast.makeText(
                                                                getActivity(),
                                                                "Bu e-posta ile eşleşen bir kullanıcı yok. Lütfen tekrar deneyin!",
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                        progressBarSignIn.setVisibility(View.GONE);


                                                    } catch (FirebaseNetworkException error) {

                                                        reference.remove();

                                                        Toast.makeText(
                                                                getActivity(),
                                                                "Lütfen internet bağlantınızı kontrol ediniz",
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                        progressBarSignIn.setVisibility(View.GONE);


                                                    } catch (FirebaseAuthInvalidCredentialsException error) {

                                                        reference.remove();

                                                        Toast.makeText(
                                                                getActivity(),
                                                                error.getLocalizedMessage(),
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                        progressBarSignIn.setVisibility(View.GONE);


                                                    } catch (Exception error) {
                                                        reference.remove();

                                                        Toast.makeText(
                                                                getActivity(),
                                                                error.getLocalizedMessage(),
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                        progressBarSignIn.setVisibility(View.GONE);


                                                    }

                                                }


                                            }
                                        });

                                        break;
                                    } else
                                        Log.i(TAG, "böyle bir email bulunamadı");


                                }

                            } else {

                                reference.remove();

                                Log.i(TAG, "email bulunamadı");

                                progressBarSignIn.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "Bu e-posta ile eşleşen bir kullanıcı yok, lütfen tekrar deneyin!", Toast.LENGTH_SHORT).show();


                            }


                        } else {

                            reference.remove();

                            Log.i(TAG, "data null");

                            progressBarSignIn.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Böyle bir kullanıcı yok. Lütfen tekrar deneyin!", Toast.LENGTH_SHORT).show();

                        }


                    }

                }
            });

        }
    }
}