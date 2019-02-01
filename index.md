<h1>Introduction</h1>

<b>Contact Picker</b> is a simple and light weight contact picker library for Android. In order to maintain its simplicity and ease of use runtime permission is not handled in the library.

<br>
<br>

<h1>Usage</h1>

1. Create picker config object

```
val pickerConfig = PickerConfig.build(this@MainActivity, ({
                toolbarTitle = "Contact Picker"
                loaderColor = ContextCompat.getColor(activity, R.color.colorPrimaryDark)
                selectLimit = 5
            }))
```
<br>

2. Open contact picker

```
ContactPicker.openPicker(this@MainActivity, pickerConfig)
```
<br>

3. Listen selected contacts in onActivityResult

```
public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ContactPicker.PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val list = data!!.getParcelableArrayListExtra<Contact>(ContactPicker.CONTACTS_LIST)
                Log.d("result", "got data")
            }
        }
    }
```
<br>
<br>
<b>NOTE : -</b>

1. Grant contact permission before open conatct picker in Android 6 or above
2. Maximum selection limit of contacts is 20
