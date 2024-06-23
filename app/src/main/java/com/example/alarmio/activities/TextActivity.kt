package com.example.alarmio.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alarmio.R
import com.example.alarmio.databinding.ActivityTextBinding
import com.example.alarmio.service.AlarmService

class TextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTextBinding
    val texts = arrayOf(
        "Не считай дни, извлекай из них пользу",
        "Не ждите. Время никогда не будет подходящим",
        "Измени свои мысли и ты изменишь мир",
        "Не сдавайся. Начало всегда самое сложное",
        "С новым днем приходит новая сила и новые мысли",
        "Ничего не меняя, ничего не изменится",
        "Чтобы быть лучшим, нужно уметь справляться с худшим",
        "Будьте собой; все остальные уже заняты",
        "Счастье зависит от нас самих",
        "Кто не ошибается, тот не развивается"
    )
    private var promptText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_text)
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

        val randomIndex = (0 until texts.size).random()
        promptText = texts[randomIndex]

        binding.textToType.text = promptText
    }

    fun onSubmitClick() {
        val input = binding.userInput.text.toString()
        if (input == promptText) {
            val intentService = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(intentService)
            finishAffinity()
        } else {
            showIncorrectTextDialog()
        }
    }

    private fun showIncorrectTextDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Неправильный ввод")
            .setMessage("Текст введен неверно. Попробуйте еще раз.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                binding.userInput.text.clear()
            }
            .create()
            .show()
    }
}
