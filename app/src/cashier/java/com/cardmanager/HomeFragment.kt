package com.cardmanager

import android.app.AlertDialog
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cardmanager.databinding.FragmentHomeBinding
import com.cardmanager.utils.CardUtils
import com.cardmanager.viewmodels.HomeViewModel
import com.davidmiguel.numberkeyboard.NumberKeyboardListener
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var nfcAdapter: NfcAdapter? = null
    private val binding get() = _binding!!
    private val vm by viewModel<HomeViewModel>()

    private var amountText: String = ""
    private var amount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val nfcReaderCallback = NfcAdapter.ReaderCallback {
            val id = CardUtils.getId(it)
            vm.cardId.postValue(id)

            lifecycleScope.launch {
                val res = vm.checkCard(id!!)
                if(!res) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Добавить новую карту")
                    builder.setMessage("Карта в система не найдена. Хотите добавить карту?")
                    builder.setCancelable(true)
                    builder.setPositiveButton("Да") { dialogInterface, i ->
                        dialogInterface.dismiss()
                        lifecycleScope.launch {
                            val res = vm.addCard(id)
                            if(res)
                                Toast.makeText(requireContext(), "Карта добавлена успешно", Toast.LENGTH_SHORT).show()
                        }
                    }
                    val dialog = builder.create()
                    dialog.show()

                    Toast.makeText(requireContext(), "Карта не найдена", Toast.LENGTH_SHORT).show()
                }
            }
        }

        nfcAdapter =
            NfcAdapter.getDefaultAdapter(requireContext())

        if (nfcAdapter == null) {
            Toast.makeText(requireContext(), "NO NFC Capabilities",
                Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        nfcAdapter!!.enableReaderMode(
            requireActivity(),
            nfcReaderCallback,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null)

        /*
        vm.cardId.observe(viewLifecycleOwner) {
            binding.cardIdText.text = it
        }

         */

        vm.balance.observe(viewLifecycleOwner) {
            binding.cardBalanceText.text = "Баланс: $it"
        }

        vm.isCardAttached.observe(viewLifecycleOwner) {
            if(it) {
                binding.cardAttachedLayout.visibility = View.VISIBLE
                binding.cardNotAttachedLayout.visibility = View.GONE
            }
            else {
                amount = 0.0
                amountText = ""
                binding.amountEdittext.text = ""
                binding.cardAttachedLayout.visibility = View.GONE
                binding.cardNotAttachedLayout.visibility = View.VISIBLE
            }
        }

        binding.topupButton.setOnClickListener {
            println("Попытка пополнения в размере $amount")

            lifecycleScope.launch {
                val res = vm.addMoney(amount)
                if(res) {
                    Toast.makeText(requireContext(),
                        "Деньги зачислены успешно ($amount)",
                        Toast.LENGTH_SHORT).show()
                    vm.isCardAttached.postValue(false)
                }
                else {
                    Toast.makeText(requireContext(),
                        "Ошибка зачисления",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.refundButton.setOnClickListener {
            println("Попытка восполнения в размере $amount")

            lifecycleScope.launch {
                val res = vm.refundMoney(amount)
                if(res) {
                    Toast.makeText(requireContext(),
                        "Деньги сняты успешно ($amount)",
                        Toast.LENGTH_SHORT).show()
                    vm.isCardAttached.postValue(false)
                }
                else {
                    Toast.makeText(requireContext(),
                        "Ошибка снятия",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.keyboard.setListener(object: NumberKeyboardListener {
            override fun onLeftAuxButtonClicked() {
                // Comma button
                if (!hasComma(amountText)) {
                    amountText = if (amountText.isEmpty()) "0," else "$amountText,"
                    showAmount(amountText)
                }
            }

            override fun onNumberClicked(number: Int) {
                if (amountText.isEmpty() && number == 0) {
                    return
                }
                updateAmount(amountText + number)
            }

            override fun onRightAuxButtonClicked() {
                // Delete button
                if (amountText.isEmpty()) {
                    return
                }
                var newAmountText: String
                if (amountText.length <= 1) {
                    newAmountText = ""
                } else {
                    newAmountText = amountText.substring(0, amountText.length - 1)
                    if (newAmountText[newAmountText.length - 1] == ',') {
                        newAmountText = newAmountText.substring(0, newAmountText.length - 1)
                    }
                    if ("0" == newAmountText) {
                        newAmountText = ""
                    }
                }
                updateAmount(newAmountText)
            }
        })

        return root
    }

    private fun updateAmount(newAmountText: String) {
        val newAmount = if (newAmountText.isEmpty()) 0.0 else java.lang.Double.parseDouble(newAmountText.replace(",".toRegex(), "."))
        if (newAmount >= 0.0) {
            amountText = newAmountText
            amount = newAmount
            showAmount(amountText)
        }
    }

    private fun showAmount(amount: String) {
        binding.amountEdittext.text = amount.ifEmpty { "0,0" }
    }

    private fun hasComma(text: String): Boolean {
        for (element in text) {
            if (element == ',') {
                return true
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}