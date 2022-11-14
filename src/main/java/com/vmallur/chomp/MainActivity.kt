package com.vmallur.chomp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val rows = 5
    private val columns = 4

    private lateinit var menuItemRestart: MenuItem
    private lateinit var progressBar: ProgressBar
    private lateinit var txtTurn: TextView
    private lateinit var viewChocolate: GridView

    private var isOpponentsTurn = false
    private var search = Search(rows, columns)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        progressBar = findViewById(R.id.progress_bar)
        txtTurn = findViewById(R.id.txt_turn)
        viewChocolate = findViewById(R.id.view_chocolate)
        viewChocolate.adapter = ChocolateAdapter(this, getStartingPosition())
        viewChocolate.setOnItemClickListener { _, _, index, _ ->
            if (!isOpponentsTurn) {
                chomp(index)
            }
        }

        showHelpDialog()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menuItemRestart = menu.getItem(1)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.help -> {
                showHelpDialog()
                true
            }
            R.id.restart -> {
                restart()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getStartingPosition(): List<Boolean> {
        val startingPosition = ArrayList<Boolean>()
        startingPosition.add(false)
        for (i in 1 until rows * columns) {
            startingPosition.add(true)
        }
        return startingPosition
    }

    private fun chomp(index: Int) {
        var squares: List<Boolean> = (viewChocolate.adapter as ChocolateAdapter).getSquares()
        if (squares[index]) {
            squares = search.getChompedSquares(index, squares)
            viewChocolate.adapter = ChocolateAdapter(this, squares)
            isOpponentsTurn = !isOpponentsTurn

            if (!squares.contains(true)) {
                val message = if (isOpponentsTurn) getString(R.string.message_win)
                else getString(R.string.message_loss)

                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.game_over))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        restart()
                    }
                    .show()
            }
            else if (isOpponentsTurn) {
                menuItemRestart.isEnabled = false
                progressBar.visibility = View.VISIBLE
                txtTurn.visibility = View.INVISIBLE

                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    val bestSquare = getBestSquare()
                    handler.postDelayed({
                        chomp(bestSquare)
                        menuItemRestart.isEnabled = true
                        progressBar.visibility = View.INVISIBLE
                        txtTurn.visibility = View.VISIBLE
                    }, 1000)
                }
            }
        }
    }

    private fun getBestSquare(): Int {
        val squares = (viewChocolate.adapter as ChocolateAdapter).getSquares()
        return if (search.isWinning(squares)) {
            columns + 1
        } else {
            val childSquares = search.getChildSquares(squares)
            val scores = IntArray(rows * columns) { -2 }
            for ((key, value) in childSquares) {
                scores[key] = search.minimax(value, 8, -1, 1, false)
            }
            val maxScore = scores.maxOrNull()
            val bestSquares = mutableListOf<Int>()
            for (i in scores.indices) {
                if (scores[i] == maxScore) {
                    bestSquares.add(i)
                }
            }
            bestSquares.random()
        }
    }

    private fun restart() {
        isOpponentsTurn = false
        menuItemRestart.isEnabled = false
        viewChocolate.adapter = ChocolateAdapter(this, getStartingPosition())
    }

    private fun showHelpDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.help_title)
            .setMessage(R.string.help_message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}