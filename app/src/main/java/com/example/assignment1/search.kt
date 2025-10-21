package com.example.assignment1

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Standardized User data class that matches the Firebase structure
data class User(
    var uid: String = "", // Made a var to be set manually from the snapshot key
    val username: String = "",
    val email: String = "",
    val searchUsername: String = "",
    val profilePicture: String = "",
    val bio: String = "",
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val createdAt: Long = 0
)

// Adapter for the search results RecyclerView
class UserAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.usernameTextView.text = user.username
        // In a real app, load user.profilePicture into the ImageView using Glide/Picasso
    }

    override fun getItemCount() = userList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val profileImageView: ImageView = itemView.findViewById(R.id.userProfileImageView)
    }
}

class search : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var exploreFeedScrollView: ScrollView
    private lateinit var filterChipGroup: ChipGroup
    private lateinit var sortChipGroup: ChipGroup

    private lateinit var userAdapter: UserAdapter
    private var allUsers = mutableListOf<User>()
    private var displayedUsers = mutableListOf<User>()

    private val followingUids = HashSet<String>()
    private val followerUids = HashSet<String>()

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private var currentSearchText = ""
    private var currentFilter = R.id.chipAll
    private var currentSort = R.id.chipUsername

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize UI components
        searchEditText = findViewById(R.id.searchEditText)
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)
        exploreFeedScrollView = findViewById(R.id.exploreFeedScrollView)
        filterChipGroup = findViewById(R.id.filterChipGroup)
        sortChipGroup = findViewById(R.id.sortChipGroup)

        setupRecyclerView()
        setupSearch()
        setupFiltersAndSorting()
        setupBottomNavigation()

        filterChipGroup.check(currentFilter)
        sortChipGroup.check(currentSort)
        fetchFollowAndFollowingUids()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(displayedUsers)
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        searchResultsRecyclerView.adapter = userAdapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchText = s.toString().trim()
                if (currentSearchText.isNotEmpty()) {
                    exploreFeedScrollView.visibility = View.GONE
                    searchResultsRecyclerView.visibility = View.VISIBLE
                    searchForUsers()
                } else {
                    exploreFeedScrollView.visibility = View.VISIBLE
                    searchResultsRecyclerView.visibility = View.GONE
                    allUsers.clear()
                    updateDisplayedUsers()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFiltersAndSorting() {
        filterChipGroup.setOnCheckedChangeListener { _, checkedId ->
            currentFilter = checkedId
            updateDisplayedUsers()
        }
        sortChipGroup.setOnCheckedChangeListener { _, checkedId ->
            currentSort = checkedId
            updateDisplayedUsers()
        }
    }

    private fun searchForUsers() {
        val searchText = currentSearchText.lowercase()
        val usersRef = database.getReference("users")
        val query = usersRef.orderByChild("searchUsername")
            .startAt(searchText)
            .endAt(searchText + "\uf8ff")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Diagnostic Toast to see how many results are returned
                Toast.makeText(this@search, "Found ${snapshot.childrenCount} users", Toast.LENGTH_SHORT).show()
                allUsers.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        user.uid = userSnapshot.key!!
                        if (user.uid != auth.currentUser?.uid) {
                            allUsers.add(user)
                        }
                    }
                }
                updateDisplayedUsers()
            }

            override fun onCancelled(error: DatabaseError) {
                // Diagnostic Toast for query errors
                Toast.makeText(this@search, "Search failed: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("SearchActivity", "Firebase Search Error", error.toException())
            }
        })
    }

    private fun updateDisplayedUsers() {
        val filteredList = when (currentFilter) {
            R.id.chipFollowing -> allUsers.filter { it.uid in followingUids }
            R.id.chipFollowers -> allUsers.filter { it.uid in followerUids }
            R.id.chipMutual -> allUsers.filter { it.uid in followingUids && it.uid in followerUids }
            else -> allUsers
        }

        val sortedList = when (currentSort) {
            R.id.chipFollowerCount -> filteredList.sortedByDescending { it.followerCount }
            R.id.chipLeastFollowers -> filteredList.sortedBy { it.followerCount }
            else -> filteredList.sortedBy { it.username }
        }

        displayedUsers.clear()
        displayedUsers.addAll(sortedList)
        userAdapter.notifyDataSetChanged()
    }

    private fun fetchFollowAndFollowingUids() {
        val currentUserId = auth.currentUser?.uid ?: return

        database.getReference("following").child(currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                followingUids.clear()
                for (child in snapshot.children) { child.key?.let { followingUids.add(it) } }
                if(currentSearchText.isNotEmpty()) updateDisplayedUsers() // Only update if a search is active
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        database.getReference("followers").child(currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                followerUids.clear()
                for (child in snapshot.children) { child.key?.let { followerUids.add(it) } }
                if(currentSearchText.isNotEmpty()) updateDisplayedUsers() // Only update if a search is active
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupBottomNavigation() {
        findViewById<ImageButton>(R.id.tab_1).setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) })
            overridePendingTransition(0, 0)
            finish()
        }
        findViewById<ImageButton>(R.id.tab_4_notification).setOnClickListener {
            startActivity(Intent(this, notifications::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
        findViewById<ImageButton>(R.id.tab_5).setOnClickListener {
            val intentMyProfile = Intent(this, OwnProfile::class.java)
            intent.getStringExtra("PROFILE_IMAGE_URI")?.let { intentMyProfile.putExtra("PROFILE_IMAGE_URI", it) }
            startActivity(intentMyProfile)
            overridePendingTransition(0, 0)
            finish()
        }
    }
}