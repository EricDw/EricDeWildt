package com.publicmethod.archer.algebras

import com.publicmethod.archer.Archer

sealed class TestCommand : Archer.Command{
    object RightCommand: TestCommand()
    object LeftCommand: TestCommand()
}