package com.publicmethod.ericdewildt.ui.eric

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import arrow.core.Option
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.publicmethod.archer.whenClicking
import com.publicmethod.ericdewildt.R
import com.publicmethod.ericdewildt.data.Eric
import com.publicmethod.ericdewildt.extensions.getViewModel
import com.publicmethod.ericdewildt.scopes.Scopes
import com.publicmethod.ericdewildt.scopes.Scopes.getEricScope
import com.publicmethod.ericdewildt.ui.eric.bow.EricViewModel
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricState
import kotlinx.android.synthetic.main.eric_fragment.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch

class EricFragment : Fragment() {

    companion object {
        fun newInstance() = EricFragment()
    }

    private val commandActor = actor<EricCommand>(CommonPool) {
        for (command in channel) {
            viewModel.send(command)
        }
    }

    private val ericScope: Option<Scopes.GetEricScope> by lazy {
        Option.fromNullable(context).flatMap { ctx ->
            getEricScope(ctx)
        }
    }

    private val viewModel: EricViewModel by lazy {
        this.getViewModel<EricViewModel>()
    }

    private val snackBar: Snackbar by lazy {
        Snackbar.make(coordinator, "Emailing Eric StateData", Snackbar.LENGTH_INDEFINITE)
    }

    private val fabButton: FloatingActionButton by lazy { fab }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.eric_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(viewModel) {
            issueInitializeCommand()
            state.value?.run { render(this) }
            state.observe(this@EricFragment, Observer {
                render(it)
            })
        }

        whenClicking(fabButton) {
            EricCommand.EmailEricCommand
        } then {
            commandActor.offer(it)
        }

    }

    private fun render(state: EricState) {
        with(state) {

            if (isLoading) renderLoadingState()

            when (navBarEnabled) {
                true -> bottom_appbar.navigationIcon =
                        context?.getDrawable(R.drawable.ic_menu_white_24dp)
                false -> bottom_appbar.navigationIcon =
                        null
            }

            error.fold({}, {
                renderShowErrorState()
            })

            eric.fold({}, {
                renderShowEricState(it)
            })

            when (showSnackBar) {
                true -> renderShowEmailState()
                false -> renderDismissSnackBarState()
            }
        }
    }

    private fun issueInitializeCommand() {
        ericScope.map { getEricScope ->
            launch {
                commandActor.send(EricCommand.InitializeCommand(getEricScope))
            }
        }
    }

    private fun renderDismissSnackBarState() {
        snackBar.dismiss()
    }

    private fun renderShowEmailState() {
        context?.run {
            with(snackBar) {
                setAction("DISMISS") {
                    launch {
                        commandActor.send((EricCommand.DismissSnackBarCommand))
                    }
                }
                show()
            }
        }
    }

    private fun renderLoadingState() {
        hideProfileViews()
        showStatusViews()
    }

    private fun renderShowEricState(eric: Eric) {
        hideStatusViews()
        showProfileViews()
        textView_fullName.text = eric.fullName
        textView_description.text = eric.description
    }

    private fun renderShowErrorState() {
        hideProfileViews()
        showStatusViews()
        status_message.text = getString(R.string.error_message_eric_not_found)
    }

    private fun showProfileViews() {
        imageView_profile.visibility = VISIBLE
        textView_fullName.visibility = VISIBLE
        textView_description_title.visibility = VISIBLE
        textView_description.visibility = VISIBLE
        recyclerView.visibility = VISIBLE
    }

    private fun hideProfileViews() {
        imageView_profile.visibility = GONE
        textView_fullName.visibility = GONE
        textView_description_title.visibility = GONE
        textView_description.visibility = GONE
        recyclerView.visibility = VISIBLE
    }

    private fun showStatusViews() {
        status_message.visibility = VISIBLE
        progressBar.visibility = VISIBLE
    }

    private fun hideStatusViews() {
        status_message.visibility = GONE
        progressBar.visibility = GONE
    }

}
