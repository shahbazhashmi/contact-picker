## contact-picker

### Introduction

**Contact Picker** is a simple and light weight contact picker library for Android. In order to maintain its simplicity and ease of use runtime permission is not handled in the library.

![](contact_picker_gif.gif)

### Usage

1. Create picker config object

```markdown
val pickerConfig = PickerConfig.build(this@MainActivity, ({
                toolbarTitle = "Contact Picker"
                loaderColor = ContextCompat.getColor(activity, R.color.colorPrimaryDark)
                selectLimit = 5
            }))
```

2. Open contact picker

```
ContactPicker.openPicker(this@MainActivity, pickerConfig)
```

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

### Note

1. Grant contact permission before open conatct picker in Android 6 or above
2. Maximum selection limit of contacts is 20
