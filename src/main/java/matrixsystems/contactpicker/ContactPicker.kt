package matrixsystems.contactpicker

import android.Manifest.permission.READ_CONTACTS
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

/**
 * Created by Shahbaz Hashmi on 25/01/19.
 */
class ContactPicker private constructor(){

    private val MAX_SELECTION = 20

    companion object {

        val PICKER_REQUEST = 100

        val CONFIG_OBJCET = "config_object"
        val CONTACTS_LIST = "contacts_list"

        private var instance : ContactPicker? = null

        fun openPicker(activity: Activity, pickerConfig: PickerConfig) {
            if(instance == null){
                instance = ContactPicker()
            }
            instance!!.openContactPicker(activity, pickerConfig)
        }
    }



    @Throws(Exception::class)
    fun openContactPicker(activity : Activity, pickerConfig: PickerConfig)
    {
        if (ContextCompat.checkSelfPermission(activity, READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            throw SecurityException("contact permission required")
        }
        if(pickerConfig.selectLimit > MAX_SELECTION){
            throw IllegalStateException("you can not select more than $MAX_SELECTION contacts")
        }
        if(pickerConfig.selectLimit < 1){
            throw IllegalStateException("please enter valid select limit")
        }
        var intent = Intent(activity, ContactPickerActivity::class.java)
        intent.putExtra(CONFIG_OBJCET, pickerConfig)
        activity.startActivityForResult(intent, PICKER_REQUEST)
    }


}