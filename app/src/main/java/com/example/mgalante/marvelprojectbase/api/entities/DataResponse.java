package com.example.mgalante.marvelprojectbase.api.entities;

import java.util.List;

/**
 * Estructura de datos que devuelve la API de Marvel
 * */
public class DataResponse<T> {
    public int offset;
    public int limit;
    public int total;
    public int count;
    public List<T> results;
}
