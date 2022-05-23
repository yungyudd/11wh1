package com.edvard.myfitnessfriend.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.edvard.myfitnessfriend.*
import com.edvard.myfitnessfriend.activities.LoginActivity

import kotlinx.android.synthetic.main.fragment_my_page.*
import org.json.JSONException
import org.json.JSONObject

class MyPage : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_my_page, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(AppStat.myStat.getIsLogin()){
            loginButton.isEnabled = false
            loginButton.text = AppStat.myStat.getUserId()
            logoutButton.visibility = View.VISIBLE
            logoutButton.isEnabled = true
            val mRvAdapter = RvAdapter(context!!, AppStat.friendList)
            friendList.adapter = mRvAdapter

            val hour = AppStat.myStat.gettotalTime().toInt()/3600
            val min = AppStat.myStat.gettotalTime().toInt()/60 - hour * 60
            val sec = AppStat.myStat.gettotalTime().toInt()%60
            _time.text="${hour}시간 ${min}분 ${sec}초"
            _score.text =AppStat.myStat.gettotalCal().toInt().toString()+" Cal"
            val lm =LinearLayoutManager(context)
            friendList.layoutManager = lm
            friendList.setHasFixedSize(true)
        }
        else{
            loginButton.isEnabled = true
            loginButton.text = "로그인 해주세요."
            logoutButton.visibility = View.INVISIBLE
            logoutButton.isEnabled = false
        }
        loginButton.setOnClickListener {
            val mintent = Intent(context, LoginActivity::class.java)
            startActivity(mintent)
            activity?.finish()
        }
        logoutButton.setOnClickListener {
            AppStat.clear()
            activity?.recreate()
        }
        addFriendButton.setOnClickListener {
            if(AppStat.myStat.getIsLogin()){
                val builder = AlertDialog.Builder(context)
                val dialogView = layoutInflater.inflate(R.layout.addfriend, null)
                val dialogSearchFriendID = dialogView.findViewById<EditText>(R.id.searchFriendID)
                val dialogAddButton = dialogView.findViewById<Button>(R.id.addButton)
                dialogAddButton.setOnClickListener {
                    val userID = AppStat.myStat.getUserId()
                    val userFRIENDID = dialogSearchFriendID.text.toString()
                    //Toast.makeText(context, "$userID $userFRIENDID",Toast.LENGTH_SHORT).show()

                    val responseListener = Response.Listener<String?> { response ->
                        try {
                            val jsonObject = JSONObject(response)
                            val success = jsonObject.getBoolean("success")
                            if (success) {
                                if(!isDup(AppStat.friendList, User(userFRIENDID).getUserId()) && userFRIENDID != AppStat.myStat.getUserId()){
                                    AppStat.friendList.add(User(dialogSearchFriendID.text.toString()))
                                    Toast.makeText(context, "친구추가에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    Toast.makeText(context, "친구추가에 실패하였습니다.1", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "친구추가에 실패하였습니다2.", Toast.LENGTH_SHORT).show()
                                return@Listener
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    val friendRequest = FriendRequest(userID,userFRIENDID, responseListener)
                    val queue: RequestQueue = Volley.newRequestQueue(context)
                    queue.add(friendRequest)
            }

            builder.setView(dialogView).show()
        }
        else{
            Toast.makeText(context,"로그인을 해주세요.",Toast.LENGTH_SHORT).show()
        }
        }

    }
    fun isDup(list: ArrayList<User>, newID: String): Boolean{
        for(i in list){
            if(i.getUserId() == newID){
                return true
            }
        }
        return false
    }

    companion object{
        private const val num = "3"

        fun newInstance(Number: Int) : MyPage{
            return MyPage().apply{
                arguments = Bundle().apply {
                    arguments = Bundle().apply {
                        putInt(num, Number)
                    }
                }
            }
        }
    }
}