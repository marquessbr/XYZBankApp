package com.xyzbank.xyzbankapp.data.model

import com.xyzbank.bankapp.data.model.UserInfo
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


object JSONParser {
    @Throws(IOException::class)
    fun getUserAccount(username: String, password: String): UserInfo {
        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType =
            MediaType.parse("application/x-www-form-urlencoded")
        //val body = RequestBody.create(mediaType, "user=test_user&password=Test@1")
        val body = RequestBody.create(mediaType, "user=$username&password=$password")
        val request = Request.Builder()
            .url("https://bank-app-test.herokuapp.com/api/login")
            .method("POST", body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        val response = client.newCall(request).execute()

        // get JSONObject from JSON file
        val obj = JSONObject(response.body()!!.string())
        // fetch JSONObject named userAccount
        val ru: JSONObject = obj.getJSONObject("userAccount")
        return UserInfo(
            ru.getInt("userId"),
            ru.getString("name"),
            ru.getString("bankAccount"),
            ru.getString("agency"),
            ru.getDouble("balance")
        )
    }

    @Throws(IOException::class)
    fun getAccountEntries(id: String): ArrayList<AccountInfo> {
        val client = OkHttpClient().newBuilder()
                .build()
        val request = Request.Builder()
                .url("https://bank-app-test.herokuapp.com/api/statements/$id")
                .method("GET", null)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()
        val response = client.newCall(request).execute()

        // get JSONObject from JSON file
        val obj = JSONObject(response.body()!!.string())
        // fetch JSONObject into a array of accounts entries
        val jsonRoot = obj.getJSONArray("statementList")

        val result: ArrayList<AccountInfo> = ArrayList()
        for (i in 0 until jsonRoot.length()) {
            val accountData = jsonRoot.getJSONObject(i)
            var aci = AccountInfo("",",","",0.00)
            for (j in 0 until accountData.length()) {
                aci = AccountInfo(
                    accountData.getString("title"),
                    accountData.getString("desc"),
                    accountData.getString("date"),
                    accountData.getDouble("value")
                )
            }
            result.add(aci)
        }
        return result
    }

    /*
"statementList": [
    {
        "title": "Pagamento",
        "desc": "Conta de luz",
        "date": "2018-08-15",
        "value": -50
    }
],
"error": {}
}
*/
}
