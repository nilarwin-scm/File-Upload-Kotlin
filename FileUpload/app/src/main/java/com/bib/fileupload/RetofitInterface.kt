package com.bib.fileupload
import com.bib.fileupload.model.ReturnData
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface RetofitInterface {
    //Leave Api
//    @Multipart
//    @POST("/leaveReport?save=true")
//    abstract fun singleLeaveReport(@Header("Authorization") authToken: String, @Part jsonObject: MultipartBody.Part): Call<ReturnData>

    //Leave Api
    @Multipart
    @POST("leaveReport?save=true")
//    abstract fun singleLeaveReport(@Header("Authorization") authToken: String,
//                                   @Part data : MultipartBody.Part,
//                                   @Part attachFile : MultipartBody.Part): Call<ReturnData>

    abstract fun singleLeaveReport(@Header("Authorization") authToken: String, @Part("leaveReportDetail") reportDetail : JsonObject ,@Part attachFile : MultipartBody.Part): Call<ReturnData>


}