package com.example.hobosigns.rest;

import java.io.InputStream;
import java.util.concurrent.Callable;

/*
 * This interface extends the interface for callable and adds the ability to pass in a json string when a user calls call on the object.
 * This is intended to be used to pass in a method for onPostExecute.
 * */

public interface MyCallable<T> extends Callable<T> {

	public T call(InputStream jsonReader) throws Exception;

}
