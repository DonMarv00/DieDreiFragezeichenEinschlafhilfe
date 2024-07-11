package de.msdevs.einschlafhilfe.fragments.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import de.msdevs.einschlafhilfe.R
import de.msdevs.einschlafhilfe.models.JsonResponse

class FilterFragment : Fragment() {

    private val episodeList = ArrayList<JsonResponse>()
    private lateinit var rvEpisodes : RecyclerView
    private lateinit var pgLoadEpisodes : ProgressBar

    companion object {
        private const val ARG_POSITION = "position"

        fun newInstance(position: Int): FilterFragment {
            val fragment = FilterFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    private var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
