package matrixsystems.contactpicker

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import de.hdodenhof.circleimageview.CircleImageView
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.widget.ProgressBar
import android.widget.Toast


/**
 * Created by Shahbaz Hashmi on 13/11/18.
 */
internal class ContactAdapter(val context: Context, val contactSelectionListener: ContactSelectionListener, val contactList: MutableList<Contact>, val pickerConfig: PickerConfig) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var selectedList : MutableList<Contact> = mutableListOf()

    private val VIEWTYPE_ITEM = 1
    private val VIEWTYPE_LOADER = 2
    private var mShowLoader: Boolean = false

    //this method is returning the view for each item matrixsystems the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEWTYPE_ITEM) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_contact, parent, false)
            return ContactViewHolder(v)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_load_more, parent, false)
            return LoaderViewHolder(view)
        }
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ContactViewHolder) {
            holder.bindItems(position)
        }else if(holder is LoaderViewHolder){
            holder.bindItems()
        }
    }

    override fun getItemCount(): Int {
        // If no items are present, there's no need for loader
        return if (contactList == null || contactList.size == 0) {
            0
        } else contactList.size + 1 // +1 for loader
    }

    override fun getItemViewType(position: Int): Int {

        // loader can't be at position 0
        // loader can only be at the last position
        return if (position != 0 && position == itemCount - 1) {
            VIEWTYPE_LOADER
        } else VIEWTYPE_ITEM
    }


    inner class LoaderViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar) as ProgressBar
            Utils.setTint(progressBar, pickerConfig.loaderColor, false)
            if (mShowLoader) {
                progressBar.setVisibility(View.VISIBLE)
            } else {
                progressBar.setVisibility(View.GONE)
            }
        }
    }

    //the class is hodling the list view
    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(position : Int) {
            val contact = contactList.get(position)
            val imageViewDp = itemView.findViewById(R.id.iv_dp) as CircleImageView
            val textViewName = itemView.findViewById(R.id.tv_name) as TextView
            val textViewPhone  = itemView.findViewById(R.id.tv_phone) as TextView
            textViewName.text = contact.name
            textViewPhone.text = contact.phone

            if(!TextUtils.isEmpty(contact.thumbnail)){
                Picasso.get().load(contact.thumbnail).into(imageViewDp)
            }else{
                if(!TextUtils.isEmpty(contact.name)) {

                    val generator = ColorGenerator.MATERIAL // or use DEFAULT

                    val color1 = generator.randomColor

                    val initials = contact.name!!.substring(0, 1).toUpperCase()

                    val drawable = TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.WHITE)
                        .useFont(Typeface.DEFAULT)
                        .fontSize(20) /* size matrixsystems px */
                        .width(60)  // width matrixsystems px
                        .height(60) // height matrixsystems px
                        .endConfig()
                        .buildRound(initials, color1)

                    imageViewDp.setImageDrawable(drawable)
                }
                else{

                }
            }

            itemView.setBackgroundColor(if(isItemSelected(contact)) ContextCompat.getColor(context,
                R.color.selectionGrey
            ) else ContextCompat.getColor(context, R.color.rowBackground))

            itemView.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    val updatedFlag = !isItemSelected(contact)

                    if(updatedFlag){
                        if(selectedList.size >= pickerConfig.selectLimit){
                            Toast.makeText(context, context.getString(R.string.select_restrict, pickerConfig.selectLimit.toString()), Toast.LENGTH_SHORT).show()
                            return
                        }
                        selectedList.add(contact)
                        itemView.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.selectionGrey
                        ))
                    }
                    else{
                        selectedList.remove(contact)
                        itemView.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.rowBackground
                        ))
                    }

                    contactSelectionListener.onSelectionChange(selectedList.size)
                }
            })

        }


    }

    fun showLoading(status: Boolean) {
        mShowLoader = status
    }


    private fun isItemSelected(contact : Contact) : Boolean{
        return selectedList.contains(contact)
    }


    fun clearSelection(){
        selectedList.clear()
        contactSelectionListener.onSelectionChange(selectedList.size)
    }




}