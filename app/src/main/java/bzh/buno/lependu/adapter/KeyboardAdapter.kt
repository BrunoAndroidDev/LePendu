package bzh.buno.lependu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import bzh.buno.lependu.R

/**
 * Definition of the KeyboardAdapter object.
 */
class KeyboardAdapter(context: Context, val items: List<Int>) : BaseAdapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.keyboard_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.image.setImageResource(items[position])
        return view
    }

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = items.size

    class ViewHolder(view: View?) {
        val image: ImageView = view?.findViewById(R.id.keyboard_item) as ImageView
    }

}
