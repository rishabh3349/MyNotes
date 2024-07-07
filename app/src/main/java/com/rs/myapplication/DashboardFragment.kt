package com.rs.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class DashboardFragment : Fragment() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var notesRepository: NotesRepository
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val logout: ImageView = view.findViewById(R.id.logout)
        logout.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                Toast.makeText(requireContext(), "Logging Out", Toast.LENGTH_SHORT).show()
                val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().remove("user_id").apply()
                (activity as HostActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .commit()
            }
        }
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", null)!!

        notesRepository = NotesRepository(requireContext())
        setupRecyclerView(view)

        val addNoteButton = view.findViewById<ImageView>(R.id.add_note_button)
        addNoteButton.setOnClickListener { showAddNoteDialog() }

        return view
    }

    private fun setupRecyclerView(view: View) {
        val notesRecyclerView = view.findViewById<RecyclerView>(R.id.notes_recycler_view)
        notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val notes = notesRepository.getNotes(userId)
        notesAdapter = NotesAdapter(notes, this::editNote, this::deleteNote)
        notesRecyclerView.adapter = notesAdapter
    }

    private fun showAddNoteDialog() {
        val dialog = AddEditNoteDialogFragment(null, this::addNote)
        dialog.show(parentFragmentManager, "AddEditNoteDialogFragment")
    }

    private fun addNote(title: String, content: String) {
        notesRepository.addNote(userId, title, content)
        refreshNotes()
    }

    private fun editNote(note: Note) {
        val dialog = AddEditNoteDialogFragment(note) { title, content ->
            updateNote(note, title, content)
        }
        dialog.show(parentFragmentManager, "AddEditNoteDialogFragment")
    }

    private fun updateNote(note: Note, title: String, content: String) {
        notesRepository.updateNote(note.id, title, content)
        refreshNotes()
    }

    private fun deleteNote(note: Note) {
        notesRepository.deleteNote(note.id)
        refreshNotes()
    }

    private fun refreshNotes() {
        val notes = notesRepository.getNotes(userId)
        notesAdapter = NotesAdapter(notes, this::editNote, this::deleteNote)
        view?.findViewById<RecyclerView>(R.id.notes_recycler_view)?.adapter = notesAdapter
    }
}
