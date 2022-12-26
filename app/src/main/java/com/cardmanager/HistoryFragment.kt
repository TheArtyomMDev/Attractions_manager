package com.cardmanager

import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cardmanager.adapters.TransactionsRecyclerAdapter
import com.cardmanager.data.Attraction
import com.cardmanager.databinding.FragmentHistoryBinding
import com.cardmanager.utils.CardUtils
import com.cardmanager.viewmodels.HistoryViewModel
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val vm by viewModel<HistoryViewModel>()
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var nfcReaderCallback: NfcAdapter.ReaderCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {


        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        nfcReaderCallback = NfcAdapter.ReaderCallback {
            val id = CardUtils.getId(it)
            vm.cardId.postValue(id)

            lifecycleScope.launch {
                val res = vm.checkCard(id!!)
                if(!res) {
                    Toast.makeText(requireContext(), "Карта не найдена", Toast.LENGTH_SHORT).show()
                    vm.isCardAttached.postValue(false)
                }
            }
        }

        nfcAdapter =
            NfcAdapter.getDefaultAdapter(requireContext())

        if (nfcAdapter == null) {
            Toast.makeText(requireContext(), "NO NFC Capabilities",
                Toast.LENGTH_SHORT).show()
            // requireActivity().finish()
        }

        nfcAdapter!!.enableReaderMode(
            requireActivity(),
            nfcReaderCallback,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null)

        vm.cardId.observe(viewLifecycleOwner) {
            binding.cardIdText.text = it
            lifecycleScope.launch {
                vm.updateTransactions()
            }
        }

        vm.balance.observe(viewLifecycleOwner) {
            binding.cardBalanceText.text = "Баланс: $it"
        }

        vm.isCardAttached.observe(viewLifecycleOwner) {
            if(it) {
                binding.cardAttachedLayout.visibility = View.VISIBLE
                binding.cardNotAttachedLayout.visibility = View.GONE
            }
            else {
                binding.cardAttachedLayout.visibility = View.GONE
                binding.cardNotAttachedLayout.visibility = View.VISIBLE
            }
        }

        lifecycleScope.launch {
            val attractions = vm.getAllAttractions()

            print("attractions list: $attractions")

            val mappedAttractions = mutableMapOf<String, Attraction>()
            for (attraction in attractions) {
                mappedAttractions[attraction.id] = attraction
            }

            binding.recView.adapter = TransactionsRecyclerAdapter(
                viewLifecycleOwner, vm.transactionsLD, mappedAttractions)
            binding.recView.layoutManager = LinearLayoutManager(requireContext())
        }

        return root
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter!!.enableReaderMode(
            requireActivity(),
            nfcReaderCallback,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null)
    }

    private suspend fun getAttractionById(id: String): Attraction? {
        return vm.getAttractionById(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}