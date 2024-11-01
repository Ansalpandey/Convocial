package com.app.convocial.common

/**
 * A sealed class that represents different states of a resource.
 *
 * @param T The type of data contained in the resource.
 * @property data The data contained in the resource, if any.
 * @property message The error message, if any.
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
  /** Represents a loading state. */
  class Loading<T> : Resource<T>()

  /**
   * Represents a success state.
   *
   * @param data The data contained in the resource.
   */
  class Success<T>(data: T?) : Resource<T>(data = data)

  /**
   * Represents an error state.
   *
   * @param message The error message.
   */
  class Error<T>(message: String?) : Resource<T>(message = message)
}
