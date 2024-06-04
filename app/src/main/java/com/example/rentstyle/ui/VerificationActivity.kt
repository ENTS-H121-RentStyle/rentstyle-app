package com.example.rentstyle.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rentstyle.databinding.ActivityVerificationBinding

class VerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}