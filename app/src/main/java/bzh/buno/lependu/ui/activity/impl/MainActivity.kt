package bzh.buno.lependu.ui.activity.impl

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import bzh.buno.lependu.R
import bzh.buno.lependu.adapter.KeyboardAdapter
import bzh.buno.lependu.data.Keyboard
import bzh.buno.lependu.data.Word
import bzh.buno.lependu.ui.activity.AbsActivity
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

    companion object {
        private val KEY_USER_INPUT = "key_user_input"
        private val KEY_CURRENT_WORD = "key_current_word"
        private val KEY_USED_LETTERS = "key_used_letters"
        private val KEY_CURRENT_CLUE = "key_current_clue"
        private val KEY_NB_ERRORS = "key_nb_errors"
        private val KEY_ALERT_TITLE = "key_alert_title"
        private val KEY_ALERT_MESSAGE = "key_alert_message"

        private var MAX_TRY = 10
        private var CLUE_INDEX = 5
    }

    private val HANGMAN_DRAWABLE = arrayOf(
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

    private var items: List<Word>? = null
    private var usedLetters = String()
    private var currentWord = ""
    private var currentClue = ""
    private var nbError = 0
    private var alertTitle = ""
    private var alertMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
        if (savedInstanceState != null) {
            currentWord = savedInstanceState.getString(KEY_CURRENT_WORD)!!
            currentClue = savedInstanceState.getString(KEY_CURRENT_CLUE)!!
            usedLetters = savedInstanceState.getString(KEY_USED_LETTERS)!!
            nbError = savedInstanceState.getInt(KEY_NB_ERRORS)

            user_input.text = savedInstanceState.getString(KEY_USER_INPUT)
            wrong_letters.text = usedLetters
            hangman.setImageResource(HANGMAN_DRAWABLE[nbError])
            showClueIfNeeded()
            alertTitle = savedInstanceState.getString(KEY_ALERT_TITLE)!!
            alertMessage = savedInstanceState.getString(KEY_ALERT_MESSAGE)!!
            if (alertTitle.isNotEmpty() && alertMessage.isNotEmpty()) {
                displayAlert(alertTitle, alertMessage)
            }
        } else {
            refreshUI()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_USER_INPUT, user_input.text.toString())
        outState.putString(KEY_CURRENT_WORD, currentWord)
        outState.putString(KEY_USED_LETTERS, usedLetters)
        outState.putString(KEY_CURRENT_CLUE, currentClue)
        outState.putInt(KEY_NB_ERRORS, nbError)
        outState.putString(KEY_ALERT_TITLE, alertTitle)
        outState.putString(KEY_ALERT_MESSAGE, alertMessage)
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
        items = gson.fromJson(inputAsString)

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
     * Used to reset all data displayed in screen
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

        alertTitle = ""
        alertMessage = ""

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
            var found = false

            // does "word to find" contains the given letter?
            for (i in 0 until currentWord.length) {
                if (c.equals(currentWord[i], true)) {
                    val text = user_input.text
                    val builder = StringBuilder()
                    builder.append(text.slice(0 until i))
                    builder.append(c)
                    builder.append(text.slice(i + 1 until text.length))
                    user_input.text = builder.toString()
                    found = true
                }
            }

            // end of the game?
            if (found) {
                if (currentWord.compareTo(user_input.text.toString()) == 0) {
                    val message: String = if (nbError == 0) {
                        getString(R.string.win_message_no_error)
                    } else {
                        resources.getQuantityString(R.plurals.win_message, nbError, nbError)
                    }
                    displayAlert(getString(R.string.win_title), message)
                }
            } else {
                // one more try?
                nbError++
                wrong_letters.append(c.toString())
                hangman.setImageResource(HANGMAN_DRAWABLE[nbError])
                if (nbError == MAX_TRY) {
                    displayAlert(getString(R.string.game_over_title), getString(R.string.game_over_message, currentWord))
                } else {
                    // need clue?
                    showClueIfNeeded()
                }
            }
        }
    }

    private fun displayAlert(title: String, message: String) {
        alertTitle = title
        alertMessage = message
        alert(title = title, message = message) {
            positiveButton(android.R.string.yes) { refreshUI() }
            negativeButton(android.R.string.no) { finish() }
            isCancelable = false
        }.show()
    }

    private fun showClueIfNeeded() {
        if (nbError >= CLUE_INDEX) {
            help.visibility = View.VISIBLE
        } else {
            help.visibility = View.INVISIBLE
        }
    }
}
