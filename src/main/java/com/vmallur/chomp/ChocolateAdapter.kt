package com.vmallur.chomp

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class ChocolateAdapter constructor(private val context: Context, private var squares: List<Boolean>) : BaseAdapter() {

    override fun getCount(): Int {
        return squares.size
    }

    override fun getItem(p0: Int): Any {
        return squares[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view = p1
        if (view == null) {
            view = View.inflate(context, R.layout.square, null)
        }
        view!!.visibility = if (squares[p0]) View.VISIBLE else View.INVISIBLE
        return view
    }

    fun getSquares(): List<Boolean> {
        return squares
    }
}