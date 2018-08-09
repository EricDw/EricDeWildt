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
import com.publicmethod.archer.Archer.ViewController
import com.publicmethod.ericdewildt.R
import com.publicmethod.ericdewildt.data.Eric
import com.publicmethod.ericdewildt.extensions.getViewModel
import com.publicmethod.ericdewildt.scopes.Scopes
import com.publicmethod.ericdewildt.scopes.Scopes.getEricScope
import com.publicmethod.ericdewildt.ui.eric.mvk.EricBow
import com.publicmethod.ericdewildt.ui.eric.mvk.EricState
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricKommand
import kotlinx.android.synthetic.main.eric_fragment.*

class EricFragment : Fragment(), ViewController<EricState> {

    companion object {
        fun newInstance() = EricFragment()
    }

    private val ericScope: Option<Scopes.GetEricScope> by lazy {
        Option.fromNullable(context).flatMap { ctx ->
            getEricScope(ctx)
        }
    }

    private lateinit var viewModel: EricBow

    private lateinit var fabButton: FloatingActionButton

    private val snackBar: Snackbar by lazy {
        Snackbar.make(coordinator, "Emailing Eric State", Snackbar.LENGTH_INDEFINITE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.eric_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel()
        issueInitializeCommand()
        viewModel.state.observe(this, Observer {
            render(it)
        })

        with(viewModel.state.value) {
            this?.run { render(this) }
        }

        fabButton = fab

        fabButton.setOnClickListener {
            issueEmailEricKommand()
        }

    }

    private fun issueEmailEricKommand() {
        viewModel.issueKommand(EricKommand.EmailEricKommand)
    }

    private fun issueInitializeCommand() {
        ericScope.map { getEricScope ->
            viewModel.issueKommand(EricKommand.InitializeKommand(getEricScope))
        }
    }

    override fun render(state: EricState) {
        with(state) {

            if (isLoading) renderLoadingState()

            error.fold({}, {
                renderShowErrorState()
            })
            eric.fold({}, {
                renderShowEricState(it)
            })

            when(showSnackBar) {
                true -> renderShowEmailState()
                false -> renderDismissSnackBarState()
            }
        }
    }

    private fun renderDismissSnackBarState() {
        snackBar.dismiss()
    }

    private fun renderShowEmailState() {
        context?.run {
            with(snackBar) {
                setAction("DISMISS") { issueDismissSnackBarCommand() }
                show()
            }
        }
    }

    private fun issueDismissSnackBarCommand() {
        viewModel.issueKommand(EricKommand.DismissSnackBarKommand)
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
    }

    private fun hideProfileViews() {
        imageView_profile.visibility = GONE
        textView_fullName.visibility = GONE
        textView_description_title.visibility = GONE
        textView_description.visibility = GONE
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

