package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.databinding.ItemRecyclerBuyBinding
import com.uos.project_new_windy.databinding.ItemRecyclerNormalBinding
import com.uos.project_new_windy.databinding.ItemRecyclerSellBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO

class ContentSellRecyclerViewAdapter (private val context: Context) : RecyclerView.Adapter<ContentSellRecyclerViewAdapter.ContentSellRecyclerViewAdapterViewHolder>() {

    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var contentSellDTO: ArrayList<ContentSellDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var uid : String ? = null
    var data = listOf<ContentSellDTO>()

    init {
        uid = FirebaseAuth.getInstance().currentUser?.uid

        firestore?.collection("contents")?.orderBy("timestamp")
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentSellDTO.clear()
                contentUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentSellDTO::class.java)
                    contentSellDTO.add(item!!)
                    contentUidList.add(snapshot.id)







                }

                notifyDataSetChanged()
            }
        data = contentSellDTO
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContentSellRecyclerViewAdapterViewHolder {
        val binding = ItemRecyclerSellBinding.inflate(LayoutInflater.from(context),parent,false)
        return ContentSellRecyclerViewAdapter.ContentSellRecyclerViewAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentSellRecyclerViewAdapterViewHolder, position: Int) {
        holder.onBind(contentSellDTO[position])


        //프로필 이미지
        firestore?.collection("profileImages")?.document(contentSellDTO[position].uid!!)
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
        Glide.with(holder.itemView.context).load(contentSellDTO!![position].imageDownLoadUrlList?.get(0))
            .into(holder.binding.itemRecyclerNormalImageviewImage)
    }

    override fun getItemCount(): Int = contentSellDTO.size



    class ContentSellRecyclerViewAdapterViewHolder(val binding: ItemRecyclerSellBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentSellDTO){
            binding.contentsell = data
        }
    }

}