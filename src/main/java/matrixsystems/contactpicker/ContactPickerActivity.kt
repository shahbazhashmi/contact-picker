package matrixsystems.contactpicker

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.SearchManager
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import java.util.ArrayList


internal class ContactPickerActivity : AppCompatActivity() , ContactFetchListener,
    ContactSelectionListener {

    val SHOW_LOADING = 0
    val SHOW_DATA = 1
    val SHOW_ERROR = 2

    private var viewState = SHOW_LOADING

    private var fetchedAll = false
    private var isLoading = false

    lateinit var pickerConfig : PickerConfig
    lateinit var footerLt : RelativeLayout
    lateinit var errorTv : TextView
    lateinit var clearTv : TextView
    lateinit var finishTv : TextView
    lateinit var progressBar : ProgressBar
    lateinit var recyclerView : RecyclerView
    lateinit var contactAdapter : ContactAdapter
    lateinit var contactHelper : ContactHelper

    var contactList : MutableList<Contact> = mutableListOf()

    var PAGE = 0;
    var SEARCH : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_picker)

        errorTv = findViewById(R.id.error_tv) as TextView
        clearTv = findViewById(R.id.clear_tv) as TextView
        finishTv = findViewById(R.id.finish_tv) as TextView
        footerLt = findViewById(R.id.footer_lt) as RelativeLayout
        progressBar = findViewById(R.id.progress_bar) as ProgressBar
        recyclerView = findViewById(R.id.recycler_view) as RecyclerView

        initIntentData(intent)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        contactAdapter =
            ContactAdapter(this@ContactPickerActivity, this, contactList, pickerConfig)
        recyclerView.adapter = contactAdapter
        contactHelper = ContactHelper(this@ContactPickerActivity)

        loadData("", 0)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = /*recyclerView.getChildCount()*/ 15;
                val totalItemCount = mLayoutManager.getItemCount();
                val firstVisibleItemIndex = mLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !fetchedAll && (totalItemCount - visibleItemCount) <= firstVisibleItemIndex) {
                    loadData(SEARCH, ++PAGE)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })


        clearTv.setOnClickListener(){
            contactAdapter.clearSelection()
            contactAdapter.notifyDataSetChanged()
        }

        finishTv.setOnClickListener(){
            returnDataAndClose(true)
        }

    }


    private fun initIntentData(intent: Intent){
        pickerConfig = intent.getParcelableExtra<PickerConfig>(ContactPicker.CONFIG_OBJCET)

        title = pickerConfig.toolbarTitle

        Utils.setTint(progressBar, pickerConfig.loaderColor, false)
    }


    private fun loadData(search : String, page : Int){
        SEARCH = search
        PAGE = page
        isLoading = true
        if(PAGE==0) { // show main loader
            showState(SHOW_LOADING)
        }else{ // show load more loader
            contactAdapter.showLoading(true)
        }
        fetchedAll = false
        contactHelper.fetchContacts(search, page).execute()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.contact_picker_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)

        val searchManager = this@ContactPickerActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        var searchView: SearchView? = null
        if (searchItem != null) {
            searchView = searchItem!!.getActionView() as SearchView
        }
        if (searchView != null) {
            searchView!!.setSearchableInfo(searchManager.getSearchableInfo(this@ContactPickerActivity.getComponentName()))
            //searchView.setIconifiedByDefault(false)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String): Boolean {
                    return true
                }

                override fun onQueryTextChange(p0: String): Boolean {
                    if(!p0.equals(SEARCH)) {
                        loadData(p0, 0)
                    }
                    return true
                }
            });
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onContactsFetched(search: String, page: Int, list: MutableList<Contact>) {

        /**
         * accept only current search result
         */
        if(!search.equals(SEARCH) || page!=PAGE){
            return
        }

        // no data found matrixsystems page 1
        if(PAGE==0 && list.size==0){
            contactList.clear()
            if (TextUtils.isEmpty(SEARCH)) {
                showState(SHOW_ERROR, getString(R.string.contact_not_found))
            } else {
                showState(SHOW_ERROR, getString(R.string.search_not_found))
            }
        }
        // no data found on load more
        else if(PAGE!=0 && list.size==0){
            fetchedAll = true
            contactAdapter.showLoading(false)
        }
        // data found on page 1
        else if(PAGE==0 && list.size!=0){
            contactList.clear()
            contactList.addAll(list)
            showState(SHOW_DATA)
        }
        // data found on load more
        else if(PAGE!=0 && list.size!=0){
            contactList.addAll(list)
            contactAdapter.showLoading(false)
        }

        isLoading = false
        contactAdapter.notifyDataSetChanged()
    }

    override fun onError(message: String) {
        Toast.makeText(this@ContactPickerActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showState(state : Int, errorMessage : String = ""){
        viewState = state
        when(state){
            SHOW_LOADING -> {
                if(PAGE==0) // dont show loader on load more
                {
                    errorTv.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
            }
            SHOW_DATA -> {
                errorTv.visibility = View.GONE
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            SHOW_ERROR -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                errorTv.text = errorMessage
                errorTv.visibility = View.VISIBLE
            }
        }
    }


    override fun onSelectionChange(count: Int) {
        if(count==0){
            footerLt.visibility = View.GONE
        }
        else{
            footerLt.visibility = View.VISIBLE
        }
        //Toast.makeText(this@ContactPickerActivity, count.toString()+" contacts selected", Toast.LENGTH_SHORT).show()
    }


    fun returnDataAndClose(flag : Boolean){
        val returnIntent = Intent()
        if(flag) {
            returnIntent.putParcelableArrayListExtra(ContactPicker.CONTACTS_LIST, ArrayList(contactAdapter.selectedList))
            setResult(Activity.RESULT_OK, returnIntent)
        }
        else{
            setResult(Activity.RESULT_CANCELED, returnIntent)
        }

        finish()
    }


    override fun onBackPressed() {
        returnDataAndClose(false)
    }
}
