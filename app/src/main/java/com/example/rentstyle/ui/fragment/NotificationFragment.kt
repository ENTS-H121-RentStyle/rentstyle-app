package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.FragmentNotificationBinding
import com.example.rentstyle.helpers.FilterModel.getNotificationFilter
import com.example.rentstyle.helpers.adapter.RecyclerFilterAdapter

class NotificationFragment : Fragment() {
    private lateinit var _binding: FragmentNotificationBinding
    private val binding get() = _binding

    private lateinit var filterNotification: RecyclerView
    private lateinit var filterAdapter: RecyclerFilterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        filterNotification = binding.rvFilterNotificationResult
        filterAdapter = RecyclerFilterAdapter(getNotificationFilter(requireContext()))

        filterNotification.adapter = filterAdapter

        filterAdapter.setOnClickListener(object : RecyclerFilterAdapter.OnClickListener {
            override fun onClick(position: Int) {
                filterAdapter.resetFilterAppearance()
            }

        })

        return binding.root
    }
}