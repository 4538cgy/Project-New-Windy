package com.uos.project_new_windy.newwindymall

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityAddReviewBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel
import com.uos.project_new_windy.navigationlobby.AddSellContentActivity
import kotlinx.android.synthetic.main.item_image_list.view.*

class AddReviewActivity : AppCompatActivity() {

    var PICK_IMAGE_FROM_ALBUM = 0
    lateinit var binding : ActivityAddReviewBinding
    private var productId : String ? = null
    private var ratingPoint : Long ? = 0
    var imageUriList: ArrayList<Uri> = arrayListOf()
    private var comment : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_review)
        binding.activityaddreview = this
        productId = intent.getStringExtra("product")


        binding.activityAddReviewImagebuttonClose.setOnClickListener { finish() }

        binding.activityAddReviewUpload.setOnClickListener { uploadReview() }

        binding.activityAddReviewImagebuttonAddPhoto.setOnClickListener {

            addPhoto()
        }

        binding.activityAddReviewRatingbar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            ratingPoint = rating.toLong()
        }

        initPhotoRecyclerViewAdapter()
    }

    fun initPhotoRecyclerViewAdapter(){
        binding.activityAddReviewRecycler.adapter = ProductPhotoRecyclerAdapter(this, imageUriList)
        binding.activityAddReviewRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
    }

    fun addPhoto() {
        Toast.makeText(this, "사진을 꾹 누르시면 여러장을 올릴 수 있어요.", Toast.LENGTH_LONG).show()
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, PICK_IMAGE_FROM_ALBUM)
        }


    }

    fun uploadReview(){
        var review = MallMainModel.Product.Review()
        review.comment = binding.activityAddReviewEdittextReviewComment.text.toString()
        review.uid = FirebaseAuth.getInstance().currentUser!!.uid
        review.rating = ratingPoint
        review.timestamp = System.currentTimeMillis()
        imageUriList.forEach {
            review.imageUrlList!!.add(it.toString())
        }

        val tsDocReview = FirebaseFirestore.getInstance().collection("Mall").document("product").collection("product").document(productId.toString())

        FirebaseFirestore.getInstance().runTransaction {
            transaction ->

            var product = transaction.get(tsDocReview).toObject(MallMainModel.Product::class.java)
            product!!.review.put(FirebaseAuth.getInstance().currentUser!!.uid,review)

            transaction.set(tsDocReview,product)
            return@runTransaction
        }.addOnSuccessListener { println("성공")
            finish()}
            .addOnFailureListener { println("실패 ${it.toString()}") }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {

            if (data!!.clipData != null) {
                val count = data.clipData!!.itemCount
                var currentItem = 0
                while (currentItem < count) {
                    val imageUri =
                        data.clipData!!.getItemAt(currentItem).uri
                    imageUriList.add(imageUri)
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                    currentItem = currentItem + 1
                }
            } else {
                val fullPhotoUri: Uri = data!!.data!!
                imageUriList.add(fullPhotoUri)
            }


        } else {
            finish()
        }
        binding.activityAddReviewRecycler.adapter?.notifyDataSetChanged()
    }

}