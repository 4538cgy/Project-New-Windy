package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.addcontentadapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uos.project_new_windy.R
import com.uos.project_new_windy.navigationlobby.AddSellContentActivity
import com.uos.project_new_windy.navigationlobby.AddShopContentActivity
import kotlinx.android.synthetic.main.item_image_list.view.*

class AddShopContentActivityRecyclerViewAdapter (var activity: AddShopContentActivity, var imageUriList : ArrayList<Uri>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_list,parent,false)
        return CustomViewHolder(view)
    }

    inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var viewHolder = (holder as CustomViewHolder).itemView

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            Glide.with(holder.itemView.context)
                .load(imageUriList[position])
                .into(viewHolder.item_image_list_imageview)

        }

        viewHolder.item_image_list_const_all.setOnClickListener {
            imageUriList.removeAt(position)

            notifyDataSetChanged()
        }


    }

    override fun getItemCount(): Int {
        return imageUriList.size
    }
}