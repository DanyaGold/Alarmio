package com.example.alarmio.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alarmio.R
import com.example.alarmio.databinding.ActivityMathBinding
import com.example.alarmio.service.AlarmService

class MathActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMathBinding
    private var correctAnswer: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_math)
        binding.activity = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        generateMathProblem()
    }

    fun onSubmitAnswerClick() {
        val answer = binding.mathAnswer.text.toString().toIntOrNull()
        if (answer == correctAnswer) {
            val intentService = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(intentService)
            finishAffinity()
        } else {
            showIncorrectAnswerDialog()
        }
    }

    private fun generateMathProblem() {
        val a = (1..10).random()
        val b = (1..10).random()
        val c = (1..10).random()
        val operation = (1..4).random()

        val problemText = when (operation) {
            1 -> {
                correctAnswer = a + b
                "$a + $b = ?"
            }
            2 -> {
                correctAnswer = (a + b) * c
                "($a + $b) * $c = ?"
            }
            3 -> {
                correctAnswer = a * b + c
                "$a * $b + $c = ?"
            }
            4 -> {
                correctAnswer = a * b
                "$a * $b = ?"
            }
            5 -> {
                correctAnswer = a * b
                "$a + $b + $c = ?"
            }
            else -> {
                correctAnswer = a + b
                "$a + $b = ?"
            }
        }
        binding.mathProblem.text = problemText
    }

    private fun showIncorrectAnswerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Неправильный ответ")
            .setMessage("Ответ неверный. Попробуйте еще раз.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                binding.mathAnswer.text.clear()
            }
            .create()
            .show()
    }
}
