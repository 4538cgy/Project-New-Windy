package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.addcontentadapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uos.project_new_windy.R
import com.uos.project_new_windy.navigationlobby.AddContentActivity
import com.uos.project_new_windy.navigationlobby.AddSellContentActivity
import kotlinx.android.synthetic.main.item_image_list.view.*

class AddSellContentActivityRecyclerViewAdapter (var activity: AddSellContentActivity, var imageUriList : ArrayList<Uri>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_list,parent,false)
        return CustomViewHolder(view)
    }

    inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var viewHolder = (holder as CustomViewHolder).itemView

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            viewHolder.item_image_list_imageview.setImageURI(imageUriList[position])
        }


    }

    override fun getItemCount(): Int {
        return imageUriList.size
    }
}