package com.hdev.prometeoai

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : UiState

    /**
     * Still loading
     */
    object Loading : UiState

    /**
     * Text has been generated
     */
    object Success : UiState

    /**
     * There was an error generating text
     */
    object Error : UiState
}