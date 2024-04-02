package com.example.rublerate.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rublerate.R
import com.example.rublerate.data.Currency
import com.example.rublerate.databinding.FragmentCurrencyListBinding
import com.example.rublerate.network.CurrencyApi
import com.example.rublerate.network.RetrofitClient
import com.example.rublerate.ui.adapter.CurrencyAdapter
import com.example.rublerate.util.Constants
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment responsible for displaying a list of currencies fetched from an API.
 */
class CurrencyListFragment : Fragment() {
    // View binding for the fragment layout
    private var _binding: FragmentCurrencyListBinding? = null
    private val binding get() = _binding!!

    // Adapter for the RecyclerView
    private lateinit var currencyAdapter: CurrencyAdapter

    // Date format for API response
    private val apiDateFormat = SimpleDateFormat(Constants.API_DATE_FORMAT, Locale.getDefault())

    // Date format for displaying
    private val displayDateFormat = SimpleDateFormat(Constants.DISPLAY_DATE_FORMAT, Locale.getDefault())

    // Timer for periodic data fetching
    private var timer: Timer? = null

    // Handler for running tasks on the main thread
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflating the fragment layout
        _binding = FragmentCurrencyListBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initializing the RecyclerView adapter
        currencyAdapter = CurrencyAdapter(emptyList())
        binding.currencyRecyclerView.adapter = currencyAdapter
        binding.currencyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Setting up the swipe refresh layout listener
        binding.swipeRefreshLayout.setOnRefreshListener {
            startTimer()
        }

        // Starting the timer for periodic data fetching
        startTimer()

        return view
    }

    // Start the timer for periodic data fetching
    private fun startTimer() {
        // Cancel any existing timer
        stopTimer()
        // Create a new timer
        timer = Timer()
        // Schedule the timer task to run at fixed intervals
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Run the fetch data task on the main thread
                handler.post {
                    fetchData()
                }
            }
        }, 0, Constants.UPDATE_INTERVAL_MS.toLong())
    }

    // Stop the timer
    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    // Fetch data from the API
    private fun fetchData() {
        val currencyApi = RetrofitClient.retrofit.create(CurrencyApi::class.java)
        val call = currencyApi.getCurrencies()

        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                binding.circularProgressIndicator.visibility = CircularProgressIndicator.GONE
                binding.swipeRefreshLayout.isRefreshing = false

                if (response.isSuccessful) {
                    val gson = Gson()
                    val currentTime = Calendar.getInstance().time
                    try {
                        val responseData = response.body()
                        responseData?.let { data ->
                            val currencyMapType = object : TypeToken<Map<String, Any>>() {}.type
                            val currencyMap = gson.fromJson<Map<String, Any>>(gson.toJson(data), currencyMapType)

                            val date: Date? = apiDateFormat.parse(currencyMap["Timestamp"] as String)
                            val currencies = currencyMap["Valute"] as Map<*, *>

                            val currencyList = currencies.map { entry ->
                                val currencyInfo = entry.value as Map<*, *>
                                val id = currencyInfo["ID"] as String
                                val numCode = currencyInfo["NumCode"] as String
                                val charCode = currencyInfo["CharCode"] as String
                                val nominal = currencyInfo["Nominal"] as Double
                                val name = currencyInfo["Name"] as String
                                val value = currencyInfo["Value"] as Double
                                val previous = currencyInfo["Previous"] as Double
                                Currency(id, numCode, charCode, nominal, name, value, previous)
                            }

                            // Update UI on the main thread
                            requireActivity().runOnUiThread {
                                binding.currencyRecyclerView.visibility = View.VISIBLE
                                currencyAdapter.updateData(currencyList)
                                binding.topAppBar.subtitle = getString(R.string.last_updated,
                                    displayDateFormat.format(currentTime))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    // Handle HTTP error
                    showSnackbar(getString(R.string.http_error_message))
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.topAppBar.subtitle = getString(R.string.empty_last_updated)
                binding.circularProgressIndicator.visibility = View.VISIBLE
                binding.currencyRecyclerView.visibility = View.INVISIBLE
                // Handle network error
                showSnackbar(getString(R.string.network_error_message))
            }
        })
    }

    // Display a snackbar with the given message
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up resources
        _binding = null
        stopTimer()
    }

    override fun onResume() {
        super.onResume()
        // Restart the timer
        startTimer()
    }

    override fun onPause() {
        super.onPause()
        // Stop the timer
        stopTimer()
    }
}
