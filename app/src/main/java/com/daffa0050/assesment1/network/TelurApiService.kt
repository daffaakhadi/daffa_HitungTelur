    package com.daffa0050.assesment1.network

    import com.daffa0050.assesment1.model.ApiResponse
    import com.daffa0050.assesment1.model.OpStatus
    import com.squareup.moshi.Moshi
    import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
    import okhttp3.MultipartBody
    import okhttp3.RequestBody
    import retrofit2.Retrofit
    import retrofit2.converter.moshi.MoshiConverterFactory
    import retrofit2.http.DELETE
    import retrofit2.http.GET
    import retrofit2.http.Header
    import retrofit2.http.Multipart
    import retrofit2.http.POST
    import retrofit2.http.PUT
    import retrofit2.http.Part
    import retrofit2.http.Path

    interface TelurApiService {

        @GET("eggs")
        suspend fun getPemesanan(
            @Header("Authorization") userId: String
        ): ApiResponse

        @Multipart
        @POST("eggs")
        suspend fun postPemesanan(
            @Header("Authorization") userId: String,
            @Part("customerName") customerName: RequestBody,
            @Part("customerAddress") customerAddress: RequestBody,
            @Part("purchaseType") purchaseType: RequestBody,
            @Part("amount") amount: RequestBody,
            @Part("total") total: RequestBody,
            @Part image: MultipartBody.Part
        ): OpStatus

        @DELETE("eggs/{id}")
        suspend fun deletePemesanan(
            @Header("Authorization") userId: String,
            @Path("id") id: String
        ): OpStatus

        @Multipart
        @PUT("eggs/{id}")
        suspend fun updatePemesanan(
            @Path("id") id: Int,
            @Header("Authorization") token: String,
            @Part("customerName") customerName: RequestBody,
            @Part("customerAddress") customerAddress: RequestBody,
            @Part("purchaseType") purchaseType: RequestBody,
            @Part("amount") amount: RequestBody,
            @Part("total") total: RequestBody,
            @Part image: MultipartBody.Part?
        ): OpStatus



        companion object {
            private const val BASE_URL = "https://egg-api.sendiko.my.id/"

            fun create(): TelurApiService {
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()

                return retrofit.create(TelurApiService::class.java)
            }
            enum class ApiStatus{ LOADING, SUCCESS, ERROR }
        }
    }
