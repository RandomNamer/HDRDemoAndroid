package com.example.hdrplaybacktest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hdrplaybacktest.R
import com.example.hdrplaybacktest.data.VideoFileInfo
import com.example.hdrplaybacktest.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class VideoListFragment : Fragment() {

    private var _binding:FragmentFirstBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater,container,false)
        return binding.root
    }
    private val viewModel by lazy{
        (activity as MainActivity).listViewModel
    }

    private val onItemClickListener = { v: VideoFileInfo ->
        findNavController().navigate(
            R.id.action_FirstFragment_to_SecondFragment,
            bundleOf(getString(R.string.video_detail_frag_arg_bundle_name) to v.uri)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
//            buttonFirst.setOnClickListener {
//                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//            }
//            textviewFirst.text = buildSpannable(SpannableStringBuilderExt.STYLE_NORMAL){
//                section("section"){
//                    title("Hello") text "World"
//                    title("Hello") text "Android"
//                }
//                section("section 2"){
//                    title("hello") text "world"
//                }
//                section("section 3"){
//                    title("Hello") text "world"
//                    title("Hello") text "world"
//                    title("Hello") text "world"
//                }
//            }
            val adapter = VideoListAdapter()
            adapter.setOnClickListener(onItemClickListener)
            viewModel.videoHistoryLiveData.observe(viewLifecycleOwner){
                adapter.submitList(it)
            }
            videoHistoryRv.apply {
                setAdapter(adapter)
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val video = adapter.currentList[viewHolder.adapterPosition]
                    viewModel.deleteVideoEntry(video)
                }
            }).attachToRecyclerView(videoHistoryRv)

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}