package com.example.newswavtest.ui.gist_detail

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.newswavtest.R
import com.example.newswavtest.databinding.ActivityGistDetailBinding
import com.example.newswavtest.utils.EXTRA_GIST_ID
import com.example.popsicaltest.utils.InjectorUtils

class GistDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGistDetailBinding
    private val gistDetailVM: GistDetailVM by viewModels {
        InjectorUtils.provideGistDetailViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gist_detail)
        binding.apply {
            vm = gistDetailVM
            lifecycleOwner = this@GistDetailActivity
        }

        setSupportActionBar(findViewById(R.id.toolbar))

        // get gist detail based on id from intent
        gistDetailVM.gistId = intent.getStringExtra(EXTRA_GIST_ID)
        gistDetailVM.getGistDetailLD()
        gistDetailVM.getGistDetailFromApi(this)

        setupRefreshObserver()
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

    private fun setupRefreshObserver() {
        gistDetailVM.isLoadingMLD.observe(this, Observer {
            binding.swipe.isRefreshing = it
        })
    }
}