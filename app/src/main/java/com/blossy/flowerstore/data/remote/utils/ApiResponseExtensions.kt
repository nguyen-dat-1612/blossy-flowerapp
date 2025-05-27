package com.blossy.flowerstore.data.remote.utils

import android.util.Log
import com.blossy.flowerstore.domain.utils.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

inline fun <T, R> BaseResponse<T>.toResult(mapper: (T) -> R): Result<R> {
    return if (success && data != null) {
        try {
            Result.Success(mapper(data))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Data mapping failed")
        }
    } else {
        Result.Error(message)
    }
}

inline fun <T> BaseResponse<T>.toResult(): Result<T> {
    return this.toResult { it }
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

inline suspend fun <T> safeApiCall(
    timeoutMillis: Long = 5000,
    crossinline apiCall: suspend () -> Result<T>
): Result<T> {
    return try {
        withTimeout(timeoutMillis) {
            apiCall()
        }
    } catch (e: TimeoutCancellationException) {
        Log.e("safeApiCall", "TimeoutCancellationException: ${e.message}")
        Result.Error("Yêu cầu quá thời gian, vui lòng thử lại")
    } catch (e: CancellationException) {
        Log.e("safeApiCall", "CancellationException: ${e.message}")
        Result.Error("Yêu cầu đã bị huỷ")
    } catch (e: IOException) {
        Log.e("safeApiCall", "IOException: ${e.message}")
        Result.Error("Lỗi mạng: ${e.message}")
    } catch (e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        Log.e("safeApiCall", "HttpException: $errorBody")
        Result.Error("Lỗi HTTP ${e.code()}: ${errorBody ?: e.message()}")
    } catch (e: Exception) {
        Log.e("safeApiCall", "Exception: ${e.message}")
        Result.Error("Lỗi không xác định: ${e.message}")
    }
}
