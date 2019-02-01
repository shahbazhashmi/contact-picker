package matrixsystems.contactpicker

/**
 * Created by Shahbaz Hashmi on 15/11/18.
 */
internal interface ContactFetchListener {
    fun onContactsFetched(search : String, page : Int,contactList : MutableList<Contact>)
    fun onError(message : String)
}