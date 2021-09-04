package com.blackpanthers.snapshots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blackpanthers.snapshots.databinding.FragmentHomeBinding
import com.blackpanthers.snapshots.databinding.ItemSnapshotBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {

  private lateinit var binding: FragmentHomeBinding
  private lateinit var firebaseAdapter: FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>

  inner class SnapshotHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemSnapshotBinding.bind(view)
    fun setListener(snapshot: Snapshot) {
      TODO()
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)
    binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val query = FirebaseDatabase.getInstance().reference.child("snapshots")
    val options = FirebaseRecyclerOptions.Builder<Snapshot>().setQuery(query, Snapshot::class.java).build()
    firebaseAdapter = object : FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>(options) {

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_snapshot, parent, false)
        return SnapshotHolder(view)
      }

      override fun onBindViewHolder(holder: SnapshotHolder, position: Int, model: Snapshot) {
        val snapshot = getItem(position)
        with(holder) {
          binding.textTitle.text = snapshot.title
          Glide.with(context!!)
            .load(snapshot.photoURL)
            .error(R.drawable.ic_broken_image)
            .centerInside()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(binding.imgPhoto)
        }
      }

      override fun onDataChanged() {
        super.onDataChanged()
        binding.progressBar.visibility = View.GONE
      }

      override fun onError(error: DatabaseError) {
        super.onError(error)
        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
      }
    }

    binding.snapshotList.apply {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(context)
      adapter = firebaseAdapter
    }

  }

  override fun onStart() {
    super.onStart()
    firebaseAdapter.startListening()
  }

  override fun onStop() {
    super.onStop()
    firebaseAdapter.stopListening()
  }

}

