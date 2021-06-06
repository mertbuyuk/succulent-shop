package school.cactus.succulentshop.infra.snackbar

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.snackbar.Snackbar
import school.cactus.succulentshop.resolveAsString

class SnackbarObserver : LifecycleObserver {
    private var snackbar: Snackbar? = null

    fun observe(
        snackbarState: LiveData<SnackbarState>,
        view: View,
        lifecycleOwner: LifecycleOwner
    ) =
        snackbarState.observe(lifecycleOwner) { state ->
            if (state != null) {
                val message = state.errorRes?.resolveAsString(view.context) ?: state.error
                snackbar = Snackbar.make(view, message!!, state.length)
                if (state.action != null) {
                    snackbar!!.setAction(state.action.text) {
                        state.action.action()
                    }
                }
                snackbar!!.show()
            } else {
                snackbar?.dismiss()
            }
        }.also {
            lifecycleOwner.lifecycle.addObserver(this)
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        snackbar?.dismiss()
    }
}