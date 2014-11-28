package com.example.hobosigns.rest;

import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;

/*
 * This interface extends the interface for callable and adds the ability to pass in a json string when a user calls call on the object.
 * This is intended to be used to pass in a method for onPostExecute.
 * */

public interface MyCallable<T> extends Callable<T> {

	public T call(HttpEntity jsonReader) throws Exception;

}
