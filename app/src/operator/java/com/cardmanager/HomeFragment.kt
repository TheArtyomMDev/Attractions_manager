package com.cardmanager


import android.media.MediaPlayer
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cardmanager.adapters.AttractionsRecyclerAdapter
import com.cardmanager.databinding.FragmentHomeBinding
import com.cardmanager.utils.CardUtils
import com.cardmanager.viewmodels.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var nfcAdapter: NfcAdapter? = null
    private val binding get() = _binding!!
    private val vm by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val successMusic: MediaPlayer = MediaPlayer.create(requireActivity(), R.raw.success)
        val failedMusic: MediaPlayer = MediaPlayer.create(requireActivity(), R.raw.failed)

        val nfcReaderCallback = NfcAdapter.ReaderCallback {
            val id = CardUtils.getId(it)
            vm.cardId.postValue(id)

            nfcAdapter!!.disableReaderMode(requireActivity())

            lifecycleScope.launch {
                val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.scale)
                lateinit var layout: ConstraintLayout

                var res = vm.checkCard(id!!)
                if(!res) {

                    Toast.makeText(requireContext(), "Карта не найдена", Toast.LENGTH_SHORT).show()
                    layout = binding.failedLayout

                    failedMusic.start()

                }
                else {
                    val attraction = vm.chosenAttraction.value!!
                    val balance = vm.getCard(id).balance

                    vm.balance.postValue(balance.toString().toDouble())

                    println(attraction)
                    println("Попытка списания в размере ${attraction.price}")

                    res = vm.spendMoney(attraction)

                    layout = if(res) {
                        successMusic.start()
                        binding.moneyLeftText.text = (balance - attraction.price).toString()
                        binding.successLayout
                    } else {
                        failedMusic.start()
                        binding.moneyLeftText2.text = balance.toString()
                        binding.failedLayout
                    }

                }

                anim.setAnimationListener(object: AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {}

                    override fun onAnimationEnd(p0: Animation?) {
                        lifecycleScope.launch {
                            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.hide)
                            delay(2000)

                            layout.startAnimation(anim)
                            delay(400)
                            layout.visibility = View.GONE
                            binding.cardAttachedLayout.visibility = View.VISIBLE
                        }
                    }

                    override fun onAnimationRepeat(p0: Animation?) {}
                })

                layout.visibility = View.VISIBLE
                binding.cardAttachedLayout.visibility = View.GONE
                binding.cardNotAttachedLayout.visibility = View.GONE
                layout.startAnimation(anim)
                vm.isCardAttached.postValue(false)
            }

        }



        /*
        vm.cardId.observe(viewLifecycleOwner) {
            binding.cardIdText.text = it
        }

         */

        /*
        vm.balance.observe(viewLifecycleOwner) {
            binding.cardBalanceText.text = "Баланс: $it"
        }

         */

        /*
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
        */


        binding.payButton.setOnClickListener {
            println("Ждём карту")

            binding.cardAttachedLayout.visibility = View.GONE
            binding.cardNotAttachedLayout.visibility = View.VISIBLE

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
                NfcAdapter.FLAG_READER_NFC_A
                        or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
                        or NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                null)

        }

        vm.chosenAttraction.observe(viewLifecycleOwner) {
            binding.payButton.isEnabled = (it.id != "")
        }



        binding.recView.adapter = AttractionsRecyclerAdapter(viewLifecycleOwner, vm.attractionsLD, vm.chosenAttraction, requireContext())
        binding.recView.layoutManager = LinearLayoutManager(requireContext())

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}