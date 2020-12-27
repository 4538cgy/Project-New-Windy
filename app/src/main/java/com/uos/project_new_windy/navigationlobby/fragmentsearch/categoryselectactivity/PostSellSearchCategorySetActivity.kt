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
import com.uos.project_new_windy.databinding.ActivityPostSellSearchCategorySetBinding
import com.uos.project_new_windy.databinding.ItemSellSearchCategoryBinding
import com.uos.project_new_windy.model.CategorySellPostDTO

class PostSellSearchCategorySetActivity : AppCompatActivity() {

    lateinit var binding : ActivityPostSellSearchCategorySetBinding

    var categoryList : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_post_sell_search_category_set)
        binding.activitypostsellsearchcategoryset = this@PostSellSearchCategorySetActivity

        binding.activityPostSellSearchCategorySetRecycler.adapter = PostSellSearchCategoryRecyclerViewAdapter()
        binding.activityPostSellSearchCategorySetRecycler.layoutManager = GridLayoutManager(this,2)



        binding.activityPostSellSearchCategorySetButtonComplete.setOnClickListener {

            var intent = Intent()

            intent.putExtra("categoryList",categoryList)
            setResult(1556,intent)
            finish()
        }

        binding.activityPostSellSearchCategorySetImagebuttonBack.setOnClickListener {
            System.out.println("액티비티 종료")

            categoryList.forEach {
                System.out.println(" 리스트 = $it")
            }

            var intent = Intent()
            intent.putExtra("categoryList" , categoryList)

            setResult(1555,Intent())
            finish()
        }
    }


    inner class PostSellSearchCategoryRecyclerViewAdapter : RecyclerView.Adapter<PostSellSearchCategoryRecyclerViewAdapter.PostSellSearchCategoryRecyclerViewAdapterViewHolder>(){

        var categorySellPostDTO : ArrayList<CategorySellPostDTO> = arrayListOf()

        var data  = listOf<CategorySellPostDTO>()
        init {


            categorySellPostDTO.add(CategorySellPostDTO("농기계",false))
            categorySellPostDTO.add(CategorySellPostDTO("농산물",false))
            categorySellPostDTO.add(CategorySellPostDTO("축산물",false))
            categorySellPostDTO.add(CategorySellPostDTO("소모품",false))
            categorySellPostDTO.add(CategorySellPostDTO("농지",false))
            categorySellPostDTO.add(CategorySellPostDTO("가구",false))
            categorySellPostDTO.add(CategorySellPostDTO("기타",false))


            data = categorySellPostDTO

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostSellSearchCategoryRecyclerViewAdapterViewHolder {

            val binding = ItemSellSearchCategoryBinding.inflate(LayoutInflater.from(this@PostSellSearchCategorySetActivity),parent,false)
            return PostSellSearchCategoryRecyclerViewAdapterViewHolder(binding)
        }


        override fun getItemCount(): Int {
            return categorySellPostDTO.size
        }

        override fun onBindViewHolder(holder: PostSellSearchCategoryRecyclerViewAdapterViewHolder, position: Int) {
            holder.onBind(categorySellPostDTO[position])

            
            //체크 박스를 클릭했을때
            holder.binding.itemSellSearchCategoryCheckbox.setOnClickListener {
                



                //해당 포지션이 체크되지 않았을 경우
                if (categorySellPostDTO[position].check == false){
                    //데이터 추가
                    categoryList.add(categorySellPostDTO[position].title.toString())
                    categorySellPostDTO[position].check = true
                    
                    //해당 포지션이 체크되었을 경우
                }else{
                    //리스트를 조회해서 같은 이름을 가진 인덱스 삭제
                    categoryList.remove(categorySellPostDTO[position].title.toString())

                    categorySellPostDTO[position].check = false
                }


                categoryList.forEach {
                    System.out.println("리스트 = $it")
                }
               
            }
        }

        inner class PostSellSearchCategoryRecyclerViewAdapterViewHolder(val binding: ItemSellSearchCategoryBinding) : RecyclerView.ViewHolder(binding.root){
            fun onBind(data: CategorySellPostDTO){
                binding.itemsellsearchcategory = data
            }
        }


    }

}