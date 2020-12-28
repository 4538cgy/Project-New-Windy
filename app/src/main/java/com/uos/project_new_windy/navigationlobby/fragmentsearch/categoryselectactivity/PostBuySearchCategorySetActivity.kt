package com.uos.project_new_windy.navigationlobby.fragmentsearch.categoryselectactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityPostBuySearchCategorySetBinding
import com.uos.project_new_windy.databinding.ItemBuySearchCategoryBinding
import com.uos.project_new_windy.databinding.ItemSellSearchCategoryBinding
import com.uos.project_new_windy.model.CategorySellPostDTO
import com.uos.project_new_windy.model.CatgoryBuyPostDTO

class PostBuySearchCategorySetActivity : AppCompatActivity() {

    lateinit var binding: ActivityPostBuySearchCategorySetBinding

    var categoryList : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_post_buy_search_category_set)
        binding.activitypostbuysearchcategoryset = this@PostBuySearchCategorySetActivity

        binding.activityPostBuySearchCategorySetRecycler.adapter = PostBuySearchCategoryRecyclerViewAdapter()
        binding.activityPostBuySearchCategorySetRecycler.layoutManager = GridLayoutManager(this,2)



        binding.activityPostBuySearchCategorySetButtonComplete.setOnClickListener {

            var intent = Intent()

            intent.putExtra("categoryList",categoryList)
            setResult(1556,intent)
            finish()
        }

        binding.activityPostBuySearchCategorySetImagebuttonBack.setOnClickListener {
            System.out.println("액티비티 종료")

            categoryList.forEach {
                System.out.println(" 리스트 = $it")
            }

            var intent = Intent()
            intent.putExtra("categoryList" , categoryList)

            setResult(1555, Intent())
            finish()
        }
    }

    inner class PostBuySearchCategoryRecyclerViewAdapter : RecyclerView.Adapter<PostBuySearchCategoryRecyclerViewAdapter.PostBuySearchCategoryRecyclerViewAdapterViewHolder>(){

        var categoryBuyPostDTO : ArrayList<CatgoryBuyPostDTO> = arrayListOf()

        var data  = listOf<CatgoryBuyPostDTO>()
        init {


            categoryBuyPostDTO.add(CatgoryBuyPostDTO("농기계",false))
            categoryBuyPostDTO.add(CatgoryBuyPostDTO("농산물",false))
            categoryBuyPostDTO.add(CatgoryBuyPostDTO("축산물",false))
            categoryBuyPostDTO.add(CatgoryBuyPostDTO("소모품",false))
            categoryBuyPostDTO.add(CatgoryBuyPostDTO("농지",false))
            categoryBuyPostDTO.add(CatgoryBuyPostDTO("가구",false))
            categoryBuyPostDTO.add(CatgoryBuyPostDTO("기타",false))


            data = categoryBuyPostDTO

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostBuySearchCategoryRecyclerViewAdapterViewHolder {

            val binding = ItemBuySearchCategoryBinding.inflate(LayoutInflater.from(this@PostBuySearchCategorySetActivity),parent,false)
            return PostBuySearchCategoryRecyclerViewAdapterViewHolder(binding)
        }


        override fun getItemCount(): Int {
            return categoryBuyPostDTO.size
        }

        override fun onBindViewHolder(holder: PostBuySearchCategoryRecyclerViewAdapterViewHolder, position: Int) {
            holder.onBind(categoryBuyPostDTO[position])


            //체크 박스를 클릭했을때
            holder.binding.itemBuySearchCategoryCheckbox.setOnClickListener {




                //해당 포지션이 체크되지 않았을 경우
                if (categoryBuyPostDTO[position].check == false){
                    //데이터 추가
                    categoryList.add(categoryBuyPostDTO[position].title.toString())
                    categoryBuyPostDTO[position].check = true

                    //해당 포지션이 체크되었을 경우
                }else{
                    //리스트를 조회해서 같은 이름을 가진 인덱스 삭제
                    categoryList.remove(categoryBuyPostDTO[position].title.toString())

                    categoryBuyPostDTO[position].check = false
                }


                categoryList.forEach {
                    System.out.println("리스트 = $it")
                }

            }
        }

        inner class PostBuySearchCategoryRecyclerViewAdapterViewHolder(val binding: ItemBuySearchCategoryBinding) : RecyclerView.ViewHolder(binding.root){
            fun onBind(data: CatgoryBuyPostDTO){
                binding.itembuysearchcategory = data
            }
        }


    }
}