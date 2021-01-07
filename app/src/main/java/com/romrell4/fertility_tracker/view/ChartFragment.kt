package com.romrell4.fertility_tracker.view

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.romrell4.fertility_tracker.databinding.FragmentLiveChartBinding
import com.romrell4.fertility_tracker.databinding.ViewHolderChartCycleBinding
import com.romrell4.fertility_tracker.viewmodel.ChartViewModel
import com.romrell4.fertility_tracker.viewmodel.ChartViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.OutputStream


@ExperimentalCoroutinesApi
class ChartFragment : MainFragment(), ExportDelegate {
    private val viewModel: ChartViewModel by viewModels {
        defaultViewModelProviderFactory
    }
    private val adapter = CycleAdapter(this)

    private lateinit var binding: FragmentLiveChartBinding
    private val permissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            exportDataCache?.let {
                doExport(it.first, it.second)
                exportDataCache = null
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveChartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.viewStateFlow.collect {
                render(it)
            }
        }

        binding.cyclesRecyclerView.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(context)
        }

        viewModel.loadAllEntries()
    }

    override fun reload() {
        viewModel.loadAllEntries()
    }

    override fun exportCycleView(binding: ViewHolderChartCycleBinding, filename: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            doExport(binding, filename)
        } else {
            exportDataCache = binding to filename
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun doExport(binding: ViewHolderChartCycleBinding, filename: String) {
        getOutputStream(filename).use {
            val bitmap = Bitmap.createBitmap(
                binding.root.width - binding.chartScrollView.width + binding.scrollContentView.width,
                binding.root.height,
                Bitmap.Config.ARGB_8888
            )
            Canvas(bitmap).also { canvas ->
                // Set the background
                canvas.drawColor(Color.WHITE)

                fun View.draw(x: Float = this.x, y: Float = this.y) {
                    println("Moving to ($x, $y)")
                    canvas.translate(x, y)
                    println("Drawing")
                    draw(canvas)
                    println("Moving back to the origin")
                    canvas.translate(-x, -y)
                }
                binding.cycleNumberText.draw()
                binding.cycleDetailsLayout.draw()
                binding.yAxisLayout.draw()
                binding.scrollContentView.draw(binding.chartScrollView.x, binding.chartScrollView.y)
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 0, it) // PNG is lossless, so quality will be ignored
            it.flush()
            it.close()
        }

        Toast.makeText(
            requireContext(),
            "You're file has been saved to your Downloads folder as '$filename'",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun getOutputStream(filename: String): OutputStream {
        val filenameWithExt = "$filename.png"
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val resolver = requireContext().contentResolver
            resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filenameWithExt)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                }
            )?.let { uri ->
                resolver.openOutputStream(uri)
            } ?: throw IllegalStateException("Unable to open file stream")
        } else {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                filenameWithExt
            ).outputStream()
        }
    }

    private fun render(viewState: ChartViewState) {
        adapter.submitList(viewState.cycles)
    }

    companion object {
        private var exportDataCache: Pair<ViewHolderChartCycleBinding, String>? = null

        fun newInstance(): ChartFragment {
            return ChartFragment()
        }
    }
}
