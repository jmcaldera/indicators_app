package dev.josecaldera.indicators.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.josecaldera.indicators.R
import dev.josecaldera.indicators.databinding.FragmentLoginBinding
import dev.josecaldera.indicators.toolbar.ToolbarViewModel
import dev.josecaldera.indicators.utils.hideSoftKeyboard
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.emptyState

class LoginFragment : Fragment() {

    private val toolbarViewModel: ToolbarViewModel by sharedViewModel()
    private val viewModel: LoginViewModel by viewModel(state = emptyState())
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeState()
        observeEvents()
    }

    private fun setupViews() {

        binding.fieldEmail.doAfterTextChanged {
            viewModel.email.value = it.toString()
        }

        binding.fieldPassword.apply {
            setOnFocusChangeListener { _, hasFocus ->
                viewModel.onPasswordFocusChanged(hasFocus)
            }

            doAfterTextChanged {
                viewModel.password.value = it.toString()
            }
        }

        binding.buttonLogIn.setOnClickListener {
            viewModel.onLoginClicked()
        }

        toolbarViewModel.hide()
    }

    private fun observeState() {
        viewModel.isLoading.observe(viewLifecycleOwner, { loading ->
            binding.refreshLogin.isRefreshing = loading
        })

        viewModel.email.observe(viewLifecycleOwner, {
            val current = binding.fieldEmail.text.toString()
            if (current != it) binding.fieldEmail.setText(it)
        })

        viewModel.emailError.observe(viewLifecycleOwner, {
            if (it.isNotBlank()) {
                binding.layoutEmail.error = it
            } else {
                binding.layoutEmail.error = null
            }
        })

        viewModel.password.observe(viewLifecycleOwner, {
            val current = binding.fieldPassword.text.toString()
            if (current != it) binding.fieldPassword.setText(it)
        })

        viewModel.isLogInButtonEnabled.observe(viewLifecycleOwner, { enabled ->
            binding.buttonLogIn.isEnabled = enabled
        })
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    LoginViewModel.Event.HideSoftKeyboard -> hideSoftKeyboard()
                    LoginViewModel.Event.LoginError -> {
                        // For demo purposes, let's show a simple dialog
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.error_message)
                            .setMessage(R.string.error_invalid_credentials)
                            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    is LoginViewModel.Event.LoginSuccess -> {
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginToIndicators(event.user.name)
                        )
                    }
                }
            }
        }
    }
}
