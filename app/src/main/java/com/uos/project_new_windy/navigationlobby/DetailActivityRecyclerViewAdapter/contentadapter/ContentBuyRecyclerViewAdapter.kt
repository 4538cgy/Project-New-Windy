package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogContentOption
import com.uos.project_new_windy.databinding.ItemRecyclerBuyBinding
import com.uos.project_new_windy.databinding.ItemRecyclerNormalBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO

class ContentBuyRecyclerViewAdapter(private val context: Context,var fragmentManager: FragmentManager) : RecyclerView.Adapter<ContentBuyRecyclerViewAdapter.ContentBuyRecyclerViewAdapterViewHolder>() {

    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var contentBuyDTO: ArrayList<ContentBuyDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var uid : String ? = null
    var data = listOf<ContentBuyDTO>()

    init {
        Log.d("디테일!" , "교체완료됬습니다.")

        uid = FirebaseAuth.getInstance().currentUser?.uid

        firestore?.collection("contents")?.document("buy").collection("data").orderBy("timeStamp", Query.Direction.DESCENDING)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentBuyDTO.clear()
                contentUidList.clear()
                
                
                System.out.println("구매하기 데이터 가져오기 성공")
                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {

                    var item = snapshot.toObject(ContentBuyDTO::class.java)

                    System.out.println("데이터들 " + item.toString())
                    contentBuyDTO.add(item!!)
                    System.out.println("데이터들2" + contentBuyDTO.toString())
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

        Glide.with(holder.itemView.context).load(contentBuyDTO!![position].imageUrl)
            .into(holder.binding.itemRecyclerNormalImageviewImage)

        //옵션 버튼 클릭
        holder.binding.itemRecyclerBuyImagebuttonOption.setOnClickListener {
            val bottomeSheetDialog : BottomSheetDialogContentOption = BottomSheetDialogContentOption()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentBuyDTO[position].uid)
            bundle.putString("userId",contentBuyDTO[position].userId)
            bundle.putString("postUid",contentUidList[position])
            bundle.putString("uid" , contentBuyDTO[position].uid)
            bundle.putString("postType", "buy")
            bottomeSheetDialog.arguments = bundle
            bottomeSheetDialog.show(fragmentManager,"dd")
        }


    }

    override fun getItemCount(): Int = data.size

    class ContentBuyRecyclerViewAdapterViewHolder(val binding: ItemRecyclerBuyBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentBuyDTO){
            binding.contentbuy = data
        }
    }


}