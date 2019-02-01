package matrixsystems.contactpicker

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.support.annotation.ColorInt
import android.widget.ProgressBar

/**
 * Created by Shahbaz Hashmi on 30/01/19.
 */
internal object Utils {

    fun setTint(progressBar: ProgressBar, @ColorInt color: Int, skipIndeterminate: Boolean) {
        try {
            val sl = ColorStateList.valueOf(color)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressBar.progressTintList = sl
                progressBar.secondaryProgressTintList = sl
                if (!skipIndeterminate)
                    progressBar.indeterminateTintList = sl
            } else {
                var mode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    mode = PorterDuff.Mode.MULTIPLY
                }
                if (!skipIndeterminate && progressBar.indeterminateDrawable != null)
                    progressBar.indeterminateDrawable.setColorFilter(color, mode)
                if (progressBar.progressDrawable != null)
                    progressBar.progressDrawable.setColorFilter(color, mode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
