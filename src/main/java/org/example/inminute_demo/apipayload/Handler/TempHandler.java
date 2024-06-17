package org.example.inminute_demo.apipayload.Handler;


import org.example.inminute_demo.apipayload.code.BaseCode;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.exception.GeneralException;

public class TempHandler extends GeneralException {
    public TempHandler(BaseCode errorCode) {
        super((ErrorStatus) errorCode);
    }
}