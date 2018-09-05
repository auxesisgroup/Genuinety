package com.auxesisgroup.genuinety

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

object WebService {

    private fun getService(): ApiService {
        return setupRetrofit().create(ApiService::class.java)
    }

    private fun setupLogging(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }


    private fun provideOkHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.addNetworkInterceptor(setupLogging())
        client.addNetworkInterceptor { chain ->
            val request = chain.request()
            val newRequest = request.newBuilder()
                    .build()
            chain.proceed(newRequest)
        }
        return client.build()
    }

    private fun setupRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.GENUINETYAPI)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(provideOkHttpClient())
                .build()
    }

    private fun <T> apiRequest(epCall: Call<T>): Observable<T> {
        return Observable.create { subscriber ->
            val response = epCall.execute()
            when {
                response.isSuccessful -> {
                    subscriber.onNext(response.body()!!)
                    subscriber.onComplete()
                }
                else -> subscriber.onError(Throwable(response.toString()))
            }
        }
    }

    // API CALLS
    fun getItem(clientId: String, itemCode: String) : Observable<Item> {
        return apiRequest(getService().fetchItemInfo(clientId, itemCode))
                .subscribeOn(Schedulers.io())
    }

    fun addItem(item: Item) : Observable<Item> {
        return apiRequest(getService().postItemInfo(item))
    }

    fun getItemForUpdate(clientId: String, itemCode: String, cb: ApiCallback) {
        apiRequest(getService().fetchItemInfo(clientId, itemCode))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res ->
                            cb.onResponse(res)
                            Log.e("Response", res.toJSONLike())
                        },
                        { err ->
                            cb.onError(err)
                            Log.e("Error", err.toJSONLike())
                        }
                )
    }

    fun updateItem(item: Item, cb: ApiCallback) {
        apiRequest(getService().updateItemInfo(item.id, item))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res ->
                            cb.onResponse(res)
                            Log.e("Response", res.toJSONLike())
                        },
                        { err ->
                            cb.onError(err)
                            Log.e("Error", err.toJSONLike())
                        }
                )
    }

    fun updateItemObservable(item: Item) : Observable<Item> {
        return apiRequest(getService().updateItemInfo(item.id, item))
    }
}

interface ApiService {
    @GET("/items/{clientId}/{itemCode}")
    fun fetchItemInfo(@Path("clientId") cId: String, @Path("itemCode") iCode: String) : Call<Item>

    @PUT("/items/{id}")
    fun updateItemInfo(@Path("id") id: Int, @Body item: Item) : Call<Item>

    @POST("/items")
    fun postItemInfo(@Body item: Item) : Call<Item>
}