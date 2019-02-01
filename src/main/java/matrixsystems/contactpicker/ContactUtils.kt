package matrixsystems.contactpicker

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import android.text.TextUtils

/**
 * Created by Shahbaz Hashmi on 15/11/18.
 */
internal class ContactUtils {

    companion object {

        val MINIMUM_NUMBER_LENGTH = 5

        val PAGE_SIZE = 50

        val DISPLAY_NAME = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        else
            ContactsContract.Contacts.DISPLAY_NAME

        private val PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        private val HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER

        private val PROJECTION = arrayOf(
            ContactsContract.Contacts._ID,
            DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            HAS_PHONE_NUMBER
        )

        private val NUMBER_PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        fun getContactCursor(resolver : ContentResolver, searchParam: String, offset: Int): Cursor {

            var selection =
                ContactsContract.Data.DISPLAY_NAME + " IS NOT NULL AND " + ContactsContract.Data.DISPLAY_NAME + " != \"\" AND " +
                        HAS_PHONE_NUMBER + " IS NOT NULL AND " + HAS_PHONE_NUMBER + " != 0"

            if (!TextUtils.isEmpty(searchParam)) {
                selection += " AND " + ContactsContract.Data.DISPLAY_NAME + " LIKE '%" + searchParam + "%' ESCAPE '_'"
            }

            return resolver!!.query(
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                selection, null,
                ContactsContract.Data.DISPLAY_NAME + " COLLATE NOCASE ASC LIMIT " + PAGE_SIZE.toString() + " OFFSET " + offset.toString()
            )
        }


        fun getPhoneCursor(resolver : ContentResolver, id: Long): Cursor? {
            return resolver!!.query(
                PHONE_CONTENT_URI,
                NUMBER_PROJECTION,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id.toString() + " AND LENGTH(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ") >= " + MINIMUM_NUMBER_LENGTH.toString(),
                null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " COLLATE NOCASE ASC"
            )
        }

    }

}