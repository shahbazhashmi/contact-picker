package matrixsystems.contactpicker

/**
 * Created by Shahbaz Hashmi on 13/11/18.
 */

import android.database.Cursor
import android.text.TextUtils


internal object ColumnMapper {

    fun mapName(cursor: Cursor, contact: Contact, columnIndex: Int) {
        val name = cursor.getString(columnIndex)
        if (name != null && !name.isEmpty()) {
            contact.name = name
        }
    }

    fun mapPhoneNumber(cursor: Cursor, contact: Contact, columnIndex: Int): Boolean {
        try {
            var phoneNumber = cursor.getString(columnIndex)

            if(TextUtils.isEmpty(phoneNumber)){
                throw Exception("number not found")
            }

            phoneNumber.replace("\\D+".toRegex(), "")

            /*val numberProto = phoneNumberUtil.parse(phoneNumber, "IN")

            if(!phoneNumberUtil.isValidNumber(numberProto)){
                throw IllegalStateException("invalid number")
            }*/

            contact.phone = phoneNumber
            return true
        }
        catch (e : Exception){
            e.printStackTrace()
            return false
        }
    }


    fun mapThumbnail(cursor: Cursor, contact: Contact, columnIndex: Int) {
        val uri = cursor.getString(columnIndex)
        if (uri != null && !uri.isEmpty()) {
            contact.thumbnail = uri
        }
    }

}