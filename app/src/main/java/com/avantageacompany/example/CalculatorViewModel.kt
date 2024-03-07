package com.avantageacompany.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {
    var state by mutableStateOf(CalculatorState())
        private set

    fun onAction(action: CalculatorActions) {
        when (action) {
            is CalculatorActions.Number -> enterNumber(action.number)
            is CalculatorActions.Decimal -> enterDecimal()
            is CalculatorActions.Clear -> clearState()
            is CalculatorActions.Operation -> enterOperation(action.operation)
            is CalculatorActions.Calculate -> performCalculation()
            is CalculatorActions.Delete -> performDelete()
        }
    }

    private fun performDelete() {
        when {
            state.number2.isNotBlank() -> state = state.copy(number2 = state.number2.dropLast(1))
            state.operation != null -> state = state.copy(operation = null)
            state.number1.isNotBlank() -> state = state.copy(number1 = state.number1.dropLast(1))
        }
    }

    private fun performCalculation() {
        val number1 = state.number1.toDoubleOrNull()
        val number2 = state.number2.toDoubleOrNull()

        if (number1 != null && number2 != null && state.operation != null) {
            state = state.copy(
                number1 = performOperation(number1, number2).toString().take(15),
                number2 = "",
                operation = null
            )
        }
    }

    private fun performOperation(number1: Double, number2: Double): Double {
        return when (state.operation) {
            is CalculatorOperation.Add -> number1 + number2
            is CalculatorOperation.Subtract -> number1 - number2
            is CalculatorOperation.Multiply -> number1 * number2
            is CalculatorOperation.Divide -> number1 / number2
            is CalculatorOperation.Percent -> number1 / 100
            else -> throw IllegalArgumentException("Unsupported operation")
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if (state.number1.isNotBlank()) {
            state = state.copy(operation = operation)
        }
    }

    private fun enterDecimal() {
        val targetNumber = if (state.operation == null) state.number1 else state.number2
        if (!targetNumber.contains(".") && targetNumber.isNotBlank()) {
            state = state.copy(number1 = targetNumber + ".")
        }
    }

    private fun enterNumber(number: Int) {
        val targetNumber = if (state.operation == null) state.number1 else state.number2
        if (targetNumber.length < MAX_NUM_LENGTH) {
            state = if (state.operation == null) {
                state.copy(number1 = targetNumber + number)
            } else {
                state.copy(number2 = targetNumber + number)
            }
        }
    }

    private fun clearState() {
        state = CalculatorState()
    }

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }
}
