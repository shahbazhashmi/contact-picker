package matrixsystems.contactpicker

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat

/**
 * Created by Shahbaz Hashmi on 25/01/19.
 */

class PickerConfig (@ColorInt val loaderColor : Int, val selectLimit : Int, val toolbarTitle : String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    private constructor(builder: Builder) : this(builder.loaderColor, builder.selectLimit, builder.toolbarTitle)


    class Builder(val activity: Activity){
        @ColorInt var loaderColor : Int = ContextCompat.getColor(activity,
            R.color.colorAccent
        )
        var selectLimit : Int = 5
        var toolbarTitle : String = activity.getString(R.string.app_name)

        fun build() = PickerConfig(this)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(loaderColor)
        parcel.writeInt(selectLimit)
        parcel.writeString(toolbarTitle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PickerConfig> {
        override fun createFromParcel(parcel: Parcel): PickerConfig {
            return PickerConfig(parcel)
        }

        override fun newArray(size: Int): Array<PickerConfig?> {
            return arrayOfNulls(size)
        }

        inline fun build(activity: Activity, block: Builder.() -> Unit) = Builder(
            activity
        ).apply(block).build()
    }


}

