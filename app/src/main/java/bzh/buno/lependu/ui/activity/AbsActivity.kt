package bzh.buno.lependu.ui.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Definition of the AbsActivity object.
 */
open class AbsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}