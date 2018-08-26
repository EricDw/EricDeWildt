package com.publicmethod.archer.algebras

import com.publicmethod.archer.Command

sealed class TestCommand : Command {
    object RightCommand : TestCommand()
    object LeftCommand : TestCommand()
    object WorkCommand : TestCommand()
}