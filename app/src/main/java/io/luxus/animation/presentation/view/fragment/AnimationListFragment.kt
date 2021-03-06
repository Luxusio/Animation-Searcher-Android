package io.luxus.animation.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import dagger.hilt.android.AndroidEntryPoint
import io.luxus.animation.R
import io.luxus.animation.databinding.FragmentAnimationListBinding
import io.luxus.animation.domain.model.AnimationModel
import io.luxus.animation.presentation.view.adapter.AnimationListAdapter
import io.luxus.animation.presentation.view.custom.listener.RecyclerItemClickListener
import io.luxus.animation.presentation.viewmodel.AnimationListViewModel
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class AnimationListFragment : Fragment() {

    companion object {
        private val TAG: String = AnimationListFragment::class.simpleName.toString()
    }

    private val viewModel: AnimationListViewModel by viewModels()

    private lateinit var animationListAdapter: AnimationListAdapter

    private lateinit var startScroller: SmoothScroller
    private lateinit var recyclerView: RecyclerView

    private val pageListener = object : Listener {
        override fun onNextPageClicked(view: View) {
            val nowPage: Int = viewModel.getNowPage().get()
            if(nowPage == viewModel.getMaxPage().get()) return

            if (!startScroller.isRunning) {
                movePage(nowPage + 1);
            }
        }

        override fun onPrevPageClicked(view: View) {
            val nowPage: Int = viewModel.getNowPage().get()
            if (nowPage == 1) return

            if (!startScroller.isRunning) {
                movePage(nowPage - 1)
            }
        }

    }

    interface Listener {
        fun onNextPageClicked(view: View)
        fun onPrevPageClicked(view: View)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        animationListAdapter = AnimationListAdapter(viewModel.getAnimationList())
        animationListAdapter.setHasStableIds(true)
        val binding = FragmentAnimationListBinding.inflate(inflater, container, false)

        viewModel.init(animationListAdapter.listener)
        viewModel.getLoadStatus().observe(viewLifecycleOwner, {
            if (it == null) return@observe
            when (it) {
                AnimationListViewModel.LoadStatus.SUCCEED-> binding.progressBar.visibility = View.GONE
                AnimationListViewModel.LoadStatus.LOADING-> binding.progressBar.visibility = View.VISIBLE
                AnimationListViewModel.LoadStatus.FAILED-> Toast.makeText(
                    requireContext(), R.string.initial_load_failed,
                    Toast.LENGTH_LONG).show()
            }
        })

        startScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
        }

        recyclerView = binding.itemList
        recyclerView.adapter = animationListAdapter
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
        //recyclerView.isDrawingCacheEnabled = true
        //recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireContext(), object:
            RecyclerItemClickListener.OnItemClickListener.Builder() {
            override fun onItemClick(view: View, position: Int) {
                super.onItemClick(view, position)
                // TODO : move fragment


            }
        }))
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager: GridLayoutManager =
                        recyclerView.layoutManager as GridLayoutManager

                val firstPosition: Int = layoutManager.findFirstVisibleItemPosition()
                val lastPosition: Int = layoutManager.findLastVisibleItemPosition()

                if (firstPosition == -1) return

                val topModel: AnimationModel = viewModel.getAnimationList()[firstPosition]
                val firstPage: Int? = viewModel.getNumberPageHashMap()[topModel.id]
                if (firstPage == null) {
                    Log.e(TAG, "firstPage null(${topModel.id}, ${firstPosition})")
                    return
                }

                viewModel.getNowPage().set(firstPage)

                if (firstPage <= viewModel.getTPage() + viewModel.prefetchPage) {
                    viewModel.loadTopPage()
                }

                val botModel: AnimationModel = viewModel.getAnimationList()[lastPosition]
                val lastPage: Int? = viewModel.getNumberPageHashMap()[botModel.id]
                if (lastPage == null) {
                    Log.e(TAG, "lastPage null(${topModel.id}, ${firstPosition})")
                    return
                }

                if (lastPage >= viewModel.getBPage() - viewModel.prefetchPage) {
                    viewModel.loadBottomPage()
                }
            }
        })

        binding.pageEdit.clearFocus()
        binding.pageEdit.setOnEditorActionListener OnEditorActionListener@{ v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                var text = v.text.toString()
                val idx = text.indexOf('.')
                text = if (idx == -1) text else text.substring(0, idx)
                var toPage = text.toInt()

                if (toPage >= viewModel.getTPage() && toPage <= viewModel.getBPage()) {
                    if (abs(toPage - viewModel.getNowPage().get()) > 2) {
                        teleportPage(toPage)
                    } else {
                        movePage(toPage)
                    }
                } else {
                    toPage = 1.coerceAtLeast(toPage.coerceAtMost(viewModel.getMaxPage().get()))
                    viewModel.changePage(toPage)
                }

                val inputMethodManager = (v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

                v.clearFocus()
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        }

        binding.viewModel = viewModel
        binding.listener = pageListener
        binding.lifecycleOwner = viewLifecycleOwner

        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        Log.d("TEST", "CreateOptionMenu")
        viewModel.loadFirst()
    }

    private fun movePage(page: Int) {
        val position: Int = viewModel.getPagePositionHashMap()[page]!!
        startScroller.targetPosition = position
        recyclerView.layoutManager?.startSmoothScroll(startScroller)
    }

    private fun teleportPage(page: Int) {
        val position: Int = viewModel.getPagePositionHashMap()[page]!!
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
    }

}



