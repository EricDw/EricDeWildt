package com.publicmethod.ericdewildt.ui.eric

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import arrow.core.Option
import com.publicmethod.ericdewildt.R
import com.publicmethod.ericdewildt.extensions.getViewModel
import com.publicmethod.ericdewildt.scopes.Scopes
import com.publicmethod.ericdewildt.scopes.Scopes.getEricScope
import com.publicmethod.ericdewildt.ui.eric.mvk.EricBow
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricKommand
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricState
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricState.*
import kotlinx.android.synthetic.main.eric_fragment.*

class EricFragment : Fragment() {

    companion object {
        fun newInstance() = EricFragment()
    }


    private val ericScope: Option<Scopes.GetEricScope> by lazy {
        Option.fromNullable(context).flatMap { ctx ->
            getEricScope(ctx)
        }
    }

    private lateinit var viewModel: EricBow

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

        fab.setOnClickListener {
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

    private fun render(state: EricState) =
            when (state) {
                is ShowErrorState -> renderShowErrorState(state)
                is ShowEricState -> renderShowEricState(state)
                is ShowLoadingState -> renderShowLoadingState(state)
                is EricState.ShowEmailEricState -> renderShowEmailState(state)
            }

    private fun renderShowEmailState(state: ShowEmailEricState) {
        context?.run {
            Toast.makeText(this, "Emailing Eric State", Toast.LENGTH_LONG).show()
        }
    }

    private fun renderShowLoadingState(state: ShowLoadingState) {
        hideProfileViews()
        showErrorViews()
    }

    private fun renderShowEricState(state: ShowEricState) {
        state.ericModel.eric.fold({}, {
            hideErrorViews()
            showProfileViews()
            textView_fullName.text = it.fullName
            textView_description.text = it.description
        })
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

    private fun showErrorViews() {
        status_message.visibility = VISIBLE
        progressBar.visibility = VISIBLE
    }

    private fun hideErrorViews() {
        status_message.visibility = GONE
        progressBar.visibility = GONE
    }

    private fun renderShowErrorState(state: ShowErrorState) {
        state.ericModel.error.fold({}, {
            status_message.text = getString(R.string.error_message_eric_not_found)
        })
    }

}

