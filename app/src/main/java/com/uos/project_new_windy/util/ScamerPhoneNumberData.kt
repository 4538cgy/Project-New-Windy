package com.uos.project_new_windy.util

class ScamerPhoneNumberData {

    fun getSamerPhoneNumberData() : ArrayList<String>{

        var data : ArrayList<String> = arrayListOf()

        data.add("01064665153")
        data.add("01024775109")
        data.add("01084604905")
        data.add("01022646220")
        data.add("01097165662")
        data.add("01064475949")
        data.add("01075022091")
        data.add("01048837276")
        data.add("01021740051")
        data.add("01048367661")
        data.add("01083061730")
        data.add("01021810297")
        data.add("01021181475")
        data.add("01097391475")
        data.add("01065616974")
        data.add("01083792930")
        data.add("01092522703")
        data.add("01084422431")
        data.add("01058974301")
        data.add("01076578647")
        data.add("01075542859")
        data.add("01057984301")
        data.add("01080894728")
        data.add("01077933123")
        data.add("01039821160")
        data.add("01084205420")
        data.add("01066353605")
        data.add("01064672310")
        data.add("01075128024")
        data.add("01021985639")
        data.add("01075988749")
        data.add("01075988749")
        data.add("01075022091")
        data.add("01056215252")
        data.add("01048837276")
        data.add("01084622913")
        data.add("01084422431")
        data.add("01058974301")
        data.add("01079062034")
        data.add("01048351585")
        data.add("01074980745")
        data.add("01025292385")
        data.add("01084691603")
        data.add("01084635818")
        data.add("01095472421")
        data.add("01074108443")
        data.add("01057099911")
        data.add("01097285508")
        data.add("01097665662")
        data.add("01021740051")
        data.add("01064665153")
        data.add("01084604905")
        data.add("01083672581")
        data.add("01021181475")
        data.add("01057884905")
        data.add("01086555152")
        data.add("01086555152")
        data.add("01021324565")
        data.add("01075474041")
        data.add("01028162548")
        data.add("01026589902")
        data.add("01023754302")
        data.add("01057933404")
        data.add("01097649692")
        data.add("01058576452")
        data.add("01021181475")
        data.add("01097211663")
        data.add("01084622913")
        data.add("01083264041")
        data.add("01039522662")
        data.add("01021502413")
        data.add("01076124483")
        data.add("01027615115")
        data.add("01083582198")
        data.add("01082593874")
        data.add("01076600401")
        data.add("01043510181")
        data.add("01039821160")
        data.add("01034759899")
        data.add("01076690228")
        data.add("01076892214")
        data.add("01052504920")
        data.add("01075542859")
        data.add("01097391479")
        data.add("01065523251")
        data.add("01058974301")
        data.add("01021514667")
        data.add("01084422431")
        data.add("01077077877")
        data.add("01058623530")
        data.add("01067456598")
        data.add("01048367661")
        data.add("01096355577")
        data.add("01022646220")
        data.add("01064665153")
        data.add("01026333753")
        data.add("01048880045")
        data.add("01021500516")
        data.add("01027034621")
        data.add("01065843069")
        data.add("01057235949")
        data.add("01075474041")
        data.add("01097069498")
        data.add("01072939849")
        data.add("01023754032")

        return data
    }

    fun getExistPhoneNumber(data : String) : Boolean{

        var exist : Boolean = false
        var numberData : ArrayList<String> = arrayListOf()
        numberData = getSamerPhoneNumberData()

        numberData.forEach {
            if (it.contains(data)){
                exist = true
            }
        }


        return exist
    }

}