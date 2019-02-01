package matrixsystems.contactpicker

/**
 * Created by Shahbaz Hashmi on 13/11/18.
 */

import android.content.ContentResolver
import android.content.Context
import android.os.AsyncTask
import android.provider.ContactsContract
import android.text.TextUtils
import java.lang.Exception


internal class ContactHelper(val context: Context) {

    private var mContactFetchListener : ContactFetchListener
    private var mResolver: ContentResolver



    init {
        mResolver = context.contentResolver
        mContactFetchListener = context as ContactFetchListener
    }

        inner class fetchContacts(val searchQuery: String, val page : Int) : AsyncTask<Void, Void, Void>() {

            var offset : Int
            var contactList : ArrayList<Contact>
            var exception : Exception? = null

            init {
                offset = page * ContactUtils.PAGE_SIZE
                contactList = ArrayList()
            }

            override fun doInBackground(vararg params: Void?): Void? {

                try {

                    val cursor = ContactUtils.getContactCursor(
                        mResolver,
                        if (TextUtils.isEmpty(searchQuery)) "" else searchQuery,
                        offset
                    )
                    val idColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    val displayNamePrimaryColumnIndex = cursor.getColumnIndex(ContactUtils.DISPLAY_NAME);
                    val thumbnailColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);

                    while (cursor.moveToNext()) {

                        val id = cursor.getLong(idColumnIndex);

                        ///----  phone number ----///

                        val phoneCursor =
                            ContactUtils.getPhoneCursor(mResolver, id);
                        val phoneNumberColumnIndex =
                            phoneCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        val idPhoneColumnIndex =
                            phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);

                        while (phoneCursor.moveToNext()) {

                            val phoneId = phoneCursor.getLong(idPhoneColumnIndex);
                            var contact = Contact(id, phoneId);
                            /**
                             * get only valid mobile
                             */
                            if (ColumnMapper.mapPhoneNumber(
                                    phoneCursor,
                                    contact,
                                    phoneNumberColumnIndex
                                )
                            ) {
                                ColumnMapper.mapName(
                                    cursor,
                                    contact,
                                    displayNamePrimaryColumnIndex
                                );
                                ColumnMapper.mapThumbnail(
                                    cursor,
                                    contact,
                                    thumbnailColumnIndex
                                );
                                contactList.add(contact)
                            }
                        }
                        phoneCursor.close();

                        ///----               ----///
                    }

                    cursor.close();

                }
                catch (e : Exception){
                    e.printStackTrace()
                    exception = e
                }
                catch (e : InterruptedException){
                    e.printStackTrace()
                    exception = e
                }

                return null
            }

            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)

                if(contactList.size==0 && exception!=null){
                    mContactFetchListener.onError(if(!TextUtils.isEmpty(exception!!.localizedMessage)) exception!!.localizedMessage else "something went wrong")
                }else {
                    mContactFetchListener.onContactsFetched(searchQuery, page, contactList)
                }
            }
        }



}