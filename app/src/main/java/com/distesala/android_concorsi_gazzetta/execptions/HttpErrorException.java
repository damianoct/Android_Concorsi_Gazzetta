package com.distesala.android_concorsi_gazzetta.execptions;

/**
 * Created by damiano on 23/08/16.
 */
public class HttpErrorException extends Exception
{
    public HttpErrorException()
    {
        super();
    }

    public HttpErrorException(String detailMessage)
    {
        super(detailMessage);
    }
}
