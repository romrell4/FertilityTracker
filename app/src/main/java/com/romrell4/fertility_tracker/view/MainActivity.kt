package com.romrell4.fertility_tracker.view

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.ActivityMainBinding
import com.romrell4.fertility_tracker.databinding.ImportDialogBinding
import com.romrell4.fertility_tracker.viewmodel.ActivityViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val FRAGMENT_STATE_KEY = "fragmentState"

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private var fragmentState: FragmentState? = null
        set(new) {
            //Only replace the fragment if it's actually changing
            if (field != new && new != null) {
                supportFragmentManager.commit {
                    replace(R.id.frame, new.getFragment())
                }
            }
            field = new
        }
    private val dataEntryFragment by lazy { DataEntryFragment.newInstance() }
    private val chartFragment by lazy { ChartFragment.newInstance() }

    private val viewModel: ActivityViewModel by viewModels {
        defaultViewModelProviderFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set the default fragment
        fragmentState = FragmentState.DATA_ENTRY

        binding.bottomNav.setOnNavigationItemSelectedListener {
            fragmentState = when (it.itemId) {
                R.id.bottom_nav_entry -> FragmentState.DATA_ENTRY
                R.id.bottom_nav_chart -> FragmentState.CHART
                else -> throw NotImplementedError()
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.base_menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.export_option -> {
                val data = viewModel.exportData()
                getSystemService<ClipboardManager>()
                    ?.setPrimaryClip(ClipData.newPlainText("Fertility Tracker Exported Data", data))
                Log.i(TAG, "Exported '$data' to the clipboard")
                Toast.makeText(this, getString(R.string.export_success_message), Toast.LENGTH_LONG).show()
            }
            R.id.import_option -> {
                val binding = ImportDialogBinding.inflate(layoutInflater)
                MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.import_dialog_message))
                    .setView(binding.root)
                    .setPositiveButton(R.string.save) { _, _ ->
                        val data = binding.inputText.text.toString()
                        val message = try {
                            viewModel.importData(data)
                            fragmentState?.getFragment()?.reload()
                            getString(R.string.import_success_message)
                        } catch (e: Throwable) {
                            Log.e(TAG, "Failed to import '$data`", e)
                            getString(R.string.import_error_message)
                        }
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(FRAGMENT_STATE_KEY, fragmentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        fragmentState = savedInstanceState.getSerializable(FRAGMENT_STATE_KEY) as? FragmentState
    }

    enum class FragmentState {
        DATA_ENTRY,
        CHART
    }

    fun FragmentState.getFragment(): MainFragment = when (this) {
        FragmentState.DATA_ENTRY -> dataEntryFragment
        FragmentState.CHART -> chartFragment
    }
}

abstract class MainFragment : Fragment() {
    abstract fun reload()
}
