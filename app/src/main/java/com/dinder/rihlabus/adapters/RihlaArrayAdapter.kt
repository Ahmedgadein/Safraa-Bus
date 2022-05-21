package com.dinder.rihlabus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.dinder.rihlabus.common.DropDownItem
import com.dinder.rihlabus.databinding.DropdownItemBinding
import java.util.*

class RihlaArrayAdapter<T : DropDownItem>(
    context: Context,
    resource: Int,
    private val objects: MutableList<T>
) :
    ArrayAdapter<T>(context, resource, objects) {

    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int = objects.size

    override fun getItem(position: Int): T {
        return objects[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            if (convertView == null) DropdownItemBinding.inflate(
                inflater,
                parent,
                false
            ) else DropdownItemBinding.bind(convertView)

        val item = getItem(position)

        binding.english.text = item.name
        binding.arabic.text = item.arabicName

        return binding.root
    }

    override fun getFilter(): Filter {
        return nameFilter
    }

    private val nameFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            TODO("Not yet implemented")
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            TODO("Not yet implemented")
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            val item = resultValue as T
            val isArabic = Locale.getDefault().language.equals(Locale("ar").language)
            return if (isArabic) item.arabicName else item.name
        }
    }
}
