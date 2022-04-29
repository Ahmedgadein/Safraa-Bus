package com.dinder.rihlabus.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinder.rihlabus.adapters.SeatAdapter
import com.dinder.rihlabus.data.model.Seat
import com.dinder.rihlabus.databinding.SeatsInfoFragmentBinding

class SeatsInfoFragment : Fragment() {
    private lateinit var binding: SeatsInfoFragmentBinding
    private var seats: List<Seat>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            seats = it.getParcelableArrayList<Seat>(SEATS)?.toList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SeatsInfoFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        val seatsAdapter = SeatAdapter()
        binding.seatsInfoRecyclerView.apply {
            adapter = seatsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        seatsAdapter.submitList(seats)
    }

    companion object {
        private const val SEATS = "seats"

        fun newInstance(seats: List<Seat>) =
            SeatsInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(SEATS, ArrayList(seats))
                }
            }
    }
}
