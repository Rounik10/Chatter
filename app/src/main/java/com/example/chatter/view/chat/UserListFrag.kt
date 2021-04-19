package com.example.chatter.view.chat

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatter.R
import com.example.chatter.adapters.UserListAdapter
import com.example.chatter.databinding.FragmentUserListBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User

class UserListFrag : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private val userAdapter by lazy { UserListAdapter() }

    private val client = ChatClient.instance()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)

        val actionbar = (requireActivity() as AppCompatActivity).supportActionBar
        actionbar?.show()
        actionbar?.title = "Search Users"

        setUpRecyclerView()
        queryAllUsers()

        binding.createGroupBtn.setOnClickListener {
            val success = userAdapter.createGroup(it)
            if(!success) {
                Toast.makeText(context, "Select at least 2 people", Toast.LENGTH_SHORT).show()
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.usersRecycler.layoutManager = LinearLayoutManager(context)
        binding.usersRecycler.adapter = userAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        searchView = menu.findItem(R.id.action_search).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query!!.isEmpty()) {
                    queryAllUsers()
                } else {
                    searchUser(query)
                }
                return true
            }
        })

        searchView.setOnCloseListener {
            queryAllUsers()
            false
        }

    }

    private fun searchUser(query: String) {
        val filter = Filters.and(
                Filters.autocomplete("name", query),
                Filters.ne("id", client.getCurrentUser()!!.id)
        )
        val request = QueryUsersRequest(
                filter = filter,
                offset = 0,
                limit = 100
        )
        client.queryUsers(request).enqueue {
            if (it.isSuccess) {
                val users = it.data()
                userAdapter.setData(users)
            } else {
                Log.e("UserListFrag", it.error().message.toString())
            }
        }
    }

    private fun queryAllUsers() {
        val request = QueryUsersRequest(
                filter = Filters.ne("id", client.getCurrentUser()!!.id),
                offset = 0,
                limit = 100
        )
        client.queryUsers(request).enqueue { result ->
            if (result.isSuccess) {
                val users: List<User> = result.data()
                userAdapter.setData(users)
            } else {
                Log.d("UserListFragment", result.error().message.toString())
            }
        }
    }

}