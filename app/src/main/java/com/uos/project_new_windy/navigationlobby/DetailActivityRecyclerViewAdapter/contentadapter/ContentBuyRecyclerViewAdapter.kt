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
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO

class ContentBuyRecyclerViewAdapter(private val context: Context) : RecyclerView.Adapter<ContentBuyRecyclerViewAdapter.ContentBuyRecyclerViewAdapterViewHolder>() {

    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var contentBuyDTO: ArrayList<ContentBuyDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var uid : String ? = null
    var data = listOf<ContentBuyDTO>()

    init {
        uid = FirebaseAuth.getInstance().currentUser?.uid

        firestore?.collection("contents")?.orderBy("timestamp")
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentBuyDTO.clear()
                contentUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentBuyDTO::class.java)
                    contentBuyDTO.add(item!!)
                    contentUidList.add(snapshot.id)







                }

                notifyDataSetChanged()
            }
        data = contentBuyDTO
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContentBuyRecyclerViewAdapterViewHolder {
        val binding = ItemRecyclerBuyBinding.inflate(LayoutInflater.from(context),parent,false)
        return ContentBuyRecyclerViewAdapter.ContentBuyRecyclerViewAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentBuyRecyclerViewAdapterViewHolder, position: Int) {
        holder.onBind(contentBuyDTO[position])


        //프로필 이미지
        firestore?.collection("profileImages")?.document(contentBuyDTO[position].uid!!)
            ?.get()?.addOnCompleteListener { task ->

                if (task.isSuccessful)
                {
                    var url = task.result!!["image"]
                    Glide.with(holder.itemView.context)
                        .load(url)
                        .apply(RequestOptions().circleCrop()).into(holder.binding.itemRecyclerBuyImageviewProfile)
                }

            }

        //사진
        /*
        Glide.with(holder.itemView.context).load(contentBuyDTO!![position].imageDownLoadUrlList?.get(0))
            .into(holder.binding.itemRecyclerNormalImageviewImage)

         */
    }

    override fun getItemCount(): Int = contentBuyDTO.size

    class ContentBuyRecyclerViewAdapterViewHolder(val binding: ItemRecyclerBuyBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentBuyDTO){
            binding.contentbuy = data
        }
    }


}