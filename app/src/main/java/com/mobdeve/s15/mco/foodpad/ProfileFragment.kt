package com.mobdeve.s15.mco.foodpad

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {

    private lateinit var usernameTV : TextView
    private lateinit var bioTV : TextView
    private lateinit var followersTV : TextView

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_profile , container , false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameTV = view.findViewById(R.id.usernameTV)
        bioTV = view.findViewById(R.id.bioTV)
        followersTV = view.findViewById(R.id.numFollowersTV)

//        val username = activity?.intent?.getStringExtra(IntentKeys.USERNAME_KEY.name).toString()
//        val followerCount = activity?.intent?.getStringExtra(IntentKeys.FOLLOWER_COUNT_KEY.name).toString().toInt()
//        val bio = activity?.intent?.getStringExtra(IntentKeys.BIO_KEY.name).toString()
//
//        setUserProfile(username, followerCount, bio)

    }

    private fun setUserProfile(username: String, followerCount: Int, bio: String){
        usernameTV.text = username
        if(bio.isNotEmpty()){
            bioTV.text = bio
        }
        followersTV.text = "${followerCount} follower${if(followerCount < 1) "" else "s"}"
    }

}