package com.publicmethod.ericdewildt.ui.eric

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arrow.core.Option
import com.publicmethod.ericdewildt.R
import com.publicmethod.ericdewildt.extensions.getViewModel
import com.publicmethod.ericdewildt.scopes.Scopes.getEricScope
import com.publicmethod.ericdewildt.ui.eric.mvc.EricViewModel
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricState
import kotlinx.android.synthetic.main.eric_fragment.*

class EricFragment : Fragment() {

    companion object {
        fun newInstance() = EricFragment()
    }

    private lateinit var viewModel: EricViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.eric_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel()
        viewModel.state.observe(this, Observer {
            it?.run {
                render(this)
            }
        })

        with(viewModel.state.value) {
            this?.run { render(this) }
        }

        fab.setOnClickListener {
            issueInitializeCommand()
        }

    }

    private fun issueInitializeCommand() {
        Log.d(this::class.java.canonicalName, "Issuing Initialization state.")
        Option.fromNullable(context).map { ctx ->
            getEricScope(ctx).map { getEricScope ->
                viewModel.issueCommand(EricCommand.InitializeCommand(getEricScope))
            }
        }
    }

    private fun render(state: EricState) =
            when (state) {
                is EricState.ShowErrorState -> renderShowErrorState(state)
                is EricState.ShowEricState -> renderShowEricState(state)
            }

    private fun renderShowEricState(state: EricState.ShowEricState) {
        state.ericModel.eric.fold({}, {
            message.text = it.toString()
        })
    }

    private fun renderShowErrorState(state: EricState.ShowErrorState) {
        state.ericModel.error.fold({}, {
            message.text = "Eric Was Not Found"
        })
    }

}

