package bzh.buno.lependu.ui.activity.impl

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import bzh.buno.lependu.R
import bzh.buno.lependu.adapter.KeyboardAdapter
import bzh.buno.lependu.data.Keyboard
import bzh.buno.lependu.data.Word
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.linearlistview.LinearListView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.util.*

/*
Pack d'icônes par l'artiste Iconexpo : http://www.iconarchive.com/artist/iconexpo.html
Icone about, Help: Jack Cai, http://findicons.com/icon/175921/info_black (Licence: https://creativecommons.org/licenses/by-nd/3.0/)
*/
class MainActivity : AbsActivity() {

    val HANGMAN_DRAWABLE = arrayOf(
            R.drawable.img0,
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
            R.drawable.img5,
            R.drawable.img6,
            R.drawable.img7,
            R.drawable.img8,
            R.drawable.img9,
            R.drawable.img10
    )
    private var MAX_TRY = 10
    private var CLUE_INDEX = 5

    private var items: List<Word>? = null
    private var usedLetters = String()
    private var currentWord = ""
    private var currentClue = ""
    private var nbError = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
        refreshUI()
    }

    /**
     * Setup UI
     * Used to initialize the screen
     */
    private fun setupUI() {

        //import json data
        val inputStream = resources.openRawResource(R.raw.dico)
        val inputAsString = inputStream.bufferedReader().use { it.readText() }
        val gson = Gson()
        items = gson.fromJson<List<Word>>(inputAsString)

        //setup UI
        keyboard_top.adapter = KeyboardAdapter(this, Keyboard.TOP_LINE_DRAWABLE.asList())
        keyboard_top.onItemClickListener = LinearListView.OnItemClickListener { parent, view, position, id -> addLetter(Keyboard.TOP_LINE[position]) }
        keyboard_middle.adapter = KeyboardAdapter(this, Keyboard.MIDDLE_LINE_DRAWABLE.asList())
        keyboard_middle.onItemClickListener = LinearListView.OnItemClickListener { parent, view, position, id -> addLetter(Keyboard.MIDDLE_LINE[position]) }
        keyboard_bottom.adapter = KeyboardAdapter(this, Keyboard.BOTTOM_LINE_DRAWABLE.asList())
        keyboard_bottom.onItemClickListener = LinearListView.OnItemClickListener { parent, view, position, id -> addLetter(Keyboard.BOTTOM_LINE[position]) }

        help.setOnClickListener { toast(currentClue) }

        // set strikethrough on wrong letters
        wrong_letters.paintFlags += Paint.STRIKE_THRU_TEXT_FLAG
    }

    /**
     * Refresh UI
     * Used to reset all data displaued in screen
     */
    private fun refreshUI() {
        // reset word to find
        val index = Random().nextInt(items!!.size)
        if (items?.get(index) is Word) {
            val word = items?.get(index)
            currentWord = word!!.value
            currentClue = word.clue
        }
        usedLetters = ""
        nbError = 0

        // clean screen
        user_input.text = ""
        repeat(currentWord.length) { user_input.append("-") }
        wrong_letters.text = ""

        hangman.setImageResource(HANGMAN_DRAWABLE[nbError])
    }

    /**
     * Add letter
     * It contains the app intelligency
     */
    private fun addLetter(c: Char) {
        // check if letter hasn't already been used
        if (usedLetters.indexOf(c) == -1) {
            usedLetters += c
            var found: Boolean = false

            // does "word to find" contains the given letter?
            for (i in 0..currentWord.length - 1) {
                if (c.equals(currentWord[i], true)) {
                    val text = user_input.text
                    val builder = StringBuilder()
                    builder.append(text.slice(0..i - 1))
                    builder.append(c)
                    builder.append(text.slice(i + 1..text.length - 1))
                    user_input.text = builder.toString()
                    found = true
                }
            }

            // end of the game?
            if (found) {
                if (currentWord.compareTo(user_input.text.toString()) == 0) {
                    val message: String
                    if (nbError == 0) {
                        message = getString(R.string.win_message_no_error)
                    } else {
                        message = resources.getQuantityString(R.plurals.win_message, nbError, nbError)
                    }
                    alert(title = getString(R.string.win_title), message = message) {
                        positiveButton(android.R.string.yes) { refreshUI() }
                        negativeButton(android.R.string.no) { finish() }
                    }.show()
                }
            } else {
                // one more try?
                nbError++
                hangman.setImageResource(HANGMAN_DRAWABLE[nbError])
                wrong_letters.append(c.toString())
                if (nbError == MAX_TRY) {
                    alert(title = getString(R.string.game_over_title), message = getString(R.string.game_over_message, currentWord)) {
                        positiveButton(android.R.string.yes) { refreshUI() }
                        negativeButton(android.R.string.no) { finish() }
                    }.show()
                } else {
                    // need clue?
                    if (nbError >= CLUE_INDEX) {
                        help.visibility = View.VISIBLE
                    } else {
                        help.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }
}
