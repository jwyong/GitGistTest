package com.example.newswavtest.ui.gists_list

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newswavtest.R
import com.example.newswavtest.databinding.ActivityGistsListBinding
import com.example.newswavtest.db.repo.OfflineSyncRepo
import com.example.popsicaltest.utils.InjectorUtils

class GistsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGistsListBinding
    private val gistsListVM: GistsListVM by viewModels {
        InjectorUtils.provideGistsListViewModelFactory(this)
    }
    private val gistListAdapter = GistsListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gists_list)
        binding.apply {
            vm = gistsListVM
            lifecycleOwner = this@GistsListActivity
        }

        setSupportActionBar(findViewById(R.id.toolbar))

        // setup liveData to rv, then get latest from server
        setupGistListRV()
        gistsListVM.getGistsListFromApi(this)

        setupRefreshObserver()
        setupOfflineSyncObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupGistListRV() {
        binding.rvGistList.apply {
            adapter = gistListAdapter

            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context, layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
        }

        gistsListVM.gistListLD.observe(this, Observer {
            gistListAdapter.submitList(it)
        })
    }

    private fun setupRefreshObserver() {
        gistsListVM.isLoadingMLD.observe(this, Observer {
            binding.swipe.isRefreshing = it
        })
    }

    private fun setupOfflineSyncObserver() {
        gistsListVM.offlineSyncListLD.observe(this, Observer {
            if (it.isEmpty())
                gistsListVM.getGistsListFromApi(this)
        })
    }
}