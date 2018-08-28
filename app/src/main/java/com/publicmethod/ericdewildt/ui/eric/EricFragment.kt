package com.publicmethod.ericdewildt.ui.eric

import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import arrow.core.Option
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.publicmethod.ericdewildt.R
import com.publicmethod.ericdewildt.data.Eric
import com.publicmethod.ericdewildt.extensions.getViewModel
import com.publicmethod.ericdewildt.scopes.GetEricScope
import com.publicmethod.ericdewildt.scopes.getEricScope
import com.publicmethod.ericdewildt.ui.eric.archer.EricViewModel
import com.publicmethod.ericdewildt.ui.eric.archer.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.archer.states.EricState
import kotlinx.android.synthetic.main.eric_activity.*
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

    private val ericScope: Option<GetEricScope> by lazy {
        Option.fromNullable(context).flatMap { ctx ->
            getEricScope(ctx)
        }
    }

    private val viewModel: EricViewModel by lazy {
        this.getViewModel<EricViewModel>()
    }

    private lateinit var snackBar: Snackbar
    private lateinit var fabButton: FloatingActionButton
    private lateinit var navigationBar: BottomAppBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.eric_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.apply {

            fabButton = fab

            navigationBar = bottom_appbar

            snackBar = Snackbar.make(
                activity_coordinator,
                "Emailing Eric StateData",
                Snackbar.LENGTH_INDEFINITE
            )
            val listener = View.OnScrollChangeListener { _, x, y, _, _ ->
                when (y < 0) {
                    true -> {
                        fabButton.show()
                    }
                    else -> {
                        fabButton.hide()
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                scrollView.setOnScrollChangeListener(listener)
//                navigationBar.setOnSystemUiVisibilityChangeListener {
//                    when (it == INVISIBLE) {
//                        true -> fabButton.hide()
//                        false -> fabButton.show()
//                    }
//                }
            }

            fabButton.setOnClickListener {
                commandActor.offer(EricCommand.EmailEricCommand)
            }

            with(viewModel) {
                issueInitializeCommand()
                state.value?.run { render(this) }
                state.observe(this@EricFragment, Observer {
                    render(it)
                })

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.eric_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun render(state: EricState) {
        with(state) {

            if (isLoading) renderLoadingState()

            when (navBarEnabled) {
                true -> navigationBar.navigationIcon =
                        context?.getDrawable(R.drawable.ic_menu_white_24dp)
                false -> navigationBar.navigationIcon =
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
//        viewAdapter.updateItems(eric.skills)
        context?.apply {
            fabButton.setImageDrawable(getDrawable(R.drawable.ic_email_accent_24dp))
            fabButton.setOnClickListener {
                commandActor.offer(EricCommand.EmailEricCommand)
            }
        }
    }

    private fun renderShowErrorState() {
        hideProfileViews()
        showStatusViews()
        progressBar.visibility = GONE
        status_message.text = getString(R.string.error_message_eric_not_found)
        context?.apply {
            fabButton.setImageDrawable(getDrawable(R.drawable.ic_refresh_accent_24dp))
            fabButton.setOnClickListener {
                issueInitializeCommand()
            }
        }
    }

    private fun showProfileViews() {
        imageView_profile.visibility = VISIBLE
        textView_fullName.visibility = VISIBLE
        textView_description_title.visibility = VISIBLE
        textView_description.visibility = VISIBLE
//        recyclerView.visibility = VISIBLE
    }

    private fun hideProfileViews() {
        imageView_profile.visibility = GONE
        textView_fullName.visibility = GONE
        textView_description_title.visibility = GONE
        textView_description.visibility = GONE
//        recyclerView.visibility = GONE
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
