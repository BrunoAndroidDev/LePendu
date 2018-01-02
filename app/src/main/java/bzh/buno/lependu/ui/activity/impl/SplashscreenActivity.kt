package bzh.buno.lependu.ui.activity.impl

import android.os.Bundle
import bzh.buno.lependu.ui.activity.AbsActivity
import org.jetbrains.anko.startActivity

class SplashscreenActivity : AbsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity<MainActivity>()
        finish()
    }
}
