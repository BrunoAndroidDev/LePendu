package bzh.buno.lependu.data

import bzh.buno.lependu.R

/**
 * Definition of the Keyboard object.
 * It contains both the graphical design and the internal data, splitted in 3 lines according to AZERTY keyboard
 */
object Keyboard {
    val TOP_LINE: String = "AZERTYUIOP"
    val TOP_LINE_DRAWABLE = arrayOf(
            R.drawable.a,
            R.drawable.z,
            R.drawable.e,
            R.drawable.r,
            R.drawable.t,
            R.drawable.y,
            R.drawable.u,
            R.drawable.i,
            R.drawable.o,
            R.drawable.p
    )

    val MIDDLE_LINE: String = "QSDFGHJKLM"
    val MIDDLE_LINE_DRAWABLE = arrayOf(
            R.drawable.q,
            R.drawable.s,
            R.drawable.d,
            R.drawable.f,
            R.drawable.g,
            R.drawable.h,
            R.drawable.j,
            R.drawable.k,
            R.drawable.l,
            R.drawable.m
    )

    val BOTTOM_LINE: String = "WXCVBN"
    val BOTTOM_LINE_DRAWABLE = arrayOf(
            R.drawable.w,
            R.drawable.x,
            R.drawable.c,
            R.drawable.v,
            R.drawable.b,
            R.drawable.n
    )
}
