package com.dicoding.storyapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.ui.home.HomeActivity
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.data.remote.response.LoginResponse

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                loginViewModel.loginUser(email, password)
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Please fill the form correctly",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        observerLoginResult()
    }

    private fun observerLoginResult() {
        loginViewModel.loginResult.observe(this@LoginActivity) { response ->
            when (response) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this@LoginActivity,
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

    private fun handleSuccess(data: LoginResponse) {
        AlertDialog.Builder(this@LoginActivity).apply {
            setTitle("Login Success")
            setMessage(data.message)
            setPositiveButton("OKE") { _, _ ->
                navigateToHome()
            }
            create()
            show()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun validateInput(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.length >= 8
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}