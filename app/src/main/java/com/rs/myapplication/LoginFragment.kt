package com.rs.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var signInButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        FirebaseApp.initializeApp(requireContext())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        firebaseAuth = FirebaseAuth.getInstance()
        signInButton = view.findViewById(R.id.Signin)
        progressBar = view.findViewById(R.id.progressBar)

        val imgDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.img)
        imgDrawable?.setBounds(0, 0, 60, 60)
        signInButton.setCompoundDrawables(imgDrawable, null, null, null)

        signInButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            signInButton.visibility = View.GONE
            signInGoogle()
        }

        return view
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            } else {
                progressBar.visibility = View.GONE
                signInButton.visibility = View.VISIBLE
            }
        } catch (e: ApiException) {
            Toast.makeText(requireContext(),"Error logging in , check your internet connection.", Toast.LENGTH_LONG).show()
            Log.e("error", e.toString())
            progressBar.visibility = View.GONE
            signInButton.visibility = View.VISIBLE
        }
    }

    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            progressBar.visibility = View.GONE
            signInButton.visibility = View.VISIBLE
            if (task.isSuccessful) {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("user_id", userId)
                    editor.apply()

                    (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, DashboardFragment())
                        .commit()
                }
            } else {
                Toast.makeText(requireContext(), "Error logging in , check your internet connection.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(requireContext()) != null) {
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DashboardFragment())
                .commit()
        }
    }
}
