package com.blossy.flowerstore.data.remote.utils
import com.blossy.flowerstore.domain.utils.Result
import retrofit2.Response

inline fun <T, R> Response<BaseResponse<T>>.toResult(
    mapper: (T) -> R
): Result<R> {
    return if (isSuccessful) {
        val body = body()
        if (body != null && body.success && body.data != null) {
            try {
                Result.Success(mapper(body.data))
            } catch (e: Exception) {
                Result.Error(e.message ?: "Data mapping failed")
            }
        } else {
            Result.Error(body?.message ?: "API returned failure")
        }
    } else {
        Result.Error(message() ?: "API call failed")
    }
}

inline fun  <T, R> Response<T>.toWrappedResult(mapper: (T) -> R): Result<R> {
    return if (isSuccessful) {
        val body = body()
        if (body != null) {
            try {
                Result.Success(mapper(body))
            } catch (e: Exception) {
                Result.Error(e.message ?: "Data mapping failed")
            }
        } else {
            Result.Error("Empty response")
        }
    } else {
        Result.Error(message() ?: "API call failed")
    }
}

inline fun <T> Response<BaseResponse<T>>.toResult(): Result<T> {
    return this.toResult { it }
}

inline fun <T> safeApiCall(apiCall: () -> Result<T>): Result<T> {
    return try {
        apiCall()
    } catch (e: Exception) {
        Result.Error(e.message ?: "Unexpected error occurred")
    }
}