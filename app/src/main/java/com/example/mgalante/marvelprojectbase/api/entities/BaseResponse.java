package com.example.mgalante.marvelprojectbase.api.entities;

public class BaseResponse<T> {
    public int code;
    public String status;
    public String etag;
    public DataResponse<T> data;
}
