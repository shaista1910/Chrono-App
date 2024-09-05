package com.example.opensource_poe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.opensource_poe.databinding.ActivityTimerBinding
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class StopWatch : AppCompatActivity() {

    lateinit var binding: ActivityTimerBinding
    lateinit var dataHelper: DataHelper

    private val stopWatch = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataHelper = DataHelper(applicationContext)

        binding.startStopButton.setOnClickListener { startStopAction() }
        binding.resetButton.setOnClickListener { resetAction() }

        if (dataHelper.timerCounting() == true) {
            startTimer()
        } else {
            stopTimer()
            if (dataHelper.startTime() != null && dataHelper.stopTime() != null) {
                val time = Date().time - calcRestartTime().time
                binding.tvStopwatch.text = timeStringFromLong(time)
            }
        }

        stopWatch.scheduleAtFixedRate(TimeTask(), 0, 500)
    }

    private inner class TimeTask : TimerTask() {
        override fun run() {
            if (dataHelper.timerCounting() == true) {
                val time = Date().time - dataHelper.startTime()!!.time
                binding.tvStopwatch.text = timeStringFromLong(time)

                // Check if time is 30 minutes or more
                val minutes = (time / (1000 * 60) % 60)
                if (minutes >= 1) {
                    // Stop the timer
                    stopTimer()

                    // Show the popup message
                    runOnUiThread {
                        showPopupDialog()
                    }
                }
            }
        }
    }

    private fun resetAction() {
        dataHelper.setStopTime(null)
        dataHelper.setStartTime(null)
        stopTimer()
        binding.tvStopwatch.text = timeStringFromLong(0)
    }

    private fun stopTimer() {
        dataHelper.setTimerCounting(false)
        binding.startStopButton.text = getString(R.string.start)
    }

    private fun startTimer() {
        dataHelper.setTimerCounting(true)
        binding.startStopButton.text = getString(R.string.stop)
    }

    private fun startStopAction() {
        if (dataHelper.timerCounting() == true) {
            dataHelper.setStopTime(Date())
            stopTimer()
        } else {
            if (dataHelper.stopTime() != null) {
                dataHelper.setStartTime(calcRestartTime())
                dataHelper.setStopTime(null)
            } else {
                dataHelper.setStartTime(Date())
            }
            startTimer()
        }
    }

    private fun calcRestartTime(): Date {
        val diff = dataHelper.startTime()!!.time - dataHelper.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff)
    }

    private fun timeStringFromLong(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60) % 60)
        val hours = (ms / (1000 * 60 * 60) % 24)
        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun showPopupDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Take Another Screen?")
            .setMessage("Do you want to go play a game?")
            .setPositiveButton("Yes") { dialog, which ->
                // Start the AnotherScreenActivity
                val intent = Intent(this@StopWatch, SnakeGame::class.java)
                startActivity(intent)
            }
            .setNegativeButton("No") { dialog, which ->
                // User chose not to go to another screen, resume the timer
                startTimer()
            }
            .setCancelable(false) // Prevent dismissing the dialog by tapping outside

        val dialog = builder.create()
        dialog.show()
    }
}