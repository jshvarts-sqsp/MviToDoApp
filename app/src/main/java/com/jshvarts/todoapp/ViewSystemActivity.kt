package com.jshvarts.todoapp

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.jshvarts.todoapp.databinding.ActivityViewSystemBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ViewSystemActivity : AppCompatActivity() {
  private lateinit var binding: ActivityViewSystemBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityViewSystemBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setSupportActionBar(binding.toolbar)

    binding.root.viewTreeObserver.addOnGlobalLayoutListener {
      val r = Rect()
      window.decorView.getWindowVisibleDisplayFrame(r)

      val height = window.decorView.height
      (height - r.bottom > height * 0.1).let {
        binding.rteBar.isVisible = it
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.activity_view_system_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_add_block -> {
        EditText(this).apply {
          LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
          ).let {
            it.setMargins(0, 32, 0, 32)
            layoutParams = it
          }
        }.let { editText ->
          editText.hint = resources.getString(R.string.write_here)
          binding.dynamicBlocks.addView(editText, binding.dynamicBlocks.childCount).also {
            editText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, 0)
          }
        }
        true
      }
      else -> false
    }
  }
}
