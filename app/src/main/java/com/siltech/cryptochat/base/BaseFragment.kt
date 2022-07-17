package com.siltech.cryptochat.base

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>
) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.cancel()
    }

    fun showErrorDialog(view: View) {

        val builder = AlertDialog.Builder(requireContext())

        with(builder) {
            setTitle("Ошибка")
            setMessage("Отсутствует интернет соединение")
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    protected open fun init() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
