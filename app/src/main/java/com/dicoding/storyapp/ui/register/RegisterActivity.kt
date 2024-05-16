package com.dicoding.storyapp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.remote.response.MessageResponse
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.ui.login.LoginActivity
import com.dicoding.storyapp.ui.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupObervers()

    }

    private fun setupObervers() {
        registerViewModel.responseResultState.observe(this@RegisterActivity) { response ->
            when (response) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this@RegisterActivity,
                        response.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is ResultState.Success -> {
                    showLoading(false)
                    handleSuccess(response.data)
                }

                else -> showLoading(false)
            }
        }
    }

    private fun setupView() {
        binding.apply {
            signupButton.setOnClickListener {
                if (nameEditText.text?.isNotEmpty() == true &&
                    emailEditText.text?.isNotEmpty() == true &&
                    passwordEditText.text?.length!! >= 8
                ) {
                    registerViewModel.registerUser(
                        name = nameEditText.text.toString().trim(),
                        email = emailEditText.text.toString().trim(),
                        password = passwordEditText.text.toString().trim()
                    )
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please fill the form correctly",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun handleSuccess(data: MessageResponse) {
        AlertDialog.Builder(this@RegisterActivity).apply {
            setTitle("User Registered")
            setMessage(data.message)
            setPositiveButton("OKE") { _, _ ->
                navigateToLogin()
            }
            create()
            show()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

