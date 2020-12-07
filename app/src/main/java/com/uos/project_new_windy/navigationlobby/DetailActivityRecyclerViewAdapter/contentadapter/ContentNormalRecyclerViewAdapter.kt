package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.databinding.ItemRecyclerNormalBinding
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO

class ContentNormalRecyclerViewAdapter (private val context: Context) : RecyclerView.Adapter<ContentNormalRecyclerViewAdapter.ContentNormalRecyclerViewAdapterViewHolder>(){

    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var contentNormalDTO: ArrayList<ContentNormalDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var contentCommentSize : ArrayList<Int> = arrayListOf()
    var uid : String ? = null
    var data = listOf<ContentNormalDTO>()

    init {
        Log.d("디테일!" , "교체완료됬습니다.")

        uid = FirebaseAuth.getInstance().currentUser?.uid

        firestore?.collection("contents")?.document("normal").collection("data").orderBy("timestamp",
            Query.Direction.DESCENDING)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentNormalDTO.clear()
                contentUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentNormalDTO::class.java)
                    contentNormalDTO.add(item!!)
                    contentUidList.add(snapshot.id)







                }

                notifyDataSetChanged()
            }
        data = contentNormalDTO
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentNormalRecyclerViewAdapterViewHolder {
        val binding = ItemRecyclerNormalBinding.inflate(LayoutInflater.from(context),parent,false)
        return ContentNormalRecyclerViewAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int = contentNormalDTO.size

    override fun onBindViewHolder(holder: ContentNormalRecyclerViewAdapterViewHolder, position: Int) {
        holder.onBind(contentNormalDTO[position])

        
        //프로필 이미지
        firestore?.collection("profileImages")?.document(contentNormalDTO[position].uid!!)
            ?.get()?.addOnCompleteListener { task ->

                if (task.isSuccessful)
                {
                    var url = task.result!!["image"]
                    Glide.with(holder.itemView.context)
                        .load(url)
                        .apply(RequestOptions().circleCrop()).into(holder.binding.itemRecyclerNormalImageviewProfile)
                }

            }

        //사진
        Glide.with(holder.itemView.context).load(contentNormalDTO!![position].imageDownLoadUrlList?.get(0))
            .into(holder.binding.itemRecyclerNormalImageviewImage)



    }



    class ContentNormalRecyclerViewAdapterViewHolder(val binding: ItemRecyclerNormalBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentNormalDTO){
            binding.contentnormal = data
        }
    }
}