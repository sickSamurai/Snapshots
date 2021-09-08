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
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError

class HomeFragment : Fragment(), Reselectable {

  private lateinit var binding: FragmentHomeBinding
  private lateinit var firebaseAdapter: FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>

  inner class SnapshotHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemSnapshotBinding.bind(view)
    fun setListener(snapshot: Snapshot) {
      binding.btnDelete.setOnClickListener { deleteSnapshot(snapshot) }
      binding.checkboxLike.setOnCheckedChangeListener { _, isChecked ->
        setLike(snapshot, isChecked)
      }
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)
    binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val query = FirebaseUtilities.getDatabaseReference()
    val options = FirebaseRecyclerOptions.Builder<Snapshot>().setQuery(query) {
        data -> data.getValue(Snapshot::class.java).also { it!!.id = data.key!! }!!
    }.build()

    firebaseAdapter = object : FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>(options) {

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotHolder {
        val viewHolder = LayoutInflater.from(context).inflate(R.layout.item_snapshot, parent, false)
        return SnapshotHolder(viewHolder)
      }

      override fun onBindViewHolder(holder: SnapshotHolder, position: Int, model: Snapshot) {
        val snapshot = getItem(position)
        with(holder) {
          holder.setListener(snapshot)
          with(binding) {
            textTitle.text = snapshot.title
            checkboxLike.text = snapshot.likeList.keys.size.toString()
            FirebaseUtilities.getCurrentUser()?.let { checkboxLike.isChecked = snapshot.likeList.containsKey(it.uid) }
            Glide.with(context!!)
              .load(snapshot.photoURL)
              .error(R.drawable.ic_broken_image)
              .centerInside()
              .into(imgPhoto)
          }
        }
      }

      override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()
        binding.progressBar.visibility = View.GONE
      }

      override fun onError(error: DatabaseError) {
        super.onError(error)
        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
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

  fun deleteSnapshot(snapshot: Snapshot) {
    val databaseReference = FirebaseUtilities.getDatabaseReference()
    databaseReference.child(snapshot.id).removeValue().addOnSuccessListener {
      Toast.makeText(context, R.string.message_snapshot_deleted, Toast.LENGTH_SHORT).show()
    }
  }

  fun setLike(snapshot: Snapshot, isChecked: Boolean) {
    val databaseReference = FirebaseUtilities.getDatabaseReference()
    val valueToSave = if (isChecked) isChecked else null
    databaseReference.child(snapshot.id)
      .child("likeList")
      .child(FirebaseUtilities.getCurrentUserUID())
      .setValue(valueToSave)
  }

  override fun goToTop() {
    binding.snapshotList.smoothScrollToPosition(0)
  }

}

