package matrixsystems.contactpicker

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Shahbaz Hashmi on 12/11/18.
 */

data class Contact(val contactId : Long, val phoneId : Long) : Parcelable{
    var name : String? = "";
    var phone : String? = "";
    var thumbnail : String? = "";

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong()
    ) {
        name = parcel.readString()
        phone = parcel.readString()
        thumbnail = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(contactId)
        parcel.writeLong(phoneId)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(thumbnail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}